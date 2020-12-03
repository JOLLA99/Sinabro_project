package com.example.sinabro;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private TextView textView1,textView2,textView3,textView4;
    private Button button1,button2;
    public static Button btn_STOP;


    /*
    String date=null;
    String weatheri_temp=null;
    String weatheri_rainfall=null;
    String kor_temp=null;
    String kor_rainfall=null; */


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 건들이지 말자!!!!!
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_STOP = findViewById(R.id.btn_stop);

        if(AlarmService.flag)
        {
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
            alert_confirm.setMessage("시나브로가 알려드린 날씨와 함께 재미있게 즐기셨나요?").setCancelable(false).setPositiveButton("잘 맞았어요",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 'YES'
                        }
                    }).setNegativeButton("맞지 않았어요",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 'No'
                            return;
                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();

        }



        /*task Task = new task();
        Task.getData();
        textView1.setText(Task.weatheri_temp);
        textView2.setText(Task.weatheri_rainfall);
        textView3.setText(Task.kor_temp);
        textView4.setText(Task.kor_rainfall);*/

        textView1 = (TextView) findViewById(R.id.weatheri_temp); //웨더아이 기온
        textView2 = (TextView) findViewById(R.id.weatheri_rainfall); //웨더아이 강수확률
        textView3 = (TextView) findViewById(R.id.kor_temp); //기상청 기온
        textView4 = (TextView) findViewById(R.id.kor_rainfall); //기상청 강수량
        button1 = (Button) findViewById(R.id.logout_button); // 로그아웃 버튼
        button2 = (Button) findViewById(R.id.calendar_button); //캘린터 버튼


        //로그아웃 버튼 누르면 로그인 화면으로 이동
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // 캘린더 버튼 누르면 캘린더 화면으로 이동
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class); // 여기 캘린더액티비티로 바꿔주세용~!
                startActivity(intent);
            }
        });

    }

    /*public class task {

        String[] data = new String[5];

        String date = data[0];
        String weatheri_rainfall = data[1];
        String weatheri_temp = data[2];
        String kor_rainfall = data[3];
        String kor_temp = data[4];


        public String[] getData() {

            //String[] data = new String[5];

            new Thread() {
                @Override
                public void run() {
                    try {

                        //getData("http://34.64.77.135:3000/weather/");
                        URL url = new URL("http://34.64.77.135:3000/weather/");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST"); //전송방식
                        connection.setDoOutput(true);       //데이터를 쓸 지 설정
                        connection.setDoInput(true);        //데이터를 읽어올지 설정

                        OutputStream os = connection.getOutputStream();
                        JSONObject sendData = new JSONObject();
                        //샘플데이터
                        sendData.put("date", "12월01일 14:51 갱신");
                        sendData.put("naverPrecipitation", "30");
                        sendData.put("naverTemp", "1");
                        sendData.put("govPrecipitation", "20");
                        sendData.put("govTemp", "2.5");

                        Log.d("제이슨 오브젝트", sendData.toString());
                        String body = sendData.toString();
                        // Request Body에 Data 셋팅.
                        os.write(body.getBytes("utf-8"));

                        // Request Body에 Data 입력.
                        os.flush();

                        // OutputStream 종료.
                        os.close();

                        InputStream is = connection.getInputStream();
                        StringBuilder sb = new StringBuilder();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        String result;
                        while ((result = br.readLine()) != null) {
                            sb.append(result + "\n");
                        }

                        connection.disconnect();

                        result = sb.toString();


                        Log.d("리절트", result);
                        JSONObject obj = new JSONObject(result);
                        Log.d("받아온 리절트", obj.getString("result"));

                        data[0] = obj.getString("date");
                        data[1] = obj.getString("naverPrecipitation");
                        data[2] = obj.getString("naverTemp");
                        data[3] = obj.getString("govPrecipitation");
                        data[4] = obj.getString("govTemp");


                    } catch (Exception e) {

                    }

                }
            }.start();

            return data;
        }


    }*/
}

