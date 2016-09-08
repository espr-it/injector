package it.espr.injector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Configuration {

	protected final Utils utils = new Utils();

	final Map<Class<?>, Collection<Class<?>>> bindings = new HashMap<>();

	public <I> void bind(Class<I> i, Collection<Class<? extends I>> classes) {
		if (classes == null || classes.size() == 0) {
			return;
		}

		if (!this.bindings.containsKey(i)) {
			this.bindings.put(i, new ArrayList<Class<?>>());
		}
		this.bindings.get(i).addAll(classes);
	}

	@SuppressWarnings("unchecked")
	<I> Collection<Class<I>> getBindings(Class<? extends I> i) {
		Collection<Class<I>> result = new ArrayList<>();
		Collection<Class<?>> content = bindings.get(i);
		for (Class<?> c : content) {
			result.add((Class<I>) c);
		}
		return result;
	}

	boolean isBound(Class<?> i) {
		return bindings.containsKey(i);
	}

	final Map<String, Object> instances = new HashMap<>();

	public void add(String name, Object instance) {
		this.add(name, instance.getClass(), instance);
		if (instance instanceof Map) {
			this.add(name, Map.class, instance);
		} else if (instance instanceof List) {
			this.add(name, List.class, instance);
		} else if (instance instanceof Set) {
			this.add(name, Set.class, instance);
		}
	}

	public void add(String name, Class<?> clazz, Object instance) {
		this.instances.put(utils.key(name, clazz), instance);
	}
}
