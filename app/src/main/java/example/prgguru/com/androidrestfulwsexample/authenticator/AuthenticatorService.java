package example.prgguru.com.androidrestfulwsexample.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by deksen on 4/14/16 AD.
 */
public class AuthenticatorService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Authenticator ac = new Authenticator(this);
        return ac.getIBinder();
    }
}
