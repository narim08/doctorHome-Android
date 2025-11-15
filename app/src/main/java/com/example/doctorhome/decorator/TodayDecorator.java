package com.example.doctorhome.decorator;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class TodayDecorator implements DayViewDecorator {

    private final CalendarDay today;

    public TodayDecorator() {
        this.today = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return today.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED));
    }
}