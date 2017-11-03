package com.anilaynaci.weatherapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.anilaynaci.weatherapp.entities.List;
import com.anilaynaci.weatherapp.entities.RootObject;

import java.util.ArrayList;

/**
 * Created by anila on 1.11.2017.
 */

class RetrieveRestClient extends AsyncTask<String, Void, java.util.List<java.util.List<List>>> {

    java.util.List<java.util.List<List>> days;

    java.util.List<List> currentDate = new ArrayList<List>();
    java.util.List<List> firstDate = new ArrayList<List>();
    java.util.List<List> secondDate = new ArrayList<List>();
    java.util.List<List> thirdDate = new ArrayList<List>();
    java.util.List<List> fourthDate = new ArrayList<List>();

    Drawable[] drawables;

    Utilities utilities;

    RootObject rootObject;

    Context context;

    public RetrieveRestClient(Context context) {
        this.context = context;
    }

    protected java.util.List<java.util.List<List>> doInBackground(String... params) {
        try {
            RootObjectRestClient restClient = new RootObjectRestClient();
            rootObject = restClient.getRootObject(params[0], params[1]);

            utilities = new Utilities();
            //veriler günlere göre ayrıldı
            utilities.getWeatherByDate(rootObject, currentDate, firstDate, secondDate, thirdDate, fourthDate);

            drawables = new Drawable[5];

            //buradaki parçalama işlemi her güne ait icon koduna ulaşılıp, servisten png dosyasını almak için yapılmıştır.
            //current date image
            String currentImageCode = currentDate.get(0).getWeather().get(0).getIcon() + ".png";
            Drawable currentImageIcon = restClient.getDrawable(currentImageCode);
            drawables[0] = currentImageIcon;
            //first date image
            String firstImageCode = utilities.mostRepeatedWord(firstDate) + ".png";
            Drawable firstImageIcon = restClient.getDrawable(firstImageCode);
            drawables[1] = firstImageIcon;
            //second date image
            String secondImageCode = utilities.mostRepeatedWord(secondDate) + ".png";
            Drawable secondImageIcon = restClient.getDrawable(secondImageCode);
            drawables[2] = secondImageIcon;
            //third date image
            String thirdImageCode = utilities.mostRepeatedWord(thirdDate) + ".png";
            Drawable thirdImageIcon = restClient.getDrawable(thirdImageCode);
            drawables[3] = thirdImageIcon;
            //fourth date image
            String fourthImageCode = utilities.mostRepeatedWord(fourthDate) + ".png";
            Drawable fourthImageIcon = restClient.getDrawable(fourthImageCode);
            drawables[4] = fourthImageIcon;

            days = new ArrayList<>();
            days.add(currentDate);
            days.add(firstDate);
            days.add(secondDate);
            days.add(thirdDate);
            days.add(fourthDate);

            return days;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(java.util.List<java.util.List<List>> days) {
        if (days != null) {
            MainActivity mA = (MainActivity) context;

            if (rootObject.getCity() != null) {
                String cityName = rootObject.getCity().getName();
                String country = rootObject.getCity().getCountry();
                String str = country + " - " + cityName;
                mA.showLocation(str);
            } else {
                String str = "Şehir bilgisi alınamadı.";
                mA.showLocation(str);
            }

            mA.showInputValue(days, drawables, false);
        }
    }
}