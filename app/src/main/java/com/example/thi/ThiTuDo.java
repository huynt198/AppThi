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
    //tạo các biến lưu trữ thoiaf gian làm bài
    Integer a2;
    Integer sec = 59000;
    Integer min = 89;
    Question que = new Question();
    //tạo mangt lưu trữ các button ứng với câu hỏi tương ứng
    private View[] a = new View[50];
    private TextView cau;
    int flag = 0;
    //tạo mảng chứa các câu trả lới của thí sinh
    JSONArray result = new JSONArray();
    Boolean doubleBackToExitPressedOnce = false;

    //nhấn 2 lần bạck để thoát khỏi activity
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
        //khởi tạo giá trả lời mặc định của tất cả các câu hỏi và lưu trữ các giá trị của nút button(dùng khi muốn chuyển câu hỏi)
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
        //xác nhận trước khi làm bài
        final AlertDialog.Builder a = new AlertDialog.Builder(this);
        a.setTitle("Xac nhan");
        a.setCancelable(false);
        a.setMessage("Ban co 90 phut de lam bai!\nBan chi nop bai sau 60 phut.");
        final TextView as = (TextView) findViewById(R.id.time);
        //khởi tạo câu đầu tiên hiển thi khi load xong đề
        add((View) findViewById(R.id.a0));
        //Nếu người dùng ấn nút gửi bài
        ((Button) findViewById(R.id.send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // lấy hết các câu trả lời hiện tại
                select();
                //gọi hàm xử lý yêu cầu gủi bài
                send();
                flag = 9;
            }
        });

        a.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nếu người dùng săn sàng làm bài
                //tạo countdown hiển thì thời gian còn lại để làm bài
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
                        //Nếu hết thời gian sẽ tự động nộp bài
                        if (flag == 0)
                            send();
                    }
                }.start();

            }
        });
        //hiển thị thông báo trước khi làm bài
        a.show();

    }

    private void send() {
        System.out.println(result.toString());
        //kiểm tra thời gian làm bài nếu chưa đến 2/3 thời gian sẽ không cho nộp bài
        if (min >= 30) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ThiTuDo.this);
            alertDialogBuilder.setCancelable(true);
            // alertDialogBuilder.setIcon(R.drawable.ic_menu_send);
            alertDialogBuilder.setMessage("Chua den gio nop bai!");
            alertDialogBuilder.show();
        } else {
            final ProgressDialog progress;
            //hiển thị màn hình check kết quả
            progress = new ProgressDialog(ThiTuDo.this);
            progress.setTitle("Gui ket qua");
            progress.setMessage("Wait while checking...");
            progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
            progress.show();
            //tạo hảng đợi xử lý request đến server
            RequestQueue que;
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            que = new RequestQueue(cache, network);
            que.start();
            //truyền các tham số vào trong yếu cầu
            JSONObject re = new JSONObject();
            try {
                re.put("id_exam", CONST.id_exam);
                re.put("result", (Object) result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //tạo yêu cầu đến server
            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, url + "check_result", re,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.cancel();
                            //xử lý keestq quả  trả về
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
                                //sau khi làm  bài xong thì chuyển về main activity và hủy activity hiện tại
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ThiTuDo.this, Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            //hiển thị kết quả
                            aw.show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.cancel();

                    AlertDialog.Builder aw = new AlertDialog.Builder(ThiTuDo.this);
                    //nếu có lỗi thì thông báo cho người dùng và cho phép nguoiwd dùng gửi lại yếu cầu
                    aw.setTitle("Loi ket noi");
                    aw.setMessage("Vui long kiem tra ket noi. Thu lai?");
                    aw.setCancelable(false);
                    aw.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //gủi lại yêu cầu
                            send();
                        }
                    });
                    aw.show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    //thêm token vào headres nếu yêu cầu xác the=ực
                    headers.put("Authorization", "Beare " + CONST.user.getToken());
                    return headers;
                }
            };
            //thêm yêu cầu vào hang đợi xử lý
            que.add(jsonOblect);
        }
    }

    //hiển thi câu hỏi khi người dùng ấn vào câu tương ứng
    public void add(View view) {
        //cập nhật giá trị của câu hỏi trước đó
        select();
        //khởi tạo lại giá trị của RadioGroup
        ((RadioGroup) findViewById(R.id.ans)).clearCheck();
        //tìm button tường ứng với câu hỏi mà người dùng click vào
        a2 = findIndex(a, view);

        Integer a3 = a2 + 1;
        cau.setText("Cau " + a3.toString() + ":");
        que = CONST.exam[a2];
        //nếu câu hỏi ko có hình ảnh thì ImageView sẽ ẩn
        if (!que.getImg().equals("")) {
            ((ImageView) findViewById(R.id.image)).setVisibility(View.VISIBLE);
            Picasso.with(this).load(que.getImg()).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into((ImageView) findViewById(R.id.image));
        } else
            //Nếu image có hình ảnh thì hiển thị hinh ảnh
            ((ImageView) findViewById(R.id.image)).setVisibility(View.GONE);
        //cập nhật trạng thái của câu hỏi và các đáp án trả lời lên view
        ((TextView) findViewById(R.id.message)).setText(que.getQuestion());
        ((RadioButton) findViewById(R.id.A)).setText("A :" + que.getST1());
        ((RadioButton) findViewById(R.id.B)).setText("B :" + que.getST2());
        ((RadioButton) findViewById(R.id.C)).setText("C :" + que.getST3());
        checked();

    }

    //nếu người dung chọn câu hỏi đã làm rôi thì hiển thị câu hỏi và check vào đáp án đó
    private void checked() {
        //dùng mảng để duyệt xem người dùng đã  làm câu đấy hay chưa
        for (int i = 0; i < result.length(); i++) {
            try {
                JSONObject ed = (JSONObject) result.getJSONObject(i);
                //nếu câu hỏi đã tồn tại đáp án
                if (ed.get("id").equals(que.getId())) {
                    //nếu đáp án vẫn là đáp án mặc định thì coi như chưa làm
                    if (ed.get("answer").equals("")) {

                    } else {
                        //nếu đáp án khác đán án mặc định thì cần check đến đúng đáp án đó
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

        //check đáp án mà người dùng chọn
        RadioGroup ans = (RadioGroup) findViewById(R.id.ans);
        switch (ans.getCheckedRadioButtonId()) {
            //khi người dùng chọn đáp án thì màu sắc ở ô câu hỏi tương ứng sẽ thay đổi để người dùng biết là mình
            //đã làm câu này
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
        //thêm lựa chọn của người dùng vào mảng result vioiws vị trí tương ứng
        //nếu người dùng lựa chọn thì cập nhật lại giá trị mặc định trong mảng result
        //nếu người dùng không lựa chọn thì  bỏ qua
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

    //tìm View(button) khi người dùng bấm vào câu hỏi
    private int findIndex(View[] a, View view) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == view)
                return i;
        }
        return -1;
    }
}
