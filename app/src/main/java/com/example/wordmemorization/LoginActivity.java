package com.example.wordmemorization;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    String serveraddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView id = (TextView)findViewById(R.id.username);
        TextView pw = (TextView)findViewById(R.id.password);
        TextView msgview = (TextView)findViewById(R.id.loginmsg);
        Button btn  = (Button)findViewById(R.id.login);

        btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                TextView id = (TextView)findViewById(R.id.username);
                TextView pw = (TextView)findViewById(R.id.password);
                Login( id.getText().toString(), pw.getText().toString() );
            }
        });
    }

    private void Login(final String id, final String pw){

        serveraddr = getString(R.string.serverip)+":5000";

        if( id.length() < 4 || pw.length() < 4){
            TextView msgview = (TextView)findViewById(R.id.loginmsg);
            msgview.setText("아이디와 비밀번호는 4자 이상 입력해 주세요");
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String message = new String();
                JSONObject obj = null;

                try {
                    URL url = new URL(serveraddr+"/login"); //in the real code, there is an ip and a port
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id", id);
                    jsonParam.put("password", pw);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        builder.append(str + "\n");
                    }
                    obj =  new JSONObject(builder.toString());

                    message = obj.getString("msg");
                    Log.i("message", message);

                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView msgview = (TextView)findViewById(R.id.loginmsg);
                        msgview.setText("");
                    }
                });

                if(message.compareTo("succeed") == 0){  //로그인 성공

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView msgview = (TextView)findViewById(R.id.loginmsg);
                            msgview.setText("로그인 중 입니다.");
                        }
                    });


                    try {

                        //응답이 정상적으로 처리 되지 않으면 아무것도 안함
                        if(obj == null) return;

                        //URL url = new URL(serveraddr + "/note/"+obj.getString("accountID")); //in the real code, there is an ip and a port
                        URL url = new URL(serveraddr + "/note/"+"1"); //in the real code, there is an ip and a port
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Accept","application/json");

                        int code = conn.getResponseCode();
                        //정상연결
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                            InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                            BufferedReader reader = new BufferedReader(tmp);
                            StringBuilder builder = new StringBuilder();
                            String str;
                            while ((str = reader.readLine()) != null) {
                                builder.append(str + "\n");
                            }
                            JSONArray noteArray = new JSONArray(builder.toString());

                            MainActivity.note.clear();
                            for ( int i = 0 ; i < noteArray.length() ; ++i){
                                JSONObject o = (JSONObject) noteArray.get(i);
                                MainActivity.note.put(o.getString("word"), o.getString("description"));
                            }

                            reader.close();


                            MainActivity.setIsLogined(true);
                            Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }



                }else if( message.compareTo("not correct password") == 0){ //비번 불일치
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView msgview = (TextView)findViewById(R.id.loginmsg);
                            msgview.setText("비밀번호가 일치하지 않습니다.");
                        }
                    });


                }else if( message.compareTo("id not found") == 0){  // 계정없음(계정생성)
                    try {
                        URL url = new URL(serveraddr + "/create"); //in the real code, there is an ip and a port
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("PUT");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.connect();

                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("id", id);
                        jsonParam.put("password", pw);

                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        os.writeBytes(jsonParam.toString());

                        os.flush();
                        os.close();

                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        Log.i("MSG", conn.getResponseMessage());

                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuilder builder = new StringBuilder();
                        String str;

                        while ((str = reader.readLine()) != null) {
                            builder.append(str + "\n");
                        }

                        obj = new JSONObject(builder.toString());
                        Log.i("message", obj.toString());
                        conn.disconnect();


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView msgview = (TextView)findViewById(R.id.loginmsg);
                                msgview.setText("계정이 생성되었습니다.\n 다시 로그인 해 주세요");
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else  //뭔가 문제가 있음
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView msgview = (TextView)findViewById(R.id.loginmsg);
                            msgview.setText("일시적인 문제가 있습니다. 나중에 다시 시도해 주세요");
                        }
                    });

            }
        });
        thread.start();
    }

}