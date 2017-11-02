package com.anilaynaci.weatherapp;

import com.anilaynaci.weatherapp.entities.List;
import com.anilaynaci.weatherapp.entities.RootObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by anila on 1.11.2017.
 */

public class Utilities {

    public String DegreesToCardinalDetailed(double degrees) {
        degrees *= 10;
        String[] cardinals = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"};
        return cardinals[(int) Math.round((degrees % 3600) / 225)];
    }

    public java.util.List<String> getLastFiveDays() {

        java.util.List<String> days = new ArrayList<String>();

        Calendar c = Calendar.getInstance();

        //1.gün
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = df.format(c.getTime());
        days.add(currentDate);
        //2.gün
        c.add(Calendar.DATE, +1);
        String secondDate = df.format(c.getTime());
        days.add(secondDate);
        //3.gün
        c.add(Calendar.DATE, +1);
        String thirdDate = df.format(c.getTime());
        days.add(thirdDate);
        //4.gün
        c.add(Calendar.DATE, +1);
        String fourthDate = df.format(c.getTime());
        days.add(fourthDate);
        //5.gün
        c.add(Calendar.DATE, +1);
        String fifthDate = df.format(c.getTime());
        days.add(fifthDate);

        return days;
    }

    public int compareMaxTemp(java.util.List<List> firstDateMain) {

        Double temp = firstDateMain.get(0).getMain().getTemp_max();

        for (int i = 0; i < firstDateMain.size(); i++) {
            Double tempMax = firstDateMain.get(i).getMain().getTemp_max();
            if (tempMax > temp) {
                temp = tempMax;
            }
        }

        return (int) (Math.round(temp) - 273d);
    }

    public int compareMinTemp(java.util.List<List> firstDateMain) {

        Double temp = firstDateMain.get(0).getMain().getTemp_max();

        for (int i = 0; i < firstDateMain.size(); i++) {
            Double tempMin = firstDateMain.get(i).getMain().getTemp_min();
            if (tempMin < temp) {
                temp = tempMin;
            }
        }

        return (int) (Math.round(temp) - 273d);
    }

    public String getCurrentDate() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = date.format(c.getTime());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        String currentTime = time.format(c.getTime());
        String current = currentDate + " " + currentTime;

        return current;
    }

    public void getWeatherByDate(RootObject r, java.util.List<List>... params) {
        java.util.List<String> lastFiveDays = getLastFiveDays();

        for (int i = 0; i < r.getList().size(); i++) {
            List myList = r.getList().get(i);
            if (myList.getDt_txt().contains(lastFiveDays.get(0))) {
                params[0].add(myList);
            } else if (myList.getDt_txt().contains(lastFiveDays.get(1))) {
                params[1].add(myList);
            } else if (myList.getDt_txt().contains(lastFiveDays.get(2))) {
                params[2].add(myList);
            } else if (myList.getDt_txt().contains(lastFiveDays.get(3))) {
                params[3].add(myList);
            } else if (myList.getDt_txt().contains(lastFiveDays.get(4))) {
                params[4].add(myList);
            }
        }
    }
}