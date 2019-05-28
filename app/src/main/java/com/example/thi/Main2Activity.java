package com.example.thi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

    Question question = new Question();
    String id_toan = "5ced66754a1a0230b49e097e";
    String id_ly = "5cecb5b575044006c01cfc1a";
    String id_hoa = "5cecbb9275044006c01cfc1b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ((ImageView) findViewById(R.id.vatly)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thi(id_ly);
            }
        });
        ((ImageView) findViewById(R.id.hoa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thi(id_hoa);
            }
        });
        ((ImageView) findViewById(R.id.toan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thi(id_toan);
            }
        });
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // set name user
        ((TextView) findViewById(R.id.nameuser)).setText(CONST.user.getName());
        ((TextView) findViewById(R.id.email_user)).setText(CONST.user.getEmail());
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // ((TextView) findViewById(R.id.nameuser)).setText(CONST.user.getName());

        if (id == R.id.homepage) {

        } else if (id == R.id.practice) {
//chuyển activity tương ứng
            Intent intent = new Intent(Main2Activity.this, detail.class);
            startActivity(intent);
        } else if (id == R.id.exam) {
//hiển thi diaolog nhập mã đề thi
            checkexam("ok");
        }
        // else if (id == R.id.news) {
        //
        // }
        else if (id == R.id.logout) {
            //logout và chuyển về activity đăng nhập
            if ((new UserHelper(this)).delete(CONST.user.getEmail())) {
                CONST.user = new User();
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkexam(String err) {
        //hiển thị diaolog nhập mã đề thi

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhap ma de thi");
        if (err.equals("err"))
            builder.setMessage("Ma khong chinh xac!");
        builder.setCancelable(false);

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //hiển thị checking đề thi
                final ProgressDialog progress;
                progress = new ProgressDialog(Main2Activity.this);
                progress.setTitle("Checking");

                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                final String test = input.getText().toString();
                System.out.println("-------------" + test.length());
//Tạo hàng đợi xử lý request
                RequestQueue queue;
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
                Network network = new BasicNetwork(new HurlStack());
                queue = new RequestQueue(cache, network);
                queue.start();
//tạo Object chứa các tham số truyền lên server
                JSONObject text = new JSONObject();
                try {
                    text.put("code", input.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST,
                        CONST.url + "check_code_exam", text, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//gọi hàm xử lý kết quả trả về
                        progress.dismiss();
                        getexam(response, test);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                        progress.dismiss();
//thông báo lloix
                        Toast.makeText(Main2Activity.this, "Vui long kiem tra lai", Toast.LENGTH_LONG);
                        checkexam("err");

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        final Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Beare " + CONST.user.getToken());// put your token here
                        return headers;
                    }
                };
                queue.add(jsonOblect);

            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
//hiển thị dialog
        builder.show();
    }

    private void getexam(JSONObject response, String test) {
        //xử lý kết quả khi check mã  đề thi
        String code = "9999";
        try {
            code = response.getString("code").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //nếu mã dề thi tồn tại thì sẽ lấy dữ liệu đổ vào biến trong hệ thống
        if (code.equals("1000")) {
            CONST.id_exam = test;
            System.out.println(response.toString());
            try {
                JSONArray data = (JSONArray) ((JSONObject) ((JSONArray) response.getJSONArray("data")).getJSONObject(0))
                        .getJSONArray("questions");
                for (int i = 0; i < data.length(); i++) {
                    //tạo 1 đối tuong câu hỏi rooig gán giá trị sau đó lưu vào mảng để sử dụng
                    //xử dụng JSONObject và JSONArray để tách data
                    Question a = new Question();
                    JSONObject question = (JSONObject) data.getJSONObject(i);
                    JSONArray answer = ((JSONArray) question.getJSONArray("answer"));
                    a.setId(question.getString("id"));
                    a.setQuestion(question.getString("question"));
                    a.setST1(((JSONObject) answer.getJSONObject(0)).getString("A"));
                    a.setST2(((JSONObject) answer.getJSONObject(1)).getString("B"));
                    a.setST3(((JSONObject) answer.getJSONObject(2)).getString("C"));
                    if (question.has("url"))
                        a.setImg(question.getString("url"));
                    else
                        a.setImg("");
//lưu vào mảng
                    CONST.exam[i] = a;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(Main2Activity.this, ThiTuDo.class);
            startActivity(intent);

        } else
            //nếu mã đề thi sai thì thông báo cho nguoid dùng và yêu cầu nhập lại
            {

            checkexam("err");
        }
    }

    private void thi(final String id) {
        //đối với các đề thi dc fix săn ở giao diện khi click vào sẽ có giá trị code tương ứng mà ko cần nhập mã đề
        final ProgressDialog progress;
        progress = new ProgressDialog(Main2Activity.this);
        progress.setTitle("Checking");

        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
//tạo hàng đợi xử lý request

        RequestQueue queue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();
        //tạo đối tượng chưa các tham số lên server

        JSONObject text = new JSONObject();
        try {
            text.put("code", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST,
                CONST.url + "check_code_exam", text, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progress.dismiss();
                //gọi hàm xử lý kết quả từ server
                getexam(response, id);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                progress.dismiss();

                Toast.makeText(Main2Activity.this, "Vui long kiem tra lai", Toast.LENGTH_LONG);
                checkexam("err");

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Beare " + CONST.user.getToken());// put your token here
                return headers;
            }
        };
        //thêm vao hầng đợi
        queue.add(jsonOblect);
    }

}
