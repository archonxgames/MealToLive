package pbl.g12.sem1.mealtolive;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener
{
	private static final int REQUEST_LOGIN = 1;
	private static final int REQUEST_DONATE = 2;
	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nav_drawer);
		mAuth = FirebaseAuth.getInstance();

		setContentView(R.layout.activity_nav_drawer);
		Toolbar toolbar = findViewById(R.id.nav_toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		// Check if user is signed in (non-null) and update UI accordingly.
		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null)
			if (currentUser.isEmailVerified())
				updateUI(currentUser);
			else
				onSignOut();
		else
			onSignOut();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_LOGIN)
		{
			if (resultCode == RESULT_OK)
			{
				FirebaseUser user = mAuth.getCurrentUser();
				updateUI(user);
			}
		}
	}

	@Override
	public void onBackPressed()
	{
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawer(GravityCompat.START);
		}
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nav_drawer, menu);
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

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_news)
		{
			// Handle the camera action
		}
		else if (id == R.id.nav_donate)
		{
			Intent intent = new Intent(getApplicationContext(), DonationActivity.class);
			startActivityForResult(intent, REQUEST_DONATE);
		}
		else if (id == R.id.nav_events)
		{

		}
		else if (id == R.id.nav_manage)
		{

		}
		else if (id == R.id.nav_share)
		{

		}
		else if (id == R.id.nav_logout)
		{
			AlertDialog.Builder confirmAlert = new AlertDialog.Builder(MainActivity.this);
			confirmAlert.setMessage("Are you sure you want to logout?");
			confirmAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialogInterface, int i)
				{
					mAuth.signOut();
					onSignOut();
				}
			});

			confirmAlert.setNegativeButton("No", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});

			confirmAlert.show();
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void onSignOut()
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	private void updateUI(FirebaseUser user)
	{
		NavigationView navigationView = findViewById(R.id.nav_view);
		View headerView = navigationView.getHeaderView(0);
		TextView _name = headerView.findViewById(R.id.nav_name);
		TextView _email = headerView.findViewById(R.id.nav_email);
		_name.setText(user.getDisplayName());
		_email.setText(user.getEmail());
	}
}
