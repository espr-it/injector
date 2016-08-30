package it.espr.injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BeanInspector {

	@SuppressWarnings("unchecked")
	private static class MyTypeSafeMap {
		private Map<Class<?>, Bean<?>> map = new HashMap<>();

		public <Type> void put(Class<Type> key, Bean<Type> value) {
			map.put(key, value);
		}

		public <Type> Bean<Type> get(Class<Type> key) {
			return (Bean<Type>) map.get(key);
		}
	}

	private MyTypeSafeMap cache = new MyTypeSafeMap();

	public <Type> Bean<Type> inspect(Class<Type> type) throws BeanException {
		Bean<Type> bean = this.cache.get(type);

		if (bean == null) {
			Constructor<Type> constructor = inspectConstructors(type);
			List<Bean<?>> constructorParameters = inspectConstructorParameters(constructor);
			String name = this.inspectName(type);

			String key = this.key(null, type);

			bean = new Bean<Type>(name, key, type, constructor, constructorParameters);
			this.cache.put(type, bean);
		}
		return bean;
	}

	private String inspectName(Class<?> type) {
		return null;
	}

	private List<Bean<?>> inspectConstructorParameters(Constructor<?> constructor) throws BeanException {
		List<Bean<?>> constructorParametersBeans = null;
		Class<?>[] constructorParameters = constructor.getParameterTypes();
		if (constructorParameters.length != 0) {
			constructorParametersBeans = new ArrayList<>();
			for (int index = 0; index < constructorParameters.length; index++) {
				try {
					constructorParametersBeans.add(this.inspect(constructorParameters[index]));
				} catch (BeanException e) {
					throw new BeanException("Problem when inspecting '" + index + " constructor parameter of type '" + constructorParameters[index] + "'");
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

	public String key(String name, Class<?> type) {
		return (name == null ? "" : name) + type.getCanonicalName();
	}
}
