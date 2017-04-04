package keep.jack.com.processkeep;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;



/**
 * 用于唤醒LocalService的RemoteService
 */
public class RemoteService extends Service {

    private RemoteBinder binder;   //和LocalService的binder相互绑定
    private RemoteConn myConn;
    private final String TAG = "RemoteService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new RemoteBinder();
        if(myConn == null){
            myConn = new RemoteConn();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        bindService(new Intent(this, LocalService.class),
                myConn,
                Context.BIND_IMPORTANT);
    }

    class RemoteBinder extends ProcessService.Stub{
        @Override
        public String getServiceName() throws RemoteException {
            return "RemoteService";
        }
    }


    class RemoteConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "LocalService连接成功--------");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "LocalService killed--------");
            startService(new Intent(RemoteService.this, LocalService.class));
            bindService(new Intent(RemoteService.this, LocalService.class),
                    myConn, Context.BIND_IMPORTANT);
        }
    }

}
