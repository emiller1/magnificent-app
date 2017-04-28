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

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    String destination, deliveryMed;
    TextView confimration_message;
    Button reset_button;
    EditText password, code;
    String TAG = "ResetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Bundle extras = getIntent().getExtras();
        destination = extras.getString("destination");
        deliveryMed = extras.getString("deliveryMed");

        confimration_message = (TextView)findViewById(R.id.textView9);
        password = (EditText)findViewById(R.id.password2_editText);
        code = (EditText)findViewById(R.id.verificationCode2_editText);

        reset_button = (Button)findViewById(R.id.resetPassword_button);
        reset_button.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "onResume()");
        if (destination != null && deliveryMed != null) {
            confimration_message.setText("A confirmation code was sent to " + destination + " via " + deliveryMed);
        }
        else{
            String msg = "Please Enter Your Verification Code";
            confimration_message.setText(msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetPassword_button:
                String p = password.getText().toString();
                String c = code.getText().toString();
                if (!p.matches("") && !c.matches("")) {
                    Intent intent = new Intent();
                    intent.putExtra("password", p);
                    intent.putExtra("code", c);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                //TODO: make errors look nice and more helpful if possible (FAILURE FOR GOTPASSWORD => username does not exist)
                Toast.makeText(this, "Password has been reset", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
