package prototypes.rxjava;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;
import rx.Observable;
import rx.util.functions.Func2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates use of Hystrix. Hystrix does add some overhead to calls, but should be acceptable and
 * small compared to the total call overhead. Hystrix should be used for remote calls.
 */
public class HystrixRxServicesDemo {

    public HystrixRxServicesDemo() {

        // service A and service B are called synchronously
        // combined result of A and B feeds into service C
        // result of C is the final response

        // service A
        final Stopwatch timer = SimonManager.getStopwatch("Sync");

        Split split = timer.start();

        //String serviceA = prototypes.rxjava.SyncServicesDemo.ServiceA.doService();
        //String serviceB = prototypes.rxjava.SyncServicesDemo.ServiceB.doService();
        //List<String> serviceC = prototypes.rxjava.SyncServicesDemo.ServiceC.doService(serviceA, serviceB);

        Observable<String> serviceA = new ServiceAHystrixCommand().observe();
        Observable<String> serviceB = new ServiceBHystrixCommand().observe();

        Map<String, String>
            serviceAandBResults = Observable.zip(serviceA, serviceB, new Func2<String, String, Map<String, String>>() {
            @Override
            public Map<String, String> call(final String s, final String s2) {
                Map<String, String> m = new HashMap<>();
                m.put("a", s);
                m.put("b", s2);
                return m;
            }
        }).toBlockingObservable().single();

        final String a = serviceAandBResults.get("a");
        final String b = serviceAandBResults.get("b");

        List<String> serviceC = new ServiceCHystrixCommand(a, b).execute();

        for(String s : serviceC) {
            System.out.println("Result: " + s);
        }

        split.stop();

        System.out.println("Time: " + timer);

        for(HystrixCommandMetrics m : HystrixCommandMetrics.getInstances()) {
            System.out.println(getStatsStringFromMetrics(m));
        }

    }

    public static void main(String[] args) throws Exception {

        new HystrixRxServicesDemo();

        System.out.println("Exiting JVM.");
        System.exit(0);

    }

    private static String getStatsStringFromMetrics(HystrixCommandMetrics metrics) {
        StringBuilder m = new StringBuilder();
        if (metrics != null) {
            HystrixCommandMetrics.HealthCounts health = metrics.getHealthCounts();
            m.append(metrics.getCommandKey().name()).append(" Sample ");
            m.append("Requests: ").append(health.getTotalRequests()).append(" ");
            m.append("Errors: ").append(health.getErrorCount()).append(" (").append(health.getErrorPercentage()).append( "%)   ");
            m.append("Mean: ").append(metrics.getExecutionTimePercentile(50)).append(" ");
            m.append("75th: ").append(metrics.getExecutionTimePercentile(75)).append(" ");
            m.append("90th: ").append(metrics.getExecutionTimePercentile(90)).append(" ");
            m.append("99th: ").append(metrics.getExecutionTimePercentile(99)).append(" ");
        }
        return m.toString();
    }

    static class ServiceAHystrixCommand extends HystrixCommand<String> {
        ServiceAHystrixCommand() {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("DemoServices"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                    .withExecutionIsolationThreadTimeoutInMilliseconds(5000)));
        }

        @Override
        protected String run() throws Exception {
            return SyncServicesDemo.ServiceA.doService();
        }
    }

    static class ServiceBHystrixCommand extends HystrixCommand<String> {
        ServiceBHystrixCommand() {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("DemoServices"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                    .withExecutionIsolationThreadTimeoutInMilliseconds(5000)));
        }

        @Override
        protected String run() throws Exception {
            // fail 20% of the time
            if(Math.random() > 0.8) {
                throw new Exception("I'm failing randomly!");
            }
            return SyncServicesDemo.ServiceB.doService();
        }

        @Override
        protected String getFallback() {
            return "Service-B-Fallback";
        }
    }

    static class ServiceCHystrixCommand extends HystrixCommand<List<String>> {

        private String serviceAResult;
        private String serviceBResult;

        ServiceCHystrixCommand(final String serviceAResult, final String serviceBResult) {
            super(HystrixCommandGroupKey.Factory.asKey("DemoServices"));
            this.serviceAResult = serviceAResult;
            this.serviceBResult = serviceBResult;
        }

        @Override
        protected List<String> run() throws Exception {
            return SyncServicesDemo.ServiceC.doService(serviceAResult, serviceBResult);
        }
    }

}
