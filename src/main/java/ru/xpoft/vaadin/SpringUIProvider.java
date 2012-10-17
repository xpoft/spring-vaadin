package ru.xpoft.vaadin;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

/**
 * @author xpoft
 */
@Configurable(preConstruction = true)
class SpringUIProvider extends UIProvider
{
    private static Logger logger = LoggerFactory.getLogger(SpringUIProvider.class);
    @Autowired
    private transient ApplicationContext applicationContext;
    private final String vaadinBeanName;

    public SpringUIProvider(String vaadinBeanName)
    {
        this.vaadinBeanName = vaadinBeanName;
    }

    @Override
    public UI createInstance(UICreateEvent event)
    {
        return (UI) applicationContext.getBean(vaadinBeanName);
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent uiClassSelectionEvent)
    {
        return (Class<? extends UI>) applicationContext.getType(vaadinBeanName);
    }

    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
}
