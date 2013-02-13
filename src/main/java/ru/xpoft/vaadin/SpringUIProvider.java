package ru.xpoft.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.*;
import com.vaadin.ui.UI;

/**
 * @author xpoft
 */
public class SpringUIProvider extends UIProvider {
  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Servlet parameter name for UI bean.
   * 
   * @value beanName
   */
  public static final String BEAN_NAME_PARAMETER = "beanName";

  private static Logger logger = LoggerFactory
      .getLogger(SpringUIProvider.class);

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.vaadin.server.UIProvider#createInstance(com.vaadin.server.UICreateEvent
   * )
   */
  @Override
  public UI createInstance(UICreateEvent event) {
    return (UI) SpringApplicationContext.getApplicationContext().getBean(
        getUIBeanName(event.getRequest()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.vaadin.server.UIProvider#getUIClass(com.vaadin.server.UIClassSelectionEvent
   * )
   */
  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {

    return (Class<? extends UI>) SpringApplicationContext
        .getApplicationContext().getType(getUIBeanName(event.getRequest()));
  }

  /**
   * Returns the bean name to be retrieved from the application bean context and
   * used as the UI. The default implementation uses the servlet init property
   * {@link #BEAN_NAME_PARAMETER} or "ui" if not defined.
   * 
   * @param request
   *          the current Vaadin request
   * @return the UI bean name in the application context
   */
  protected String getUIBeanName(VaadinRequest request) {
    String vaadinBeanName = "ui";

    Object uiBeanName = request.getService().getDeploymentConfiguration()
        .getApplicationOrSystemProperty(BEAN_NAME_PARAMETER, null);

    if (uiBeanName != null && uiBeanName instanceof String) {
      vaadinBeanName = uiBeanName.toString();
    }

    logger.debug("found BEAN_NAME_PARAMETER: {}", vaadinBeanName);
    return vaadinBeanName;
  }
}
