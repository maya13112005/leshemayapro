package com.example.leshemayapro.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leshemayapro.R;
import com.example.leshemayapro.classes.FirebaseManager;
import com.example.leshemayapro.classes.TextValidator;
import com.example.leshemayapro.classes.User;
import com.example.leshemayapro.databinding.ActivityRegisterBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private boolean isEmailValid = false, isPasswordValid = false;
    private final String TAG = this.getClass().getSimpleName();
    private ActivityRegisterBinding binding;
    public static FirebaseAuth firebaseAuth;
    public static String Uid, email, phone, fn, un;
    private int numberOfIncorrectAttempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();

        setListeners();
//        binding.signUp.setOnClickListener(view ->
//                startActivity(new Intent(this, LoginActivity.class)));
//
//        binding.userAndPasswordSignup.setOnClickListener(this::addToDB);
    }

    private void setListeners ()
    {
        binding.username
                .addTextChangedListener(new TextValidator(binding.username)
                {
                    @Override
                    public void validate (TextView textView, String text)
                    {
                        validateEmailOrPassword(binding.textInputLayout, text,
                                R.string.invalid_email, true);
                    }
                });

        binding.password
                .addTextChangedListener(new TextValidator(binding.password)
                {
                    @Override
                    public void validate (TextView textView, String text)
                    {
                        validateEmailOrPassword(binding.textInputLayout1, text,
                                R.string.invalid_password, false);
                    }
                });
        binding.helpPassword.setOnClickListener(this::showPasswordRules);
        binding.showPassword
                .setOnCheckedChangeListener((compoundButton, isChecked) ->
                        changePasswordState(isChecked));
        binding.userAndPasswordSignup
                .setOnClickListener(view -> login());
        binding.signUp
                .setOnClickListener(view ->
                        startActivity(new Intent(this, LoginActivity.class)));
    }

    private void validateEmailOrPassword (TextInputLayout inputLayout, String text, int resourceID, boolean isEmail)
    {
        if (isEmail) isEmailValid = isTextValidUsingRegex(text, true);
        else isPasswordValid = isTextValidUsingRegex(text, false);
        if (isTextValidUsingRegex(text, isEmail))
        {
            inputLayout.setError(null);
            numberOfIncorrectAttempts = 0;
        }
        else
        {
            inputLayout.setError(getString(resourceID));
            numberOfIncorrectAttempts++;
        }
        binding.userAndPasswordSignup.setEnabled(isEmailValid && isPasswordValid);
        if(numberOfIncorrectAttempts > 10)
            binding.helpPassword.setVisibility(View.VISIBLE);
    }

    private boolean isTextValidUsingRegex (String text, boolean isEmail)
    {
        String regex;
        if (isEmail)
            regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        else
			/*
            ^                                   # start of line
            (?=.*[0-9])                         # positive lookahead, digit [0-9]
            (?=.*[a-z])                         # positive lookahead, one lowercase character [a-z]
            (?=.*[A-Z])                         # positive lookahead, one uppercase character [A-Z]
            (?=.*[!@#&()–[{}]:;',?/*~$^+=<>])   # positive lookahead, one of the special character in this [..]
            .                                   # matches anything
            {8,20}                              # length at least 8 characters and maximum of 20 characters
            $                                   # end of line
            */
            regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    private void showPasswordRules (View view)
    {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
        builder.setTitle(R.string.password_rules)
                .setMessage(getString(R.string.password_rule) + "!@#&()–[{}]:;',?/*~$^+=<>")
                .setNegativeButton("ok", (dialogInterface, i) -> {})
                .setIcon(R.drawable.ic_password)
                .setCancelable(true)
                .show();
    }

    private void changePasswordState (boolean isChecked)
    {
        binding.password.setTransformationMethod(
                isChecked ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
        binding.password.setSelection(binding.password.getText().length());
    }

    private void login ()
    {
        binding.userAndPasswordSignup.setEnabled(false);
        String emailAddress = binding.username.getText().toString().trim();
        String pass = binding.password.getText().toString().trim();
        if (validatePhone())
            createAccount(emailAddress, pass);
        else Toast.makeText(this, "not a valid phone number", Toast.LENGTH_SHORT).show();
    }

    private boolean validatePhone()
    {
        return binding.phoneText.getText().toString().length() == 10;
    }

    private void createAccount (String email, String password)
    {
        // [START create_user_with_email]
        FirebaseManager
                .getAuth()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful())
                    {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = FirebaseManager.getAuth().getCurrentUser();
                        if (user != null) {
                            sendUserData(user);
                        }
                    }
                    else
                    {
                        // If sign in fails, display a message to the user.
                        if (task.getException().getMessage()
                                .equals("The email address is already in use by another account."))
                            Snackbar.make(binding.userAndPasswordSignup, R.string.email_occupied, Snackbar.LENGTH_SHORT)
                                    .setAction("clear",view -> binding.username.setText(""))
                                    .show();
                        Toast.makeText(this, "auth failed", Toast.LENGTH_SHORT).show();
                        binding.userAndPasswordSignup.setEnabled(true);
                        // signIn(email, password);
                    }
                });
        // [END create_user_with_email]
    }

    private void sendUserData(FirebaseUser user)
    {
        Uid = user.getUid();
        DatabaseReference myRef1 = FirebaseManager.getDataRef("users/" + Uid + "/user");
        User newUser = new User(Uid, binding.fullNameText.getText().toString(), user.getEmail(), binding.phoneText.getText().toString());
        myRef1.setValue(newUser);
        startActivity(new Intent(this, MainActivity.class));
    }
}