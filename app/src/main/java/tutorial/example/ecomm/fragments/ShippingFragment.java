package tutorial.example.ecomm.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import tutorial.example.ecomm.CheckOutActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.models.ShipmentM;

import static tutorial.example.ecomm.MainActivity.MYID;
import static tutorial.example.ecomm.StartActivity.SHIP;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
public class ShippingFragment extends Fragment {
    private TextInputEditText name, address, city, code, country, method;
    private Spinner cityS, countryS, methodS;
    private Button confirm;
    private InputMethodManager imm;
    private ShipmentM shipmentM;

    private int mode;

    public ShippingFragment(int mode) {
        this.mode = mode;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ship, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initAdapter();
        initFireB();
        initListeners();
    }

    private void initFireB() {
        reference.child(SHIP).child(MYID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    shipmentM = dataSnapshot.getValue(ShipmentM.class);
                    updateUI();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI() {
        name.setText(shipmentM.getName());
        address.setText(shipmentM.getAddress());
        city.setText(shipmentM.getCity());
        code.setText(shipmentM.getZip());
        country.setText(shipmentM.getCountry());
        method.setText(shipmentM.getMethod());
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {
        name = getView().findViewById(R.id.name_ship);
        address = getView().findViewById(R.id.address_ship);
        city = getView().findViewById(R.id.city_ship);
        code = getView().findViewById(R.id.code_ship);
        country = getView().findViewById(R.id.country_ship);
        method = getView().findViewById(R.id.method_ship);
        confirm = getView().findViewById(R.id.continue_ship);

        cityS = getView().findViewById(R.id.spinner_city_ship);
        countryS = getView().findViewById(R.id.spinner_country_ship);
        methodS = getView().findViewById(R.id.spinner_method_card);

        city.setEnabled(false);
        country.setEnabled(false);
        method.setEnabled(false);

        if (mode == 0) {
            confirm.setText("Set up shipping");
        } else {
            confirm.setText("Continue to payment");
        }

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initAdapter() {
        try {
            InputStream inputStream = getContext().getAssets().open("countries.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            JSONArray json = new JSONArray(new String(buffer, StandardCharsets.UTF_8));
            String[] countries = new String[json.length()];
            String[] cities = new String[json.length()];
            for (int i = 0; i < json.length(); i++) {
                try {
                    countries[i] = (String) ((JSONObject) json.get(i)).get("name");
                    cities[i] = (String) ((JSONObject) json.get(i)).get("capital");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.cities, R.layout.item_spinner);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cityS.setAdapter(adapter1);

            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.countries, R.layout.item_spinner);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            countryS.setAdapter(adapter2);

            ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(), R.array.methods, R.layout.item_spinner);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            methodS.setAdapter(adapter3);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        cityS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        countryS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        methodS.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        cityS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    city.setText("");
                    country.setText("");
                } else {
                    city.setText((CharSequence) cityS.getAdapter().getItem(i));
                    countryS.setSelection(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        countryS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    city.setText("");
                    country.setText("");
                } else {
                    country.setText((CharSequence) countryS.getAdapter().getItem(i));
                    cityS.setSelection(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        methodS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    method.setText("");
                } else {
                    method.setText((CharSequence) methodS.getAdapter().getItem(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString().trim();
                String a = address.getText().toString().trim();
                String t = city.getText().toString().trim();
                String z = code.getText().toString().trim();
                String c = country.getText().toString().trim();
                String m = method.getText().toString().trim();
                if (check(n, a, t, z, c, m)) {
                    reference.child(SHIP).child(MYID).setValue(new ShipmentM(n, a, t, z, c, m));
                    if (mode == 0) {
                        getActivity().finish();
                    } else {
                        CheckOutActivity activity = (CheckOutActivity) getActivity();
                        PaymentFragment paymentFragment = activity.getPaymentFragment();
                        paymentFragment.setShipmentM(new ShipmentM(n, a, t, z, c, m));
                        activity.getViewPager().setCurrentItem(1);
                    }
                }
            }
        });
    }

    private boolean check(String n, String a, String t, String z, String c, String m) {
        if (n.isEmpty()) {
            Toast.makeText(getContext(), "please enter your name", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            name.requestFocus();
            return false;
        }
        if (a.isEmpty()) {
            Toast.makeText(getContext(), "please enter your address", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            address.requestFocus();
            return false;
        }
        if (t.isEmpty()) {
            Toast.makeText(getContext(), "please select your city", Toast.LENGTH_SHORT).show();
            cityS.requestFocus();
            return false;
        }
        if (z.isEmpty()) {
            Toast.makeText(getContext(), "please enter your zip code", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            code.requestFocus();
            return false;
        }
        if (c.isEmpty()) {
            Toast.makeText(getContext(), "please select your country", Toast.LENGTH_SHORT).show();
            countryS.requestFocus();
            return false;
        }
        if (m.isEmpty()) {
            Toast.makeText(getContext(), "please select shipping method", Toast.LENGTH_SHORT).show();
            methodS.requestFocus();
            return false;
        }

        return true;
    }
}
