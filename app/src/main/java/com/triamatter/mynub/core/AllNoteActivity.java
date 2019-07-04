package com.triamatter.mynub.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.triamatter.mynub.adapter.NoteAdapter;
import com.triamatter.mynub.auth.LoginActivity;
import com.triamatter.mynub.core.admin.AdminAddRoutineActivity;
import com.triamatter.mynub.fragment.BottomSheetDialog;
import com.triamatter.mynub.model.admin.Note;

public class AllNoteActivity extends AppCompatActivity {

    String crUid;

    NoteAdapter noteAdapter;
    RecyclerView recyclerView;

    //Firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;
    CollectionReference noteRef;
    CollectionReference userRef;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_note);
        //init views
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setUpBottomNavbar();
        recyclerView = findViewById(R.id.allNoteRecyclerview);

        //Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("All Notes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Firebase
        db = FirebaseFirestore.getInstance();
        noteRef = db.collection("Notes");
        userRef = db.collection("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    private void setUpNoteRecyclerview()
    {
        SharedPreferences crRef = getSharedPreferences("CR", MODE_PRIVATE);
        boolean isCR = crRef.getBoolean("isCR", false);

        if(!isCR)
        {
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
                            }
                            getAllNotes(crUid);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "No CR Found!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AllNoteActivity.this, CRChooserActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
        else
        {
            getAllNotes(mUser.getUid());
        }

    }

    private void getAllNotes(String crUid)
    {
        Query query = noteRef.document(crUid).collection("Note").orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note.class)
            .build();

        noteAdapter = new NoteAdapter(options, getApplicationContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(noteAdapter);

        if(noteAdapter != null)
        {
            noteAdapter.startListening();
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //Set up note recyclerview
        setUpNoteRecyclerview();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(noteAdapter != null)
        {
            noteAdapter.stopListening();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.menu_chat)
        {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                        Intent intent = new Intent(AllNoteActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    }

                    case R.id.nav_notes:
                    {
                        Intent intent = new Intent(AllNoteActivity.this, AllNoteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_portal:
                    {
                        Intent intent = new Intent(AllNoteActivity.this, PortalActivity.class);
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
