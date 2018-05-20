package biz.zenpets.users.utils.adapters.kennels;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.kennels.Kennel;

public class KennelsAdapter extends RecyclerView.Adapter<KennelsAdapter.KennelsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Kennel> arrKennels;

    public KennelsAdapter(Activity activity, ArrayList<Kennel> arrKennels) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrKennels = arrKennels;
    }

    @Override
    public int getItemCount() {
        return arrKennels.size();
    }

    @Override
    public void onBindViewHolder(@NonNull KennelsVH holder, final int position) {
        final Kennel data = arrKennels.get(position);
    }

    @NonNull
    @Override
    public KennelsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.kennels_item, parent, false);

        return new KennelsVH(itemView);
    }

    class KennelsVH extends RecyclerView.ViewHolder	{

        KennelsVH(View v) {
            super(v);
        }
    }
}