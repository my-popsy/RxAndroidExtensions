package popsy.rxjava.android.net;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import popsy.rxjava.android.content.BroadcastObservable;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

public class NetworkInfoObservable {

    private NetworkInfoObservable(){ }

    static public Observable<NetworkInfo> create(final Context context) {
        return Observable.defer(new Func0<Observable<NetworkInfo>>() {
            @Override
            public Observable<NetworkInfo> call() {
                final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                return BroadcastObservable.create(context, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
                        .map(new Func1<Intent, NetworkInfo>() {
                            @Override
                            public NetworkInfo call(Intent intent) {
                                return cm.getActiveNetworkInfo();
                            }
                        })
                        .startWith(cm.getActiveNetworkInfo());
            }
        });
    }
}
