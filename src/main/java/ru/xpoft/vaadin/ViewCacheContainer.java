package ru.xpoft.vaadin;

import com.vaadin.navigator.View;

/**
 * @author xpoft
 */
public interface ViewCacheContainer
{
    public View getView(String name, String beanName, boolean cached);
}
