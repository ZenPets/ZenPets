package biz.zenpets.kennels.landing.newmodules;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.inventory.KennelInventory;
import biz.zenpets.kennels.kennels.TestKennelsList;
import biz.zenpets.kennels.reports.ReportsActivity;
import biz.zenpets.kennels.reviews.ReviewsList;
import biz.zenpets.kennels.utils.TypefaceSpan;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    /** SHOW THE LIST OF KENNELS **/
    @OnClick(R.id.linlaKennels) void showKennels()  {
//        Intent intent = new Intent(getActivity(), KennelsList.class);
        Intent intent = new Intent(getActivity(), TestKennelsList.class);
        startActivity(intent);
    }

    /** MANAGE THE KENNEL INVENTORY **/
    @OnClick(R.id.linlaInventory) void manageInventory()    {
        Intent intent = new Intent(getActivity(), KennelInventory.class);
        startActivity(intent);
    }

    /** SHOW THE LIST OF REVIEWS **/
    @OnClick(R.id.linlaReviews) void showReviews()  {
        Intent intent = new Intent(getActivity(), ReviewsList.class);
        startActivity(intent);
    }

    /** SHOW THE LIST OF CLIENTS **/
    @OnClick(R.id.linlaClients) void showClients()  {
    }

    /** SHOW THE REPORTS FOR THE LISTED KENNELS **/
    @OnClick(R.id.linlaReports) void showReports()  {
        Intent intent = new Intent(getActivity(), ReportsActivity.class);
        startActivity(intent);
    }

    /** SHOW THE SETTINGS PAGES **/
    @OnClick(R.id.linlaSettings) void  showSettings()   {
    }

    /** SHOW THE HELP PAGE **/
    @OnClick(R.id.linlaHelp) void showHelp()    {
    }

    /** SHOW THE FEEDBACK PAGE **/
    @OnClick(R.id.linlaFeedback) void showFeedback()    {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE **/
        setRetainInstance(true);

        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU **/
        setHasOptionsMenu(true);

        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES **/
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* CONFIGURE THE TOOLBAR */
        configTB();
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = getActivity().findViewById(R.id.myToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        String strTitle = "Zen Pets - Home";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
            default:
                break;
        }
        return false;
    }
}