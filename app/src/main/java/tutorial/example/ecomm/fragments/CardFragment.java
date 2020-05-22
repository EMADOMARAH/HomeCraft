package tutorial.example.ecomm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import tutorial.example.ecomm.CheckOutActivity;
import tutorial.example.ecomm.R;
import tutorial.example.ecomm.models.VisaM;

import static tutorial.example.ecomm.MainActivity.MYID;
import static tutorial.example.ecomm.StartActivity.VISA;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings("ConstantConditions")
public class CardFragment extends Fragment {
    private TextInputEditText name, bank, number, expire, code;
    private Spinner spinner;
    private Button add;
    private InputMethodManager imm;
    private String[] strings;

    private int red, grn;
    private int mode;

    public CardFragment(int mode) {
        this.mode = mode;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initAdapters();
        initListeners();
    }

    private void initViews() {
        red = getResources().getColor(android.R.color.holo_red_dark);
        grn = getResources().getColor(android.R.color.holo_green_dark);

        name = getView().findViewById(R.id.name_card);
        bank = getView().findViewById(R.id.bank_card);
        number = getView().findViewById(R.id.num_card);
        expire = getView().findViewById(R.id.exp_card);
        code = getView().findViewById(R.id.code_card);
        spinner = getView().findViewById(R.id.spinner_card);
        add = getView().findViewById(R.id.add_card);

        bank.setEnabled(false);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
        spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    bank.setText("");
                } else {
                    bank.setText(strings[i]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = name.getText().toString().trim();
                String b = bank.getText().toString().trim();
                String n = number.getText().toString().trim();
                String e = expire.getText().toString().trim();
                String c = code.getText().toString().trim();

                if (check(a, b, n, e, c)) {
                    String v = reference.child(VISA).child(MYID).push().getKey();
                    VisaM visaM = new VisaM(v, a, b, n, e, c);
                    reference.child(VISA).child(MYID).child(v).setValue(visaM);
                    if (mode == 1) {
                        ((CheckOutActivity) getActivity()).getPaymentFragment().getVisaMList().add(visaM);
                        ((CheckOutActivity) getActivity()).getPaymentFragment().getAdapter().notifyDataSetChanged();
                    }
                    getActivity().onBackPressed();
                }
            }
        });
        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 16) {
                    code.setTextColor(red);
                } else {
                    code.setTextColor(grn);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initAdapters() {
        strings = new String[]{
                "-/-",
                "HSBC",
                "Alex Bank",
                "Bank Audi",
                "Banque Misr",
                "Crédit Agricole",
                "Banque du Caire",
                "National Bank of Egypt",
                "Arab Banking Corporation",
                "Qatar National Bank (QNB)",
                "Commercial International Bank (CIB)",
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, strings);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }


    private boolean check(String a, String b, String n, String e, String c) {
        if (a.isEmpty()) {
            Toast.makeText(getContext(), "please enter account name", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            name.requestFocus();
            return false;
        }
        if (b.isEmpty()) {
            Toast.makeText(getContext(), "please select your bank", Toast.LENGTH_SHORT).show();
            spinner.requestFocus();
            return false;
        }
        if (n.isEmpty()) {
            Toast.makeText(getContext(), "please enter account number", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            number.requestFocus();
            return false;
        }
        if (e.isEmpty()) {
            Toast.makeText(getContext(), "please enter expire date", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            expire.requestFocus();
            return false;
        }
        if (c.isEmpty()) {
            Toast.makeText(getContext(), "please enter security code", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            code.requestFocus();
            return false;
        }
        if (c.length() != 16) {
            Toast.makeText(getContext(), "security code must be 16 digits", Toast.LENGTH_SHORT).show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            code.requestFocus();
            return false;
        }
        return true;
    }

}
