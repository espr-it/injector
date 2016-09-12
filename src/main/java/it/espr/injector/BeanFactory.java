package it.espr.injector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanFactory {

	private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

	private Map<String, Object> cache = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <Type> Type create(Bean<Type> bean) {
		if (bean.instance != null) {
			this.handleConfiguratedCollections(bean.instance);
			return bean.instance;
		}

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
						constructorParameterInstances[i] = this.create(bean.constructorParameters.get(i));
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
						f.set(instance, this.create(b));

						if (resetAccessible) {
							f.setAccessible(false);
						}
					}
				}

				if (bean.singleton) {
					this.cache.put(bean.key, instance);
				}
			} catch (Exception e) {
				log.error("Problem when creating bean {}", bean, e);
			}
		}

		return instance;
	}

	private <Type> void handleConfiguratedCollections(Type configuredBean) {
		if (configuredBean instanceof List) {
			this.handleConfiguredList(configuredBean);
		} else if (configuredBean instanceof Map) {
			this.handleConfiguredMap(configuredBean);
		}
	}

	@SuppressWarnings("unchecked")
	private <Type> void handleConfiguredList(Type configuredList) {
		@SuppressWarnings("rawtypes")
		List configuredBeanList = (List) configuredList;
		for (int i = 0; i < configuredBeanList.size(); i++) {
			if (configuredBeanList.get(i) instanceof Bean<?>) {
				configuredBeanList.set(i, this.create((Bean<?>) configuredBeanList.get(i)));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <Type> void handleConfiguredMap(Type configuredMap) {
		@SuppressWarnings("rawtypes")
		Map<Object, Object> configuredBeanMap = (Map) configuredMap;
		for (Entry<Object, Object> entry : configuredBeanMap.entrySet()) {
			if (entry.getValue() instanceof Bean<?>) {
				entry.setValue(this.create((Bean<?>) entry.getValue()));
			}
		}
	}

}
