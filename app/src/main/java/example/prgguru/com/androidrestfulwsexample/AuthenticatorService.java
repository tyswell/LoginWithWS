package example.prgguru.com.androidrestfulwsexample;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by deksen on 4/9/16 AD.
 */
public interface AuthenticatorService {

    public void signIn(String username, String password, AsyncHttpResponseHandler handler);
    public void signUp(String name, String username, String password, AsyncHttpResponseHandler handler);

}
