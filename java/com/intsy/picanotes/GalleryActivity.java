package com.intsy.picanotes;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.intsy.picanotes.SyncData.ImageAdapter;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class GalleryActivity extends ActionBarActivity{
	GridView mGridView;
	private ImageAdapter mGridAdapter;
    private int imageListOffSet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_new_layout);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#61AFFF")));
		imageListOffSet = 0;

		SharedPreferences prefs = getSharedPreferences("TimePrefs", 0);
		Editor edit = prefs.edit();
		String mSelectedCourse = prefs.getString("SelectedCourse", null);
		SharedPreferences coursePics = getSharedPreferences(mSelectedCourse, 0);
		
		ArrayList<String> outputList = new ArrayList<String>();
		mGridView = (GridView)findViewById(R.id.gridView1);
		mGridAdapter = new ImageAdapter(this);
		
		if(!(mSelectedCourse.equals(null))){
			int length = coursePics.getInt("Length", 0);
			String holder = null;
			
			for(int index = 0; index < length; index++){
				holder = coursePics.getString("" + index, null);
				if(!(holder.equals(null)))
					outputList.add(holder);
			}
		}
		else{
			Toast.makeText(GalleryActivity.this, "Error Finding Course", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		/*Begin making thumbnails and displaying*/
		for(int thumbNailIndex = 0; thumbNailIndex < outputList.size(); thumbNailIndex++){
			final int THUMBSIZE = 128;
			
			Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(outputList.get(thumbNailIndex)), THUMBSIZE, THUMBSIZE);
			mGridAdapter.mWantedFiles.add(ThumbImage);
		}

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
		
		finish();
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause(){
		super.onPause(); 
		finish();
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
	            imageView.setPadding(10, 10, 10, 10);
	        } else {
	            imageView = (ImageView) convertView;
	        }
	        //Bitmap mCurImage = BitmapFactory.decodeFile((String)mWantedFiles.get(position));
	        //imageView.setImageURI(Uri.fromFile(mWantedFiles.get(position)));
            boolean mRepeat = true;
            int sizeOfArrayList = mWantedFiles.size();
            do {
                try {
                    imageView.setImageBitmap(mWantedFiles.get(position + imageListOffSet));
                } catch (Exception e) {
                    imageListOffSet++;
                    if (((position + imageListOffSet) >= sizeOfArrayList)) {
                        mRepeat = false;
                    }
                }
            }while(mRepeat == true);
            imageListOffSet = 0;
	        return imageView;
	    }

	    // references to our images
	}
}
