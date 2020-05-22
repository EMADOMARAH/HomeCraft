package tutorial.example.ecomm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tutorial.example.ecomm.models.CollectionM;
import tutorial.example.ecomm.models.ProductM;

import static tutorial.example.ecomm.StartActivity.COLL;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class CollectionActivity extends AppCompatActivity {
    TextView name, desc;
    ImageView pic;
    RecyclerView recyclerView;
    CollectionM collectionM;
    List<ProductM> productMList;
    MyAdapter myAdapter;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        collectionM = (CollectionM) intent.getSerializableExtra("collectionM");
        initFireB();
        populate();
    }

    private void populate() {
        name.setText(collectionM.getName());
        desc.setText(collectionM.getDesc());
        if (!collectionM.getImg().isEmpty()) {
            Picasso.get().load(collectionM.getPic()).into(pic);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        initViews();
        initFireB();

        populate();
    }

    private void initFireB() {
        reference.child(COLL).child(collectionM.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productMList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    productMList.add(ds.getValue(ProductM.class));
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        getWindow().setStatusBarColor(Color.WHITE);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_collection));
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collectionM = (CollectionM) getIntent().getSerializableExtra("collectionM");

        name = findViewById(R.id.name_collection);
        desc = findViewById(R.id.desc_collection);
        pic = findViewById(R.id.pic_collection);

        productMList = new ArrayList<>();
        myAdapter = new MyAdapter();

        recyclerView = findViewById(R.id.recycler_collection);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myAdapter);
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

    private class MyAdapter extends RecyclerView.Adapter<VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_products, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position) {
            final ProductM productM = productMList.get(position);

            holder.name.setText(productM.getName());
            holder.price.setText("$" + productM.getPrice());
            Picasso.get().load(productM.getImg()).into(holder.img);

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.reach(productM);
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
        TextView name, price;
        ImageButton fav;

        VH(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img_products);
            name = itemView.findViewById(R.id.name_products);
            price = itemView.findViewById(R.id.price_products);
            fav = itemView.findViewById(R.id.fav_products);

            fav.setVisibility(View.GONE);
        }

        void reach(ProductM productM) {
            startActivity(new Intent(CollectionActivity.this, ProductActivity.class)
                    .putExtra("productM", productM.getProductID())
                    .putExtra("category", productM.getCategory()));
        }
    }

}
