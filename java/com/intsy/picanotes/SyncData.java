package com.intsy.picanotes;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SyncData extends ActionBarActivity{
	GridView mGridView;
	private ImageAdapter mGridAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_new_layout);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#61AFFF")));
		
		SharedPreferences prefs = getSharedPreferences("TimePrefs", 0);
		Editor edit = prefs.edit();
		
		File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/100MEDIA");  // /.thumbnails
		
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".jpg");
		    }
		});
		
		if(files == null){
			files = dir.listFiles();
		}
		
		mGridView = (GridView)findViewById(R.id.gridView1);
		mGridAdapter = new ImageAdapter(this);
		
		int arrayLenHolder = files.length;
		int wantedPortion[] = new int[arrayLenHolder];
		
		for(int j = 0; j < arrayLenHolder; j++){
			String tempString = files[j].getAbsolutePath();
			int sizeofString = tempString.length();
			tempString = tempString.substring(sizeofString - 8, sizeofString - 4);
			if(isInteger(tempString)){
				wantedPortion[j] = Integer.parseInt(tempString);
			}
			else{
				wantedPortion[j] = -1;
				Log.d("File Format", "Num:" + tempString);
			}
		}
		
		/*Checking metadata*/
		int lastPictureRecognized = prefs.getInt("lastPicture",0);
		String dateString[] = new String[files.length];
		ArrayList<String> outputList = new ArrayList<String>();
		Calendar c = Calendar.getInstance(); 
		int curMonth = c.get(Calendar.MONTH) + 1;
		int curDayofMonth = c.get(Calendar.DAY_OF_MONTH);
		int curYear = c.get(Calendar.YEAR);
		
		int curSemester = timeOfSeason(curMonth, curDayofMonth, curYear);
		
		for(int i = 0; i < files.length; i++){
			ExifInterface intf = null;
			try {
			    intf = new ExifInterface(files[i].getAbsolutePath());
			} catch(IOException e) {
			    e.printStackTrace();
			}
			if(intf == null) {
			    /* File doesn't exist or isn't an image */
			}
			else{
				dateString[i] = intf.getAttribute(ExifInterface.TAG_DATETIME);
				
				lastPictureRecognized = 5;
				if(lastPictureRecognized != 0){
					int refMonth = Integer.parseInt(dateString[i].substring(5,7));
					int refDayofMonth = Integer.parseInt(dateString[i].substring(8,10));
					int refYear = Integer.parseInt(dateString[i].substring(0,4));
					
					GregorianCalendar cal = new GregorianCalendar(refYear, refMonth, refDayofMonth);
					
					int hourTaken = Integer.parseInt(dateString[i].substring(11, 13));
					int minuteTaken = Integer.parseInt(dateString[i].substring(14, 16));
					int dayNum =  cal.get(Calendar.DAY_OF_WEEK);
					
					int refSemester = timeOfSeason(refMonth, refDayofMonth, refYear);
					
					String dayofWeek = null;
					if(dayNum == 5){
				        dayofWeek = "MO";           
				    } else if (dayNum == 6){
				        dayofWeek = "TU";
				    } else if (dayNum == 7){
				        dayofWeek = "WE";
				    } else if (dayNum == 1){
				        dayofWeek = "TH";
				    } else if (dayNum == 2){
				        dayofWeek = "FR";
				    } else if (dayNum == 3){
				        dayofWeek = null; //Saturday
				    } else if (dayNum == 4){
				        dayofWeek = null; //Sunday
				    }
					
					if((dayofWeek != null) && (refSemester == curSemester)){
						String courseNameTemp = null;
						int indexPartial = (((hourTaken - 8) * 2) + (minuteTaken / 30));
						if(indexPartial >= 0){
							dayofWeek = (dayofWeek + "array_" + indexPartial);
							
							courseNameTemp = prefs.getString(dayofWeek, "NOCLASS0");
							if(!(courseNameTemp.equals("NOCLASS0"))){
								outputList.add(files[i].getAbsolutePath());
							}
						}
					}
				}
			}
		}
		
		/*Begin making thumbnails and displaying*/
		for(int thumbNailIndex = 0; thumbNailIndex < outputList.size(); thumbNailIndex++){
			final int THUMBSIZE = 64;
			
			Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(outputList.get(thumbNailIndex)), THUMBSIZE, THUMBSIZE);
			mGridAdapter.mWantedFiles.add(ThumbImage);
		}
		
		
//		File thumbDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/.thumbnails");  // /.thumbnails
//		File[] thumbFiles = thumbDir.listFiles(new FilenameFilter() {
//		    public boolean accept(File dir, String name) {
//		        return name.toLowerCase().endsWith(".jpg");
//		    }
//		});
//		for(int j = 0; j < thumbFiles.length; j++){
//			mGridAdapter.mWantedFiles.add(thumbFiles[j]);
//		}
//		
		/* Do your date/time stuff here */

        mGridView.setAdapter(mGridAdapter);
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
		
		Intent intent = new Intent(this, CourseList.class);
		startActivity(intent);
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause(){
		super.onPause(); 
		finish();
	}
	
	public static int timeOfSeason(int Month, int DayofMonth, int Year){
		/* timeOfSeason First Digit: 0-Winter, 1-Spring, 2-Summer, 3-Fall
		 * 		Format: YYYYSem*/
		int timeOfSeason = 4;
		
		if(Month == 1){
			if(DayofMonth <= 4)
				timeOfSeason = ((Year - 1)*10);	//Special case: tracing wrong year pictures
			else
				timeOfSeason = 1;
		}
		else if(Month < 5)
			timeOfSeason = 1;
		else if(Month == 5){
			if(DayofMonth <= 20){
				timeOfSeason = 1;
			}
			else
				timeOfSeason = 2;
		}
		else if(Month < 8)
			timeOfSeason = 2;
		else if(Month == 8){
			if(DayofMonth <= 14)
				timeOfSeason = 2;
			else
				timeOfSeason = 3;
		}
		else if(Month < 12)
			timeOfSeason = 3;
		else{
			if(DayofMonth <= 15)
				timeOfSeason = 3;
			else
				timeOfSeason = 0;
		}
		
		//check if special case was used
		if(timeOfSeason < 10)
			timeOfSeason = timeOfSeason + (Year*10);
		if(timeOfSeason == 20140){
			DayofMonth += 5;
		}
		
		return timeOfSeason;
	}
	
	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false;
			}
		}
		return true;
	}
	
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    //private ArrayList<File> mWantedFiles = new ArrayList<File>();
	    private ArrayList<Bitmap> mWantedFiles = new ArrayList<Bitmap>();
	    
	    public ImageAdapter(Context c) {
	        mContext = c;
	    }

	    public int getCount() {
	        return mWantedFiles.size();
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(300, 450));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(10, 20, 10, 20);
	        } else {
	            imageView = (ImageView) convertView;
	        }
	        //Bitmap mCurImage = BitmapFactory.decodeFile((String)mWantedFiles.get(position));
	        //imageView.setImageURI(Uri.fromFile(mWantedFiles.get(position)));
	        imageView.setImageBitmap(mWantedFiles.get(position));
	        
	        return imageView;
	    }

	    // references to our images
	}
}
