package tutorial.example.ecomm.fragments.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.h6ah4i.android.widget.advrecyclerview.headerfooter.AbstractHeaderFooterWrapperAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tutorial.example.ecomm.ProductActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.models.ProductM;

import static tutorial.example.ecomm.MainActivity.MYID;
import static tutorial.example.ecomm.StartActivity.PROD;
import static tutorial.example.ecomm.StartActivity.FAVS;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager gridLayoutManager, listLayoutManager;
    private ImageButton grid, list, order;
    private TextView count;
    private Spinner spinner;
    private List<ProductM> productMList;
    private WrapperAdapter adapter;
    private String name;
    private String[] queries;
    private String query = "name";
    private boolean asc = true;
    private SwipeRefreshLayout swipeRefreshLayout;

    CategoryFragment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initAdapters();
        initListeners();
        initFireBase();
    }

    private void initAdapters() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, queries);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    private void initViews() {
        productMList = new ArrayList<>();
        adapter = new WrapperAdapter(new MyAdapter());

        gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        listLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        recyclerView = getView().findViewById(R.id.recycler_category);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        count = getView().findViewById(R.id.count_category);
        spinner = getView().findViewById(R.id.spinner_category);

        grid = getView().findViewById(R.id.grid_category);
        grid.setSelected(true);

        list = getView().findViewById(R.id.list_category);
        list.setSelected(false);

        order = getView().findViewById(R.id.order_category);
        queries = new String[]{"name", "views", "price", "rating", "sales", "popularity"};

        swipeRefreshLayout = getView().findViewById(R.id.refresh_category);
    }

    private void initFireBase() {
        reference.child(PROD).child(name).orderByChild(query).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productMList.clear();
                if (asc) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        productMList.add(ds.getValue(ProductM.class));
                    }
                } else {
                    List<DataSnapshot> dataSnapshots = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        dataSnapshots.add(ds);
                    }
                    for (int i = dataSnapshots.size() - 1; i >= 0; i--) {
                        productMList.add(dataSnapshots.get(i).getValue(ProductM.class));
                    }
                }
                adapter.getWrappedAdapter().notifyDataSetChanged();
                count.setText(dataSnapshot.getChildrenCount() + " products");
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initListeners() {
        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setLayoutManager(gridLayoutManager);
                grid.setSelected(true);
                list.setSelected(false);
            }
        });
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setLayoutManager(listLayoutManager);
                grid.setSelected(false);
                list.setSelected(true);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                query = queries[i];
                initFireBase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asc = !asc;
                order.animate().rotationBy(180).setDuration(250).start();
                initFireBase();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initFireBase();
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    class MyAdapter extends RecyclerView.Adapter<VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_products, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position) {
            final ProductM productM = productMList.get(position);
            String img = productM.getImage();
            String name = productM.getName();
            String price = "$" + productM.getPrice();

            holder.name.setText(name);
            holder.price.setText(price);
            Picasso.get().load(img).into(holder.img);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.reach(productM);
                }
            });

            if (productM.getAvailable() > 0) {
                holder.outStock.setVisibility(View.GONE);
            } else {
                holder.outStock.setVisibility(View.VISIBLE);
            }

            holder.fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reference.child(PROD).child(productM.getCategory()).child(productM.getProductID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            reference.child(PROD)
                                    .child(productM.getCategory())
                                    .child(productM.getProductID())
                                    .child("popularity")
                                    .setValue(holder.fav.isSelected() ?
                                            dataSnapshot.getValue(ProductM.class).getPopularity() - 1 :
                                            dataSnapshot.getValue(ProductM.class).getPopularity() + 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    if (!holder.fav.isSelected()) {
                        holder.fav.setSelected(true);
                        reference.child(FAVS).child(MYID).child(productM.getProductID()).setValue(productM);
                    } else {
                        holder.fav.setSelected(false);
                        reference.child(FAVS).child(MYID).child(productM.getProductID()).removeValue();
                    }
                }
            });

            reference.child(FAVS).child(MYID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(productM.getProductID())) {
                        holder.fav.setSelected(true);
                    } else {
                        holder.fav.setSelected(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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
        LinearLayout outStock;

        VH(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img_products);
            name = itemView.findViewById(R.id.name_products);
            price = itemView.findViewById(R.id.price_products);
            fav = itemView.findViewById(R.id.fav_products);
            outStock = itemView.findViewById(R.id.outStock_products);
        }

        void reach(ProductM productM) {
            startActivity(new Intent(getContext(), ProductActivity.class)
                    .putExtra("productM", productM.getProductID())
                    .putExtra("category", productM.getCategory()));
        }
    }

    @SuppressWarnings("unchecked")
    class WrapperAdapter extends AbstractHeaderFooterWrapperAdapter<HVH, FVH> {

        WrapperAdapter(RecyclerView.Adapter adapter) {
            setAdapter(adapter);
        }

        @NonNull
        @Override
        public HVH onCreateHeaderItemViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @NonNull
        @Override
        public FVH onCreateFooterItemViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_shop, parent, false));
        }

        @Override
        public void onBindHeaderItemViewHolder(@NonNull HVH holder, int localPosition) {

        }

        @Override
        public void onBindFooterItemViewHolder(@NonNull FVH holder, int localPosition) {

        }

        @Override
        public int getHeaderItemCount() {
            return 0;
        }

        @Override
        public int getFooterItemCount() {
            return 1;
        }
    }

    private class HVH extends RecyclerView.ViewHolder {
        public HVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class FVH extends RecyclerView.ViewHolder {
        FVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
