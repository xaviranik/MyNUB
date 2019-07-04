package com.triamatter.mynub.core;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.triamatter.mynub.R;
import com.triamatter.mynub.model.UserProfile;

import java.util.HashMap;
import java.util.Map;

public class CRChooserActivity extends AppCompatActivity {

    String crUid;
    UserProfile userProfile;

    CardView crProfileCard;

    EditText editTextCrId;
    TextView textViewCrName;
    TextView textViewCrId;
    TextView textViewCrDept;

    Button searchBtn;
    Button chooseBtn;

    //Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("Users");
    CollectionReference allCrRef = db.collection("CRs");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cr_chooser);

        //Init views
        crProfileCard = (CardView) findViewById(R.id.crProfileCard);
        textViewCrName = (TextView) findViewById(R.id.textViewCrName);
        textViewCrDept =(TextView) findViewById(R.id.textViewCrDept);
        textViewCrId = (TextView) findViewById(R.id.textViewCrID);
        editTextCrId = (EditText) findViewById(R.id.editText_cr_id);
        searchBtn = (Button) findViewById(R.id.btn_search);
        chooseBtn = (Button) findViewById(R.id.btn_choose);

        //Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Choose Your CR");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Search CR
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                searchCR();
            }
        });

        //Choose CR
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chooseCR();
            }
        });
    }

    private void chooseCR()
    {
        Map<String, Object> crMap = new HashMap<>();
        crMap.put("crId", userProfile.getUserId());
        userRef.document(mAuth.getCurrentUser().getUid()).collection("CR").document(crUid).set(crMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(getApplicationContext(), "CR choosed!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CRChooserActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void searchCR()
    {
        String crId = editTextCrId.getText().toString();

        if(crId.isEmpty())
        {
            editTextCrId.setError("ID can't be empty!");
            editTextCrId.requestFocus();
            return;
        }

        if(crId.length() < 9)
        {
            editTextCrId.setError("ID length should be nine! eg: 160200000");
            editTextCrId.requestFocus();
            return;
        }

        allCrRef.whereEqualTo("userId", crId).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if(task.isSuccessful())
                {
                    if(!task.getResult().isEmpty())
                    {
                        crProfileCard.setVisibility(View.VISIBLE);

                        for(DocumentSnapshot documentSnapshot : task.getResult())
                        {
                            crUid = documentSnapshot.getId();

                            userProfile = documentSnapshot.toObject(UserProfile.class);
                        }

                        textViewCrName.setText(userProfile.getUserFullName());
                        textViewCrId.setText(userProfile.getUserId());
                        textViewCrDept.setText(userProfile.getUserDept());
                    }
                    else
                    {
                        crProfileCard.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "CR not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
