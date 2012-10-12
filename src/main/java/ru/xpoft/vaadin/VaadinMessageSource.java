package ru.xpoft.vaadin;

import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

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
        Locale locale = UI.getCurrent().getSession().getLocale();
        return messageSource.getMessage(code, null, locale);
    }

    public String getMessage(String code, String defaultMessage)
    {
        Locale locale = UI.getCurrent().getSession().getLocale();
        return messageSource.getMessage(code, null, defaultMessage, locale);
    }

    public String getMessage(String code, Object[] args)
    {
        Locale locale = UI.getCurrent().getSession().getLocale();
        return messageSource.getMessage(code, args, locale);
    }
}
