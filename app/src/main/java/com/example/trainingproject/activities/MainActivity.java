package com.example.trainingproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.trainingproject.models.ErrorResponse;
import com.example.trainingproject.models.LoginInfo;
import com.example.trainingproject.models.LoginUser;
import com.example.trainingproject.R;
import com.example.trainingproject.httpRequest.RetrofitClient;
import com.example.trainingproject.models.UserProfile;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {



    private static final String TAG = "MainActivity" ;
    View bgMain;
    View layout;
    ViewFlipper viewFlipper;
    TextInputLayout userTextEmail;
    TextInputLayout userPassword;
    String email;
    String password;
    static SharedPreferences sharedPreferences;
    public static boolean isLoggedIn = false;
    public static UserProfile userProfile;
    static Gson gson;
    static Activity activity;

    public void registerScreen(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        bgMain = findViewById(R.id.bgMain);
        layout = findViewById(R.id.constraintLayoutLogin);
        userTextEmail = findViewById(R.id.text_input_email1);
        userPassword = findViewById(R.id.text_input_pasword1);
        gson = new Gson();
        sharedPreferences = getSharedPreferences("com.example.trainingproject", MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);
        String myProfile = sharedPreferences.getString("UserProfile","");
        userProfile = gson.fromJson(myProfile, UserProfile.class);
        Log.i(TAG, String.valueOf(isLoggedIn));
        if(isLoggedIn){
            goToHomeScreen();
        }

        bgMain.setOnClickListener(this);
        layout.setOnClickListener(this);
        userPassword.getEditText().setOnKeyListener(this);
        viewFlipper = findViewById(R.id.v_fliper);
        int[] bgImages = new int[]{R.drawable.bg1,R.drawable.bg};

        for(int image :bgImages){
            fliperImages(image);
        }



    }

    public void fliperImages(int image){

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(image);

        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);

        viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

    }

    public boolean validateEmail(){
        email = userTextEmail.getEditText().getText().toString().trim().toLowerCase();
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = java.util.regex.Pattern.compile(ePattern);

        if (email.isEmpty()){
            userTextEmail.setError("Field email cannot be empty.");
            return false;
        }
        else if(!pattern.matcher(email).matches()){
            userTextEmail.setError("Invalid email address.");
            return false;
        }
        else {
            userTextEmail.setError(null);
            return true;
        }

    }

    public boolean validatePassword(){
        password = userPassword.getEditText().getText().toString().trim();
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        if (password.isEmpty()){
            userPassword.setError("Field email cannot be empty.");
            return false;

        }else if (!password.matches(pattern)){
            userPassword.setError("Password should be 8 characters long, containing at least 1 uppercase, 1 lowercase and 1 special character.");
            return false;
        }
        else {
            userPassword.setError(null);
            return true;
        }

    }

    public void loginUser(View view){

        if(isNetworkAvailable()){
            if (validateEmail() && validatePassword()){

                userPassword.getEditText().setText("");
                userTextEmail.getEditText().clearFocus();
                userPassword.getEditText().clearFocus();

                login(email, password, activity);
            }


        } else {
            Log.i(TAG, "No internet");
            Snackbar.make(view,"NO INTERNET CONNECTION", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public static void login(String emailID, String loginPassword, final Activity activity){

        LoginUser loginUser = new LoginUser(emailID, loginPassword);

        Call<LoginInfo> loginCall = RetrofitClient.getInstance().getApi().login(loginUser);
        loginCall.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Call<LoginInfo> call, Response<LoginInfo> response) {

                int responseCode = response.code();

                String toastMsg = null;
                Log.i(TAG, String.valueOf(responseCode));
                if(responseCode == 200) {

                    isLoggedIn = true;
                    sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply();
                    userProfile = response.body().getUser();
                    String profile = gson.toJson(userProfile);
                    sharedPreferences.edit().putString("UserProfile", profile).apply();
                    toastMsg = "Welcome "+userProfile.getFullname();

                    Log.i(TAG, userProfile.toString());
                    goToHomeScreen();

                }else if(responseCode==500){
                    try {
                        ErrorResponse errorResponse = gson.fromJson(response.errorBody().string(),ErrorResponse.class);
                        toastMsg = errorResponse.getInvalidResponse();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }else {
                    toastMsg = "Something went wrong, please try again.";
                }
                Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<LoginInfo> call, Throwable t) {
                Log.e(TAG, "cause ", t.getCause());
                Log.e("localized msg", t.getLocalizedMessage(),t);
                Log.e("msg", t.getMessage(),t );
                Log.e(TAG, t.toString(), t);
            }
        });


    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.bgMain || v.getId() ==R.id.constraintLayoutLogin){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            if(inputMethodManager.isAcceptingText()){
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            loginUser(v);
        }
        return false;
    }

    public static void goToHomeScreen(){
        Intent intent = new Intent(activity, HomeScreenActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}