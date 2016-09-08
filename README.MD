# Simple injector for Java [![Build Status](https://travis-ci.org/espr-it/injector.svg?branch=master)](https://travis-ci.org/espr-it/injector)

Super simple and hopefuly fast injector for Java projects. It's intentionally functionaly limited and supports only couple of most important features. It's used by esprit-mvc which tries to be light weight and very friendly with Google App Engine. Doesn't run any classpath scanning and support injections of constructors and fields with @Inject, @Singleton and @Named annotations. Also support very simple configuration of beans through java Configuration class.

This is really alpha release not recommended to be used on production. But feel free to experiment and submit pull requests in case you find a bug or have an idea about any missing and usefull feture.

## Getting Started

Clone and compile with maven - ie. mvn eclipse:eclipse to import to eclipse or 'mvn install' to add to local repository. I'll add a real maven repo little bit later.
