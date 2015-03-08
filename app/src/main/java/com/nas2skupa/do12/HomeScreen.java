package com.nas2skupa.do12;
 
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeScreen extends BaseActivity {   
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GcmIntentService.class != null && !GcmIntentService.pendingNotifications.isEmpty())
            startActivity(new Intent(this, Organizer.class));
    }

    public void getSubcats(View v){
	String cat=null;
    switch (v.getId()) {
    case R.id.zdravljebtn:
        cat="1";
        break;
    case R.id.servisbtn:
        cat="2";
        break;
    case R.id.ljepotabtn:
        cat="3";
        break;
    case R.id.intelektbtn:
        cat="4";
        break;
    case R.id.dombtn:
        cat="5";
        break;
    case R.id.sportbtn:
        cat="6";
        break;
    case R.id.turizambtn:
        cat="7";
        break;
    case R.id.prijevozbtn:
        cat="8";
        break;
    case R.id.kulturabtn:
        cat="9";
        break;
		}
		Intent i = new Intent(this, Subcats.class).putExtra("cat", cat);
	    startActivity(i);	
	}
 
}