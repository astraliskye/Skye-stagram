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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity
{
    public final String TAG = "LoginActivity";
    EditText _etUsername;
    EditText _etPassword;
    Button _btnLogin;
    Button _btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null)
        {
            String name = ParseUser.getCurrentUser().getUsername();
            goMainActivity(name);
        }

        _etUsername = findViewById(R.id.etUsername);
        _etPassword = findViewById(R.id.etPassword);
        _btnLogin = findViewById(R.id.btnLogin);
        _btnSignup = findViewById(R.id.btnSignup);
        _btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "onClickLoginButton");

                String username = _etUsername.getText().toString();
                String password = _etPassword.getText().toString();

                loginUser(username, password);
            }
        });
        _btnSignup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "onClickSignupButton");

                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginUser(String username, String password)
    {
        final String name = username;
        Log.i(TAG, "Attempting to log in user: " + name);

        //  Preferred because login logic will execute on a background thread,
        //  and not interfere with the execution of UI logic
        //  Always want to make server requests in the background
        ParseUser.logInInBackground(username, password, new LogInCallback()
        {
            @Override
            public void done(ParseUser user, ParseException e)
            {

                if (e != null)
                {
                    Log.e(TAG, "Exception occurred in LogInCallback: " + e.getCode(), e);

                    if (e.getCode() == 101)
                    {
                        Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                    }
                    else if (e.getCode() == 201)
                    {

                        Toast.makeText(LoginActivity.this, "No password entered.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Issue with login.", Toast.LENGTH_SHORT).show();
                    }

                    return;
                }

                Log.i(TAG, "Login successful!");
                goMainActivity(name);
            }
        });
    }

    private void goMainActivity(String name)
    {
        Toast.makeText(LoginActivity.this, "Welcome " + name + "!", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        // Call finish so that the user can't press the back button to go back to the login screen
        finish();
    }
}
