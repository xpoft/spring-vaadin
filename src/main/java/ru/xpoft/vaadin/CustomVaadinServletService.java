package ru.xpoft.vaadin;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

/**
 * @author xpoft
 */
public class CustomVaadinServletService extends VaadinServletService
{
    private SystemMessages systemMessages;

    public CustomVaadinServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration)
    {
        super(servlet, deploymentConfiguration);
    }

    public void setSystemMessages(SystemMessages systemMessages)
    {
        this.systemMessages = systemMessages;
    }

    @Override
    public SystemMessages getSystemMessages()
    {
        if (systemMessages != null)
        {
            return systemMessages;
        }
        return super.getSystemMessages();
    }
}
