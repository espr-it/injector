package it.espr.injector;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import it.espr.injector.bean.BeanWithConstructorWithMultipleLevelDependencies;
import it.espr.injector.bean.BeanWithConstructorWithSingleLevelDependencies;
import it.espr.injector.bean.EmptyBean;
import it.espr.injector.bean.EmptyBeanWithConstructor;

public class BeanInstantiatorTest {

	private BeanInstantiator beanInstantiator = new BeanInstantiator();

	private BeanInspector beanInspector = new BeanInspector();

	@Test
	public void instantiateEmptyBean() throws BeanException {
		EmptyBean bean = beanInstantiator.instantiate(beanInspector.inspect(EmptyBean.class));
		assertThat(bean).isNotNull();
	}

	@Test
	public void instantiateEmptyBeanWithConstructor() throws BeanException {
		EmptyBeanWithConstructor bean = beanInstantiator.instantiate(beanInspector.inspect(EmptyBeanWithConstructor.class));
		assertThat(bean).isNotNull();
	}

	@Test
	public void instantiateBeanWithConstructorWithSingleLevelDependencies() throws BeanException {
		BeanWithConstructorWithSingleLevelDependencies bean = beanInstantiator.instantiate(beanInspector.inspect(BeanWithConstructorWithSingleLevelDependencies.class));
		assertThat(bean).isNotNull();
	}

	@Test
	public void BeanWithConstructorWithMultipleLevelDependencies() throws BeanException {
		BeanWithConstructorWithMultipleLevelDependencies bean = beanInstantiator.instantiate(beanInspector.inspect(BeanWithConstructorWithMultipleLevelDependencies.class));
		assertThat(bean).isNotNull();
	}

}
