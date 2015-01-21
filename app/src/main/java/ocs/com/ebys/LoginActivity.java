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
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Onur Cem on 1/20/2015.
 */
public class LoginActivity extends ActionBarActivity implements OnLoginTaskCompleted {
    private ProgressDialog progressDialog;
    private EditText username;
    private EditText password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        password.setTransformationMethod(new PasswordTransformationMethod());
        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Oturum açılıyor...");
                progressDialog.show();

                EBYSController ebysCtrl = EBYSController.getInstance();
                ebysCtrl.login(LoginActivity.this, username.getText().toString(),
                        password.getText().toString());
            }
        });
    }

    @Override
    public void onLoginTaskCompleted(ServerResult result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (result.isSuccess()) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}