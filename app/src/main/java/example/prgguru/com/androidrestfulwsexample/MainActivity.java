package example.prgguru.com.androidrestfulwsexample;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import example.prgguru.com.androidrestfulwsexample.authenticator.AuthenticatorConstant;

public class MainActivity extends AppCompatActivity {

    private AccountManager am;
    private Bundle bnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        am = AccountManager.get(this);

        Button btn = (Button) findViewById(R.id.logoutBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
                addNewAccount(AuthenticatorConstant.ACCOUNTYTPE, AuthenticatorConstant.AUTHTOKEN_TYPE_FULL_ACCESS);
            }
        });
    }

    private void performLogout() {
        Account[] accounts = am.getAccountsByType(AuthenticatorConstant.ACCOUNTYTPE);
        if (accounts.length != 0) {
            for (int i = 0; i < accounts.length; i++) {
                am.clearPassword(accounts[i]);
                am.invalidateAuthToken(AuthenticatorConstant.ACCOUNTYTPE, am.getAuthToken(accounts[i],
                        AuthenticatorConstant.AUTHTOKEN_TYPE_FULL_ACCESS,
                        null,
                        true,
                        new AccountManagerCallback<Bundle>() {
                            @Override
                            public void run(AccountManagerFuture<Bundle> future) {
                                try {
                                    Log.d("invalidateAuthToken", future.getResult().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, null).toString());

                if (Build.VERSION.SDK_INT < 23) { // use deprecated method
                    am.removeAccount(accounts[i], new AccountManagerCallback<Boolean>() {
                        @Override
                        public void run(AccountManagerFuture<Boolean> future) {
                            try {
                                if (future.getResult()) {
                                    Log.d("ACCOUNT REMOVAL", "ACCOUNT  REMOVED");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, null);
                } else {
                    am.removeAccount(accounts[i], this, new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            try {
                                if (future.getResult() != null) {
                                    Log.d("ACCOUNT REMOVAL", "ACCOUNT REMOVED");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, null);
                }
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAuthen();
    }

    /**
     * Add new account to the account manager
     * @param accountType
     * @param authTokenType
     */
    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = am.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Log.d("udinic", "AddNewAccount Bundle is " + bnd);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    private void checkAuthen() {
        AccountManagerFuture<Bundle> future = am.getAuthTokenByFeatures(AuthenticatorConstant.ACCOUNTYTPE,
                AuthenticatorConstant.AUTHTOKEN_TYPE_FULL_ACCESS,
                null,
                this,
                null,
                null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            bnd = future.getResult();
                            Toast.makeText(getBaseContext(), bnd.getString(AccountManager.KEY_ACCOUNT_NAME), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                null
        );
    }
}
