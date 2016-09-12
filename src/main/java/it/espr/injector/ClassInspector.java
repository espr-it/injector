package it.espr.injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.injector.Configuration.Bindings;
import it.espr.injector.exception.BeanException;
import it.espr.injector.exception.CircularDependencyExpection;

public class ClassInspector {

	private static final Logger log = LoggerFactory.getLogger(ClassInspector.class);

	private Bindings bindings;

	ClassInspector(Bindings bindings) {
		this.bindings = bindings;
	}

	@SuppressWarnings("unchecked")
	private class MyTypeSafeMap {
		private Map<String, Bean<?>> map = new HashMap<>();

		public <Type> void put(Class<Type> type, Bean<Type> value) {
			map.put(Utils.key(value.name, type), value);
		}

		public <Type> Bean<Type> get(Class<Type> type, String name) {
			return (Bean<Type>) map.get(Utils.key(name, type));
		}
	}

	private MyTypeSafeMap cache = new MyTypeSafeMap();

	public <Type> Bean<Type> inspect(Class<Type> type) throws BeanException {
		return this.inspect(type, new HashSet<Class<?>>());
	}

	private <Type> Bean<Type> inspect(Class<Type> type, Set<Class<?>> stack) throws BeanException {
		return this.inspect(type, null, stack);
	}

	public <Type> Bean<Type> inspect(Class<Type> type, String named) throws BeanException {
		return this.inspect(type, named, new HashSet<Class<?>>());
	}

	@SuppressWarnings("unchecked")
	private <Type> Bean<Type> inspect(Class<Type> type, String named, Set<Class<?>> stack) throws BeanException {
		if (!stack.add(type)) {
			log.error("Circular dependency for {} - current dependency stack is: {}", type, stack);
			throw new CircularDependencyExpection("Circular dependency detected: '" + type);
		}
		Bean<Type> bean = this.cache.get(type, named);

		if (bean == null) {
			if (this.bindings.has(named, type)) {
				bean = (Bean<Type>) this.inspectBindings(type, named);
			} else {
				String name = this.inspectName(type);

				if (!Utils.isEmpty(named) && (Utils.isEmpty(name) || !named.equals(name))) {
					stack.remove(type);
					return null;
				}

				Constructor<Type> constructor = inspectConstructors(type);
				String key = Utils.key(name, type);
				boolean singleton = this.inspectSingleton(type);
				List<Bean<?>> constructorParameters = inspectConstructorParameters(constructor, stack);
				Map<Field, Bean<?>> fields = this.inspectFields(type);

				bean = new Bean<Type>(name, key, singleton, type, constructor, constructorParameters, fields);
			}
			this.cache.put(type, bean);
		}
		stack.remove(type);
		return bean;
	}

	@SuppressWarnings("unchecked")
	public <Type> Bean<? extends Type> inspectBindings(Class<Type> type, String named) throws BeanException {
		Object binding = this.bindings.get(named, type);
		if (binding instanceof Class) {
			return this.inspect((Class<? extends Type>) binding, named);
		} else if (binding instanceof List) {
			List<Object> bindingList = (List<Object>) binding;
			for (int i = 0; i < bindingList.size(); i++) {
				if (bindingList.get(i) instanceof Class<?>) {
					bindingList.set(i, this.inspect((Class<Type>) bindingList.get(i)));
				}
			}
		} else if (binding instanceof Map) {
			Map<Object, Object> bindingMap = (Map<Object, Object>) binding;
			for (Entry<Object, Object> entry : bindingMap.entrySet()) {
				if (entry.getValue() instanceof Class<?>) {
					entry.setValue(this.inspect((Class<Type>) entry.getValue()));
				}
			}
		}
		return new Bean<Type>((Type) binding, named, Utils.key(named, binding.getClass()));
	}

	private boolean inspectSingleton(Class<?> type) {
		return type.isAnnotationPresent(Singleton.class);
	}

	private String inspectName(Class<?> type) {
		String name = null;
		Named named = type.getAnnotation(Named.class);
		if (named != null && named.value() != null && !named.value().trim().equals("")) {
			name = named.value();
		}
		return name;
	}

	private Map<Field, Bean<?>> inspectFields(Class<?> type) throws BeanException {
		Map<Field, Bean<?>> fields = new LinkedHashMap<>();

		Class<?> c = type;
		while (!c.equals(Object.class)) {
			Field[] declaredFields = c.getDeclaredFields();
			for (Field field : declaredFields) {
				if (field.isAnnotationPresent(Inject.class)) {
					fields.put(field, null);
				}
			}
			c = c.getSuperclass();
		}

		for (Entry<Field, Bean<?>> entry : fields.entrySet()) {
			Class<?> t = entry.getKey().getType();
			Named named = entry.getKey().getAnnotation(Named.class);
			String n = null;
			if (named != null && !Utils.isEmpty(named.value())) {
				n = named.value().trim();
			}

			Bean<?> bean = this.inspect(t, n);
			if (bean == null) {
				throw new BeanException("Can't find a bean for type '" + t + "' @Named as '" + n + "'");
			}
			entry.setValue(bean);
		}
		return fields.isEmpty() ? null : fields;
	}

	private List<Bean<?>> inspectConstructorParameters(Constructor<?> constructor, Set<Class<?>> stack) throws BeanException {
		List<Bean<?>> constructorParametersBeans = null;
		Class<?>[] constructorParameters = constructor.getParameterTypes();
		if (constructorParameters.length != 0) {
			constructorParametersBeans = new ArrayList<>();
			for (int index = 0; index < constructorParameters.length; index++) {
				try {
					String named = Utils.getAnnotationValue(Named.class, constructor.getParameterAnnotations()[index]);
					constructorParametersBeans.add(this.inspect(constructorParameters[index], named, stack));
				} catch (BeanException e) {
					log.error("Problem when inspecting '{}.' constructor parameter of type '{}'", index + 1, constructorParameters[index]);
					throw e;
				}
			}
		}
		return constructorParametersBeans;
	}

	@SuppressWarnings("unchecked")
	public <Type> Constructor<Type> inspectConstructors(Class<Type> type) throws BeanException {
		List<Constructor<?>> constructors = Arrays.asList(type.getDeclaredConstructors());
		List<Constructor<?>> candidates = new ArrayList<>();

		for (Constructor<?> constructor : constructors) {
			if (Utils.isPublic(constructor)) {
				candidates.add(constructor);
			}
		}

		if (candidates.size() == 0) {
			throw new BeanException("Couldn't find any valid constructor for '" + type + "'");
		}

		Constructor<Type> found = null;
		if (constructors.size() > 1) {
			for (Constructor<?> constructor : constructors) {
				if (constructor.getParameterTypes().length == 0) {
					found = (Constructor<Type>) constructor;
					break;
				}
			}
		} else {
			found = (Constructor<Type>) constructors.get(0);
		}

		if (found == null) {
			throw new BeanException("Found '" + constructors.size() + "' valid parametric constructors only, not sure which one to use (help me with @Inject)");
		}

		return found;
	}
}
