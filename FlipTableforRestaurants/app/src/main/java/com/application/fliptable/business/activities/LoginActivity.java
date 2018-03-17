package com.application.fliptable.business.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.application.fliptable.business.models.LoginResponse;

import com.application.fliptable.business.R;

import com.application.fliptable.business.rest.ApiClient;
import com.application.fliptable.business.rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText emailEditText = findViewById(R.id.input_email);
        final EditText passwordEditText = findViewById(R.id.input_password);
        Button signInButton = findViewById(R.id.btn_signin);
        final SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.LOGIN_SHARED_PREF,MODE_PRIVATE);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                // Login
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                retrofit2.Call<LoginResponse> loginCall = apiInterface.login(email,password);
                loginCall.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        progressDialog.hide();
                        if (response.body().getError() == 0){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra(MainActivity.SPOT_ID_KEY, response.body().getId());
                            sharedPreferences.edit().putInt(MainActivity.SPOT_ID_KEY, response.body().getId()).apply();
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        progressDialog.hide();
                        Toast.makeText(LoginActivity.this, "Cannot connect to server. Please check your connection", Toast.LENGTH_LONG).show();
                    }
                });
                hideSoftKeyboard(LoginActivity.this, passwordEditText);
            }
        });

    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
