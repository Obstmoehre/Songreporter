package me.jakob.songreporter.reporting.services;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.jakob.songreporter.REST.ReportPayload;
import me.jakob.songreporter.REST.Songdetails;
import okhttp3.*;
import org.openqa.selenium.Cookie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

    public Songdetails fetchSongdetails(String ccliSongNumber, Set<Cookie> cookieSet) {
        String json = null;

        ArrayList<String> cookieNames = new ArrayList<>();
        cookieNames.add("ARRAffinity");
        cookieNames.add("ARRAffinitySameSite");
        cookieNames.add("CCLI_AUTH");
        cookieNames.add("CCLI_JWT_AUTH");
        cookieNames.add(".AspNetCore.Antiforgery.w5W7x28NAIs");
        cookieNames.add(".AspNetCore.Session");

        Request request = new Request.Builder()
                .url("https://reporting.ccli.com/api/detail/song/" + ccliSongNumber)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Cookie", buildCookieString(cookieNames, this.cookies))
                .build();

        Response response = performRequest(request);
        try {
            if (response != null && response.body() != null) {
                json = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this.gson.fromJson(json, Songdetails.class);
    }

    public int reportSongs(Songdetails[] songs) {
        String requestVerificationToken = getRequestVerificationToken(cookies);
        String reportPayload = this.gson.toJson(new ReportPayload(songs));

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("reporting.ccli.com")
                .port(443)
                .addPathSegment("api")
                .addPathSegment("report")
                .build();
        ArrayList<String> cookieNames = new ArrayList<>();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Cookie",
                        "ARRAffinity=" + cookies.get("ARRAffinity") + "; " +
                        "ARRAffinitySameSite=" + cookies.get("ARRAffinitySameSite") + "; " +
                        "CCLI_AUTH=" + cookies.get("CCLI_AUTH") + "; " +
                        "CCLI_JWT_AUTH=" + cookies.get("CCLI_JWT_AUTH") + "; " +
                        ".AspNetCore.Antiforgery.w5W7x28NAIs=" + cookies.get(".AspNetCore.Antiforgery.w5W7x28NAIs") + "; " +
                        ".AspNetCore.Session=" + cookies.get(".AspNetCore.Session"))
                .addHeader("Host", "reporting.ccli.com")
                .addHeader("Origin", "https://reporting.ccli.com")
                .addHeader("Refer", "https://reporting.ccli.com/search?s=" + songs[0].getCcliSongNo() + "&page=1&category=all")
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

        Response response = performRequest(request);
        if (response != null && response.body() != null) {
            return response.code();
        } else {
            return -1;
        }
    }

    private String getRequestVerificationToken(HashMap<String, String> cookies) {
        String responseHeader;
        String requestVerificationToken = null;

        Request request = new Request.Builder()
                .url("https://reporting.ccli.com/api/antiForgery")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie",
                        "ARRAffinity=" + cookies.get("ARRAffinity") + "; " +
                        "ARRAffinitySameSite=" + cookies.get("ARRAffinitySameSite") + "; " +
                        "CCLI_AUTH=" + cookies.get("CCLI_AUTH") + "; " +
                        "CCLI_JWT_AUTH=" + cookies.get("CCLI_JWT_AUTH") + "; " +
                        ".AspNetCore.Session=" + cookies.get(".AspNetCore.Session"))
                .addHeader("Host", "reporting.ccli.com")
                .addHeader("Origin", "https://reporting.ccli.com")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache")
                .build();

        Response response = performRequest(request);

            if (response != null && response.body() != null) {
                try {
                    requestVerificationToken = response.body().string().replace("\"", "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        return requestVerificationToken;
    }

    private Response performRequest(Request request) {
        try (Response response = this.client.newCall(request).execute()) {
            if (response.body() != null) {
                putCookies(response.headers("Set-Cookie"));
                return response;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void putCookies(List<String> headerValues) {
        for (String headerValue : headerValues) {
            this.cookies.put(headerValue.substring(0, headerValue.indexOf("=")),
                    headerValue.substring(headerValue.indexOf("=")+1, headerValue.indexOf(";")));
        }
    }

    private String buildCookieString(ArrayList<String> cookieNames, HashMap<String, String> cookies) {
        StringBuilder cookieStringBuilder = new StringBuilder();
        for (String cookieName : cookieNames) {
            if (!cookieNames.get(0).equals(cookieName)) {
                cookieStringBuilder.append("; ");
            }
            cookieStringBuilder.append(cookieName).append("=").append(cookies.get(cookieName));
        }

        return cookieStringBuilder.toString();
    }
}
