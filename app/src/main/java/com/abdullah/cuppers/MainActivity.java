package com.abdullah.cuppers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;   
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    TextView loginNavHeaderTextView;
    GoogleSignInAccount googleSignInAccount;
    View view;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        ReadyForDrawer(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViews();
        LoginCheck();
    }

    private void LoginCheck() {
        if (SaveSharedPreference.getUserName(MainActivity.this).length() > 0) {
            loginNavHeaderTextView.setText(SaveSharedPreference.getUserName(MainActivity.this));
        } else {
            loginNavHeaderTextView.setText(getString(R.string.login));
        }
    }

    private void ReadyForDrawer(Bundle savedInstanceState) {
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        db = FirebaseFirestore.getInstance();
        view = navigationView.getHeaderView(0);
        loginNavHeaderTextView = view.findViewById(R.id.loginNavHeaderTextView);
        firebaseAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawerLayout);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_cart:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CartFragment()).commit();
                break;
            case R.id.nav_contact_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ContactUsFragment()).commit();
                break;
            case R.id.nav_edit:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new EditFragment()).commit();
                break;
            case R.id.nav_favorite:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FavoriteFragment()).commit();
                break;
            case R.id.nav_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new OrdersFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_your_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new YourOrdersFragment()).commit();
                break;
            case R.id.nav_language:
                Toast.makeText(this, "Change The Language", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public void LoginTextViewClicked(View view) {
        Intent toLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(toLoginActivity);
    }

    public void button(View view) {
        firebaseAuth.signOut();
        if (googleSignInAccount != null) {
            googleSignInOptions = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .build();
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            googleSignInClient.signOut();
        } else {
            LoginManager.getInstance().logOut();
        }
        loginNavHeaderTextView.setText(getString(R.string.login));
        SaveSharedPreference.setUserName(this, "");
    }

    public static class SaveSharedPreference {
        static final String PREF_USER_NAME = "UserName";

        static SharedPreferences getSharedPreferences(Context ctx) {
            return PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        public static void setUserName(Context ctx, String username) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_USER_NAME, username);
            editor.apply();
        }

        public static String getUserName(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
        }
    }
}
