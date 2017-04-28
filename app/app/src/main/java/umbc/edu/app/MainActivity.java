package umbc.edu.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
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

import com.amazonaws.regions.Regions;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.HashMap;
import java.util.Map;

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
    Button login_button;
    TextView createAccount_button, forgotPassword;
    CallbackManager callbackManager;
    private ForgotPasswordContinuation forgotPasswordContinuation;
    protected static CognitoCachingCredentialsProvider credentialsProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.facebooklogin_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("****YEA***",loginResult.toString());
                Map<String, String> logins = new HashMap<String, String>();
                logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
                credentialsProvider = new CognitoCachingCredentialsProvider(getBaseContext(), AppHelper.getPoolId(),
                        Regions.US_EAST_1);
                credentialsProvider.setLogins(logins);
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("****YEA***",error.toString());
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Create EditText
        username = (EditText) findViewById(R.id.username_editText);
        password = (EditText) findViewById(R.id.password_editText);

        //Create Buttons
        login_button = (Button) findViewById(R.id.signIn_button);

        //Create TextViews
        createAccount_button = (TextView) findViewById(R.id.createAccount_button);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword_textView);

        //Set onClick listeners
        login_button.setOnClickListener(this);
        createAccount_button.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
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
            case R.id.forgotPassword_textView:
                //fragment.show(getSupportFragmentManager(), "Forgot password");
                if (!user.matches("")){
                    AppHelper.init(getApplicationContext());
                    AppHelper.getPool().getUser(user).forgotPasswordInBackground(forgotPasswordHandler);
                }else{
                    Toast.makeText(this, "Please Enter Your Username", Toast.LENGTH_LONG).show();
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
            forgotPasswordContinuation = continuation;

            Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
            intent.putExtra("destination",destination);
            intent.putExtra("deliveryMed",deliveryMed);
            startActivityForResult(intent, 1);

            //----------------------------------------------------------------------------------
            // These are just notes: Actions are taken place in onActivityResult Method
            //----------------------------------------------------------------------------------
            // Code to get the code from the user - user dialogs etc.
            // If the program control has to exit this method, take the "continuation" object.
            // "continuation" is the only possible way to continue with the process
            // When the code is available
            // Set the new password
            //continuation.setPassword("PASSWORD");
            // Set the code to verify
            //continuation.setVerificationCode("CODE#");/
            // Let the forgot password process proceed
            //continuation.continueTask();
        }

        @Override
        public void onFailure(Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            Toast.makeText(MainActivity.this,"FAILURE FOR GOTPASSWORD", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(requestCode==RESULT_OK){
                String password = data.getExtras().getString("password");
                String code = data.getExtras().getString("code");
                forgotPasswordContinuation.setPassword(password);
                forgotPasswordContinuation.setVerificationCode(code);
                forgotPasswordContinuation.continueTask();
            }
        }else{
            Log.d("FaceBook Result", String.valueOf(requestCode));
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}

