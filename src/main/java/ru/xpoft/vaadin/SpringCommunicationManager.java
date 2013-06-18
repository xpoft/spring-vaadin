package ru.xpoft.vaadin;

import com.vaadin.server.*;
import com.vaadin.ui.UI;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author xpoft
 */
public class SpringCommunicationManager extends CommunicationManager
{
    /**
     * TODO New constructor - document me!
     *
     * @param session
     */
    public SpringCommunicationManager(VaadinSession session)
    {
        super(session);
    }

    @Override
    public void handleBrowserDetailsRequest(VaadinRequest request, VaadinResponse response, VaadinSession session) throws IOException
    {
        List<UIProvider> uiProviders = session.getUIProviders();

        UIClassSelectionEvent classSelectionEvent = new UIClassSelectionEvent(
                request);

        UIProvider provider = null;
        Class<? extends UI> uiClass = null;
        for (UIProvider p : uiProviders)
        {
            uiClass = p.getUIClass(classSelectionEvent);
            if (uiClass != null)
            {
                provider = p;
                break;
            }
        }

        if (provider != null)
        {
            if (provider instanceof SpringUIProvider && ((SpringUIProvider)provider).isSessionScopedUI(request))
            {
                String windowName = request.getParameter("v-wn");

                Map<String, Integer> retainOnRefreshUIs = session.getPreserveOnRefreshUIs();
                if (windowName != null && !retainOnRefreshUIs.isEmpty() && retainOnRefreshUIs.get(windowName) == null)
                {
                    // Check for session-scope-UI
                    for(Map.Entry<String, Integer> entry : retainOnRefreshUIs.entrySet())
                    {
                        UI ui = session.getUIById(entry.getValue());
                        session.getPreserveOnRefreshUIs().put(windowName, ui.getUIId());
                        break;
                    }
                }
            }
        }

        super.handleBrowserDetailsRequest(request, response, session);
    }
}
