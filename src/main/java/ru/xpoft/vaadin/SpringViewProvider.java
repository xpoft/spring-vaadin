package ru.xpoft.vaadin;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import org.springframework.context.ApplicationContext;

/**
 * @author xpoft
 */
public class SpringViewProvider extends Navigator.ClassBasedViewProvider
{
    private transient ApplicationContext applicationContext;

    /**
     * Create a new view provider which creates new view instances based on
     * a view class.
     *
     * @param viewName  name of the views to create (not null)
     * @param viewClass class to instantiate when a view is requested (not null)
     */
    public SpringViewProvider(ApplicationContext applicationContext, String viewName, Class<? extends View> viewClass)
    {
        super(viewName, viewClass);
        this.applicationContext = applicationContext;
    }

    @Override
    public View getView(String viewName)
    {
        if (getViewName().equals(viewName))
        {
            return applicationContext.getBean(getViewClass());
        }

        return null;
    }
}
