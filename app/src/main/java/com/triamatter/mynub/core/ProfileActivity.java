package com.triamatter.mynub.core;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.triamatter.mynub.R;
import com.triamatter.mynub.middleware.AuthMiddlewareActivity;
import com.triamatter.mynub.model.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner deptSpinner;
    Spinner sectionSpinner;

    EditText editTextFullName;
    EditText editTextID;

    Button nextButton;

    //Firestore database
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser mUser;

    CollectionReference userRef;

    String userId, userFullName, userDept, userSection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        deptSpinner = (Spinner) findViewById(R.id.spinner_dept);
        sectionSpinner = (Spinner) findViewById(R.id.spinner_section);

        editTextFullName = (EditText) findViewById(R.id.et_fullname);
        editTextID = (EditText) findViewById(R.id.et_id);

        nextButton = (Button) findViewById(R.id.btn_next);

        //Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        userRef = db.collection("Users");

        deptSpinner.setOnItemSelectedListener(this);
        sectionSpinner.setOnItemSelectedListener(this);

        setUpSpinners();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                saveUserProfile();
            }
        });
    }

    private void saveUserProfile()
    {
        userFullName = editTextFullName.getText().toString().trim();
        userId = editTextID.getText().toString().trim();

        //Validation
        if(userFullName.isEmpty())
        {
            editTextFullName.setError("Name can't be empty!");
            editTextFullName.requestFocus();
            return;
        }

        if(userId.isEmpty())
        {
            editTextID.setError("ID can't be empty!");
            editTextID.requestFocus();
            return;
        }

        if(userId.length() < 9)
        {
            editTextID.setError("ID length should be nine! eg: 160200000");
            editTextID.requestFocus();
            return;
        }


        userRef.whereEqualTo("userId", userId).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(queryDocumentSnapshots.isEmpty())
                {
                    //Updating profile
                    UserProfile userProfile = new UserProfile(userFullName, userId, userDept, userSection);

                    userRef.document(mUser.getUid()).set(userProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(getApplicationContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileActivity.this, AuthMiddlewareActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "An account is already in use with your provided ID!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void setUpSpinners()
    {
        //Department
        List<String> departments = new ArrayList<String>();
        departments.add("Computer Science & Engineering");
        departments.add("Electrical and Electronic Engineering");
        departments.add("Textile Engineering");
        departments.add("Business Administration");
        departments.add("Law");
        departments.add("Bachelor of Pharmacy");

        ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, departments);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptSpinner.setAdapter(deptAdapter);

        //Sections
        List<String> sections = new ArrayList<String>();
        sections.add("A");
        sections.add("B");
        sections.add("C");
        sections.add("D");

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sections);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSpinner.setAdapter(sectionAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if(parent.getId() == R.id.spinner_dept)
        {
            String userDeptFull = parent.getItemAtPosition(position).toString();

            switch (userDeptFull)
            {
                case "Computer Science & Engineering":
                {
                    userDept = "CSE";
                    break;
                }
                case "Electrical and Electronic Engineering":
                {
                    userDept = "EEE";
                    break;
                }
                case "Textile Engineering":
                {
                    userDept = "TE";
                    break;
                }
                case "Business Administration":
                {
                    userDept = "BBA";
                    break;
                }
                case "Law":
                {
                    userDept = "LAW";
                    break;
                }
                case "Bachelor of Pharmacy":
                {
                    userDept = "PHA";
                    break;
                }
                default:
                {
                    userDept = "CSE";
                }
            }
        }
        else if(parent.getId() == R.id.spinner_section)
        {
            userSection = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}
