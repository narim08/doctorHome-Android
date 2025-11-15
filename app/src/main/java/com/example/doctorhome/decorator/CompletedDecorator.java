package com.example.doctorhome.decorator;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import java.util.Collection;
import java.util.HashSet;

//복용 완료 날짜 데코레이터 (초록색)
public class CompletedDecorator implements DayViewDecorator {

    private final ColorDrawable greenDrawable;
    private final HashSet<CalendarDay> dates;

    public CompletedDecorator(Collection<CalendarDay> dates) {
        this.greenDrawable = new ColorDrawable(Color.parseColor("#4CAF50"));
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(greenDrawable);
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
