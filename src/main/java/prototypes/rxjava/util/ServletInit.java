package prototypes.rxjava.util;

import prototypes.rxjava.HystrixRxServicesDemo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Timer;
import java.util.TimerTask;

public class ServletInit implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {

        System.out.println("============== STARTING HYSTRIX DEMO ==============");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                new HystrixRxServicesDemo();
            }
        }, 0, 3000);

    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {

    }

}
