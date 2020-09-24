package com.sangwoo.simplecommunity.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BoardRequest extends StringRequest {

    final static private String URL = "http://112.146.241.138:80/serverimg/upload.php";
    private Map<String, String> parameters;

    public BoardRequest(String title, String user, String data, StringBuffer img, String text, String orientation, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("title", title);
        parameters.put("user", user);
        parameters.put("data", data);
        parameters.put("img", String.valueOf(img));
        parameters.put("text", text);
        parameters.put("orientation", orientation);
        Log.d("test", title + "/" + user + "/" + data + "/" + img + "/" + text + "/" + orientation);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
