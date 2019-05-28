package com.example.thi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.Database;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    UserHelper helper;
    Cursor model = null;
    //ngắt kết nối scdl sau khi sử dụng
    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //gọi hàm để đọc csdl
        helper = new UserHelper(this);
        //lấy dữ liệu người dùng trong csdl
        model = helper.getUser();
        //kiểm tra xem người dùng đã đăng nhập hay chưa, nếu đăng nhập  rồi thì chuyển qua activity khác, nếu chưa thì yêu cầu đăng nhập
        if (!model.moveToNext()) {
            Button save = (Button) findViewById(R.id.save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //mở dialog loading
                    final ProgressDialog progress;
                    progress = new ProgressDialog(MainActivity.this);
                    progress.setTitle("Loading");
                    progress.setMessage("Wait while loading...");
                    progress.setCancelable(false);
                    progress.show();
                    Log.d("test", "-------------------------------------------------------");
                    //tạo RequesQueue quản lý các request đến server
                    RequestQueue que;
                    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
                    Network network = new BasicNetwork(new HurlStack());
                    que = new RequestQueue(cache, network);
                    que.start();
                    //tạo JSON chứa thông tin để gủi lên server
                    JSONObject jsonBody = new JSONObject();
                    //đẩy dữ liệu vào trong JSON
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
                    //tạo yêu cầu lên server
                    JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("ereeeeeee", response.toString());
                            try {
                                //Xử lý kết quả trả về
                                //kết quả trả về dạng:  {
                                // code:number,
                                // data:token,
                                // name: name,
                                // email:email
                                // }
                                if (response.get("data").toString().length() > 0) {
                                    CONST.user.setToken(response.get("data").toString());
                                    CONST.user.setName(response.get("name").toString());
                                    CONST.user.setEmail(((TextView) findViewById(R.id.username)).getText().toString());
                                    Log.d("res", "thanh cong");
                                    Toast.makeText(MainActivity.this, "dang nhap thanh cong", Toast.LENGTH_LONG);
                                    //sau khi đăng nhập thành công thì gọi hàm luu vào csdl
                                    helper.insert(CONST.user);
                                    //chuyển activity
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
                        //thông báo lỗi kết nối nếu có
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.dismiss();
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                            alertDialogBuilder.setCancelable(true);
                            alertDialogBuilder.setMessage("Loi ket noi");
                            alertDialogBuilder.show();

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            final Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Beare ");//put your token here
                            return headers;
                        }
                    };
                    //thêm yêu cầu vào hàng đơi
                    que.add(jsonOblect);


                }
            });
        } else {
            System.out.println(model.getString(4) + "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
            //lấy dữ liệu trong bộ nhớ để sử dụng
            CONST.user.setName(model.getString(1));
            CONST.user.setToken(model.getString(4));
            CONST.user.setEmail(model.getString(2));
            //chuyển sang activity tiếp theo và hủy activity hiện tại
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
            finish();

        }
    }


}
