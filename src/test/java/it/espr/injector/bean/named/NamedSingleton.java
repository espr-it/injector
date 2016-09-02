package it.espr.injector.bean.named;

import javax.inject.Named;
import javax.inject.Singleton;

@Named("single")
@Singleton
public class NamedSingleton implements InterfaceForNamedBeans {

}
