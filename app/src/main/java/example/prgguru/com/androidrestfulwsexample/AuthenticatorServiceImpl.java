package example.prgguru.com.androidrestfulwsexample;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by deksen on 4/9/16 AD.
 */
public class AuthenticatorServiceImpl implements AuthenticatorService {



    @Override
    public void signIn(String username, String password, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        // Put Http parameter username with value of Email Edit View control
        params.put("username", username);
        // Put Http parameter password with value of Password Edit Value control
        params.put("password", password);
        // Invoke RESTful Web Service with Http parameters
        invokeWS("login/dologin", params, handler);
    }

    @Override
    public void signUp(String name, String username, String password, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("name", name);
        // Put Http parameter username with value of Email Edit View control
        params.put("username", username);
        // Put Http parameter password with value of Password Edit View control
        params.put("password", password);
        // Invoke RESTful Web Service with Http parameters
        invokeWS("register/doregister", params, handler);
    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(String service, RequestParams params, AsyncHttpResponseHandler handler){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://10.0.3.2:8080/useraccount/" + service, params ,handler);
    }
}
