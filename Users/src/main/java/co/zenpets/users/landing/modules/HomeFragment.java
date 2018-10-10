package co.zenpets.users.landing.modules;

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
import android.view.View;
import android.view.ViewGroup;

import co.zenpets.users.R;
import co.zenpets.users.adoptions.TestAdoptionsList;
import co.zenpets.users.boarding.BoardingsList;
import co.zenpets.users.doctors.DoctorsList;
import co.zenpets.users.groomers.GroomersList;
import co.zenpets.users.kennels.KennelsList;
import co.zenpets.users.trainers.TrainersList;
import co.zenpets.users.utils.TypefaceSpan;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("ConstantConditions")
public class HomeFragment extends Fragment {

    /** SHOW THE LIST OF DOCTORS **/
    @OnClick(R.id.linlaAppointment) void showDoctors()   {
        Intent intent = new Intent(getActivity(), DoctorsList.class);
        startActivity(intent);
    }

    /** SHOW THE LIST OF ADOPTIONS **/
    @OnClick(R.id.linlaAdopt) void showAdoptions()  {
        Intent intent = new Intent(getActivity(), TestAdoptionsList.class);
        startActivity(intent);
    }

    /** SHOW THE LIST OF GROOMERS **/
    @OnClick(R.id.linlaGroomers) void showGroomers()    {
        Intent intent = new Intent(getActivity(), GroomersList.class);
        startActivity(intent);
    }

    /** SHOW THE LIST OF PET KENNELS **/
    @OnClick(R.id.linlaPetKennels) void showPetHostels() {
        Intent intent = new Intent(getActivity(), KennelsList.class);
        startActivity(intent);
    }

    /** SHOW THE LIST OF HOME BOARDINGS **/
    @OnClick(R.id.linlaHomeBoarding) void showHomeBoardings()   {
        Intent intent = new Intent(getActivity(), BoardingsList.class);
        startActivity(intent);
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
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.landing_notifications, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                getActivity().finish();
//                break;
//            default:
//                break;
//        }
//        return false;
//    }
}