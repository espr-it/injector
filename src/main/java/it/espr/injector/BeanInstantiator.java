package it.espr.injector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BeanInstantiator {

	private Map<String, Object> cache = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <Type> Type instantiate(Bean<Type> bean) {
		Type instance = null;
		if (bean.singleton) {
			instance = (Type) this.cache.get(bean.key);
		}

		if (instance == null) {
			try {
				if (bean.constructorParameters == null || bean.constructorParameters.size() == 0) {
					instance = bean.constructor.newInstance();
				} else {
					Object[] constructorParameterInstances = new Object[bean.constructorParameters.size()];
					for (int i = 0; i < bean.constructorParameters.size(); i++) {
						constructorParameterInstances[i] = this.instantiate(bean.constructorParameters.get(i));
					}
					instance = bean.constructor.newInstance(constructorParameterInstances);
				}
				if (bean.singleton) {
					this.cache.put(bean.key, instance);
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return instance;
	}
}
