package com.intsy.picanotes;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class InitActivity extends ActionBarActivity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean setupRun;
		SharedPreferences prefs = getSharedPreferences("TimePrefs", 0);
		Editor edit = prefs.edit();
		
		setupRun = prefs.getBoolean("RunBefore", false);
		if(setupRun == true){
			Intent intent = new Intent(this, CourseList.class);
			startActivity(intent);
		}
		setContentView(R.layout.course_list_activity);
		
		
		edit.putBoolean("RunBefore", true);
		edit.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sync_new_menu, menu);
		ActionBar bar = getActionBar();
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause(){
		super.onPause(); 
		finish();
	}
}
