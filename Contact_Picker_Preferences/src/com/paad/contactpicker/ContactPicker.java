package com.paad.contactpicker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactPicker extends Activity {
  
	 int nameLength = 0;
	 private static final int SHOW_PREFERENCES = 1;
	 private static final int PICK_CONTACT = 2;
	 final Context context = this;
	 Cursor c;
	 Uri data ;
	 String dataPath ;
	 
	@Override
	public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
    
    Intent intent = getIntent();
    dataPath = intent.getData().toString();

    updateFromPreferences();
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
      MenuInflater menuInflater = getMenuInflater();
      menuInflater.inflate(R.layout.menu, menu);
      return true;
  } 
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId())
    {
    case R.id.filter_name:
        Intent i = new Intent(this, Preferences.class);
        startActivityForResult(i, SHOW_PREFERENCES);
        return true;

    default:
        return super.onOptionsItemSelected(item);
    }
  }
  private void updateFromPreferences() {
    Context context = getApplicationContext();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    nameLength = Integer.parseInt(prefs.getString(Preferences.PREF_NAME_LEN, "0"));
    //dialog(prefs.getString(Preferences.PREF_NAME_LEN, "0"));  
   
        data = Uri.parse(dataPath + "people/");
        
        c = managedQuery(data, null, null,null, null);
    
        ///////////////////ONLY This much change in ContactPick.java Class//////////////
        //////////////Which filters all name using Length /////////////////////
	    ArrayList<String> newFrom = new ArrayList<String>() ;
        for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() ) 
        {
        	String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        	
        	if(name.length()>nameLength)
        	{
        		newFrom.add(name);
        	}
        }

	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listitemlayout,R.id.itemTextView, newFrom);
	    
	    /////////////////////////////////////////////////////////////////////////////////
	    ListView lv = (ListView)findViewById(R.id.contactListView);
	    lv.setAdapter(adapter);
	    
	    lv.setOnItemClickListener(new OnItemClickListener() {
	      public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
	        // Move the cursor to the selected item
	        c.moveToPosition(pos);
	        // Extract the row id.
	        int rowId = c.getInt(c.getColumnIndexOrThrow("_id"));
	        // Construct the result URI.
	        Uri outURI = Uri.parse(data.toString() + rowId);
	        Intent outData = new Intent();
	        outData.setData(outURI);
	        setResult(Activity.RESULT_OK, outData);
	        finish();
	      }
	    }); 
  }
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

      switch (requestCode) 
      {
		case(SHOW_PREFERENCES) :
		{
		    updateFromPreferences();
		    break;
		} 
      }
     }
  
}