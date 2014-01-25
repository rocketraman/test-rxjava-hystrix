package prototypes.rxjava;

import rx.Observable;
import rx.util.functions.Action0;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

import java.util.ArrayList;
import java.util.Arrays;

public class FlatmapDemo {

    public static void main(String[] args) {

        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);

        Observable.from(numbers)
            .flatMap(
                new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(final Integer integer) {
                        return Observable.from(Arrays.asList(integer * 2, integer * 3));
                    }
                })
            .subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer i) {
                        System.out.println("Hello " + i + "!");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable throwable) {
                        System.out.println("Running Throwable action 2, stack trace:");
                        throwable.printStackTrace(System.out);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        System.out.println("Done!");
                    }
                }
            );

        System.out.println("Done main!");

    }

}
