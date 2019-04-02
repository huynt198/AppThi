package com.example.thi;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class CONST {
    public static User user = new User();
    public  static  String id_exam;
    public static Question[] exam= new Question[50];

    public static String url_test = "http://192.168.42.111:8000/api/";
    public static String url = "http://thionline-test.herokuapp.com/api/";
}
