package com.rachitpanwar.mycredible1.UserDetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import com.rachitpanwar.mycredible1.LoginClasses.LoginSignupData;
import com.rachitpanwar.mycredible1.LoginClasses.ServerTest;
import com.rachitpanwar.mycredible1.LoginClasses.User;
import com.rachitpanwar.mycredible1.R;
import com.rachitpanwar.mycredible1.Remote.APIUtils;
import com.rachitpanwar.mycredible1.Remote.UserService;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))";
    //private static final String url2 = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";

    public static final String MY_PREF = "MyPreference";

    private String userEmail;
    private int userId;

    private String email, password;

    UserService userService;

    Button loginButton, signUpButton;
    Button loginLinkedinButton;

    EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.signup_button);
        loginLinkedinButton = findViewById(R.id.login_linkedin_button);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);

//        emailEditText.setText("utkarsh@gmail.com");
//        passwordEditText.setText("123456");

        userService = APIUtils.getUserService();

        serverTest();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();

                if(email != null && password != null)
                {
                    User user = new User(email, password);
                    loginUser(user);
                }
                else
                {
                    showToast("Enter the details", Toast.LENGTH_SHORT);
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();


                if(email != null && password != null)
                {
                    User user = new User(email, password);
                    signUpUser(user, email);
                }
                else
                {
                    showToast("Enter the details", Toast.LENGTH_SHORT);
                }
            }
        });

        //Linkedin Sign in
        loginLinkedinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LISessionManager.getInstance(getApplicationContext()).init(LoginActivity.this, buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
//                        showToast("Auth Success", Toast.LENGTH_LONG);
                        linkedinHelperApi();
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        Toast.makeText(getApplicationContext(), R.string.toast_login_failed, Toast.LENGTH_SHORT).show();
                    }
                }, true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }

    public void linkedinHelperApi()
    {
        APIHelper apiHelper = APIHelper.getInstance(this);
        //showToast(apiHelper.toString(), Toast.LENGTH_LONG);
        apiHelper.getRequest(LoginActivity.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                try
                {
                    getFinalResult(apiResponse.getResponseDataAsJson());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void onApiError(LIApiError LIApiError) {
                showToast("onAPIFailed : " + LIApiError.toString(), Toast.LENGTH_LONG);
                showToast("Fetch profile Error : " + LIApiError.getLocalizedMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    public void getFinalResult(JSONObject jsonObject)
    {
        try
        {
            String firstName = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");

            showToast(firstName + "\n" + lastName + "\n", Toast.LENGTH_LONG);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Data not fetched", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void serverTest()
    {
        Call<ServerTest> call = userService.getServerStatus();
        call.enqueue(new Callback<ServerTest>() {
            @Override
            public void onResponse(Call<ServerTest> call, Response<ServerTest> response) {
                showToast("ServerStatus : " + response.body().getStatus(), Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(Call<ServerTest> call, Throwable t) {
                showToast("ServerStatus : Down : " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }


    public void loginUser(final User user)
    {
        Call<LoginSignupData> call = userService.getUser(user);
        call.enqueue(new Callback<LoginSignupData>() {
            @Override
            public void onResponse(Call<LoginSignupData> call, Response<LoginSignupData> response) {
                if(response.body().getData() == null )
                {
                    showToast(getString(R.string.toast_no_such_user), Toast.LENGTH_SHORT);
                    return;
                }

                userId = Integer.parseInt(response.body().getData().getId());
                userEmail = response.body().getData().getEmail();

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
                editor.putString("email", userEmail);
                editor.putInt("id", userId);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                intent.putExtra("id", userId);

                startActivity(intent);
            }

            @Override
            public void onFailure(Call<LoginSignupData> call, Throwable t) {
                showToast("Login Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void signUpUser(final User user, final String email)
    {
        Call<LoginSignupData> call = userService.addUser(user);
        call.enqueue(new Callback<LoginSignupData>() {
            @Override
            public void onResponse(Call<LoginSignupData> call, Response<LoginSignupData> response) {
                userId = Integer.parseInt(response.body().getData().getId());
                userEmail = response.body().getData().getEmail();

                showToast("Thanks for joining us.\nYour unique ID is: " + userId + ".\nPlease fill the details to continue", Toast.LENGTH_LONG);

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
                editor.putString("email", userEmail);
                editor.putInt("id", userId);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, PersonalDetailsActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<LoginSignupData> call, Throwable t) {
                showToast("Signup Failed: " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void showToast(String msg, int length)
    {
        Toast.makeText(this, msg, length).show();
    }
}
