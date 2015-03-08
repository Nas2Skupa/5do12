package com.nas2skupa.do12;

import com.navdrawer.SimpleSideDrawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BaseListActivity extends ListActivity {
    SimpleSideDrawer slide_me;
    EditText inputSearch;
    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        slide_me = new SimpleSideDrawer(this);
        slide_me.setRightBehindContentView(R.layout.sidemenu);
        TextView pocTV = (TextView) findViewById(R.id.pocetnabtn);
        TextView orgTV = (TextView) findViewById(R.id.organizatorbtn);
        TextView favTV = (TextView) findViewById(R.id.favoritibtn);
        TextView akcTV = (TextView) findViewById(R.id.akcijebtn);
        TextView odjTV = (TextView) findViewById(R.id.odjavabtn);
        TextView proTV = (TextView) findViewById(R.id.profilbtn);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    searchActivity();
                }
                return false;
            }
        });
        pocTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                homeActivity();
            }
        });
        orgTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                listActivity();
            }
        });
        favTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                favActivity();
            }
        });
        akcTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                vaznoActivity();
            }
        });
        proTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                profilActivity();
            }
        });
        odjTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutActivity();
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.home:
                homeActivity();
                return true;

            case R.id.list:
                listActivity();
                return true;

            case R.id.fav:
                favActivity();
                return true;

            case R.id.akcije:
                vaznoActivity();
                return true;

            case R.id.option:
                optionActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void searchActivity(){
        Intent i = new Intent(this, SearchActivity.class);
        i.putExtra("search", inputSearch.getText().toString());
        startActivity(i);

    }
    public void homeActivity() {
        Intent i = new Intent(this, HomeScreen.class);
        startActivity(i);
    }

    public void listActivity() {
        //Toast.makeText(this, "List Option Selexted", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, Organizer.class);
        startActivity(i);
    }

    public void favActivity() {

        Intent i = new Intent(this, Favorites.class);
        startActivity(i);
    }

    public void vaznoActivity() {
        Intent i = new Intent(this, Akcije.class);
        startActivity(i);
    }
    public void profilActivity() {
        Intent i = new Intent(this, ProfilActivity.class);
        startActivity(i);
    }

    public void logoutActivity() {
        Intent i = new Intent(this, LoginScreen.class);
        i.setAction("logout");
        startActivity(i);
    }

    public void optionActivity() {
        slide_me.toggleRightDrawer();
    }

    public String[] catSettings = new String[2];

    public String[] getCatSett(int CatId) {

        switch (CatId) {

            case 1:
                catSettings[0]="zdravstvene usluge";
                catSettings[1]="#7ec5c4";
                return catSettings;
            case 2:
                catSettings[0]="servis i održavanje";
                catSettings[1]="#8c7c66";
                return catSettings;
            case 3:
                catSettings[0]="ljepota";
                catSettings[1]="#a80364";
                return catSettings;
            case 4:
                catSettings[0]="intelektualne usluge";
                catSettings[1]="#49019a";
                return catSettings;
            case 5:
                catSettings[0]="dom i obitelj";
                catSettings[1]="#687f95";
                return catSettings;
            case 6:
                catSettings[0]="sport i rekreacija";
                catSettings[1]="#7dad91";
                return catSettings;
            case 7:
                catSettings[0]="turizam i ugostiteljstvo";
                catSettings[1]="#e2a217";
                return catSettings;
            case 8:
                catSettings[0]="prijevoz";
                catSettings[1]="#919294";
                return catSettings;
            case 9:
                catSettings[0]="kultura i zabava";
                catSettings[1]="#826882";
                return catSettings;



            default:
                return catSettings;
        }
    }
}
