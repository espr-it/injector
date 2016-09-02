package it.espr.injector;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Injector {

	private static final Logger log = LoggerFactory.getLogger(Injector.class);

	private static final Injector injector = new Injector();

	private BeanInstantiator beanInstantiator = new BeanInstantiator();

	private Binder binder = new Binder();

	private BeanInspector beanInspector = new BeanInspector(binder);

	public static Injector get() {
		return injector;
	}

	@SuppressWarnings("unchecked")
	public <I> void bind(Class<I> i, Class<? extends I>... c) {
		this.binder.bind(i, Arrays.asList(c));
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
