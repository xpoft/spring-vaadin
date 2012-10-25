package ru.xpoft.vaadin;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xpoft
 */
class SpringUIProvider extends UIProvider
{
    private static Logger logger = LoggerFactory.getLogger(SpringUIProvider.class);

    private final String vaadinBeanName;

    public SpringUIProvider(String vaadinBeanName)
    {
        this.vaadinBeanName = vaadinBeanName;
    }

    @Override
    public UI createInstance(UICreateEvent event)
    {
        return (UI) SpringApplicationContext.getApplicationContext().getBean(vaadinBeanName);
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent uiClassSelectionEvent)
    {
        return (Class<? extends UI>) SpringApplicationContext.getApplicationContext().getType(vaadinBeanName);
    }
}
