package it.espr.injector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.espr.injector.exception.BeanCreationExpection;

public class BeanFactory {

	private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

	private Map<String, Object> cache = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <Type> Type create(Bean<Type> bean) throws BeanCreationExpection {
		if (bean.instance != null) {
			this.handleConfiguratedCollections(bean.instance);
			return bean.instance;
		}

		Type instance = null;
		if (bean.singleton) {
			log.debug("Bean {} is singleton, getting from cache", bean);
			instance = (Type) this.cache.get(bean.key);
			log.debug("Singleton bean {} {} found in cache", bean, instance == null ? "wasn't" : "was");
		}

		if (instance == null) {
			try {
				log.debug("Creating a new instance of {}", bean);
				if (bean.constructorParameters == null || bean.constructorParameters.size() == 0) {
					instance = bean.constructor.newInstance();
				} else {
					Object[] constructorParameterInstances = new Object[bean.constructorParameters.size()];
					for (int i = 0; i < bean.constructorParameters.size(); i++) {
						constructorParameterInstances[i] = this.create(bean.constructorParameters.get(i));
					}
					instance = bean.constructor.newInstance(constructorParameterInstances);
				}
				log.debug("New instance of created {}", bean);

				// instantiate fields
				if (bean.fields != null) {
					log.debug("Setting fields {} for bean {}", bean.fields, bean);
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
					log.debug("Fields set {} for bean {}", bean.fields, bean);
				}

				if (bean.singleton) {
					this.cache.put(bean.key, instance);
				}
			} catch (Exception e) {
				log.error("Problem when creating bean {}", bean, e);
				throw new BeanCreationExpection("Problem when creating bean '" + bean + "'", e);
			}
		}

		return instance;
	}

	private <Type> void handleConfiguratedCollections(Type configuredBean) throws BeanCreationExpection {
		if (configuredBean instanceof List) {
			this.handleConfiguredList(configuredBean);
		} else if (configuredBean instanceof Map) {
			this.handleConfiguredMap(configuredBean);
		}
	}

	@SuppressWarnings("unchecked")
	private <Type> void handleConfiguredList(Type configuredList) throws BeanCreationExpection {
		@SuppressWarnings("rawtypes")
		List configuredBeanList = (List) configuredList;
		for (int i = 0; i < configuredBeanList.size(); i++) {
			if (configuredBeanList.get(i) instanceof Bean<?>) {
				configuredBeanList.set(i, this.create((Bean<?>) configuredBeanList.get(i)));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <Type> void handleConfiguredMap(Type configuredMap) throws BeanCreationExpection {
		@SuppressWarnings("rawtypes")
		Map<Object, Object> configuredBeanMap = (Map) configuredMap;
		for (Entry<Object, Object> entry : configuredBeanMap.entrySet()) {
			if (entry.getValue() instanceof Bean<?>) {
				entry.setValue(this.create((Bean<?>) entry.getValue()));
			}
		}
	}

}
