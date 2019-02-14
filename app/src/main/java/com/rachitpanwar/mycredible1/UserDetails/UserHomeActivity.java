package com.rachitpanwar.mycredible1.UserDetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rachitpanwar.mycredible1.Classes.SimpleFragmentPagerAdapter;
import com.rachitpanwar.mycredible1.PersonalDetailsClasses.PersonalDetailsData;
import com.rachitpanwar.mycredible1.ProfessionalDetailsClasses.ProfessionalDetailsData;
import com.rachitpanwar.mycredible1.ProfilePicClasses.StatusMessage;
import com.rachitpanwar.mycredible1.R;
import com.rachitpanwar.mycredible1.Remote.APIUtils;
import com.rachitpanwar.mycredible1.Remote.UserService;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rachitpanwar.mycredible1.UserDetails.LoginActivity.MY_PREF;

public class UserHomeActivity extends AppCompatActivity {

    final private String imageUrl = "http://139.59.65.145:9090/user/personaldetail/profilepic/";

    private ViewPager mViewPager;

    private TextView nameTextView, emailTextView, organizationTextView, locationTextView;

    private ImageView userProfilePic;

    private String name, email, organization, location;

    private UserService userService;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        nameTextView = findViewById(R.id.name_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        organizationTextView = findViewById(R.id.organization_text_view);
        locationTextView = findViewById(R.id.location_text_view);

        userProfilePic = findViewById(R.id.user_profile_pic);

        name = email = organization = location = "";

        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        email = prefs.getString("email", "-");
        userId = prefs.getInt("id", 0);

        emailTextView.setText(email);

        SimpleFragmentPagerAdapter simpleFragmentPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.fragment_container);
        mViewPager.setAdapter(simpleFragmentPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        userService = APIUtils.getUserService();

        getProfilePic();
        getPersonalDetails();
        getProfessionalDetails();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out_menu:
                //AuthUI.getInstance().signOut(this);
                showToast("Signed out Successfully!", Toast.LENGTH_SHORT);
                finish();
                startActivity(new Intent(UserHomeActivity.this, LoginActivity.class));
                return true;
            case R.id.delete_account_menu:
                deleteAccount();
                finish();
                Intent intent = new Intent(UserHomeActivity.this, LoginActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getPersonalDetails()
    {
        Call<PersonalDetailsData> call = userService.getPersonalDetails(userId);
        call.enqueue(new Callback<PersonalDetailsData>() {
            @Override
            public void onResponse(Call<PersonalDetailsData> call, Response<PersonalDetailsData> response) {
                if(response.body() != null) {
                    name = response.body().getData().getName();
                    location = response.body().getData().getLocation();
                    nameTextView.setText(name);
                    locationTextView.setText(location);
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

    public void getProfessionalDetails()
    {
        Call<ProfessionalDetailsData> call = userService.getProfessionalDetails(userId);
        call.enqueue(new Callback<ProfessionalDetailsData>() {
            @Override
            public void onResponse(Call<ProfessionalDetailsData> call, Response<ProfessionalDetailsData> response) {
                if(response.body() != null) {
                    organization = response.body().getData().getOrganisation();
                    organizationTextView.setText(organization);
                } else {
                    showToast("Professional Details Response Empty", Toast.LENGTH_LONG);
                }
            }
            @Override
            public void onFailure(Call<ProfessionalDetailsData> call, Throwable t) {
                showToast("Response Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void deleteAccount()
    {
        deleteProfessionalData();
        deleteEducationData();
        showToast("Account deleted successfully!", Toast.LENGTH_SHORT);
        showToast(getString(R.string.login_or_signup_message), Toast.LENGTH_SHORT);
    }

    public void deleteProfessionalData()
    {
        Call<StatusMessage> call = userService.deleteProfessionalDetails(userId);
        call.enqueue(new Callback<StatusMessage>() {
            @Override
            public void onResponse(Call<StatusMessage> call, Response<StatusMessage> response) {

            }
            @Override
            public void onFailure(Call<StatusMessage> call, Throwable t) {
                showToast("Delete Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void deleteEducationData()
    {
        Call<StatusMessage> call = userService.deleteEducationalDetails(userId);
        call.enqueue(new Callback<StatusMessage>() {
            @Override
            public void onResponse(Call<StatusMessage> call, Response<StatusMessage> response) {

            }
            @Override
            public void onFailure(Call<StatusMessage> call, Throwable t) {
                showToast("Delete Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void getProfilePic()
    {
        Uri uri = Uri.parse(imageUrl + userId);
        Picasso.with(getApplicationContext()).load(uri).into(userProfilePic);
    }

    public void showToast(String msg, int length)
    {
        Toast.makeText(this, msg, length).show();
    }

}
