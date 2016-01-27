package com.intsy.picanotes;

import java.util.ArrayList;
import java.util.TreeSet;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CourseList extends ActionBarActivity {
	ListActivity listActivity;
	ListView courseNameLV;
	
	private static final String EMPTY_STRING_STD = "NOCLASS0";
	private static final int TIME_ARRAY_SIZE = 31;
	private MyCustomAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_list_activity);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#61AFFF")));
		
		int courseIndexer;
		courseNameLV = (ListView) findViewById(R.id.mcourseLView);
		
		SharedPreferences prefs = getSharedPreferences("TimePrefs", 0);
		final Editor edit = prefs.edit();
		
		mAdapter = new MyCustomAdapter();
		mAdapter.deletePresent = false;
		courseNameLV.setAdapter(mAdapter);   
		
		courseIndexer = prefs.getInt("courseArraySize", 0);
		if(courseIndexer != 0){
			for(int i = 0; i < courseIndexer; i++){
				mAdapter.addItem(prefs.getString("courseArray_" + i, "ERROR 4"));
			}		 
			
			courseNameLV.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	                String item = mAdapter.mData.get(position);
	                edit.putString("SelectedCourse", item);
	                edit.commit();
	                //SharedPreferences coursePic = getSharedPreferences(item, 0);
	                
	                Intent intent = new Intent(CourseList.this, GalleryActivity.class);
	    			startActivity(intent);
	            }
	        });
		}
	}

	@Override
	protected void onResume(){
		super.onResume();
		
		int coursesIndexer;
		SharedPreferences prefs = getSharedPreferences("TimePrefs", 0);
		
		coursesIndexer = prefs.getInt("courseArraySize", 0);
		if(coursesIndexer != 0){
			mAdapter.mData.clear();
			for(int i = 0; i < coursesIndexer; i++){
				mAdapter.addItem(prefs.getString("courseArray_" + i, "ERROR 4"));
			} 
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.newCourseBtn) {			
			Intent intent = new Intent(this, AddCourseActivity.class);
			startActivity(intent);
		}
		if (id == R.id.enableDeleteBtn){
			SharedPreferences prefs = getSharedPreferences("TimePrefs", 0);
			
			int coursesIndexer = prefs.getInt("courseArraySize", 0);
			
			if(coursesIndexer != 0){
				mAdapter.deletePresent = !(mAdapter.deletePresent);
				mAdapter.mData.clear();
				for(int i = 0; i < coursesIndexer; i++){
					mAdapter.addItem(prefs.getString("courseArray_" + i, "ERROR 5"));
				} 
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class MyCustomAdapter extends BaseAdapter {
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = 1;
        private boolean deletePresent;
        
        private ArrayList<String> mData = new ArrayList<String>();
        private LayoutInflater mInflater;
        private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ViewHolder holder = null;
        	final int rootPosition = position;
            int type = getItemViewType(position);
            
            System.out.println("getView " + position + " " + convertView + " type = " + type);
            if (convertView == null) {
                holder = new ViewHolder();
                
                switch (type) {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.course_listing_subitem, null);
                        
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView = (TextView)convertView.findViewById(R.id.courseNameTextView);
            holder.textView.setText(mData.get(position));
            holder.btnOnly = (Button)convertView.findViewById(R.id.enableDeleteBtnenableDeleteBtn);
            SharedPreferences prefs = getSharedPreferences("TimePrefs", 0);
            if(deletePresent && (prefs.getString("courseArray_" + 0, "XX") != "XX")){
            	holder.btnOnly.setVisibility(View.VISIBLE);
            	holder.btnOnly.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {	
						//TODO: Add delete methods for course name and time entries shifting over course names
						SharedPreferences prefs = getSharedPreferences("TimePrefs", 0);
						Editor edit = prefs.edit();
						
						//Delete class from time listing
						String selectedCourse = mData.get(rootPosition);
						for(int i = 0; i < TIME_ARRAY_SIZE; i++){
							if(prefs.getString("MOarray_" + i, "ERROR6").equals(selectedCourse))
								edit.putString("MOarray_" + i,EMPTY_STRING_STD);
							if(prefs.getString("TUarray_" + i, "ERROR6").equals(selectedCourse))
								edit.putString("TUarray_" + i,EMPTY_STRING_STD);
							if(prefs.getString("WEarray_" + i, "ERROR6").equals(selectedCourse))
								edit.putString("WEarray_" + i,EMPTY_STRING_STD);
							if(prefs.getString("THarray_" + i, "ERROR6").equals(selectedCourse))
								edit.putString("THarray_" + i,EMPTY_STRING_STD);
							if(prefs.getString("FRarray_" + i, "ERROR6").equals(selectedCourse))
								edit.putString("FRarray_" + i,EMPTY_STRING_STD);
						}
						
						//Delete class from course names
//						edit.putString("courseArray_" + courseIndexer, endCourseName);
//						edit.putInt("courseArraySize", courseIndexer + 1);
//						edit.commit();	
						int traceIndexer = rootPosition;
						int reducedArraySize = prefs.getInt("courseArraySize", 99);
						edit.putInt("courseArraySize", reducedArraySize - 1);
						for(int followIndexer = (traceIndexer + 1); followIndexer < reducedArraySize; followIndexer++){							
							edit.putString("courseArray_" + traceIndexer, prefs.getString("courseArray_" + followIndexer, "ERROR7"));
							traceIndexer++;
						}
						edit.commit();
						
						traceIndexer = prefs.getInt("courseArraySize", 0);
						mData.clear();
						if(traceIndexer != 0){
							for(int i = 0; i < traceIndexer; i++){
								addItem(prefs.getString("courseArray_" + i, "ERROR 5"));
							} 
						}
						else{
							notifyDataSetChanged();
						}
						
						Toast.makeText(CourseList.this, "Delete Button Pressed", Toast.LENGTH_SHORT).show();
					}
            	});
            }
            else{
            	holder.btnOnly.setVisibility(View.GONE);
            }
            return convertView;
        }
	}
	
	public static class ViewHolder {
        public TextView textView = null;
        public Button btnOnly;
    }
}

