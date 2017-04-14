package umbc.edu.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;

import umbc.edu.helpers.AppHelper;

public class ConfirmAccountActivity extends AppCompatActivity implements View.OnClickListener {

    //http://docs.aws.amazon.com/cognito/latest/developerguide/tutorial-integrating-user-pools-android.html
    //https://github.com/awslabs/aws-sdk-android-samples/blob/8957e9402cf7490bfa9c3939eabc92f1b7d4572e/AmazonCognitoYourUserPoolsDemo/app/src/main/java/com/amazonaws/youruserpools/RegisterUser.java

    String username, destination, deliveryMed;
    TextView confimration_message, resend;
    Button confirm_button;
    EditText code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_account);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        destination = extras.getString("destination");
        deliveryMed = extras.getString("deliveryMed");

        confimration_message = (TextView)findViewById(R.id.confirm_message);
        confirm_button = (Button)findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(this);
        code = (EditText) findViewById(R.id.editText_code);
        resend = (TextView) findViewById(R.id.resend);
        resend.setOnClickListener(this);
        // AppHelper.getPool().getUser(userName).resendConfirmationCodeInBackground(resendConfCodeHandler);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (destination != null && deliveryMed != null) {
            confimration_message.setText("A confirmation code was sent to " + destination + " via " + deliveryMed);
        }
        else{
            String msg = "Please Enter Your Verification Code";
            confimration_message.setText(msg);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.confirm_button:
                String code_ = code.getText().toString();
                if(!code_.matches("")){
                    Toast.makeText(this, username,Toast.LENGTH_LONG).show();
                    AppHelper.getPool().getUser(username).confirmSignUpInBackground(code_, true, confHandler);
                    Log.d("confirm_button","***WHAT***");
                }
                break;
            case R.id.resend:
                AppHelper.getPool().getUser(username).resendConfirmationCodeInBackground(resendConfCodeHandler);
                break;
        }

    }

    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            AlertDialogFragment fragment = new AlertDialogFragment();
            fragment.show(getSupportFragmentManager(), "ConfirmAccountActivity Success");

            Log.d("onSuccess","***Success***");
        }

        @Override
        public void onFailure(Exception exception) {
            AlertDialogFragment fragment = new AlertDialogFragment();
            fragment.show(getSupportFragmentManager(), "ConfirmAccountActivity Failure");

            Log.d("ERROR","***FAILURE***"+exception.getLocalizedMessage());
        }
    };

    VerificationHandler resendConfCodeHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {

        }

        @Override
        public void onFailure(Exception exception) {

        }
    };
}