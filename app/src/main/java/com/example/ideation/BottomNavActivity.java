package com.example.ideation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.ideation.Fragments.BookmarkFragment;
import com.example.ideation.Fragments.ExploreFragment;
import com.example.ideation.Fragments.FeedFragment;
import com.example.ideation.Fragments.HomeFragment;
import com.example.ideation.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavActivity extends AppCompatActivity {

    BottomNavigationView bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        replace(new HomeFragment());

        bottom_nav = findViewById(R.id.nav);

        bottom_nav.setItemHorizontalTranslationEnabled(true);
        bottom_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.home) replace(new HomeFragment());
                else if (item.getItemId()==R.id.feed) replace(new FeedFragment());
                else if (item.getItemId()==R.id.bookmark) replace(new BookmarkFragment());
                else if (item.getItemId()==R.id.idea_about) replace(new ExploreFragment());
                else replace(new ProfileFragment());
                return true;
            }
        });

    }

    private void replace(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.frame_layout,fragment).commit();
    }
}