package pbl.g12.sem1.mealtolive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
	private FirebaseAuth mAuth;
	private static final int REQUEST_SIGN_UP = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		mAuth = FirebaseAuth.getInstance();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FirebaseUser currentUser = mAuth.getCurrentUser();

		if (currentUser == null)
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent,REQUEST_SIGN_UP);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_SIGN_UP)
		{
			if (resultCode == RESULT_OK)
			{

				// TODO: Implement successful sign-up logic here
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}