package example.prgguru.com.androidrestfulwsexample.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import example.prgguru.com.androidrestfulwsexample.AuthenticatorService;
import example.prgguru.com.androidrestfulwsexample.AuthenticatorServiceImpl;
import example.prgguru.com.androidrestfulwsexample.LoginActivity;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

/**
 * Created by deksen on 4/9/16 AD.
 */
public class Authenticator extends AbstractAccountAuthenticator {

    private Context context;
    private AuthenticatorService service = new AuthenticatorServiceImpl();



    public Authenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(final AccountAuthenticatorResponse response, final Account account, final String authTokenType, Bundle options) throws NetworkErrorException {
        final Bundle result = new Bundle();

        AccountManager am = AccountManager.get(context);
        String authToken = am.peekAuthToken(account, authTokenType);
        if (TextUtils.isEmpty(authToken)) {

            String password = am.getPassword(account);
            if (password != null) {
                service.signIn(account.name, password, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String str = new String(responseBody);
                        JSONObject obj = null;
                        String authToken = null;
                        try {
                            obj = new JSONObject(str);
                            authToken = obj.getString("authToken");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        result.putString( AccountManager.KEY_ACCOUNT_NAME, account.name );
                        result.putString( AccountManager.KEY_ACCOUNT_TYPE, account.type );
                        result.putString( AccountManager.KEY_AUTHTOKEN, authToken );
                        response.onResult( result );
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        result.putInt( AccountManager.KEY_ERROR_CODE, statusCode );
                        result.putString( AccountManager.KEY_ERROR_MESSAGE, error.getMessage() );
                        response.onError( statusCode, error.getMessage() );
                    }
                });
            }

            return null;
        }

        // Otherwise, the key is valid, it returns.
//        result.putString( AccountManager.KEY_ACCOUNT_NAME, account.name );
//        result.putString( AccountManager.KEY_ACCOUNT_TYPE, account.type );
        result.putString( AccountManager.KEY_AUTHTOKEN, authToken );
        result.putString(LoginActivity.ARG_ACCOUNT_TYPE, account.type);
        result.putString(LoginActivity.ARG_AUTH_TYPE, authTokenType);
        result.putString(LoginActivity.ARG_ACCOUNT_NAME, account.name);
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {

        if (AuthenticatorConstant.AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
            return AuthenticatorConstant.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
        else if (AuthenticatorConstant.AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
            return AuthenticatorConstant.AUTHTOKEN_TYPE_READ_ONLY_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
