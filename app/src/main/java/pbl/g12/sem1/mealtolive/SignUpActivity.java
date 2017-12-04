package pbl.g12.sem1.mealtolive;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignUpActivity extends AppCompatActivity
{
	@InjectView(R.id.input_name)
	EditText _nameText;
	@InjectView(R.id.input_email)
	EditText _emailText;
	@InjectView(R.id.input_password)
	EditText _passwordText;
	@InjectView(R.id.btn_signup)
	Button _signUpButton;
	@InjectView(R.id.link_login)
	TextView _loginLink;
	private FirebaseAuth mAuth;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		ButterKnife.inject(this);
		mAuth = FirebaseAuth.getInstance();

		_signUpButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				signUp();
			}
		});

		_loginLink.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Finish the registration screen and return to the Login activity
				finish();
			}
		});
	}

	public void signUp()
	{
		if (!validate())
		{
			onSignUpFailed();
			return;
		}

		_signUpButton.setEnabled(false);

		final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
				R.style.AppTheme_Dark_Dialog);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Creating Account...");
		progressDialog.show();

		final String name = _nameText.getText().toString();
		final String email = _emailText.getText().toString();
		final String password = _passwordText.getText().toString();

		mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							onSignUpSuccess(name);
						} else
						{
							onSignUpFailed();
						}

						// [START_EXCLUDE]
						progressDialog.dismiss();
						// [END_EXCLUDE]
					}
				});
	}


	public void onSignUpSuccess(String name)
	{
		_signUpButton.setEnabled(true);
		// Sign in success, update UI with the signed-in user's information
		FirebaseUser user = mAuth.getCurrentUser();

		UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
				.setDisplayName(name)
				.build();

		if (user != null)
		{
			user.updateProfile(profileUpdates);
		}
		// TODO: Insert UI Update if sign-up success
		setResult(RESULT_OK, null);
		finish();
	}

	public void onSignUpFailed()
	{
		// If sign in fails, display a message to the user.
		Toast.makeText(SignUpActivity.this, "Signup failed.",
				Toast.LENGTH_SHORT).show();
		// TODO: Insert UI Update if sign-up fails
		_signUpButton.setEnabled(true);
	}

	public boolean validate()
	{
		boolean valid = true;

		String name = _nameText.getText().toString();
		String email = _emailText.getText().toString();
		String password = _passwordText.getText().toString();

		if (name.isEmpty())
		{
			_nameText.setError("cannot be empty");
			valid = false;
		} else
		{
			_nameText.setError(null);
		}

		if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
		{
			_emailText.setError("enter a valid email address");
			valid = false;
		} else
		{
			_emailText.setError(null);
		}

		if (password.isEmpty() || password.length() < 6)
		{
			_passwordText.setError("at least 6 characters");
			valid = false;
		} else
		{
			_passwordText.setError(null);
		}

		return valid;
	}
}