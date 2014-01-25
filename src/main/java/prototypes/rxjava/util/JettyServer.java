package prototypes.rxjava.util;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

    public static void main(String[] args) throws Exception {

        Server s = new Server(8080);

        WebAppContext webContext = new WebAppContext();
        webContext.setResourceBase("src/main/webapp");
        webContext.setContextPath("/test-rxjava-hystrix");
        webContext.setParentLoaderPriority(true);

        s.setHandler(webContext);

        s.start();
        s.join();

    }

}
