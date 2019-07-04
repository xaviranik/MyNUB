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
import com.triamatter.mynub.adapter.NoteAdapter;
import com.triamatter.mynub.core.AllNoteActivity;
import com.triamatter.mynub.core.MainActivity;
import com.triamatter.mynub.core.PortalActivity;
import com.triamatter.mynub.fragment.admin.AddNoteDialog;
import com.triamatter.mynub.fragment.BottomSheetDialog;
import com.triamatter.mynub.model.admin.Note;

public class AdminAddNoteActivity extends AppCompatActivity implements AddNoteDialog.AddNoteDialogListener {

    NoteAdapter noteAdapter;

    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CollectionReference noteRef;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_note);

        //Init views
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setUpBottomNavbar();

        //Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Note");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Firebase
        db = FirebaseFirestore.getInstance();
        noteRef = db.collection("Notes");
        mAuth = FirebaseAuth.getInstance();

        //Set up note recyclerview
        setUpNoteRecyclerview();
    }

    private void setUpNoteRecyclerview()
    {
        Query query = noteRef.document(mAuth.getCurrentUser().getUid()).collection("Note");
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note.class)
                .build();

        noteAdapter = new NoteAdapter(options, getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.allNoteRecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(noteAdapter);
    }

    private void openAddNoteDialog()
    {
        AddNoteDialog addNoteDialog = new AddNoteDialog();
        addNoteDialog.show(getSupportFragmentManager(), "Add note dialog");
    }



    @Override
    protected void onStart()
    {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        noteAdapter.stopListening();
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
            openAddNoteDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void addNote(String noteTitle, String noteUrl, final AlertDialog dialog)
    {
        Note note = new Note(noteTitle, noteUrl, Timestamp.now());
        noteRef.document(mAuth.getCurrentUser().getUid()).collection("Note").document().set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(getApplicationContext(), "Note Added!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
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
                        startActivity(new Intent(AdminAddNoteActivity.this, MainActivity.class));
                        break;

                    case R.id.nav_notes:
                    {
                        Intent intent = new Intent(AdminAddNoteActivity.this, AllNoteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_portal:
                    {
                        Intent intent = new Intent(AdminAddNoteActivity.this, PortalActivity.class);
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
