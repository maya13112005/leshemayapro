package com.example.leshemayapro.activities;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leshemayapro.classes.TextValidator;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;

import com.example.leshemayapro.R;
import com.example.leshemayapro.classes.FirebaseManager;
import com.example.leshemayapro.classes.LanguageManager;
import com.example.leshemayapro.classes.PrefManager;
import com.example.leshemayapro.databinding.ActivityLoginBinding;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;


import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private boolean isEmailValid = false, isPasswordValid = false;
    private static final int RC_SIGN_IN = 9001;

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    private ActivityLoginBinding binding;
    private CallbackManager callbackManager;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    public void onStart()
    {
        super.onStart();
        startSplashScreen();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (FirebaseManager.isSignedIn())
            startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        callbackManager = CallbackManager.Factory.create();
        handleSharedPreferences();
        setListeners();
    }

    private void handleSharedPreferences()
    {
        PrefManager prefsManager = new PrefManager(this);
        LanguageManager languageManager = new LanguageManager(this);
        languageManager.setLanguage(prefsManager.getPrefString(PrefManager.KEY_LANGUAGE, "en"));
    }

    private void setListeners()
    {
        binding.username.addTextChangedListener(new TextValidator(binding.username) {
            @Override
            public void validate(TextView textView, String text) {
                validateEmailOrPassword(
                        binding.textInputLayout,
                        text,
                        R.string.invalid_email,
                        true);
            }
        });
        binding.helpPassword.setOnClickListener(this::showPasswordRules);
        binding.password.addTextChangedListener(new TextValidator(binding.password) {
            @Override
            public void validate(TextView textView, String text) {
                validateEmailOrPassword(
                        binding.textInputLayout1,
                        text,
                        R.string.invalid_password,
                        false);
            }
        });
        Intent toRegister = new Intent(this, RegisterActivity.class);
        binding.newAccount.setOnClickListener(view ->
                startActivity(toRegister));
        binding.showPassword.setOnCheckedChangeListener((compoundButton, isChecked) ->
                changePasswordState(isChecked));
        binding.loginButton.setOnClickListener(view -> facebookLogin());
        binding.googleButton.setOnClickListener(view -> googleLogin());
        binding.userAndPasswordLogin.setOnClickListener(view -> login());
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

    private void startSplashScreen()
    {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() ->
        {
            // Keep the splash screen on as long as
            // the user is not interacting with the app.
            return false;
        });
    }

    private void validateEmailOrPassword(TextInputLayout inputLayout, String text, int resourceID, boolean isEmail) {
        if (isEmail) isEmailValid = isTextValidUsingRegex(text, true);
        else isPasswordValid = isTextValidUsingRegex(text, false);
        if (isTextValidUsingRegex(text, isEmail)) {
            inputLayout.setError(null);
        } else {
            inputLayout.setError(getString(resourceID));
        }
        binding.userAndPasswordLogin.setEnabled(isEmailValid && isPasswordValid);
    }

    private boolean isTextValidUsingRegex(String text, boolean isEmail) {
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

    private void changePasswordState(boolean isChecked) {
        binding.password.setTransformationMethod(
                isChecked ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
        binding.password.setSelection(binding.password.getText().length());
    }

    private void login() {
        binding.userAndPasswordLogin.setEnabled(false);
        String emailAddress = binding.username.getText().toString().trim();
        String pass = binding.password.getText().toString().trim();
        loginWithEmailAndPass(emailAddress, pass);
    }

    private void loginWithEmailAndPass(String email, String password) {
        // [START sign_in_with_email]
        FirebaseManager.getAuth()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, this::handleTaskResult);
        // [END sign_in_with_email]
    }

    private void facebookLogin() {
        FacebookSdk.sdkInitialize(this, () -> {});
        LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));

        LoginManager.getInstance()
                .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(@NonNull FacebookException exception) {
                        Log.w(TAG, "facebook:onError", exception);
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token.getUserId());
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseManager.getAuth()
                .signInWithCredential(credential)
                .addOnCompleteListener(this, this::handleTaskResult);
    }

    private void googleLogin() {
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();

        // [START config_sign_in]
        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions
//                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.idToken))
//                .requestEmail()
//                .build();
//        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_sign_in]

//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        // registerForActivityResult(new ActivityResultContracts.GetContent(), result -> Log.d(TAG, result.toString()));
//        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent
        // from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = googleCredential.getGoogleIdToken();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with Firebase.
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    FirebaseManager.getAuth().signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, this::handleTaskResult);
                }
            } catch (ApiException e) {
                // ...
            }
        }
        Log.d(TAG, "onActivityResult requestCode: " + requestCode + "\nresultCode: " + resultCode);
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK)
        {
            Log.d(TAG, "oooooooooooooooooooooooooo onActivityResult: data = " + data.getExtras().toString());
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.d(TAG, "onActivityResult: data ahhhhhhhhhhhhh = " + data.getExtras().toString());
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: data = " + data.getExtras().toString());
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d(TAG, "firebaseAuthWithGoogle: help!!!!!!!!!!!!!!");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseManager.getAuth()
                .signInWithCredential(credential)
                .addOnCompleteListener(this, this::handleTaskResult);
    }

    private void handleTaskResult(Task<AuthResult> task) {
        if (task.isSuccessful()) // Sign in success, update UI with the signed-in user's information
            sendUserData(FirebaseManager.getAuth().getCurrentUser());
        else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "handleTaskResult:failure", task.getException());
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            if (Objects.requireNonNull(Objects.requireNonNull(task.getException())
                            .getMessage())
                    .equals("The password is invalid or the user does not have a password."))
                Snackbar.make(binding.userAndPasswordLogin, R.string.wrong_password, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.clear, view -> binding.password.setText(""))
                        .show();
            binding.userAndPasswordLogin.setEnabled(true);
        }
    }

    private void sendUserData(FirebaseUser user) {
        DatabaseReference myRef2 = FirebaseManager.getDataRef("users/" + user.getUid() + "/email");
//        myRef2.setValue(user.getEmail());
        startActivity(new Intent(this, MainActivity.class));
    }
}