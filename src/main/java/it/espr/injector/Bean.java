package it.espr.injector;

import java.lang.reflect.Constructor;
import java.util.List;

public class Bean<Type> {

	public final String name;

	public final String key;

	public final Class<Type> type;

	public final Constructor<Type> constructor;

	public final List<Bean<?>> constructorParameters;

	public Bean(String name, String key, Class<Type> type, Constructor<Type> constructor, List<Bean<?>> constructorParameters) {
		super();
		this.name = name;
		this.key = key;
		this.type = type;
		this.constructor = constructor;
		this.constructorParameters = constructorParameters;
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
