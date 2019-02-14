package com.rachitpanwar.mycredible1.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rachitpanwar.mycredible1.EducationDetails.EducationDetailsData;
import com.rachitpanwar.mycredible1.R;
import com.rachitpanwar.mycredible1.Remote.APIUtils;
import com.rachitpanwar.mycredible1.Remote.UserService;
import com.rachitpanwar.mycredible1.UserDetails.EducationDetailsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.rachitpanwar.mycredible1.UserDetails.LoginActivity.MY_PREF;

public class EducationFragment extends Fragment {

    private TextView organizationTextView, degreeTextView, locationTextView;
    private Button updateDetailsButton;

    String organization, degree, location;

    UserService userService;

    private int userId;

    public EducationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_education, container, false);

        userService = APIUtils.getUserService();

        organizationTextView = rootView.findViewById(R.id.organization_text_view);
        degreeTextView = rootView.findViewById(R.id.degree_text_view);
        locationTextView = rootView.findViewById(R.id.location_text_view);

        updateDetailsButton = rootView.findViewById(R.id.update_details_button);

        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREF, MODE_PRIVATE);
        userId = prefs.getInt("id", 0);

        getEducationDetails(userId);

        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EducationDetailsActivity.class);
                intent.putExtra("isUpdate", "true");
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void getEducationDetails(final int userId)
    {
        Call<EducationDetailsData> call = userService.getEducationalDetails(userId);
        call.enqueue(new Callback<EducationDetailsData>() {
            @Override
            public void onResponse(Call<EducationDetailsData> call, Response<EducationDetailsData> response) {
                if(response.body() != null) {
                    organizationTextView.setText(response.body().getData().getOrganisation());
                    degreeTextView.setText(response.body().getData().getDegree());
                    locationTextView.setText(response.body().getData().getLocation());
                }
            }

            @Override
            public void onFailure(Call<EducationDetailsData> call, Throwable t) {
                showToast("Get education details filed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void showToast(String msg, int length)
    {
        Toast.makeText(getContext(), msg, length).show();
    }

}
