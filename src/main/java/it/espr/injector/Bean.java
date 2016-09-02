package it.espr.injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class Bean<Type> {

	public final String name;

	public final String key;

	public final boolean singleton;

	public final Class<Type> type;

	public final Constructor<Type> constructor;

	public final List<Bean<?>> constructorParameters;

	public final Map<Field, Bean<?>> fields;

	public Bean(String name, String key, boolean singleton, Class<Type> type, Constructor<Type> constructor, List<Bean<?>> constructorParameters,
			Map<Field, Bean<?>> fields) {
		super();
		this.name = name;
		this.key = key;
		this.singleton = singleton;
		this.type = type;
		this.constructor = constructor;
		this.constructorParameters = constructorParameters;
		this.fields = fields;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass())
			return false;

		Bean<?> other = (Bean<?>) obj;
		return key.equals(other.key);
	}
}
