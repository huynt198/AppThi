package com.example.thi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.thi.CONST.url;

public class ThiTuDo<a> extends AppCompatActivity {
    Integer a2;
    Integer sec = 59000;
    Integer min = 89;
    Question que = new Question();
    private View[] a = new View[50];
    private TextView cau;
    int flag = 0;
    JSONArray result = new JSONArray();
    Boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Ban dang thi, nhan laan nua de thoat", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_fragment);

        for (int i = 0; i < 50; i++) {
            JSONObject ao = new JSONObject();
            try {
                ao.put("answer", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                ao.put("id", CONST.exam[i].getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String ad = "a" + String.valueOf(i);
            int resID = getResources().getIdentifier(ad, "id", getPackageName());
            a[i] = (Button) findViewById(resID);

            result.put((Object) ao);

        }

        cau = (TextView) findViewById(R.id.cau);
        final AlertDialog.Builder a = new AlertDialog.Builder(this);
        a.setTitle("Xac nhan");
        a.setCancelable(false);
        a.setMessage("Ban co 90 phut de lam bai!\nBan chi nop bai sau 60 phut.");
        final TextView as = (TextView) findViewById(R.id.time);

        add((View) findViewById(R.id.a0));
        ((Button) findViewById(R.id.send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select();
                send();
                flag = 9;
            }
        });

        a.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new CountDownTimer((min + 1) * 60 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        if (sec < 0) {
                            sec = 59000;
                            min -= 1;
                            Integer s = sec / 1000;

                            if (s < 10)
                                as.setText(min.toString() + ":0" + s.toString());
                            else {
                                as.setText(min.toString() + ":" + s.toString());
                            }
                        } else {
                            Integer s = sec / 1000;
                            if (s < 10)
                                as.setText(min.toString() + ":0" + s.toString());
                            else {
                                as.setText(min.toString() + ":" + s.toString());
                            }
                        }
                        sec -= 1000;
                    }

                    @Override
                    public void onFinish() {

                        if (flag == 0)
                            send();
                    }
                }.start();

            }
        });
        a.show();

    }

    private void send() {
        System.out.println(result.toString());
        if (min >= 30) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ThiTuDo.this);
            alertDialogBuilder.setCancelable(true);
            // alertDialogBuilder.setIcon(R.drawable.ic_menu_send);

            alertDialogBuilder.setMessage("Chua den gio nop bai!");
            alertDialogBuilder.show();
        } else {
            final ProgressDialog progress;
            progress = new ProgressDialog(ThiTuDo.this);
            progress.setTitle("Gui ket qua");
            progress.setMessage("Wait while checking...");
            progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
            progress.show();
            RequestQueue que;
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            que = new RequestQueue(cache, network);
            que.start();
            JSONObject re = new JSONObject();
            try {
                re.put("id_exam", CONST.id_exam);
                re.put("result", (Object) result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, url + "check_result", re,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.cancel();

                            AlertDialog.Builder aw = new AlertDialog.Builder(ThiTuDo.this);
                            aw.setTitle("Ket qua");
                            try {
                                aw.setMessage(response.get("message").toString()
                                        + "\nVui long xem ket qua chi tiet trong myExam");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            aw.setCancelable(false);
                            aw.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ThiTuDo.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            aw.show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.cancel();

                            AlertDialog.Builder aw = new AlertDialog.Builder(ThiTuDo.this);
                            aw.setTitle("Loi ket noi");
                            aw.setMessage("Vui long kiem tra ket noi. Thu lai?");
                            aw.setCancelable(false);
                            aw.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    send();
                                }
                            });
                            aw.show();

                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Beare " + CONST.user.getToken());// put your token here
                    return headers;
                }
            };
            que.add(jsonOblect);
        }
    }

    public void add(View view) {

        select();
        ((RadioGroup) findViewById(R.id.ans)).clearCheck();
        a2 = findIndex(a, view);

        Integer a3 = a2 + 1;
        cau.setText("Cau " + a3.toString() + ":");
        que = CONST.exam[a2];
        if (!que.getImg().equals("")) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            ((ImageView) findViewById(R.id.image)).setVisibility(View.VISIBLE);
            Picasso.with(this).load(que.getImg()).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into((ImageView) findViewById(R.id.image));
        } else
            ((ImageView) findViewById(R.id.image)).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.message)).setText(que.getQuestion());
        ((RadioButton) findViewById(R.id.A)).setText("A :" + que.getST1());
        ((RadioButton) findViewById(R.id.B)).setText("B :" + que.getST2());
        ((RadioButton) findViewById(R.id.C)).setText("C :" + que.getST3());
        checked();

    }

    private void checked() {
        for (int i = 0; i < result.length(); i++) {
            try {

                JSONObject ed = (JSONObject) result.getJSONObject(i);
                if (ed.get("id").equals(que.getId())) {
                    if (ed.get("answer").equals("")) {

                    } else {
                        String aaa = ed.get("answer").toString();
                        if (((RadioButton) findViewById(R.id.A)).getText().equals(aaa)) {
                            int resID = getResources().getIdentifier("A", "id", getPackageName());
                            ((RadioGroup) findViewById(R.id.ans)).check(resID);
                        } else if (((RadioButton) findViewById(R.id.B)).getText().equals(aaa)) {
                            int resID = getResources().getIdentifier("B", "id", getPackageName());
                            ((RadioGroup) findViewById(R.id.ans)).check(resID);
                        } else {
                            int resID = getResources().getIdentifier("C", "id", getPackageName());
                            ((RadioGroup) findViewById(R.id.ans)).check(resID);
                        }

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void select() {
        JSONObject jsonBody = new JSONObject();

        RadioGroup ans = (RadioGroup) findViewById(R.id.ans);

        switch (ans.getCheckedRadioButtonId()) {

        case R.id.A:
            try {
                jsonBody.put("answer", ((RadioButton) findViewById(R.id.A)).getText());
                jsonBody.put("id", que.getId());
                ((Button) a[a2]).setTextColor(Color.parseColor("#000000"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            break;
        case R.id.B:
            try {
                jsonBody.put("answer", ((RadioButton) findViewById(R.id.B)).getText());
                jsonBody.put("id", que.getId());
                ((Button) a[a2]).setTextColor(Color.parseColor("#000000"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            break;
        case R.id.C:
            try {
                jsonBody.put("answer", ((RadioButton) findViewById(R.id.C)).getText());
                jsonBody.put("id", que.getId());
                ((Button) a[a2]).setTextColor(Color.parseColor("#000000"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            break;

        default:

        }
        if (jsonBody != null) {

            for (int i = 0; i < result.length(); i++)
                try {
                    JSONObject az = (JSONObject) result.getJSONObject(i);
                    if (az.get("id").equals(que.getId())) {
                        az.put("answer", jsonBody.get("answer"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

    }

    private int findIndex(View[] a, View view) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == view)
                return i;
        }
        return -1;
    }
}
