package it.espr.injector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

public abstract class Configuration {

	public class Binding<Type> {

		private final Bindings bindings;

		public final Class<Type> type;

		public List<Class<? extends Type>> implementations;

		public final Type instance;

		@SuppressWarnings("unchecked")
		public Binding(Bindings bindings, Type instance) {
			this.bindings = bindings;
			this.type = (Class<Type>) instance.getClass();
			this.instance = instance;
			this.bindings.bind(type, this.instance, null);
		}

		public Binding(Bindings bindings, Class<Type> type) {
			this.bindings = bindings;
			this.type = type;
			this.instance = null;
		}

		@SuppressWarnings("unchecked")
		public Binding<Type> to(Class<?>... implementations) {
			this.implementations = new ArrayList<>();
			for (Class<?> implementation : implementations) {
				if (!type.isAssignableFrom(implementation)) {
					throw new RuntimeException("Can't bind '" + implementation + "' to '" + type + "' - different types.");
				}
				String name = Utils.getAnnotationValue(Named.class, implementation.getAnnotations());
				this.bindings.bind(type, (Class<? extends Type>) implementation, name);
				this.implementations.add((Class<? extends Type>) implementation);
			}
			return this;
		}

		public void named(String name) {
			if (this.instance != null) {
				this.bindings.bind(type, this.instance, name);
			} else if (this.implementations != null) {
				for (Class<? extends Type> impl : this.implementations) {
					this.bindings.bind(type, impl, name);
				}
			}
		}
	}

	protected class Bindings {

		private Map<String, Object> bindings = new HashMap<>();

		public <Type> void bind(Class<Type> type, Type instance, String name) {
			String key = Utils.key(name, type);
			this.bindings.put(key, instance);
			if (instance instanceof Map) {
				this.bindings.put(Utils.key(name, Map.class), instance);
			} else if (instance instanceof List) {
				this.bindings.put(Utils.key(name, List.class), instance);
			} else if (instance instanceof Set) {
				this.bindings.put(Utils.key(name, Set.class), instance);
			}
		}

		public <Type> void bind(Class<Type> type, Class<? extends Type> implementation, String name) {
			String key = Utils.key(name, type);
			if (this.bindings.containsKey(key)) {
				throw new RuntimeException("Trying to bind multiple instances under the same name");
			}
			this.bindings.put(key, implementation);
		}

		public Object get(Class<?> type) {
			return this.get(null, type);
		}

		public Object get(String name, Class<?> type) {
			return this.bindings.get(Utils.key(name, type));
		}
		
		public boolean has(Class<?> type) {
			return this.has(null, type);
		}

		public boolean has(String name, Class<?> type) {
			return this.bindings.containsKey(Utils.key(name, type));
		}
	}

	final Bindings bindings;

	protected Configuration() {
		this.bindings = new Bindings();
	}

	protected <Type> Binding<Type> bind(Class<Type> type) {
		return new Binding<Type>(this.bindings, type);
	}

	protected <Type> Binding<Type> bind(Type type) {
		return new Binding<Type>(this.bindings, type);

	}

	protected void configure() {
		// override for custom config
	};

	final void initialise() {
		this.configure();
	}
}
