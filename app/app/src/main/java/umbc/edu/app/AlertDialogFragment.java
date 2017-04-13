package umbc.edu.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import umbc.edu.helpers.AppHelper;

/**
 * Created by Katelyn Seitz on 4/12/2017.
 * This is used to create a Dialog in another activity by calling the
 * fragment.show(getSupportFragmentManager(), SOME_UNIQUE_STRING) method.
 * If anyone wishes to add their own Dialog, they can do it here
 */

public class AlertDialogFragment extends DialogFragment{

    static AlertDialogFragment newInstance(String u) {
        AlertDialogFragment f = new AlertDialogFragment();

        Bundle args = new Bundle();
        args.putString("username", u);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(getTag().contains("User is not confirmed")) {
            builder.setTitle("Unconfirmed User");
            builder.setMessage("Please reenter your email address to return to verification screen");

            //set EditText for email in dialog
            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input);

            builder.setPositiveButton("Resend", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent resend = new Intent(getActivity(), ConfirmAccountActivity.class);
                    resend.putExtra("username", getArguments().getString("username"));
                    resend.putExtra("deliveryMed", "email");
                    startActivity(resend);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        else if (getTag().contains("User does not exist")) {
            builder.setTitle("Invalid Username");
            builder.setMessage("Try a different username or create a new account");
        }
        else if (getTag().contains("Incorrect username or password")) {
            builder.setTitle("Invalid Password");
            builder.setMessage("Please try again");
        }
        else if (getTag().contains("ConfirmAccountActivity Success")) {
            builder.setTitle("Success");
            builder.setMessage("Your account has been confirmed");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("success ", AppHelper.USER_CONFIRMED);
                    startActivity(intent);
                }
            });
        }
        else if (getTag().contains("ConfirmAccountActivity Failure")) {
            builder.setTitle("Error");
            builder.setMessage("Try resending the verification code");
        }
        else if (getTag().contains("Value at \'username\' failed to satisfy constraint")) {
            builder.setTitle("Invalid Username");
            builder.setMessage("Username cannot contain spaces");
        }
        else if (getTag().contains("Invalid email address format")) {
            builder.setTitle("Invalid Email");
            builder.setMessage("Use format\nemail@example.com");
        }
        else if (getTag().contains("Value at \'password\' failed to satisfy constraint: Member must have length greater than or equal to 6")) {
            builder.setTitle("Invalid Password");
            builder.setMessage("Password must contain at least 6 characters");
        }
        else if (getTag().contains("Password must have lowercase characters")) {
            builder.setTitle("Invalid Password");
            builder.setMessage("Password must contain at least 1 lowercase character");
        }
        else if (getTag().contains("Password must have uppercase characters")) {
            builder.setTitle("Invalid Password");
            builder.setMessage("Password must contain at least 1 uppercase character");
        }
        else if (getTag().contains("Password must have numeric characters")) {
            builder.setTitle("Invalid Password");
            builder.setMessage("Password must contain at least 1 number");
        }
        else if (getTag().contains("Password must have symbol characters")) {
            builder.setTitle("Invalid Password");
            builder.setMessage("Password must contain at least 1 special character");
        }
        else if (getTag().contains("Value at \'password\' failed to satisfy constraint: Member must satisfy regular expression pattern")) {
            builder.setTitle("Invalid Password");
            builder.setMessage("Password cannot contain spaces");
        }
        else if (getTag().contains("User already exists")) {
            builder.setTitle("Invalid Username");
            builder.setMessage("Username already exists. Try a different one");
        }
        else {
            builder.setTitle("Unclassified Error");
            builder.setMessage(getTag());
        }
        return builder.create();
    }
}
