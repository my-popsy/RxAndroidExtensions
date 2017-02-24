package popsy.rxjava.android.view;

import android.view.View;
import android.view.ViewTreeObserver;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class PreDrawObservable implements Observable.OnSubscribe<View> {

    private final View view;

    public static Observable<View> create(View view) {
        return Observable.create(new PreDrawObservable(view));
    }

    private PreDrawObservable(View view) {
        this.view = view;
    }

    @Override
    public void call(final Subscriber<? super View> subscriber) {

        final ViewTreeObserver.OnPreDrawListener observer = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (subscriber.isUnsubscribed()) {
                    remove(this);
                    return false;
                }else{
                    subscriber.onNext(view);
                    return true;
                }
            }
        };

        final Subscription subscription = Subscriptions.create(new Action0() {
            @Override
            public void call() {
                remove(observer);
            }
        });

        subscriber.add(subscription);

        final ViewTreeObserver vto = view.getViewTreeObserver();

        if(vto.isAlive())
            view.getViewTreeObserver().addOnPreDrawListener(observer);
        else
            subscriber.onCompleted();
    }

    private void remove(ViewTreeObserver.OnPreDrawListener listener) {
        final ViewTreeObserver vto = view.getViewTreeObserver();
        if (!vto.isAlive()) {
            return;
        }
        vto.removeOnPreDrawListener(listener);
    }
}
