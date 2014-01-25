package prototypes.rxjava;

import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;

import java.util.Arrays;
import java.util.List;

import static prototypes.rxjava.util.Sleeper.sleep;

public class SyncServicesDemo {

    public static void main(String[] args) throws Exception {

        // service A and service B are called synchronously
        // combined result of A and B feeds into service C
        // result of C is the final response

        // service A
        Stopwatch timer = SimonManager.getStopwatch("Sync");
        Split split = timer.start();

        String serviceA = ServiceA.doService();
        String serviceB = ServiceB.doService();
        List<String> serviceC = ServiceC.doService(serviceA, serviceB);

        for(String s : serviceC) {
            System.out.println("Result: " + s);
        }

        split.stop();
        System.out.println("Time: " + timer);

    }

    static class ServiceA {

        public static String doService() {
            sleep(2000);
            return "Service-A-Result";
        }

    }

    static class ServiceB {

        public static String doService() {
            sleep(1500);
            return "Service-B-Result";
        }

    }

    static class ServiceC {

        public static List<String> doService(String serviceAResult, String serviceBResult) {
            sleep(100);
            return Arrays.asList(serviceAResult + "-From-C", serviceBResult + "-From-C", "Service-C-Result");
        }

    }

}
