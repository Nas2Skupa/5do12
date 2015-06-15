package com.nas2skupa.do12;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ranko on 18.5.2015..
 */
public class Register extends Activity {

    private Button register;
    private EditText surname;
    private EditText name;
    private EditText address;
    private EditText email;
    private EditText password;
    private EditText repeatPassword;
    private EditText code;
    private Spinner city;
    private Spinner district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register);

        CitiesFilter citiesFilter = new CitiesFilter(this, (Spinner) findViewById(R.id.city), (Spinner) findViewById(R.id.district), 0, R.layout.register_spinner_item, R.layout.register_spiner_dropdown_item);
        name = (EditText) findViewById(R.id.name);
        surname = (EditText) findViewById(R.id.surname);
        city = (Spinner) findViewById(R.id.city);
        district = (Spinner) findViewById(R.id.district);
        address = (EditText) findViewById(R.id.address);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        repeatPassword = (EditText) findViewById(R.id.repeat_password);
        repeatPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    register.performClick();
                }
                return false;
            }
        });
        code = (EditText) findViewById(R.id.code);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(this.TELEPHONY_SERVICE);
        final String imei = telephonyManager.getDeviceId();

        register = (Button) findViewById(R.id.send_registration);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty() ||
                    surname.getText().toString().isEmpty() ||
                    address.getText().toString().isEmpty() ||
                    email.getText().toString().isEmpty() ||
                    password.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(Register.this, R.string.mandatoryFields, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                else if (!password.getText().toString().equals(repeatPassword.getText().toString())) {
                    Toast toast = Toast.makeText(Register.this, R.string.passwordsMismatch, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                    password.setText("");
                    repeatPassword.setText("");
                    password.requestFocus();
                }
                else {
                    City curr_city = Globals.cities.get(city.getSelectedItem().toString());
                    District curr_district = curr_city.districts.get(district.getSelectedItem().toString());
                    Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/registerUser.aspx")
                        .appendQueryParameter("name", name.getText().toString())
                        .appendQueryParameter("surname", surname.getText().toString())
                        .appendQueryParameter("quart", curr_district.id)
                        .appendQueryParameter("city", curr_city.id)
                        .appendQueryParameter("address", address.getText().toString())
                        .appendQueryParameter("email", email.getText().toString())
                        .appendQueryParameter("password", password.getText().toString())
                        .appendQueryParameter("referral", code.getText().toString())
                        .appendQueryParameter("imei", imei)
                        .build();
                    new HttpRequest(getApplicationContext(), uri, true)
                        .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                            @Override
                            public void onHttpResult(String result) {
                                if (result == null) return;

                                Integer user_id = Integer.getInteger(result);
                                int message = R.string.serverError;
                                if (user_id != null && user_id == 0) message = R.string.registrationUserExists;
                                else if (user_id != null && user_id > 0) message = R.string.registrationComplete;

                                Toast toast = Toast.makeText(Register.this, message, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                            }
                        });
                }    
            }
        });
    }
}
