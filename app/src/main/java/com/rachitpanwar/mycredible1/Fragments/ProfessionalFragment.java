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

import com.rachitpanwar.mycredible1.ProfessionalDetailsClasses.ProfessionalDetailsData;
import com.rachitpanwar.mycredible1.Remote.APIUtils;
import com.rachitpanwar.mycredible1.PersonalDetailsClasses.PersonalDetailsData;
import com.rachitpanwar.mycredible1.R;
import com.rachitpanwar.mycredible1.Remote.UserService;
import com.rachitpanwar.mycredible1.UserDetails.PersonalDetailsActivity;
import com.rachitpanwar.mycredible1.UserDetails.ProfessionalDetailsActivity;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.rachitpanwar.mycredible1.UserDetails.LoginActivity.MY_PREF;

public class ProfessionalFragment extends Fragment {

    private TextView organizationTextView, designationTextView, startDateTextView, endDateTextView;
    private Button updateDetailsButton;
    String organization, designation, startDate, endDate;

    UserService userService;

    private int userId;

    public ProfessionalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_professional, container, false);

        userService = APIUtils.getUserService();

        organizationTextView = rootView.findViewById(R.id.organization_text_view);
        designationTextView = rootView.findViewById(R.id.designation_text_view);
        startDateTextView = rootView.findViewById(R.id.start_date_text_view);
        endDateTextView = rootView.findViewById(R.id.end_date_text_view);

        updateDetailsButton = rootView.findViewById(R.id.update_details_button);

        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREF, MODE_PRIVATE);
        userId = prefs.getInt("id", 0);

        getProfessionalDetails(userId);

        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfessionalDetailsActivity.class);
                intent.putExtra("isUpdate", "true");
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void getProfessionalDetails(final int userId)
    {
        Call<ProfessionalDetailsData> call = userService.getProfessionalDetails(userId);
        call.enqueue(new Callback<ProfessionalDetailsData>() {
            @Override
            public void onResponse(Call<ProfessionalDetailsData> call, Response<ProfessionalDetailsData> response) {
                if(response.body() != null) {
                    organizationTextView.setText(response.body().getData().getOrganisation());
                    designationTextView.setText(response.body().getData().getDesignation());
                    startDateTextView.setText(response.body().getData().getStart_date());
                    endDateTextView.setText(response.body().getData().getEnd_date());
                }
            }

            @Override
            public void onFailure(Call<ProfessionalDetailsData> call, Throwable t) {
                showToast("Get professional details failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void showToast(String msg, int length)
    {
        Toast.makeText(getContext(), msg, length).show();
    }

}
