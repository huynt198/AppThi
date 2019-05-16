package com.example.thi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
//import com.example.thi.ui.detail.DetailFragment;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class detail extends AppCompatActivity {

    Question question = new Question();
    float screen_width;
    String[] subjectes = {"Toan", "Ly", "Hoa"};
    String subject = "Toan";
    String[] classes = {"12", "11", "10"};
    String clas = "12";
    String[] levels = {"De", "Trung binh", "Kho"};
    String level = "1";

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
        final ImageView image_prative = (ImageView) findViewById(R.id.image_prative);

        A.setTextColor(Color.parseColor("#000000"));
        B.setTextColor(Color.parseColor("#000000"));
        C.setTextColor(Color.parseColor("#000000"));

        // Handle the camera action
        Log.d("cameraaaaaaaaa", "------------------------------------------");
        String url = CONST.url + "get_question";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("subject", subject);
            jsonBody.put("level", level);
            jsonBody.put("class", clas);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                Log.d("ereeeeeee", response.toString());
                try {

                    JSONArray a = (JSONArray) response.get("data");
                    if (a.length() == 0) {
                        message.setText("No data to display, please chose another");

                        A.setText("");

                        B.setText("");

                        C.setText("");
                        image_prative.setVisibility(View.GONE);
                        ((Button) findViewById(R.id.check)).setEnabled(false);
                    } else {
                        ((Button) findViewById(R.id.check)).setEnabled(true);
                        JSONObject ques = a.getJSONObject(0);
                        image_prative.setVisibility(View.GONE);
                        if (ques.has("url")) {
                            image_prative.setVisibility(View.VISIBLE);
                            Picasso.with(detail.this).load(ques.getString("url")).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .into(image_prative);
                        }

                        JSONArray answer = (JSONArray) ques.get("answer");
                        question.setId(ques.get("_id").toString());
                        System.out.println(a.toString());
                        Ans.clearCheck();
                        message.setText(ques.get("question").toString());
                        A.setText("A :" + answer.getJSONObject(0).get("A").toString());
                        B.setText("B :" + answer.getJSONObject(1).get("B").toString());
                        C.setText("C :" + answer.getJSONObject(2).get("C").toString());

                    }


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


        Spinner spinner_classes = (Spinner) findViewById(R.id.spinner_class);

        Spinner spinner_subjectes = (Spinner) findViewById((R.id.spinner_subject));


        Spinner spinner_levels = (Spinner) findViewById(R.id.spinner_level);


        ArrayAdapter<String> adapterClasses = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, classes);
        adapterClasses.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner_classes.setAdapter(adapterClasses);


        ArrayAdapter<String> adapterSubjectes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectes);
        adapterSubjectes.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner_subjectes.setAdapter(adapterSubjectes);


        ArrayAdapter<String> adapterLevels = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levels);
        adapterSubjectes.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner_levels.setAdapter(adapterLevels);


        spinner_classes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("class", parent.getItemAtPosition(position).toString());
                clas = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_subjectes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("sublect", parent.getItemAtPosition(position).toString());
                subject = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_levels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("levels", parent.getItemAtPosition(position).toString());
                switch (parent.getItemAtPosition(position).toString()) {
                    case "De": {
                        level = "1";
                        break;
                    }
                    case "Trung binh": {
                        level = "2";
                        break;
                    }
                    case "Kho": {
                        level = "3";
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        clicek();

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, DetailFragment.newInstance())
//                    .commitNow();
//        }


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

                final RadioGroup ans = (RadioGroup) findViewById(R.id.ans);
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
                        try {
                            if(response.getString("message").equals("Chinh Xac")){
                                ((RadioButton) findViewById(ans.getCheckedRadioButtonId())).setTextColor(Color.parseColor("#00DD00"));
                            }
                            else{
                                ((RadioButton) findViewById(ans.getCheckedRadioButtonId())).setTextColor(Color.parseColor("#FF3300"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
