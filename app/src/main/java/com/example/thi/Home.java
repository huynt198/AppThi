package com.example.thi;

import android.content.Intent;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView test = (TextView) findViewById(R.id.test);
        test.setText(CONST.user.getName());



        Log.d("loi", "--------------------------------");
        Button load = (Button) findViewById(R.id.load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestQueue que ;
                Cache cache = new DiskBasedCache(getCacheDir(), 1024000 * 1024);
                Network network = new BasicNetwork(new HurlStack());
                que = new RequestQueue(cache, network);
                que.start();
                JSONObject jsonBody1 = new JSONObject();
                try {
                    jsonBody1.put("mes", "12");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = "http://thionline-test.herokuapp.com/api/question";
                JsonObjectRequest jsonOblect1 = new JsonObjectRequest(Request.Method.POST
                        , url, jsonBody1, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

//                            JSONObject res= new JSONObject(response.toString());

                        Log.d("res", "thanh cong");

                        Log.d("respone", response.toString());
//                        Intent intent = new Intent(Home.this,Home.class);
//                        startActivity(intent);
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
                        headers.put("Authorization", "Beare " + "");//put your token here
                        return headers;
                    }
                };

                que.add(jsonOblect1);
            }
        });


    }
}
