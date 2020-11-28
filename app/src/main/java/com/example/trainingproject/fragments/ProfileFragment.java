package com.example.trainingproject.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainingproject.BuildConfig;
import com.example.trainingproject.activities.MainActivity;
import com.example.trainingproject.models.ProfilePicture;
import com.example.trainingproject.utils.CircleTransform;
import com.example.trainingproject.models.MovieRating;
import com.example.trainingproject.R;
import com.example.trainingproject.httpRequest.RetrofitClient;
import com.example.trainingproject.adapters.UserReviewsAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    TextView userFullname;
    TextView userEmail;
    TextView refresh;
    TextView checkFavourites;
    TextView errorMessage;
    TextView noReviews;

    ImageView profilePicture;
    UserReviewsAdapter userReviewsAdapter;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    FloatingActionButton logoutButton;
    ShimmerFrameLayout userReviewShimmer;
    String path;
    View somethingWentWrong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences("com.example.trainingproject", Context.MODE_PRIVATE);

        userReviewShimmer = view.findViewById(R.id.userReviewShimmer);
        userReviewShimmer.startShimmer();
        userFullname = view.findViewById(R.id.userFullname);
        userEmail = view.findViewById(R.id.userEmail);
        recyclerView = view.findViewById(R.id.userReviewsRecyclerView);
        logoutButton = view.findViewById(R.id.logout);
        profilePicture = view.findViewById(R.id.profileImg);
        somethingWentWrong = view.findViewById(R.id.somethingWentWrongg);
        errorMessage = view.findViewById(R.id.errorMessage);
        refresh = view.findViewById(R.id.refresh);
        checkFavourites = view.findViewById(R.id.checkFavvourites);
        noReviews = view.findViewById(R.id.noReviews);

        Picasso.with(getContext()).load(R.drawable.person_placeholder).transform(new CircleTransform()).into(profilePicture);
        logoutButton.setOnClickListener(this);
        profilePicture.setOnClickListener(this);
        refresh.setOnClickListener(this);
        checkFavourites.setOnClickListener(this);

        userFullname.setText("Hello " + MainActivity.userProfile.getFullname() + ",");
        userEmail.setText(MainActivity.userProfile.getEmail());

        userReviewsAdapter = new UserReviewsAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setAdapter(userReviewsAdapter);
        recyclerView.setLayoutManager(layoutManager);

        refresh();



        return view;
    }

    public void refresh(){
        Call<List<MovieRating>> userRatingsCall = RetrofitClient.getInstance().getApi().getUserMovieRatings(MainActivity.userProfile.getUserid());
        userRatingsCall.enqueue(new Callback<List<MovieRating>>() {
            @Override
            public void onResponse(Call<List<MovieRating>> call, Response<List<MovieRating>> response) {


                if (response.isSuccessful()) {

                    List<MovieRating> userMovieRating = response.body();
                    if(userMovieRating.isEmpty()){
                        noReviews.setVisibility(View.VISIBLE);
                    }else {
                        noReviews.setVisibility(View.GONE);
                    }
                    userReviewShimmer.setVisibility(View.GONE);
                    userReviewShimmer.stopShimmer();
                    userReviewsAdapter.setData(userMovieRating);
                    somethingWentWrong.setVisibility(View.GONE);

                }else {
                    userReviewShimmer.setVisibility(View.GONE);
                    userReviewShimmer.stopShimmer();
                    somethingWentWrong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<MovieRating>> call, Throwable t) {
                somethingWentWrong.setVisibility(View.VISIBLE);
                errorMessage.setText("Seems like you are offline");
                userReviewShimmer.setVisibility(View.GONE);
                userReviewShimmer.stopShimmer();

            }
        });
    }

    public void logout() {
        MainActivity.isLoggedIn = false;
        sharedPreferences.edit().putBoolean("isLoggedIn", MainActivity.isLoggedIn).apply();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.getActivity().finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.logout:
                logout();
            break;

            case R.id.profileImg:

                Log.i("Profile", "onClick: ");
                if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},2);

                }else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
                break;

            case R.id.refresh:
                refresh();
                break;

            case R.id.checkFavvourites:
                Fragment favouritesFragment = new FavouritesFragment();
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.cointainer_fragment,favouritesFragment).commit();
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_nav);
                bottomNav.setSelectedItemId(R.id.favourites);

        }

    }

    public String getPathFromUri(Uri uri){
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri,proj,null,null,null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            path = null;
            File profilePic = null;
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
/*
                //File file = new File("files","profile");
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = null;
                file = File.createTempFile("image", ".jpg", storageDir);
                //path = img.getAbsolutePath();

*/              File file = new File(getContext().getExternalCacheDir(), File.separator +"profile.jpg");
                FileOutputStream fOut = new FileOutputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                //file.setReadable(true, false);
                Uri photoURI = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID +".provider", file);
                path = photoURI.getPath();
                profilePic = new File(path);
                
                profilePicture.setImageBitmap(bitmap);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // profilePicture.setImageURI(selectedImg);



            //Uri uri = FileProvider.getUriForFile(getContext(),getContext().getPackageName()+".provider",newFile);
          //Bitmap selectedImage = BitmapFactory.decodeFile(uri.getPath());
           // profilePicture.setImageBitmap(selectedImage);

            /*
            path = selectedImg.getPath();
            Log.i("Path", selectedImg.getPath());
            *//*
            File newFile = new File(path);
           File image = new File(getContext().getFilesDir()+File.separator+System.currentTimeMillis());
            try {
                FileReader fileReader = new FileReader(newFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine())!= null){
                    stringBuilder.append(line);


                }
                FileWriter fileWriter = new FileWriter(image);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(stringBuilder.toString(),0,stringBuilder.length());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
*/
/*
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File img = null;
            try {
                img = File.createTempFile("image", ".jpg", storageDir);
                path = img.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), profilePic);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", profilePic.getName(), requestBody);

            Call<ProfilePicture> uploadCall = RetrofitClient.getInstance().getApi().updateProfile(body);
            uploadCall.enqueue(new Callback<ProfilePicture>() {
                @Override
                public void onResponse(Call<ProfilePicture> call, Response<ProfilePicture> response) {
                    Log.i("upload", String.valueOf(response.code()));
                }

                @Override
                public void onFailure(Call<ProfilePicture> call, Throwable t) {
                   Log.e("error", "onFailure: ", t);
                }
            });




        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if(requestCode ==2 ){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }else {
                Toast.makeText(getContext(), "Please grant permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

