package tutorial.example.ecomm.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import tutorial.example.ecomm.CheckOutActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.models.OrderM;
import tutorial.example.ecomm.models.ProductM;
import tutorial.example.ecomm.models.ShipmentM;
import tutorial.example.ecomm.models.VisaM;

import static tutorial.example.ecomm.MainActivity.MYID;
import static tutorial.example.ecomm.StartActivity.CART;
import static tutorial.example.ecomm.StartActivity.PROD;
import static tutorial.example.ecomm.StartActivity.ORDR;
import static tutorial.example.ecomm.StartActivity.VISA;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class PaymentFragment extends Fragment {
    private SliderView sliderView;
    private TextView add, total;
    private Button confirm;
    private List<VisaM> visaMList;
    private MySliderAdapter adapter;
    private List<ProductM> productMList;
    private ShipmentM shipmentM;

    private double d;

    public PaymentFragment(List<ProductM> productMList) {
        this.productMList = productMList;
    }

    void setShipmentM(ShipmentM shipmentM) {
        this.shipmentM = shipmentM;
    }

    MySliderAdapter getAdapter() {
        return adapter;
    }

    List<VisaM> getVisaMList() {
        return visaMList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initAdapters();
        initListeners();
        initFireB();

        d = 0;
        for (ProductM productM : productMList) {
            d += productM.getPrice() * productM.getQuantity();
        }
        d = Math.round(d);
        total.setText("$" + String.format("%.0f", d));
    }

    private void initFireB() {
        reference.child(VISA).child(MYID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                visaMList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    visaMList.add(ds.getValue(VisaM.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        sliderView = getView().findViewById(R.id.slider_payment);
        confirm = getView().findViewById(R.id.confirm_pay);
        add = getView().findViewById(R.id.add_pay);
        total = getView().findViewById(R.id.total_pay);
    }

    private void initAdapters() {
        visaMList = new ArrayList<>();
        adapter = new MySliderAdapter();
        sliderView.setSliderAdapter(adapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SWAP);
    }

    private void initListeners() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CheckOutActivity) getActivity()).getViewPager().setCurrentItem(2);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!visaMList.isEmpty()) {
                    reference.child(CART).child(MYID).removeValue();
                    for (int i = 0; i < productMList.size(); i++) {
                        final ProductM productM = productMList.get(i);
                        reference.child(PROD).child(productM.getCategory()).child(productM.getProductID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                reference.child(PROD)
                                        .child(productM.getCategory())
                                        .child(productM.getProductID())
                                        .child("sales")
                                        .setValue(dataSnapshot.getValue(ProductM.class).getSales() + productM.getQuantity());
                                reference.child(PROD)
                                        .child(productM.getCategory())
                                        .child(productM.getProductID())
                                        .child("available")
                                        .setValue(dataSnapshot.getValue(ProductM.class).getAvailable() - productM.getQuantity());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    String k = reference.child(ORDR).child(MYID).push().getKey();
                    reference.child(ORDR)
                            .child(MYID)
                            .child(k)
                            .setValue(new OrderM(k, visaMList.get(sliderView.getCurrentPagePosition()), shipmentM, productMList, System.currentTimeMillis(), d));
                    ((CheckOutActivity) getActivity()).getViewPager().setCurrentItem(3);
                } else {
                    Toast.makeText(getContext(), "please insert credit card", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class MySliderAdapter extends SliderViewAdapter<VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visa, parent, false));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            VisaM visaM = visaMList.get(position);

            StringBuilder code = new StringBuilder();
            for (int i = 0; i < visaM.getSecCode().length(); i++) {
                code.append(visaM.getSecCode().charAt(i));
                if (i == 3 || i == 7 || i == 11)
                    code.append(" ");
            }

            holder.name.setText(visaM.getAccName());
            holder.code.setText(code.toString());
            holder.date.setText(visaM.getExDate());
        }

        @Override
        public int getCount() {
            return visaMList.size();
        }
    }

    private class VH extends SliderViewAdapter.ViewHolder {
        TextView name, code, date;

        VH(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_visa);
            code = itemView.findViewById(R.id.code_visa);
            date = itemView.findViewById(R.id.date_visa);
        }
    }
}
