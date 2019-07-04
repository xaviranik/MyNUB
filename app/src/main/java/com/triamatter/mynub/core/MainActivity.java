package com.triamatter.mynub.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.triamatter.mynub.R;
import com.triamatter.mynub.adapter.CourseNoticeAdapter;
import com.triamatter.mynub.adapter.NoticeAdapter;
import com.triamatter.mynub.adapter.RoutineAdapter;
import com.triamatter.mynub.auth.LoginActivity;
import com.triamatter.mynub.core.admin.AdminAddCourseNoticeActivity;
import com.triamatter.mynub.core.admin.AdminAddNoteActivity;
import com.triamatter.mynub.core.admin.AdminAddRoutineActivity;
import com.triamatter.mynub.fragment.BottomSheetDialog;
import com.triamatter.mynub.model.Notice;
import com.triamatter.mynub.model.UserProfile;
import com.triamatter.mynub.model.admin.CourseNotice;
import com.triamatter.mynub.model.admin.Routine;
import com.triamatter.mynub.util.EmptyRecyclerView;

import java.util.Calendar;

import me.anwarshahriar.calligrapher.Calligrapher;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView userNameTextview;
    TextView userIDTextview;
    TextView noClassTextview;

    ImageView noClassImage;

    FloatingActionsMenu adminFab;
    FloatingActionButton adminAddNoteFAB;
    FloatingActionButton adminAddRoutineFAB;
    FloatingActionButton adminAddCourseNoticeFAB;

    //Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference noticeRef = db.collection("Notices");
    CollectionReference courseNoticeRef = db.collection("CourseNotices");
    CollectionReference routineRef = db.collection("Routines");
    CollectionReference userRef = db.collection("Users");
    CollectionReference allCrRef = db.collection("CRs");

    //Adapters
    NoticeAdapter noticeAdapter;
    CourseNoticeAdapter courseNoticeAdapter;
    RoutineAdapter routineAdapter;


    BottomNavigationView bottomNavigationView;

    String crUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //Initialize Firebase
        FirebaseApp.initializeApp(this);

        //View init
        userNameTextview = (TextView) findViewById(R.id.user_name);
        userIDTextview = (TextView) findViewById(R.id.user_id);

        noClassTextview = (TextView) findViewById(R.id.emptyClass);
        noClassImage = (ImageView) findViewById(R.id.noClassImage);

        adminFab = (FloatingActionsMenu) findViewById(R.id.fab_admin);
        adminAddNoteFAB = (FloatingActionButton) findViewById(R.id.admin_add_note);
        adminAddCourseNoticeFAB = (FloatingActionButton) findViewById(R.id.admin_add_notice);
        adminAddRoutineFAB = (FloatingActionButton) findViewById(R.id.admin_add_routine);

        adminAddNoteFAB.setOnClickListener(this);
        adminAddRoutineFAB.setOnClickListener(this);
        adminAddCourseNoticeFAB.setOnClickListener(this);


        //Setting admin panel
        adminFab.setVisibility(View.GONE);
        crInit();

        //Changing font family
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "Roboto_Regular.ttf", true);

        //Setting up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting up bottom nav
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setUpBottomNavbar();

        //Setting up user info
        setUpUserInfo();

        //Set up notice recyclerView
        setUpNoticeRecyclerView();

    }


    private void crInit()
    {
        SharedPreferences crPref = getSharedPreferences("CR", MODE_PRIVATE);
        boolean isCR = crPref.getBoolean("isCR", false);
        boolean isUser = crPref.getBoolean("isUser", false);
        crUid = crPref.getString("crUid", null);

        Log.i("CRUID", "crInit: " + crUid);

        if(isCR)
        {
            adminFab.setVisibility(View.VISIBLE);
            setUpCourseNoticeRecyclerView(mUser.getUid());
            setUpRoutineRecyclerView(mUser.getUid());
        }
        else if(isUser)
        {
            adminFab.setVisibility(View.GONE);
            if(crUid != null)
            {
                setUpCourseNoticeRecyclerView(crUid);
                setUpRoutineRecyclerView(crUid);
            }
        }
        else
        {
            checkIfCR();
        }
    }

    private void checkIfCR()
    {

        allCrRef.document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if(documentSnapshot.exists())
                {
                    setUpCourseNoticeRecyclerView(mUser.getUid());
                    setUpRoutineRecyclerView(mUser.getUid());
                    adminFab.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor editor = getSharedPreferences("CR", MODE_PRIVATE).edit();
                    editor.putBoolean("isCR", true);
                    editor.apply();
                }
                else
                {
                    //Getting the CR
                    userRef.document(mUser.getUid()).collection("CR").limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if(task.isSuccessful())
                            {
                                if(!task.getResult().isEmpty())
                                {
                                    for(DocumentSnapshot documentSnapshot : task.getResult())
                                    {
                                        crUid = documentSnapshot.getId();
                                        setUpCourseNoticeRecyclerView(crUid);
                                        setUpRoutineRecyclerView(crUid);

                                        SharedPreferences.Editor editor = getSharedPreferences("CR", MODE_PRIVATE).edit();
                                        editor.putString("crUid", crUid);
                                        editor.putBoolean("isUser", true);
                                        editor.apply();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "No CR Found!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, CRChooserActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void setUpNoticeRecyclerView()
    {
        Query query = noticeRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
        FirestoreRecyclerOptions<Notice> options = new FirestoreRecyclerOptions.Builder<Notice>().setQuery(query, Notice.class)
           .build();

        noticeAdapter = new NoticeAdapter(options, getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.noticeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(noticeAdapter);
    }

    private void setUpCourseNoticeRecyclerView(String crUid)
    {
        Query query = courseNoticeRef.document(crUid).collection("Notice").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);

        FirestoreRecyclerOptions<CourseNotice> options = new FirestoreRecyclerOptions.Builder<CourseNotice>().setQuery(query, CourseNotice.class).build();

        courseNoticeAdapter = new CourseNoticeAdapter(options, getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.course_noticeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(courseNoticeAdapter);

        courseNoticeAdapter.startListening();
    }

    private void setUpRoutineRecyclerView(String crUid)
    {
        String day = getCurrentDay();

        if(day != null)
        {
            Query query = routineRef.document(crUid).collection(day).orderBy("timeStart", Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<Routine> options = new FirestoreRecyclerOptions.Builder<Routine>().setQuery(query, Routine.class).build();

            routineAdapter = new RoutineAdapter(options, getApplicationContext());

            EmptyRecyclerView recyclerViewRoutine = (EmptyRecyclerView) findViewById(R.id.classRecyclerView);
            recyclerViewRoutine.setHasFixedSize(true);
            recyclerViewRoutine.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewRoutine.setAdapter(routineAdapter);

            routineAdapter.startListening();

            recyclerViewRoutine.setEmptyView(noClassTextview);
            recyclerViewRoutine.setEmptyViewIcon(noClassImage);
        }

    }

    private String getCurrentDay()
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day)
        {
            case Calendar.SUNDAY:
                return "sunday";
            case Calendar.MONDAY:
                return "monday";
            case Calendar.TUESDAY:
                return "tuesday";
            case Calendar.WEDNESDAY:
                return "wednesday";
            case Calendar.THURSDAY:
                return "thursday";
            case Calendar.FRIDAY:
                return "friday";
            case Calendar.SATURDAY:
                return "saturday";
            default:
                return null;
        }
    }

    private void setUpUserInfo()
    {
        SharedPreferences crPref = getSharedPreferences("USER", MODE_PRIVATE);

        String userFullname = crPref.getString("userName", null);
        String userDept = crPref.getString("userDept", null);
        String userId = crPref.getString("userId", null);

        if(userFullname != null && userDept != null && userId != null)
        {
            userNameTextview.setText(userFullname);
            userIDTextview.setText(userDept + userId);
        }

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
                        break;

                    case R.id.nav_notes:
                    {
                        Intent intent = new Intent(MainActivity.this, AllNoteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_portal:
                    {
                        Intent intent = new Intent(MainActivity.this, PortalActivity.class);
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
    protected void onStart() {
        super.onStart();
        noticeAdapter.startListening();
        if(courseNoticeAdapter != null)
        {
            courseNoticeAdapter.startListening();
        }

        if(routineAdapter != null)
        {
            routineAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        noticeAdapter.stopListening();

        if(courseNoticeAdapter != null)
        {
            courseNoticeAdapter.stopListening();
        }

        if(routineAdapter != null)
        {
            routineAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_chat) {
            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.admin_add_note:
            {
                Intent intent = new Intent(MainActivity.this, AdminAddNoteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            }
            case R.id.admin_add_notice:
            {
                Intent intent = new Intent(MainActivity.this, AdminAddCourseNoticeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            }
            case R.id.admin_add_routine:
            {
                Intent intent = new Intent(MainActivity.this, AdminAddRoutineActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            }
        }
    }
}
