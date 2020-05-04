package org.techtown.notepad.classes_for_methods;

import android.widget.TextView;

import org.techtown.notepad.list.ListItem;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TImeUtility {
    public static void setTime(TextView time, ListItem item){
        // 마지막 수정시간 계산
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentDate = format.format(date);
        String then_date = item.getTime();
        int currentYear = Integer.parseInt(currentDate.substring(0, 4));
        int thenYear = Integer.parseInt(then_date.substring(0, 4));
        int currentMonth = Integer.parseInt(currentDate.substring(4, 6));
        int thenMonth = Integer.parseInt(then_date.substring(4, 6));
        int currentDay = Integer.parseInt(currentDate.substring(6, 8));
        int thenDay = Integer.parseInt(then_date.substring(6, 8));
        int currentHour = Integer.parseInt(currentDate.substring(8, 10));
        int thenHour = Integer.parseInt(then_date.substring(8, 10));
        int currentMinute = Integer.parseInt(currentDate.substring(10, 12));
        int thenMinute = Integer.parseInt(then_date.substring(10, 12));
        int currentSecond = Integer.parseInt(currentDate.substring(12, 14));
        int thenSecond = Integer.parseInt(then_date.substring(12, 14));

        if ((currentYear-thenYear>=2) || (currentYear - thenYear == 1
                && currentMonth - thenMonth >= 0)) {
            time.setText(currentYear-thenYear+"년 전");
        } else if (currentYear - thenYear == 1 && currentMonth - thenMonth < 0) {
            time.setText(currentMonth + 12 - thenMonth+"달 전");
        } else if ((currentMonth-thenMonth>=2) || (currentMonth - thenMonth == 1
                && currentDay - thenDay >= 0)) {
            time.setText(currentMonth-thenMonth+"달 전");
        } else if (currentMonth - thenMonth == 1 && currentDay - thenDay < 0) {
            time.setText(currentDay + 30 - thenDay+"일 전");
        } else if ((currentDay-thenDay>=2) || (currentDay - thenDay == 1
                && currentHour - thenHour >= 0)) {
            time.setText(currentDay-thenDay+"일 전");
        } else if (currentDay - thenDay == 1 && currentHour - thenHour < 0) {
            time.setText(currentHour + 24 - thenHour+"시간 전");
        } else if ((currentHour-thenHour>=2) || (currentHour - thenHour == 1
                && currentMinute - thenMinute >= 0)) {
            time.setText(currentHour-thenHour+"시간 전");
        } else if (currentHour - thenHour == 1 && currentMinute - thenMinute < 0) {
            time.setText(currentMinute + 60 - thenMinute+"분 전");
        } else if ((currentMinute-thenMinute>=2) || (currentMinute - thenMinute == 1
                && currentSecond - thenSecond >= 0)) {
            time.setText(currentMinute-thenMinute+"분 전");
        } else {
            time.setText("방금 전");
        }
    }
}
