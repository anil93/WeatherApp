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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    java.util.List<java.util.List<List>> days;

    Drawable[] drawables;
    //Current Date
    TextView txtStatus, txtCurrentTemp, txtHumidity, txtPressure, txtWind, txtCurrentMain, txtCurrentDescription, txtLocation;
    //Other Date
    TextView txtFirstTempMax, txtFirstTempMin;
    TextView txtSecondTempMax, txtSecondTempMin;
    TextView txtThirdTempMax, txtThirdTempMin;
    TextView txtFourthTempMax, txtFourthTempMin;
    TextView txtUpdate;

    ImageView imgCurrent, imgFirst, imgSecond, imgThird, imgFourth;

    LinearLayout topLayout, middleLayout, bottomLayout;

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
        txtStatus = findViewById(R.id.txtStatus);
        txtCurrentTemp = findViewById(R.id.txtCurrentTemp);
        txtHumidity = findViewById(R.id.txtHumidity);
        txtPressure = findViewById(R.id.txtPressure);
        txtWind = findViewById(R.id.txtWind);
        txtCurrentMain = findViewById(R.id.txtCurrentMain);
        txtCurrentDescription = findViewById(R.id.txtCurrentDescription);
        txtLocation = findViewById(R.id.txtLocation);
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

        if (sp.contains("days")) {
            try {
                //location verisini alınması
                showLocation(sp.getString("location", null));
                //days listesindeki verilerin alınması
                Type type = new TypeToken<java.util.List<java.util.List<List>>>() {
                }.getType();
                days = new Gson().fromJson(sp.getString("days", null), type);
                //güncellenme tarihini alınması
                txtUpdate.setText(sp.getString("updateTime", ""));
                //kayıtlı imagelerin alınması
                drawables = new Drawable[5];
                for (int i = 0; i < drawables.length; i++) {
                    byte[] array = Base64.decode(sp.getString(Integer.toString(i), "null"), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
                    Drawable drawable = new BitmapDrawable(getBaseContext().getResources(), bitmap);
                    drawables[i] = drawable;
                }
                showInputValue(days, drawables, true);
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

    public void showStatusValue(String status) {
        txtStatus.setText(status);
    }

    public void showLocation(String location) {
        txtLocation.setText(location);
        edt.putString("location", location);
    }

    public void showInputValue(java.util.List<java.util.List<List>> days, Drawable[] d, boolean flag) {
        //current date
        txtCurrentMain.setText(days.get(0).get(0).getWeather().get(0).getMain());
        txtCurrentDescription.setText("(" + days.get(0).get(0).getWeather().get(0).getDescription() + ")");
        txtHumidity.setText(days.get(0).get(0).getMain().getHumidity() + " %");
        //currentTemp
        int tempMin = (int) (Math.round(days.get(0).get(0).getMain().getTemp()) - 273d);
        txtCurrentTemp.setText(tempMin + "°C");
        //get pressure
        double pressure = days.get(0).get(0).getMain().getPressure();
        txtPressure.setText(Double.toString(pressure) + " hPa");
        //get wind
        double windSpeed = days.get(0).get(0).getWind().getSpeed();
        double windDegree = days.get(0).get(0).getWind().getDeg();
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
        txtFirstTempMax.setText(utilities.compareMaxTemp(days.get(1)) + "°");
        txtFirstTempMin.setText(utilities.compareMinTemp(days.get(1)) + "°");
        //second date temperature
        txtSecondTempMax.setText(utilities.compareMaxTemp(days.get(2)) + "°");
        txtSecondTempMin.setText(utilities.compareMinTemp(days.get(2)) + "°");
        //third date temperature
        txtThirdTempMax.setText(utilities.compareMaxTemp(days.get(3)) + "°");
        txtThirdTempMin.setText(utilities.compareMinTemp(days.get(3)) + "°");
        //fourth date temperature
        txtFourthTempMax.setText(utilities.compareMaxTemp(days.get(4)) + "°");
        txtFourthTempMin.setText(utilities.compareMinTemp(days.get(4)) + "°");

        middleLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);

        //yeni gelen verilerin cihaza kaydının yapılması
        if (!flag) {
            topLayout.setVisibility(View.INVISIBLE);
            //days listesinin preferences a eklenmesi
            String data = new Gson().toJson(days);
            edt.putString("days", data);
            //Son güncelleme tarihinin preferences a eklenmesi
            String updateTime = utilities.getCurrentDate();
            edt.putString("updateTime", updateTime);
            txtUpdate.setText(updateTime);
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