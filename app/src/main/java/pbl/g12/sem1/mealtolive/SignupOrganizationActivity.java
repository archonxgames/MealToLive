package pbl.g12.sem1.mealtolive;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupOrganizationActivity extends AppCompatActivity
{
	/**
	 * Id to identity RC_LOGIN intent request code.
	 */
	private static final int RC_LOGIN = 1;

	private FirebaseAuth mAuth;
	private DatabaseReference mDatabase;

	//UI References
	private EditText mNameView;
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mAddressView;
	private EditText mContactNoView;
	private Button _signupButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_organization);
		setupActionBar();
		// Set up the signup form.
		mNameView = findViewById(R.id.o_signup_input_name);
		mNameView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL)
				{
					mEmailView.requestFocus();
					return true;
				}
				return false;
			}
		});

		mEmailView = findViewById(R.id.o_signup_input_email);
		mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL)
				{
					mPasswordView.requestFocus();
					return true;
				}
				return false;
			}
		});

		mPasswordView = findViewById(R.id.o_signup_input_password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL)
				{
					mAddressView.requestFocus();
					return true;
				}
				return false;
			}
		});

		mAddressView = findViewById(R.id.o_signup_input_address);
		mAddressView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL)
				{
					mContactNoView.requestFocus();
					return true;
				}
				return false;
			}
		});

		mContactNoView = findViewById(R.id.o_signup_input_contactNo);
		mContactNoView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL)
				{
					attemptSignup();
					return true;
				}
				return false;
			}
		});

		_signupButton = findViewById(R.id.p_signup_button);
		_signupButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				attemptSignup();
			}
		});

		TextView _loginLink = findViewById(R.id.link_login);
		_loginLink.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Finish the registration screen and return to the Login activity
				setResult(RC_LOGIN, null);
				finish();
			}
		});

		mDatabase = FirebaseDatabase.getInstance().getReference();
		mAuth = FirebaseAuth.getInstance();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar()
	{
		Toolbar toolbar = findViewById(R.id.nav_toolbar);
		setSupportActionBar(toolbar);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Attempts to sign in or register the organization account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptSignup()
	{
		// Reset errors.
		mNameView.setError(null);
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mAddressView.setError(null);
		mContactNoView.setError(null);

		_signupButton.setEnabled(false);

		// Store values at the time of the login attempt.
		final String displayName = mNameView.getText().toString();
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();
		final String address = mAddressView.getText().toString();
		final String contactNo = mContactNoView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		//Check for a valid contact number
		if (TextUtils.isEmpty(contactNo))
		{
			mContactNoView.setError(getString(R.string.error_field_required));
			focusView = mContactNoView;
			cancel = true;
		}
		//Check for a valid work address
		if (TextUtils.isEmpty(address))
		{
			mAddressView.setError(getString(R.string.error_field_required));
			focusView = mAddressView;
			cancel = true;
		}

		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password))
		{
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		else if (!isPasswordValid(password))
		{
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email))
		{
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}
		else if (!isEmailFormatValid(email))
		{
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		//Check for a valid display name
		if (displayName.isEmpty())
		{
			mNameView.setError(getString(R.string.error_field_required));
			focusView = mNameView;
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
			final ProgressDialog progressDialog = new ProgressDialog(SignupOrganizationActivity.this,
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
								onSignUpSuccess(displayName, address, contactNo);
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

	private void onSignUpFailed()
	{
		// If sign in fails, display a message to the user.
		Toast.makeText(SignupOrganizationActivity.this, "Signup failed.",
				Toast.LENGTH_SHORT).show();
		_signupButton.setEnabled(true);
	}

	private void onSignUpSuccess(String displayName, String address, String contactNo)
	{
		_signupButton.setEnabled(true);
		// Sign in success, update database with the signed-in user's information
		final FirebaseUser user = mAuth.getCurrentUser();
		if (user != null)
		{
			final String userUid = user.getUid();

			//Fill in database values
			UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
					.setDisplayName(displayName)
					.build();

			mDatabase.child("Users").child("Organizations").child(userUid).child("Email").setValue(user.getEmail());
			mDatabase.child("Users").child("Organizations").child(userUid).child("Address").setValue(address);
			mDatabase.child("Users").child("Organizations").child(userUid).child("ContactNo").setValue(contactNo);

			user.updateProfile(profileUpdates);
			user.sendEmailVerification();
		}
		setResult(RESULT_OK, null);
		finish();
	}

	private boolean isEmailFormatValid(String email)
	{
		return Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	private boolean isPasswordValid(String password)
	{
		return password.length() >= 6;
	}
}
