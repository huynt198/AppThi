package com.example.thi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.example.thi.ui.detail.DetailFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class detail extends AppCompatActivity {

    Question question = new Question();


    private void clicek() {
        final ProgressDialog progress;
        progress = new ProgressDialog(detail.this);
        progress.setTitle("Loading...");

        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        RequestQueue queue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();
        final RadioButton A = (RadioButton) findViewById(R.id.A);
        final RadioButton B = ((RadioButton) findViewById(R.id.B));
        final RadioButton C = ((RadioButton) findViewById(R.id.C));
        final RadioGroup Ans = (RadioGroup) findViewById(R.id.ans);
        final TextView message = (TextView) findViewById(R.id.message);

        // Handle the camera action
        Log.d("cameraaaaaaaaa", "------------------------------------------");
        String url = "http://thionline-test.herokuapp.com/api/next_question";
        JSONObject jsonBody = new JSONObject();

        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                Log.d("ereeeeeee", response.toString());
                try {

                    JSONArray a = (JSONArray) response.get("data");
                    JSONObject ques = a.getJSONObject(0);
                    JSONArray answer = (JSONArray) ques.get("answer");
                    question.setId(ques.get("_id").toString());
                    System.out.println(a.toString());
                    Ans.clearCheck();
                    message.setText(ques.get("question").toString());
                    A.setText("A :" + answer.getJSONObject(0).get("A").toString());
                    B.setText("B :" + answer.getJSONObject(1).get("B").toString());
                    C.setText("C :" + answer.getJSONObject(2).get("C").toString());

                } catch (JSONException e) {


                    e.printStackTrace();
                }
                Log.d("resssssss", String.valueOf(response));


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progress.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(detail.this);
                alertDialogBuilder.setCancelable(true);
//                        alertDialogBuilder.setIcon(R.drawable.ic_menu_send);

                alertDialogBuilder.setMessage("Loi ket noi");
                alertDialogBuilder.show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Beare " + CONST.user.getToken());//put your token here
                return headers;
            }
        };
        queue.add(jsonOblect);
        Log.d("out", "9999999999999999999999999999999999999");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        clicek();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DetailFragment.newInstance())
                    .commitNow();
        }


        ((Button) findViewById(R.id.check)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progress;
                progress = new ProgressDialog(detail.this);
                progress.setTitle("Checking...");

                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                RequestQueue queue;
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
                Network network = new BasicNetwork(new HurlStack());
                queue = new RequestQueue(cache, network);
                queue.start();
                String url = "http://thionline-test.herokuapp.com/api/check_question";
                JSONObject jsonBody = new JSONObject();

                RadioGroup ans = (RadioGroup) findViewById(R.id.ans);
                switch (ans.getCheckedRadioButtonId()) {

                    case R.id.A:
                        try {
                            jsonBody.put("answer", ((RadioButton) findViewById(R.id.A)).getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.B:
                        try {
                            jsonBody.put("answer", ((RadioButton) findViewById(R.id.B)).getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.C:
                        try {
                            jsonBody.put("answer", ((RadioButton) findViewById(R.id.C)).getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            jsonBody.put("answer", "A :--------------------------------------------------------------");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
                try {
                    jsonBody.put("id_question", question.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ereeeeeee", response.toString());
                        progress.cancel();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(detail.this);
                        alertDialogBuilder.setCancelable(true);
                        try {
                            alertDialogBuilder.setMessage(response.get("message").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        alertDialogBuilder.show();

                        Log.d("resssssss", String.valueOf(response));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.cancel();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(detail.this);
                        alertDialogBuilder.setCancelable(true);
//                        alertDialogBuilder.setIcon(R.drawable.ic_menu_send);

                        alertDialogBuilder.setMessage("Loi ket noi");
                        alertDialogBuilder.show();

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        final Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Beare " + CONST.user.getToken());//put your token here
                        return headers;
                    }
                };
                queue.add(jsonOblect);
                Log.d("out", "9999999999999999999999999999999999999");


            }
        });


        ((Button) findViewById(R.id.next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicek();
            }
        });
    }

}
