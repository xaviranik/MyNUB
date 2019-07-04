package com.triamatter.mynub.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.triamatter.mynub.R;
import com.triamatter.mynub.adapter.RoutineAdapter;
import com.triamatter.mynub.fragment.BottomSheetDialog;
import com.triamatter.mynub.model.admin.Routine;
import com.triamatter.mynub.util.EmptyRecyclerView;

public class RoutineActivity extends AppCompatActivity {

    String crUid;

    //Views
    TextView emptyViewSun;
    TextView emptyViewMon;
    TextView emptyViewTue;
    TextView emptyViewWed;
    TextView emptyViewThu;
    TextView emptyViewFri;
    TextView emptyViewSat;
    //BottomNav
    BottomNavigationView bottomNavigationView;

    //Adapters
    RoutineAdapter sundayAdapter;
    RoutineAdapter mondayAdapter;
    RoutineAdapter tuesdayAdapter;
    RoutineAdapter wednesdayAdapter;
    RoutineAdapter thursdayAdapter;
    RoutineAdapter fridayAdapter;
    RoutineAdapter saturdayAdapter;


    //Firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;
    CollectionReference routineRef;
    CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        //Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Routine");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //init
        emptyViewSun = (TextView) findViewById(R.id.emptyViewSun);
        emptyViewMon = (TextView) findViewById(R.id.emptyViewMon);
        emptyViewTue = (TextView) findViewById(R.id.emptyViewTue);
        emptyViewWed = (TextView) findViewById(R.id.emptyViewWed);
        emptyViewThu = (TextView) findViewById(R.id.emptyViewThur);
        emptyViewFri = (TextView) findViewById(R.id.emptyViewFri);
        emptyViewSat = (TextView) findViewById(R.id.emptyViewSat);



