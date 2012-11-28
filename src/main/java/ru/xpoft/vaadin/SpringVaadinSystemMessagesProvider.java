package ru.xpoft.vaadin;

import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xpoft
 */
public class SpringVaadinSystemMessagesProvider implements SystemMessagesProvider
{
    public static final String DEFAULT_IMPLEMENTATION = "DEFAULT";
    private static Logger logger = LoggerFactory.getLogger(SpringVaadinSystemMessagesProvider.class);
    private final ConcurrentMap<Locale, SystemMessages> systemMessagesCache = new ConcurrentHashMap<Locale, SystemMessages>();
    private SpringSystemMessagesProvider systemMessagesBean;

    public SpringVaadinSystemMessagesProvider(ApplicationContext applicationContext, String systemMessagesBeanName)
    {
        if (systemMessagesBeanName.equals(DEFAULT_IMPLEMENTATION))
        {
            MessageSource messageSource = applicationContext.getBean(MessageSource.class);
            systemMessagesBean = new DefaultSpringSystemMessagesProvider();
            ((DefaultSpringSystemMessagesProvider)systemMessagesBean).setMessageSource(messageSource);
            logger.debug("use default systemMessagesBean");
        }
        else
        {
            systemMessagesBean = applicationContext.getBean(systemMessagesBeanName, SpringSystemMessagesProvider.class);
        }
        logger.debug("get systemMessagesBean: {}", systemMessagesBean);
    }

    @Override
    public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo)
    {
        Locale locale = systemMessagesInfo.getLocale();

        if (systemMessagesCache.containsKey(locale))
        {
            return systemMessagesCache.get(locale);
        }

        SystemMessages systemMessages = systemMessagesBean.getSystemMessages(locale);
        systemMessagesCache.put(locale, systemMessages);

        return systemMessages;
    }
}
