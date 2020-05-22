package tutorial.example.ecomm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.EmailAuthProvider;

import tutorial.example.ecomm.R;

import static tutorial.example.ecomm.StartActivity.auth;

@SuppressWarnings("ConstantConditions")
public class PasswordFragment extends Fragment {
    private TextInputEditText current, neuvo, confirm;
    private Button change;
    private InputMethodManager imm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pass, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initListeners();
    }

    private void initViews() {
        current = getView().findViewById(R.id.current_pass);
        neuvo = getView().findViewById(R.id.new_pass);
        confirm = getView().findViewById(R.id.confirm_pass);
        change = getView().findViewById(R.id.change_pass);

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
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c = current.getText().toString().trim();
                String n = neuvo.getText().toString().trim();
                String f = confirm.getText().toString().trim();
                if (check(c, n, f)) {
                    changePass(c, n);
                }
            }
        });
    }

    private void changePass(String c, final String n) {
        String e = auth.getCurrentUser().getEmail();
        auth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(e, c)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    auth.getCurrentUser().updatePassword(n).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            } else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean check(String c, String n, String f) {
        if (c.isEmpty()) {
            Toast.makeText(getContext(), "please enter your old password", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            current.requestFocus();
            return false;
        }
        if (n.isEmpty()) {
            Toast.makeText(getContext(), "please enter your new password", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            neuvo.requestFocus();
            return false;
        }
        if (!f.equals(n)) {
            Toast.makeText(getContext(), "please confirm your new password", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            confirm.requestFocus();
            return false;
        }
        return true;
    }
}
