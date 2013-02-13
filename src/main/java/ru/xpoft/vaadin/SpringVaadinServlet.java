package ru.xpoft.vaadin;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

/**
 * @author xpoft
 */
public class SpringVaadinServlet extends VaadinServlet {
  private static Logger logger = LoggerFactory
      .getLogger(SpringVaadinServlet.class);
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
  private String systemMessagesBeanName = "";

  /*
   * (non-Javadoc)
   * 
   * @see com.vaadin.server.VaadinServlet#init(javax.servlet.ServletConfig)
   */
  @Override
  public void init(ServletConfig servletConfig) throws ServletException {
    applicationContext = WebApplicationContextUtils
        .getWebApplicationContext(servletConfig.getServletContext());

    if (servletConfig.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER) != null) {
      systemMessagesBeanName = servletConfig
          .getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER);
      logger.debug("found SYSTEM_MESSAGES_BEAN_NAME_PARAMETER: {}",
          systemMessagesBeanName);
    }

    if (SpringApplicationContext.getApplicationContext() == null) {
      SpringApplicationContext.setApplicationContext(applicationContext);
    }

    super.init(servletConfig);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.vaadin.server.VaadinServlet#createServletService(com.vaadin.server.
   * DeploymentConfiguration)
   */
  @Override
  protected VaadinServletService createServletService(
      DeploymentConfiguration deploymentConfiguration) {
    final VaadinServletService service = super
        .createServletService(deploymentConfiguration);

    // Spring system messages provider
    if (systemMessagesBeanName != null && systemMessagesBeanName != "") {
      SpringVaadinSystemMessagesProvider messagesProvider = new SpringVaadinSystemMessagesProvider(
          applicationContext, systemMessagesBeanName);
      logger.debug("set SpringVaadinSystemMessagesProvider");
      service.setSystemMessagesProvider(messagesProvider);
    }

    return service;
  }
}
