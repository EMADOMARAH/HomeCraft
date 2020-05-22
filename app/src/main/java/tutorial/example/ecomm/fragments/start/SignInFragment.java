package tutorial.example.ecomm.fragments.start;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import tutorial.example.ecomm.MainActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.StartActivity;
import tutorial.example.ecomm.models.UserM;

import static tutorial.example.ecomm.StartActivity.USER;
import static tutorial.example.ecomm.StartActivity.admin;
import static tutorial.example.ecomm.StartActivity.auth;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class SignInFragment extends Fragment {
    private TextInputEditText email, pass;
    private TextView forgot, signUp;
    private Switch aSwitch;
    private Button login;
    private InputMethodManager imm;
    private ProgressBar progressBar;

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initListeners();
        initPreference();
    }

    private void initViews() {
        email = getView().findViewById(R.id.email_sign_in);
        pass = getView().findViewById(R.id.pass_sign_in);
        forgot = getView().findViewById(R.id.forgot_sign_in);
        signUp = getView().findViewById(R.id.signUp_sign_in);
        aSwitch = getView().findViewById(R.id.switch_sign_in);
        login = getView().findViewById(R.id.login_sign_in);
        progressBar = getView().findViewById(R.id.progress_sign_in);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initListeners() {
        getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = email.getText().toString().trim();
                resendPass(e);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((StartActivity) getActivity()).getViewPager().setCurrentItem(1);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = email.getText().toString().trim();
                String p = pass.getText().toString().trim();
                if (check(e, p)) {
                    progressBar.setVisibility(View.VISIBLE);
                    login(e, p);
                }
            }
        });
    }

    private void resendPass(String e) {
        auth.sendPasswordResetEmail(e).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com/mail/u/0/?tab=rm&ogbl#inbox")));
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initPreference() {
        sharedPreferences = getActivity().getSharedPreferences("autoLogin", 0);
        if (sharedPreferences.contains("email") && sharedPreferences.contains("pass")) {
            String e = sharedPreferences.getString("email", "");
            String p = sharedPreferences.getString("pass", "");
            email.setText(e);
            pass.setText(p);
        }
    }

    private boolean check(String e, String p) {
        if (e.isEmpty()) {
            Toast.makeText(getContext(), "please enter your email", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            email.requestFocus();
            return false;
        }
        if (p.isEmpty()) {
            Toast.makeText(getContext(), "please enter your password", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            pass.requestFocus();
            return false;
        }
        return true;
    }

    private void login(final String email, final String pass) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (aSwitch.isChecked()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString("email", email);
                        editor.putString("pass", pass);
                        editor.apply();
                    }
                    reference.child(USER).child(task.getResult().getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(getContext(), MainActivity.class));
                            admin = dataSnapshot.getValue(UserM.class).isAdmin();
                            getActivity().finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
