package com.triamatter.mynub.middleware;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.triamatter.mynub.R;
import com.triamatter.mynub.auth.LoginActivity;
import com.triamatter.mynub.core.MainActivity;
import com.triamatter.mynub.core.ProfileActivity;
import com.triamatter.mynub.model.UserProfile;

public class AuthMiddlewareActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_middleware);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");

        checkForAuthUser();
    }

    private void checkForProfileInfo()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
        if(sharedPreferences.contains("userName"))
        {

            Intent intent = new Intent(AuthMiddlewareActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else
        {
            userRef.document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    if(documentSnapshot.exists())
                    {
                        UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);

                        SharedPreferences.Editor editor = getSharedPreferences("USER", MODE_PRIVATE).edit();
                        editor.putString("userName", userProfile.getUserFullName());
                        editor.putString("userDept", userProfile.getUserDept());
                        editor.putString("userSection", userProfile.getUserSection());
                        editor.putString("userId", userProfile.getUserId());
                        editor.apply();

                        Intent intent = new Intent(AuthMiddlewareActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(AuthMiddlewareActivity.this, ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void checkForAuthUser()
    {
        if(mAuth.getCurrentUser() == null)
        {
            Intent intent = new Intent(AuthMiddlewareActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else
        {
            checkForProfileInfo();
        }
    }
}
