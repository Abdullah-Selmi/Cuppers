package com.abdullah.cuppers;

import androidx.annotation.NonNull;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    static final int RC_SIGN_IN = 1;
    Button googleButton, facebookButton;
    GoogleApiClient mGoogleSignInClient;
    FirebaseAuth firebaseAuth;
    GoogleSignInOptions googleSignInOptions;
    CallbackManager callbackManager;
    FirebaseFirestore db;
    GoogleSignInAccount googleSignInAccount;
    ProgressBar loginProgressBar;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        Clickable(false);
        loginProgressBar.setVisibility(View.VISIBLE);
        GoogleLogIn();
        Clickable(true);
        loginProgressBar.setVisibility(View.GONE);
    }

    public void findViews() {
        googleButton = findViewById(R.id.googleButton);
        firebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        facebookButton = findViewById(R.id.facebookButton);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Connected()) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    Clickable(true);
                    Toast.makeText(this, R.string.canceled, Toast.LENGTH_SHORT).show();
                    loginProgressBar.setVisibility(View.GONE);
                }
            } else {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            Toast.makeText(this, R.string.check_the_internet_connection, Toast.LENGTH_SHORT).show();
            Clickable(true);
            loginProgressBar.setVisibility(View.GONE);
        }
    }

    private void GoogleLogIn() {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Clickable(true);
                        loginProgressBar.setVisibility(View.GONE);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    public void googleButtonClicked(View view) {
        Clickable(false);
        loginProgressBar.setVisibility(View.VISIBLE);
        if (Connected()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            Toast.makeText(this, R.string.check_the_internet_connection, Toast.LENGTH_SHORT).show();
            loginProgressBar.setVisibility(View.GONE);
            Clickable(true);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        if (Connected()) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                AddingGoogleUsers();
                            } else {
                                Clickable(true);
                                loginProgressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, R.string.failed_to_login, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, R.string.check_the_internet_connection, Toast.LENGTH_SHORT).show();
            Clickable(true);
            loginProgressBar.setVisibility(View.GONE);
        }
    }

    private void AddingGoogleUsers() {
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (Connected()) {
            db.collection(getString(R.string.users))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getId().equals(googleSignInAccount.getEmail())) {
                                        SaveSharedPreference.setEmail(LoginActivity.this, googleSignInAccount.getEmail());
                                        SaveSharedPreference.setUserName(LoginActivity.this, googleSignInAccount.getDisplayName());
                                        SaveSharedPreference.setPhoneNumber(LoginActivity.this, "");
                                        check = true;
                                        finish();
                                        Clickable(true);
                                        loginProgressBar.setVisibility(View.GONE);
                                    }
                                }
                                if (!check) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put(getString(R.string.username), googleSignInAccount.getDisplayName());
                                    user.put(getString(R.string.email), googleSignInAccount.getEmail());
                                    user.put(getString(R.string.phone), "");

                                    db.collection(getString(R.string.users))
                                            .document(googleSignInAccount.getEmail())
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    SaveSharedPreference.setEmail(LoginActivity.this, googleSignInAccount.getEmail());
                                                    SaveSharedPreference.setUserName(LoginActivity.this, googleSignInAccount.getDisplayName());
                                                    SaveSharedPreference.setPhoneNumber(LoginActivity.this, "");
                                                    finish();
                                                    Clickable(true);
                                                    loginProgressBar.setVisibility(View.GONE);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(LoginActivity.this, R.string.failed_to_login, Toast.LENGTH_SHORT).show();
                                                    Clickable(true);
                                                    loginProgressBar.setVisibility(View.GONE);
                                                }
                                            });
                                }
                                check = false;
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.failed_to_login), Toast.LENGTH_SHORT).show();
                                Clickable(true);
                                loginProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            Toast.makeText(this, getString(R.string.check_the_internet_connection), Toast.LENGTH_SHORT).show();
            Clickable(true);
            loginProgressBar.setVisibility(View.GONE);
        }
    }

    public void facebookButtonClicked(View view) {
        Clickable(false);
        loginProgressBar.setVisibility(View.VISIBLE);
        if (Connected()) {
            FacebookLogin();
        } else {
            Toast.makeText(this, getString(R.string.check_the_internet_connection), Toast.LENGTH_SHORT).show();
            Clickable(true);
            loginProgressBar.setVisibility(View.GONE);
        }
    }

    private void FacebookLogin() {
        if (Connected()) {
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                    Arrays.asList(getString(R.string.email), getString(R.string.public_profile)));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivity.this, getString(R.string.canceled), Toast.LENGTH_SHORT).show();
                    Clickable(true);
                    loginProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_while_logging_in), Toast.LENGTH_SHORT).show();
                    Clickable(true);
                    loginProgressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.check_the_internet_connection), Toast.LENGTH_SHORT).show();
            Clickable(true);
            loginProgressBar.setVisibility(View.GONE);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        if (Connected()) {
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                AddingFacebookUsers(user);
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.failed_to_login, Toast.LENGTH_SHORT).show();
                                Clickable(true);
                                loginProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            Toast.makeText(this, getString(R.string.check_the_internet_connection), Toast.LENGTH_SHORT).show();
            Clickable(true);
            loginProgressBar.setVisibility(View.GONE);
        }
    }

    private void AddingFacebookUsers(final FirebaseUser firebaseUser) {
        if (Connected()) {
            db.collection(getString(R.string.users))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getId().equals(firebaseUser.getEmail())) {
                                        SaveSharedPreference.setEmail(LoginActivity.this, firebaseUser.getEmail());
                                        SaveSharedPreference.setUserName(LoginActivity.this, firebaseUser.getDisplayName());
                                        SaveSharedPreference.setPhoneNumber(LoginActivity.this, "");
                                        check = true;
                                        finish();
                                        Clickable(true);
                                        loginProgressBar.setVisibility(View.GONE);
                                    }
                                }
                                if (!check) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put(getString(R.string.username), firebaseUser.getDisplayName());
                                    user.put(getString(R.string.email), firebaseUser.getEmail());
                                    user.put(getString(R.string.phone), "");

                                    db.collection(getString(R.string.users))
                                            .document(firebaseUser.getEmail())
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    SaveSharedPreference.setEmail(LoginActivity.this, firebaseUser.getEmail());
                                                    SaveSharedPreference.setUserName(LoginActivity.this, firebaseUser.getDisplayName());
                                                    SaveSharedPreference.setPhoneNumber(LoginActivity.this, "");
                                                    finish();
                                                    Clickable(true);
                                                    loginProgressBar.setVisibility(View.GONE);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(LoginActivity.this, getString(R.string.failed_to_login), Toast.LENGTH_SHORT).show();
                                                    Clickable(true);
                                                    loginProgressBar.setVisibility(View.GONE);
                                                }
                                            });
                                }
                                check = false;
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.failed_to_login), Toast.LENGTH_SHORT).show();
                                Clickable(true);
                                loginProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            Toast.makeText(this, getString(R.string.check_the_internet_connection), Toast.LENGTH_SHORT).show();
            Clickable(true);
            loginProgressBar.setVisibility(View.GONE);
        }
    }

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

    public static class SaveSharedPreference {
        static final String PREF_EMAIL = "Email";
        static final String PREF_USER_NAME = "UserName";
        static final String PREF_PHONE_NUMBER = "PhoneNumber";

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
    }

}
