package ru.xpoft.vaadin;

import com.vaadin.navigator.View;

/**
 * @author xpoft
 */
public interface ViewScopedContainer
{
    public View getView(String name, Class<? extends View> clazz, String scope);
}
