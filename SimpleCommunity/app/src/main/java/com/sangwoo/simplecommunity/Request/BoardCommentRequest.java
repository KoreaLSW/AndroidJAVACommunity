package com.sangwoo.simplecommunity.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BoardCommentRequest extends StringRequest {

    final static private String URL = "http://112.146.241.138:80/serverimg/commentupload.php";
    private Map<String, String> parameters;

    public BoardCommentRequest(int idx, String user, String data, String text, String count,  Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("idx", String.valueOf(idx));
        parameters.put("user", user);
        parameters.put("data", data);
        parameters.put("text", text);
        parameters.put("count", count);
        Log.d("commentchkek", idx + " " + user + " " + data + " " + text);
    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