        //Firebase
        db = FirebaseFirestore.getInstance();
        routineRef = db.collection("Routines");
        userRef = db.collection("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        //Setting up bottom nav
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setUpBottomNavbar();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //Setting up recyclerViews
        SharedPreferences crRef = getSharedPreferences("CR", MODE_PRIVATE);
        boolean isCR = crRef.getBoolean("isCR", false);

        if (!isCR) {
            userRef.document(mUser.getUid()).collection("CR").limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task)
                {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                crUid = documentSnapshot.getId();
                            }
                            //RecyclerViews
                            setUpRecyclerViews(crUid);
                        } else {
                            Toast.makeText(getApplicationContext(), "No CR Found!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RoutineActivity.this, CRChooserActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                }
            });
        } else {
            setUpRecyclerViews(mUser.getUid());
        }
    }

    private void setUpSundayRecyclerView(String crUid)
    {
        Query query = routineRef.document(crUid).collection("sunday").orderBy("timeStart", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Routine> options = new FirestoreRecyclerOptions.Builder<Routine>().setQuery(query, Routine.class).build();

        sundayAdapter = new RoutineAdapter(options, getApplicationContext());
        EmptyRecyclerView recyclerViewSun = findViewById(R.id.sunday_recyclerview);
        recyclerViewSun.setHasFixedSize(true);
        recyclerViewSun.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSun.setAdapter(sundayAdapter);

        sundayAdapter.startListening();

        recyclerViewSun.setEmptyView(emptyViewSun);
    }

    private void setUpMondayRecyclerView(String crUid)
    {
        Query query = routineRef.document(crUid).collection("monday").orderBy("timeStart", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Routine> options = new FirestoreRecyclerOptions.Builder<Routine>().setQuery(query, Routine.class).build();

        mondayAdapter = new RoutineAdapter(options, getApplicationContext());
        EmptyRecyclerView recyclerViewMon = findViewById(R.id.monday_recyclerview);
        recyclerViewMon.setHasFixedSize(true);
        recyclerViewMon.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMon.setAdapter(mondayAdapter);

        mondayAdapter.startListening();

        recyclerViewMon.setEmptyView(emptyViewMon);
    }

    private void setUpTuesdayRecyclerView(String crUid)
    {
        Query query = routineRef.document(crUid).collection("tuesday").orderBy("timeStart", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Routine> options = new FirestoreRecyclerOptions.Builder<Routine>().setQuery(query, Routine.class).build();

        tuesdayAdapter = new RoutineAdapter(options, getApplicationContext());

        EmptyRecyclerView recyclerViewTue = (EmptyRecyclerView) findViewById(R.id.tuesday_recyclerview);
        recyclerViewTue.setHasFixedSize(true);
        recyclerViewTue.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTue.setAdapter(tuesdayAdapter);

        tuesdayAdapter.startListening();

        recyclerViewTue.setEmptyView(emptyViewTue);

    }


    private void setUpWednesdayRecyclerView(String crUid)
    {
        Query query = routineRef.document(crUid).collection("wednesday").orderBy("timeStart", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Routine> options = new FirestoreRecyclerOptions.Builder<Routine>().setQuery(query, Routine.class).build();

        wednesdayAdapter = new RoutineAdapter(options, getApplicationContext());

        EmptyRecyclerView recyclerViewWed = (EmptyRecyclerView) findViewById(R.id.wednesday_recyclerview);
        recyclerViewWed.setHasFixedSize(true);
        recyclerViewWed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewWed.setAdapter(wednesdayAdapter);

        wednesdayAdapter.startListening();

        recyclerViewWed.setEmptyView(emptyViewWed);

    }

    private void setUpThursdayRecyclerView(String crUid)
    {
        Query query = routineRef.document(crUid).collection("thursday").orderBy("timeStart", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Routine> options = new FirestoreRecyclerOptions.Builder<Routine>().setQuery(query, Routine.class).build();

        thursdayAdapter = new RoutineAdapter(options, getApplicationContext());

        EmptyRecyclerView recyclerViewThur = (EmptyRecyclerView) findViewById(R.id.thursday_recyclerview);
        recyclerViewThur.setHasFixedSize(true);
        recyclerViewThur.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewThur.setAdapter(thursdayAdapter);

        thursdayAdapter.startListening();

        recyclerViewThur.setEmptyView(emptyViewThu);

    }

    private void setUpFridayRecyclerView(String crUid)
    {
        Query query = routineRef.document(crUid).collection("friday").orderBy("timeStart", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Routine> options = new FirestoreRecyclerOptions.Builder<Routine>().setQuery(query, Routine.class).build();

        fridayAdapter = new RoutineAdapter(options, getApplicationContext());

        EmptyRecyclerView recyclerViewFri = (EmptyRecyclerView) findViewById(R.id.friday_recyclerview);
        recyclerViewFri.setHasFixedSize(true);
        recyclerViewFri.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFri.setAdapter(fridayAdapter);

        fridayAdapter.startListening();

        recyclerViewFri.setEmptyView(emptyViewFri);

    }

    private void setUpSaturdayRecyclerView(String crUid)
    {
        Query query = routineRef.document(crUid).collection("saturday").orderBy("timeStart", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Routine> options = new FirestoreRecyclerOptions.Builder<Routine>().setQuery(query, Routine.class).build();

        saturdayAdapter = new RoutineAdapter(options, getApplicationContext());

        EmptyRecyclerView recyclerViewSat = (EmptyRecyclerView) findViewById(R.id.saturday_recyclerview);
        recyclerViewSat.setHasFixedSize(true);
        recyclerViewSat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSat.setAdapter(saturdayAdapter);

        saturdayAdapter.startListening();

        recyclerViewSat.setEmptyView(emptyViewSat);

    }


    private void setUpRecyclerViews(String crUid)
    {
        setUpSundayRecyclerView(crUid);
        setUpMondayRecyclerView(crUid);
        setUpTuesdayRecyclerView(crUid);
        setUpWednesdayRecyclerView(crUid);
        setUpThursdayRecyclerView(crUid);
        setUpFridayRecyclerView(crUid);
        setUpSaturdayRecyclerView(crUid);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        sundayAdapter.stopListening();
        mondayAdapter.stopListening();
        tuesdayAdapter.stopListening();
        wednesdayAdapter.stopListening();
        thursdayAdapter.stopListening();
        fridayAdapter.stopListening();
        saturdayAdapter.stopListening();
    }

    private void setUpBottomNavbar()
    {
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                    {
                        Intent intent = new Intent(RoutineActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_notes:
                    {
                        Intent intent = new Intent(RoutineActivity.this, AllNoteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_portal:
                    {
                        Intent intent = new Intent(RoutineActivity.this, PortalActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_menu:
                    {
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                        bottomSheetDialog.show(getSupportFragmentManager(), "bottomSheet");
                        break;
                    }
                }


                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
