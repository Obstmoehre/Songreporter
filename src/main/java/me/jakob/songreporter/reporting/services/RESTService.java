package me.jakob.songreporter.reporting.services;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.jakob.songreporter.reporting.objects.Categories;
import me.jakob.songreporter.reporting.objects.ReportPayload;
import me.jakob.songreporter.reporting.objects.Song;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RESTService {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getDeclaredClass().getName().equals("boolean") &&
                    fieldAttributes.getName().equals("publicDomain");
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    }).create();
    private final HashMap<String, String> cookies;


    public RESTService(HashMap<String, String> cookies) {
        this.cookies = cookies;
    }

    public Song fetchSongdetails(String ccliSongNumber) {
        String json;

        ArrayList<String> cookieNames = new ArrayList<>();
        cookieNames.add("ARRAffinity");
        cookieNames.add("ARRAffinitySameSite");
        cookieNames.add("CCLI_AUTH");
        cookieNames.add("CCLI_JWT_AUTH");
        cookieNames.add(".AspNetCore.Session");
        cookieNames.add(".AspNetCore.Antiforgery.w5W7x28NAIs");

        Request request = new Request.Builder()
                .url("https://reporting.ccli.com/api/detail/song/" + ccliSongNumber)
                .addHeader("Accept", "application/json;charset=utf-8")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Cookie", buildCookieString(cookieNames))
                .addHeader("Refer", "https://reporting.ccli.com/search?s="
                        + ccliSongNumber + "&page=1&category=all")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36")
                .build();

        try (Response response = this.client.newCall(request).execute()) {
            if (response.body() != null) {
                json = response.body().string();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return this.gson.fromJson(json, Song.class);
    }

    public HashMap<Song, Integer> reportSongs(ArrayList<Song> songs, Categories categories) {
        HashMap<Song, Integer> responseCodes = new HashMap<>();
        String requestVerificationToken = getRequestVerificationToken(cookies);
        if (requestVerificationToken == null || requestVerificationToken.equals("")) {
            for (Song song : songs) {
                responseCodes.put(song, -3);
            }
            return responseCodes;
        }

        ArrayList<String> cookieNames = new ArrayList<>();
        cookieNames.add("ARRAffinity");
        cookieNames.add("ARRAffinitySameSite");
        cookieNames.add("CCLI_AUTH");
        cookieNames.add("CCLI_JWT_AUTH");
        cookieNames.add(".AspNetCore.Antiforgery.w5W7x28NAIs");
        cookieNames.add(".AspNetCore.Session");

        for (Song song : songs) {
            if (!song.isPublicDomain() && !(song.getCcliSongNo() == null)) {
                String reportPayload = this.gson.toJson(new ReportPayload(song, categories)).replace("\"0\"", "0");
                Request request = new Request.Builder()
                        .url("https://reporting.ccli.com/api/report")
                        .addHeader("Accept", "application/json, text/plain, */*")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("Cookie", buildCookieString(cookieNames))
                        .addHeader("Host", "reporting.ccli.com")
                        .addHeader("Origin", "https://reporting.ccli.com")
                        .addHeader("Pragma", "no-cache")
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("client-locale", "de-DE")
                        .addHeader("Sec-Fetch-Site", "same-origin")
                        .addHeader("Sec-Fetch-Mode", "cors")
                        .addHeader("Sec-Fetch-Dest", "empty")
                        .addHeader("dnt", "1")
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36")
                        .addHeader("RequestVerificationToken", requestVerificationToken) //verification token still missing here
                        .post(RequestBody.create(MediaType.parse("application/json"), reportPayload))
                        .build();

                try (Response response = this.client.newCall(request).execute()) {
                    if (response.body() != null) {
                        putCookies(response.headers("Set-Cookie"));
                        responseCodes.put(song, response.code());
                    } else {
                        responseCodes.put(song, -2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    responseCodes.put(song, -1);
                }
            } else {
                responseCodes.put(song, 0);
            }
        }

        return responseCodes;
    }

    private String getRequestVerificationToken(HashMap<String, String> cookies) {
        String requestVerificationToken;

        ArrayList<String> cookieNames = new ArrayList<>();
        cookieNames.add("ARRAffinity");
        cookieNames.add("ARRAffinitySameSite");
        cookieNames.add("CCLI_AUTH");
        cookieNames.add("CCLI_JWT_AUTH");
        cookieNames.add(".AspNetCore.Session");

        Request request = new Request.Builder()
                .url("https://reporting.ccli.com/api/antiForgery")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", buildCookieString(cookieNames))
                .addHeader("Host", "reporting.ccli.com")
                .addHeader("Origin", "https://reporting.ccli.com")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache")
                .build();

        try (Response response = this.client.newCall(request).execute()) {
            if (response.body() != null) {
                putCookies(response.headers("Set-Cookie"));
                requestVerificationToken = response.body().string().replace("\"", "");
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return requestVerificationToken;
    }

    private void putCookies(List<String> headerValues) {
        for (String headerValue : headerValues) {
            this.cookies.put(headerValue.substring(0, headerValue.indexOf("=")),
                    headerValue.substring(headerValue.indexOf("=") + 1, headerValue.indexOf(";")));
        }
    }

    private String buildCookieString(ArrayList<String> cookieNames) {
        StringBuilder cookieStringBuilder = new StringBuilder();
        for (String cookieName : cookieNames) {
            if (!cookieNames.get(0).equals(cookieName)) {
                cookieStringBuilder.append("; ");
            }
            cookieStringBuilder.append(cookieName).append("=").append(this.cookies.get(cookieName));
        }

        return cookieStringBuilder.toString();
    }
}
