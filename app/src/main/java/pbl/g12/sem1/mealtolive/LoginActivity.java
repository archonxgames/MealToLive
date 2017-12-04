package pbl.g12.sem1.mealtolive;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity
{
	private static final String TAG = "LoginActivity";
	private static final int REQUEST_SIGNUP = 0;
	@InjectView(R.id.input_email)
	EditText _emailText;
	@InjectView(R.id.input_password)
	EditText _passwordText;
	@InjectView(R.id.btn_login)
	Button _loginButton;
	@InjectView(R.id.link_signup)
	TextView _signupLink;
	private FirebaseAuth mAuth;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.inject(this);

		// Configure Google Sign In
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.mealtolive_web_client_id))
				.requestEmail()
				.build();

		mAuth = FirebaseAuth.getInstance();

		_loginButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				login();
			}
		});

		_signupLink.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// Start the Sign-up activity
				Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
				startActivityForResult(intent, REQUEST_SIGNUP);
			}
		});
	}

	public void login()
	{
		if (!validate())
		{
			onLoginFailed();
			return;
		}

		_loginButton.setEnabled(false);

		final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Authenticating...");
		progressDialog.show();

		String email = _emailText.getText().toString();
		String password = _passwordText.getText().toString();

		mAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							onLoginSuccess();
							// Sign in success, update UI with the signed-in user's information
							// TODO: Insert UI Update if login success
						} else
						{
							// If sign in fails, display a message to the user.
							onLoginFailed();
							// TODO: Insert UI Update if login fails
						}

						// [START_EXCLUDE]
						progressDialog.dismiss();
						// [END_EXCLUDE]
					}
				});
	}

	@Override
	public void onBackPressed()
	{
		// disable going back to the MainActivity
		moveTaskToBack(true);
	}

	private void onLoginSuccess()
	{
		setResult(REQUEST_SIGNUP,getIntent());
		finish();
	}

	private void onLoginFailed()
	{
		Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
		_loginButton.setEnabled(true);
	}

	public boolean validate()
	{
		boolean valid = true;

		String email = _emailText.getText().toString();
		String password = _passwordText.getText().toString();

		if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
		{
			_emailText.setError("enter a valid email address");
			valid = false;
		} else
			_emailText.setError(null);

		if (password.isEmpty())
		{
			_passwordText.setError("password is incorrect");
			valid = false;
		} else
			_passwordText.setError(null);

		return valid;
	}

	private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
	{
		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							onLoginSuccess();
						}
						else
						{
							onLoginFailed();
						}
					}
				});
	}

}

