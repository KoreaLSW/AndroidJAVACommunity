package com.sangwoo.simplecommunity.ShootingGame;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ScoreRequest extends StringRequest {

    final static private String URL = "http://112.146.241.138:80/serverimg/gamescroupload.php";
    private Map<String, String> parameters;

    public ScoreRequest(String user, int score, String data, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("score", String.valueOf(score));
        parameters.put("data", data);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
