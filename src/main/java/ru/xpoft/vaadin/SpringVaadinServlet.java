package ru.xpoft.vaadin;

import com.vaadin.server.*;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xpoft
 */
public class SpringVaadinServlet extends VaadinServlet
{
    private class SpringProvider extends UIProvider
    {
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
    }

    private static Logger logger = LoggerFactory.getLogger(SpringVaadinServlet.class);
    private transient WebApplicationContext applicationContext;
    private static final String BEAN_NAME_PARAMETER = "beanName";
    private String vaadinBeanName = "ui";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);

        applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
        if (servletConfig.getInitParameter(BEAN_NAME_PARAMETER) != null)
        {
            vaadinBeanName = servletConfig.getInitParameter(BEAN_NAME_PARAMETER);
        }
    }

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
    {
        final VaadinServletService service = super.createServletService(deploymentConfiguration);
        service.addSessionInitListener(new SessionInitListener()
        {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException
            {
                service.addUIProvider(event.getSession(), new SpringProvider());
            }
        });

        return service;
    }
}
