package tutorial.example.ecomm.fragments.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tutorial.example.ecomm.CollectionActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.models.CollectionM;

import static tutorial.example.ecomm.StartActivity.COLL;
import static tutorial.example.ecomm.StartActivity.COLM;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class CollectionsFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<CollectionM> collectionMList;
    private MyAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collections, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initFireB();
    }

    private void initViews() {
        collectionMList = new ArrayList<>();
        myAdapter = new MyAdapter();
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_collection);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(myAdapter);

        swipeRefreshLayout = getView().findViewById(R.id.refresh_collection);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initFireB();
            }
        });
    }

    private void initFireB() {
        reference.child(COLM).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                collectionMList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    collectionMList.add(ds.getValue(CollectionM.class));
                }
                myAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position) {
            final CollectionM collectionM = collectionMList.get(position);

            holder.name.setText(collectionM.getName());
            Picasso.get().load(collectionM.getImg()).into(holder.img);

            reference.child(COLL).child(collectionM.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    holder.count.setText(dataSnapshot.getChildrenCount() + " items");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), CollectionActivity.class)
                            .putExtra("collectionM", collectionM));
                }
            });
        }

        @Override
        public int getItemCount() {
            return collectionMList.size();
        }
    }

    private class VH extends RecyclerView.ViewHolder {
        TextView name, count;
        ImageView img;

        VH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_collection);
            count = itemView.findViewById(R.id.count_collection);
            img = itemView.findViewById(R.id.img_collection);
        }
    }
}
