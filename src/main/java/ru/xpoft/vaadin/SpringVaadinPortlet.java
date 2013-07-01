package ru.xpoft.vaadin;

import com.vaadin.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.portlet.context.PortletApplicationContextUtils;
import org.springframework.web.portlet.context.XmlPortletApplicationContext;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

/**
 * @author xpoft
 */
public class SpringVaadinPortlet extends VaadinPortlet
{
    private static Logger logger = LoggerFactory.getLogger(SpringVaadinPortlet.class);
    /**
     * Servlet parameter name for UI bean
     */
    private static final String SYSTEM_MESSAGES_BEAN_NAME_PARAMETER = "systemMessagesBeanName";
    private static final String CONTEXT_CONFIG_LOCATION_PARAMETER = "contextConfigLocation";
    /**
     * Spring Application Context
     */
    private transient ApplicationContext applicationContext;
    /**
     * system message bean name
     */
    private String systemMessagesBeanName = "";

    @Override
    public void init(PortletConfig config) throws PortletException
    {
        applicationContext = PortletApplicationContextUtils.getWebApplicationContext(config.getPortletContext());

        if (config.getInitParameter(CONTEXT_CONFIG_LOCATION_PARAMETER) != null)
        {
            XmlPortletApplicationContext context = new XmlPortletApplicationContext();
            context.setParent(applicationContext);
            context.setConfigLocation(config.getInitParameter(CONTEXT_CONFIG_LOCATION_PARAMETER));
            context.setPortletConfig(config);
            context.setPortletContext(config.getPortletContext());
            context.refresh();

            applicationContext = context;
        }

        if (config.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER) != null)
        {
            systemMessagesBeanName = config.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER);
            logger.debug("found SYSTEM_MESSAGES_BEAN_NAME_PARAMETER: {}", systemMessagesBeanName);
        }

        if (SpringApplicationContext.getApplicationContext() == null)
        {
            SpringApplicationContext.setApplicationContext(applicationContext);
        }

        super.init(config);
    }

    @Override
    protected VaadinPortletService createPortletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException
    {
        final VaadinPortletService service = super.createPortletService(deploymentConfiguration);

        // Spring system messages provider
        if (systemMessagesBeanName != null && systemMessagesBeanName != "")
        {
            SpringVaadinSystemMessagesProvider messagesProvider = new SpringVaadinSystemMessagesProvider(applicationContext, systemMessagesBeanName);
            logger.debug("set SpringVaadinSystemMessagesProvider");
            service.setSystemMessagesProvider(messagesProvider);
        }

        String uiProviderProperty = service.getDeploymentConfiguration().getApplicationOrSystemProperty(Constants.SERVLET_PARAMETER_UI_PROVIDER, null);

        // Add SpringUIProvider if custom provider doesn't defined.
        if (uiProviderProperty == null)
        {
            service.addSessionInitListener(new SessionInitListener()
            {
                @Override
                public void sessionInit(SessionInitEvent event) throws ServiceException
                {
                    event.getSession().addUIProvider(new SpringUIProvider());
                }
            });
        }

        return service;
    }
}
