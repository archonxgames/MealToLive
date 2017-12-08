package pbl.g12.sem1.mealtolive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignupNavActivity extends AppCompatActivity
{

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
	}
}
