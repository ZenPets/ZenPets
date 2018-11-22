package co.zenpets.kennels.utils.adapters.inventory;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.models.inventory.Type;

public class InventoryTypesAdapter extends ArrayAdapter<Type> {

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final List<Type> arrTypes;

    public InventoryTypesAdapter(Activity activity, int resource, List<Type> arrTypes) {
        super(activity, resource);

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrTypes = arrTypes;

        /* INSTANTIATE THE LAYOUT INFLATER INSTANCE */
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrTypes.size();
    }

    @Override
    public Type getItem(int position) {
        return arrTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

        /* A VIEW HOLDER INSTANCE **/
        ViewHolder holder;

        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE **/
        View vi = convertView;

        /* CHECK CONVERT VIEW STATUS **/
        if (convertView == null)	{
            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi **/
            vi = inflater.inflate(R.layout.inventory_types_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtInventoryType = vi.findViewById(R.id.txtInventoryType);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE INVENTORY TYPE NAME **/
        String strKennelName = arrTypes.get(position).getInventoryTypeName();
        if (strKennelName != null)	{
            holder.txtInventoryType.setText(strKennelName);
        }

        return vi;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        /* A VIEW HOLDER INSTANCE **/
        ViewHolder holder;

        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE **/
        View vi = convertView;

        /* CHECK CONVERT VIEW STATUS **/
        if (convertView == null)	{
            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi **/
            vi = inflater.inflate(R.layout.inventory_types_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtInventoryType = vi.findViewById(R.id.txtInventoryType);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE INVENTORY TYPE NAME **/
        String strKennelName = arrTypes.get(position).getInventoryTypeName();
        if (strKennelName != null)	{
            holder.txtInventoryType.setText(strKennelName);
        }

        return vi;
    }

    private static class ViewHolder	{
        TextView txtInventoryType;
    }
}