package ru.xpoft.vaadin;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * @author xpoft
 */
public class DiscoveryNavigator extends Navigator implements ViewScopedContainer
{
    class ViewCache implements Serializable
    {
        private final String name;
        private final Class<? extends View> clazz;
        private final String scope;

        ViewCache(String name, Class<? extends View> clazz, String scope)
        {
            this.name = name;
            this.clazz = clazz;
            this.scope = scope;
        }

        public String getName()
        {
            return name;
        }

        public Class<? extends View> getClazz()
        {
            return clazz;
        }

        public String getScope()
        {
            return scope;
        }
    }

    private static Logger logger = LoggerFactory.getLogger(DiscoveryNavigator.class);
    private static final List<ViewCache> views = Collections.synchronizedList(new ArrayList<ViewCache>());
    private final Map<String, View> viewScoped = Collections.synchronizedMap(new HashMap<String, View>());

    public DiscoveryNavigator(UI ui, ComponentContainer display)
    {
        super(ui, display);

        if (views.isEmpty())
        {
            logger.debug("discovery views from spring context");

            long start = Calendar.getInstance().getTimeInMillis();
            String[] beansName = SpringApplicationContext.getApplicationContext().getBeanDefinitionNames();
            for (String beanName : beansName)
            {
                Class beanClass = SpringApplicationContext.getApplicationContext().getType(beanName);
                if (beanClass.isAnnotationPresent(VaadinView.class) && View.class.isAssignableFrom(beanClass))
                {
                    VaadinView vaadinView = (VaadinView) beanClass.getAnnotation(VaadinView.class);
                    String viewName = vaadinView.value();
                    String viewScope = vaadinView.scope();

                    ViewCache viewCache = new ViewCache(viewName, beanClass, viewScope);
                    views.add(viewCache);
                    logger.debug("view name: \"{}\", class: {}, scope: {}", new Object[]{viewName, beanClass, viewScope});
                }
            }

            long end = Calendar.getInstance().getTimeInMillis();
            logger.debug("time: {}ms", (end - start));
        }
        else
        {
            logger.debug("discovery views from cache");
        }

        for (ViewCache view : views)
        {
            addBeanView(view.name, view.clazz, view.scope);
        }
    }

    public void addBeanView(String viewName, Class<? extends View> viewClass)
    {
        addBeanView(viewName, viewClass, VaadinViewScopes.PROTOTYPE);
    }

    public void addBeanView(String viewName, Class<? extends View> viewClass, String scope)
    {
        // Check parameters
        if (viewName == null || viewClass == null)
        {
            throw new IllegalArgumentException("view and viewClass must be non-null");
        }

        removeView(viewName);
        addProvider(new SpringViewProvider(viewName, viewClass, scope, this));
    }

    @Override
    public void navigateTo(String navigationState)
    {
        // We can't bind NULL
        if (navigationState == null)
        {
            navigationState = "";
        }

        // fix Vaadin
        if (navigationState.startsWith("!"))
        {
            super.navigateTo(navigationState.substring(1));
        }
        else
        {
            super.navigateTo(navigationState);
        }
    }

    @Override
    public View getView(String name, Class<? extends View> clazz, String scope)
    {
        if (scope.equals(VaadinViewScopes.PROTOTYPE))
        {
            return SpringApplicationContext.getApplicationContext().getBean(clazz);
        }
        else if (scope.equals(VaadinViewScopes.UI))
        {
            if (viewScoped.containsKey(name))
            {
                return viewScoped.get(name);
            }

            View view = SpringApplicationContext.getApplicationContext().getBean(clazz);
            viewScoped.put(name, view);

            return view;
        }

        return null;
    }
}
