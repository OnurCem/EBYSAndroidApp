package ocs.com.ebys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Onur Cem on 1/20/2015.
 */
public class LoginActivity extends ActionBarActivity implements OnLoginTaskCompleted {
    private String username;
    private String password;
    private ProgressDialog progressDialog;
    private EditText editUsername;
    private EditText editPassword;
    private Button login;
    private CheckBox rememberMe;
    private EBYSController ebysCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ebysCtrl = EBYSController.getInstance();
        editUsername = (EditText) findViewById(R.id.username);
        editPassword = (EditText) findViewById(R.id.password);
        editPassword.setTransformationMethod(new PasswordTransformationMethod());
        login = (Button) findViewById(R.id.login);
        rememberMe = (CheckBox) findViewById(R.id.remember_me);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                username = editUsername.getText().toString();
                password = editPassword.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    login(username, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Öğrenci no ve şifrenizi giriniz", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (isRememberedUser()) {
            login(username, password);
            editUsername.setText(username);
            rememberMe.setChecked(true);
        }
    }

    @Override
    public void onLoginTaskCompleted(ServerResult result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (result.isSuccess()) {
            if (rememberMe.isChecked()) {
                PreferenceController.saveSharedSetting(LoginActivity.this,
                        PreferenceController.PREF_USERNAME, username);
                PreferenceController.saveSharedSetting(LoginActivity.this,
                        PreferenceController.PREF_PASSWORD, password);
            }
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void login(String username, String password) {
        ebysCtrl.login(LoginActivity.this, username, password);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Oturum açılıyor...");
        progressDialog.show();
    }

    private boolean isRememberedUser() {
        username = PreferenceController.readSharedSetting(LoginActivity.this,
                PreferenceController.PREF_USERNAME, null);
        password = PreferenceController.readSharedSetting(LoginActivity.this,
                PreferenceController.PREF_PASSWORD, null);

        if (username != null && password != null) {
            return true;
        }
        return false;
    }
}