package ru.xpoft.vaadin;

import com.vaadin.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.portlet.context.PortletApplicationContextUtils;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Date;

/**
 * @author xpoft
 */
public class SpringVaadinPortlet extends VaadinPortlet
{
    private static Logger logger = LoggerFactory.getLogger(SpringVaadinPortlet.class);
    /**
     * Servlet parameter name for UI bean
     */
    private static final String BEAN_NAME_PARAMETER = "beanName";
    /**
     * Servlet parameter name for UI bean
     */
    private static final String SYSTEM_MESSAGES_BEAN_NAME_PARAMETER = "systemMessagesBeanName";
    /**
     * Spring Application Context
     */
    private transient ApplicationContext applicationContext;
    /**
     * UI bean name
     */
    private String vaadinBeanName = "ui";
    private String systemMessagesBeanName = "";

    @Override
    public void init(PortletConfig config) throws PortletException
    {
        applicationContext = PortletApplicationContextUtils.getWebApplicationContext(config.getPortletContext());
        if (config.getInitParameter(BEAN_NAME_PARAMETER) != null)
        {
            vaadinBeanName = config.getInitParameter(BEAN_NAME_PARAMETER);
            logger.debug("found BEAN_NAME_PARAMETER: {}", vaadinBeanName);
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
    protected VaadinPortletService createPortletService(DeploymentConfiguration deploymentConfiguration)
    {
        final VaadinPortletService service = super.createPortletService(deploymentConfiguration);

        // Spring system messages provider
        if (systemMessagesBeanName != null && systemMessagesBeanName != "")
        {
            SpringVaadinSystemMessagesProvider messagesProvider = new SpringVaadinSystemMessagesProvider(applicationContext, systemMessagesBeanName);
            logger.debug("set SpringVaadinSystemMessagesProvider");
            service.setSystemMessagesProvider(messagesProvider);
        }

        // Add UI provider for new session
        service.addSessionInitListener(new SessionInitListener()
        {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException
            {
                event.getSession().addUIProvider(new SpringUIProvider(vaadinBeanName));
            }
        });

        return service;
    }
}
