package com.anilaynaci.weatherapp;

import android.util.Log;

import com.anilaynaci.weatherapp.entities.RootObject;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by anila on 29.10.2017.
 */

public class RootObjectRestClient {

    private String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";
    private String myAPPID = "b41774e3395246a8fcbc2eb880961c38";
    private RestTemplate restTemplate = new RestTemplate();

    public RootObject getRootObject(double lat, double lon){
        try{
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            String url = String.format(BASE_URL+"?lat="+lat+"&lon="+lon+"&appid="+myAPPID);
            RootObject rootObject = restTemplate.getForObject(url, RootObject.class);
            Double message = rootObject.getMessage();
            return rootObject;
        }catch (Exception e){
            Log.d("hata",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
