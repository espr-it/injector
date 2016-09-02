package it.espr.injector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

				// instantiate fields
				if (bean.fields != null) {
					for (Entry<Field, Bean<?>> entry : bean.fields.entrySet()) {
						Field f = entry.getKey();
						Bean<?> b = entry.getValue();
						
						boolean resetAccessible = false;
						if (!f.isAccessible()) {
							f.setAccessible(true);
							resetAccessible = true;
						}
						f.set(instance, this.instantiate(b));
						
						if (resetAccessible) {
							f.setAccessible(false);
						}
					}
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
