package ru.xpoft.vaadin.security;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexf on 14-Jan-15.
 */
public class SpringSecurityNavigator extends SecurityAwareDiscoveryNavigator {
    public SpringSecurityNavigator(UI ui, ComponentContainer container) {
        super(ui, container);
    }

    public SpringSecurityNavigator(UI ui, SingleComponentContainer container) {
        super(ui, container);
    }

    public SpringSecurityNavigator(UI ui, ViewDisplay display) {
        super(ui, display);
    }

    public SpringSecurityNavigator(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
        super(ui, stateManager, display);
    }

    @Override
    protected boolean hasAccess(Class<? extends View> viewClass) {
        Secured secured= AnnotationUtils.findAnnotation(viewClass,Secured.class);
        if(null==secured){
            return true;
        }
        List<String> requiredAuthorities = Arrays.asList(secured.value());
        for(GrantedAuthority grantedAuthority :SecurityContextHolder.getContext().getAuthentication().getAuthorities()){
            if(requiredAuthorities.contains(grantedAuthority.getAuthority())){
                return  true;
            }
        }
        return false;

    }
}
