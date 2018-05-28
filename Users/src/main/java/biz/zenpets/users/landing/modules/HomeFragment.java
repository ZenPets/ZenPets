package biz.zenpets.users.landing.modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import biz.zenpets.users.R;
import biz.zenpets.users.adoptions.AdoptionsList;
import biz.zenpets.users.doctors.NewDoctorsList;
import biz.zenpets.users.kennels.KennelsList;
import biz.zenpets.users.trainers.TrainersList;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    /** SHOW THE LIST OF DOCTORS **/
    @OnClick(R.id.linlaAppointment) void showDoctors()   {
        Intent intent = new Intent(getActivity(), NewDoctorsList.class);
        startActivity(intent);
    }

    /** SHOW THE LIST OF ADOPTIONS **/
    @OnClick(R.id.linlaAdopt) void showAdoptions()  {
        Intent intent = new Intent(getActivity(), AdoptionsList.class);
        startActivity(intent);
    }

    /** SHOW THE LIST OF GROOMERS **/
    @OnClick(R.id.linlaGroomers) void showGroomers()    {
        Toast.makeText(getActivity(), "Coming real soon...", Toast.LENGTH_SHORT).show();
    }

    /** SHOW THE LIST OF PET KENNELS **/
    @OnClick(R.id.linlaPetKennels) void showPetHostels() {
        Intent intent = new Intent(getActivity(), KennelsList.class);
        startActivity(intent);
    }

    /** SHOW THE LIST OF HOME BOARDINGS **/
    @OnClick(R.id.linlaHomeBoarding) void showHomeBoardings()   {
        Toast.makeText(getActivity(), "Coming real soon...", Toast.LENGTH_SHORT).show();
    }

    /** SHOW THE LIST OF TRAINERS **/
    @OnClick(R.id.linlaTrainers) protected void showTrainers()  {
        Intent intent = new Intent(getActivity(), TrainersList.class);
        startActivity(intent);
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

        /* CONFIGURE THE TOOLBAR **/
        configTB();
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = getActivity().findViewById(R.id.myToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        String strTitle = "Zen Pets - Home";
//        SpannableString s = new SpannableString(strTitle);
//        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(strTitle);
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