package tutorial.example.ecomm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tutorial.example.ecomm.custom.CustomViewPager;
import tutorial.example.ecomm.fragments.start.SignInFragment;
import tutorial.example.ecomm.fragments.start.SignUpFragment;

@SuppressWarnings("deprecation")
public class StartActivity extends AppCompatActivity {

    public static String CATG = "Categories";
    public static String PROD = "Products";
    public static String USER = "Users";
    public static String VISA = "Visas";
    public static String FAVS = "Favourites";
    public static String CART = "MyBag";
    public static String SHIP = "Shipment";
    public static String COLL = "Collections";
    public static String COLM = "CollectionsModels";
    public static String DESG = "Designers";
    public static String ORDR = "Orders";
    public static String RATE = "Ratings";
    public static String MRTE = "MyRatings";

    public static FirebaseAuth auth;
    public static DatabaseReference reference;
    public static boolean admin = false;

    CustomViewPager viewPager;
    FragmentPagerAdapter fragmentPagerAdapter;
    SignInFragment signInFragment;
    SignUpFragment signUpFragment;
    Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initViews();
        initAdapters();

        reference = FirebaseDatabase.getInstance().getReference();
    }

    private void initAdapters() {
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
    }

    private void initViews() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        signInFragment = new SignInFragment();
        signUpFragment = new SignUpFragment();
        fragments = new Fragment[]{signInFragment, signUpFragment};
        viewPager = findViewById(R.id.viewPager_start);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }
}
