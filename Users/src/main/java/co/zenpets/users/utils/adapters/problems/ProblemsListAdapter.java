package co.zenpets.users.utils.adapters.problems;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.problems.Problem;

public class ProblemsListAdapter extends BaseAdapter {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
    private LayoutInflater inflater = null;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<Problem> arrProblems;

    /** SELECTED ITEM POSITION **/
    private int selectedItem = -1;

    public ProblemsListAdapter(Activity activity, ArrayList<Problem> arrProblems) {

        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrProblems = arrProblems;

        /* INSTANTIATE THE LAYOUT INFLATER */
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrProblems.size();
    }

    @Override
    public Object getItem(int position) {
        return arrProblems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectedIndex(int position)  {
        selectedItem = position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /* A VIEW HOLDER INSTANCE */
        ViewHolder holder;

        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE */
        View vi = convertView;

        /* CHECK CONVERT VIEW STATUS */
        if (convertView == null)	{
            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi */
            vi = inflater.inflate(R.layout.problems_list_item, null);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtProblemText = vi.findViewById(R.id.txtProblemText);
            holder.rdbtnSelectedProblem = vi.findViewById(R.id.rdbtnSelectedProblem);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE SELECTED POSITION */
        if (selectedItem == position)   {
            holder.rdbtnSelectedProblem.setChecked(true);
        } else {
            holder.rdbtnSelectedProblem.setChecked(false);
        }

        /* SET THE PROBLEM TEXT */
        holder.txtProblemText.setText(arrProblems.get(position).getProblemText());

        return vi;
    }


    private static class ViewHolder	{
        AppCompatTextView txtProblemText;
        AppCompatRadioButton rdbtnSelectedProblem;
    }
}