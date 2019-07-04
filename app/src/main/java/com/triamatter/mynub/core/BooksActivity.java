package com.triamatter.mynub.core;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.triamatter.mynub.R;
import com.triamatter.mynub.adapter.BookAdapter;
import com.triamatter.mynub.auth.LoginActivity;
import com.triamatter.mynub.fragment.BottomSheetDialog;
import com.triamatter.mynub.model.Book;
import com.triamatter.mynub.util.EmptyRecyclerView;

public class BooksActivity extends AppCompatActivity {

    MenuItem item;

    BookAdapter bookAdapter;

    EmptyRecyclerView recyclerView;

    MaterialSearchView searchView;

    TextView emptyBook;
    ImageView emptyBookImage;
    BottomNavigationView bottomNavigationView;

    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference bookRef = db.collection("Books");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        //Setting up the toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Books");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //init views
        recyclerView = (EmptyRecyclerView) findViewById(R.id.book_recyclerView);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        emptyBook = (TextView) findViewById(R.id.emptyBook);
        emptyBookImage = (ImageView) findViewById(R.id.emptyBookImage);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setUpBottomNavbar();

        searchView.setHint("Search book by course code...");

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBook(query.toUpperCase().trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                toolbar.setTitle("");
                item.setVisible(false);

            }

            @Override
            public void onSearchViewClosed() {
                toolbar.setTitle("Books");
                item.setVisible(true);
            }
        });
    }

    private void setUpBooksRecyclerView()
    {
        Query query = bookRef.limit(20);

        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query, Book.class).build();

        bookAdapter = new BookAdapter(options, getApplicationContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(bookAdapter);

        bookAdapter.startListening();

        recyclerView.setEmptyView(emptyBook);
        recyclerView.setEmptyViewIcon(emptyBookImage);
    }

    private void searchBook(String courseCode)
    {
        Query query = bookRef.whereEqualTo("courseCode", courseCode).limit(20);

        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query, Book.class).build();

        bookAdapter = new BookAdapter(options, getApplicationContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(bookAdapter);

        bookAdapter.startListening();
        bookAdapter.notifyDataSetChanged();

        recyclerView.setEmptyView(emptyBook);
        recyclerView.setEmptyViewIcon(emptyBookImage);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        setUpBooksRecyclerView();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        bookAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_search_menu, menu);

        item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;

    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen())
        {
            searchView.closeSearch();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                    {
                        Intent intent = new Intent(BooksActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_notes:
                    {
                        Intent intent = new Intent(BooksActivity.this, AllNoteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_portal:
                    {
                        Intent intent = new Intent(BooksActivity.this, PortalActivity.class);
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
