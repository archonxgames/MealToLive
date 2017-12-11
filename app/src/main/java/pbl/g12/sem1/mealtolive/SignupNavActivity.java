package pbl.g12.sem1.mealtolive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SignupNavActivity extends AppCompatActivity
{

	private static final int RC_LOGIN = 1;
	private int REQUEST_SIGNUP = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_nav);

		Button _personalSignupButton = findViewById(R.id.p_nav_signup_button);
		_personalSignupButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(getApplicationContext(), SignupPersonalActivity.class);
				startActivityForResult(intent, REQUEST_SIGNUP);
			}
		});

		Button _organizationSignupButton = findViewById(R.id.o_nav_signup_button);
		_organizationSignupButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(getApplicationContext(), SignupOrganizationActivity.class);
				startActivityForResult(intent, REQUEST_SIGNUP);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_SIGNUP)
		{
			if (resultCode == RESULT_OK || resultCode == RC_LOGIN)
			{
				finish();
			}
		}
	}
}
