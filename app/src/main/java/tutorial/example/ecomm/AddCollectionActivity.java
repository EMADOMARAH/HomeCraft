package tutorial.example.ecomm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import tutorial.example.ecomm.models.CollectionM;

import static tutorial.example.ecomm.StartActivity.COLM;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class AddCollectionActivity extends AppCompatActivity {
    TextInputEditText name;
    EditText desc;
    ImageView img, pic;
    Button upload;
    ProgressBar progressBar;
    CollectionM collectionM;

    int cropFlag = 0;

    Uri uri1, uri2;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collection);
        initViews();
        initListeners();
        if (collectionM != null) {
            populateUI();
        }
    }

    @SuppressLint("SetTextI18n")
    private void populateUI() {
        img.setImageTintList(null);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        pic.setScaleType(ImageView.ScaleType.FIT_CENTER);
        upload.setText("Edit collection");

        Picasso.get().load(collectionM.getPic()).into(pic);
        Picasso.get().load(collectionM.getImg()).into(img);

        uri1 = Uri.parse(collectionM.getImgName());
        uri2 = Uri.parse(collectionM.getPicName());

        name.setText(collectionM.getName());
        desc.setText(collectionM.getDesc());
    }

    private void initViews() {
        collectionM = (CollectionM) getIntent().getSerializableExtra("collectionM");

        if (collectionM != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        setSupportActionBar(((Toolbar) findViewById(R.id.toolbar_add_collection)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        progressBar = findViewById(R.id.progress_add_collection);
        name = findViewById(R.id.name_add_collection);
        desc = findViewById(R.id.desc_add_collection);
        img = findViewById(R.id.image_add_collection);
        pic = findViewById(R.id.pic_add_collection);
        upload = findViewById(R.id.add_add_collection);

        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initListeners() {
        findViewById(R.id.container_add_collection).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .start(AddCollectionActivity.this);
                cropFlag = 1;
            }
        });
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(AddCollectionActivity.this);
                cropFlag = 2;
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString().trim();
                String d = desc.getText().toString().trim();
                if (check(n, d)) {
                    uploadIMG(n, d);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (cropFlag == 1 && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                uri1 = result.getUri();
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                img.setImageTintList(null);
                Picasso.get().load(uri1).into(img);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }

        if (cropFlag == 2 && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                uri2 = result.getUri();
                pic.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Picasso.get().load(uri2).into(pic);
            }
        }
    }

    private boolean check(String n, String d) {
        if (n.isEmpty()) {
            Toast.makeText(this, "please enter collection name", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            name.requestFocus();
            return false;
        }
        if (d.isEmpty()) {
            Toast.makeText(this, "please describe the collection", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            desc.requestFocus();
            return false;
        }
        if (uri2 == null) {
            Toast.makeText(this, "please set thumbnail of the collection", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (uri1 == null) {
            Toast.makeText(this, "please set photo of the collection", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadIMG(final String n, final String d) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + uri1.getLastPathSegment());
        UploadTask uploadTask = storageReference.putFile(uri1);

        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                return storageReference.getDownloadUrl();
            }
        });

        task.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                final String uri1S = task.getResult().toString();
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + uri2.getLastPathSegment());
                UploadTask uploadTask = storageReference.putFile(uri2);

                Task<Uri> task2 = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        return storageReference.getDownloadUrl();
                    }
                });

                task2.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String uri2S = task.getResult().toString();
                        String uri1N = uri1.toString();
                        String uri2N = uri2.toString();
                        uploadDB(n, uri2S, uri2N, uri1S, uri1N, d);
                    }
                });
            }
        });
    }

    private void uploadDB(String n, String uri2S, String uri2N, String uri1S, String uri1N, String d) {
        CollectionM collectionM = new CollectionM(n, uri2S, uri2N, uri1S, uri1N, d);
        this.collectionM = collectionM;

        reference.child(COLM).child(n).setValue(collectionM);

        progressBar.setVisibility(View.INVISIBLE);
        startActivity(new Intent(AddCollectionActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
