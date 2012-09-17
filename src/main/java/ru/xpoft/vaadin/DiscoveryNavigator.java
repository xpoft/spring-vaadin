package ru.xpoft.vaadin;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xpoft
 */
public class DiscoveryNavigator extends Navigator
{
    private static Logger logger = LoggerFactory.getLogger(DiscoveryNavigator.class);
    private WebApplicationContext applicationContext;
    private static final Map<String, Class> views = new ConcurrentHashMap<>();

    static
    {
        logger.debug("discovery views:");

        try
        {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(VaadinView.class));
            Set<BeanDefinition> beans = scanner.findCandidateComponents("");
            for (BeanDefinition bean : beans)
            {
                Class clazz = Class.forName(bean.getBeanClassName());
                VaadinView vaadinView = (VaadinView) clazz.getAnnotation(VaadinView.class);
                views.put(vaadinView.value(), clazz);
                logger.debug("found \"{}\" for \"{}\"", new Object[]{vaadinView.value(), bean.getBeanClassName()});
            }
        }
        catch (ClassNotFoundException e)
        {
            logger.error("Error loading: {}", e);
        }
    }

    public DiscoveryNavigator(WebApplicationContext applicationContext, Page page, ViewDisplay display)
    {
        super(page, display);
        this.applicationContext = applicationContext;

        for (Map.Entry<String, Class> view : views.entrySet())
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
        addProvider(new SpringViewProvider(applicationContext, viewName, viewClass));
    }

    @Override
    public void navigateTo(String navigationState)
    {
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
