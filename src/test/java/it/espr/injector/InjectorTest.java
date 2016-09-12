package it.espr.injector;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import it.espr.injector.bean.BeanWithConfiguredFields;
import it.espr.injector.bean.EmptyBean;
import it.espr.injector.bean.SingletonBean;

public class InjectorTest {

	private Injector injector;
	
	@Before
	public void setUp() {
		injector = Injector.get(new Configuration() {
		});
	}
	
	@Test
	public void whenGetSimpleclassWithoutAnnotationThenAlwaysReturnPrototype() {
		EmptyBean emptyBean1 = injector.get(EmptyBean.class);
		EmptyBean emptyBean2 = injector.get(EmptyBean.class);

		assertThat(emptyBean1).isNotNull();
		assertThat(emptyBean2).isNotNull();
		assertThat(emptyBean1).isNotSameAs(emptyBean2);
	}

	@Test
	public void whenGetSingletonAnnotatedClassThenAlwaysReturnSameInstance() {
		SingletonBean singleton1 = injector.get(SingletonBean.class);
		SingletonBean singleton2 = injector.get(SingletonBean.class);

		assertThat(singleton1).isNotNull();
		assertThat(singleton2).isNotNull();
		assertThat(singleton1).isSameAs(singleton2);
	}

	@Test
	public void whenConfigurationDefinedInstancesAreInjected() {
		final Map<String, String> mapA = new LinkedHashMap<>();
		final Map<String, String> mapB = new LinkedHashMap<>();
		final List<Integer> listA = new ArrayList<>();
		final List<Integer> listB = new ArrayList<>();
		final EmptyBean beanA = new EmptyBean();
		final EmptyBean beanB = new EmptyBean();

		Configuration configuration = new Configuration() {
			@Override
			protected void configure() {
				this.bind(mapA).named("map a");
				this.bind(mapB).named("map b");
				this.bind(listA).named("list a");
				this.bind(listB).named("list b");
				this.bind(beanA).named("bean a");
				this.bind(beanB).named("bean b");
			}
		};

		Injector injector = Injector.get(configuration);

		BeanWithConfiguredFields bean1 = injector.get(BeanWithConfiguredFields.class);
		BeanWithConfiguredFields bean2 = injector.get(BeanWithConfiguredFields.class);

		assertThat(bean1).isNotNull();
		assertThat(bean2).isNotNull();
		assertThat(bean1).isNotSameAs(bean2);

		assertThat(bean1.getMapA()).isSameAs(mapA);
		assertThat(bean1.getMapB()).isSameAs(mapB);
		assertThat(bean1.getListA()).isSameAs(listA);
		assertThat(bean1.getListB()).isSameAs(listB);
		assertThat(bean1.getBeanA()).isSameAs(beanA);
		assertThat(bean1.getBeanB()).isSameAs(beanB);
	}
}
