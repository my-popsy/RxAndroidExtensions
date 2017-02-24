package popsy.rxjava.android.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;


public class BroadcastObservable implements Observable.OnSubscribe<Intent> {

    private final Context context;
    private final IntentFilter intentFilter;

    public static Observable<Intent> create(Context context, IntentFilter intentFilter) {
        return Observable.create(new BroadcastObservable(context, intentFilter));
    }

    BroadcastObservable(Context context, IntentFilter intentFilter) {
        this.context = context;
        this.intentFilter = intentFilter;
    }

    @Override
    public void call(final Subscriber<? super Intent> subscriber) {
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!subscriber.isUnsubscribed())
                    subscriber.onNext(intent);
                else
                    context.unregisterReceiver(this);
            }
        };

        final Subscription subscription = Subscriptions.create(new Action0() {
            @Override
            public void call() {
                context.unregisterReceiver(receiver);
            }
        });

        subscriber.add(subscription);
        context.registerReceiver(receiver, intentFilter);
    }
}
