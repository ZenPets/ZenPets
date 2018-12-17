package co.zenpets.users.creator.kennel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.users.R;
import co.zenpets.users.utils.helpers.classes.RangeDayDecorator;

public class KennelDateSelector extends AppCompatActivity
        implements OnDateSelectedListener, OnRangeSelectedListener {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, d MMM yyyy");

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.kennelBookingCalendar) MaterialCalendarView kennelBookingCalendar;

    private RangeDayDecorator decorator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_booking_date_selector);
        ButterKnife.bind(this);

        decorator = new RangeDayDecorator(this);

        kennelBookingCalendar.setOnDateChangedListener(this);
        kennelBookingCalendar.setOnRangeSelectedListener(this);
        kennelBookingCalendar.addDecorator(decorator);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        final String text = selected ? FORMATTER.format(date.getDate()) : "No Selection";
        Toast.makeText(KennelDateSelector.this, text, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
        if (dates.size() > 0) {
            Log.e("SIZE", String.valueOf(dates.size()));
            decorator.addFirstAndLast(dates.get(0), dates.get(dates.size() - 1));
            for (int i = 0; i < dates.size(); i++) {
                Log.e("DATE", String.valueOf(dates.get(i).getDate()));
            }
            kennelBookingCalendar.invalidateDecorators();
        }
    }
}