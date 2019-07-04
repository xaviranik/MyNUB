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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.triamatter.mynub.R;
import com.triamatter.mynub.core.MainActivity;
import com.triamatter.mynub.core.ProfileActivity;
import com.triamatter.mynub.middleware.AuthMiddlewareActivity;

public class LoginActivity extends AppCompatActivity {

    TextView signupTextview;

    EditText editTextEmail;
    EditText editTextPassword;

    Button loginButton;

    ProgressBar loginProgressbar;

    //Firebase auth
    FirebaseAuth mAuth;;
    CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signupTextview = (TextView) findViewById(R.id.tv_signup);

        editTextEmail = (EditText) findViewById(R.id.et_email);
        editTextPassword = (EditText) findViewById(R.id.et_password);

        loginButton = (Button) findViewById(R.id.btn_login);

        loginProgressbar = (ProgressBar) findViewById(R.id.loginProgressbar);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseFirestore.getInstance().collection("Users");

        if(mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        signupTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginUser();
            }
        });
    }

    private void loginUser()
    {
        String userEmail = editTextEmail.getText().toString();
        String userPassword = editTextPassword.getText().toString();

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

        loginProgressbar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                loginProgressbar.setVisibility(View.GONE);
                loginButton.setEnabled(true);

                if(task.isSuccessful())
                {
                    Intent intent = new Intent(LoginActivity.this, AuthMiddlewareActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else
                {
                    if(task.getException() instanceof FirebaseNetworkException)
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
