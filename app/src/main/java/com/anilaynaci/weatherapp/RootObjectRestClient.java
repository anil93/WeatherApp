package com.anilaynaci.weatherapp;

import android.graphics.drawable.Drawable;

import com.anilaynaci.weatherapp.entities.RootObject;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by anila on 29.10.2017.
 */

public class RootObjectRestClient {

    private String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";
    private String myAPPID = "b41774e3395246a8fcbc2eb880961c38";
    private RestTemplate restTemplate = new RestTemplate();

    public RootObject getRootObject(String lat, String lon) {
        try {
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            String url = BASE_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + myAPPID;
            RootObject rootObject = restTemplate.getForObject(url, RootObject.class);
            return rootObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Drawable getDrawable(String iconCode) {
        String IMG_URL = "http://openweathermap.org/img/w/";

        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();

        try {
            InputStream is = (InputStream) new URL(IMG_URL + iconCode).getContent();
            Drawable d = Drawable.createFromStream(is, randomUUIDString);
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] getImage(String code) {

        String IMG_URL = "http://openweathermap.org/img/w/";

        HttpURLConnection con = null ;
        InputStream is = null;
        try {
            con = (HttpURLConnection) ( new URL(IMG_URL + code)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while ( is.read(buffer) != -1)
                baos.write(buffer);

            return baos.toByteArray();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }
}