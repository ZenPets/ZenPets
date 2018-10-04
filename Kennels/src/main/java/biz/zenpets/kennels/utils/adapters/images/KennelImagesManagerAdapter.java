package biz.zenpets.kennels.utils.adapters.images;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import biz.zenpets.kennels.utils.models.images.KennelImage;
import biz.zenpets.kennels.utils.models.images.KennelImagesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class KennelImagesManagerAdapter extends RecyclerView.Adapter<KennelImagesManagerAdapter.ImagesVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<KennelImage> arrImages;

    public KennelImagesManagerAdapter(Activity activity, ArrayList<KennelImage> arrImages) {

        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrImages = arrImages;
    }

    @Override
    public int getItemCount() {
        return arrImages.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ImagesVH holder, @SuppressLint("RecyclerView") final int position) {
        final KennelImage data = arrImages.get(position);

        /* SET THE KENNEL IMAGE */
        String strKennelImageURL = data.getKennelImageURL();
        if (strKennelImageURL != null
                && !strKennelImageURL.equalsIgnoreCase("")
                && !strKennelImageURL.equalsIgnoreCase("null")) {
            Uri uri = Uri.parse(strKennelImageURL);
            holder.imgvwKennelImage.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.ic_business_black_48dp)
                    .build();
            holder.imgvwKennelImage.setImageURI(request.getSourceUri());
        }

        /* DELETE A KENNEL IMAGE */
        holder.imgvwImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(activity)
                        .icon(ContextCompat.getDrawable(activity, R.drawable.ic_info_black_24dp))
                        .title("Delete Image?")
                        .content("Are your sure you want to delete this Image?")
                        .cancelable(false)
                        .positiveText("Delete")
                        .negativeText("No")
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                KennelImagesAPI api = ZenApiClient.getClient().create(KennelImagesAPI.class);
                                Call<KennelImage> call = api.deleteKennelImage(data.getKennelImageID());
                                call.enqueue(new Callback<KennelImage>() {
                                    @Override
                                    public void onResponse(Call<KennelImage> call, Response<KennelImage> response) {
                                        if (response.isSuccessful())    {
                                            notifyDataSetChanged();
                                            notifyItemChanged(position);
                                            arrImages.remove(position);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<KennelImage> call, Throwable t) {
                                        Log.e("DELETE FAILURE", t.getMessage());
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
        });
    }

    @NonNull
    @Override
    public ImagesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.kennel_image_manager_item, parent, false);

        return new ImagesVH(itemView);
    }

    class ImagesVH extends RecyclerView.ViewHolder   {
        final SimpleDraweeView imgvwKennelImage;
        IconicsImageView imgvwImageDelete;

        ImagesVH(View v) {
            super(v);
            imgvwKennelImage = v.findViewById(R.id.imgvwKennelImage);
            imgvwImageDelete = v.findViewById(R.id.imgvwImageDelete);
        }
    }
}