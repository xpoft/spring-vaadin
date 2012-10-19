Spring Vaadin Integration
=======================

Vaadin 7.x supports only.

Current version: Vaadin 7.0.0.beta4

http://vaadin.com/addon/springvaadinintegration

# Servlet

beanName - Spring bean name of root UI.

web.xml
~~~~~ xml
    <servlet>
        <servlet-name>Test Application</servlet-name>
        <servlet-class>ru.xpoft.vaadin.SpringVaadinServlet</servlet-class>
        <init-param>
            <param-name>beanName</param-name>
            <param-value>myUI</param-value>
        </init-param>
    </servlet>

    <!-- Bind as ordinary VaadingServlet -->
    <servlet-mapping>
        <servlet-name>Test Application</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>Test Application</servlet-name>
        <url-pattern>/VAADIN/*</url-pattern>
    </servlet-mapping>
~~~~~

UI class example

~~~~ java
@Component
@Scope("prototype")
@Theme("myTheme")
public class MyUI extends UI
{
    private static Logger logger = LoggerFactory.getLogger(TrackerUI.class);

    @Autowired
    private transient ApplicationContext applicationContext;

    @Autowired
    private MyClass myClass;

    ....
}
~~~~

# Autowiring views
New DiscoveryNavigator.

Using example:
~~~~ java
@Component
@Scope("prototype")
@Theme("myTheme")
public class MyUI extends UI
{
    @Autowired
    private MyClass myClass;

    @Override
    protected void init(final VaadinRequest request)
    {
        Navigator.SimpleViewDisplay display = new Navigator.SimpleViewDisplay();
        setContent(display);

        DiscoveryNavigator navigator = new DiscoveryNavigator(UI.getCurrent(), display);
        navigator.navigateTo(UI.getCurrent().getPage().getFragment());
    }
}
~~~~

View example:
~~~~ java
@Component
@Scope("prototype")
@VaadinView(MainView.NAME)
public class MainView extends Panel implements View
{
    public static final String NAME = "profile";

    @Autowired
    private transient ApplicationContext applicationContext;

    @Autowired
    private SimpleForm form;

    @PostConstruct
    public void PostConstruct()
    {
        MainLayout mainLayout = new MainLayout();
        mainLayout.setContent(form);

        setContent(mainLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event)
    {
    }
}
~~~~

# Maven

pom.xml
~~~~ xml
...
    <repositories>
        <repository>
            <id>vaadin-addons</id>
            <url>http://maven.vaadin.com/vaadin-addons</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>ru.xpoft.vaadin</groupId>
            <artifactId>spring-vaadin-integration</artifactId>
            <version>1.4.7</version>
        </dependency>
    </dependencies>
~~~~

# Serialization

You should use "transient" attribute for ApplicationContext and other's context's beans.
~~~~ java
    @Autowired
    private transient ApplicationContext applicationContext;
~~~~

# SystemMessages customization
You can use Spring MessageSource as source for Vaadin SystemMessages.
And wow! Now you can use localized messages!

You should reload page after changing language. Texts for CommunicationError and AuthenticationError implements on page loading. It's Vaadin.

## Use default SpringSystemMessagesProvider

web.xml
~~~~ xml
    <servlet>
        <servlet-name>Vaadin Application</servlet-name>
        <servlet-class>ru.xpoft.vaadin.SpringVaadinServlet</servlet-class>
        ...
        <init-param>
            <param-name>systemMessagesBeanName</param-name>
            <param-value>DEFAULT</param-value>
        </init-param>
    </servlet>
~~~~

in your Spring messages:

~~~~ txt
vaadin.sessionExpired.Caption = ...
vaadin.sessionExpired.Message = ...
vaadin.sessionExpired.URL = ...
vaadin.sessionExpired.NotificationEnabled = ... (true / false)
~~~~

Other message types: communicationError, authenticationError, internalError, outOfSync, cookiesDisabled

~~~~ txt
vaadin.communicationError.Caption = ...
~~~~

If some translations don't found, it will use default Vaadin message. So you can translate "caption" only, for example.

## Use your bean (more difficult way)
web.xml
~~~~ xml
    <servlet>
        <servlet-name>Vaadin Application</servlet-name>
        <servlet-class>ru.xpoft.vaadin.SpringVaadinServlet</servlet-class>
        ...
        <init-param>
            <param-name>systemMessagesBeanName</param-name>
            <param-value>customSystemMessages</param-value>
        </init-param>
    </servlet>
~~~~

CustomSystemMessages class must implements SpringSystemMessagesProvider interface.

~~~~

# Sample

https://github.com/xpoft/spring-vaadin/tree/sample
~~~~
git clone git://github.com/xpoft/spring-vaadin.git -b sample spring-vaadin
cd spring-vaadin
mvn jetty:run
~~~~

Then go to http://locahost:9090

# Other libraries
## https://vaadin.com/ru/directory#addon/spring-integration
Simple library.
Vaadin 7.0+ not supported. Last update: Apr 26, 2011

## https://vaadin.com/ru/directory#addon/contextual-object-lookup
Simple library.
Vaadin 7.0+ not supported. Last update: Sep 5, 2011

## https://vaadin.com/ru/directory#addon/mvp-and-uibinder-spring-integration
Vaadin 7.0+ not supported. Last update: Sep 14, 2010

## https://vaadin.com/ru/directory#addon/spring-stuff
Very good library. It was the best (and only one) library for integration with Spring.
Vaadin 7.0+ supported. Last update: Aug 28, 2012

# Changelog

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