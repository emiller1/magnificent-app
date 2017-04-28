package umbc.edu.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * @author elishiah miller
 * Created 3/17/17
 */
public class GuideBoxService extends Service {

    //TODO: This key should be loaded upon successful login from the online db
    //TODO: We will keep it hard coded for now
    final private String api_key = "8c6513c863495b95018e7ba2aa2ce49360dc418f";
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public GuideBoxService() {
    }

    /**
     * Class used for the client Binder.
     */
    public class LocalBinder extends Binder {
        GuideBoxService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GuideBoxService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //TODO: Implement methods for the REST-API
}
