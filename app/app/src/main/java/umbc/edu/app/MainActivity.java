package umbc.edu.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;

import umbc.edu.helpers.AppHelper;

/**
 * @author Elishiah Miller, Katelyn Seitz
 * Created 3/17/17
 *
 * This is the login screen and the main activity that gets called first.
 */
public class    MainActivity extends AppCompatActivity implements View.OnClickListener{

    String TAG = "MainActivity";
    String user, pass;

    EditText username, password;
    Button login_button, facebook_button;
    TextView createAccount_button, forgotUsername, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Create EditText
        username = (EditText) findViewById(R.id.username_editText);
        password = (EditText) findViewById(R.id.password_editText);

        //Create Buttons
        login_button = (Button) findViewById(R.id.signIn_button);
        facebook_button = (Button) findViewById(R.id.facebook_button);

        //Create TextViews
        createAccount_button = (TextView) findViewById(R.id.createAccount_button);
        forgotUsername = (TextView) findViewById(R.id.textView7);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword_textView);

        //Set onClick listeners
        login_button.setOnClickListener(this);
        createAccount_button.setOnClickListener(this);
        forgotUsername.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        facebook_button.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG, "onStop()");
    }

    @Override
    public void onClick(View v) {
        user = username.getText().toString();
        pass = password.getText().toString();
        AlertDialogFragment fragment = new AlertDialogFragment();
        switch(v.getId()){
            case R.id.createAccount_button:// When createButton is clicked
                Intent createAccountIntent = new Intent(this, CreateAccountActivity.class);
                createAccountIntent.putExtra("user", user);
                createAccountIntent.putExtra("pass", pass);
                startActivity(createAccountIntent);
                break;
            case R.id.signIn_button:// When loginButton is clicked
                if (!user.matches("") && !pass.matches("")) {
                    AppHelper.init(getApplicationContext());
                    AppHelper.getPool().getUser(user).getSessionInBackground(authenticationHandler);
                }
                break;
            case R.id.textView7:
                //TODO: implement for forgotUsername
                fragment.show(getSupportFragmentManager(), "Forgot username");
                break;
            case R.id.forgotPassword_textView:
                //fragment.show(getSupportFragmentManager(), "Forgot password");
                if (!user.matches("")){
                    AppHelper.getPool().getUser(user).forgotPasswordInBackground(forgotPasswordHandler);
                }
                break;
            case R.id.facebook_button:
                //TODO: implement for facebook
                fragment.show(getSupportFragmentManager(), "Login with Facebook");
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
            Log.w(TAG, "getAuthenticationDetails()");
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(user, pass, null);

            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);

            // Allow the sign-in to continue
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            Log.w(TAG, "getMFACode()");
            //Toast.makeText(MainActivity.this, "GET MFA CODE CALLED", Toast.LENGTH_LONG).show();
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation challengeContinuation) {
            Log.w(TAG, "authenticationChallenge()");
        }

        @Override
        public void onFailure(Exception e) {
            Log.w(TAG, "authenticationChallenge()");
            AlertDialogFragment fragment = new AlertDialogFragment().newInstance(user);
            fragment.show(getSupportFragmentManager(), e.getLocalizedMessage());
        }
    };

    // Callbacks
    ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void getResetCode(ForgotPasswordContinuation continuation) {
            // This will indicate where the code was sent
            CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails = continuation.getParameters();
            String destination = cognitoUserCodeDeliveryDetails.getDestination();
            String deliveryMed = cognitoUserCodeDeliveryDetails.getDeliveryMedium();

            // Code to get the code from the user - user dialogs etc.

            // If the program control has to exit this method, take the "continuation" object.
            // "continuation" is the only possible way to continue with the process

            //TODO GET THE USER'S NEW PASSWORD

            // When the code is available

            // Set the new password
            continuation.setPassword("PASSWORD");//TODO CHANGE THIS TO THE USER ENTERED PASSWORD

            // Set the code to verify
            continuation.setVerificationCode("CODE#");//TODO CHANGE THIS TO THE CODE THE USER RECEIVED

            // Let the forgot password process proceed
            continuation.continueTask();
        }

        @Override
        public void onFailure(Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    };
}

