package ru.xpoft.vaadin;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author xpoft
 */
public class DiscoveryNavigator extends Navigator
{
    private static class DiscoveryClass
    {
        private String viewName;
        private Class<? extends View> clazz;

        private DiscoveryClass(String viewName, Class<? extends View> clazz)
        {
            this.viewName = viewName;
            this.clazz = clazz;
        }

        public String getViewName()
        {
            return viewName;
        }

        public void setViewName(String viewName)
        {
            this.viewName = viewName;
        }

        public Class<? extends View> getClazz()
        {
            return clazz;
        }

        public void setClazz(Class<? extends View> clazz)
        {
            this.clazz = clazz;
        }

        @Override
        public String toString()
        {
            return "DiscoveryClass{" +
                    "viewName='" + viewName + '\'' +
                    ", clazz=" + clazz +
                    '}';
        }
    }

    private static Logger logger = LoggerFactory.getLogger(DiscoveryNavigator.class);
    private transient ApplicationContext applicationContext;
    private static final List<DiscoveryClass> views = new CopyOnWriteArrayList<>();

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
                Class<? extends View> clazz = (Class<? extends View>) Class.forName(bean.getBeanClassName());
                VaadinView vaadinView = (VaadinView) clazz.getAnnotation(VaadinView.class);

                DiscoveryClass discoveryClass = new DiscoveryClass(vaadinView.value(), clazz);
                views.add(discoveryClass);
                logger.debug("found \"{}\" for \"{}\"", new Object[]{vaadinView.value(), bean.getBeanClassName()});
            }
        }
        catch (ClassNotFoundException e)
        {
            logger.error("Error loading: {}", e);
        }
    }

    public DiscoveryNavigator(ApplicationContext applicationContext, UI ui, ViewDisplay display)
    {
        this(applicationContext, ui, display, true);
    }

    public DiscoveryNavigator(ApplicationContext applicationContext, UI ui, ViewDisplay display, boolean discoveryViews)
    {
        super(ui, display);
        this.applicationContext = applicationContext;

        if (discoveryViews)
        {
            discoveryViews("");
        }
    }

    public void discoveryViews(String basePackage, String[] excludePackages)
    {
        for (DiscoveryClass discoveryClass : views)
        {
            String viewName = discoveryClass.viewName;
            Class<? extends View> clazz = discoveryClass.getClazz();
            String packageName = clazz.getPackage().getName();

            if (packageName.startsWith(basePackage))
            {
                boolean exclude = false;
                for (String excludePackage : excludePackages)
                {
                    if (packageName.startsWith(excludePackage))
                    {
                        exclude = true;
                        break;
                    }
                }

                if (!exclude)
                {
                    addBeanView(viewName, clazz);
                }
            }
        }
    }

    public void discoveryViews(String basePackage)
    {
        discoveryViews(basePackage, new String[]{});
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
