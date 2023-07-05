package org.example;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Downloader {

    public byte[] download(String sourceurl,String bottoken){
        try {
            String bot_url="https://api.telegram.org/file/bot"+bottoken+"/";
            System.out.println("downloader");
            //creo ogg. di tipo URL
            URL url = new URL(sourceurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Leggi la risposta JSON e ottieni il percorso di download
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();
            JSONObject jsonResponse = new JSONObject(response);
            String filePath = jsonResponse.getJSONObject("result").getString("file_path");
            System.out.println(filePath);
            String downloadUrl = bot_url+ filePath;
            System.out.println("download "+downloadUrl);

            // Scarica l'immagine utilizzando l'URL di download
            URL imageUrl = new URL(downloadUrl);
            InputStream inputStream = imageUrl.openStream();
            byte[] imageData = inputStream.readAllBytes();
            System.out.println(imageData);
            return imageData;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
