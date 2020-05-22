package tutorial.example.ecomm.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemState;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tutorial.example.ecomm.R;
import tutorial.example.ecomm.custom.CustomSlider;
import tutorial.example.ecomm.models.OrderM;
import tutorial.example.ecomm.models.ProductM;

import static tutorial.example.ecomm.MainActivity.MYID;
import static tutorial.example.ecomm.StartActivity.ORDR;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class OrdersFragment extends Fragment {
    private List<OrderM> orderMList;
    private SimpleDateFormat dateForm;
    private float elev;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFireB();
    }

    private void initFireB() {
        reference.child(ORDR).child(MYID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    orderMList = new ArrayList<>();
                    int i = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        OrderM orderM = ds.getValue(OrderM.class);
                        orderM.setFakeID(i++);
                        List<ProductM> productMList = new ArrayList<>();
                        for (int j = 0; j < orderM.getProductMList().size(); j++) {
                            ProductM productM = orderM.getProductMList().get(j);
                            productM.setFakeId(j);
                            productMList.add(productM);
                        }
                        orderM.setProductMList(productMList);
                        orderMList.add(orderM);
                    }
                    initViews();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void initViews() {
        elev = getResources().getDisplayMetrics().density * 5;
        dateForm = new SimpleDateFormat("MM/dd/yyyy");

        RecyclerViewExpandableItemManager expandableItemManager = new RecyclerViewExpandableItemManager(null);
        RecyclerView.Adapter myAdapter = expandableItemManager.createWrappedAdapter(new MyAdapter());

        GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);

        RecyclerView recyclerView = getView().findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(myAdapter);
        recyclerView.setItemAnimator(animator);

        expandableItemManager.attachRecyclerView(recyclerView);
    }

    class MyAdapter extends AbstractExpandableItemAdapter<GroupVH, ChildVH> {
        MyAdapter() {
            setHasStableIds(true);
        }

        @Override
        public int getGroupCount() {
            return orderMList.size();
        }

        @Override
        public int getChildCount(int groupPosition) {
            return orderMList.get(groupPosition).getProductMList().size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return orderMList.get(groupPosition).getFakeID();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return orderMList.get(groupPosition).getProductMList().get(childPosition).getFakeId();
        }

        @NonNull
        @Override
        public GroupVH onCreateGroupViewHolder(ViewGroup parent, int viewType) {
            return new GroupVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_group, parent, false));
        }

        @NonNull
        @Override
        public ChildVH onCreateChildViewHolder(ViewGroup parent, int viewType) {
            return new ChildVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_child, parent, false));
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void onBindGroupViewHolder(@NonNull final GroupVH holder, final int groupPosition, int viewType) {
            final OrderM orderM = orderMList.get(groupPosition);
            ExpandableItemState expandState = holder.getExpandState();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(orderM.getDate());
            String date = dateForm.format(calendar.getTime());

            holder.itemView.setClickable(true);

            holder.name.setText(orderM.getShipmentM().getName());
            holder.date.setText(date);
            holder.total.setText("$" + String.format("%.0f", orderM.getTotal()));

            if (expandState.isUpdated()) {
                if (expandState.isExpanded()) {
                    holder.cardView.setCardElevation(elev);
                    holder.indicator.animate().rotation(180).setDuration(250).start();
                } else {
                    holder.cardView.setCardElevation(0);
                    holder.indicator.animate().rotation(0).setDuration(250).start();
                }
            }

            holder.sliderView.setSliderAdapter(new MySlider(orderM.getProductMList()));
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void onBindChildViewHolder(@NonNull ChildVH holder, int groupPosition, int childPosition, int viewType) {
            ProductM productM = orderMList.get(groupPosition).getProductMList().get(childPosition);

            holder.name.setText(productM.getName());
            holder.price.setText("$" + String.format("%.0f", productM.getPrice()));
            holder.count.setText(productM.getQuantity() + "");

            Picasso.get().load(productM.getImg()).into(holder.img);
        }

        @Override
        public boolean onCheckCanExpandOrCollapseGroup(@NonNull GroupVH holder, int groupPosition, int x, int y, boolean expand) {
            return true;
        }
    }

    private class GroupVH extends AbstractExpandableItemViewHolder {
        CardView cardView;
        TextView name, date, total;
        ImageView indicator;
        CustomSlider sliderView;

        GroupVH(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_order);
            name = itemView.findViewById(R.id.name_order);
            date = itemView.findViewById(R.id.date_order);
            total = itemView.findViewById(R.id.total_order);
            indicator = itemView.findViewById(R.id.indicator_order);
            sliderView = itemView.findViewById(R.id.slider_order);
            sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
            sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
        }
    }

    private class ChildVH extends AbstractExpandableItemViewHolder {
        TextView name, price, count;
        ImageView img;

        ChildVH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_order);
            price = itemView.findViewById(R.id.price_order);
            count = itemView.findViewById(R.id.count_order);
            img = itemView.findViewById(R.id.img_order);
        }
    }

    private class MySlider extends SliderViewAdapter<SliderVH> {
        List<ProductM> productMList;

        MySlider(List<ProductM> productMList) {
            this.productMList = productMList;
        }

        @Override
        public SliderVH onCreateViewHolder(ViewGroup parent) {
            return new SliderVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide, parent, false));
        }

        @Override
        public void onBindViewHolder(SliderVH viewHolder, int position) {
            ProductM productM = productMList.get(position);
            Picasso.get()
                    .load(productM.getImage())
                    .into(viewHolder.imageView);
        }

        @Override
        public int getCount() {
            return productMList.size();
        }
    }

    private class SliderVH extends SliderViewAdapter.ViewHolder {
        ImageView imageView;

        SliderVH(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_slider);
        }
    }
}
