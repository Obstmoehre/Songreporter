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
    private HashMap<String, String> cookies;

    public RESTService(Set<Cookie> cookieSet) {
        this.cookies = convertCookies(cookieSet);
    }

    public Songdetails fetchSongdetails(String ccliSongNumber, Set<Cookie> cookieSet) {
        String res = null;
        HashMap<String, String> cookies = convertCookies(cookieSet);

        Request request = new Request.Builder()
                .url("https://reporting.ccli.com/api/detail/song/" + ccliSongNumber)
                .addHeader("Accept", "application/json, text/plain, */*")
//                .addHeader("Accept-Language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Cookie",
//                        "_ga=" + cookies.get("_ga") + "; " +
//                        "_hjid=" + cookies.get("_hjid") + "; " +
                        "ARRAffinity=" + cookies.get("ARRAffinity") + "; " +
                        "ARRAffinitySameSite=" + cookies.get("ARRAffinitySameSite") + "; " +
//                        "_gid=" + cookies.get("_gid") + "; " +
//                        "_hjTLDTest=" + cookies.get("_hjTLDTest") + "; " +
//                        "_hjAbsoluteSessionInProgress=" + cookies.get("_hjAbsoluteSessionInProgress") + "; " +
                        "CCLI_AUTH=" + cookies.get("CCLI_AUTH") + "; " +
                        "CCLI_JWT_AUTH=" + cookies.get("CCLI_JWT_AUTH") + "; " +
                        ".AspNetCore.Antiforgery.w5W7x28NAIs=" + cookies.get(".AspNetCore.Antiforgery.w5W7x28NAIs") + "; " +
                        ".AspNetCore.Session=" + cookies.get(".AspNetCore.Session"))
//                .addHeader("Host", "reporting.ccli.com")
//                .addHeader("Referer", "https://reporting.ccli.com/search?s=" + ccliSongNumber + "&page=1&category=all")
//                .addHeader("Sec-Fetch-Dest", "empty")
//                .addHeader("Sec-Fetch-Mode", "cors")
//                .addHeader("Sec-Fetch-Site", "same-origin")
//                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36")
//                .addHeader("client-locale", "de-DE")
//                .addHeader("dnt", "1")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                res = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gson.fromJson(res, Songdetails.class);
    }

    public int reportSongs(Songdetails[] songs, Set<Cookie> cookieSet) {
        int res = 0;
        HashMap<String, String> cookies = convertCookies(cookieSet);
        String requestVerificationToken = getRequestVerificationToken(cookies);
        String reportPayload = gson.toJson(new ReportPayload(songs));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), reportPayload);

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("reporting.ccli.com")
                .port(443)
                .addPathSegment("api")
                .addPathSegment("report")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Cookie",
//                        "_ga=" + cookies.get("_ga") + "; " +
//                        "_hjid=" + cookies.get("_hjid") + "; " +
                        "ARRAffinity=" + cookies.get("ARRAffinity") + "; " +
                        "ARRAffinitySameSite=" + cookies.get("ARRAffinitySameSite") + "; " +
//                        "_gid=" + cookies.get("_gid") + "; " +
//                        "_hjTLDTest=" + cookies.get("_hjTLDTest") + "; " +
                        "CCLI_AUTH=" + cookies.get("CCLI_AUTH") + "; " +
                        "CCLI_JWT_AUTH=" + cookies.get("CCLI_JWT_AUTH") + "; " +
                        ".AspNetCore.Antiforgery.w5W7x28NAIs=" + cookies.get(".AspNetCore.Antiforgery.w5W7x28NAIs") + "; " +
                        ".AspNetCore.Session=" + cookies.get(".AspNetCore.Session"))
//                        "_gat_UA-11918520-51=" + cookies.get("_gat_UA-11918520-51") + "; " +
//                        "_gat_UA-11918520-80=" + cookies.get("_gat_UA-11918520-80") + "; " +
//                        "_hjFirstSeen=" + cookies.get("_hjFirstSeen") + "; " +
//                        "_hjAbsoluteSessionInProgress=" + cookies.get("_hjAbsoluteSessionInProgress"))
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
                .method("POST", body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                res = response.code();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
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
//                        "_ga=" + cookies.get("_ga") + "; " +
//                        "_hjid=" + cookies.get("_hjid") + "; " +
                        "ARRAffinity=" + cookies.get("ARRAffinity") + "; " +
                        "ARRAffinitySameSite=" + cookies.get("ARRAffinitySameSite") + "; " +
//                        "_gid=" + cookies.get("_gid") + "; " +
//                        "_hjTLDTest=" + cookies.get("_hjTLDTest") + "; " +
                        "CCLI_AUTH=" + cookies.get("CCLI_AUTH") + "; " +
                        "CCLI_JWT_AUTH=" + cookies.get("CCLI_JWT_AUTH") + "; " +
                        ".AspNetCore.Session=" + cookies.get(".AspNetCore.Session"))
                .addHeader("Host", "reporting.ccli.com")
                .addHeader("Origin", "https://reporting.ccli.com")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                requestVerificationToken = response.body().string().replace("\"", "");
                responseHeader = response.header("Set-Cookie");
                if (responseHeader != null) {
                    cookies.put(responseHeader.substring(0, responseHeader.indexOf("=")),
                            responseHeader.substring(responseHeader.indexOf("=") + 1, responseHeader.indexOf(";")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return requestVerificationToken;
    }

    private Response performRequest(Request request) {
        Response res = null;
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                res = response;
                putCookies(response.headers("Set-Cookie"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    private HashMap<String, String> convertCookies(Set<Cookie> cookieSet) {
        HashMap<String, String> cookieMap = new HashMap<>();
        for (Cookie cookie : cookieSet) {
            cookieMap.put(cookie.getName(), cookie.getValue());
        }

        return cookieMap;
    }

    private void putCookies(List<String> headerValues) {
        for (String headerValue : headerValues) {
            cookies.put(headerValue.substring(0, headerValue.indexOf("=")),
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
