package biz.zenpets.users.user.pets;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.creator.pet.NewPetCreator;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.pet.UserPetsAdapter;
import biz.zenpets.users.utils.helpers.pets.pet.FetchUserPets;
import biz.zenpets.users.utils.helpers.pets.pet.FetchUserPetsInterface;
import biz.zenpets.users.utils.models.pets.pets.Pet;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("ConstantConditions")
public class UserPets extends AppCompatActivity
        implements FetchUserPetsInterface {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE USER ID **/
    private String USER_ID = null;

    /** THE PETS ADAPTER AND ARRAY LIST **/
    private UserPetsAdapter adapter;
    private ArrayList<Pet> arrPets = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listPets) RecyclerView listPets;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW PET **/
    @OnClick(R.id.linlaEmpty) void newPet() {
        Intent addNewPet = new Intent(this, NewPetCreator.class);
        startActivityForResult(addNewPet, 101);
    }

    /** ADD A NEW PET (FAB) **/
    @OnClick(R.id.fabNewPet) void fabNewPet()   {
        Intent addNewPet = new Intent(this, NewPetCreator.class);
        startActivityForResult(addNewPet, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_pets_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();
        if (USER_ID != null)    {
            /* SHOW THE PROGRESS AND FETCH THE USER'S PETS */
            linlaProgress.setVisibility(View.VISIBLE);
            new FetchUserPets(this).execute(USER_ID);
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void userPets(ArrayList<Pet> data) {
        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
        arrPets = data;

        /* CHECK FOR THE SIZE OF THE RESULT */
        if (arrPets.size() > 0) {
            /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY VIEW */
            listPets.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);

            /* SET THE ADAPTER TO THE RECYCLER VIEW */
            listPets.setAdapter(new UserPetsAdapter(UserPets.this, arrPets));
        } else {
            /* HIDE THE RECYCLER VIEW AND SHOW THE EMPTY VIEW */
            linlaEmpty.setVisibility(View.VISIBLE);
            listPets.setVisibility(View.GONE);
        }

        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
        linlaProgress.setVisibility(View.GONE);
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "My Pets";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = new MenuInflater(UserPets.this);
//        inflater.inflate(R.menu.activity_common_creator, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuNew:
                Intent addNewPet = new Intent(this, NewPetCreator.class);
                startActivityForResult(addNewPet, 101);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 101)  {
            /* CLEAR THE ARRAY */
            arrPets.clear();

            /* FETCH THE LIST OF PETS AGAIN */
            new FetchUserPets(this).execute(USER_ID);
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listPets.setLayoutManager(manager);
        listPets.setHasFixedSize(true);

        /* INSTANTIATE AND SET THE ADAPTER */
        adapter = new UserPetsAdapter(UserPets.this, arrPets);
        listPets.setAdapter(adapter);
    }
}