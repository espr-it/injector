package it.espr.injector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

	final Map<Class<?>, Collection<Class<?>>> bindings = new HashMap<>();

	public <I> void bind(Class<I> i, Collection<Class<? extends I>> classes) {
		if (classes == null || classes.size() == 0) {
			return;
		}

		if (!this.bindings.containsKey(i)) {
			this.bindings.put(i, new ArrayList<>());
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

}