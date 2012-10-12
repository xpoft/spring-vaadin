package ru.xpoft.vaadin;

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Default provider
 *
 * @author xpoft
 */
public class DefaultSpringSystemMessagesProvider implements SpringSystemMessagesProvider
{
    private static Logger logger = LoggerFactory.getLogger(DefaultSpringSystemMessagesProvider.class);
    private transient MessageSource messageSource;
    private static final String MESSAGE_NOT_FOUND = "MESSAGE_NOT_FOUND";

    public void setMessageSource(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    @Override
    public SystemMessages getSystemMessages(Locale locale)
    {
        CustomizedSystemMessages systemMessages = new CustomizedSystemMessages();
        String message;

        message = messageSource.getMessage("vaadin.sessionExpired.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setSessionExpiredCaption(message);
        }
        message = messageSource.getMessage("vaadin.sessionExpired.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setSessionExpiredMessage(message);
        }
        message = messageSource.getMessage("vaadin.sessionExpired.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setSessionExpiredURL(message);
        }
        message = messageSource.getMessage("vaadin.sessionExpired.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setSessionExpiredNotificationEnabled(notificationEnabled);
        }

        message = messageSource.getMessage("vaadin.communicationError.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setCommunicationErrorCaption(message);
        }
        message = messageSource.getMessage("vaadin.communicationError.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setCommunicationErrorMessage(message);
        }
        message = messageSource.getMessage("vaadin.communicationError.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setCommunicationErrorURL(message);
        }
        message = messageSource.getMessage("vaadin.communicationError.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setCommunicationErrorNotificationEnabled(notificationEnabled);
        }

        message = messageSource.getMessage("vaadin.authenticationError.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setAuthenticationErrorCaption(message);
        }
        message = messageSource.getMessage("vaadin.authenticationError.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setAuthenticationErrorMessage(message);
        }
        message = messageSource.getMessage("vaadin.authenticationError.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setAuthenticationErrorURL(message);
        }
        message = messageSource.getMessage("vaadin.authenticationError.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setAuthenticationErrorNotificationEnabled(notificationEnabled);
        }

        message = messageSource.getMessage("vaadin.internalError.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setInternalErrorCaption(message);
        }
        message = messageSource.getMessage("vaadin.internalError.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setInternalErrorMessage(message);
        }
        message = messageSource.getMessage("vaadin.internalError.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setInternalErrorURL(message);
        }
        message = messageSource.getMessage("vaadin.internalError.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setInternalErrorNotificationEnabled(notificationEnabled);
        }

        message = messageSource.getMessage("vaadin.outOfSync.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setOutOfSyncCaption(message);
        }
        message = messageSource.getMessage("vaadin.outOfSync.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setOutOfSyncMessage(message);
        }
        message = messageSource.getMessage("vaadin.outOfSync.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setOutOfSyncURL(message);
        }
        message = messageSource.getMessage("vaadin.outOfSync.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setOutOfSyncNotificationEnabled(notificationEnabled);
        }

        message = messageSource.getMessage("vaadin.cookiesDisabled.Caption", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setCookiesDisabledCaption(message);
        }
        message = messageSource.getMessage("vaadin.cookiesDisabled.Message", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setCookiesDisabledMessage(message);
        }
        message = messageSource.getMessage("vaadin.cookiesDisabled.URL", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            systemMessages.setCookiesDisabledURL(message);
        }
        message = messageSource.getMessage("vaadin.cookiesDisabled.NotificationEnabled", null, MESSAGE_NOT_FOUND, locale);
        if (!message.equals(MESSAGE_NOT_FOUND))
        {
            boolean notificationEnabled = (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("1"));
            systemMessages.setCookiesDisabledNotificationEnabled(notificationEnabled);
        }

        return systemMessages;
    }
}
