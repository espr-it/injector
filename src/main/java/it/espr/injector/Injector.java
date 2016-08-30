package it.espr.injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Injector {

	private static final Logger log = LoggerFactory.getLogger(Injector.class);

	private static final Injector injector = new Injector();

	private BeanInspector beanInspector = new BeanInspector();

	private BeanInstantiator beanInstantiator = new BeanInstantiator();

	public static Injector get() {
		return injector;
	}

	public <Type> Type get(Class<Type> type) {
		Type instance = null;
		try {
			Bean<Type> bean = beanInspector.inspect(type);
			instance = beanInstantiator.instantiate(bean);
		} catch (Exception e) {
			log.error("Problem when getting instance of {}", type, e);
		}

		return instance;
	}
}
