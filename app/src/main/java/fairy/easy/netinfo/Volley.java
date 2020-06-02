package fairy.easy.netinfo;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Volley {

    public static void useVolley(Context context) {
        RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, App.HTTPS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(App.TAG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("name1", "value1");
                params.put("name2", "value2");

                return params;
            }
        };

        requestQueue.add(stringRequest);

    }
}
