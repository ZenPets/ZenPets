package co.zenpets.kennels.utils.adapters.kennels.inventory;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.models.inventory.Inventory;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Inventory> arrInventory;

    public InventoryAdapter(Activity activity, ArrayList<Inventory> arrInventory) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrInventory = arrInventory;
    }

    @Override
    public int getItemCount() {
        return arrInventory.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryVH holder, final int position) {
        final Inventory data = arrInventory.get(position);

        /* SET THE INVENTORY ITEM NAME */
        if (data.getInventoryTypeName() != null)    {
            holder.txtInventoryName.setText(data.getKennelInventoryName());
        }
    }

    @NonNull
    @Override
    public InventoryVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.dash_inventory_item, parent, false);

        return new InventoryVH(itemView);
    }

    class InventoryVH extends RecyclerView.ViewHolder	{

        TextView txtInventoryName;

        InventoryVH(View v) {
            super(v);

            txtInventoryName = v.findViewById(R.id.txtInventoryName);
        }
    }
}