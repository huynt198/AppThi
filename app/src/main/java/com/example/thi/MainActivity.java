package com.example.thi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CONST.user.setToken("");
        if (CONST.user.getToken().length() > 0) {
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
        }
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open dialog loading
                final ProgressDialog progress;
                progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
// To dismiss the dialog
//                progress.dismiss();
                Log.d("test", "-------------------------------------------------------");
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
                    jsonBody.put("pass", ((TextView) findViewById(R.id.password)).getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = "http://thionline-test.herokuapp.com/api/login";

                JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ereeeeeee", response.toString());
                        try {


                            if (response.get("data").toString().length() > 0) {
                                CONST.user.setToken(response.get("data").toString());
                                CONST.user.setName(response.get("name").toString());
//                                ((TextView) findViewById(R.id.nameuser)).setText(response.get("name").toString());
                                Log.d("res", "thanh cong");
                                Toast.makeText(MainActivity.this, "dang nhap thanh cong", Toast.LENGTH_LONG);
                                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                startActivity(intent);
                                finish();

                                progress.dismiss();
                            } else {
                                Log.d("res", "Loi");
                                progress.dismiss();
                                Toast.makeText(MainActivity.this, "Loi dang nhap:  ", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            progress.dismiss();
                            Log.d("res", "Loi");
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setCancelable(true);
//                        alertDialogBuilder.setIcon(R.drawable.ic_menu_send);

                        alertDialogBuilder.setMessage("Loi ket noi");
                        alertDialogBuilder.show();
//                        onBackPressed();

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        final Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Beare " + CONST.user.getToken());//put your token here
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
