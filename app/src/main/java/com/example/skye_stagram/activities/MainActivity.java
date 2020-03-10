package com.example.skye_stagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.skye_stagram.R;
import com.example.skye_stagram.fragments.ComposeFragment;
import com.example.skye_stagram.fragments.HomeFragment;
import com.example.skye_stagram.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity
        implements ComposeFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener
{
    public static final String TAG = MainActivity.class.getSimpleName();

    Toolbar _toolbar;
    NavigationView _nvDrawer;
    DrawerLayout _drawer;
    ActionBarDrawerToggle _drawerToggle;
    BottomNavigationView _bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Link all the views and initialize private variables
        _toolbar = findViewById(R.id.toolbar);
        _nvDrawer = findViewById(R.id.nvDrawer);
        _bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Set up drawer stuff
        _drawer = findViewById(R.id.drawerLayout);
        _drawerToggle = new ActionBarDrawerToggle(this, _drawer, _toolbar,
                R.string.drawerOpen, R.string.drawerClose);
        _nvDrawer = findViewById(R.id.nvDrawer);
        _nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                if (menuItem.getItemId() == R.id.action_logout)
                {
                    ParseUser.logOut();
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }

                _drawer.closeDrawers();

                return true;
            }
        });

        // Navigation listener
        _bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                Fragment fragment = null;
                Class fragmentClass;

                switch(menuItem.getItemId())
                {
                    case R.id.action_home:
                        fragmentClass = HomeFragment.class;
                        break;
                    case R.id.action_compose:
                        fragmentClass = ComposeFragment.class;
                        break;
                    case R.id.action_profile:
                        fragmentClass = ProfileFragment.class;
                        break;
                    default:
                        fragmentClass = HomeFragment.class;
                        break;
                }

                try
                {
                    fragment = (Fragment)fragmentClass.newInstance();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                menuItem.setChecked(true);

                return true;
            }
        });

        _bottomNavigationView.getMenu().getItem(0).setChecked(true);
        fragmentManager.beginTransaction().replace(R.id.flContent, new HomeFragment()).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }
}
