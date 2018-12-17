package co.zenpets.kennels.details.bookings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.kennels.R;

public class BookingHistory extends AppCompatActivity {

    /** THE INCOMING INVENTORY ID **/
    String INVENTORY_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listBookings) RecyclerView listBookings;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_hostory_list);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /** FETCH THE INVENTORY BOOKING HISTORY **/
    private void fetchBookingHistory() {
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("INVENTORY_ID"))   {
            INVENTORY_ID = bundle.getString("INVENTORY_ID");
            if (INVENTORY_ID != null)   {
                /* FETCH THE INVENTORY BOOKING HISTORY */
                fetchBookingHistory();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Failed to get required info...",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Failed to get required info...",
                    Toast.LENGTH_SHORT).show();
        }
    }
}