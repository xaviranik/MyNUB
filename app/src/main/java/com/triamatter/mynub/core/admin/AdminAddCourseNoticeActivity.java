package com.triamatter.mynub.core.admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.triamatter.mynub.R;
import com.triamatter.mynub.adapter.CourseNoticeAdapter;
import com.triamatter.mynub.adapter.admin.ShowCourseNoticeAdapter;
import com.triamatter.mynub.core.AllNoteActivity;
import com.triamatter.mynub.core.MainActivity;
import com.triamatter.mynub.core.PortalActivity;
import com.triamatter.mynub.fragment.BottomSheetDialog;
import com.triamatter.mynub.fragment.admin.AddCourseNoticeDialog;
import com.triamatter.mynub.fragment.admin.AddNoteDialog;
import com.triamatter.mynub.model.admin.CourseNotice;

public class AdminAddCourseNoticeActivity extends AppCompatActivity implements AddCourseNoticeDialog.AddCourseNoticeDialogListener {

    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CollectionReference courseNoticeRef;
    BottomNavigationView bottomNavigationView;

    ShowCourseNoticeAdapter courseNoticeAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_course_notice);

        //Init views
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        recyclerView = findViewById(R.id.allCourseNoticeRecyclerView);
        setUpBottomNavbar();

        //Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Course Notice");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Firebase
        db = FirebaseFirestore.getInstance();
        courseNoticeRef = db.collection("CourseNotices");
        mAuth = FirebaseAuth.getInstance();

        //Set up note recyclerview
        setUpCourseNoticeRecyclerView();
    }

    private void setUpCourseNoticeRecyclerView()
    {
        Query query = courseNoticeRef.document(mAuth.getCurrentUser().getUid()).collection("Notice");
        FirestoreRecyclerOptions<CourseNotice> options = new FirestoreRecyclerOptions.Builder<CourseNotice>().setQuery(query, CourseNotice.class)
                .build();

        courseNoticeAdapter = new ShowCourseNoticeAdapter(options, getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(courseNoticeAdapter);
    }

    private void setUpBottomNavbar()
    {
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        startActivity(new Intent(AdminAddCourseNoticeActivity.this, MainActivity.class));
                        break;

                    case R.id.nav_notes:
                    {
                        Intent intent = new Intent(AdminAddCourseNoticeActivity.this, AllNoteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_portal:
                    {
                        Intent intent = new Intent(AdminAddCourseNoticeActivity.this, PortalActivity.class);
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
    protected void onStart()
    {
        super.onStart();

        if(courseNoticeAdapter != null)
        {
            courseNoticeAdapter.startListening();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if(courseNoticeAdapter != null)
        {
            courseNoticeAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.admin_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.admin_menu_add) {
            openAddCourseNoticeDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void openAddCourseNoticeDialog()
    {
        AddCourseNoticeDialog addCourseNoticeDialog = new AddCourseNoticeDialog();
        addCourseNoticeDialog.show(getSupportFragmentManager(), "Add notice dialog");
    }

    @Override
    public void addNotice(String notice, String courseTitle, final AlertDialog dialog)
    {
        CourseNotice courseNotice = new CourseNotice(notice, courseTitle, Timestamp.now());
        courseNoticeRef.document(mAuth.getCurrentUser().getUid()).collection("Notice").document().set(courseNotice).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(getApplicationContext(), "Notice Added!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}
