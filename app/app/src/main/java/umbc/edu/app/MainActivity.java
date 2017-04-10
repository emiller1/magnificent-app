package umbc.edu.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

import umbc.edu.helpers.AppHelper;
import umbc.edu.services.UserAccountService;

/**
 * @author Elishiah Miller, Katelyn Seitz
 * Created 3/17/17
 *
 * This is the login screen and the main activity that gets called first.
 *
 * Notes: This Activity should use the UserAccount service. It should not bind to the service it
 * should only start the service using startService(). The Activity should create a pending
 * intent and send it to the service, in which the service should send infomration back to
 * the Activity using the sent pending intent
 */
public class    MainActivity extends AppCompatActivity implements View.OnClickListener{

    String TAG = "MainActivity.class";
    protected String tag = "MainActivity";
    String user, pass;

    EditText username, password;
    Button loginButton, createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(tag, "onCreate()");
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        loginButton = (Button) findViewById(R.id.button2);
        createButton = (Button) findViewById(R.id.button);

        loginButton.setOnClickListener(this);
        createButton.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(tag, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(tag, "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(tag, "onDestroy()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(tag, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(tag, "onStop()");
    }

    @Override
    public void onClick(View v) {
        user = username.getText().toString();
        pass = password.getText().toString();
        switch(v.getId()){
            case R.id.button:       // When createButton is clicked
                Intent createAccountIntent = new Intent(this, CreateAccountActivity.class);
                createAccountIntent.putExtra("user", user);
                createAccountIntent.putExtra("pass", pass);
                startActivity(createAccountIntent);
                break;
            case R.id.button2:      // When loginButton is clicked
                if (!user.matches("") && !pass.matches("")) {
                    AppHelper.init(getApplicationContext());
                    AppHelper.getPool().getUser(user).getSessionInBackground(authenticationHandler);
                }
                break;
        }
    }
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice cognitoDevice) {
            Log.d(TAG, "Auth Success");
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(cognitoDevice);
            //Go To Home Activity
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(user, pass, null);

            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);

            // Allow the sign-in to continue
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            Toast.makeText(MainActivity.this, "GET MFA CODE CALLED", Toast.LENGTH_LONG).show();
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation challengeContinuation) {

        }

        @Override
        public void onFailure(Exception e) {
            Toast.makeText(MainActivity.this, "Failed" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    };
}

