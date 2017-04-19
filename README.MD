# Simple Java Injector
[![Build Status](https://travis-ci.org/espr-it/injector.svg?branch=master)](https://travis-ci.org/espr-it/injector) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/it.espr/esprit-injector/badge.svg)](https://maven-badges.herokuapp.com/maven-central/it.espr/esprit-injector) [![codecov.io](http://codecov.io/github/espr-it/injector/coverage.svg?branch=master)](http://codecov.io/github/espr-it/injector?branch=master)

Main goal of this project is to provide simple and easy to use injection capatibilities for java projects with as little as possible configuration needed. Aiming for speed (especially on Google App Engine) made me to do some upfront decisions on design:

- no classpath scanning
- no Java 7+ features

This means that some features which are available in Spring or elsewhere by default take a tiny bit of configuration here (ie. can't use named parameters with constructors, implementing classes must be bound to interfaces etc.).

## Super simple projects

If you have a real simple project containing only concrete classes (no interfaces, abstracts etc.) and your dependencies are passed in single constructor, you don't need to have any configuration at all. Simply do:

```
Injector injector = Injector.injector();
MyClass myClass = injector.get(MyClass.class)
```

And that's it - the injector will find all needed classes and their dependencies and bind them automatically.

## Simple projects

Some projects are still considered simple but you would like to inject dependencies through setters or maybe use something as a singleton. You still don't need to use configuration for such cases, all following is possible through standard javax annotations.

```
@Singleton
public class MyClass {

	@Inject
    private DependencyA dependencyA;
    
    // you can mix-up setter and constructor injections
    private DependencyB dependencyB;
    
    public MyClass(DependencyB dependencyB) {
    	this.dependencyB = dependencyB;
    }
}
```


## When you need a configuration

If you have a project with interfaces, lists/maps of dependencies, constructors which accept named implementation etc you would need to do a bit of configuration to let injector know how to bind classes in runtime.

##### Create and use configuration with injector

```
Configuration configuration = new Configuration() {
	@Override
	protected void configure() {
	}
};
Injector injector = Injector.injector(configuration);
```

##### Binding implementations to interface

```
// even when you have only one implementation of the interface, you need to bind it
configuration.bind(MyInterface.class).to(BeanA.class);

// you can bind all implementations to single interface and then use @Named annotation on beans to pick correct one
configuration.bind(SimpleInterface.class).to(SimpleBeanA.class, SimpleBeanB.class, SimpleBeanC.class);

// or you can bind them step by step with named property directly in the config (ie. when you can't add @Named annotation to implementing beans)
configuration.bind(SimpleInterface.class).to(SimpleBeanA.class).named("beanA");
configuration.bind(SimpleInterface.class).to(SimpleBeanB.class).named("beanB");
configuration.bind(SimpleInterface.class).to(SimpleBeanC.class).named("beanC");
```

##### Binding collections

You can bind lists and maps containing both classes and instances and get list/maps on runtime with initialised beans.

```
final List<?> beans = new ArrayList<>(Arrays.asList(new Class[] { EmptyBean.class, new SingletonBean(), EmptyBean.class }));
Configuration configuration = new Configuration() {
	@Override
	protected void configure() {
		this.bind(beans).named("myList");
	}
};
// myInstances will contain initialised and injected beans for classes and the same instance of SingletonBean you have configured
List<?> myInstances = Injector.injector(configuration).get("myList");
```

Similar for maps:
```
final Map<String,Object> map = new HashMap<>();
map.put("a", EmptyBean.class);
map.put("b", new SingletonBean());
map.put("c", EmptyBean.class);
		
Configuration configuration = new Configuration() {
	@Override
	protected void configure() {
	this.bind(map).named("myMap");
};
// myInstances will contain initialised and injected beans for classes and the same instance of SingletonBean you have configured
Map<String,Object> myInstances = Injector.injector(configuration).get("myMap");
```

## Project status

The project is still being developed as I feel a need for new features - here is the list of things I would like to add in near future. You are welcome to submit any pull requests, either if you find a bug (submit failing test please) or adding a new feature (bear in mind that I would like to keep code compact and fast).

- add some sort of factory/provide functionality (providing specific isnatnces/class to bean on inject time when we can't annotate target with @Named)

## Download and use

There are regular releases on maven now so grab the latest binary and use it if you wish https://mvnrepository.com/artifact/it.espr/esprit-injector.
