package prototypes.rxjava;

import rx.Observable;
import rx.util.functions.Action0;
import rx.util.functions.Action1;
import rx.util.functions.Func2;

import java.util.ArrayList;

public class ReduceDemo {

    public static void main(String[] args) {

        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);

        Observable.from(numbers)
            .reduce(new Func2<Integer, Integer, Integer>() {
                @Override
                public Integer call(final Integer integer, final Integer integer2) {
                    System.out.println("Reduce on " + integer + " and " + integer2);
                    return integer + integer2;
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
