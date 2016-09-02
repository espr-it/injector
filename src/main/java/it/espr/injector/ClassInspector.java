package it.espr.injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

public class ClassInspector {

	private Configuration configuration;

	private Utils utils;

	ClassInspector(Configuration configuration) {
		super();
		this.configuration = configuration;
		this.utils = new Utils();
	}

	@SuppressWarnings("unchecked")
	private class MyTypeSafeMap {
		private Map<String, Bean<?>> map = new HashMap<>();

		public <Type> void put(Class<Type> type, Bean<Type> value) {
			map.put(utils.key(value.name, type), value);
		}

		public <Type> Bean<Type> get(Class<Type> type, String name) {
			return (Bean<Type>) map.get(utils.key(name, type));
		}
	}

	private MyTypeSafeMap cache = new MyTypeSafeMap();

	public <Type> Bean<Type> inspect(Class<Type> type) throws BeanException {
		return this.inspect(type, null);
	}

	public <Type> Bean<Type> inspect(Class<Type> type, String named) throws BeanException {
		Bean<Type> bean = this.cache.get(type, named);

		if (bean == null) {
			if (this.configuration.instances.containsKey(utils.key(named, type))) {
				bean = new Bean<>(named, utils.key(named, type), true, type, null, null, null);
			} else if (this.configuration.isBound(type)) {
				bean = this.inspectBindings(type, named);
			} else {
				String name = this.inspectName(type);

				if (!utils.isEmpty(named) && (utils.isEmpty(name) || !named.equals(name))) {
					return null;
				}

				Constructor<Type> constructor = inspectConstructors(type);
				String key = utils.key(name, type);
				boolean singleton = this.inspectSingleton(type);
				List<Bean<?>> constructorParameters = inspectConstructorParameters(constructor);
				Map<Field, Bean<?>> fields = this.inspectFields(type);

				bean = new Bean<Type>(name, key, singleton, type, constructor, constructorParameters, fields);
			}
			this.cache.put(type, bean);
		}
		return bean;
	}

	public <Type> Bean<Type> inspectBindings(Class<Type> type, String named) throws BeanException {
		Collection<Class<Type>> candidates = this.configuration.getBindings(type);
		List<Bean<Type>> candidateBeans = new ArrayList<>();
		for (Class<Type> candidate : candidates) {
			Bean<Type> candidateBean = this.inspect(candidate, named);
			if (candidateBean != null) {
				candidateBeans.add(candidateBean);
			}
		}
		if (candidateBeans.size() != 1) {
			throw new BeanException("Found '" + candidateBeans.size() + "' candidates for bean for '" + type + "': Either you forgot to bind the bean or add @Named to it");
		}
		return candidateBeans.get(0);
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
			if (named != null && !utils.isEmpty(named.value())) {
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

	private List<Bean<?>> inspectConstructorParameters(Constructor<?> constructor) throws BeanException {
		List<Bean<?>> constructorParametersBeans = null;
		Class<?>[] constructorParameters = constructor.getParameterTypes();
		if (constructorParameters.length != 0) {
			constructorParametersBeans = new ArrayList<>();
			for (int index = 0; index < constructorParameters.length; index++) {
				try {
					String named = utils.getAnnotationValue(Named.class, constructor.getParameterAnnotations()[index]);
					constructorParametersBeans.add(this.inspect(constructorParameters[index], named));
				} catch (BeanException e) {
					throw new BeanException("Problem when inspecting '" + index + "' constructor parameter of type '" + constructorParameters[index] + "'");
				}
			}
		}
		return constructorParametersBeans;
	}

	@SuppressWarnings("unchecked")
	public <Type> Constructor<Type> inspectConstructors(Class<Type> type) throws BeanException {
		List<Constructor<?>> constructors = Arrays.asList(type.getDeclaredConstructors());
		Iterator<Constructor<?>> iterator = constructors.iterator();
		while (iterator.hasNext()) {
			Constructor<?> constructor = iterator.next();
			List<Integer> modifiers = Arrays.asList(constructor.getModifiers());
			for (Integer modifier : modifiers) {
				if (Modifier.isPublic(modifier)) {
					break;
				}
				iterator.remove();
			}
		}
		if (constructors.size() != 1) {
			throw new BeanException("Found '" + constructors.size() + "' valid constructors - can resolve as a bean");
		}
		return (Constructor<Type>) constructors.get(0);
	}
}
