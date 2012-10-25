package ru.xpoft.vaadin;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

/**
 * @author xpoft
 */
public class SpringViewProvider extends Navigator.ClassBasedViewProvider
{
    private final String scope;
    private final ViewScopedContainer scopedContainer;

    /**
     * Create a new view provider which creates new view instances based on
     * a view class.
     *
     * @param viewName  name of the views to create (not null)
     * @param viewClass class to instantiate when a view is requested (not null)
     */
    public SpringViewProvider(String viewName, Class<? extends View> viewClass, String scope, ViewScopedContainer scopedContainer)
    {
        super(viewName, viewClass);
        this.scope = scope;
        this.scopedContainer = scopedContainer;
    }

    @Override
    public View getView(String viewName)
    {
        if (getViewName().equals(viewName))
        {
            return scopedContainer.getView(viewName, getViewClass(), scope);
        }

        return null;
    }
}
