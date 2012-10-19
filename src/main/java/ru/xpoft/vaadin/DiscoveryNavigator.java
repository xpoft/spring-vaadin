package ru.xpoft.vaadin;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xpoft
 */
@Configurable(preConstruction = true)
public class DiscoveryNavigator extends Navigator
{
    private static Logger logger = LoggerFactory.getLogger(DiscoveryNavigator.class);
    private static final Map<String, Class<? extends View>> views = Collections.synchronizedMap(new HashMap<String, Class<? extends View>>());

    @Autowired
    private transient ApplicationContext applicationContext;

    public DiscoveryNavigator(UI ui, ComponentContainer display)
    {
        super(ui, display);

        if (views.isEmpty())
        {
            logger.debug("discovery views from spring context");

            long start = Calendar.getInstance().getTimeInMillis();
            String[] beansName = applicationContext.getBeanDefinitionNames();
            for (String beanName : beansName)
            {
                Class beanClass = applicationContext.getType(beanName);
                if (beanClass.isAnnotationPresent(VaadinView.class) && View.class.isAssignableFrom(beanClass))
                {
                    VaadinView vaadinView = (VaadinView) beanClass.getAnnotation(VaadinView.class);
                    String viewName = vaadinView.value();

                    views.put(viewName, beanClass);
                    logger.debug("view name: \"{}\", class: {}", new Object[]{viewName, beanClass});
                }
            }

            long end = Calendar.getInstance().getTimeInMillis();
            logger.debug("time: {}ms", (end - start));
        }
        else
        {
            logger.debug("discovery views from cache");
        }

        for (Map.Entry<String, Class<? extends View>> view : views.entrySet())
        {
            addBeanView(view.getKey(), view.getValue());
        }
    }

    public void addBeanView(String viewName, Class<? extends View> viewClass)
    {
        // Check parameters
        if (viewName == null || viewClass == null)
        {
            throw new IllegalArgumentException("view and viewClass must be non-null");
        }

        removeView(viewName);
        addProvider(new SpringViewProvider(viewName, viewClass));
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
}
