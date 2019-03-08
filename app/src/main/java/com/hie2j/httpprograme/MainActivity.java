package com.hie2j.httpprograme;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private EditText edit_phone;
    private TextView txt_message;

    private Handler handler;
    private static final int GET_RESULT = 1001;

    private String baseUrl = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_phone = findViewById(R.id.edit_phone);
        txt_message = findViewById(R.id.txt_message);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == GET_RESULT){
                    String html = (String) msg.obj;
                    txt_message.setText(html);
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = edit_phone.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        usesGet(phone);
                    }
                }).start();
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = edit_phone.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        usesPost(phone);
                    }
                }).start();
            }
        });
    }

    private void usesPost(String phone) {
        HttpPost httpPost = new HttpPost(baseUrl);
        HttpClient httpClient = new DefaultHttpClient();
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("mobileCode",phone));
        nameValuePairList.add(new BasicNameValuePair("userID",""));
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String htmlContent = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
            Log.e(TAG,htmlContent);

            Message message = Message.obtain();
            message.what = GET_RESULT;
            message.obj = htmlContent;
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void usesGet(String phone) {
        Log.e(TAG,"phone = " + phone);
        String url = baseUrl.concat("?mobileCode=" + phone).concat("&userID=");
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try{
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int code = httpResponse.getStatusLine().getStatusCode();
            Log.e(TAG,"code = " + code);
            //获取页面内容
            String htmlContent = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            Log.e(TAG, htmlContent);

            Message message = Message.obtain();
            message.what = GET_RESULT;
            message.obj = htmlContent;
            handler.sendMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
