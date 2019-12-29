package com.hydrologis.remarkable.cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.json.JSONObject;

import com.hydrologis.remarkable.PreKeys;
import com.hydrologis.remarkable.utils.GuiUtilities;

public class ApiHandler {

    public static final String RESPONSE_CODE = "responseCode";

    public ApiHandler() {

    }

    public BufferedReader httpGet( String urlPath, String authHeader, HashMap<String, Object> map4Error )
            throws MalformedURLException, IOException, ProtocolException {
        URL url = new URL(urlPath);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        if (authHeader != null)
            connection.setRequestProperty("Authorization", authHeader);

        BufferedReader in = null;
        try {
            InputStream content = (InputStream) connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(content));
        } catch (Exception e) {
            Map<String, List<String>> responseHeaderMap = connection.getHeaderFields();
            for( Entry<String, List<String>> entry : responseHeaderMap.entrySet() ) {
                List<String> list = entry.getValue();
                if (list != null && list.size() > 0) {
                    map4Error.put(entry.getKey(), list.get(0));
                }
            }

            int responseCode = connection.getResponseCode();
            map4Error.put(RESPONSE_CODE, responseCode);
            throw e;
        }
        return in;
    }

    public String stringFromGet( String urlPath, String auth, HashMap<String, Object> map4Error ) throws Exception {
        try (BufferedReader reader = httpGet(urlPath, auth, map4Error)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while( (line = reader.readLine()) != null ) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    public BufferedReader httpPost( String urlPath, String authHeader, JSONObject payloadJson, HashMap<String, Object> map4Error )
            throws Exception {
        URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        if (authHeader != null) {
            connection.setRequestProperty("Authorization", authHeader);
        }
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        try {
            if (payloadJson != null) {
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(payloadJson.toString());
                wr.flush();
            }
        } catch (Exception e) {
            Map<String, List<String>> responseHeaderMap = connection.getHeaderFields();
            for( Entry<String, List<String>> entry : responseHeaderMap.entrySet() ) {
                List<String> list = entry.getValue();
                if (list != null && list.size() > 0) {
                    map4Error.put(entry.getKey(), list.get(0));
                }
            }

            int responseCode = connection.getResponseCode();
            map4Error.put(RESPONSE_CODE, responseCode);
            throw e;
        }

        int httpResult = connection.getResponseCode();
        if (httpResult == HttpURLConnection.HTTP_OK || httpResult == HttpURLConnection.HTTP_ACCEPTED) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));// ,
                                                                                                       // "utf-8"));
            return br;
        } else {
            throw new Exception("POST returned " + httpResult + " with error: " + connection.getResponseMessage());
        }
    }

    public String stringFromPost( String urlPath, String auth, JSONObject requestJson, HashMap<String, Object> map4Error )
            throws Exception {
        try (BufferedReader reader = httpPost(urlPath, auth, requestJson, map4Error)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while( (line = reader.readLine()) != null ) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    private String getToken( String code ) throws Exception {
        String token = GuiUtilities.getPreference(PreKeys.CLOUD_TOKEN, "");
        if (token.length() == 0) {
            if (code == null) {
                String userCode = GuiUtilities.showInputDialog(null,
                        "Please enter the code generated at https://my.remarkable.com/generator-device", "");
                if (userCode == null || userCode.trim().length() == 0) {
                    return null;
                }
                code = userCode;
            }

            String url = "https://my.remarkable.com/token/json/2/device/new";
            /*
             {
            "code": "gliuqtne",
            "deviceDesc": "desktop-windows",
            "deviceID": "701c3752-1025-4770-af43-5ddcfa4dabb2"
            }
             */
            JSONObject payload = new JSONObject();
            payload.put("code", code);
            payload.put("deviceDesc", "desktop-linux");
//            MessageDigest salt = MessageDigest.getInstance("SHA-256");
//            salt.update(UUID.randomUUID().toString().getBytes("UTF-8"));
//            String uuid4hex = bytesToHex(salt.digest());
            String uuid = UUID.randomUUID().toString();
            payload.put("deviceID", uuid);

            HashMap<String, Object> errorMap = new HashMap<>();
            try {
                stringFromPost(url, "Bearer", payload, errorMap);
            } catch (Exception e) {
                printFatalErrorAndExit("ERROR", e, errorMap);
            }
        }
        return token;
    }

    public static String bytesToHex( byte[] bytes ) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void printFatalErrorAndExit( String msg, Exception exception, HashMap<String, Object> map4Error ) {
        System.out.println(msg);
        if (map4Error != null) {
            Object respCodeObj = map4Error.get(ApiHandler.RESPONSE_CODE);
            if (respCodeObj instanceof Number && ((Number) respCodeObj).intValue() == 429) {
                System.out.println(
                        "The server doesn't accept more requests from the current user. Wait some time before trying again.");
                Object retyAfterObj = map4Error.get("Retry-After");
                if (retyAfterObj != null) {
                    String secondsString = retyAfterObj.toString();
                    try {
                        int seconds = Integer.parseInt(secondsString);
                        System.out.println("The server suggests to retry after " + seconds + " seconds.");
                        if (seconds == 0) {
                            System.out.println("Since 0 seconds doesn't make much sense, more info following");
                        } else {
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        }
        if (exception != null)
            System.out.println("The message supplied: " + exception.getMessage());
        if (map4Error != null) {
            System.out.println("The response headers supplied:");
            for( Entry<String, Object> entry : map4Error.entrySet() ) {
                System.out.println("Header key: " + entry.getKey() + "      value: " + entry.getValue().toString());
            }
        }
        System.exit(1);
    }

    public static void main( String[] args ) throws Exception {
        ApiHandler apiHandler = new ApiHandler();

        String token = apiHandler.getToken(null);
        if (token != null) {
            System.out.println(token);
            GuiUtilities.setPreference(PreKeys.CLOUD_TOKEN, token);
        } else {
            System.err.println("Token not generated!");
        }

    }
}
