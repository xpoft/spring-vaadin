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
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

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
    private static final ConcurrentMap<String, List<DiscoveryClass>> viewsCache = new ConcurrentHashMap<>();
    private transient ApplicationContext applicationContext;

    public DiscoveryNavigator(ApplicationContext applicationContext, UI ui, ViewDisplay display, String basePackage)
    {
        this(applicationContext, ui, display, basePackage, new String[]{});
    }

    public DiscoveryNavigator(ApplicationContext applicationContext, UI ui, ViewDisplay display, String basePackage, String[] excludePackages)
    {
        super(ui, display);
        this.applicationContext = applicationContext;

        logger.debug("discovery views for base package {}", basePackage);

        try
        {
            int excludeHash = 0;
            if (excludePackages != null && excludePackages.length > 0)
            {
                excludeHash = Arrays.hashCode(excludePackages);
            }
            String hash = basePackage.hashCode() + " " + excludeHash;
            if (!viewsCache.containsKey(hash))
            {
                logger.debug("discovery in classpath");

                ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
                scanner.addIncludeFilter(new AnnotationTypeFilter(VaadinView.class));
                for (String excludePackage : excludePackages)
                {
                    logger.debug("exclude package: {}", excludePackage);
                    Pattern pattern = Pattern.compile(excludePackage);
                    scanner.addExcludeFilter(new RegexPatternTypeFilter(pattern));
                }

                List<DiscoveryClass>discoveryClasses = new ArrayList<>();
                Set<BeanDefinition> beans = scanner.findCandidateComponents(basePackage);
                for (BeanDefinition bean : beans)
                {
                    Class<? extends View> clazz = (Class<? extends View>) Class.forName(bean.getBeanClassName());
                    VaadinView vaadinView = clazz.getAnnotation(VaadinView.class);
                    String viewName = vaadinView.value();

                    DiscoveryClass discoveryClass = new DiscoveryClass(viewName, clazz);
                    discoveryClasses.add(discoveryClass);

                    logger.debug("found \"{}\" for \"{}\"", new Object[]{viewName, bean.getBeanClassName()});
                }

                viewsCache.put(hash, discoveryClasses);
            }
            else
            {
                logger.debug("get views from cache");
            }

            for(DiscoveryClass discoveryClass : viewsCache.get(hash))
            {
                addBeanView(discoveryClass.getViewName(), discoveryClass.getClazz());
            }
        }
        catch (ClassNotFoundException e)
        {
            logger.error("Error loading: {}", e);
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
