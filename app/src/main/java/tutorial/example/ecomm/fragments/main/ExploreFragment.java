package tutorial.example.ecomm.fragments.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tutorial.example.ecomm.ProductActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.models.ProductM;

import static tutorial.example.ecomm.StartActivity.PROD;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings({"ConstantConditions"})
public class ExploreFragment extends Fragment {
    private List<ProductM> productsSlider, productsSold, productsViewed, productsLiked;
    private MySliderAdapter mySliderAdapter;
    private MyAdapterRecycler soldAdapter, viewedAdapter, likedAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initFireB();
    }

    private void initFireB() {
        reference.child(PROD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            productsSlider.clear();

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds.getValue(ProductM.class).isFeatured()) {
                                    productsSlider.add(ds.getValue(ProductM.class));
                                }

                            }
                            mySliderAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    ds.getRef().orderByChild("sales").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            productsSold.clear();
                            List<DataSnapshot> dataSnapshots = new ArrayList<>();

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                dataSnapshots.add(ds);
                            }
                            for (int i = dataSnapshots.size() - 1; i >= 0; i--) {
                                productsSold.add(dataSnapshots.get(i).getValue(ProductM.class));
                            }
                            soldAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    ds.getRef().orderByChild("views").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            productsViewed.clear();
                            List<DataSnapshot> dataSnapshots = new ArrayList<>();

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                dataSnapshots.add(ds);
                            }
                            for (int i = dataSnapshots.size() - 1; i >= 0; i--) {
                                productsViewed.add(dataSnapshots.get(i).getValue(ProductM.class));
                            }
                            viewedAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    ds.getRef().orderByChild("rating").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            productsLiked.clear();
                            List<DataSnapshot> dataSnapshots = new ArrayList<>();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                dataSnapshots.add(ds);
                            }
                            for (int i = dataSnapshots.size() - 1; i >= 0; i--) {
                                productsLiked.add(dataSnapshots.get(i).getValue(ProductM.class));
                            }
                            likedAdapter.notifyDataSetChanged();
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
    }

    private void initViews() {
        productsSlider = new ArrayList<>();
        mySliderAdapter = new MySliderAdapter();
        SliderView sliderView = getView().findViewById(R.id.slider_explore);
        sliderView.setSliderAdapter(mySliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SCALE);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

        productsSold = new ArrayList<>();
        soldAdapter = new MyAdapterRecycler();
        soldAdapter.setProductMList(productsSold);
        RecyclerView recyclerView1 = getView().findViewById(R.id.recycler_sold_explore);
        recyclerView1.setLayoutManager(new LinearLayoutManager(recyclerView1.getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView1.setAdapter(soldAdapter);

        productsViewed = new ArrayList<>();
        viewedAdapter = new MyAdapterRecycler();
        viewedAdapter.setProductMList(productsViewed);
        RecyclerView recyclerView2 = getView().findViewById(R.id.recycler_viewed_explore);
        recyclerView2.setLayoutManager(new LinearLayoutManager(recyclerView2.getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView2.setAdapter(viewedAdapter);

        productsLiked = new ArrayList<>();
        likedAdapter = new MyAdapterRecycler();
        likedAdapter.setProductMList(productsLiked);
        RecyclerView recyclerView3 = getView().findViewById(R.id.recycler_liked_explore);
        recyclerView3.setLayoutManager(new LinearLayoutManager(recyclerView3.getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView3.setAdapter(likedAdapter);

        swipeRefreshLayout = getView().findViewById(R.id.refresh_explore);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
                initFireB();
            }
        });
    }

    public class MyAdapterRecycler extends RecyclerView.Adapter<VH> {
        List<ProductM> productMList = new ArrayList<>();

        void setProductMList(List<ProductM> productMList) {
            this.productMList = productMList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_may_like, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            final ProductM productM = productMList.get(position);
            String img = productM.getImg();

            Picasso.get().load(img).into(holder.img);

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), ProductActivity.class)
                            .putExtra("productM", productM.getProductID())
                            .putExtra("category", productM.getCategory()));
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

    public class MySliderAdapter extends SliderViewAdapter<sVH> {
        @Override
        public sVH onCreateViewHolder(ViewGroup parent) {
            return new sVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide, parent, false));
        }

        @Override
        public void onBindViewHolder(sVH viewHolder, int position) {
            final ProductM productM = productsSlider.get(position);

            Picasso.get().load(productM.getAds()).into(viewHolder.imageView);

            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), ProductActivity.class)
                            .putExtra("productM", productM.getProductID())
                            .putExtra("category", productM.getCategory()));
                }
            });
        }

        @Override
        public int getCount() {
            return productsSlider.size();
        }
    }

    private class sVH extends SliderViewAdapter.ViewHolder {
        ImageView imageView;

        sVH(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_slider);
        }
    }
}
