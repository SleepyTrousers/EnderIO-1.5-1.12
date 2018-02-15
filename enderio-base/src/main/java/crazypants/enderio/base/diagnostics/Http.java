package crazypants.enderio.base.diagnostics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.HttpUtil;

public class Http {

  public static Response post(URL url, Object postData) throws IOException {
    return post(url, postData, null);
  }

  public static Response post(URL url, Object postData, Map<String, String> reqHeaders) throws IOException {
    try {
      String data = "";
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      // fill POST headers
      if (postData != null) {
        data = postData instanceof Map ? HttpUtil.buildPostString((Map<String, Object>) postData) : postData.toString();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");
      }

      // set up connection
      connection.setRequestProperty("User-Agent", "crash-reporter");
      connection.setUseCaches(false);
      connection.setDoInput(true);

      if (reqHeaders != null) {
        for (Map.Entry<String, String> entry : reqHeaders.entrySet()) {
          connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
      }

      // fill POST data
      if (postData != null) {
        connection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(data);
        out.flush();
        out.close();
      }

      // place request
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String text = "";
      String line;
      while ((line = in.readLine()) != null) {
        text += line + "\n";
      }

      Map<String, String> headers = new HashMap<String, String>();

      for (String header : connection.getHeaderFields().keySet()) {
        headers.put(header, connection.getHeaderField(header));
      }

      return new Response(text, connection.getURL().toString(), headers);
    } catch (Throwable e) {
      throw new IOException(e);
    }
  }

  public static class Response {
    public final String text;
    public final String url;
    public final Map<String, String> headers;

    private Response(String text, String url, Map<String, String> headers) {
      this.text = text;
      this.url = url;
      this.headers = Collections.unmodifiableMap(headers);
    }
  }
}