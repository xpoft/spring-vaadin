package ru.xpoft.vaadin;

import org.springframework.context.ApplicationContext;

import java.io.Serializable;

/**
 * @author xpoft
 */
public class SpringApplicationContext implements Serializable
{
    private static transient ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext)
    {
        SpringApplicationContext.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
}
