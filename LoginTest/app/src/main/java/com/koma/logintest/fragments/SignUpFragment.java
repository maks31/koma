package com.koma.logintest.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.koma.logintest.MainActivity;
import com.koma.logintest.R;
import com.koma.logintest.utils.Utils;
import com.koma.logintest.utils.ValidatorUtils;

import static com.koma.logintest.R.id.btnRegister;
import static com.koma.logintest.R.id.etPwdConfirm;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {



    private View view;

    private EditText etEmail,etPwd,etUsername,etPhn,etPwdConfirm;
    private Button btnRegister;
    private Context mContext ;

//firebase objects defnation
    private FirebaseAuth mAuth;
    private String TAG="SignUpFragment";
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.signup_frag, container, false);

        mContext=getActivity();

        mAuth=FirebaseAuth.getInstance();
        //Initiate the view objects here

        etUsername=(EditText)view.findViewById(R.id.etUsername);
        etEmail=(EditText)view.findViewById(R.id.etEmail);
        etPhn = (EditText)view.findViewById(R.id.etPhone);
        etPwd=(EditText)view.findViewById(R.id.etPwd);
        etPwdConfirm=(EditText)view.findViewById(R.id.etPwdConfirm);
        btnRegister= (Button) view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();



        if(id==R.id.btnRegister){
                if(Utils.isNetworkAvailable(mContext)){
                    if(validated())

                    {
                     //   Toast.makeText(mContext,"Signup Successful",Toast.LENGTH_SHORT).show();
                       userRegister(etEmail.getText().toString(),etPwd.getText().toString());
                    }
                }else Toast.makeText(mContext, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void userRegister(String userName,String password) {

        mAuth.createUserWithEmailAndPassword(userName,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG , "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, task.getException().getMessage()+"", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validated() {
        TextInputLayout tilEmail= (TextInputLayout) view.findViewById(R.id.tilEmail);
        TextInputLayout tilPassword= (TextInputLayout) view.findViewById(R.id.tilPassword);
        TextInputLayout tilPasswordConfirm= (TextInputLayout) view.findViewById(R.id.tilPasswordConfirm);
        TextInputLayout tilPhone= (TextInputLayout) view.findViewById(R.id.tilPhone);
        TextInputLayout tilUsername = (TextInputLayout) view.findViewById(R.id.tilUsername);

        /*remove error mesage if every thing is valid in all fields*/
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilPhone.setError(null);
        tilUsername.setError(null);
        boolean properSubmit = true;

        if(!ValidatorUtils.userNameValidate(etUsername.getText().toString())){
            tilUsername.setError("User name should be atleast 3 characters");
            properSubmit =  false;
        }

        if(!ValidatorUtils.userNameValidate(etUsername.getText().toString())){
            tilUsername.setError("User name should be atleast 3 characters");
            properSubmit =  false;
        }
        if(!ValidatorUtils.phoneValidate(etPhn.getText().toString())){
            tilPhone.setError("Please provide valid phone number");
            properSubmit =  false;
        }


        if(!ValidatorUtils.emailValidate(etEmail.getText().toString())){
             tilEmail.setError("Please provide valid email address");
            properSubmit =  false;
        }


        if(!ValidatorUtils.passwordValidate(etPwd.getText().toString())){
            tilPassword.setError("Password should be atleasst 6 characters");
            properSubmit =  false;
        }

        if(!etPwd.getText().toString().equalsIgnoreCase(etPwdConfirm.getText().toString())){
            tilPasswordConfirm.setError("Please provide same password as above");
            properSubmit =  false;
        }



        return  properSubmit;
    }
}
