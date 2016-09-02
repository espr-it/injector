package it.espr.injector.bean.named;

import javax.inject.Inject;
import javax.inject.Named;

public class BeanWithNamedFields {

	@Inject
	@Named("a")
	private InterfaceForNamedBeans beanA;

	@Inject
	@Named("b")
	private InterfaceForNamedBeans beanB;
}
