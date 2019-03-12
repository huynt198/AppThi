package com.example.thi;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
//    RequestQueue queue = Volley.newRequestQueue(this);
    User user= new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("loi", "ssssssssssssssssssss");
                RequestQueue que;
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
                Network network = new BasicNetwork(new HurlStack());
                que = new RequestQueue(cache, network);
                que.start();


                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", ((TextView) findViewById(R.id.username)).getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jsonBody.put("password", ((TextView)findViewById(R.id.password)).getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = "http://192.168.31.150:7001/api/admin/login";

                JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            JSONObject res= new JSONObject(response.toString());
                            if(response.get("token").toString().length() >0)
                            {
                                user.setToken(response.get("token").toString());
                                user.setId(Integer.parseInt(String.valueOf(response.get("id"))));
                                user.setName(response.get("name").toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                      Toast.makeText(getApplicationContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        onBackPressed();

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        final Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Beare " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NCwicm9sZSI6ImFkbWluIiwiaWF0IjoxNTUyMjM5NDUzLCJleHAiOjE1NTI0NTU0NTN9.Xuim67qzFNq62guWp_U90fSM7bhEv_flvf_yzIOOlU8");//put your token here
                        return headers;
                    }
                };
                que.add(jsonOblect);


//                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                // Do something with the response
//                                Log.d("res",response  );
//                                Object re=response;
//                                TextView name=(TextView)findViewById(R.id.name);
//
//                                if(response instanceof String){
//                                    Log.d("res","toString"  );
//                                }else{
//                                    Log.d("res","anthor"  );
//                                }
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                // Handle error
//                                Log.d("loi internet",error.toString());
//                            }
//                        });
//                que.add(stringRequest);

            }
        });


    }
}
