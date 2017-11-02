package com.anilaynaci.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.anilaynaci.weatherapp.entities.List;
import com.anilaynaci.weatherapp.entities.RootObject;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    java.util.List<List> currentDate = new ArrayList<List>();
    java.util.List<List> firstDate = new ArrayList<List>();
    java.util.List<List> secondDate = new ArrayList<List>();
    java.util.List<List> thirdDate = new ArrayList<List>();
    java.util.List<List> fourthDate = new ArrayList<List>();

    Drawable[] drawables;
    //Current Date
    TextView txtCity,txtCurrentTemp,txtHumidity,txtPressure,txtWind,txtCurrentMain,txtCurrentDescription;
    //Other Date
    TextView txtFirstTempMax,txtFirstTempMin;
    TextView txtSecondTempMax,txtSecondTempMin;
    TextView txtThirdTempMax,txtThirdTempMin;
    TextView txtFourthTempMax,txtFourthTempMin;
    TextView txtUpdate;

    ImageView imgCurrent,imgFirst,imgSecond,imgThird,imgFourth;

    LinearLayout topLayout,middleLayout,bottomLayout;

    SharedPreferences sp;
    SharedPreferences.Editor edt;

    Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        TabHost tabHost = findViewById(R.id.tabHost);
        tabHost.setup();
        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("tab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Bugün");
        tabHost.addTab(spec);
        //Tab 2
        spec = tabHost.newTabSpec("tab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("4 Gün");
        tabHost.addTab(spec);
        //current date
        txtCity = findViewById(R.id.txtCity);
        txtCurrentTemp = findViewById(R.id.txtCurrentTemp);
        txtHumidity = findViewById(R.id.txtHumidity);
        txtPressure = findViewById(R.id.txtPressure);
        txtWind = findViewById(R.id.txtWind);
        txtCurrentMain = findViewById(R.id.txtCurrentMain);
        txtCurrentDescription = findViewById(R.id.txtCurrentDescription);
        //other date
        txtFirstTempMax = findViewById(R.id.txtFirstTempMax);
        txtFirstTempMin = findViewById(R.id.txtFirstTempMin);
        txtSecondTempMax = findViewById(R.id.txtSecondTempMax);
        txtSecondTempMin = findViewById(R.id.txtSecondTempMin);
        txtThirdTempMax = findViewById(R.id.txtThirdTempMax);
        txtThirdTempMin = findViewById(R.id.txtThirdTempMin);
        txtFourthTempMax = findViewById(R.id.txtFourthTempMax);
        txtFourthTempMin = findViewById(R.id.txtFourthTempMin);

        txtUpdate = findViewById(R.id.txtUpdate);

        imgCurrent = findViewById(R.id.imgCurrent);
        imgFirst = findViewById(R.id.imgFirst);
        imgSecond = findViewById(R.id.imgSecond);
        imgThird = findViewById(R.id.imgThird);
        imgFourth = findViewById(R.id.imgFourth);

        topLayout = findViewById(R.id.topLayout);
        middleLayout = findViewById(R.id.middleLayout);
        bottomLayout = findViewById(R.id.bottomLayout);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        utilities = new Utilities();

        sp = this.getPreferences(Context.MODE_PRIVATE);
        edt = sp.edit();

        if (sp.contains("rootObject")) {
            try {
                Gson gson = new Gson();
                //kayıtlı rootObject alınıyor
                String json = sp.getString("rootObject", "");
                RootObject r = gson.fromJson(json, RootObject.class);

                txtUpdate.setText(sp.getString("updateTime", ""));
                //kayıtlı imageler alınıyor
                drawables = new Drawable[5];
                for (int i = 0; i < drawables.length; i++) {
                    byte[] array = Base64.decode(sp.getString(Integer.toString(i), "null"), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
                    Drawable drawable = new BitmapDrawable(getBaseContext().getResources(), bitmap);
                    drawables[i] = drawable;
                }
                showInputValue(r, drawables, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(this, "GPS izni verilmedi. Konum almak için kabul etmelisin.", Toast.LENGTH_LONG).show();
            } else {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "GPS izni verildi.", Toast.LENGTH_LONG).show();
                    GPSReceiver gpsReceiver = new GPSReceiver(this);
                    gpsReceiver.getGeoCoord();
                } else {
                    Toast.makeText(this, "GPS izni verilmemiş. Tekrar sorma olarak belirtilmiş. Lütfen uygulama ayarlarını sıfırlayın.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void showStatusValue(String status){
        txtCity.setText(status);
    }

    public void showInputValue(RootObject r, Drawable[] d, boolean flag) {
        utilities.getWeatherByDate(r, currentDate,firstDate,secondDate,thirdDate,fourthDate);

        if (r.getCity() != null) {
            String cityName = r.getCity().getName();
            String country = r.getCity().getCountry();
            txtCity.setText(country + " - " + cityName);
        } else {
            txtCity.setText("Şehir bilgisi alınamadı.");
        }
        //current date
        txtCurrentMain.setText(currentDate.get(0).getWeather().get(0).getMain());
        txtCurrentDescription.setText("(" + currentDate.get(0).getWeather().get(0).getDescription() + ")");
        txtHumidity.setText(currentDate.get(0).getMain().getHumidity() + " %");
        //currentTemp
        int tempMin = (int) (Math.round(currentDate.get(0).getMain().getTemp()) - 273d);
        txtCurrentTemp.setText(tempMin + "°C");
        //get pressure
        double pressure = currentDate.get(0).getMain().getPressure();
        txtPressure.setText(Double.toString(pressure) + " hPa");
        //get wind
        double windSpeed = currentDate.get(0).getWind().getSpeed();
        double windDegree = currentDate.get(0).getWind().getDeg();
        String wind = windSpeed + "m/s " + utilities.DegreesToCardinalDetailed(windDegree);
        txtWind.setText(wind);
        //get images
        if (d != null) {
            imgCurrent.setImageDrawable(d[0]);
            imgFirst.setImageDrawable(d[1]);
            imgSecond.setImageDrawable(d[2]);
            imgThird.setImageDrawable(d[3]);
            imgFourth.setImageDrawable(d[4]);
        }
        //first date temperature
        txtFirstTempMax.setText(utilities.compareMaxTemp(firstDate) + "°");
        txtFirstTempMin.setText(utilities.compareMinTemp(firstDate) + "°");
        //second date temperature
        txtSecondTempMax.setText(utilities.compareMaxTemp(secondDate) + "°");
        txtSecondTempMin.setText(utilities.compareMinTemp(secondDate) + "°");
        //third date temperature
        txtThirdTempMax.setText(utilities.compareMaxTemp(thirdDate) + "°");
        txtThirdTempMin.setText(utilities.compareMinTemp(thirdDate) + "°");
        //fourth date temperature
        txtFourthTempMax.setText(utilities.compareMaxTemp(fourthDate) + "°");
        txtFourthTempMin.setText(utilities.compareMinTemp(fourthDate) + "°");

        middleLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);

        //yeni gelen verilerin cihaza kaydının yapılması
        if (!flag) {
            //Son güncelleme tarihi
            String updateTime = utilities.getCurrentDate();
            txtUpdate.setText(updateTime);
            try {
                Gson gson = new Gson();
                String json = gson.toJson(r);
                edt.putString("rootObject", json);
                edt.putString("updateTime", updateTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //imagelerin preferences'a eklenmesi
            for (int i = 0; i < d.length; i++) {
                Bitmap bitmap = ((BitmapDrawable) d[i]).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                String saveThis = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
                edt.putString(Integer.toString(i), saveThis);
            }
            edt.commit();
        }
    }
}