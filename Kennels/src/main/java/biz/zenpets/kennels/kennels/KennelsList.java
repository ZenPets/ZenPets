package biz.zenpets.kennels.kennels;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.creator.kennel.KennelCreator;
import biz.zenpets.kennels.details.kennel.KennelDetails;
import biz.zenpets.kennels.modifier.kennel.KennelModifier;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.TypefaceSpan;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import biz.zenpets.kennels.utils.models.kennels.Kennel;
import biz.zenpets.kennels.utils.models.kennels.KennelPages;
import biz.zenpets.kennels.utils.models.kennels.Kennels;
import biz.zenpets.kennels.utils.models.kennels.KennelsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelsList extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE KENNEL OWNER'S ID **/
    private String KENNEL_OWNER_ID = null;

    /** AN ARRAY LIST TO STORE THE LIST OF KENNELS **/
    ArrayList<Kennel> arrKennels = new ArrayList<>();

    /** A LINEAR LAYOUT MANAGER INSTANCE **/
    LinearLayoutManager manager;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listKennels) RecyclerView listKennels;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW KENNEL (FAB) **/
    @OnClick(R.id.fabNewKennel) void newFabKennel() {
        /* CHECK TOTAL KENNELS CREATED BY CURRENT KENNEL OWNER */
        checkPublishedKennels();
    }

    /** ADD A NEW KENNEL (EMPTY LAYOUT) **/
    @OnClick(R.id.linlaEmpty) void newKennel()  {
        /* CHECK TOTAL KENNELS CREATED BY CURRENT KENNEL OWNER */
        checkPublishedKennels();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_kennels_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* GET THE KENNEL OWNER'S ID */
        KENNEL_OWNER_ID = getApp().getKennelOwnerID();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* SHOW THE PROGRESS AND FETCH THE LIST OF KENNELS */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchKennels();
    }

    /***** FETCH THE LIST OF KENNELS *****/
    private void fetchKennels() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennels> call = api.fetchKennelsListByOwner(KENNEL_OWNER_ID);
        call.enqueue(new Callback<Kennels>() {
            @Override
            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
//                Log.e("RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getKennels() != null)    {
                    arrKennels = response.body().getKennels();
                    if (arrKennels.size() > 0)  {
                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listKennels.setAdapter(new KennelsAdapter(KennelsList.this, arrKennels));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listKennels.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listKennels.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listKennels.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 101)   {
            /* CLEAR THE ARRAY LIST */
            arrKennels.clear();

            /* SHOW THE PROGRESS AND FETCH THE LIST OF KENNELS AGAIN */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchKennels();
        } else if (resultCode == Activity.RESULT_OK && requestCode == 102)   {
            /* CLEAR THE ARRAY LIST */
            arrKennels.clear();

            /* SHOW THE PROGRESS AND FETCH THE LIST OF KENNELS AGAIN */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchKennels();
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listKennels.setLayoutManager(manager);
        listKennels.setHasFixedSize(true);

        /* INSTANTIATE AND SET THE ADAPTER */
        listKennels.setAdapter(new KennelsAdapter(this, arrKennels));
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Your Kennels";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(this), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
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

    /** CHECK TOTAL KENNELS CREATED BY CURRENT KENNEL OWNER **/
    private void checkPublishedKennels() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<KennelPages> call = api.fetchOwnerKennels(KENNEL_OWNER_ID);
        call.enqueue(new Callback<KennelPages>() {
            @Override
            public void onResponse(Call<KennelPages> call, Response<KennelPages> response) {
                if (response.body() != null)    {
                    int publishedKennels = Integer.parseInt(response.body().getTotalKennels());
                    Log.e("KENNELS", String.valueOf(publishedKennels));
                    if (publishedKennels < 2)   {
                        Intent intent = new Intent(KennelsList.this, KennelCreator.class);
                        intent.putExtra("LISTING_TYPE", "FREE");
                        startActivityForResult(intent, 101);
                    } else {
                        new MaterialDialog.Builder(KennelsList.this)
                                .icon(ContextCompat.getDrawable(KennelsList.this, R.drawable.ic_info_black_24dp))
                                .title("Exceeding Kennel Limit")
                                .content(getString(R.string.kennel_creator_limit_exceed_message))
                                .cancelable(false)
                                .positiveText("Sure Thing")
                                .negativeText("Not Now")
                                .theme(Theme.LIGHT)
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent = new Intent(KennelsList.this, KennelCreator.class);
                                        intent.putExtra("LISTING_TYPE", "PAID");
                                        startActivityForResult(intent, 101);
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<KennelPages> call, Throwable t) {
            }
        });
    }

    /** THE KENNELS ADAPTER **/
    private class KennelsAdapter extends RecyclerView.Adapter<KennelsAdapter.KennelsVH> {

        /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
        private final Activity activity;

        /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
        private final ArrayList<Kennel> arrKennelsAdapter;

        KennelsAdapter(Activity activity, ArrayList<Kennel> arrKennelsAdapter) {

            /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
            this.activity = activity;

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrKennelsAdapter = arrKennelsAdapter;
        }

        @Override
        public int getItemCount() {
            return arrKennelsAdapter.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final KennelsAdapter.KennelsVH holder, final int position) {
            final Kennel data = arrKennelsAdapter.get(position);

            /* SET THE KENNEL COVER PHOTO */
            String strKennelCoverPhoto = data.getKennelCoverPhoto();
            if (strKennelCoverPhoto != null
                    && !strKennelCoverPhoto.equalsIgnoreCase("")
                    && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
                Uri uri = Uri.parse(strKennelCoverPhoto);
                holder.imgvwKennelCoverPhoto.setImageURI(uri);
            } else {
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithResourceId(R.drawable.empty_graphic)
                        .build();
                holder.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
            }

            /* SET THE KENNEL NAME */
            if (data.getKennelName() != null)   {
                holder.txtKennelName.setText(data.getKennelName());
            }

            /* SET THE KENNEL ADDRESS */
            String strKennelAddress = data.getKennelAddress();
            String cityName = data.getCityName();
            String kennelPinCode = data.getKennelPinCode();
            holder.txtKennelAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, strKennelAddress, cityName, kennelPinCode));

            /* SET THE CAPACITY OF LARGE SIZE PETS */
            if (data.getKennelPetCapacity() != null
                    && !data.getKennelPetCapacity().equalsIgnoreCase("")
                    && !data.getKennelPetCapacity().equalsIgnoreCase("null"))   {
                holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_placeholder, data.getKennelPetCapacity()));
            } else {
                holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_zero));
            }

            /* SHOW THE KENNEL OPTIONS POPUP MENU */
            holder.imgvwKennelOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu pm = new PopupMenu(activity, holder.imgvwKennelOptions);
                    pm.getMenuInflater().inflate(R.menu.pm_kennel_options, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())   {
                                case R.id.menuDetails:
                                    Intent intentDetails = new Intent(activity, KennelDetails.class);
                                    intentDetails.putExtra("KENNEL_ID", data.getKennelID());
                                    startActivity(intentDetails);
                                    break;
                                case R.id.menuEdit:
                                    Intent intentEdit = new Intent(activity, KennelModifier.class);
                                    intentEdit.putExtra("KENNEL_ID", data.getKennelID());
                                    startActivityForResult(intentEdit, 102);
                                    break;
                                case R.id.menuDelete:
                                    /* SHOW THE DELETE DIALOG */
                                    showDeleteDialog(data.getKennelID(), data.getKennelName(), position);
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    pm.show();
                }
            });
        }

        /***** SHOW THE DELETE DIALOG *****/
        private void showDeleteDialog(final String kennelID, final String kennelName, final int position) {
            new MaterialDialog.Builder(activity)
                    .icon(ContextCompat.getDrawable(activity, R.drawable.ic_info_black_24dp))
                    .title("Delete Kennel?")
                    .content(activity.getString(R.string.delete_kennel_content, kennelName))
                    .cancelable(false)
                    .positiveText("Delete")
                    .negativeText("No")
                    .theme(Theme.LIGHT)
                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
                            Call<Kennel> call = api.deleteKennelRecord(kennelID);
                            call.enqueue(new Callback<Kennel>() {
                                @Override
                                public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                                    if (response.isSuccessful())    {
                                        /* SHOW THE SUCCESS MESSAGE */
                                        Toast.makeText(activity, "Record deleted...", Toast.LENGTH_SHORT).show();

                                        /* DELETE THE ITEM FROM THE ARRAY LIST (THIS IS TEMPORARY OF COURSE) */
                                        arrKennelsAdapter.remove(position);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(activity, "Failed to delete record...", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Kennel> call, Throwable t) {
//                                    Log.e("DELETE FAILURE", t.getMessage());
                                    Crashlytics.logException(t);
                                }
                            });
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        @NonNull
        @Override
        public KennelsAdapter.KennelsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.dash_kennels_item, parent, false);

            return new KennelsAdapter.KennelsVH(itemView);
        }

        class KennelsVH extends RecyclerView.ViewHolder	{

            SimpleDraweeView imgvwKennelCoverPhoto;
            TextView txtKennelName;
            IconicsImageView imgvwKennelOptions;
            TextView txtKennelAddress;
            TextView txtPetCapacity;

            KennelsVH(View v) {
                super(v);

                imgvwKennelCoverPhoto = v.findViewById(R.id.imgvwKennelCoverPhoto);
                txtKennelName = v.findViewById(R.id.txtKennelName);
                imgvwKennelOptions = v.findViewById(R.id.imgvwKennelOptions);
                txtKennelAddress = v.findViewById(R.id.txtKennelAddress);
                txtPetCapacity = v.findViewById(R.id.txtPetCapacity);
            }
        }
    }
}