package com.anilaynaci.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.anilaynaci.weatherapp.entities.RootObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RetrieveRestClient().execute();
    }

    class RetrieveRestClient extends AsyncTask<Void, Void, RootObject> {

        private Exception exception;

        protected RootObject doInBackground(Void... params) {
            try {
                RootObjectRestClient restClient = new RootObjectRestClient();
                RootObject rootObject = restClient.getRootObject(16,17);
                return rootObject;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(RootObject r) {

            TextView txt1 = (TextView) findViewById(R.id.txt1);
            TextView txt2 = (TextView) findViewById(R.id.txt2);
            TextView txt3 = (TextView) findViewById(R.id.txt3);
            TextView txt4 = (TextView) findViewById(R.id.txt4);
            TextView txt5 = (TextView) findViewById(R.id.txt5);

            try{
                int cnt = r.getCnt();
                txt1.setText(Integer.toString(cnt));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public List<String> getLastFiveDays() {

        List<String> days = new ArrayList<String>();

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


}