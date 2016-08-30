package it.espr.injector;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import it.espr.injector.bean.BeanWithConstructorWithMultipleLevelDependencies;
import it.espr.injector.bean.BeanWithConstructorWithSingleLevelDependencies;
import it.espr.injector.bean.EmptyBean;
import it.espr.injector.bean.EmptyBeanWithConstructor;
import it.espr.injector.bean.SingletonBean;

public class BeanInspectorTest {

	private BeanInspector beanInspector = new BeanInspector();

	@Test
	public void whenInspectingTheSameBeanTheInspectorShouldReturnTheSameInstance() throws BeanException {
		Bean<EmptyBean> emptyBean1 = beanInspector.inspect(EmptyBean.class);
		Bean<EmptyBean> emptyBean2 = beanInspector.inspect(EmptyBean.class);

		assertThat(emptyBean1).isSameAs(emptyBean2);
	}

	@Test
	public void inspectEmptyBean() throws BeanException {
		Bean<EmptyBean> emptyBean = beanInspector.inspect(EmptyBean.class);

		assertThat(emptyBean).isNotNull();
		assertThat(emptyBean.type).isEqualTo(EmptyBean.class);
		assertThat(emptyBean.constructorParameters).isNull();
		assertThat(emptyBean.key).isEqualTo(EmptyBean.class.getCanonicalName());
	}

	@Test
	public void inspectEmptyBeanWithConstructor() throws BeanException {
		Bean<EmptyBeanWithConstructor> emptyBeanWithConstructor = beanInspector.inspect(EmptyBeanWithConstructor.class);

		assertThat(emptyBeanWithConstructor).isNotNull();
		assertThat(emptyBeanWithConstructor.type).isEqualTo(EmptyBeanWithConstructor.class);
		assertThat(emptyBeanWithConstructor.constructorParameters).isNull();
		assertThat(emptyBeanWithConstructor.key).isEqualTo(EmptyBeanWithConstructor.class.getCanonicalName());
	}

	@Test
	public void inspectBeanWithConstructorWithSingleLevelDependencies() throws BeanException {
		Bean<BeanWithConstructorWithSingleLevelDependencies> bean = beanInspector.inspect(BeanWithConstructorWithSingleLevelDependencies.class);

		assertThat(bean).isNotNull();
		assertThat(bean.type).isEqualTo(BeanWithConstructorWithSingleLevelDependencies.class);
		assertThat(bean.constructorParameters).hasSize(2);
		assertThat(bean.key).isEqualTo(BeanWithConstructorWithSingleLevelDependencies.class.getCanonicalName());
	}

	@Test
	public void inspectBeanWithConstructorWithMultipleLevelDependencies() throws BeanException {
		Bean<BeanWithConstructorWithMultipleLevelDependencies> bean = beanInspector.inspect(BeanWithConstructorWithMultipleLevelDependencies.class);

		assertThat(bean).isNotNull();
		assertThat(bean.type).isEqualTo(BeanWithConstructorWithMultipleLevelDependencies.class);
		assertThat(bean.constructorParameters).hasSize(3);
		assertThat(bean.key).isEqualTo(BeanWithConstructorWithMultipleLevelDependencies.class.getCanonicalName());
	}

	@Test
	public void inspectSingletonBean() throws BeanException {
		Bean<SingletonBean> bean = beanInspector.inspect(SingletonBean.class);

		assertThat(bean).isNotNull();
		assertThat(bean.type).isEqualTo(SingletonBean.class);
		assertThat(bean.constructorParameters).isNull();
		assertThat(bean.key).isEqualTo(SingletonBean.class.getCanonicalName());
		assertThat(bean.singleton).isTrue();
	}

}
