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
public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    protected String tag = "CreateAccountActivity";

    EditText username, email, password;
    Button register;
    IntentFilter intentFilter;
    Intent accountIntent, confirmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(tag, "onCreate()");
        setContentView(R.layout.activity_create_account);

        username = (EditText) findViewById(R.id.editText3);
        email = (EditText) findViewById(R.id.editText4);
        password = (EditText) findViewById(R.id.editText5);
        register = (Button) findViewById(R.id.button3);
        register.setOnClickListener(this);    //TODO: this is wrong

        // Allows user to carry over attempted login credentials for use in account registration
        Intent createIntent = getIntent();
        username.setText(createIntent.getStringExtra("user"));
        password.setText(createIntent.getStringExtra("pass"));

        intentFilter = new IntentFilter();
        intentFilter.addAction(AppHelper.SIGN_UP_FAILED);
        intentFilter.addAction(AppHelper.USER_CONFIRMED);
        intentFilter.addAction(AppHelper.USER_NOT_CONFIRMED);
        registerReceiver(intentReceiver, intentFilter);
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
        unregisterReceiver(intentReceiver);
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
        switch(v.getId()){
            case R.id.button3:       // When createButton is clicked
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String mail = email.getText().toString();
                if(!user.matches("") && !mail.matches("") && !pass.matches("")){
                    confirmIntent = new Intent(this, ConfirmAccountActivity.class);
                    accountIntent = new Intent(this, UserAccountService.class);
                    accountIntent.putExtra("username", user);
                    accountIntent.putExtra("password", pass);
                    accountIntent.putExtra("email", mail);
                    accountIntent.putExtra("action", "register");
                    startService(accountIntent);
                }
                else {
                    Toast.makeText(this, "Please Enter All Information", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case AppHelper.SIGN_UP_FAILED:
                    //TODO: Show exception handler in dialog box in easy to read format rather than toasting error message
                    Toast.makeText(getBaseContext(), intent.getStringExtra(AppHelper.SIGN_UP_FAILED), Toast.LENGTH_LONG).show();

                    accountIntent.removeExtra("username");
                    accountIntent.removeExtra("password");
                    accountIntent.removeExtra("email");
                    accountIntent.removeExtra("action");
                    stopService(accountIntent);
                    break;
                case AppHelper.USER_CONFIRMED:
                    //TODO: use dialog box instead of toast message
                    Toast.makeText(getBaseContext(), "This user already exists", Toast.LENGTH_LONG).show();
                    break;
                case AppHelper.USER_NOT_CONFIRMED:
                    Bundle extras = intent.getExtras();
                    confirmIntent.putExtra("username", extras.getString("username"));
                    confirmIntent.putExtra("destination", extras.getString("destination"));
                    confirmIntent.putExtra("deliveryMed", extras.getString("deliveryMed"));
                    confirmIntent.putExtra("attribute", extras.getString("attribute"));
                    Toast.makeText(getBaseContext(), "ConfirmAccountActivity not booting", Toast.LENGTH_LONG).show();
                    //startActivity(confirmIntent);
                    break;
            }
        }
    };
}
