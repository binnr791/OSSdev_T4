package com.example.wordmemorization;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//단어 검색 액티비티
public class SearchActivity extends AppCompatActivity {
    boolean isAddble = false; // 단어를 추가해도 되는 상태인지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final TextView word = (TextView)findViewById(R.id.word);
        final TextView desc = (TextView)findViewById(R.id.descript);
        final TextView editbox = (TextView)findViewById(R.id.inputtext);
        Button searchbtn = (Button)findViewById(R.id.searchbtn);
        Button addbtn = (Button)findViewById(R.id.addword);
        addbtn.setEnabled(isAddble);

        //입력상제 클릭시 내용 삭제
        editbox.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                editbox.setText("");
            }
        });

        //검색버튼 클릭
        searchbtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                getNaverEncycAPI(editbox.getText().toString());
            }
        });

        //추가버튼 클릭
        addbtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                if( isAddble )
                    MainActivity.note.put( word.getText().toString().toLowerCase(), desc.getText().toString());
            }
        });

    }

    //API 요청 쓰레드
    private void getNaverEncycAPI(final String keyword){
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {

                    final String str = getNaverSearch(keyword);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Button addbtn = (Button)findViewById(R.id.addword);
                            try {
                                JSONObject jObject = new JSONObject(str);

                                String wordstring = keyword;
                                String descstring = new String();
                                ArrayList<String> descstringlist = new ArrayList<String>();

                                for (int i = 0; i < jObject.length(); i++) {
                                    String str = jObject.getJSONArray("items").getJSONObject(i).getString("title");
                                    if(str.contains("<"))
                                        continue;
                                    if( !descstringlist.contains(str)) {
                                        descstringlist.add(str);
                                        //descstring += descstringlist.size() + ". " + str + "\n";
                                        descstring += str + "\n";
                                    }
                                }

                                TextView word = (TextView) findViewById(R.id.word);
                                TextView desc = (TextView) findViewById(R.id.descript);

                                word.setText(wordstring);
                                desc.setText(descstring);

                                isAddble = true;

                                addbtn.setEnabled(isAddble);
                            } catch (JSONException e) {
                                TextView word = (TextView) findViewById(R.id.word);
                                TextView desc = (TextView) findViewById(R.id.descript);

                                word.setText("백과사전에 없는 단어");
                                desc.setText("다시 검색해 주세요");
                                isAddble = false;
                                addbtn.setEnabled(isAddble);

                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getNaverSearch(String keyword) {

        String clientId = "k3UlWznKK8AjYEpsd9MR"; //애플리케이션 클라이언트 아이디값"
        String clientSecret = "LVV3QCwIjt"; //애플리케이션 클라이언트 시크릿값"

        String text = null;
        try {
            text = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/encyc.json?query=" + text;    // json 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);

        return responseBody;

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }


}
