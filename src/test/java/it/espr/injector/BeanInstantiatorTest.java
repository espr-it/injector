package it.espr.injector;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

import it.espr.injector.bean.BeanWithConstructorWithMultipleLevelDependencies;
import it.espr.injector.bean.BeanWithConstructorWithSingleLevelDependencies;
import it.espr.injector.bean.ComplexBean;
import it.espr.injector.bean.EmptyBean;
import it.espr.injector.bean.EmptyBeanWithConstructor;
import it.espr.injector.bean.SingletonBean;
import it.espr.injector.bean.named.InterfaceForNamedBeans;
import it.espr.injector.bean.named.NamedEmptyBeanA;
import it.espr.injector.bean.named.NamedEmptyBeanB;
import it.espr.injector.bean.named.NamedSingleton;

public class BeanInstantiatorTest {

	private BeanInstantiator beanInstantiator = new BeanInstantiator();

	private Binder binder = new Binder();

	private BeanInspector beanInspector = new BeanInspector(binder);

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

	@Test
	public void whenInstantiatingSignletonBeanAlwaysReturnSameInstance() throws BeanException {
		Bean<SingletonBean> singletonBean = beanInspector.inspect(SingletonBean.class);

		SingletonBean singleton1 = beanInstantiator.instantiate(singletonBean);
		SingletonBean singleton2 = beanInstantiator.instantiate(singletonBean);
		assertThat(singleton1).isSameAs(singleton2);
	}

	@Test
	public void instantiateBeanWithMultipleDependenciesAndAnnotations() throws BeanException {
		Bean<SingletonBean> singletonBean = beanInspector.inspect(SingletonBean.class);

		SingletonBean singleton1 = beanInstantiator.instantiate(singletonBean);
		SingletonBean singleton2 = beanInstantiator.instantiate(singletonBean);
		assertThat(singleton1).isSameAs(singleton2);
	}

	@Test
	public void instantiateComplexBean() throws BeanException {
		binder.bind(InterfaceForNamedBeans.class, Arrays.asList(NamedEmptyBeanA.class, NamedEmptyBeanB.class, NamedSingleton.class));
		Bean<ComplexBean> complexBean = beanInspector.inspect(ComplexBean.class);

		ComplexBean complexBean1 = beanInstantiator.instantiate(complexBean);
		ComplexBean complexBean2 = beanInstantiator.instantiate(complexBean);
		assertThat(complexBean1).isNotSameAs(complexBean2);
		
		assertDifferent(complexBean1.getBeanA(), complexBean2.getBeanA());
		assertDifferent(complexBean1.getBeanB(), complexBean2.getBeanB());
		assertDifferent(complexBean1.getBeanWithConstructorWithSingleLevelDependencies(), complexBean2.getBeanWithConstructorWithSingleLevelDependencies());
		assertDifferent(complexBean1.getEmptyBeanWithConstructor(), complexBean2.getEmptyBeanWithConstructor());
		
		assertSame(complexBean1.getNamedSingleton(), complexBean2.getNamedSingleton());
		assertSame(complexBean1.getSingletonBean(), complexBean2.getSingletonBean());
	}

	private void assertNotNull(Object o1, Object o2) {
		assertThat(o1).isNotNull();
		assertThat(o2).isNotNull();
	}

	private void assertSame(Object o1, Object o2) {
		assertNotNull(o1, o2);
		assertThat(o1).isSameAs(o2);
	}
	
	private void assertDifferent(Object o1, Object o2) {
		assertNotNull(o1, o2);
		assertThat(o1).isNotSameAs(o2);
	}

}
