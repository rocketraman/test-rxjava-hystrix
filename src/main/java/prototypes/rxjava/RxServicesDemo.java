package prototypes.rxjava;

import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.ExecutorScheduler;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action0;
import rx.util.functions.Action1;
import rx.util.functions.Func2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class RxServicesDemo {

    public static void main(String[] args) throws Exception {

        // simulates:
        // service A and service B called simultaneously
        // combined result of A and B feeds into service C
        // result of C is the final response

        Stopwatch timer = SimonManager.getStopwatch("RxJava");
        Split split = timer.start();

        // service A
        Observable<String> serviceA = ServiceA.doService();
        Observable<String> serviceB = ServiceB.doService();
        Observable<String> serviceC = ServiceC.doService(serviceA, serviceB);

//        final Semaphore semaphore = new Semaphore(0);

        serviceC.subscribe(
            new Action1<String>() {
                @Override
                public void call(final String s) {
                    System.out.println("Result: " + s);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable throwable) {
                    System.out.println("Observable error:");
                    throwable.printStackTrace(System.out);
//                    semaphore.release();
                }
            }, new Action0() {
                @Override
                public void call() {
//                    semaphore.release();
                }
            }, new ExecutorScheduler(Executors.newFixedThreadPool(5))
        );

        System.out.println("Waiting for Observables.");
        serviceC.toBlockingObservable().last();
//        semaphore.acquire();

        split.stop();
        System.out.println("Time: " + timer);

        System.out.println("Exiting JVM.");
        System.exit(0);

    }

    static class ServiceA {

        public static Observable<String> doService() {
            return Observable.create(new Observable.OnSubscribeFunc<String>() {
                @Override
                public Subscription onSubscribe(final Observer<? super String> o) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            o.onNext(SyncServicesDemo.ServiceA.doService());
                            o.onCompleted();
                        }
                    }).start();
                    return Subscriptions.empty();
                }
            });
        }

    }

    static class ServiceB {

        public static Observable<String> doService() {
            return Observable.create(new Observable.OnSubscribeFunc<String>() {
                @Override
                public Subscription onSubscribe(final Observer<? super String> o) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            o.onNext(SyncServicesDemo.ServiceB.doService());
                            o.onCompleted();
                        }
                    }).start();
                    return Subscriptions.empty();
                }
            });
        }

    }

    static class ServiceC {

        public static Observable<String> doService(final Observable<String> serviceAResult, final Observable<String> serviceBResult) {
            return Observable.create(new Observable.OnSubscribeFunc<String>() {
                @Override
                public Subscription onSubscribe(final Observer<? super String> o) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
/*                          // blocking calls on service a and b, not good because total time will be addition of both
                            String a = serviceAResult.toBlockingObservable().single();
                            String b = serviceBResult.toBlockingObservable().single();
*/
                            // just to demonstrate zip
                            Map<String, String> serviceAandBResults = Observable.zip(serviceAResult, serviceBResult, new Func2<String, String, Map<String, String>>() {
                                @Override
                                public Map<String, String> call(final String left, final String right) {
                                    Map<String, String> m = new HashMap<>();
                                    m.put("a", left);
                                    m.put("b", right);
                                    return m;
                                }
                            }).toBlockingObservable().single();

                            final String a = serviceAandBResults.get("a");
                            final String b = serviceAandBResults.get("b");

                            final List<String> strings = SyncServicesDemo.ServiceC.doService(a, b);
                            for(String s : strings) o.onNext(s);
                            o.onCompleted();
                        }
                    }).start();
                    return Subscriptions.empty();
                }
            });
        }
    }

}
