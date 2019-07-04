package com.triamatter.mynub.auth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.triamatter.mynub.R;
import com.triamatter.mynub.core.MainActivity;
import com.triamatter.mynub.core.ProfileActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextConfirmPassword;

    ProgressBar signupProgressbar;

    Button signupButton;

    TextView loginTextView;

    //Firebase Auth
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.et_email);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        editTextConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);

        signupButton = (Button) findViewById(R.id.btn_login);

        loginTextView = (TextView) findViewById(R.id.tv_login);

        signupProgressbar = (ProgressBar) findViewById(R.id.signupProgressbar);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerUser();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void registerUser()
    {
        String userEmail = editTextEmail.getText().toString();
        String userPassword = editTextPassword.getText().toString();
        String userConfirmPassword = editTextConfirmPassword.getText().toString();

        //Validation
        if(userEmail.isEmpty())
        {
            editTextEmail.setError("Email can't be empty!");
            editTextEmail.requestFocus();
            return;
        }

        if(userPassword.isEmpty())
        {
            editTextPassword.setError("Password can't be empty!");
            editTextPassword.requestFocus();
            return;
        }

        if(userConfirmPassword.isEmpty())
        {
            editTextConfirmPassword.setError("Please re-enter the password!");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if(!userPassword.equals(userConfirmPassword))
        {
            editTextPassword.setError("Password should be same!");
            editTextPassword.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            editTextEmail.setError("Please enter a valid Email address!");
            editTextEmail.requestFocus();
            return;
        }

        if(userPassword.length() < 6)
        {
            editTextPassword.setError("Minimum password length should be six characters!");
            editTextPassword.requestFocus();
            return;
        }

        signupProgressbar.setVisibility(View.VISIBLE);
        signupButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                signupProgressbar.setVisibility(View.GONE);
                signupButton.setEnabled(true);

                if(task.isSuccessful())
                {
                    Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(), "This Email is already in use!", Toast.LENGTH_SHORT).show();
                    }
                    else if(task.getException() instanceof FirebaseNetworkException)
                    {
                        Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }
}
