package pbl.g12.sem1.mealtolive;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class SignupOrganizationActivity extends AppCompatActivity {
    /*
        Objects
     */
    private AutoCompleteTextView orgName;
    private AutoCompleteTextView orgBuissnessAddress;
    private AutoCompleteTextView orgEmail;
    private AutoCompleteTextView orgContactNumber;
    private EditText orgPassword;
    private Button _signUpButton;
    /*
        Firebase
     */
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_organization);
        /*
         * Find Ids Here
         */
        orgName = findViewById(R.id.p_signup_input_name2);
        orgBuissnessAddress = findViewById(R.id.p_signup_input_address);
        orgEmail = findViewById(R.id.p_signup_input_email);
        orgContactNumber = findViewById(R.id.p_signup_input_address2);
        orgPassword = findViewById(R.id.p_signup_input_password);


    }

    private void attemptSignup()
    {
        // Reset errors.
        orgEmail.setError(null);
        orgPassword.setError(null);

        _signUpButton.setEnabled(false);

        // Store values at the time of the login attempt.
        final String displayName = orgName.getText().toString();
        String buisnessAddress = orgBuissnessAddress.getText().toString();
        String email = orgEmail.getText().toString();
        String contactNumber = orgContactNumber.getText().toString();
        String password = orgPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password))
        {
            orgPassword.setError(getString(R.string.error_field_required));
            focusView = orgPassword;
            cancel = true;
        }
        else if (!isPasswordValid(password))
        {
            orgPassword.setError(getString(R.string.error_invalid_password));
            focusView = orgPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email))
        {
            orgEmail.setError(getString(R.string.error_field_required));
            focusView = orgEmail;
            cancel = true;
        }
        else if (!isEmailFormatValid(email))
        {
            orgEmail.setError(getString(R.string.error_invalid_email));
            focusView = orgEmail;
            cancel = true;
        }

        //Check for a valid display name
        if (displayName.isEmpty())
        {
            orgName.setError(getString(R.string.error_field_required));
            focusView = orgName;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            //[START init_progress_dialog]
            final ProgressDialog progressDialog = new ProgressDialog(SignupPersonalActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();
            //[END init_progress_dialog]
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                onSignUpSuccess(displayName);
                            }
                            else
                            {
                                onSignUpFailed();
                            }

                            // [START_EXCLUDE]
                            progressDialog.dismiss();
                            // [END_EXCLUDE]
                        }
                    });
        }
    }

    private boolean isPasswordValid(String password)
    {
        return password.length() >= 6;
    }

    private boolean isEmailFormatValid(String email)
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
