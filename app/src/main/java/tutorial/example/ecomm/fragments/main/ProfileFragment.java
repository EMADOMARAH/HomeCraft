package tutorial.example.ecomm.fragments.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import tutorial.example.ecomm.MainActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.SettingActivity;
import tutorial.example.ecomm.StartActivity;
import tutorial.example.ecomm.models.UserM;

import static android.app.Activity.RESULT_OK;
import static tutorial.example.ecomm.MainActivity.MYID;
import static tutorial.example.ecomm.StartActivity.CART;
import static tutorial.example.ecomm.StartActivity.FAVS;
import static tutorial.example.ecomm.StartActivity.MRTE;
import static tutorial.example.ecomm.StartActivity.ORDR;
import static tutorial.example.ecomm.StartActivity.SHIP;
import static tutorial.example.ecomm.StartActivity.USER;
import static tutorial.example.ecomm.StartActivity.VISA;
import static tutorial.example.ecomm.StartActivity.auth;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class ProfileFragment extends Fragment {
    private ProgressBar progressBar;
    private CircleImageView img;
    private TextView email, loc, pass, pay, ship, orders, del, log;
    private ImageButton edit;
    private EditText name;
    private Dialog dialog;
    private LinearLayout dialogLayout;
    private TextInputEditText pas, con;
    private Button yes, no;
    private InputMethodManager imm;
    private UserM userM;

    public void setUserM(UserM userM) {
        this.userM = userM;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initListeners();

        name.setText(userM.getUserName());
        email.setText(userM.getEmail());
        if (!userM.getProfPic().isEmpty()) {
            Picasso.get().load(userM.getProfPic()).into(img);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {
        progressBar = getView().findViewById(R.id.progress_profile);
        img = getView().findViewById(R.id.img_profile);
        name = getView().findViewById(R.id.name_profile);
        email = getView().findViewById(R.id.email_profile);
        loc = getView().findViewById(R.id.loc_profile);
        pass = getView().findViewById(R.id.pass_profile);
        pay = getView().findViewById(R.id.pay_profile);
        ship = getView().findViewById(R.id.ship_profile);
        orders = getView().findViewById(R.id.orders_profile);
        edit = getView().findViewById(R.id.edit_profile);
        log = getView().findViewById(R.id.logout_profile);
        del = getView().findViewById(R.id.del_profile);

        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);

        dialogLayout = dialog.findViewById(R.id.container_delete);
        pas = dialog.findViewById(R.id.pass_delete);
        con = dialog.findViewById(R.id.confirm_delete);
        yes = dialog.findViewById(R.id.yes_delete);
        no = dialog.findViewById(R.id.no_delete);

        edit.setSelected(false);

        name.setEnabled(false);
        name.setText("username");

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initListeners() {
        getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    name.setEnabled(false);
                    edit.setSelected(false);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //////////////////////////////////////////////////////////////////////////////////////
            }
        });
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingActivity.class).putExtra("mode", 0));
            }
        });
        ship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingActivity.class).putExtra("mode", 1));
            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingActivity.class).putExtra("mode", 2));
            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingActivity.class).putExtra("mode", 3));
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edit.isSelected()) {
                    name.requestFocus();
                    name.setEnabled(true);
                    edit.setSelected(true);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    getView().requestFocus();
                }
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .start(getContext(), ProfileFragment.this);
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                getActivity().finish();
                startActivity(new Intent(getContext(), StartActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        dialogLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String p = pas.getText().toString().trim();
                String c = con.getText().toString().trim();
                if (p.isEmpty()) {
                    Toast.makeText(getContext(), "please enter password", Toast.LENGTH_SHORT).show();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    pas.requestFocus();
                } else if (c.isEmpty()) {
                    Toast.makeText(getContext(), "please confirm password", Toast.LENGTH_SHORT).show();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    con.requestFocus();
                } else if (!c.equals(p)) {
                    Toast.makeText(getContext(), "password mismatch", Toast.LENGTH_SHORT).show();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    con.requestFocus();
                } else {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    String e = auth.getCurrentUser().getEmail();
                    auth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(e, p)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                reference.child(USER).child(MYID).removeValue();
                                reference.child(CART).child(MYID).removeValue();
                                reference.child(FAVS).child(MYID).removeValue();
                                reference.child(MRTE).child(MYID).removeValue();
                                reference.child(VISA).child(MYID).removeValue();
                                reference.child(SHIP).child(MYID).removeValue();
                                reference.child(ORDR).child(MYID).removeValue();
                                auth.getCurrentUser().delete();
                                startActivity(new Intent(getContext(), StartActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            } else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                dialogLayout.requestFocus();
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogLayout.requestFocus();
            }
        });
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!name.getText().toString().trim().equals(userM.getUserName())) {
                        reference.child(USER).child(MYID).child("userName").setValue(name.getText().toString().trim());
                        ((MainActivity) getActivity()).setNavName(name.getText().toString().trim());
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                progressBar.setVisibility(View.VISIBLE);
                Uri uri = result.getUri();
                Picasso.get().load(uri).error(R.drawable.ic_person).into(img);
                uploadDB(uri);
            }
        }
    }

    private void uploadDB(Uri uri) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + uri.getLastPathSegment());
        UploadTask uploadTask = storageReference.putFile(uri);

        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                return storageReference.getDownloadUrl();
            }
        });

        task.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                final String profilePic = task.getResult().toString();
                reference.child(USER).child(MYID).child("profPic").setValue(profilePic).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ((MainActivity) getActivity()).setNavPic(profilePic);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
}

