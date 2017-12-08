package pbl.g12.sem1.mealtolive;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A signup screen where users can signup for a personal account.
 */
public class SignupPersonalActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>
{

	/**
	 * Id to identity READ_CONTACTS permission request.
	 */
	private static final int REQUEST_READ_CONTACTS = 0;

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserSignupTask mAuthTask = null;

	// UI references.
	private AutoCompleteTextView mNameView;
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_personal);
		setupActionBar();
		// Set up the login form.
		mNameView = findViewById(R.id.p_signup_input_name);
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
		mEmailView = findViewById(R.id.p_signup_input_email);
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
		populateAutoComplete();

		mPasswordView = findViewById(R.id.p_signup_input_password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL)
				{
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		Button _signupButton = findViewById(R.id.p_signup_button);
		_signupButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				attemptLogin();
			}
		});

	}

	private void populateAutoComplete()
	{
		if (!mayRequestContacts())
		{
			return;
		}

		getLoaderManager().initLoader(0, null, this);
	}

	private boolean mayRequestContacts()
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
		{
			return true;
		}
		if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
		{
			return true;
		}
		if (shouldShowRequestPermissionRationale(READ_CONTACTS))
		{
			Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
					.setAction(android.R.string.ok, new View.OnClickListener()
					{
						@Override
						@TargetApi(Build.VERSION_CODES.M)
						public void onClick(View v)
						{
							requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
						}
					});
		}
		else
		{
			requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
		}
		return false;
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults)
	{
		if (requestCode == REQUEST_READ_CONTACTS)
		{
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				populateAutoComplete();
			}
		}
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
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptLogin()
	{
		if (mAuthTask != null)
		{
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String displayName = mNameView.getText().toString();
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password))
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
		else if (!isEmailValid(email))
		{
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
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
			// kick off a background task to perform the user signup attempt.
			//TODO: Show progress here
			mAuthTask = new UserSignupTask(displayName, email, password);
			mAuthTask.execute((Void) null);
		}
	}

	private boolean isEmailValid(String email)
	{
		//TODO: Replace this with your own logic
		return email.contains("@");
	}

	private boolean isPasswordValid(String password)
	{
		//TODO: Replace this with your own logic
		return password.length() < 6;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
	{
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE +
						" = ?", new String[]{ContactsContract.CommonDataKinds.Email
				.CONTENT_ITEM_TYPE},

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
	{
		List<String> emails = new ArrayList<>();
		List<String> names = new ArrayList<>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			names.add(cursor.getString(ProfileQuery.DISPLAY_NAME_PRIMARY));
			cursor.moveToNext();
		}

		addEmailsToAutoComplete(emails);
		addNamesToAutoComplete(names);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader)
	{

	}

	private void addEmailsToAutoComplete(List<String> emailAddressCollection)
	{
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		ArrayAdapter<String> adapter =
				new ArrayAdapter<>(SignupPersonalActivity.this,
						android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

		mEmailView.setAdapter(adapter);
	}

	private void addNamesToAutoComplete(List<String> nameCollection)
	{
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		ArrayAdapter<String> adapter =
				new ArrayAdapter<>(SignupPersonalActivity.this,
						android.R.layout.simple_dropdown_item_1line, nameCollection);

		mNameView.setAdapter(adapter);
	}


	private interface ProfileQuery
	{
		String[] PROJECTION = {
				ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int DISPLAY_NAME_PRIMARY = 0;
		int ADDRESS = 1;
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	@SuppressLint("StaticFieldLeak")
	public class UserSignupTask extends AsyncTask<Void, Void, Boolean>
	{
		private final String mName;
		private final String mEmail;
		private final String mPassword;

		UserSignupTask(String displayName, String email, String password)
		{
			mName = displayName;
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected Boolean doInBackground(Void... params)
		{
			// TODO: attempt authentication against a network service.

			try
			{
				// Simulate network access.
				Thread.sleep(2000);
			}
			catch (InterruptedException e)
			{
				return false;
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success)
		{
			mAuthTask = null;
			//TODO: Stop showing progress here

			if (success)
			{
				finish();
			}
			else
			{
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled()
		{
			mAuthTask = null;
			//TODO: Stop showing progress here
		}
	}
}
