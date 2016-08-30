package it.espr.injector;

import java.lang.reflect.InvocationTargetException;

public class BeanInstantiator {

	public <Type> Type instantiate(Bean<Type> bean) {
		Type type = null;
		try {
			if (bean.constructorParameters == null || bean.constructorParameters.size() == 0) {
				type = bean.constructor.newInstance();
			} else {
				Object[] constructorParameterInstances = new Object[bean.constructorParameters.size()];
				for (int i = 0; i < bean.constructorParameters.size(); i++) {
					constructorParameterInstances[i] = this.instantiate(bean.constructorParameters.get(i));
				}
				type = bean.constructor.newInstance(constructorParameterInstances);
			}
		} catch (InstantiationException x) {
			x.printStackTrace();
		} catch (InvocationTargetException x) {
			x.printStackTrace();
		} catch (IllegalAccessException x) {
			x.printStackTrace();
		}
		return type;
	}
}
