package com.intsy.picanotes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeSet;


import android.support.v7.app.ActionBarActivity;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddCourseActivity extends ActionBarActivity {
	static final int timeArraySize = 31;
	
	ListActivity listActivity;
	ListView addTimeListView;
	EditText mCourseName;
	
	private MyCustomAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_course);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#61AFFF")));
		
		mCourseName = (EditText) findViewById(R.id.editText1);
		addTimeListView = (ListView) findViewById(R.id.mainListView);
		mAdapter = new MyCustomAdapter();
        mAdapter.addItem("Monday");
        mAdapter.addItem("Tuesday");
        mAdapter.addItem("Wednesday");
        mAdapter.addItem("Thursday");
        mAdapter.addItem("Friday");        
        addTimeListView.setAdapter(mAdapter);       
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_course, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_done){
			String endCourseName = mCourseName.getText().toString().replaceAll("\\s+", "");
			//endCourseName = endCourseName.replaceAll(".", "");
			//endCourseName = endCourseName.replaceAll("/", "");
			
			if(endCourseName.length() == 0){
				Toast.makeText(AddCourseActivity.this, "Please Enter Course Name", Toast.LENGTH_SHORT).show();
			}
			else{
				SharedPreferences prefs = getSharedPreferences("TimePrefs", 0);
				Editor edit = prefs.edit();
				if(prefs.getString("MOarray_" + 0,null) == null){
					//Check - Array not made - initialize array
					for(int i=0;i < timeArraySize; i++){
						edit.putString("MOarray_" + i, "NOCLASS0");
						edit.putString("TUarray_" + i, "NOCLASS0");
						edit.putString("WEarray_" + i, "NOCLASS0");
						edit.putString("THarray_" + i, "NOCLASS0");
						edit.putString("FRarray_" + i, "NOCLASS0");
					}
					//Initialize Size of course names array
					edit.putInt("courseArraySize", 0);
					
					edit.commit();
					
				}
				
				String MOarray[],TUarray[],WEarray[],THarray[],FRarray[];
				MOarray = new String[timeArraySize];
				TUarray = new String[timeArraySize];
				WEarray = new String[timeArraySize];
				THarray = new String[timeArraySize];
				FRarray = new String[timeArraySize];				
				
				/* for purpose of checking for preexisting times*/
				for(int i=0; i < timeArraySize; i++){
				    MOarray[i] = prefs.getString("MOarray_" + i, null);
				    TUarray[i] = prefs.getString("TUarray_" + i, null);
				    WEarray[i] = prefs.getString("WEarray_" + i, null);
				    THarray[i] = prefs.getString("THarray_" + i, null);
				    FRarray[i] = prefs.getString("FRarray_" + i, null);
				}				
				
				int courseIndexer = prefs.getInt("courseArraySize", 99);
				
				if(courseIndexer != 99){
					String testingString;
					boolean repeatedCheck = false;
					for(int strtI = 0; strtI < courseIndexer; strtI++){
						testingString = prefs.getString("courseArray_" + strtI, "ERROR2");
						if(testingString.equals(endCourseName)){
							repeatedCheck = true;
							Toast.makeText(AddCourseActivity.this, "Repeated class name", Toast.LENGTH_SHORT).show();
						}
					}
					
					int countofEnteredTimes = 0;
					for(int jx = 0; jx < mAdapter.mTimes.size(); jx += 3){
						int timePos1 = Integer.parseInt(mAdapter.mTimes.get(jx + 1));
						int timePos2 = Integer.parseInt(mAdapter.mTimes.get(jx + 2));
						
						if(timePos1 >= timePos2){
							Toast.makeText(AddCourseActivity.this, "Correct the time formatting", Toast.LENGTH_SHORT).show();
							repeatedCheck = true;
						}
						countofEnteredTimes++;
					}
					if(countofEnteredTimes == 0){
						Toast.makeText(AddCourseActivity.this, "No Entered Times", Toast.LENGTH_SHORT).show();
						repeatedCheck = true;
					}
					
					if(!repeatedCheck){
						edit.putString("courseArray_" + courseIndexer, endCourseName);
						edit.putInt("courseArraySize", courseIndexer + 1);
						edit.commit();	
						
						String dayId;						
						for(int finalIndex = 0; finalIndex < mAdapter.mTimes.size(); finalIndex += 3){
							dayId = mAdapter.mTimes.get(finalIndex).substring(0, 2);
							int timePos1 = Integer.parseInt(mAdapter.mTimes.get(finalIndex + 1));
							int timePos2 = Integer.parseInt(mAdapter.mTimes.get(finalIndex + 2));
							
//							//TDOD: Error prone! Does not exit upon realizing error
//							if(timePos1 >= timePos2)
//								Toast.makeText(AddCourseActivity.this, "Correct the time formatting", Toast.LENGTH_SHORT).show();
							for(int i = 0; (timePos1 + i) < timePos2; i++){
								edit.putString(dayId + "array_" + (timePos1 + i), endCourseName);
							}				
							
							
							
							edit.commit();					
						}
						Intent intent = new Intent(this, SyncNewService.class);
						startService(intent);
						
						finish();
					}
				}
				else
					Toast.makeText(AddCourseActivity.this, "Error updating courses array", Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class MyCustomAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
        
        private ArrayList<String> mData = new ArrayList<String>();
        private ArrayList<String> mTimes = new ArrayList<String>();
        private LayoutInflater mInflater;
        private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final String item) {
            mData.add(item);
            // save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
        	String decompString = mData.get(position);
        	char decompChar = decompString.charAt(2);
        	
        	if(Character.isDigit(decompChar)){
        		return TYPE_SEPARATOR;
        	}
        	
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
        	final int rootIndex = position;
            int type = getItemViewType(position);
            System.out.println("getView " + position + " " + convertView + " type = " + type);
            if (convertView == null) {
                holder = new ViewHolder();
                
                switch (type) {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.item1, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.text);
                        holder.btnOnly = (Button)convertView.findViewById(R.id.mBtn);
                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.item2, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
                        holder.mSpinner = (Spinner)convertView.findViewById(R.id.mainSpinner);
                        holder.mSpinner2 = (Spinner)convertView.findViewById(R.id.subSpinner);
                        holder.btnOnly = (Button)convertView.findViewById(R.id.dltBtn);
                        break;
                }
                
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            
            if(type == TYPE_ITEM){
            	final String tempString = mData.get(position);            	
            	
            	holder.textView.setText(tempString);
            	holder.btnOnly.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {	
						short checkIterator;
						int nextIndex = 1;
						String partial = null;
						
						do{
							checkIterator = 0;
							if((rootIndex + nextIndex) < mData.size()){
					        	char decompChar = mData.get(rootIndex + nextIndex).charAt(2);					        	
					        	if(!(Character.isDigit(decompChar))){					        					        
					        		checkIterator = 1;
					        	}	
					        	else{
					        		int preIndex = (int) (decompChar - '0');
					        		if(nextIndex < preIndex){
					        			checkIterator = 1;
					        		}
					        		else{					        			
					        			nextIndex++;
					        		}
					        	}
							}
							else{
								checkIterator = 1;
							}			
							if(nextIndex > 9){
								checkIterator = 2;
							}
						}while(checkIterator == 0);											
						partial = tempString.substring(0, 2).toUpperCase();
						
						if(checkIterator == 1){
							mData.add(rootIndex + nextIndex,partial + nextIndex);
							notifyDataSetChanged();
						}
					}
            	});
            }
            else{
            	final String rowName = mData.get(position);
            	int indexHolder = mTimes.indexOf(rowName);;
            	
            	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddCourseActivity.this,R.array.TimeTableDropdown, android.R.layout.simple_spinner_item);
            	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            	holder.mSpinner.setAdapter(adapter);
            	
            	ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(AddCourseActivity.this,R.array.TimeTableDropdown, android.R.layout.simple_spinner_item);
            	adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            	holder.mSpinner2.setAdapter(adapter2);
            	
            	if(indexHolder != -1){
            		holder.mSpinner.setSelection(Integer.parseInt(mTimes.get(indexHolder + 1)));
            		holder.mSpinner2.setSelection(Integer.parseInt(mTimes.get(indexHolder + 2)));
            	}
            	//holder.textView.setText(rowName);
            	holder.btnOnly.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View v) {
						int indHolder = mTimes.indexOf(rowName);
						if(indHolder != -1){
							mTimes.remove(indHolder + 2);
							mTimes.remove(indHolder + 1);
							mTimes.remove(indHolder);
							mData.remove(rootIndex);
						}
						notifyDataSetChanged();
					}
            	});
            	holder.mSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			        	String decompString = mData.get(rootIndex);
			        	int indHolder = mTimes.indexOf(decompString);
			        	if(indHolder != -1){
			        		mTimes.set(indHolder + 1, Integer.toString(pos));
			        	}
			        	else{
			        		mTimes.add(decompString);
			        		mTimes.add(Integer.toString(pos));
			        		mTimes.add("0");
			        	}					
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						Toast.makeText(AddCourseActivity.this, "Error?", Toast.LENGTH_SHORT).show();	
					}
            	});
            	
            	holder.mSpinner2.setOnItemSelectedListener(new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			        	String decompString = mData.get(rootIndex);
			        	int indHolder = mTimes.indexOf(decompString);
			        	if(indHolder != -1){
			        		mTimes.set(indHolder + 2, Integer.toString(pos));
			        	}
			        	else{
			        		mTimes.add(decompString);
			        		mTimes.add("0");
			        		mTimes.add(Integer.toString(pos));
			        	}					
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						Toast.makeText(AddCourseActivity.this, "Error?", Toast.LENGTH_SHORT).show();	
					}
            	});
            }
            return convertView;
        }
	}
	
	public static class ViewHolder {
        public TextView textView = null;
        public Button btnOnly;
        public Spinner mSpinner;
        public Spinner mSpinner2;
    }
}