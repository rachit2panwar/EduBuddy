package com.rachitpanwar.mycredible1.UserDetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rachitpanwar.mycredible1.PersonalDetailsClasses.PersonalDetails;
import com.rachitpanwar.mycredible1.PersonalDetailsClasses.PersonalDetailsData;
import com.rachitpanwar.mycredible1.ProfilePicClasses.Photo;
import com.rachitpanwar.mycredible1.R;
import com.rachitpanwar.mycredible1.Remote.APIUtils;
import com.rachitpanwar.mycredible1.Remote.UserService;
import com.rachitpanwar.mycredible1.UserDetails.StatusMessage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rachitpanwar.mycredible1.UserDetails.LoginActivity.MY_PREF;


public class PersonalDetailsActivity extends AppCompatActivity {

    final private String imageUri = "content://media/internal/images/media";
    final private String imageUrl = "http://139.59.65.145:9090/user/personaldetail/profilepic/";

    private Button saveButton;
    private EditText nameEditText, emailEditText, mobileEditText, locationEditText, linksEditText, skillsEditText;
    private ImageView userProfilePic;

    private Bitmap profilePicBitmap;
    private ByteArrayOutputStream baos;
    private byte[] imageByteArray;
    private String encodedImage;

    private String name, email, mobile, location, links, skills;

    private int userId;

    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        nameEditText = findViewById(R.id.full_name_edit_text);
//        emailEditText = findViewById(R.id.email_edit_text);
        mobileEditText = findViewById(R.id.mobile_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        linksEditText = findViewById(R.id.links_edit_text);
        skillsEditText = findViewById(R.id.skills_edit_text);

        userProfilePic = findViewById(R.id.user_profile_pic);
        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse(imageUri));
                startActivityForResult(intent, 1);
            }
        });

//        nameEditText.setText("utkarsh");
//        emailEditText.setText("utka@gmail.com");
//        mobileEditText.setText("9876543210");
//        locationEditText.setText("Vellore");
//        linksEditText.setText("www.google.com");
//        skillsEditText.setText("Android app dev");

        userService = APIUtils.getUserService();

        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        userId = prefs.getInt("id", 0);

        final String isUpdate = getIntent().getStringExtra("isUpdate");

        if (isUpdate == null)
            getSupportActionBar().setTitle("Set Personal Details");
        else {
            getSupportActionBar().setTitle("Edit Personal Details");
            getPersonalDetails();
            getProfilePic();
        }

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = nameEditText.getText().toString().trim();
                email = "";//emailEditText.getText().toString().trim();
                mobile = mobileEditText.getText().toString().trim();
                location = locationEditText.getText().toString().trim();
                links = linksEditText.getText().toString().trim();
                skills = skillsEditText.getText().toString().trim();

                PersonalDetails personalDetails = new PersonalDetails(skills, mobile, name, links, location, email);

                if (isUpdate == null)
                    setPersonalDetails(personalDetails);
                else {
                    updatePersonalDetails(personalDetails);

                }
                setProfilePic(encodedImage);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1)
        {
            Uri uri = data.getData();
            userProfilePic.setImageURI(uri);

            String picturePath = getPath(this, uri);

            profilePicBitmap = BitmapFactory.decodeFile(picturePath);
            baos = new ByteArrayOutputStream();
            profilePicBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageByteArray = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        }
    }

    public void getPersonalDetails()
    {
        Call<PersonalDetailsData> call = userService.getPersonalDetails(userId);
        call.enqueue(new Callback<PersonalDetailsData>() {
            @Override
            public void onResponse(Call<PersonalDetailsData> call, Response<PersonalDetailsData> response) {
                if(response.body() != null) {
                    nameEditText.setText(response.body().getData().getName());
                    //emailEditText.setText(response.body().getData().getEmail());
                    mobileEditText.setText(response.body().getData().getMobile_no());
                    locationEditText.setText(response.body().getData().getLocation());
                    linksEditText.setText(response.body().getData().getLinks());
                    skillsEditText.setText(response.body().getData().getSkills());
                } else {
                    showToast("Personal Details Response Empty", Toast.LENGTH_LONG);
                }
            }
            @Override
            public void onFailure(Call<PersonalDetailsData> call, Throwable t) {
                showToast("Response Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void setPersonalDetails(final PersonalDetails personalDetails)
    {
        Call<PersonalDetailsData> call = userService.setPersonalDetails(userId, personalDetails);
        call.enqueue(new Callback<PersonalDetailsData>() {
            @Override
            public void onResponse(Call<PersonalDetailsData> call, Response<PersonalDetailsData> response) {
                PersonalDetailsData personalDetailsData = new PersonalDetailsData();
                personalDetailsData.setData(response.body().getData());

                Intent intent = new Intent(PersonalDetailsActivity.this, ProfessionalDetailsActivity.class);
                intent.putExtra("id", userId);
                finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<PersonalDetailsData> call, Throwable t) {
                showToast("Set Personal details Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void updatePersonalDetails(final PersonalDetails personalDetails)
    {
        Call<PersonalDetailsData> call = userService.updatePersonalDetails(userId, personalDetails);
        call.enqueue(new Callback<PersonalDetailsData>() {
            @Override
            public void onResponse(Call<PersonalDetailsData> call, Response<PersonalDetailsData> response) {
                showToast("Personal Details Updated", Toast.LENGTH_SHORT);
                Intent intent = new Intent(PersonalDetailsActivity.this, UserHomeActivity.class);
                intent.putExtra("id", userId);
                finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<PersonalDetailsData> call, Throwable t) {
                showToast("Update Personal details Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void getProfilePic()
    {
        Uri uri = Uri.parse(imageUrl + userId);
        Picasso.with(getApplicationContext()).load(uri).into(userProfilePic);
//        Call<byte[]> call = userService.getProfilePic(userId);
//        showToast(userId + "", Toast.LENGTH_SHORT);
//        call.enqueue(new Callback<byte[]>() {
//            @Override
//            public void onResponse(Call<byte[]> call, Response<byte[]> response) {
//                byte[] decodedString = Base64.decode(response.body(), Base64.DEFAULT);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                userProfilePic.setImageBitmap(decodedByte);
//                showToast(String.valueOf(decodedString), Toast.LENGTH_SHORT);
//            }
//
//            @Override
//            public void onFailure(Call<byte[]> call, Throwable t) {
//                showToast("Get Profile Pic Failed: \n" + t.getMessage(), Toast.LENGTH_LONG);
//                Log.i("PersonalDetailsActivity", t.getMessage());
//            }
//        });
    }

    public void setProfilePic(String encodedImage)
    {
        Photo photo = new Photo(String.valueOf(userId), encodedImage);
        Call<com.rachitpanwar.mycredible1.ProfilePicClasses.StatusMessage> call = userService.setProfilePic(photo);
        call.enqueue(new Callback<com.rachitpanwar.mycredible1.ProfilePicClasses.StatusMessage>() {
            @Override
            public void onResponse(Call<com.rachitpanwar.mycredible1.ProfilePicClasses.StatusMessage> call, Response<com.rachitpanwar.mycredible1.ProfilePicClasses.StatusMessage> response) {
                showToast("Profile pic set successfully", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(Call<com.rachitpanwar.mycredible1.ProfilePicClasses.StatusMessage> call, Throwable t) {
                showToast("Update Personal details Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }

        });
    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }


    public void showToast(String msg, int length)
    {
        Toast.makeText(this, msg, length).show();
    }
}
