package com.abdullah.cuppers.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdullah.cuppers.Fragments.AnalyticsFragment;
import com.abdullah.cuppers.Fragments.CartFragment;
import com.abdullah.cuppers.Fragments.CustomersOrdersFragment;
import com.abdullah.cuppers.Fragments.EditFragment;
import com.abdullah.cuppers.Fragments.FavoriteFragment;
import com.abdullah.cuppers.Fragments.HomeFragment;
import com.abdullah.cuppers.Fragments.ProfileFragment;
import com.abdullah.cuppers.Fragments.YourCreditCardsFragment;
import com.abdullah.cuppers.Fragments.YourOrdersFragment;
import com.abdullah.cuppers.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    FirebaseFirestore db;
    TextView loginNavHeaderTextView;
    View HomeView;
    Intent toLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findHomeViews();
        ReadyForDrawer(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findHomeViews();
        LoginCheck();
    }

    private void findHomeViews() {
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        db = FirebaseFirestore.getInstance();
        HomeView = navigationView.getHeaderView(0);
        loginNavHeaderTextView = HomeView.findViewById(R.id.loginNavHeaderTextView);
        drawerLayout = findViewById(R.id.drawerLayout);
        toLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
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
        toolbar.setTitle(R.string.app_name);
    }

    private void LoginCheck() {
        if (SaveSharedPreference.getUserName(MainActivity.this).length() > 0) {
            loginNavHeaderTextView.setText(SaveSharedPreference.getUserName(MainActivity.this));
        } else {
            loginNavHeaderTextView.setText(getString(R.string.login));
        }
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
        if (SaveSharedPreference.getUserName(MainActivity.this).equals("")) {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                case R.id.nav_favorite:
                case R.id.nav_cart:
                case R.id.nav_your_orders:
                case R.id.nav_your_credit_cards:
                    startActivity(toLoginActivity);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_facebook_page:
                    Toast.makeText(this, "To Facebook Page", Toast.LENGTH_SHORT).show();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_logout:
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_customers_orders:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new CustomersOrdersFragment()).commit();
                    toolbar.setTitle(R.string.customers_orders);
                    break;
                case R.id.nav_edit:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new EditFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_analytics:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new AnalyticsFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
            }

            drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_profile:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
//                    FindProfileViews();
//                    innerConstraintLayout1.setVisibility(View.VISIBLE);
//                    innerConstraintLayout2.setVisibility(View.GONE);
//                    setInformation();
                    break;
                case R.id.nav_favorite:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new FavoriteFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_cart:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new CartFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_your_orders:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new YourOrdersFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_your_credit_cards:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new YourCreditCardsFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_facebook_page:
                    Toast.makeText(this, "To Facebook Page", Toast.LENGTH_SHORT).show();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_logout:
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_customers_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CustomersOrdersFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                break;
                case R.id.nav_edit:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new EditFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_analytics:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new AnalyticsFragment()).commit();
                    toolbar.setTitle(R.string.app_name);
                    break;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    public void LoginTextViewClicked(View view) {
        if (loginNavHeaderTextView.getText().toString().equals("Login")) {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(toLoginActivity);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            navigationView.setCheckedItem(R.id.nav_profile);
        }
    }

    public static class SaveSharedPreference {
        static final String PREF_EMAIL = "Email";
        static final String PREF_USER_NAME = "UserName";
        static final String PREF_PHONE_NUMBER = "PhoneNumber";
        static final String PREF_ADDRESS = "Address";
        static final String PREF_DATE_OF_BIRTH = "DateOfBirth";
        static final String PREF_GENDER = "Gender";

        static SharedPreferences getSharedPreferences(Context ctx) {
            return PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        public static void setEmail(Context ctx, String email) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_EMAIL, email);
            editor.apply();
        }

        public static String getEmail(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_EMAIL, "");
        }

        public static void setUserName(Context ctx, String username) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_USER_NAME, username);
            editor.apply();
        }

        public static String getUserName(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
        }

        public static void setPhoneNumber(Context ctx, String phonenumber) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_PHONE_NUMBER, phonenumber);
            editor.apply();
        }

        public static String getPhoneNumber(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_PHONE_NUMBER, "");
        }

        public static void setAddress(Context ctx, String address) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_ADDRESS, address);
            editor.apply();
        }

        public static String getAddress(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_ADDRESS, "");
        }

        public static void setDateOfBirth(Context ctx, String dateofbirth) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_DATE_OF_BIRTH, dateofbirth);
            editor.apply();
        }

        public static String getDateOfBirth(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_DATE_OF_BIRTH, "");
        }

        public static void setGender(Context ctx, String gender) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_GENDER, gender);
            editor.apply();
        }

        public static String getGender(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_GENDER, "");
        }
    }

    public void Logout() {
        Clickable(false);
        loginNavHeaderTextView.setText(getString(R.string.login));
        SaveSharedPreference.setUserName(this, "");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        toolbar.setTitle(R.string.app_name);
        navigationView.setCheckedItem(R.id.nav_home);
        Clickable(true);
    }

    public void logoutButtonClicked(View view) {
        Logout();
    }

    public void deleteAccountButtonClicked(View view) {
        AlertDialog diaBox = DeleteAccountDialog();
        diaBox.show();
    }

    public AlertDialog DeleteAccountDialog() {
        final AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Deleting this account will remove your profile data \nAre you sure you want to delete your Account ?")
                .setIcon(R.drawable.ic_delete_forever)
                .setPositiveButton("Delete Account", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (Connected()) {
                            Clickable(false);
                            DeleteAccount();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.check_the_internet_connection, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        myQuittingDialogBox.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.outOfStock));
            }
        });

        return myQuittingDialogBox;
    }

    private void DeleteAccount() {
        db.collection(getString(R.string.users)).document(SaveSharedPreference.getEmail(this))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Account has been Deleted Successfully", Toast.LENGTH_SHORT).show();
                        Logout();
                        Clickable(true);
                        View profileView = getLayoutInflater().inflate(R.layout.profile, null);
                        ProgressBar profileProgressBar = profileView.findViewById(R.id.profileProgressBar);
                        profileProgressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error while deleting account", Toast.LENGTH_SHORT).show();
                Clickable(true);
                View profileView = getLayoutInflater().inflate(R.layout.profile, null);
                ProgressBar profileProgressBar = profileView.findViewById(R.id.profileProgressBar);
                profileProgressBar.setVisibility(View.GONE);
            }
        });
    }

    public void Clickable(boolean b) {
        if (b) {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public boolean Connected() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("toolbarTitle", toolbar.getTitle().toString());
    }
}
