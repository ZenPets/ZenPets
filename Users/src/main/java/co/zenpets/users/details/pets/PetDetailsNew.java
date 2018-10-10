package co.zenpets.users.details.pets;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.zenpets.users.R;
import co.zenpets.users.details.pets.details.DetailsFragment;
import co.zenpets.users.details.pets.details.MedicalRecordsFragment;
import co.zenpets.users.details.pets.details.PrescriptionsFragment;
import co.zenpets.users.details.pets.details.VaccinationsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PetDetailsNew extends AppCompatActivity {

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_details_new);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configToolbar();

        /* SETUP THE VIEW PAGER **/
        setupViewPager(viewPager);

        /* SETUP THE TAB LAYOUT **/
        tabLayout.setupWithViewPager(viewPager);

//        /* APPLY THE CUSTOM FONT TO THE TITLES **/
//        changeTabsFont();
    }

    /** SETUP THE VIEW PAGER **/
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DetailsFragment(), "Details");
        adapter.addFragment(new VaccinationsFragment(), "Vaccinations");
        adapter.addFragment(new PrescriptionsFragment(), "Prescriptions");
        adapter.addFragment(new MedicalRecordsFragment(), "Records");
        viewPager.setAdapter(adapter);
    }

    /** THE PAGER ADAPTER **/
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /** APPLY THE CUSTOM FONT TO THE TITLES **/
    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
                }
            }
        }
    }

    /** CONFIGURE THE TOOLBAR **/
    private void configToolbar() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Pet Details";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }
}