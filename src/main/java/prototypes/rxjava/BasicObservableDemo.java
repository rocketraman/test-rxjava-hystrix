package prototypes.rxjava;

import rx.Observable;
import rx.util.Timestamped;
import rx.util.functions.Action0;
import rx.util.functions.Action1;

import java.util.ArrayList;
import java.util.List;

import static prototypes.rxjava.util.Sleeper.sleep;

public class BasicObservableDemo {

    public static void main(String[] args) throws Exception {

        ArrayList<String> names = new ArrayList<>();
        names.add("foo");
        names.add("bar");
        names.add("baz");
        names.add("goo");

        final Observable<String> observable = Observable.from(names);

        observable.timestamp().subscribe(
            new Action1<Timestamped<String>>() {
                @Override
                public void call(final Timestamped<String> stringTimestamped) {
                    System.out.println("Hello " + stringTimestamped);
                }
            });

        System.out.println("========================");

        observable.buffer(2).timestamp().subscribe(
            new Action1<Timestamped<List<String>>>() {
                @Override
                public void call(final Timestamped<List<String>> stringTimestamped) {
                    System.out.println("Hello " + stringTimestamped);
                }
            });

        System.out.println("========================");

        Observable.from(names).subscribe(
            new Action1<String>() {
                @Override
                public void call(String s) {
                    System.out.println("Hello " + s + "!");
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable throwable) {
                    throwable.printStackTrace();
                }
            }, new Action0() {
                @Override
                public void call() {
                    System.out.println("Done!");
                }
            }
        );

    }

}
