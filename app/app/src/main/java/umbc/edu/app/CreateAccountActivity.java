package umbc.edu.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import umbc.edu.helpers.AppHelper;
import umbc.edu.services.UserAccountService;

/**
 * @author elishiah miller
 * Created 3/17/17
 *
 * Notes: This Activity should use the UserAccount service. It should not bind to the service it
 * should only start the service using startService(). The Activity should create a pending
 * intent and send it to the service, in which the service should send infomration back to
 * the Activity using the sent pending intent
 */
public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{

    protected String tag = "CreateAccountActivity";
    EditText username, email, password;
    Button register_button;
    String username_, email_, password_;
    IntentFilter intentFilter;
    Intent userAccount_intent, confirmAccount_intnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(tag, "onCreate()");
        setContentView(R.layout.activity_create_account);

        username = (EditText)findViewById(R.id.username_create);
        password = (EditText)findViewById(R.id.password_create);
        email = (EditText)findViewById(R.id.email_create);
        register_button = (Button)findViewById(R.id.register_button);
        register_button.setOnClickListener(this);

        //Listen For a broadcast to check if the user registration failed
        intentFilter = new IntentFilter();
        intentFilter.addAction(AppHelper.SIGN_UP_FAILED);
        intentFilter.addAction(AppHelper.USER_CONFIRMED);
        intentFilter.addAction(AppHelper.USER_NOT_CONFIRMED);
        registerReceiver(intentReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(tag, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(tag, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(tag, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(tag, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(tag, "onDestroy()");
        unregisterReceiver(intentReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_button:
                username_ = username.getText().toString();
                email_ = email.getText().toString();
                password_ = password.getText().toString();

                if(!username_.matches("") && !email_.matches("") && !password_.matches("")){
                    confirmAccount_intnet = new Intent(this, ConfirmAccountActivity.class);

                    // Start Service. The service will send a broadcast.
                    userAccount_intent = new Intent(this, UserAccountService.class);
                    userAccount_intent.putExtra("username", username_);
                    userAccount_intent.putExtra("email", email_);
                    userAccount_intent.putExtra("password", password_);
                    userAccount_intent.putExtra("action", "register");
                    startService(userAccount_intent); //This is an intent service so no need to call stop service method
                }else{
                    Toast.makeText(this, "Please Enter All Infomration", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           String action =  intent.getAction();
            switch (action){
                case AppHelper.SIGN_UP_FAILED:
                    //TODO Show Error Message In Some Sort of Dialog Box and highlight the error.
                    //TODO For now I just toast the message
                    Toast.makeText(getBaseContext(), intent.getStringExtra(AppHelper.SIGN_UP_FAILED), Toast.LENGTH_LONG).show();
                    userAccount_intent.removeExtra("username");
                    userAccount_intent.removeExtra("email");
                    userAccount_intent.removeExtra("passowrd");
                    userAccount_intent.removeExtra("action");
                    stopService(userAccount_intent);
                    break;
                case AppHelper.USER_CONFIRMED:
                    //TODO Show user is already confimred in some sort of dialog box.
                    //TODO For now I just do a toast message
                    Toast.makeText(getBaseContext(), "User is already confirmed", Toast.LENGTH_LONG).show();
                    break;
                case AppHelper.USER_NOT_CONFIRMED:
                    Bundle extras = intent.getExtras();
                    confirmAccount_intnet.putExtra("username", extras.getString("username"));
                    confirmAccount_intnet.putExtra("destination", extras.getString("destination"));
                    confirmAccount_intnet.putExtra("deliveryMed", extras.getString("deliveryMed"));
                    confirmAccount_intnet.putExtra("attribute", extras.getString("attribute"));
                    startActivity(confirmAccount_intnet);
                    break;
            }
        }
    };
}
