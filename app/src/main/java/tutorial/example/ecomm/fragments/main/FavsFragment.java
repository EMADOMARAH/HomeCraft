package tutorial.example.ecomm.fragments.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tutorial.example.ecomm.ProductActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.models.ProductM;

import static tutorial.example.ecomm.MainActivity.MYID;
import static tutorial.example.ecomm.StartActivity.CART;
import static tutorial.example.ecomm.StartActivity.PROD;
import static tutorial.example.ecomm.StartActivity.FAVS;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class FavsFragment extends Fragment {
    private List<ProductM> productMList;
    private RecyclerView.Adapter myAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favs, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initFireB();
    }

    private void initFireB() {
        reference.child(FAVS).child(MYID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productMList = new ArrayList<>();
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ProductM productM = ds.getValue(ProductM.class);
                    productM.setFakeId(++i);
                    productMList.add(productM);
                }
                myAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        productMList = new ArrayList<>();

        RecyclerViewTouchActionGuardManager touchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        touchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        touchActionGuardManager.setEnabled(true);

        GeneralItemAnimator animator = new SwipeDismissItemAnimator();
        animator.setSupportsChangeAnimations(false);

        RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();
        myAdapter = swipeManager.createWrappedAdapter(new MyAdapter());

        RecyclerView recyclerView = getView().findViewById(R.id.recycler_favs);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(myAdapter);
        recyclerView.setItemAnimator(animator);

        touchActionGuardManager.attachRecyclerView(recyclerView);
        swipeManager.attachRecyclerView(recyclerView);

        swipeRefreshLayout = getView().findViewById(R.id.refresh_favs);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initFireB();
            }
        });
    }

    private boolean hitTest(View v, int x, int y) {
        final int tx = (int) (v.getTranslationX() + -1 / 2f);
        final int ty = (int) (v.getTranslationY() + -1 / 2f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

    class MyAdapter extends RecyclerView.Adapter<VH> implements SwipeableItemAdapter<VH> {
        long pinnedItem = 0;

        MyAdapter() {
            setHasStableIds(true);
        }

        @Override
        public long getItemId(int position) {
            return productMList.get(position).getFakeId();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favs, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, final int position) {
            final ProductM productM = productMList.get(position);
            String name = productM.getName();
            String catg = productM.getCategory();
            String price = "$" + productM.getPrice();
            String img = productM.getImg();

            holder.name.setText(name);
            holder.catg.setText(catg);
            holder.price.setText(price);
            Picasso.get().load(img).into(holder.img);

            if (productM.getAvailable() > 0) {
                holder.cart.setEnabled(true);
            } else {
                holder.cart.setEnabled(false);
            }

            holder.cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!holder.cart.isSelected()) {
                        holder.cart.setSelected(true);
                        reference.child(CART).child(MYID).child(productM.getProductID()).setValue(productM);
                    } else {
                        holder.cart.setSelected(false);
                        reference.child(CART).child(MYID).child(productM.getProductID()).removeValue();
                    }
                }
            });

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pinnedItem != productM.getFakeId()) {
                        startActivity(new Intent(getContext(), ProductActivity.class)
                                .putExtra("productM", productM.getProductID())
                                .putExtra("category", productM.getCategory()));
                    } else {
                        pinnedItem = 0;
                        notifyItemChanged(position);
                    }
                }
            });

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pinnedItem = 0;
                    notifyItemChanged(position);
                }
            });

            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pinnedItem = 0;
                    productMList.remove(position);
                    notifyItemRemoved(position);
                    reference.child(FAVS).child(MYID).child(productM.getProductID()).removeValue();
                    reference.child(PROD).child(productM.getCategory()).child(productM.getProductID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            reference.child(PROD)
                                    .child(productM.getCategory())
                                    .child(productM.getProductID())
                                    .child("popularity")
                                    .setValue(dataSnapshot.getValue(ProductM.class).getPopularity() - 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            reference.child(CART).child(MYID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(productM.getProductID())) {
                        holder.cart.setSelected(true);
                    } else {
                        holder.cart.setSelected(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.setMaxLeftSwipeAmount(-1 / 2f);
            holder.setMaxRightSwipeAmount(0);
            holder.setSwipeItemHorizontalSlideAmount(pinnedItem == productM.getFakeId() ? -1 / 2f : 0);
        }

        @Override
        public int getItemCount() {
            return productMList.size();
        }

        @Override
        public int onGetSwipeReactionType(@NonNull VH holder, int position, int x, int y) {
            if (hitTest(holder.getSwipeableContainerView(), x, y)) {
                return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
            } else {
                return SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H;
            }
        }

        @Override
        public void onSwipeItemStarted(@NonNull VH holder, int position) {
            notifyDataSetChanged();
        }

        @Override
        public void onSetSwipeBackground(@NonNull VH holder, int position, int type) {
            if (type == SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND) {
                holder.under.setVisibility(View.GONE);
            } else {
                holder.under.setVisibility(View.VISIBLE);
            }
        }

        @Nullable
        @Override
        public SwipeResultAction onSwipeItem(@NonNull VH holder, final int position, int result) {
            if (result == SwipeableItemConstants.RESULT_SWIPED_LEFT) {
                return new SwipeResultActionMoveToSwipedDirection() {
                    @Override
                    protected void onPerformAction() {
                        super.onPerformAction();
                        if (pinnedItem != productMList.get(position).getFakeId()) {
                            pinnedItem = productMList.get(position).getFakeId();
                            notifyItemChanged(position);
                        }
                    }
                };
            }
            return new SwipeResultActionDefault() {
                @Override
                protected void onPerformAction() {
                    super.onPerformAction();
                    if (pinnedItem == productMList.get(position).getFakeId()) {
                        pinnedItem = 0;
                        notifyItemChanged(position);
                    }
                }
            };
        }
    }

    private class VH extends AbstractSwipeableItemViewHolder {
        TextView name, catg, price, del;
        ImageView img;
        ImageButton cart;
        CardView container, under;

        VH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_favs);
            catg = itemView.findViewById(R.id.catg_favs);
            price = itemView.findViewById(R.id.price_favs);
            del = itemView.findViewById(R.id.del_favs);
            img = itemView.findViewById(R.id.img_favs);
            cart = itemView.findViewById(R.id.cart_favs);
            container = itemView.findViewById(R.id.container_favs);
            under = itemView.findViewById(R.id.under_favs);
        }

        @NonNull
        @Override
        public View getSwipeableContainerView() {
            return container;
        }
    }
}
