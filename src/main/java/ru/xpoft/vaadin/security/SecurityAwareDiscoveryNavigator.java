package ru.xpoft.vaadin.security;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.xpoft.vaadin.DiscoveryNavigator;

/**
 * Base class for secured views discovery.
 */
public abstract class SecurityAwareDiscoveryNavigator extends DiscoveryNavigator {
    protected static Logger logger = LoggerFactory.getLogger(SecurityAwareDiscoveryNavigator.class);
    
    public SecurityAwareDiscoveryNavigator(UI ui, ComponentContainer container) {
        super(ui, container);
    }

    public SecurityAwareDiscoveryNavigator(UI ui, SingleComponentContainer container) {
        super(ui, container);
    }

    public SecurityAwareDiscoveryNavigator(UI ui, ViewDisplay display) {
        super(ui, display);
    }

    public SecurityAwareDiscoveryNavigator(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
        super(ui, stateManager, display);
    }

    @Override
    public void addBeanView(String viewName, Class<? extends View> viewClass, boolean cached)
    {
        if (!hasAccess(viewClass))
        {
            return;
        }

        super.addBeanView(viewName, viewClass, cached);
    }

    @Override
    protected void addCachedBeans()
    {
        for (ViewCache view : views)
        {
            // Only allowed beans
            if (hasAccess(view.getClazz()))
            {
                logger.debug("view name: \"{}\", class: {}, viewCached: {}", new Object[]{view.getName(), view.getClazz(), view.isCached()});
                addBeanView(view.getName(), view.getBeanName(), view.getClazz(), view.isCached());
            }
        }
    }

    /**
     * Check access for class
     *
     * @param viewClass
     * @return
     */
    protected abstract boolean hasAccess(Class<? extends View> viewClass);
    


}
