package popsy.rxjava.android.view;

import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class LayoutChangeObservable implements Observable.OnSubscribe<View> {

    private final View view;

    public static Observable<View> create(View view) {
        return Observable.create(new LayoutChangeObservable(view));
    }

    private LayoutChangeObservable(View view) {
        this.view = view;
    }

    @Override
    public void call(final Subscriber<? super View> subscriber) {

        final View.OnLayoutChangeListener listener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (subscriber.isUnsubscribed()) {
                    v.removeOnLayoutChangeListener(this);
                }else{
                    subscriber.onNext(view);
                }
            }
        };

        final Subscription subscription = Subscriptions.create(new Action0() {
            @Override
            public void call() {
                view.removeOnLayoutChangeListener(listener);
            }
        });

        subscriber.add(subscription);
        view.addOnLayoutChangeListener(listener);
    }
}
