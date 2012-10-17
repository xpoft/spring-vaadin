package ru.xpoft.vaadin;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xpoft
 */
@Configurable(preConstruction = true)
public class DiscoveryNavigator extends Navigator
{
    private static Logger logger = LoggerFactory.getLogger(DiscoveryNavigator.class);

    @Autowired
    private transient ApplicationContext applicationContext;

    public DiscoveryNavigator(UI ui, ViewDisplay display)
    {
        super(ui, display);

        logger.debug("discovery views");

        Map<String, Object> map = applicationContext.getBeansWithAnnotation(VaadinView.class);
        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            VaadinView vaadinView = entry.getValue().getClass().getAnnotation(VaadinView.class);
            Class clazz = entry.getValue().getClass();
            if (!View.class.isAssignableFrom(clazz))
            {
                logger.warn("Class {} with view name \"{}\" isn't instance of View", new Object[]{clazz, vaadinView.value()});
                continue;
            }

            addBeanView(vaadinView.value(), clazz);
            logger.debug("key: {}, value: {}", new Object[]{vaadinView.value(), clazz});
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
