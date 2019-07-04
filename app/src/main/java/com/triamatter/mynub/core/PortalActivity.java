package com.triamatter.mynub.core;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.triamatter.mynub.R;
import com.triamatter.mynub.fragment.BottomSheetDialog;

public class PortalActivity extends AppCompatActivity {

    WebView webView;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //Portal Webview
        webView = (WebView) findViewById(R.id.portalWebview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://182.160.97.196:8088/studentportal/");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        setUpBottomNavbar();

    }

    private void setUpBottomNavbar()
    {
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                    {
                        Intent intent = new Intent(PortalActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_notes:
                    {
                        Intent intent = new Intent(PortalActivity.this, AllNoteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    case R.id.nav_portal:
                    {
                        Intent intent = new Intent(PortalActivity.this, PortalActivity.class);
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
