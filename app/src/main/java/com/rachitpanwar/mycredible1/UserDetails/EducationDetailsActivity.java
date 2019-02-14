package com.rachitpanwar.mycredible1.UserDetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.rachitpanwar.mycredible1.EducationDetails.EducationDetails;
import com.rachitpanwar.mycredible1.EducationDetails.EducationDetailsData;
import com.rachitpanwar.mycredible1.R;
import com.rachitpanwar.mycredible1.Remote.APIUtils;
import com.rachitpanwar.mycredible1.Remote.UserService;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rachitpanwar.mycredible1.UserDetails.LoginActivity.MY_PREF;

public class EducationDetailsActivity extends AppCompatActivity {

    private Button saveButton;
    private EditText organizationEditText, degreeEditText, locationEditText;
    private Spinner startYearSpinner, endYearSpinner;

    private String organization, degree, location;
    private String startYear, endYear;

    UserService userService;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_details);

        organizationEditText = findViewById(R.id.organization_edit_text);
        degreeEditText = findViewById(R.id.degree_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        startYearSpinner = findViewById(R.id.start_year_spinner);
        endYearSpinner = findViewById(R.id.end_year_spinner);

        SharedPreferences prefs = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        userId = prefs.getInt("id", 0);

//        organizationEditText.setText("VIT");
//        degreeEditText.setText("B. Tech");
//        locationEditText.setText("Vellore");

        userService = APIUtils.getUserService();

        final String isUpdate = getIntent().getStringExtra("isUpdate");

        if (isUpdate == null)
            getSupportActionBar().setTitle("Set Education Details");
        else {
            getSupportActionBar().setTitle("Edit Education Details");
            getEducationDetails();
        }

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                organization = organizationEditText.getText().toString().trim();
                degree = degreeEditText.getText().toString().trim();
                location = locationEditText.getText().toString().trim();
                startYear = startYearSpinner.getSelectedItem().toString();
                endYear = endYearSpinner.getSelectedItem().toString();

                EducationDetails educationDetails = new EducationDetails(startYear, degree, organization, location, endYear);

                if (isUpdate == null)
                    setEducationDetails(educationDetails);
                else {
                    updateEducationDetails(educationDetails);
                }
            }
        });
    }

    public void getEducationDetails()
    {
        Call<EducationDetailsData> call = userService.getEducationalDetails(userId);
        call.enqueue(new Callback<EducationDetailsData>() {
            @Override
            public void onResponse(Call<EducationDetailsData> call, Response<EducationDetailsData> response) {
                if(response.body() != null) {
                    organizationEditText.setText(response.body().getData().getOrganisation());
                    degreeEditText.setText(response.body().getData().getDegree());
                    locationEditText.setText(response.body().getData().getLocation());
                    startYearSpinner.setSelection(getIndex(startYearSpinner, response.body().getData().getStart_year()));
                    endYearSpinner.setSelection(getIndex(endYearSpinner, response.body().getData().getEnd_year()));
                } else {
                    showToast("Professional Details Response Empty", Toast.LENGTH_LONG);
                }
            }
            @Override
            public void onFailure(Call<EducationDetailsData> call, Throwable t) {
                showToast("Response Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void setEducationDetails(EducationDetails educationDetails)
    {
        Call<EducationDetailsData> call = userService.setEducationalDetails(userId, educationDetails);
        call.enqueue(new Callback<EducationDetailsData>() {
            @Override
            public void onResponse(Call<EducationDetailsData> call, Response<EducationDetailsData> response) {

                Intent intent = new Intent(EducationDetailsActivity.this, UserHomeActivity.class);
                intent.putExtra("id", userId);
                finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<EducationDetailsData> call, Throwable t) {
                showToast("Set Education details Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void updateEducationDetails(final EducationDetails educationDetails)
    {
        Call<EducationDetailsData> call = userService.updateEducationalDetails(userId, educationDetails);
        call.enqueue(new Callback<EducationDetailsData>() {
            @Override
            public void onResponse(Call<EducationDetailsData> call, Response<EducationDetailsData> response) {
                showToast("Education Details Updated", Toast.LENGTH_SHORT);
                Intent intent = new Intent(EducationDetailsActivity.this, UserHomeActivity.class);
                intent.putExtra("id", userId);
                finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<EducationDetailsData> call, Throwable t) {
                showToast("Update Education details Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    public void showToast(String msg, int length)
    {
        Toast.makeText(this, msg, length).show();
    }
}

