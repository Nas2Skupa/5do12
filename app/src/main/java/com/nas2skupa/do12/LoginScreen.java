package com.nas2skupa.do12;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

public class LoginScreen extends Activity {
    static final String TAG = "5do12";

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String SENDER_ID = "492542058520";

    private GoogleCloudMessaging gcm;
    private SharedPreferences prefs;
    private Context context;
    private String userId;
    private String regid;

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private String url;
    private Boolean loged = false;

    // JSON Node names
    private static final String TAG_ARRAY = "user";
    private static final String TAG_ID = "id";
    private static final String TAG_UNAME = "username";
    private static final String TAG_NAME = "name";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_BIRTH = "birth";
    private static final String TAG_HOMEADD = "home_add";
    private static final String TAG_BUSINESSADD = "business_add";
    private static final String TAG_CELL = "cell";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_GCMID = "gcmId";
    private static final String TAG_COUNTRY = "country";
    private static final String TAG_CITY = "city";
    private static final String TAG_CITYNAME = "cityName";
    private static final String TAG_QUART = "quart";
    private static final String TAG_QUARTNAME = "quartName";

    // subcats JSONArray
    JSONArray user = null;
    private EditText pass;
    private Button login;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        if (checkPlayServices())
            gcm = GoogleCloudMessaging.getInstance(this);
        else
            Log.i(TAG, "No valid Google Play Services APK found.");

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        if(intent.getAction() == "logout")
            prefs.edit().clear().commit();
        userId = prefs.getString(TAG_ID, "");
        if (userId.isEmpty()) {
            initialize();
        } else {
            regid = getRegistrationId(context);
            if (regid.isEmpty()) registerInBackground();
            else goHome();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        if(intent.getAction() == "logout")
            prefs.edit().clear().commit();
        userId = prefs.getString(TAG_ID, "");
        if (userId.isEmpty()) {
            initialize();
        } else {
            regid = getRegistrationId(context);
            if (regid.isEmpty()) registerInBackground();
            else goHome();
        }
    }

    private void initialize() {
        setContentView(R.layout.login);
        login = (Button) findViewById(R.id.btnLogin);
        pass = (EditText) findViewById(R.id.password);
        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    login.performClick();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    private void goHome() {
        Intent in = new Intent(this, HomeScreen.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
    }

    public void tryLogin(View view) throws MalformedURLException, IOException {
        new GetUser().execute();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    sendRegistrationIdToBackend();
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Log.i(TAG, o.toString());
                goHome();
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to app.
     */
    private void sendRegistrationIdToBackend() {
        ServiceHandler sh = new ServiceHandler();
        url = "http://nas2skupa.com/5do12/updateGcmId.aspx?userId=" + userId + "&gcmId=" + regid;
        String result = sh.makeServiceCall(url, ServiceHandler.GET);
        Log.d("Response: ", "> " + result);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginScreen.this);
            pDialog.setMessage("Trying to login...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            EditText userText = (EditText) findViewById(R.id.username);
            String userTxt = userText.getText().toString();
            EditText passText = (EditText) findViewById(R.id.password);
            String passTxt = passText.getText().toString();
            // Making a request to url and getting response
            url = "http://nas2skupa.com/5do12/getUsers.aspx?u=" + userTxt + "&p=" + passTxt;
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    user = jsonObj.getJSONArray(TAG_ARRAY);

                    if (user.length() == 0)
                        return null;
                    if (user.length() > 1)
                        Log.e(TAG, "More than one user found in database!!! Getting last one...");

                    JSONObject c = user.getJSONObject(user.length() - 1);
                    userId = c.getString(TAG_ID);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(TAG_ID, c.getString(TAG_ID));
                    editor.putString(TAG_UNAME, c.getString(TAG_UNAME));
                    editor.putString(TAG_NAME, c.getString(TAG_NAME));
                    editor.putString(TAG_SURNAME, c.getString(TAG_SURNAME));
                    editor.putString(TAG_BIRTH, c.getString(TAG_BIRTH));
                    editor.putString(TAG_HOMEADD, c.getString(TAG_HOMEADD));
                    editor.putString(TAG_BUSINESSADD, c.getString(TAG_BUSINESSADD));
                    editor.putString(TAG_CELL, c.getString(TAG_CELL));
                    editor.putString(TAG_EMAIL, c.getString(TAG_EMAIL));
                    editor.putString(TAG_GENDER, c.getString(TAG_GENDER));
                    editor.putString(TAG_GCMID, c.getString(TAG_GCMID));
                    editor.putString(TAG_COUNTRY, c.getString(TAG_COUNTRY));
                    editor.putString(TAG_CITY, c.getString(TAG_CITY));
                    editor.putString(TAG_CITYNAME, c.getString(TAG_CITYNAME));
                    editor.putString(TAG_QUART, c.getString(TAG_QUART));
                    editor.putString(TAG_QUARTNAME, c.getString(TAG_QUARTNAME));
                    editor.commit();
                    loged = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (loged) {
                regid = getRegistrationId(context);
                if (regid.isEmpty()) registerInBackground();
                else goHome();
            } else {
                Toast.makeText(LoginScreen.this, "Neispravni username i/ili password!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
