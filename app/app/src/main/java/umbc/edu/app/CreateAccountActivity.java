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
 */
public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "CreateAccountActivity";

    EditText username, email, password;
    Button register;
    IntentFilter intentFilter;
    Intent accountIntent, confirmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate()");
        setContentView(R.layout.activity_create_account);

        username = (EditText) findViewById(R.id.username2_editText);
        email = (EditText) findViewById(R.id.email_editText);
        password = (EditText) findViewById(R.id.password2_editText);

        register = (Button) findViewById(R.id.register_button);
        register.setOnClickListener(this);

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
        unregisterReceiver(intentReceiver);
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
        switch(v.getId()){
            case R.id.register_button:// When createButton is clicked
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String mail = email.getText().toString();
                if(!user.matches("") && !mail.matches("") && !pass.matches("")){
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
            AlertDialogFragment fragment = new AlertDialogFragment();
            switch (action) {
                case AppHelper.SIGN_UP_FAILED:
                    accountIntent.removeExtra("username");
                    accountIntent.removeExtra("password");
                    accountIntent.removeExtra("email");
                    accountIntent.removeExtra("action");
                    stopService(accountIntent);
                    fragment.show(getSupportFragmentManager(), intent.getStringExtra(AppHelper.SIGN_UP_FAILED));
                    break;
                case AppHelper.USER_CONFIRMED:
                    accountIntent.removeExtra("username");
                    accountIntent.removeExtra("password");
                    accountIntent.removeExtra("email");
                    accountIntent.removeExtra("action");
                    stopService(accountIntent);
                    fragment.show(getSupportFragmentManager(), "User already exists");
                    break;
                case AppHelper.USER_NOT_CONFIRMED:
                    Bundle extras = intent.getExtras();
                    confirmIntent = new Intent(CreateAccountActivity.this, ConfirmAccountActivity.class);
                    confirmIntent.putExtra("username", extras.getString("username"));
                    confirmIntent.putExtra("destination", extras.getString("destination"));
                    confirmIntent.putExtra("deliveryMed", extras.getString("deliveryMed"));
                    confirmIntent.putExtra("attribute", extras.getString("attribute"));
                    startActivity(confirmIntent);
                    break;
            }
        }
    };
}
