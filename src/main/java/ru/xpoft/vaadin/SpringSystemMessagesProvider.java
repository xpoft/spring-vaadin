package ru.xpoft.vaadin;

import com.vaadin.server.SystemMessages;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author xpoft
 */
public interface SpringSystemMessagesProvider extends Serializable
{
    public SystemMessages getSystemMessages(Locale locale);
}
