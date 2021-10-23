package com.example.tejasvedantham.pttmobile2;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackendConnections {
    public static String baseUrl = "http://gazelle.cc.gatech.edu:9002/ptt";
    final String contentType = "application/json; charset=utf-8";

    Context context;
    RequestQueue requestQueue;

    private Map<String, String> header;

    public BackendConnections(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        header = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void ExecuteHTTPRequest(String endpoint, int requestType, JSONObject postData,final VolleyCallback callback){
        String url = baseUrl + endpoint;

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestType, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onError(error);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    public interface VolleyCallback {
        public void onSuccess(JSONObject response);
        public void onError(VolleyError error);
    }
}
