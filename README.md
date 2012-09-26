Spring Vaadin Integration
=======================

Vaadin 7.x supports only.

Current version: Vaadin 7.0.0.beta2

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
@Scope("request")
@Theme("myTheme")
public class MyUI extends UI
{
    private static Logger logger = LoggerFactory.getLogger(TrackerUI.class);

    @Autowired
    private WebApplicationContext applicationContext;

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
@Scope("request")
@Theme("myTheme")
public class MyUI extends UI
{
    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private MyClass myClass;

    @Override
    protected void init(final WrappedRequest request)
    {
        Navigator.SimpleViewDisplay display = new Navigator.SimpleViewDisplay();
        setContent(display);

        DiscoveryNavigator navigator = new DiscoveryNavigator(applicationContext, UI.getCurrent().getPage().getCurrent(), display);
        navigator.navigateTo(UI.getCurrent().getPage().getFragment());
    }
}
~~~~

View example:
~~~~ java
@Component
@Scope("request")
@VaadinView(MainView.NAME)
public class MainView extends Panel implements View
{
    public static final String NAME = "profile";

    @Autowired
    private WebApplicationContext applicationContext;

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
            <id>xpoft-spring-vaadin</id>
            <url>https://github.com/xpoft/spring-vaadin/raw/master/repository</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>ru.xpoft.vaadin</groupId>
            <artifactId>spring-vaadin-integration</artifactId>
            <version>1.0.1</version>
        </dependency>
    </dependencies>
~~~~

# Changelog

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