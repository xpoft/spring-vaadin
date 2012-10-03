package ru.xpoft.vaadin;

import com.vaadin.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

/**
 * @author xpoft
 */
public class SpringVaadinServlet extends VaadinServlet
{
    private static Logger logger = LoggerFactory.getLogger(SpringVaadinServlet.class);
    /**
     * Servlet parameter name for UI bean
     */
    private static final String BEAN_NAME_PARAMETER = "beanName";
    /**
     * Session attribute for save check date
     */
    private static final String REQUEST_CHECK_DATE = SpringVaadinServlet.class.getCanonicalName() + "_check_date";
    /**
     * Spring Application Context
     */
    private transient ApplicationContext applicationContext;
    /**
     * Servlet start date
     */
    private transient Date servletStartDate = new Date();
    /**
     * UI bean name
     */
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

        // Add UI provider for new session
        service.addSessionInitListener(new SessionInitListener()
        {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException
            {
                event.getSession().addUIProvider(new SpringUIProvider(applicationContext, vaadinBeanName));
            }
        });

        return service;
    }

    /**
     * We need replace spring context for deserialized beans
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession httpSession = request.getSession();
        if (httpSession != null)
        {
            Date checkDate = (Date) httpSession.getAttribute(REQUEST_CHECK_DATE);
            if (checkDate == null || checkDate.before(servletStartDate))
            {
                try
                {
                    VaadinServiceSession vaadinSession = getService().findVaadinSession(createVaadinRequest(request));
                    for (UIProvider uiProvider : vaadinSession.getUIProviders())
                    {
                        // Spring UI provider with NULL context
                        // Replace context
                        if (uiProvider instanceof SpringUIProvider && ((SpringUIProvider) uiProvider).getApplicationContext() == null)
                        {
                            vaadinSession.removeUIProvider(uiProvider);
                            vaadinSession.addUIProvider(new SpringUIProvider(applicationContext, vaadinBeanName));
                        }
                    }
                }
                catch (ServiceException | SessionExpiredException e)
                {
                    throw new RuntimeException(e);
                }
            }

            httpSession.setAttribute(REQUEST_CHECK_DATE, servletStartDate);
        }

        super.service(request, response);
    }
}
