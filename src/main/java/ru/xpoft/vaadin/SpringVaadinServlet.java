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
import java.util.List;

/**
 * @author xpoft
 */
public class SpringVaadinServlet extends VaadinServlet
{
    private static Logger logger = LoggerFactory.getLogger(SpringVaadinServlet.class);
    private static final String BEAN_NAME_PARAMETER = "beanName";
    private static final String REQUEST_CHECK_DATE = SpringVaadinServlet.class.getCanonicalName() + "_check_date";
    private transient ApplicationContext applicationContext;
    private transient Date checkedSerialization = new Date();
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
                service.addUIProvider(event.getSession(), new SpringUIProvider(applicationContext, vaadinBeanName));
            }
        });

        return service;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession httpSession = request.getSession();
        if (httpSession != null)
        {
            Date checkDate = (Date) httpSession.getAttribute(REQUEST_CHECK_DATE);
            if (checkDate == null || checkDate.before(checkedSerialization))
            {
                try
                {
                    VaadinSession vaadinSession = getService().findVaadinSession(createVaadinRequest(request));
                    List<UIProvider> uiProviders = getService().getUIProviders(vaadinSession);
                    for (UIProvider uiProvider : uiProviders)
                    {
                        if (uiProvider instanceof SpringUIProvider)
                        {
                            ApplicationContext context = ((SpringUIProvider) uiProvider).getApplicationContext();
                            if (context == null)
                            {
                                getService().removeUIProvider(vaadinSession, uiProvider);
                                getService().addUIProvider(vaadinSession, new SpringUIProvider(applicationContext, vaadinBeanName));
                            }
                        }
                    }
                }
                catch (ServiceException | SessionExpiredException e)
                {
                    throw new RuntimeException(e);
                }
            }

            httpSession.setAttribute(REQUEST_CHECK_DATE, checkedSerialization);
        }

        super.service(request, response);
    }
}
