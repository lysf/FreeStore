package com.snailgame.cjg.personal.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.personal.widget.wheel.OnWheelChangedListener;
import com.snailgame.cjg.personal.widget.wheel.WheelView;
import com.snailgame.cjg.personal.widget.wheel.adapters.AbstractWheelTextAdapter;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import third.BottomSheet.BottomSheet;
import third.BottomSheet.ClosableSlidingLayout;


/**
 * 选择生日
 */
public class UserBirthdaySelectorDialog extends BottomSheet implements View.OnClickListener {

    private Context context;
    @Bind(R.id.wv_birth_year)
    WheelView wvYearView;
    @Bind(R.id.wv_birth_month)
    WheelView wvMonthView;
    @Bind(R.id.wv_birth_day)
    WheelView wvDayView;

    @Bind(R.id.btn_sure)
    TextView btnSure;

    private ArrayList<String> arry_years = new ArrayList<String>();
    private ArrayList<String> arry_months = new ArrayList<String>();
    private ArrayList<String> arry_days = new ArrayList<String>();

    private CalendarTextAdapter mYearAdapter;
    private CalendarTextAdapter mMonthAdapter;
    private CalendarTextAdapter mDaydapter;

    private int month;
    private int day;

    private int currentYear = getYear();
    private int currentMonth = 1;
    private int currentDay = 1;


    private boolean issetdata = false;

    private OnBirthListener onBirthListener;

    public UserBirthdaySelectorDialog(Context context) {
        super(context, R.style.Dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClosableSlidingLayout mDialogView = (ClosableSlidingLayout) View.inflate(getContext(), R.layout.dialog_user_birthday_selector, null);
        setContentDialogView(mDialogView);
        mDialogView.setFocusable(false);
        mDialogView.setEnabled(false);
        mDialogView.setFocusableInTouchMode(false);

        btnSure.setBackgroundResource(R.drawable.btn_green_selector);
        btnSure.setOnClickListener(this);

        if (!issetdata) {
            setDate(getYear(), 1, 1);
        }

        setupYearView();
        setupMonthView();
        setupDayView();

        wvYearView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                currentYear = Integer.parseInt(currentText);
                setDate(currentYear, currentMonth, currentDay);
                setupMonthView();
                setupDayView();
            }
        });


        wvMonthView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                currentMonth = Integer.valueOf(currentText);
                setDate(currentYear, currentMonth, currentDay);
                setupDayView();
            }
        });


        wvDayView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mDaydapter.getItemText(wheel.getCurrentItem());
                currentDay = Integer.valueOf(currentText);
            }
        });
    }

    private void setupYearView() {
        initYears();
        mYearAdapter = new CalendarTextAdapter(context, arry_years);
        wvYearView.setVisibleItems(5);
        wvYearView.setViewAdapter(mYearAdapter);
        wvYearView.setCurrentItem(setYear(currentYear));
    }

    private void setupMonthView() {
        initMonths(month);
        mMonthAdapter = new CalendarTextAdapter(context, arry_months);
        wvMonthView.setVisibleItems(5);
        wvMonthView.setViewAdapter(mMonthAdapter);
        wvMonthView.setCurrentItem(setMonth(currentMonth));
    }

    private void setupDayView() {

        initDays(day);
        mDaydapter = new CalendarTextAdapter(context, arry_days);
        wvDayView.setVisibleItems(5);
        wvDayView.setViewAdapter(mDaydapter);
        wvDayView.setCurrentItem(currentDay > arry_days.size() ? arry_days.size() - 1 : currentDay - 1);
    }

    @Override
    public AbsListView getAbsListView() {
        return null;
    }

    public void initYears() {
        for (int i = getYear(); i > 1950; i--) {
            arry_years.add(i + "");
        }
    }

    public void initMonths(int months) {
        arry_months.clear();
        for (int i = 1; i <= months; i++) {
            arry_months.add(i + "");
        }
    }

    public void initDays(int days) {
        arry_days.clear();
        for (int i = 1; i <= days; i++) {
            arry_days.add(i + "");
        }
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        public CalendarTextAdapter(Context context, ArrayList<String> list) {
            this(context, list, R.layout.item_birth_year, R.id.tempValue);
            this.list = list;
        }

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int itemResource, int itemTextResource) {
            super(context, itemResource, itemTextResource);
            this.list = list;
        }


        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    public void setBirthdayListener(OnBirthListener onBirthListener) {
        this.onBirthListener = onBirthListener;
    }

    @Override
    public void onClick(View v) {
        if (v == btnSure) {
            if (onBirthListener != null) {
                onBirthListener.onClick(currentYear, currentMonth, currentDay);
            }
        }
        dismiss();

    }

    public interface OnBirthListener {
        void onClick(int year, int month, int day);
    }


    public int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    public int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    public int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DATE);
    }


    /**
     * 设置年月日
     *
     * @param year
     * @param month
     * @param day
     */
    public void setDate(int year, int month, int day) {
        issetdata = true;
        this.currentYear = year;
        this.currentMonth = month;
        this.currentDay = day;
        if (year == getYear()) {
            this.month = getMonth();
        } else {
            this.month = 12;
        }
        calDays(year, month);
    }

    /**
     * 设置年份
     *
     * @param year
     */
    public int setYear(int year) {
        int yearIndex = 0;
        if (year != getYear()) {
            this.month = 12;
        } else {
            this.month = getMonth();
        }
        for (int i = getYear(); i > 1950; i--) {
            if (i == year) {
                return yearIndex;
            }
            yearIndex++;
        }
        return yearIndex;
    }

    /**
     * 设置月份
     *
     * @param month
     * @return
     */
    public int setMonth(int month) {
        int monthIndex = 0;
        calDays(currentYear, month);
        for (int i = 1; i < this.month; i++) {
            if (month == i) {
                return monthIndex;
            } else {
                monthIndex++;
            }
        }
        return monthIndex;
    }

    /**
     * 计算每月多少天
     *
     * @param month
     */
    public void calDays(int year, int month) {
        boolean leayyear = false;
        leayyear = year % 4 == 0 && year % 100 != 0;
        for (int i = 1; i <= 12; i++) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    this.day = 31;
                    break;
                case 2:
                    if (leayyear) {
                        this.day = 29;
                    } else {
                        this.day = 28;
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    this.day = 30;
                    break;
            }
        }
        if (year == getYear() && month == getMonth()) {
            this.day = getDay();
        }
    }
}