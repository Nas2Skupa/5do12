package com.nas2skupa.do12;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ranko on 18.5.2015..
 */
public class Register extends Activity {

    private Button register;
    private EditText username;
    private EditText name;
    private EditText address;
    private EditText email;
    private EditText password;
    private EditText repeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register);

        username = (EditText) findViewById(R.id.username);
        name = (EditText) findViewById(R.id.name);
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

        register = (Button) findViewById(R.id.send_registration);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().isEmpty() ||
                    name.getText().toString().isEmpty() ||
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
                    Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/registerUser.aspx")
                        .appendQueryParameter("username", username.getText().toString())
                        .appendQueryParameter("name", name.getText().toString())
                        .appendQueryParameter("address", address.getText().toString())
                        .appendQueryParameter("email", email.getText().toString())
                        .appendQueryParameter("password", password.getText().toString())
                        .build();
                    new HttpRequest(getApplicationContext(), uri, true)
                        .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                            @Override
                            public void onHttpResult(String result) {
                                Toast toast = Toast.makeText(Register.this, R.string.registrationComplete, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                            }
                        });
                }    
            }
        });
    }
}
