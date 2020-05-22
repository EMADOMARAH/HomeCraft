package tutorial.example.ecomm;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import tutorial.example.ecomm.models.ProductM;

import static tutorial.example.ecomm.MainActivity.MYID;
import static tutorial.example.ecomm.StartActivity.CART;
import static tutorial.example.ecomm.StartActivity.CATG;
import static tutorial.example.ecomm.StartActivity.COLL;
import static tutorial.example.ecomm.StartActivity.COLM;
import static tutorial.example.ecomm.StartActivity.DESG;
import static tutorial.example.ecomm.StartActivity.FAVS;
import static tutorial.example.ecomm.StartActivity.MRTE;
import static tutorial.example.ecomm.StartActivity.PROD;
import static tutorial.example.ecomm.StartActivity.RATE;
import static tutorial.example.ecomm.StartActivity.admin;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class ProductActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText name, price, desg, desc, catg, count;
    TextView rate;
    RatingBar ratingBar;
    Switch aSwitch;
    FloatingActionButton fab, edit, del, coll, done, add, thumb, image;
    Button cart;
    RecyclerView recyclerView;
    ScrollView scrollView;

    InputMethodManager imm;

    boolean upload = false, editMode = false;

    View viewCart, viewSwitch, viewRecycler, viewProduct, viewFabs;

    Dialog dialog;
    RecyclerView recyclerViewDialog;
    MyAdapterDialog myAdapterDialog;
    List<String> collectionStrings;
    int selPos = -1;

    ProductM productM;
    List<ProductM> productMList;

    MyAdapter myAdapter;

    int imgType;

    Spinner spinner, spinner2;
    ArrayAdapter<String> adapter, adapter2;
    List<String> stringList, stringList2;

    List<String> pics, pictures;
    MySliderAdapter mySliderAdapter;
    SliderView sliderView;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        initViews();
        initProduct(getIntent());
        initListeners();
    }

    private void initProduct(Intent intent) {
        upload = intent.getStringExtra("productM") == null;
        if (upload) {
            productM = new ProductM();
            editMode(true);
            initAdapter();
        } else {
            reference.child(PROD).child(intent.getStringExtra("category")).child(intent.getStringExtra("productM")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    productM = dataSnapshot.getValue(ProductM.class);
                    editMode(false);
                    initAdapter();
                    updateUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initProduct(intent);
    }

    private void initViews() {
        getWindow().setStatusBarColor(Color.BLACK);

        toolbar = findViewById(R.id.toolbar_product);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.name_product);
        price = findViewById(R.id.price_product);
        desg = findViewById(R.id.desg_product);
        desc = findViewById(R.id.desc_product);
        catg = findViewById(R.id.catg_product);
        count = findViewById(R.id.count_product);
        rate = findViewById(R.id.rate_product);
        fab = findViewById(R.id.fab_product);
        aSwitch = findViewById(R.id.switch_product);
        edit = findViewById(R.id.edit_product);
        coll = findViewById(R.id.coll_product);
        done = findViewById(R.id.done_product);
        add = findViewById(R.id.add_pic_product);
        del = findViewById(R.id.del_product);
        cart = findViewById(R.id.cart_product);
        ratingBar = findViewById(R.id.rating_product);
        scrollView = findViewById(R.id.scroll_product);
        progress = findViewById(R.id.progress_product);

        spinner = findViewById(R.id.spinner_catg_product);
        spinner2 = findViewById(R.id.spinner_desg_product);

        stringList = new ArrayList<>();
        adapter = new ArrayAdapter<>(ProductActivity.this, R.layout.item_spinner, stringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        stringList2 = new ArrayList<>();
        adapter2 = new ArrayAdapter<>(ProductActivity.this, R.layout.item_spinner, stringList2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        thumb = findViewById(R.id.add_thumb_product);
        image = findViewById(R.id.add_image_product);

        viewProduct = findViewById(R.id.container_others);
        viewFabs = findViewById(R.id.container_fabs);
        viewCart = findViewById(R.id.container_cart);
        viewSwitch = findViewById(R.id.container_switch);
        viewRecycler = findViewById(R.id.container_recycler);

        pics = new ArrayList<>();
        pictures = new ArrayList<>();
        mySliderAdapter = new MySliderAdapter();
        sliderView = findViewById(R.id.slider_product);
        sliderView.setSliderAdapter(mySliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.COLOR);

        if (admin) {
            edit.show();
        }

        imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);

        dialog = new Dialog(ProductActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_collections);

        collectionStrings = new ArrayList<>();
        myAdapterDialog = new MyAdapterDialog();
        recyclerViewDialog = dialog.findViewById(R.id.recycler_dialog_collections);
        recyclerViewDialog.setLayoutManager(new LinearLayoutManager(recyclerViewDialog.getContext(), RecyclerView.VERTICAL, false));
        recyclerViewDialog.setAdapter(myAdapterDialog);

        productMList = new ArrayList<>();
        myAdapter = new MyAdapter();

        recyclerView = findViewById(R.id.recycler_product);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(myAdapter);
    }

    private void initAdapter() {
        reference.child(COLM).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                collectionStrings.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    collectionStrings.add(ds.getKey());
                }
                if (!upload)
                    if (productM.getCollection() != null) if (!productM.getCollection().isEmpty())
                        selPos = collectionStrings.indexOf(productM.getCollection());
                myAdapterDialog.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child(CATG).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stringList.clear();
                stringList.add("Add new");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    stringList.add(ds.getValue(String.class));
                }
                adapter.notifyDataSetChanged();
                if (!upload) {
                    spinner.setSelection(stringList.indexOf(productM.getCategory()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child(DESG).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stringList2.clear();
                stringList2.add("Add new");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    stringList2.add(ds.getValue(String.class));
                }
                adapter2.notifyDataSetChanged();
                if (!upload) {
                    spinner2.setSelection(stringList2.indexOf(productM.getDesign()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFireBase() {
        reference.child(PROD).child(productM.getCategory()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productMList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!productM.getProductID().equals(ds.getValue(ProductM.class).getProductID())) {
                        productMList.add(ds.getValue(ProductM.class));
                    }
                }
                myAdapter.notifyDataSetChanged();
                if (productMList.isEmpty()) {
                    viewRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child(FAVS).child(MYID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(productM.getProductID())) {
                    fab.setSelected(true);
                } else {
                    fab.setSelected(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child(PROD).child(productM.getCategory()).child(productM.getProductID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child(PROD)
                        .child(productM.getCategory())
                        .child(productM.getProductID())
                        .child("views")
                        .setValue(dataSnapshot.getValue(ProductM.class).getViews() + 1);
                rate.setText(dataSnapshot.getValue(ProductM.class).getRating() + "/5");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child(MRTE).child(MYID).child(productM.getProductID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ratingBar.setRating(dataSnapshot.getValue(Float.class));
                } else {
                    ratingBar.setRating(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initListeners() {
        findViewById(R.id.container_product).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(PROD).child(productM.getCategory()).child(productM.getProductID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reference.child(PROD)
                                .child(productM.getCategory())
                                .child(productM.getProductID())
                                .child("popularity")
                                .setValue(fab.isSelected() ?
                                        dataSnapshot.getValue(ProductM.class).getPopularity() - 1 :
                                        dataSnapshot.getValue(ProductM.class).getPopularity() + 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if (fab.isSelected()) {
                    reference.child(FAVS).child(MYID).child(productM.getProductID()).removeValue();
                    fab.setSelected(false);
                } else {
                    reference.child(FAVS).child(MYID).child(productM.getProductID()).setValue(productM);
                    fab.setSelected(true);
                }
            }
        });
        thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgType = 0;
                thumb.setSelected(true);
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(ProductActivity.this);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgType = 1;
                image.setSelected(true);
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .start(ProductActivity.this);
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && editMode) {
                    imgType = 2;
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                            .setAspectRatio(4, 3)
                            .start(ProductActivity.this);
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgType = 3;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .start(ProductActivity.this);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.setSelected(!edit.isSelected());
                editMode(edit.isSelected());
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    uploadDB();
                    editMode(false);
                }
            }
        });
        coll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (upload) {
                    finish();
                } else {
                    new AlertDialog.Builder(ProductActivity.this)
                            .setTitle("Delete product")
                            .setMessage("Do you want to delete this product?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    reference.child(PROD).child(productM.getCategory()).child(productM.getProductID()).removeValue();
                                    reference.child(COLL).child(productM.getCollection()).child(productM.getProductID()).removeValue();
                                    reference.child(CART).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                                                reference.child(CART).child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                                            if (ds1.getValue(ProductM.class).getProductID().equals(productM.getProductID())) {
                                                                reference.child(CART).child(ds.getKey()).child(ds1.getKey()).removeValue();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    reference.child(FAVS).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                                                reference.child(FAVS).child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                                            if (ds1.getValue(ProductM.class).getProductID().equals(productM.getProductID())) {
                                                                reference.child(FAVS).child(ds.getKey()).child(ds1.getKey()).removeValue();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    finish();
                                }
                            })
                            .show();
                }
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(CART).child(MYID).child(productM.getProductID()).setValue(productM);
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float v, boolean b) {
                reference.child(MRTE).child(MYID).child(productM.getProductID()).setValue(v);
                reference.child(RATE).child(productM.getProductID()).child(MYID).setValue(v).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        reference.child(RATE).child(productM.getProductID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    float f = 0;
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        f += ds.getValue(Float.class);
                                    }
                                    f = f / (float) dataSnapshot.getChildrenCount();
                                    reference.child(PROD).child(productM.getCategory()).child(productM.getProductID()).child("rating").setValue(f);
                                    rate.setText(f + "/5");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    catg.setText("");
                } else {
                    catg.setText(stringList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    desg.setText("");
                } else {
                    desg.setText(stringList2.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean check() {
        if (name.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "enter product name", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            name.requestFocus();
            return false;
        }
        if (price.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "enter product price", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            price.requestFocus();
            return false;
        }
        if (desg.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "enter product designer", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            desg.requestFocus();
            return false;
        }
        if (catg.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "enter product category", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            catg.requestFocus();
            return false;
        }
        if (desc.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "enter product description", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            desc.requestFocus();
            return false;
        }
        if (count.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "enter product stock", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            count.requestFocus();
            return false;
        }
        if (productM.getImg() == null || productM.getImg().isEmpty()) {
            Toast.makeText(this, "set product thumbnail", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (productM.getImage() == null || productM.getImage().isEmpty()) {
            Toast.makeText(this, "set product image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadDB() {
        if (upload) {
            String k = reference.child(PROD).child(catg.getText().toString().trim()).push().getKey();
            productM.setProductID(k);
            if (productM.getCollection() != null) if (!productM.getCollection().isEmpty())
                reference.child(COLL).child(collectionStrings.get(selPos)).child(productM.getProductID()).setValue(productM);
        } else {
            reference.child(PROD).child(productM.getCategory()).child(productM.getProductID()).removeValue();
        }

        productM.setName(name.getText().toString().trim());
        productM.setPrice(Double.valueOf(price.getText().toString().trim()));
        productM.setDesign(desg.getText().toString().trim());
        productM.setCategory(catg.getText().toString().trim());
        productM.setDesc(desc.getText().toString().trim());
        productM.setAvailable(Integer.valueOf(count.getText().toString().trim()));
        productM.setFeatured(aSwitch.isChecked());

        reference.child(PROD).child(productM.getCategory()).child(productM.getProductID()).setValue(productM);
        if (productM.getCollection() != null) {
            if (!productM.getCollection().isEmpty()) {
                reference.child(COLL).child(productM.getCollection()).child(productM.getProductID()).setValue(productM);
            }
        }
        reference.child(CATG).child(productM.getCategory()).setValue(productM.getCategory());
        reference.child(DESG).child(productM.getDesign()).setValue(productM.getDesign());
        updateUI();
    }

    private void editMode(boolean b) {
        editMode = b;
        if (b) {
            done.show();
            del.show();
            coll.show();
            add.show();
            thumb.show();
            image.show();
        } else {
            done.hide();
            del.hide();
            coll.hide();
            add.hide();
            thumb.hide();
            image.hide();
        }
        catg.setVisibility(b ? View.VISIBLE : View.GONE);
        viewFabs.setVisibility(b ? View.VISIBLE : View.GONE);
        viewSwitch.setVisibility(b ? View.VISIBLE : View.GONE);
        spinner.setVisibility(b ? View.VISIBLE : View.GONE);
        spinner2.setVisibility(b ? View.VISIBLE : View.GONE);
        viewProduct.setVisibility(!b ? View.VISIBLE : View.GONE);

        name.setEnabled(b);
        price.setEnabled(b);
        desg.setEnabled(b);
        catg.setEnabled(b);
        desc.setEnabled(b);
        count.setEnabled(b);

        name.setHint(b ? "Product name" : null);
        price.setHint(b ? "Price" : null);
        desg.setHint(b ? "Designer" : null);
        catg.setHint(b ? "Category" : null);
        desc.setHint(b ? "Description" : null);
        count.setHint(b ? "In stock" : null);

        name.setBackground(b ? getResources().getDrawable(android.R.drawable.edit_text) : null);
        price.setBackground(b ? getResources().getDrawable(android.R.drawable.edit_text) : null);
        desg.setBackground(b ? getResources().getDrawable(android.R.drawable.edit_text) : null);
        catg.setBackground(b ? getResources().getDrawable(android.R.drawable.edit_text) : null);
        count.setBackground(b ? getResources().getDrawable(android.R.drawable.edit_text) : null);
        desc.setBackground(b ? getResources().getDrawable(android.R.drawable.edit_text) : null);
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

    @SuppressLint("SetTextI18n")
    void updateUI() {
        name.setText(productM.getName());
        price.setText(productM.getPrice() + "");
        desg.setText(productM.getDesign());
        catg.setText(productM.getCategory());
        desc.setText(productM.getDesc());
        count.setText(productM.getAvailable() + "");
        aSwitch.setChecked(productM.isFeatured());

        thumb.setSelected(!productM.getImg().isEmpty());
        image.setSelected(!productM.getImage().isEmpty());

        pictures.clear();
        if (productM.getImg() != null) if (!productM.getImg().isEmpty())
            pictures.add(productM.getImg());
        if (productM.getPics() != null) if (!productM.getPics().isEmpty())
            pictures.addAll(productM.getPics());
        if (productM.getImage() != null) if (!productM.getImage().isEmpty())
            pictures.add(productM.getImage());
        if (productM.getAds() != null) if (!productM.getAds().isEmpty())
            pictures.add(productM.getAds());
        mySliderAdapter.notifyDataSetChanged();
        sliderView.setSliderAdapter(mySliderAdapter);
        sliderView.setCurrentPagePosition(0);

        mySliderAdapter.notifyDataSetChanged();

        if (productM.getAvailable() > 0) {
            viewCart.setVisibility(View.VISIBLE);
        } else {
            viewCart.setVisibility(View.GONE);
        }

        scrollView.smoothScrollTo(0, 0);
        initFireBase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                progress.setVisibility(View.VISIBLE);
                uploadIMG(result.getUri());
            }
        }
    }

    private void uploadIMG(Uri uri) {
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
                progress.setVisibility(View.GONE);
                switch (imgType) {
                    case 0:
                        productM.setImg(task.getResult().toString());
                        break;
                    case 1:
                        productM.setImage(task.getResult().toString());
                        break;
                    case 2:
                        productM.setAds(task.getResult().toString());
                        break;
                    case 3:
                        pics.add(task.getResult().toString());
                        productM.setPics(pics);
                        break;
                }
                pictures.clear();
                if (productM.getImg() != null) if (!productM.getImg().isEmpty())
                    pictures.add(productM.getImg());
                if (productM.getPics() != null) if (!productM.getPics().isEmpty())
                    pictures.addAll(productM.getPics());
                if (productM.getImage() != null) if (!productM.getImage().isEmpty())
                    pictures.add(productM.getImage());
                if (productM.getAds() != null) if (!productM.getAds().isEmpty())
                    pictures.add(productM.getAds());
                mySliderAdapter.notifyDataSetChanged();
                sliderView.setSliderAdapter(mySliderAdapter);
                sliderView.setCurrentPagePosition(pictures.indexOf(task.getResult().toString()));
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_may_like, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, final int position) {
            final ProductM productm = productMList.get(position);
            String img = productm.getImg();

            Picasso.get().load(img).into(holder.img);

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    productM = productm;
                    updateUI();
                }
            });
        }

        @Override
        public int getItemCount() {
            return productMList.size();
        }
    }

    private class VH extends RecyclerView.ViewHolder {
        ImageView img;

        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_may_like);
        }
    }

    private class MyAdapterDialog extends RecyclerView.Adapter<VHD> {
        @NonNull
        @Override
        public VHD onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VHD(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_collection, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VHD holder, final int position) {
            holder.textView.setText(collectionStrings.get(position));

            holder.textView.setSelected(selPos == position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selPos != position) {
                        selPos = position;
                        productM.setCollection(collectionStrings.get(position));
                        if (!upload) {
                            if (productM.getCollection() != null)
                                if (!productM.getCollection().isEmpty())
                                    reference.child(COLL).child(productM.getCollection()).child(productM.getProductID()).removeValue();
                            reference.child(COLL).child(collectionStrings.get(position)).child(productM.getProductID()).setValue(productM);
                            reference.child(PROD)
                                    .child(productM.getCategory())
                                    .child(productM.getProductID())
                                    .child("collection")
                                    .setValue(collectionStrings.get(position));
                        }
                    } else {
                        selPos = -1;
                        productM.setCollection(null);
                        if (!upload) {
                            reference.child(COLL).child(collectionStrings.get(position)).child(productM.getProductID()).removeValue();
                            reference.child(PROD)
                                    .child(productM.getCategory())
                                    .child(productM.getProductID())
                                    .child("collection")
                                    .removeValue();
                        }
                    }
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return collectionStrings.size();
        }
    }

    private class VHD extends RecyclerView.ViewHolder {
        TextView textView;

        VHD(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.name_item_dialog_collection);
        }
    }

    private class MySliderAdapter extends SliderViewAdapter<VHS> {

        @Override
        public VHS onCreateViewHolder(ViewGroup parent) {
            return new VHS(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide, parent, false));
        }

        @Override
        public void onBindViewHolder(VHS holder, int position) {
            Picasso.get().load(pictures.get(position)).into(holder.imageView);
        }

        @Override
        public int getCount() {
            return pictures.size();
        }
    }

    private class VHS extends SliderViewAdapter.ViewHolder {
        ImageView imageView;

        VHS(View itemView) {
            super(itemView);

            itemView.setBackgroundColor(Color.WHITE);
            imageView = itemView.findViewById(R.id.img_slider);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }
}
