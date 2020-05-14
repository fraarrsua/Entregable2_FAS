package us.master.entregable1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 0x152;
    private FirebaseAuth mAuth;
    private Button siginButtonGoogle;
    private Button siginButtonMail;
    private Button loginButtonSigup;
    private ProgressBar progressBar;
    private TextInputLayout loginEmailParent;
    private TextInputLayout loginPassParent;
    private AutoCompleteTextView loginEmail;
    private AutoCompleteTextView loginPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBarLoading);
        loginEmail = findViewById(R.id.login_email_et);
        loginPass = findViewById(R.id.login_pass_et);
        loginEmailParent = findViewById(R.id.login_email);
        loginPassParent = findViewById(R.id.login_pass);
        siginButtonGoogle = findViewById(R.id.login_button_google);
        siginButtonMail = findViewById(R.id.login_button_mail);
        loginButtonSigup = findViewById(R.id.login_button_register);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .build();
        siginButtonGoogle.setOnClickListener(l -> attempLoginGoogle(googleSignInOptions));

        siginButtonMail.setOnClickListener(l -> attempLoginEmail());

        loginButtonSigup.setOnClickListener(l -> redirectSignUpActivity());
    }

    private void attempLoginGoogle(GoogleSignInOptions googleSignInOptions) {
        GoogleSignInClient googleSigIn = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = googleSigIn.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = result.getResult(ApiException.class);
                assert account != null;
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                if (mAuth == null)
                    mAuth = FirebaseAuth.getInstance();

                if (mAuth != null) {
                    mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            checkUserDatabaseLogin(user);
                        } else {
                            showErrorDialogMail();
                        }
                    });
                } else {
                    showGooglePlayServicesError();
                }
            } catch (ApiException e) {
                showErrorDialogMail();
            }
        }
    }

    private void attempLoginEmail() {
        loginEmailParent.setError(null);
        loginPassParent.setError(null);

        if (loginEmail.getText().length() == 0) {
            loginEmailParent.setErrorEnabled(true);
            loginEmailParent.setError(getString(R.string.Login_mail_error_1));
        } else if (loginPass.getText().length() == 0) {
            loginPassParent.setErrorEnabled(true);
            loginPassParent.setError(getString(R.string.Login_mail_error_2));
        } else {
            signInEmail();
        }


    }

    private void signInEmail() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPass.getText().toString()).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful() || task.getResult().getUser() == null) {
                    showErrorDialogMail();
                } else if (!task.getResult().getUser().isEmailVerified()) {
                    showErrorEmailVerified(task.getResult().getUser());
                } else {
                    FirebaseUser user = task.getResult().getUser();
                    checkUserDatabaseLogin(user);
                }
            });
        } else {
            showGooglePlayServicesError();
        }
    }

    private void showGooglePlayServicesError() {
        Snackbar.make(loginButtonSigup, R.string.login_google_play_services_error, Snackbar.LENGTH_LONG).setAction(R.string.login_download_gps, view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gps_download_uri))));
            } catch (Exception ex) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.market_download_uri))));
            }
        });
    }

    private void checkUserDatabaseLogin(FirebaseUser user) {
        //dummy
        Toast.makeText(this, String.format(getString(R.string.login_completed), user.getEmail()), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LocationActivity.class));
        finish();
    }

    private void redirectSignUpActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(SignupActivity.EMAIL_PARAM, loginEmail.getText().toString());
        startActivity(intent);

    }

    private void showErrorEmailVerified(FirebaseUser user) {
        hideLoginButton(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_verified_email_error)
                .setPositiveButton(R.string.login_verified_email_ok, ((dialog, which) -> {
                    user.sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Snackbar.make(loginEmail, R.string.login_verified_mail_error_sent, Snackbar.LENGTH_SHORT).show();

                        } else {
                            Snackbar.make(loginEmail, R.string.login_verified_mail_no_sent, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                })).setNegativeButton(R.string.login_verified_error_cancel, (dialog, which) -> {
        }).show();
    }

    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
    }

    private void showErrorDialogMail() {
        hideLoginButton(false);
        Snackbar.make(siginButtonMail, getString(R.string.login_email_access), Snackbar.LENGTH_SHORT).show();

    }

    private void hideLoginButton(boolean hide) {
        TransitionSet transactionSet = new TransitionSet();
        Transition layoutFade = new AutoTransition();
        layoutFade.setDuration(1000);
        transactionSet.addTransition(layoutFade);

        if (hide) {
            progressBar.setVisibility(View.VISIBLE);
            siginButtonMail.setVisibility(View.GONE);
            siginButtonGoogle.setVisibility(View.GONE);
            loginButtonSigup.setVisibility(View.GONE);
            loginEmailParent.setEnabled(false);
            loginPassParent.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            siginButtonMail.setVisibility(View.VISIBLE);
            siginButtonGoogle.setVisibility(View.VISIBLE);
            loginButtonSigup.setVisibility(View.VISIBLE);
            loginEmailParent.setEnabled(true);
            loginPassParent.setEnabled(true);
        }
    }
}

