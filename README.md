Spring Vaadin Integration
=======================

Vaadin 7.x supports only.

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

# Portlet

beanName - Spring bean name of root UI.

portlet.xml
~~~~~ xml
<portlet>
        <portlet-name>Test Portlet</portlet-name>
        <display-name>Test Portlet</display-name>

        <portlet-class>ru.xpoft.vaadin.SpringVaadinPortlet</portlet-class>
        <init-param>
            <name>beanName</name>
            <value>myUI</value>
        </init-param>

        <!--
          To enable displaying multiple Vaadin portlets on a page,
          they should all use the same widgetset. In that case, the
          widgetset can be configured on the portal level (parameter
          vaadin.widgetset) or here for each portlet.
        -->
        <!--
        <init-param>
            <name>widgetset</name>
            <value>com.vaadin.portal.gwt.PortalDefaultWidgetSet</value>
        </init-param>
        -->

        <!-- Supported portlet modes and content types. -->
        <supports>
            <mime-type>text/html</mime-type>
            <portlet-mode>view</portlet-mode>
            <!-- <portlet-mode>edit</portlet-mode> -->
            <!-- <portlet-mode>help</portlet-mode> -->
        </supports>

        <!-- Not always required but Liferay uses these. -->
        <portlet-info>
            <title>Test Portlet</title>
            <short-title>Test Portlet</short-title>
        </portlet-info>
    </portlet>
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
        DiscoveryNavigator navigator = new DiscoveryNavigator(this, this);
        navigator.navigateTo(UI.getCurrent().getPage().getUriFragment());
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

# UI scopes

## Prototype scope
It's the same as class without @PreserveOnRefresh. Create new instance for each new page.

@Scope("prototype")
public class MyUI extends UI
{
    ...
}

## Session scope
It will store UI in the session and it'll be reused.

@Scope("session")
public class MyUI extends UI
{
    ...
}

## Singleton scope (default for Spring)
I don't know why you want to use it, but you can do that. Everybody will have the same UI.

public class MyUI extends UI
{
    ...
}


# View cache
You can cache View in UI instance.
It created only once for every UI instance.

See sample project.

~~~~ java
@Component
@Scope("prototype")
@VaadinView(value = UIScopedView.NAME, cached = true)
public class UIScopedView extends Panel implements View
{
    ...
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent)
    {
        ...
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
            <version>1.7.3</version>
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

# Apache Shiro support
Use ShiroSecurityNavigator instead of DiscoveryNavigator.
ShiroSecurityNavigator uses @RequiresRoles, @RequiresPermissions, @RequiresAuthentication, @RequiresGuest, @RequiresUser of View class. If user doesn't have permission, View is not visible.

See sample project
~~~~ java
    ShiroSecurityNavigator navigator = new ShiroSecurityNavigator(this, getContent());
    navigator.navigateTo(UI.getCurrent().getPage().getFragment());
~~~~

~~~~
@Component
@Scope("prototype")
@VaadinView(RoleUserView.NAME)
@RequiresRoles("user")
public class RoleUserView extends Panel implements View
{
    public static final String NAME = "role_user";
...
}
~~~~

# Sample project

https://github.com/xpoft/vaadin-samples#spring-vaadin-integration

~~~~
git clone git://github.com/xpoft/spring-vaadin.git -b sample spring-vaadin
cd spring-vaadin
mvn jetty:run
~~~~

Then go to http://locahost:9090

# Changelog

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