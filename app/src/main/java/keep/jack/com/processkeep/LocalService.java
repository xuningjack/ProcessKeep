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
 * 本地主进程绑定的Service
 * Created by xuning on 17/1/18.
 */
public class LocalService extends Service {

    private LocalBinder binder;
    private LocalConn conn;
    private final String TAG = "LocalService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new LocalBinder();
        if(conn == null){
            conn = new LocalConn();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        //绑定远程服务
        bindService(new Intent(LocalService.this, RemoteService.class),
                conn,
                Context.BIND_IMPORTANT);
    }

    class LocalBinder extends ProcessService.Stub{
        @Override
        public String getServiceName() throws RemoteException {
            return "LocalService";
        }
    }


    /**
     * 绑定连接需要ServiceConnection
     */
    class LocalConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "Local连接远程服务成功 --------");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //远程服务被干掉；连接断掉的时候走此回调
            //在连接RemoateService异常断时，会回调；也就是RemoteException
            Log.e(TAG, "RemoteService killed--------");
            startService(new Intent(LocalService.this, RemoteService.class));
            //绑定远程服务
            bindService(new Intent(LocalService.this, RemoteService.class),
                    conn, Context.BIND_IMPORTANT);
        }
    }
}
