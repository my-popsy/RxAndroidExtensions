package popsy.rxjava.android.content;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class ContentObservable implements Observable.OnSubscribe<Uri> {

    private final ContentResolver mContentResolver;
    private final Uri mUri;
    private final boolean mNotifyForDescendents;

    public static Observable<Uri> create(Context context, Uri uri, boolean notifyForDescendents) {
        return Observable.create(new ContentObservable(context, uri, notifyForDescendents));
    }

    ContentObservable(Context context, Uri uri, boolean notifyForDescendents) {
        this.mContentResolver = context.getContentResolver();
        this.mUri = uri;
        this.mNotifyForDescendents = notifyForDescendents;
    }

    @Override
    public void call(final Subscriber<? super Uri> subscriber) {

        final ContentObserver observer = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
                onChange(selfChange, mUri);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                if(!subscriber.isUnsubscribed())
                    subscriber.onNext(uri);
                else
                    mContentResolver.unregisterContentObserver(this);
            }
        };

        final Subscription subscription = Subscriptions.create(new Action0() {
            @Override
            public void call() {
                mContentResolver.unregisterContentObserver(observer);
            }
        });

        subscriber.add(subscription);
        mContentResolver.registerContentObserver(mUri, mNotifyForDescendents, observer);
    }
}
