package example.prgguru.com.androidrestfulwsexample;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import example.prgguru.com.androidrestfulwsexample.authenticator.AuthenticatorConstant;

/**
 *
 * Login Activity Class
 *
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    // Progress Dialog Object
    ProgressDialog prgDialog;
    // Error Msg TextView Object
    TextView errorMsg;
    // Email Edit View Object
    EditText emailET;
    // Passwprd Edit View Object
    EditText pwdET;

    private AccountManager am;

    private final String TAG = this.getClass().getSimpleName();

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // Find Error Msg Text View control by ID
        errorMsg = (TextView)findViewById(R.id.login_error);
        // Find Email Edit View control by ID
        emailET = (EditText)findViewById(R.id.loginEmail);
        // Find Password Edit View control by ID
        pwdET = (EditText)findViewById(R.id.loginPassword);
        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        am = (AccountManager) getBaseContext().getSystemService(Context.ACCOUNT_SERVICE);

        btn = (Button) findViewById(R.id.btnLogin);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailET.getText().toString();
                // Get Password Edit View Value
                final String password = pwdET.getText().toString();


                AuthenticatorService service = new AuthenticatorServiceImpl();
                service.signIn(email, password, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String str = new String(responseBody);
                        JSONObject obj = null;
                        String authToken = null;
                        Bundle data = new Bundle();

                        try {
                            obj = new JSONObject(str);
                            authToken = obj.getString("authToken");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        data.putString(AccountManager.KEY_ACCOUNT_NAME, email);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, AuthenticatorConstant.ACCOUNTYTPE);
                        data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                        data.putString(PARAM_USER_PASS, password);

                        final Intent res = new Intent();
                        res.putExtras(data);

                        final Account ac = new Account(email, AuthenticatorConstant.ACCOUNTYTPE);

                        boolean isS = am.addAccountExplicitly(ac, password, null);
                        Log.d("tys", ""+ isS);
                        finishLogin(res);
//                        am.setAuthToken(ac, ACCOUNTYTPE, authToken);

//                        setAccountAuthenticatorResult(res.getExtras());
//                        setResult(RESULT_OK, res);




//                        finish();
//                        navigatetoHomeActivity();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        });

        Button regisBtn = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        regisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(getBaseContext(), RegisterActivity.class);
                signup.putExtras(getIntent().getExtras());
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void finishLogin(Intent intent) {
        Log.d("udinic", TAG + "> finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d("udinic", TAG + "> finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = AuthenticatorConstant.AUTHTOKEN_TYPE_FULL_ACCESS;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            am.addAccountExplicitly(account, accountPassword, null);
            am.setAuthToken(account, authtokenType, authtoken);
        } else {
            Log.d("udinic", TAG + "> finishLogin > setPassword");
            am.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Method which navigates from Login Activity to Home Activity
     */
    public void navigatetoHomeActivity(){
        Intent homeIntent = new Intent(getApplicationContext(),MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    /**
     * Method gets triggered when Register button is clicked
     *
     * @param view
     */
    public void navigatetoRegisterActivity(View view){
        Intent loginIntent = new Intent(getApplicationContext(),RegisterActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

}