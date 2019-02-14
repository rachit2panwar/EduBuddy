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

import com.rachitpanwar.mycredible1.Remote.APIUtils;
import com.rachitpanwar.mycredible1.PersonalDetailsClasses.PersonalDetailsData;
import com.rachitpanwar.mycredible1.R;
import com.rachitpanwar.mycredible1.Remote.UserService;
import com.rachitpanwar.mycredible1.UserDetails.PersonalDetailsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.rachitpanwar.mycredible1.UserDetails.LoginActivity.MY_PREF;

public class PersonalFragment extends Fragment {

    private TextView nameTextView, mobileTextView, locationTextView, linksTextView, skillsTextView;
    private Button updateDetailsButton;

    private int userId;

    UserService userService;

    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_personal, container, false);

        userService = APIUtils.getUserService();

        nameTextView = rootView.findViewById(R.id.name_text_view);
        mobileTextView = rootView.findViewById(R.id.mobile_text_view);
        locationTextView = rootView.findViewById(R.id.location_text_view);
        linksTextView = rootView.findViewById(R.id.links_text_view);
        skillsTextView = rootView.findViewById(R.id.skills_text_view);

        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREF, MODE_PRIVATE);
        userId = prefs.getInt("id", 0);

        getPersonalDetails(userId);

        updateDetailsButton = rootView.findViewById(R.id.update_details_button);

        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PersonalDetailsActivity.class);
                intent.putExtra("isUpdate", "true");
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void getPersonalDetails(final int userId)
    {
        Call<PersonalDetailsData> call = userService.getPersonalDetails(userId);
        call.enqueue(new Callback<PersonalDetailsData>() {
            @Override
            public void onResponse(Call<PersonalDetailsData> call, Response<PersonalDetailsData> response) {
                if(response.body() != null) {
                    nameTextView.setText(response.body().getData().getName());
                    mobileTextView.setText(response.body().getData().getMobile_no());
                    locationTextView.setText(response.body().getData().getLocation());
                    linksTextView.setText(response.body().getData().getLinks());
                    skillsTextView.setText(response.body().getData().getSkills());
                }
            }

            @Override
            public void onFailure(Call<PersonalDetailsData> call, Throwable t) {
                showToast("Get personal details failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void showToast(String msg, int length)
    {
        Toast.makeText(getContext(), msg, length).show();
    }

}
