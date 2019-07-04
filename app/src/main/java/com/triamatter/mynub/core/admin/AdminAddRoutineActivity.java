package com.triamatter.mynub.core.admin;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.triamatter.mynub.R;
import com.triamatter.mynub.core.AllNoteActivity;
import com.triamatter.mynub.core.MainActivity;
import com.triamatter.mynub.core.PortalActivity;
import com.triamatter.mynub.fragment.BottomSheetDialog;
import com.triamatter.mynub.fragment.admin.TimePickerFragment;
import com.triamatter.mynub.model.admin.Routine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdminAddRoutineActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    Spinner daySpinner;
    FloatingActionButton fabClassStart;
    FloatingActionButton fabClassEnd;

    TextView classTimeStartTextview;
    TextView classTimeEndTextview;
    TextView courseTitleEditText;
    TextView roomNumEditText;

    BottomNavigationView bottomNavigationView;

    Button createRoutineBtn;

    boolean timeStart;

    Timestamp classStartTimestamp;
    Timestamp classEndTimestamp;

    String daySelected;

    //Firebase
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CollectionReference routineRef = db.collection("Routines");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_routine);

        //Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Routine");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //init
        daySpinner = (Spinner) findViewById(R.id.day_spinner);
        fabClassStart = (FloatingActionButton) findViewById(R.id.fab_classStart);
        fabClassEnd = (FloatingActionButton) findViewById(R.id.fab_classEnd);
        classTimeEndTextview = (TextView) findViewById(R.id.classEndTextview);
        classTimeStartTextview = (TextView) findViewById(R.id.classStartTextview);
        createRoutineBtn = (Button) findViewById(R.id.btn_createRoutine);
        courseTitleEditText = (EditText) findViewById(R.id.editTextCourseTitle);
        roomNumEditText = (EditText) findViewById(R.id.editTextRoomNum);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setUpBottomNavbar();

        //init functions
        daySpinner.setOnItemSelectedListener(this);
        setUpDaySpinner();

        fabClassStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showTimeDialog();
                timeStart = true;
            }
        });

        fabClassEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showTimeDialog();
                timeStart = false;
            }
        });

        createRoutineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                createRoutine();
            }
        });

    }

    private void createRoutine()
    {
        String courseTitle = courseTitleEditText.getText().toString();
        String roomNum = roomNumEditText.getText().toString();

        //Validation
        if(courseTitle.isEmpty())
        {
            courseTitleEditText.setError("Course Title can't be empty!");
            courseTitleEditText.requestFocus();
            return;
        }

        if(roomNum.isEmpty())
        {
            roomNumEditText.setError("Room number can't be empty!");
            roomNumEditText.requestFocus();
            return;
        }

        if(classStartTimestamp == null)
        {
            Toast.makeText(getApplicationContext(), "Please set class starting time!" , Toast.LENGTH_LONG).show();
            return;
        }

        if(classEndTimestamp == null)
        {
            Toast.makeText(getApplicationContext(), "Please set class ending time!" , Toast.LENGTH_LONG).show();
            return;
        }

        if(routineRef != null && daySelected != null)
        {
            createRoutineBtn.setEnabled(false);

            Routine routine = new Routine(courseTitle, roomNum, classStartTimestamp, classEndTimestamp);

            routineRef.document(mUser.getUid()).collection(daySelected).document().set(routine).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Toast.makeText(getApplicationContext(), "Class added on " + daySelected , Toast.LENGTH_LONG).show();
                    resetRoutineUI();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    createRoutineBtn.setEnabled(true);
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void resetRoutineUI()
    {
        courseTitleEditText.setText("");
        roomNumEditText.setText("");
        createRoutineBtn.setEnabled(true);
    }

    private void showTimeDialog()
    {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }



    private void setUpDaySpinner()
    {
        //Days
        List<String> dayList = new ArrayList<String>();
        dayList.add("Sunday");
        dayList.add("Monday");
        dayList.add("Tuesday");
        dayList.add("Wednesday");
        dayList.add("Thursday");
        dayList.add("Friday");
        dayList.add("Saturday");

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dayList);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        if(timeStart)
        {
            classStartTimestamp = createTimestamp(hourOfDay, minute);
        }
        else
        {
            classEndTimestamp = createTimestamp(hourOfDay, minute);
        }

    }

    private void setTimeUI(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String time = dateFormat.format(date);

        if(timeStart)
        {
            classTimeStartTextview.setText(time);
        }
        else
        {
            classTimeEndTextview.setText(time);
        }
    }

    private Timestamp createTimestamp(int hourOfDay, int min)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2019);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date classTime = cal.getTime();

        setTimeUI(classTime);

        Timestamp timestamp = new Timestamp(classTime);
        return timestamp;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        daySelected = parent.getItemAtPosition(position).toString().toLowerCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    private void setUpBottomNavbar()
    {
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                    {
                        Intent intent = new Intent(AdminAddRoutineActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }


                    case R.id.nav_notes:
                    {
                        Intent intent = new Intent(AdminAddRoutineActivity.this, AllNoteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_portal:
                    {
                        Intent intent = new Intent(AdminAddRoutineActivity.this, PortalActivity.class);
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
}
