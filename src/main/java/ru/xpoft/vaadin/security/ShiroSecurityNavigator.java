package ru.xpoft.vaadin.security;

import com.vaadin.navigator.View;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.xpoft.vaadin.DiscoveryNavigator;
import ru.xpoft.vaadin.SpringApplicationContext;

import java.util.*;

/**
 * Uses RequiresRoles annotation. You don't need AspectJ
 * Exclude views, that user doesn't have access
 *
 * @author xpoft
 */
public class ShiroSecurityNavigator extends DiscoveryNavigator
{
    private static Logger logger = LoggerFactory.getLogger(ShiroSecurityNavigator.class);

    public ShiroSecurityNavigator(UI ui, ComponentContainer display)
    {
        super(ui, display);
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

    @Override
    public void addBeanView(String viewName, Class<? extends View> viewClass, boolean cached)
    {
        if (!hasAccess(viewClass))
        {
            return;
        }

        super.addBeanView(viewName, viewClass, cached);
    }

    private boolean hasAccess(Class<?> clazz)
    {
        if (clazz.isAnnotationPresent(RequiresRoles.class))
        {
            RequiresRoles requiresRoles = clazz.getAnnotation(RequiresRoles.class);
            String[] roles = requiresRoles.value();
            if (roles.length > 0)
            {
                Subject subject = SecurityUtils.getSubject();
                if (!subject.isAuthenticated() || !subject.hasAllRoles(Arrays.asList(roles)))
                {
                    return false;
                }
            }
        }

        return true;
    }
}
