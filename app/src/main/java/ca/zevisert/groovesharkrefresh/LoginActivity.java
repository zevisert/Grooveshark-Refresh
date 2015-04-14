package ca.zevisert.groovesharkrefresh;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends Activity {

    private String USER_NAME, USER_PASS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        getActionBar().hide(); //TODO Assure Actionbar is shown before leaving Activity

        VolleyContainer netQueue = new VolleyContainer(LoginActivity.this);

        final Button loginButton = (Button) findViewById(R.id.login_button);
        final Vibrator vibrator = (Vibrator) LoginActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        final EditText loginEditText_User = (EditText) findViewById(R.id.login_user);
        final EditText loginEditText_Pass = (EditText) findViewById(R.id.login_pass);

        loginEditText_User.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    loginEditText_Pass.requestFocus();
                }
                return true;
            }
        });

        loginEditText_Pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) { // TODO Set to min password length
                    loginButton.setVisibility(View.VISIBLE);
                } else loginButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loginEditText_Pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    loginButton.performClick();
                }
                return true;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(40);
                USER_NAME = loginEditText_User.getText().toString();
                Log.d("[GS ONLINE][USER_NAME]", loginEditText_User.getText().toString());

                try {
                    MessageDigest md5Digest = MessageDigest.getInstance("MD5");
                    USER_PASS = new String(md5Digest.digest(loginEditText_Pass.getText().toString().getBytes("UTF-8")));
                    Log.d("[GS REFRESH][PASSWORD]", loginEditText_Pass.getText().toString() + " : " + USER_PASS);
                } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                    Log.e("[GS REFRESH][PASSWORD]", e.getMessage());
                }
                Toast.makeText(getApplicationContext(), "User: " + USER_NAME + "\nPass: " + loginEditText_Pass.getText().toString() + " : " + USER_PASS, Toast.LENGTH_LONG).show();
                Log.d("[GS REFRESH][ONLINE]", USER_NAME + " : " + USER_PASS);
                // TODO Send API Authenticate Request
            }
        });
    }
}

