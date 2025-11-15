package com.example.doctorhome.decorator;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import java.util.Collection;
import java.util.HashSet;

//복용 예정 날짜 데코레이터 (하늘색)
public class ScheduledDecorator implements DayViewDecorator {

    private final ColorDrawable skyBlueDrawable;
    private final HashSet<CalendarDay> dates;

    public ScheduledDecorator(Collection<CalendarDay> dates) {
        this.skyBlueDrawable = new ColorDrawable(Color.parseColor("#87CEEB"));
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(skyBlueDrawable);
    }

    public void addDate(CalendarDay day) {
        dates.add(day);
    }

    public void removeDate(CalendarDay day) {
        dates.remove(day);
    }

    public void setDates(Collection<CalendarDay> newDates) {
        dates.clear();
        dates.addAll(newDates);
    }
}
