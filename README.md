Documentation

http://vaadin.xpoft.ru/

# Vaadin 7.1.x
SpringVaadinIntegration 2.x

# Vaadin 7.0.x
SpringVaadinIntegration 1.x

# Serialization

You should use "transient" attribute for ApplicationContext and other's context's beans.
~~~~ java
    @Autowired
    private transient ApplicationContext applicationContext;
~~~~

# Changelog

## 2.0.1
- Add initial OSGI support. Thx to vladimirfx.

## 2.0.1
- Add ability to initialize the plugin in a separate method. Thx to cmjartan.

## 2.0
- Vaadin 7.1 support

Vaadin 7.1.x -> SpringVaadinIntegration 2.x
Vaadin 7.0.x -> SpringVaadinIntegration 1.x

## 1.8
- Add servlet config parameter "contextConfigLocation". Path to Spring configuration.
- Session scoped UI improvements.

## 1.7.3
- Add custom UI providers support (servlet "UIProvider" property). Thx to mpilone.

## 1.7.1
- Now you can use @Scope("session") for UI, instead of @PreserveOnRefresh. Bean with "prototype" scope it's the same as class without @PreserveOnRefresh (create new instance for each new page). Other types of scope will store UI in the session and it'll be reused.

## 1.7
- VaadinMessageSource. Use VaadinSession instead of UI
- ShiroSecurityNavigator. Add @RequiresAuthentication, @RequiresGuest, @RequiresUser support.

## 1.6.8
- NPE fix in DiscoveryNavigator. Thx to mpilone.

## 1.6.7
- Add portlet support (SpringVaadinPortlet). Thx to matthiasgasser.

## 1.6.6
- Vaadin 7.0.0

## 1.6.5
- Vaadin 7.0.0.beta10
  Vaadin changes. Now UI.getContent() return instance of Component.
     Replace:
        DiscoveryNavigator navigator = new DiscoveryNavigator(this, getContent());
     With:
        DiscoveryNavigator navigator = new DiscoveryNavigator(this, this);

## 1.6.2
- Add @RequiresPermissions support

## 1.6
- Add Apache Shiro support. Simple check for roles.

## 1.5.7
- String VaadinView.scope renamed to boolean VaadinView.cached. Be careful.

## 1.5.6
- Vaadin 7.0.0.beta6
- replace AspectJ with static access to the application context. Now you can remove '<context:spring-configured/>'
- add view scopes: prototype (default) and ui

## 1.5
- Vaadin 7.0.0.beta5
- Java 6.0 support

## 1.4.7
fix caching

## 1.4.6
- Simplify DiscoveryNavigator. It uses Spring Root Context to autowiring Vaadin Views, and AspectJ for non-managed classes.
- You should add '<context:spring-configured/>' to your spring config. See sample project.
- Now serialization & deserialization work perfect.

## 1.4
- Vaadin 7.0.0.beta4 support
- Enhanced SystemMessages bean support. Now you can use localized messages! Simple & quick. See sample project.
- Use can use VaadinMessageSource, it's more simple way to use Spring MessageSource

## 1.3.5
- Add SystemMessages bean support. Now you can use Spring Beans as source for SystemMessages.

## 1.3
- Vaadin 7.0.0.beta3

## 1.2
- Improve DiscoveryNavigator (cache, performance).
Now you should set "basePackage" in DiscoveryNavigator constuctor.
Multi-jars, enhanced scanning already supported.

## 1.1
- Fix serialization.

## 1.0.4
- DiscoveryNavigator. Migrate from WebApplicationContext to ApplicationContext

## 1.0.2
- Add Vaadin 7.0.0.beta2 support

## 1.0.1
- Custom DescoveryNavigator

Default. Add all view-beans from root package:
~~~~ java
    DiscoveryNavigator navigator = new DiscoveryNavigator(applicationContext, UI.getCurrent().getPage().getCurrent(), display);
    navigator.navigateTo(UI.getCurrent().getPage().getFragment());
~~~~

Disable add view-beans to Navigator.
You can do it manual.
~~~~ java
    DiscoveryNavigator navigator = new DiscoveryNavigator(applicationContext, UI.getCurrent().getPage().getCurrent(), display, false);
    navigator.addBeanView("view1", MyView.class);

    navigator.navigateTo(UI.getCurrent().getPage().getFragment());
~~~~
or you can manual discover beans in a package
~~~~ java
    navigator.discoveryViews("ru.xpoft.vaadin.test");
~~~~
you can exclude some packages
~~~~ java
    navigator.discoveryViews("ru.xpoft.vaadin.test", new String[] {"ru.xpoft.vaadin.test.one", "ru.xpoft.vaadin.test.two"})
~~~~

## 1.0
- Autowiring UI
- DiscoveryNavigator

[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/ca5361eb2e8883eda63e0f3fddc5407c "githalytics.com")](http://githalytics.com/xpoft/spring-vaadin)
