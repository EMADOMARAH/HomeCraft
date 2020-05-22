package tutorial.example.ecomm;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import tutorial.example.ecomm.fragments.CardFragment;
import tutorial.example.ecomm.fragments.OrdersFragment;
import tutorial.example.ecomm.fragments.PasswordFragment;
import tutorial.example.ecomm.fragments.ShippingFragment;

@SuppressWarnings("ConstantConditions")
public class SettingActivity extends AppCompatActivity {
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initViews();
        initFrags();
    }

    private void initViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_setting));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        mode = getIntent().getIntExtra("mode", 0);
    }

    private void initFrags() {
        switch (mode) {
            case 0:
                loadFragment(new PasswordFragment());
                break;
            case 1:
                loadFragment(new ShippingFragment(0));
                break;
            case 2:
                loadFragment(new CardFragment(0));
                break;
            case 3:
                loadFragment(new OrdersFragment());
                break;
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_setting, fragment)
                .commit();
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
        finish();
    }
}
