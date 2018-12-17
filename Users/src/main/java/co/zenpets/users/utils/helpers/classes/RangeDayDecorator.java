package co.zenpets.users.utils.helpers.classes;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;

import co.zenpets.users.R;

public class RangeDayDecorator implements DayViewDecorator {

    private final HashSet<CalendarDay> list = new HashSet<>();
    private final Drawable drawable;

    public RangeDayDecorator(final Context context) {
        drawable = context.getResources().getDrawable(R.drawable.my_selector);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return list.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }

    /** We're changing the dates, so make sure to call **/
    public void addFirstAndLast(final CalendarDay first, final CalendarDay last) {
        list.clear();
        list.add(first);
        list.add(last);
    }
}