package com.abdullah.cuppers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ProgressBar loginProgressBar;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
    }

    public void findViews() {
        loginProgressBar = findViewById(R.id.loginProgressBar);
        db = FirebaseFirestore.getInstance();
    }

//    private void AddingGoogleUsers() {
//        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if (Connected()) {
//            db.collection(getString(R.string.users))
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    if (document.getId().equals(googleSignInAccount.getEmail())) {
//                                        SaveSharedPreference.setEmail(LoginActivity.this, document.getData().get(getString(R.string.email)).toString());
//                                        SaveSharedPreference.setUserName(LoginActivity.this, document.getData().get(getString(R.string.username)).toString());
//                                        SaveSharedPreference.setPhoneNumber(LoginActivity.this, document.getData().get(getString(R.string.phone)).toString());
//                                        SaveSharedPreference.setAddress(LoginActivity.this, document.getData().get(getString(R.string.address)).toString());
//                                        SaveSharedPreference.setDateOfBirth(LoginActivity.this, document.getData().get(getString(R.string.date_of_birth)).toString());
//                                        SaveSharedPreference.setGender(LoginActivity.this, document.getData().get(getString(R.string.gender)).toString());
//                                        check = true;
//                                        finish();
//                                        Clickable(true);
//                                        loginProgressBar.setVisibility(View.GONE);
//                                    }
//                                }
//                                if (!check) {
//                                    Map<String, Object> user = new HashMap<>();
//                                    user.put(getString(R.string.username), googleSignInAccount.getDisplayName());
//                                    user.put(getString(R.string.email), googleSignInAccount.getEmail());
//                                    user.put(getString(R.string.phone), "");
//                                    user.put(getString(R.string.address), "");
//                                    user.put(getString(R.string.date_of_birth), "//");
//                                    user.put(getString(R.string.gender), "");
//
//                                    db.collection(getString(R.string.users))
//                                            .document(googleSignInAccount.getEmail())
//                                            .set(user)
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    SaveSharedPreference.setEmail(LoginActivity.this, googleSignInAccount.getEmail());
//                                                    SaveSharedPreference.setUserName(LoginActivity.this, googleSignInAccount.getDisplayName());
//                                                    SaveSharedPreference.setPhoneNumber(LoginActivity.this, "");
//                                                    SaveSharedPreference.setAddress(LoginActivity.this, "");
//                                                    SaveSharedPreference.setDateOfBirth(LoginActivity.this, "//");
//                                                    SaveSharedPreference.setGender(LoginActivity.this, "");
//                                                    finish();
//                                                    Clickable(true);
//                                                    loginProgressBar.setVisibility(View.GONE);
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(LoginActivity.this, R.string.failed_to_login, Toast.LENGTH_SHORT).show();
//                                                    Clickable(true);
//                                                    loginProgressBar.setVisibility(View.GONE);
//                                                }
//                                            });
//                                }
//                                check = false;
//                            } else {
//                                Toast.makeText(LoginActivity.this, getString(R.string.failed_to_login), Toast.LENGTH_SHORT).show();
//                                Clickable(true);
//                                loginProgressBar.setVisibility(View.GONE);
//                            }
//                        }
//                    });
//        } else {
//            Toast.makeText(this, getString(R.string.check_the_internet_connection), Toast.LENGTH_SHORT).show();
//            Clickable(true);
//            loginProgressBar.setVisibility(View.GONE);
//        }
//    }

    private void Clickable(boolean b) {
        if (b) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private boolean Connected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    public void RegisterButtonLoginScreenClicked(View view) {
        Intent toRegisterActivity = new Intent(this, RegisterActivity.class);
        startActivity(toRegisterActivity);
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
}
