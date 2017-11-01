package com.anilaynaci.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    java.util.List<List> currentDate;
    java.util.List<List> firstDate;
    java.util.List<List> secondDate;
    java.util.List<List> thirdDate;
    java.util.List<List> fourthDate;

    Drawable[] drawables;

    TextView txtCity;
    TextView txtCurrentTemp;
    TextView txtHumidity;
    TextView txtPressure;
    TextView txtWind;
    TextView txtFirstTempMax;
    TextView txtFirstTempMin;
    TextView txtSecondTempMax;
    TextView txtSecondTempMin;
    TextView txtThirdTempMax;
    TextView txtThirdTempMin;
    TextView txtFourthTempMax;
    TextView txtFourthTempMin;
    TextView txtCurrentMain;
    TextView txtCurrentDescription;
    TextView txtUpdate;

    ImageView imgCurrent;
    ImageView imgFirst;
    ImageView imgSecond;
    ImageView imgThird;
    ImageView imgFourth;

    LinearLayout topLayout;
    LinearLayout middleLayout;
    LinearLayout bottomLayout;

    SharedPreferences sp;
    SharedPreferences.Editor edt;

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

        txtCity = findViewById(R.id.txtCity);
        txtCurrentTemp = findViewById(R.id.txtCurrentTemp);
        txtHumidity = findViewById(R.id.txtHumidity);
        txtPressure = findViewById(R.id.txtPressure);
        txtWind = findViewById(R.id.txtWind);

        txtFirstTempMax = findViewById(R.id.txtFirstTempMax);
        txtFirstTempMin = findViewById(R.id.txtFirstTempMin);
        txtSecondTempMax = findViewById(R.id.txtSecondTempMax);
        txtSecondTempMin = findViewById(R.id.txtSecondTempMin);
        txtThirdTempMax = findViewById(R.id.txtThirdTempMax);
        txtThirdTempMin = findViewById(R.id.txtThirdTempMin);
        txtFourthTempMax = findViewById(R.id.txtFourthTempMax);
        txtFourthTempMin = findViewById(R.id.txtFourthTempMin);
        txtCurrentMain = findViewById(R.id.txtCurrentMain);
        txtCurrentDescription = findViewById(R.id.txtCurrentDescription);
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

        sp = this.getPreferences(Context.MODE_PRIVATE);
        edt = sp.edit();

        if (sp.contains("rootObject")) {
            try {
                Gson gson = new Gson();
                String json = sp.getString("rootObject", "");
                RootObject r = gson.fromJson(json, RootObject.class);
                txtUpdate.setText(sp.getString("updateTime", ""));
                drawables = new Drawable[5];
                for(int i = 0; i < drawables.length ;i++){
                    byte[] array = Base64.decode(sp.getString(Integer.toString(i),"null"), Base64.DEFAULT);
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
                    getGeoCoord();
                } else {
                    Toast.makeText(this, "GPS izni verilmemiş. Tekrar sorma olarak belirtilmiş. Lütfen uygulama ayarlarını sıfırlayın.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public class GpsReceiver implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
                String latitude = Double.toString(location.getLatitude());
                String longitude = Double.toString(location.getLongitude());
                txtCity.setText(latitude + " - " + longitude);
                new RetrieveRestClient().execute(latitude, longitude);
            } else {
                Toast.makeText(MainActivity.this, "Konum bilgisi alınamıyor.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Toast.makeText(MainActivity.this, "Status changed", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(MainActivity.this, "Uygulamaya yönlendiriliyorsunuz...", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
            }, 3000);
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Toast.makeText(MainActivity.this, "Disabled", Toast.LENGTH_LONG).show();
        }
    }

    public void getGeoCoord() {

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        GpsReceiver receiver = new GpsReceiver();

        //3 saat
        Long minTime = 3 * (60 * 60) * 1000L;
        //5 km
        float minDistance = 5000.f;

        boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isNetworkEnabled) {

            txtCity.setText("GPS Kapalı! Ayarlara yönlendiriliyorsunuz...");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    //Toast.makeText(MainActivity.this, "hello world", Toast.LENGTH_SHORT).show();

                    Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(viewIntent);
                }
            }, 3000);
        } else {
            txtCity.setText("Veriler alınıyor...");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, receiver);
    }

    class RetrieveRestClient extends AsyncTask<String, Void, RootObject> {

        protected RootObject doInBackground(String... params) {
            try {
                RootObjectRestClient restClient = new RootObjectRestClient();
                RootObject rootObject = restClient.getRootObject(params[0], params[1]);

                //veriler günlere göre ayrıldı
                getWeatherByDate(rootObject);

                drawables = new Drawable[5];

                //bugünün iconu
                String currentImageCode = currentDate.get(0).getWeather().get(0).getIcon() + ".png";
                Drawable currentImageIcon = restClient.getDrawable(currentImageCode);
                /*byte[] arr = restClient.getImage(currentImageCode);
                Bitmap bmp = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                Drawable currentImageIcon = new BitmapDrawable(getBaseContext().getResources(), bmp);*/
                drawables[0] = currentImageIcon;

                //birinci günün iconu
                String firstImageCode = firstDate.get(4).getWeather().get(0).getIcon() + ".png";
                Drawable firstImageIcon = restClient.getDrawable(firstImageCode);
                drawables[1] = firstImageIcon;

                //ikinci günün iconu
                String secondImageCode = secondDate.get(4).getWeather().get(0).getIcon() + ".png";
                Drawable secondImageIcon = restClient.getDrawable(secondImageCode);
                drawables[2] = secondImageIcon;

                //üçüncü günün iconu
                String thirdImageCode = thirdDate.get(4).getWeather().get(0).getIcon() + ".png";
                Drawable thirdImageIcon = restClient.getDrawable(thirdImageCode);
                drawables[3] = thirdImageIcon;

                //dördüncü günün iconu
                String fourthImageCode = fourthDate.get(4).getWeather().get(0).getIcon() + ".png";
                Drawable fourthImageIcon = restClient.getDrawable(fourthImageCode);
                drawables[4] = fourthImageIcon;

                return rootObject;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(RootObject r) {

            if (r != null) {
                showInputValue(r, drawables, false);
            }

            //Son güncelleme tarihi
            Calendar c = Calendar.getInstance();
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = date.format(c.getTime());
            SimpleDateFormat time = new SimpleDateFormat("HH:mm");
            String currentTime = time.format(c.getTime());
            String updateTime = currentDate + " " + currentTime;

            try {
                Gson gson = new Gson();
                //edt = sp.edit();
                String json = gson.toJson(r);
                edt.putString("rootObject", json);
                edt.putString("updateTime", updateTime);
                edt.commit();
                txtUpdate.setText(sp.getString("updateTime", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showInputValue(RootObject r, Drawable[] d, boolean flag) {

        if (r.getCity() != null) {
            String cityName = r.getCity().getName();
            String country = r.getCity().getCountry();
            txtCity.setText(country + " - " + cityName);
        } else {
            txtCity.setText("Şehir bilgisi alınamadı.");
        }

        if (flag) {
            getWeatherByDate(r);
        }

        int tempMin = (int) (Math.round(currentDate.get(0).getMain().getTemp()) - 273d);
        txtCurrentTemp.setText(tempMin + "°C");
        txtCurrentMain.setText(currentDate.get(0).getWeather().get(0).getMain());
        txtCurrentDescription.setText("(" + currentDate.get(0).getWeather().get(0).getDescription() + ")");
        txtHumidity.setText(currentDate.get(0).getMain().getHumidity() + " %");

        double pressure = currentDate.get(0).getMain().getPressure();

        txtPressure.setText(Double.toString(pressure) + " hPa");

        double windSpeed = currentDate.get(0).getWind().getSpeed();
        double windDegree = currentDate.get(0).getWind().getDeg();
        String wind = windSpeed + "m/s " + DegreesToCardinalDetailed(windDegree);
        txtWind.setText(wind);

        if (d != null) {
            imgCurrent.setImageDrawable(d[0]);
            imgFirst.setImageDrawable(d[1]);
            imgSecond.setImageDrawable(d[2]);
            imgThird.setImageDrawable(d[3]);
            imgFourth.setImageDrawable(d[4]);
        }

        txtFirstTempMax.setText(compareMaxTemp(firstDate) + "°");
        txtFirstTempMin.setText(compareMinTemp(firstDate) + "°");

        txtSecondTempMax.setText(compareMaxTemp(secondDate) + "°");
        txtSecondTempMin.setText(compareMinTemp(secondDate) + "°");

        txtThirdTempMax.setText(compareMaxTemp(thirdDate) + "°");
        txtThirdTempMin.setText(compareMinTemp(thirdDate) + "°");

        txtFourthTempMax.setText(compareMaxTemp(fourthDate) + "°");
        txtFourthTempMin.setText(compareMinTemp(fourthDate) + "°");

        middleLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);

        if (!flag) {
            for (int i = 0; i < d.length; i++) {
                Bitmap bitmap = ((BitmapDrawable) d[i]).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                String saveThis = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
                //edt = sp.edit();
                edt.putString(Integer.toString(i), saveThis);
            }
            //edt.commit();
        }
    }

    private int compareMaxTemp(java.util.List<List> firstDateMain) {

        Double temp = firstDateMain.get(0).getMain().getTemp_max();

        for (int i = 0; i < firstDateMain.size(); i++) {
            Double tempMax = firstDateMain.get(i).getMain().getTemp_max();
            if (tempMax > temp) {
                temp = tempMax;
            }
        }

        return (int) (Math.round(temp) - 273d);
    }

    private int compareMinTemp(java.util.List<List> firstDateMain) {

        Double temp = firstDateMain.get(0).getMain().getTemp_max();

        for (int i = 0; i < firstDateMain.size(); i++) {
            Double tempMin = firstDateMain.get(i).getMain().getTemp_min();
            if (tempMin < temp) {
                temp = tempMin;
            }
        }

        return (int) (Math.round(temp) - 273d);
    }

    public void getWeatherByDate(RootObject r) {

        java.util.List<String> lastFiveDays = getLastFiveDays();

        currentDate = new ArrayList<List>();
        firstDate = new ArrayList<List>();
        secondDate = new ArrayList<List>();
        thirdDate = new ArrayList<List>();
        fourthDate = new ArrayList<List>();

        for (int i = 0; i < r.getList().size(); i++) {
            List myList = r.getList().get(i);
            if (myList.getDt_txt().contains(lastFiveDays.get(0))) {
                currentDate.add(myList);
            } else if (myList.getDt_txt().contains(lastFiveDays.get(1))) {
                firstDate.add(myList);
            } else if (myList.getDt_txt().contains(lastFiveDays.get(2))) {
                secondDate.add(myList);
            } else if (myList.getDt_txt().contains(lastFiveDays.get(3))) {
                thirdDate.add(myList);
            } else if (myList.getDt_txt().contains(lastFiveDays.get(4))) {
                fourthDate.add(myList);
            }
        }
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

    public String DegreesToCardinalDetailed(double degrees) {
        degrees *= 10;
        String[] cardinals = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"};
        return cardinals[(int) Math.round((degrees % 3600) / 225)];
    }
}