import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jon-bassi on 2/27/16.
 */
public class BingSearch
{
    public static List<String> search(String query) throws Exception {
        String bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%27" + java.net.URLEncoder.encode(query) + "%27&$format=JSON";

        String accountKey = "";

        byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes()); // code for encoding found on stackoverflow
        String accountKeyEnc = new String(accountKeyBytes);

        URL url = new URL(bingUrl);
        URLConnection urlConnection = url.openConnection();
        String s1 = "Basic " + accountKeyEnc;
        urlConnection.setRequestProperty("Authorization", s1);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                urlConnection.getInputStream()));
        String inputLine;
        StringBuffer sb = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine);
        in.close();
        String json = sb.toString();
        JSONObject d = (JSONObject) ((Map) JSONValue.parse(json)).get("d");
        JSONArray results = (JSONArray) d.get("results");
        int result_size = results.size();
        List<String> theResults = new ArrayList<String>();
        for (int i = 0; i < result_size; i++)
        {
            JSONObject result = (JSONObject) results.get(i);
            String url2 = (String) result.get("Url");
            theResults.add(url2);
        }
        return theResults;
    }
}
