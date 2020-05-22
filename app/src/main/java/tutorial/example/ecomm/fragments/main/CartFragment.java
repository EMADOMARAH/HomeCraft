package tutorial.example.ecomm.fragments.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.headerfooter.AbstractHeaderFooterWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tutorial.example.ecomm.CheckOutActivity;
import tutorial.example.ecomm.ProductActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.models.ProductM;

import static tutorial.example.ecomm.MainActivity.MYID;
import static tutorial.example.ecomm.StartActivity.CART;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class CartFragment extends Fragment {
    private Button confirm;
    private TextView cont;
    private RecyclerView recyclerView;
    private WrapperAdapter myAdapter;
    private List<ProductM> productMList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initListeners();
        initAdapter();
        initFireB();
    }

    private void initFireB() {
        reference.child(CART).child(MYID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productMList.clear();
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ProductM productM = ds.getValue(ProductM.class);
                    productM.setFakeId(++i);
                    productMList.add(productM);
                }
                myAdapter.getWrappedAdapter().notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        confirm = getView().findViewById(R.id.confirm_cart);
        cont = getView().findViewById(R.id.continue_cart);
        recyclerView = getView().findViewById(R.id.recycler_cart);
        swipeRefreshLayout = getView().findViewById(R.id.refresh_cart);
    }

    private void initListeners() {
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CheckOutActivity.class).putExtra("productMList", (Serializable) productMList));
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initFireB();
            }
        });
    }

    private void initAdapter() {
        productMList = new ArrayList<>();

        RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();
        RecyclerViewTouchActionGuardManager touchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        touchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        touchActionGuardManager.setEnabled(true);
        GeneralItemAnimator animator = new SwipeDismissItemAnimator();
        animator.setSupportsChangeAnimations(false);

        myAdapter = new WrapperAdapter(swipeManager.createWrappedAdapter(new MyAdapter()));
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(animator);

        touchActionGuardManager.attachRecyclerView(recyclerView);
        swipeManager.attachRecyclerView(recyclerView);
    }

    private boolean hitTest(View v, int x, int y) {
        final int tx = (int) (v.getTranslationX() + -1 / 3f);
        final int ty = (int) (v.getTranslationY() + -1 / 3f);
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
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final VH holder, final int position) {
            final ProductM productM = productMList.get(position);
            String name = productM.getName();
            String price = "$" + productM.getPrice();
            String img = productM.getImg();
            int count = productM.getQuantity();

            holder.name.setText(name);
            holder.price.setText(price);
            holder.count.setText(count + "");
            Picasso.get().load(img).into(holder.img);

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pinnedItem == productM.getFakeId()) {
                        pinnedItem = 0;
                        notifyItemChanged(position);
                    } else {
                        startActivity(new Intent(getContext(), ProductActivity.class)
                                .putExtra("productM", productM.getProductID())
                                .putExtra("category", productM.getCategory()));
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
                    myAdapter.notifyDataSetChanged();
                    reference.child(CART).child(MYID).child(productM.getProductID()).removeValue();
                }
            });

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = Integer.valueOf(holder.count.getText().toString());
                    if (i < 10) {
                        i++;
                        holder.count.setText(i + "");
                        productM.setQuantity(i);
                        myAdapter.notifyDataSetChanged();
                        reference.child(CART).child(MYID).child(productM.getProductID()).child("quantity").setValue(i);
                    } else {
                        Toast.makeText(getContext(), "Maximum ten items", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.min.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = Integer.valueOf(holder.count.getText().toString());
                    if (i > 1) {
                        i--;
                        holder.count.setText(i + "");
                        productM.setQuantity(i);
                        myAdapter.notifyDataSetChanged();
                        reference.child(CART).child(MYID).child(productM.getProductID()).child("quantity").setValue(i);
                    } else {
                        Toast.makeText(getContext(), "Minimum one item", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.setMaxLeftSwipeAmount(-1 / 3f);
            holder.setMaxRightSwipeAmount(0);
            holder.setSwipeItemHorizontalSlideAmount(pinnedItem == productM.getFakeId() ? -1 / 3f : 0);
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
        ImageView img;
        TextView name, price, catg, count, del, add, min;
        LinearLayout container, under;

        VH(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img_cart);
            name = itemView.findViewById(R.id.name_cart);
            price = itemView.findViewById(R.id.price_cart);
            catg = itemView.findViewById(R.id.catg_cart);
            count = itemView.findViewById(R.id.count_cart);
            add = itemView.findViewById(R.id.add_cart);
            min = itemView.findViewById(R.id.min_cart);
            del = itemView.findViewById(R.id.del_cart);
            container = itemView.findViewById(R.id.container_cart);
            under = itemView.findViewById(R.id.under_cart);
        }

        @NonNull
        @Override
        public View getSwipeableContainerView() {
            return container;
        }
    }

    @SuppressWarnings("unchecked")
    private class WrapperAdapter extends AbstractHeaderFooterWrapperAdapter<HeaderVH, FooterVH> {

        WrapperAdapter(RecyclerView.Adapter adapter) {
            setAdapter(adapter);
        }

        @NonNull
        @Override
        public HeaderVH onCreateHeaderItemViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @NonNull
        @Override
        public FooterVH onCreateFooterItemViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FooterVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_cart, parent, false));
        }

        @Override
        public void onBindHeaderItemViewHolder(@NonNull HeaderVH holder, int localPosition) {

        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void onBindFooterItemViewHolder(@NonNull FooterVH holder, int localPosition) {
            double d = 0;
            for (ProductM productM : productMList) {
                d += productM.getPrice() * productM.getQuantity();
            }
            d = Math.round(d);
            holder.total.setText("$" + String.format("%.0f", d));
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

    private class HeaderVH extends RecyclerView.ViewHolder {
        public HeaderVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class FooterVH extends RecyclerView.ViewHolder {
        TextView total;

        FooterVH(@NonNull View itemView) {
            super(itemView);

            total = itemView.findViewById(R.id.total_footer_cart);
        }
    }

}
