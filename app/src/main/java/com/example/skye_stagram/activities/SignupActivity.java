package com.example.skye_stagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.skye_stagram.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity
{
    public final String TAG = "Signup Activity";

    EditText _etUsername;
    EditText _etPassword;
    EditText _etEmail;
    Button _btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (ParseUser.getCurrentUser() != null)
        {
            String name = ParseUser.getCurrentUser().getUsername();
            goMainActivity(name);
        }

        _etUsername = findViewById(R.id.etUsername);
        _etPassword = findViewById(R.id.etPassword);
        _etEmail = findViewById(R.id.etEmail);
        _btnSignup = findViewById(R.id.btnSignup);
        _btnSignup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "onClickSignupButton");

                String username = _etUsername.getText().toString();
                String password = _etPassword.getText().toString();
                String email = _etEmail.getText().toString();

                signupUser(username, password, email);
            }
        });
    }

    private void signupUser(String username, String password, String email)
    {
        final String name = username;
        Log.i(TAG, "Attempting to sign up user: " + name);

        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        // Execute signup logic in background so it doesn't interfere with
        // UI logic
        user.signUpInBackground(new SignUpCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (e != null)
                {
                    Log.e(TAG, "Exception occurred in SingUpCallback: " + e.getCode(), e);

                    if (e.getCode() == 202)
                    {
                        Toast.makeText(SignupActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(SignupActivity.this, "Issue with signup.", Toast.LENGTH_SHORT).show();
                    }

                    return;
                }

                Log.i(TAG, "Signup successful!");
                goMainActivity(name);
            }
        });
    }

    private void goMainActivity(String name)
    {
        Toast.makeText(SignupActivity.this, "Welcome " + name + "!", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        // Call finish so that the user can't press the back button to go back to the login screen
        finish();
    }
}
