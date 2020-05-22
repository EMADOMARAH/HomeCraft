package tutorial.example.ecomm;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import tutorial.example.ecomm.custom.CustomViewPager;
import tutorial.example.ecomm.fragments.main.CartFragment;
import tutorial.example.ecomm.fragments.main.CollectionsFragment;
import tutorial.example.ecomm.fragments.main.ExploreFragment;
import tutorial.example.ecomm.fragments.main.FavsFragment;
import tutorial.example.ecomm.fragments.main.ProfileFragment;
import tutorial.example.ecomm.fragments.main.ShopFragment;
import tutorial.example.ecomm.models.UserM;

import static tutorial.example.ecomm.StartActivity.CATG;
import static tutorial.example.ecomm.StartActivity.USER;
import static tutorial.example.ecomm.StartActivity.admin;
import static tutorial.example.ecomm.StartActivity.auth;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings({"ConstantConditions", "deprecation"})
public class MainActivity extends AppCompatActivity {
    public static final int EXPLORE = 0, SHOP = 1, COLLECTIONS = 2, WISHLIST = 3, MYBAG = 4, PROFILE = 5;
    public static String MYID;

    FloatingActionButton fab;

    Toolbar toolbar;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    TextView name, email;
    CircleImageView img;

    CustomViewPager viewPager;

    Dialog dialog;
    TextInputEditText category;
    Button yes, no;
    InputMethodManager imm;

    ExploreFragment exploreFragment;
    ShopFragment shopFragment;
    CollectionsFragment collectionsFragment;
    FavsFragment favsFragment;
    CartFragment cartFragment;
    ProfileFragment profileFragment;
    Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initAdapters();
        initFireBase();
        initListeners();
    }

    private void initFireBase() {
        reference.child(USER).child(MYID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserM userM = dataSnapshot.getValue(UserM.class);
                profileFragment.setUserM(userM);
                email.setText(userM.getEmail());
                setNavName(userM.getUserName());
                setNavPic(userM.getProfPic());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setNavName(String n) {
        name.setText(n);
    }

    public void setNavPic(String p) {
        if (!p.isEmpty()) {
            Picasso.get().load(p).error(R.drawable.ic_person).into(img);
        }
    }

    private void initViews() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        MYID = auth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        viewPager = findViewById(R.id.viewPager_main);

        drawerLayout = findViewById(R.id.drawer_main);
        navigationView = findViewById(R.id.nav_main);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, ((Toolbar) findViewById(R.id.toggle_main)), R.string.open, R.string.close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_category);
        category = dialog.findViewById(R.id.catg_dialog_category);
        yes = dialog.findViewById(R.id.yes_dialog_category);
        no = dialog.findViewById(R.id.no_dialog_category);

        imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);

        fab = findViewById(R.id.fab_main);

        navigationView.getMenu().getItem(0).setChecked(true);
        if (admin) {
            navigationView.getMenu().findItem(R.id.upload_menu_nav).setVisible(true);
            fab.show();
        }

        name = navigationView.getHeaderView(0).findViewById(R.id.name_nav);
        email = navigationView.getHeaderView(0).findViewById(R.id.email_nav);
        img = navigationView.getHeaderView(0).findViewById(R.id.img_nav);
    }

    private void initAdapters() {
        exploreFragment = new ExploreFragment();
        shopFragment = new ShopFragment();
        collectionsFragment = new CollectionsFragment();
        favsFragment = new FavsFragment();
        cartFragment = new CartFragment();
        profileFragment = new ProfileFragment();

        fragments = new Fragment[]{
                exploreFragment,
                shopFragment,
                collectionsFragment,
                favsFragment,
                cartFragment,
                profileFragment
        };

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
    }

    private void initListeners() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.explore_menu_nav:
                        viewPager.setCurrentItem(EXPLORE, false);
                        break;
                    case R.id.shop_menu_nav:
                        viewPager.setCurrentItem(SHOP, false);
                        break;
                    case R.id.coll_menu_nav:
                        viewPager.setCurrentItem(COLLECTIONS, false);
                        break;
                    case R.id.favs_menu_nav:
                        viewPager.setCurrentItem(WISHLIST, false);
                        break;
                    case R.id.cart_menu_nav:
                        viewPager.setCurrentItem(MYBAG, false);
                        break;
                    case R.id.profile_menu_nav:
                        viewPager.setCurrentItem(PROFILE, false);
                        break;
                    case R.id.upload_menu_nav:
                        startActivity(new Intent(MainActivity.this, ProductActivity.class));
                        break;
                    case R.id.logout_menu_nav:
                        auth.signOut();
                        finish();
                        startActivity(new Intent(MainActivity.this, StartActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                for (int i = 0; i <= fragments.length; i++) {
                    if (i == position) {
                        navigationView.getMenu().getItem(position).setChecked(true);
                    } else {
                        navigationView.getMenu().getItem(i).setChecked(false);
                    }
                }
                if (admin) {
                    if (position == EXPLORE || position == SHOP || position == COLLECTIONS) {
                        fab.show();
                    } else {
                        fab.hide();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (viewPager.getCurrentItem()) {
                    case EXPLORE:
                        startActivity(new Intent(MainActivity.this, ProductActivity.class));
                        break;
                    case SHOP:
                        dialog.show();
                        break;
                    case COLLECTIONS:
                        startActivity(new Intent(MainActivity.this, AddCollectionActivity.class));
                        break;
                }
            }
        });
        dialog.findViewById(R.id.container_dialog_category).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = category.getText().toString().trim();
                if (s.isEmpty()) {
                    Toast.makeText(MainActivity.this, "please enter category name", Toast.LENGTH_SHORT).show();
                    category.requestFocus();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    reference.child(CATG).child(s).setValue(s);
                    shopFragment.initFireB();
                }
                dialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart_menu_main) {
            viewPager.setCurrentItem(MYBAG, false);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (viewPager.getCurrentItem()) {
            case EXPLORE:
                finish();
            case SHOP:
            case COLLECTIONS:
            case WISHLIST:
            case MYBAG:
            case PROFILE:
                viewPager.setCurrentItem(EXPLORE, false);
        }
    }
}
