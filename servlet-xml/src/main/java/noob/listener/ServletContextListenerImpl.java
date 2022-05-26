package noob.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextListenerImpl implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ServletContextListenerImpl.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.debug("Servlet 容器创建...");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.debug("销毁容器...");
    }
}
