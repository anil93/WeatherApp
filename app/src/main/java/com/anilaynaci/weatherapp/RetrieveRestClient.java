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

class RetrieveRestClient extends AsyncTask<String, Void, RootObject> {

    java.util.List<List> currentDate = new ArrayList<List>();
    java.util.List<List> firstDate = new ArrayList<List>();
    java.util.List<List> secondDate = new ArrayList<List>();
    java.util.List<List> thirdDate = new ArrayList<List>();
    java.util.List<List> fourthDate = new ArrayList<List>();

    Drawable[] drawables;

    Utilities utilities;

    Context context;

    public RetrieveRestClient(Context context) {
        this.context = context;
    }

    protected RootObject doInBackground(String... params) {
        try {
            RootObjectRestClient restClient = new RootObjectRestClient();
            RootObject rootObject = restClient.getRootObject(params[0], params[1]);

            utilities = new Utilities();
            //veriler günlere göre ayrıldı
            utilities.getWeatherByDate(rootObject, currentDate,firstDate,secondDate,thirdDate,fourthDate);

            drawables = new Drawable[5];

            //buradaki parçalama işlemi her güne ait icon koduna ulaşılıp, servisten png dosyasını almak için yapılmıştır.
            //current date image
            String currentImageCode = currentDate.get(0).getWeather().get(0).getIcon() + ".png";
            Drawable currentImageIcon = restClient.getDrawable(currentImageCode);
            drawables[0] = currentImageIcon;
            //first date image
            String firstImageCode = firstDate.get(4).getWeather().get(0).getIcon() + ".png";
            Drawable firstImageIcon = restClient.getDrawable(firstImageCode);
            drawables[1] = firstImageIcon;
            //second date image
            String secondImageCode = secondDate.get(4).getWeather().get(0).getIcon() + ".png";
            Drawable secondImageIcon = restClient.getDrawable(secondImageCode);
            drawables[2] = secondImageIcon;
            //third date image
            String thirdImageCode = thirdDate.get(4).getWeather().get(0).getIcon() + ".png";
            Drawable thirdImageIcon = restClient.getDrawable(thirdImageCode);
            drawables[3] = thirdImageIcon;
            //fourth date image
            String fourthImageCode = fourthDate.get(4).getWeather().get(0).getIcon() + ".png";
            Drawable fourthImageIcon = restClient.getDrawable(fourthImageCode);
            drawables[4] = fourthImageIcon;

            return rootObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(RootObject r) {
        if (r != null) {
            MainActivity mA = (MainActivity) context;
            mA.showInputValue(r,drawables,false);
        }
    }
}