package org.ligi.gobandroidhd.ai.gnugo;

import org.ligi.gobandroidhd.ai.gnugo.IGnuGoService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

public class GnuGoService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return mRemoteServiceStub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, R.string.service_create, Toast.LENGTH_SHORT).show();
        System.loadLibrary("gnuGo-3.8");
        GnuGoConnection.initGTP(8);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, R.string.service_destory, Toast.LENGTH_SHORT).show();
    }

    private IGnuGoService.Stub mRemoteServiceStub = new IGnuGoService.Stub() {

        @Override
        public String processGTP(String command) throws RemoteException {
            return GnuGoConnection.playGTP(command);
        }
    };
}
