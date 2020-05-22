package tutorial.example.ecomm.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tutorial.example.ecomm.R;

import static tutorial.example.ecomm.StartActivity.CATG;
import static tutorial.example.ecomm.StartActivity.reference;

@SuppressWarnings({"ConstantConditions", "deprecation"})
public class ShopFragment extends Fragment {
    private List<CategoryFragment> categoryFragmentList;
    private FragmentPagerAdapter fragmentPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initFireB();
    }

    public void initFireB() {
        reference.child(CATG).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryFragmentList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getValue(String.class);
                    CategoryFragment fragment = new CategoryFragment(name);
                    categoryFragmentList.add(fragment);
                }
                fragmentPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        final ViewPager viewPager = getView().findViewById(R.id.viewPager_shop);
        TabLayout tabLayout = getView().findViewById(R.id.tabLayout_shop);

        categoryFragmentList = new ArrayList<>();

        fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return categoryFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return categoryFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return categoryFragmentList.get(position).getName();
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

            }
        };

        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
