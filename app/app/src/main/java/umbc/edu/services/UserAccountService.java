package umbc.edu.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

import umbc.edu.helpers.AppHelper;


public class UserAccountService extends IntentService {

    String username;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public UserAccountService() {
        super("UserAccountService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra("action");
        switch (action){
            case "register":
                AppHelper.init(getApplicationContext());
                username = intent.getStringExtra("username");
                register(username, intent.getStringExtra("email"), intent.getStringExtra("password"));
                break;
            case "login":
                break;
        }
    }

    private void register (String username, String email, String password){
        // Create a CognitoUserAttributes object and add user attributes
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        // Add the user attributes. Attributes are added as key-value pairs
        userAttributes.addAttribute("preferred_username", username);

        // Adding user's contact info
        userAttributes.addAttribute("email", email);

        AppHelper.getPool().signUpInBackground(username,password,userAttributes,null,signupCallback);

    }

    SignUpHandler signupCallback = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Sign-up was successful

            // Check if this user (cognitoUser) needs to be confirmed
            if(!userConfirmed) {
                // This user must be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user

                //Start an Activity to allow the user to enter the verification code sent to their email
                Intent intent = new Intent();
                intent.putExtra("username", username);
                intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
                intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
                intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
                intent.setAction(AppHelper.USER_NOT_CONFIRMED);
                getBaseContext().sendBroadcast(intent);
            }
            else {
                // The user has already been confirmed
                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra(AppHelper.USER_CONFIRMED,"confirmed");
                broadcastIntent.setAction(AppHelper.USER_CONFIRMED);
                getBaseContext().sendBroadcast(broadcastIntent);
            }
        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-up failed, check exception for the cause
            Log.d("Error","In UserAccountService -> onFailure(): "+exception.getLocalizedMessage());

            //Send the error message back to the CreateAccountActivity to display to user
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra(AppHelper.SIGN_UP_FAILED,exception.getLocalizedMessage());
            broadcastIntent.setAction(AppHelper.SIGN_UP_FAILED);
            getBaseContext().sendBroadcast(broadcastIntent);
        }
    };
}
