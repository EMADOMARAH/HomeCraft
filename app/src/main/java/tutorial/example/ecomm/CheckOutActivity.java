package tutorial.example.ecomm;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import tutorial.example.ecomm.custom.CustomViewPager;
import tutorial.example.ecomm.fragments.CardFragment;
import tutorial.example.ecomm.fragments.PaymentFragment;
import tutorial.example.ecomm.fragments.ReceiptFragment;
import tutorial.example.ecomm.fragments.ShippingFragment;
import tutorial.example.ecomm.models.ProductM;

@SuppressWarnings({"unchecked", "ConstantConditions", "deprecation"})
public class CheckOutActivity extends AppCompatActivity {
    CustomViewPager viewPager;
    ShippingFragment shippingFragment;
    PaymentFragment paymentFragment;
    CardFragment cardFragment;
    ReceiptFragment receiptFragment;

    public ViewPager getViewPager() {
        return viewPager;
    }

    public PaymentFragment getPaymentFragment() {
        return paymentFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        initFrags();
        initViews();
    }

    private void initFrags() {
        shippingFragment = new ShippingFragment(1);
        paymentFragment = new PaymentFragment((List<ProductM>) getIntent().getSerializableExtra("productMList"));
        cardFragment = new CardFragment(1);
        receiptFragment = new ReceiptFragment();
    }

    @SuppressLint("InlinedApi")
    private void initViews() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_check_out));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        viewPager = findViewById(R.id.viewPager_check_out);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            Fragment[] fragments = new Fragment[]{
                    shippingFragment, paymentFragment, cardFragment, receiptFragment
            };

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                finish();
                break;
            case 1:
                viewPager.setCurrentItem(0);
                break;
            case 2:
                viewPager.setCurrentItem(1);
                break;
        }
    }
}
