package com.koma.logintest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.koma.logintest.fragments.SignUpFragment;
import com.koma.logintest.utils.Constants;
import com.koma.logintest.utils.Utils;
import com.koma.logintest.utils.ValidatorUtils;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    Context mContext;
    LinearLayout llLogin;
    FrameLayout flContent;
    TextView btnRegister;
    Button btnSignIn, tvFBLogin,tvGLogin;
    EditText etEmail, etPassword;
    private FirebaseAuth etAuth;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private CallbackManager callbackManager;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mPrefEdit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mContext = this;
        //all definations go here
        /******** UI Definations *****************/
        llLogin = (LinearLayout) findViewById(R.id.llLogin);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        flContent = (FrameLayout) findViewById(R.id.flContent);
        btnRegister = (TextView) findViewById(R.id.btnRegister);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPwd);
        tvFBLogin = (Button) findViewById(R.id.tvFBLogin);
        tvGLogin=(Button)findViewById(R.id.tvGLogin);

        /*****************************************/

        /****** other declarations ********/
        etAuth = FirebaseAuth.getInstance();

        mPref = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);

        mPrefEdit = mPref.edit();
        /************************************/
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        tvGLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });


        callbackManager = CallbackManager.Factory.create();
        initFBCallBacks();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flContent.setVisibility(View.VISIBLE);
                llLogin.setVisibility(View.GONE);
                loadFragmentInToView();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if internet is avail
                if (Utils.isNetworkAvailable(mContext)) {
                    //start main activity after login authentication
                    userLogin();
                } else {
                    Toast.makeText(mContext, "Please connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        tvFBLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // login fb in custom click
                /*if successfully logged in, control will be redirected to initFBCallBacks menthod*/
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email","public_profile"));
            }
        });
    }

    private void initFBCallBacks() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(mContext, "FB Login successful", Toast.LENGTH_SHORT).show();
               handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext, "canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void userLogin() {
        if (validate()) {
            etAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                savePreferences(Constants.EMAIL_LOGIN);
                                startMainScreen();
                            } else {
                                Toast.makeText(mContext, "incorrect username or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void savePreferences(String loginType) {

        mPrefEdit.putBoolean(Constants.PREF_IS_LOGGEDIN, true);
        mPrefEdit.putString(Constants.PREF_LOGINTYPE, loginType);
        mPrefEdit.commit();
    }

    private void startMainScreen() {
        startActivity(new Intent(mContext, LocationActivity.class));
        finishAffinity();
    }

    private boolean validate() {

        TextInputLayout tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        TextInputLayout tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);

        tilEmail.setError(null);
        tilPassword.setError(null);

        if (!ValidatorUtils.emailValidate(etEmail.getText().toString())) {
            tilEmail.setError("Please provide valid email");
            etEmail.setError("Please provide valid email");
            Toast.makeText(mContext, "Please provide valid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPassword.getText().toString().isEmpty()) {
            tilPassword.setError("Please provide password");
            return false;
        }

        return true;
    }

    private void loadFragmentInToView() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        // Toast.makeText(mContext, ""+getSupportFragmentManager().getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
        /*loading a first fragment for login*/
        if (count == 0)
            getSupportFragmentManager().beginTransaction().addToBackStack(null)
                    .add(R.id.flContent, new SignUpFragment()).commit();
        else
            getSupportFragmentManager().beginTransaction().addToBackStack(null)
                    .replace(R.id.flContent, new SignUpFragment()).commit();
    }

    @Override
    public void onBackPressed() {

        flContent.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);
        // super.onBackPressed();
    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       // client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
      //  client.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private String TAG="LoginActivity";

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            // update firebase here
            firebaseAuthWithGoogle(acct);

            Toast.makeText(mContext, ""+acct.getDisplayName(), Toast.LENGTH_SHORT).show();

         //   mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
          //  updateUI(false);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        etAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            savePreferences(Constants.GPLUS_LOGIN);
                            startMainScreen();
                        }
                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token.getToken());


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        etAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                             /*save contet to preferences*/
                            savePreferences(Constants.FB_LOGIN);
                            startMainScreen();
                        }

                        // ...
                    }
                });
    }

}