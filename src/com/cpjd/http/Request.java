package com.cpjd.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cpjd.listeners.RawResponseListener;

public class Request
{
  public static boolean OUTPUT_RAW_RESPONSES = false;
  private RawResponseListener listener;
  public String serverIP;
  private JSONParser responseParser;
  
  public void setListener(RawResponseListener listener) { this.listener = listener; }
  
  public static enum RequestType
  {
    PING, 
    GET, 
    POST;
  }
  
  public Request(String serverIP) {
    this.serverIP = serverIP;
    responseParser = new JSONParser();
  }
  
  public boolean ping() {
    try {
      return doRequest(RequestType.PING, "admin/ping", null) != null;
    } catch (Exception e) {}
    return false;
  }
  

  public Object doRequest(RequestType requestType, String urlSuffix, HashMap<String, String> parameters)
    throws IOException, ParseException
  {
    StringBuilder urlStringBuilder = new StringBuilder("http://" + serverIP + "/" + urlSuffix);
    
    if (((requestType == RequestType.GET) || (requestType == RequestType.PING)) && (parameters != null)) {
      urlStringBuilder.append("?");
      
      for (Object key : parameters.keySet()) {
        urlStringBuilder.append("&").append(encodeString(key.toString())).append("=").append(encodeString(((String)parameters.get(key)).toString()));
      }
    }
    URL url = new URL(urlStringBuilder.toString());
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    if (requestType == RequestType.PING) {
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(3000);
      connection.setReadTimeout(3000);
    }
    if (requestType == RequestType.GET) { connection.setRequestMethod("GET");
    } else if (requestType == RequestType.POST) {
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
    }
    connection.setRequestProperty("charset", "utf-8");
    connection.setUseCaches(false);

    if (requestType == RequestType.POST) {
      OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
      urlStringBuilder = new StringBuilder();
      if (parameters != null) {
        for (Object key : parameters.keySet()) {
          urlStringBuilder.append("&" + encodeString(key.toString()) + "=" + encodeString((String)parameters.get(key)));
        }
        wr.write(urlStringBuilder.toString());
      }
      wr.flush();
    }
    
    InputStream is = connection.getInputStream();
    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    StringBuilder response = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      response.append(line);
      response.append('\r');
    }
    rd.close();
    if (connection != null) connection.disconnect();
    Object toReturn = responseParser.parse(response.toString());
    if (toReturn == null) throw new IOException();
    if (listener != null) listener.messageReceived(toReturn.toString());
    if (OUTPUT_RAW_RESPONSES) System.out.println(toReturn.toString());
    return toReturn;
  }
  
  private String encodeString(String string)
  {
    try
    {
      return URLEncoder.encode(string, "UTF-8");
    } catch (UnsupportedEncodingException e) {}
    return "null";
  }
}
