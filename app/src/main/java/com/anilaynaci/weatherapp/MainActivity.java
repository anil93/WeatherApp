package com.anilaynaci.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.anilaynaci.weatherapp.entities.List;
import com.anilaynaci.weatherapp.entities.RootObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    java.util.List<List> currentDate;
    java.util.List<List> firstDate;
    java.util.List<List> secondDate;
    java.util.List<List> thirdDate;
    java.util.List<List> fourthDate;

    LocationManager lm;

    String currentImageCode;
    String firstImageCode;
    String secondImageCode;
    String thirdImageCode;
    String fourthImageCode;

    Drawable currentImageIcon;
    Drawable firstImageIcon;
    Drawable secondImageIcon;
    Drawable thirdImageIcon;
    Drawable fourthImageIcon;

    String latitude;
    String longitude;

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

    ImageView imgCurrent;
    ImageView imgFirst;
    ImageView imgSecond;
    ImageView imgThird;
    ImageView imgFourth;

    LinearLayout topLayout;
    LinearLayout middleLayout;
    LinearLayout bottomLayout;

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

        imgCurrent = findViewById(R.id.imgCurrent);
        imgFirst = findViewById(R.id.imgFirst);
        imgSecond = findViewById(R.id.imgSecond);
        imgThird = findViewById(R.id.imgThird);
        imgFourth = findViewById(R.id.imgFourth);

        topLayout = findViewById(R.id.topLayout);
        middleLayout = findViewById(R.id.middleLayout);
        bottomLayout = findViewById(R.id.bottomLayout);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // show user that permission was denied. inactive the location based feature or force user to close the app
                Toast.makeText(this, "GPS izni verilmedi. Konum almak için kabul etmelisin.", Toast.LENGTH_LONG).show();
                //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "GPS izni verildi.", Toast.LENGTH_LONG).show();
                    //  get Location from your device by some method or code
                    getGeoCoord();
                } else {
                    //set to never ask again
                    Toast.makeText(this, "GPS izni verilmemiş. Tekrar sorma olarak belirtilmiş. Lütfen uygulama ayarlarını sıfırlayın.", Toast.LENGTH_LONG).show();
                    //do something here.
                }
            }
        }
    }

    public class GpsReceiver implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
                latitude = Double.toString(location.getLatitude());
                longitude = Double.toString(location.getLongitude());

                new RetrieveRestClient().execute(latitude, longitude);
            } else {
                Toast.makeText(MainActivity.this, "Konum bilgisi alınamıyor", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public boolean isGPSEnabled() {

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    public void getGeoCoord() {

        if (!isGPSEnabled()) {
            txtCity.setText("GPS kapalı! Konum verisi alınamıyor.");
        } else {
            txtCity.setText("Konum verisi alınıyor...");
        }

        GpsReceiver receiver = new GpsReceiver();
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Criteria crt = new Criteria();
        crt.setAccuracy(Criteria.ACCURACY_FINE);
        crt.setPowerRequirement(Criteria.POWER_MEDIUM);
        crt.setSpeedRequired(false);
        crt.setAltitudeRequired(false);
        String provider = lm.getBestProvider(crt, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lm.requestLocationUpdates(provider, 9999999L, 9999999.0F, receiver);
    }

    class RetrieveRestClient extends AsyncTask<String, Void, RootObject> {

        protected RootObject doInBackground(String... params) {
            try {
                RootObjectRestClient restClient = new RootObjectRestClient();
                RootObject rootObject = restClient.getRootObject(params[0], params[1]);

                //veriler günlere göre ayrıldı
                getWeatherByDate(rootObject);

                //bugünün iconu
                currentImageCode = currentDate.get(0).getWeather().get(0).getIcon() + ".png";
                currentImageIcon = restClient.getDrawable(currentImageCode);
                //birinci günün iconu
                firstImageCode = firstDate.get(4).getWeather().get(0).getIcon() + ".png";
                firstImageIcon = restClient.getDrawable(firstImageCode);
                //ikinci günün iconu
                secondImageCode = secondDate.get(4).getWeather().get(0).getIcon() + ".png";
                secondImageIcon = restClient.getDrawable(secondImageCode);
                //üçüncü günün iconu
                thirdImageCode = thirdDate.get(4).getWeather().get(0).getIcon() + ".png";
                thirdImageIcon = restClient.getDrawable(thirdImageCode);
                //dördüncü günün iconu
                fourthImageCode = fourthDate.get(4).getWeather().get(0).getIcon() + ".png";
                fourthImageIcon = restClient.getDrawable(fourthImageCode);

                return rootObject;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(RootObject r) {

            if (r != null) {
                Drawable[] d = new Drawable[5];
                d[0] = currentImageIcon;
                d[1] = firstImageIcon;
                d[2] = secondImageIcon;
                d[3] = thirdImageIcon;
                d[4] = fourthImageIcon;

                showInputValue(r, d);
            }
        }
    }

    private void showInputValue(RootObject r, Drawable[] d) {

        if (r.getCity() != null) {
            String cityName = r.getCity().getName();
            String country = r.getCity().getCountry();
            txtCity.setText(country + " - " + cityName);
        } else {
            txtCity.setText("Şehir bilgisi alınamadı.");
        }

        imgCurrent.setImageDrawable(d[0]);
        int tempMin = (int) (Math.round(currentDate.get(0).getMain().getTemp()) - 273d);
        txtCurrentTemp.setText(tempMin + "°C");
        txtCurrentMain.setText(currentDate.get(0).getWeather().get(0).getMain());
        txtCurrentDescription.setText(currentDate.get(0).getWeather().get(0).getDescription());
        txtHumidity.setText(currentDate.get(0).getMain().getHumidity() + " %");

        double pressure = currentDate.get(0).getMain().getPressure();

        txtPressure.setText(Double.toString(pressure) + " hPa");

        double windSpeed = currentDate.get(0).getWind().getSpeed();
        double windDegree = currentDate.get(0).getWind().getDeg();
        String wind = windSpeed + "m/s " + DegreesToCardinalDetailed(windDegree);
        txtWind.setText(wind);

        imgFirst.setImageDrawable(d[1]);
        imgSecond.setImageDrawable(d[2]);
        imgThird.setImageDrawable(d[3]);
        imgFourth.setImageDrawable(d[4]);

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
        String[] cardinals = { "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N" };
        return cardinals[(int)Math.round(((double)degrees % 3600) / 225)];
    }

    /*public String degToCompass(double degree) {
        int val = (int)Math.floor((degree / 22.5) + 0.5);
        String[] arr = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        return arr[(val % 16)];
    }*/
}
