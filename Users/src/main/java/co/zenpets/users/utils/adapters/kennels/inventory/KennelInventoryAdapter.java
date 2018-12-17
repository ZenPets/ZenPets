package co.zenpets.users.utils.adapters.kennels.inventory;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.kennels.bookings.Unit;

public class KennelInventoryAdapter extends RecyclerView.Adapter<KennelInventoryAdapter.UnitsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Unit> arrUnits;

    public KennelInventoryAdapter(Activity activity, ArrayList<Unit> arrUnits) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrUnits = arrUnits;
    }

    @Override
    public int getItemCount() {
        return arrUnits.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final UnitsVH holder, final int position) {
        Unit message = arrUnits.get(position);
    }

    @NonNull
    @Override
    public UnitsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.kennel_slot_selector_item, parent, false);

        return new UnitsVH(itemView);
    }

    class UnitsVH extends RecyclerView.ViewHolder	{

        UnitsVH(View v) {
            super(v);
        }
    }
}