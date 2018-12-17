package co.zenpets.users.creator.kennel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.users.R;

public class NewKennelDateSelector extends AppCompatActivity {

    /** A CALENDAR INSTANCE **/
    Calendar calendar;

    /** THE SIMPLE DATE FORMAT INSTANCES **/
    SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat formatDisplay = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());

    /** THE DISPLAY DATE AND THE FROM AND TO DATES **/
    int TOTAL_DAYS = 0;
    String DISPLAY_DATE = null;
    String DATE_FROM = null;
    String DATE_TO = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.kennelCalendar) CalendarPickerView kennelCalendar;
    @BindView(R.id.txtCheckIn) TextView txtCheckIn;
    @BindView(R.id.txtCheckOut) TextView txtCheckOut;
    @BindView(R.id.btnApplyDates) Button btnApplyDates;

    /** APPLY THE SELECTED DATES **/
    @OnClick(R.id.btnApplyDates) void applyDates()  {
        Intent intent = new Intent();
        intent.putExtra("DATE_FROM", DATE_FROM);
        intent.putExtra("DATE_TO", DATE_TO);
        intent.putExtra("TOTAL_DAYS", TOTAL_DAYS);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_booking_date_selector_new);
        ButterKnife.bind(this);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        Date today = new Date();
        kennelCalendar.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(today);

        kennelCalendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
//                Log.e("SELECTED DATE", String.valueOf(date));

                calendar = Calendar.getInstance();
                calendar.setTime(date);
                DISPLAY_DATE = formatDisplay.format(calendar.getTime());
                txtCheckIn.setText(DISPLAY_DATE);
//                Log.e("DISPLAY START DATE", DISPLAY_DATE);

                /* SET THE FROM DATE */
                DATE_FROM = formatDB.format(calendar.getTime());
//                Log.e("DATE FROM", DATE_FROM);

                List<Date> dates = kennelCalendar.getSelectedDates();
                if (dates.size() > 1)   {
                    TOTAL_DAYS = dates.size() - 1;
//                    Log.e("TOTAL DAYS", String.valueOf(TOTAL_DAYS));
                    btnApplyDates.setEnabled(true);
                    btnApplyDates.setBackground(getResources().getDrawable(R.drawable.generic_button_bg));

                    for (int i = 0; i < dates.size(); i++) {
                        if (i == 0)  {
                            calendar = Calendar.getInstance();
                            calendar.setTime(dates.get(i));
                            SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            SimpleDateFormat formatDisplay = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
                            DISPLAY_DATE = formatDisplay.format(calendar.getTime());
                            txtCheckIn.setText(DISPLAY_DATE);
//                            Log.e("DISPLAY START DATE", DISPLAY_DATE);

                            /* SET THE FROM DATE */
                            DATE_FROM = formatDB.format(calendar.getTime());
//                            Log.e("DATE FROM", DATE_FROM);
                        }

                        if (i == dates.size() - 1)  {
                            calendar = Calendar.getInstance();
                            calendar.setTime(dates.get(i));
                            SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            SimpleDateFormat formatDisplay = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
                            DISPLAY_DATE = formatDisplay.format(calendar.getTime());
                            txtCheckOut.setText(DISPLAY_DATE);
//                            Log.e("DISPLAY START DATE", DISPLAY_DATE);

                            /* SET THE TO DATE */
                            DATE_TO = formatDB.format(calendar.getTime());
//                            Log.e("DATE TO", DATE_TO);
                        }
                    }
                } else {
                    btnApplyDates.setEnabled(false);
                    btnApplyDates.setBackground(getResources().getDrawable(R.drawable.generic_button_disabled_bg));
                }
//                Log.e("SIZE", String.valueOf(dates.size()));
            }

            @Override
            public void onDateUnselected(Date date) {
//                Log.e("UNSELECTED DATE", String.valueOf(date));
            }
        });
    }
}