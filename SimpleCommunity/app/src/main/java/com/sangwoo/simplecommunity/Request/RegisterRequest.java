package com.sangwoo.simplecommunity.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    final static private String URL = "http://112.146.241.138:80/Simple/UserRegister.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userPassword, String userEmail, String userCheck, String Delete_confirm, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userEmail", userEmail);
        parameters.put("userCheck", userCheck);
        parameters.put("Delete_confirm", Delete_confirm);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
