package com.example.trainingproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.trainingproject.R;
import com.example.trainingproject.models.ErrorResponse;
import com.example.trainingproject.models.RegUser;
import com.example.trainingproject.httpRequest.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements  View.OnClickListener, View.OnKeyListener{

    private static final String TAG = "Register";
    View bgMain;
    View logo;
    View layout;
    TextInputLayout textEmail;
    TextInputLayout textFullName;
    TextInputLayout textPassword;
    String emailInput;
    String fullName;
    String password;
    static Activity activity;

    public void loginScreen(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        activity = this;
        bgMain = findViewById(R.id.bgMainReg);
        logo = findViewById(R.id.logoImg);
        layout = findViewById(R.id.constraintLayoutReg);
        textEmail = findViewById(R.id.text_input_email);
        textFullName = findViewById(R.id.text_input_fullname);
        textPassword = findViewById(R.id.text_input_pasword);

        bgMain.setOnClickListener(this);
        logo.setOnClickListener(this);
        layout.setOnClickListener(this);
        textPassword.getEditText().setOnKeyListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bgMainReg || v.getId() ==R.id.constraintLayoutReg || v.getId()==R.id.logoImg){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            if(inputMethodManager.isAcceptingText()){
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        }
    }


    public void registerUser(View view){

        if(isNetworkAvailable()){
            if(validateEmail() | validateFullName() | validatePassword()){
                Log.i(TAG, "email :"+emailInput+"\nFullname :"+fullName+"\nPassword :"+password);

                textPassword.getEditText().setText("");
                textEmail.getEditText().clearFocus();
                textFullName.getEditText().clearFocus();
                textPassword.getEditText().clearFocus();

                RegUser regUser = new RegUser(emailInput,fullName,password);

                Call<ResponseBody> call = RetrofitClient.getInstance().getApi().register(regUser);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        int responseCode = response.code();
                        String toastMsg ="";
                        Log.i(TAG, String.valueOf(responseCode));
                        if(responseCode == 200){
                            toastMsg = "Registered successfully";
                            MainActivity.login(emailInput,password,activity);
                            finish();
                        } else if(responseCode==500){
                            try {
                                Gson gson = new Gson();
                                ErrorResponse errorResponse = gson.fromJson(response.errorBody().string(),ErrorResponse.class);
                                toastMsg = errorResponse.getInvalidResponse();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else {
                          toastMsg = "Something went wrong, please try again.";
                        }
                        Toast.makeText(RegisterActivity.this, toastMsg, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Log.e(TAG, "onFailure", t);
                        Log.i(TAG, t.toString());

                    }
                });
            }

        }
        else {
            Snackbar.make(view,"NO INTERNET CONNECTION", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


    }

    public boolean validateEmail(){
        emailInput = textEmail.getEditText().getText().toString().trim().toLowerCase();
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = java.util.regex.Pattern.compile(ePattern);

        if (emailInput.isEmpty()){
            textEmail.setError("Field email cannot be empty.");
            return false;
        }
        else if(!pattern.matcher(emailInput).matches()){
            textEmail.setError("Invalid email address.");
            return false;
        }
        else {
            textEmail.setError(null);
            return true;
        }
    }

    public boolean validateFullName(){
        fullName = textFullName.getEditText().getText().toString().trim();
        if (fullName.isEmpty()){
            textFullName.setError("Field email cannot be empty.");
            return false;
        }
        else{
            textFullName.setError(null);
            return true;
        }
    }

    public boolean validatePassword(){
        password = textPassword.getEditText().getText().toString().trim();
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        if (password.isEmpty()){
            textPassword.setError("Field email cannot be empty.");
            return false;

        }else if (!password.matches(pattern)){
            textPassword.setError("Password should be 8 characters long, containing at least 1 uppercase, 1 lowercase and 1 special character.");
            return false;
        }
        else {
            textPassword.setError(null);
            return true;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            registerUser(v);
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}