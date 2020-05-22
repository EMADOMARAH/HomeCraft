package tutorial.example.ecomm.fragments.start;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import tutorial.example.ecomm.MainActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.StartActivity;
import tutorial.example.ecomm.models.UserM;

import static android.content.Intent.ACTION_VIEW;
import static tutorial.example.ecomm.StartActivity.auth;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class SignUpFragment extends Fragment {
    private TextInputEditText email, pass;
    private TextView login, terms, privacy;
    private Button signUp, facebook;
    private InputMethodManager imm;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initListeners();
    }

    private void initViews() {
        email = getView().findViewById(R.id.email_sign_up);
        pass = getView().findViewById(R.id.pass_sign_up);
        login = getView().findViewById(R.id.login_sign_up);
        terms = getView().findViewById(R.id.terms_sign_up);
        privacy = getView().findViewById(R.id.privacy_sign_up);
        signUp = getView().findViewById(R.id.signUp_sign_up);
        facebook = getView().findViewById(R.id.facebook_sign_up);
        progressBar = getView().findViewById(R.id.progress_sign_up);

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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((StartActivity) getActivity()).getViewPager().setCurrentItem(0);
            }
        });
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ACTION_VIEW, Uri.parse("https://www.termsofusegenerator.net/live.php?token=V04VZi3VtXr2mhWGOk6lQlzR3b1dGUpd")));
            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/privacy/view/3460fc7d9ec4a6bc70f0acca1310a833")));
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = email.getText().toString();
                String p = pass.getText().toString();
                if (check(e, p)) {
                    progressBar.setVisibility(View.VISIBLE);
                    signUp(e, p);
                }
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = email.getText().toString();
                String p = pass.getText().toString();
                if (check(e, p)) {
                    facebook(e, p);
                }
            }
        });
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

    private void signUp(final String email, String pass) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = task.getResult().getUser().getUid();

                    UserM userM = new UserM(id, email, "", "");
                    reference.child("Users").child(id).setValue(userM);

                    progressBar.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void facebook(String email, String pass) {
        Toast.makeText(getContext(), "hi from facebook" + email + pass, Toast.LENGTH_SHORT).show();/////////////////////////////////////////////////
    }
}
