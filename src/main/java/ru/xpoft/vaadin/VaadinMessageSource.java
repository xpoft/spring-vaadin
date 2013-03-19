package ru.xpoft.vaadin;

import com.vaadin.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author xpoft
 */
public class VaadinMessageSource implements Serializable
{
    @Autowired
    private transient MessageSource messageSource;

    public String getMessage(String code)
    {
        Locale locale = VaadinSession.getCurrent().getLocale();
        return messageSource.getMessage(code, null, locale);
    }

    public String getMessage(String code, String defaultMessage)
    {
        Locale locale = VaadinSession.getCurrent().getLocale();
        return messageSource.getMessage(code, null, defaultMessage, locale);
    }

    public String getMessage(String code, Object[] args)
    {
        Locale locale = VaadinSession.getCurrent().getLocale();
        return messageSource.getMessage(code, args, locale);
    }
}
