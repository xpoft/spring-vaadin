package ru.xpoft.vaadin;

import com.vaadin.server.*;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author xpoft
 */
public class SpringVaadinServlet extends VaadinServlet
{
    private class SpringProvider extends AbstractUIProvider
    {
        @Override
        public UI createInstance(WrappedRequest request, Class<? extends UI> type)
        {
            return (UI) applicationContext.getBean(vaadinBeanName);
        }

        @Override
        public Class<? extends UI> getUIClass(WrappedRequest request)
        {
            return (Class<? extends UI>) applicationContext.getType(vaadinBeanName);
        }
    }

    private static Logger logger = LoggerFactory.getLogger(SpringVaadinServlet.class);
    private WebApplicationContext applicationContext;
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
    protected void onVaadinSessionStarted(WrappedHttpServletRequest request, VaadinServletSession session) throws ServletException
    {
        session.addUIProvider(new SpringProvider());
        super.onVaadinSessionStarted(request, session);
    }
}
