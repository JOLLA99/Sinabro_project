package com.example.sinabro;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private TextView textView1, textView2, textView3, textView4, textView5, textView6;
    private Button button1, button2, button3;
    Intent passedIntent;
    String pointData;
    public static int point;
    private ImageView imageView;
    ArrayList<String> data_pick;


    final String url = "http://34.64.77.135:3000/tree/"; //서버 주소
    final String url_w = "http://34.64.77.135:3000/weather/"; //날짜 서버 주소

    //int rain=0; //강수확률 저장할
    int weatherData = 0; // 날씨 판별 데이터 저장

    SavedUser savedUser = new SavedUser();
    String UserID = savedUser.getID();


    String date;
    String weatheri_temp;
    String weatheri_rainfall;
    String kor_temp;
    String kor_rainfall;
    String todoText;
    String todoDate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 건들이지 말자!!!!!
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        passedIntent = getIntent();
        processCommand(passedIntent);

        getData(url, UserID);




        //날씨 판별 데이터에 따라 이미지 바꿔 출력
        if (weatherData == 3) //비올때
            imageView.setImageResource(R.drawable.rainy1);
        else if (weatherData == 2) // 흐릴 때(올수도 있고 안올수도 있고)
            imageView.setImageResource(R.drawable.cloudy1);
        else if (weatherData == 1) //맑을 때
            imageView.setImageResource(R.drawable.sunny1);

        textView1 = findViewById(R.id.weatheri_temp); //웨더아이 기온
        textView2 = findViewById(R.id.weatheri_rainfall); //웨더아이 강수확률
        textView3 = findViewById(R.id.kor_temp); //기상청 기온
        textView4 = findViewById(R.id.kor_rainfall); //기상청 강수량
        textView5 = findViewById(R.id.todoText); // 일정 미리보기 텍스트
        textView6 = findViewById(R.id.todoDate); // 일정 날짜 텍스트

        button1 = findViewById(R.id.logout_button); // 로그아웃 버튼
        button2 = findViewById(R.id.calendar_button); //캘린터 버튼
        button3 = findViewById(R.id.btn_tree);
        imageView = findViewById(R.id.weather_img1);



        if (AlarmService.flag) {
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
            alert_confirm.setMessage("시나브로가 알려드린 날씨와 함께 재미있게 즐기셨나요?").setCancelable(false).setPositiveButton("잘 맞았어요",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 'YES'
                            SavedUser savedUser = new SavedUser();
                            getPoint(url, savedUser.getID());

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

        // 트리 버튼 누르면 트리 화면으로 이동
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GrowthActivity.class); // 여기 캘린더액티비티로 바꿔주세용~!
                startActivity(intent);
            }
        });

    }

    public int getWeather(int rain) {
        //강수확률에 따라 3단계로 구분
        if (rain >= 60) // 강수확률 60퍼 이상일때
            weatherData = 3;
            //imageView.setImageResource(R.drawable.rainy1);
        else if (rain >= 40 && rain < 60) // 40~59
            weatherData = 2;
            //imageView.setImageResource(R.drawable.cloudy1);
        else // 40 미만
            weatherData = 1;
        //imageView.setImageResource(R.drawable.sunny1);
        return weatherData; // 판별 데이터 넘김
    }


    //나무 테이블 값 보냄
    //서버로부터 포인트 받는함수
    public void getPoint(final String urls, final String id){
        new Thread(){
            @Override
            public void run() {
                try{

                    URL url = new URL(urls);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST"); //전송방식
                    connection.setDoOutput(true);       //데이터를 쓸 지 설정
                    connection.setDoInput(true);        //데이터를 읽어올지 설정

                    OutputStream os = connection.getOutputStream();
                    JSONObject sendData = new JSONObject();

                    //아이디 전송
                    sendData.put("id", id);
                    Log.d("제이슨 오브젝트",sendData.toString());
                    String body = sendData.toString();
                    // Request Body에 Data 셋팅.
                    os.write(body.getBytes("utf-8"));

                    // Request Body에 Data 입력.
                    os.flush();

                    // OutputStream 종료.
                    os.close();

                    InputStream is = connection.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String result;
                    while((result = br.readLine())!=null){
                        sb.append(result+"\n");
                    }

                    connection.disconnect();

                    result = sb.toString();

                    Log.d("리절트",result);
                    JSONObject obj = new JSONObject(result);
                    Log.d("받아온 리절트",obj.getString("result")); //성공 유무

                    String selected_result = obj.getString("result");
                    pointData = obj.getString("treeCount"); // 트리경험치

                    if(selected_result.equals("success")) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "서버 연결 성공", Toast.LENGTH_SHORT).show();

                                point = Integer.parseInt(pointData); //int로 형변환 해서 point에 저장
                            }
                        });

                    }




                }catch (Exception e){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        }.start();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        processCommand(passedIntent);

        super.onNewIntent(intent);
    }

// 이와 같이 모든 경우에 서비스로부터 받은 인텐트가 처리 될 수 있도록한다.
// 이제 processCommand() 메서드 정의.

    private void processCommand(Intent intent) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            String name = intent.getStringExtra("name");

            Toast.makeText(this, "서비스로부터 전달받은 데이터: " + command + ", " + name, Toast.LENGTH_LONG).show();






        }
    }

    public void getData(final String urls, final String id) {
        ArrayList<String> data = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urls);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST"); //전송방식
                    connection.setDoOutput(true);       //데이터를 쓸 지 설정
                    connection.setDoInput(true);        //데이터를 읽어올지 설정

                    OutputStream os = connection.getOutputStream();
                    JSONObject sendData = new JSONObject();
                    //샘플데이터
                    sendData.put("id", id);

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


                    data.add(obj.getString("date").toString());
                    data.add(obj.getString("naverPrecipitation").toString());
                    // double test = Double.parseDouble(data[1]);
                    //double_data[0] = test;
                    //System.out.println("웨더아이 강수확률 double로 형변환 :"+ test);
                    data.add(obj.getString("naverTemp").toString());
                    data.add(obj.getString("govPrecipitation").toString());
                    data.add(obj.getString("govTemp").toString());


                    int a = data.size();
                    for (int index = 0; index < a; index++) {
                        System.out.println(data.get(index));
                    }

                    data_pick = new ArrayList<>();

                    data_pick.addAll(data);


                    int b = data_pick.size();
                    for (int index = 0; index < a; index++) {
                        System.out.println(data_pick.get(index));
                    }

                    date =data_pick.get(0).toString();
                    weatheri_rainfall = data_pick.get(1).toString();
                    weatheri_temp = data_pick.get(2).toString();
                    kor_rainfall = data_pick.get(3).toString();
                    kor_temp = data_pick.get(4).toString();

                    System.out.println(weatheri_temp);


                    //강수확률에 따라 흐림/비옴/맑음 판별
                    int rain = Integer.parseInt(weatheri_rainfall);
                    weatherData = getWeather(rain);

                    //받아온 날짜 화면에 표시
                    textView1.setText(weatheri_temp+"℃");
                    System.out.println(weatheri_temp);
                    textView2.setText(weatheri_rainfall+" %");
                    System.out.println(weatheri_rainfall);
                    textView3.setText(kor_temp+"℃");
                    System.out.println(kor_temp);
                    textView4.setText(kor_rainfall+" mm");
                    System.out.println(kor_rainfall);

                    CalendarDetailActivity detailActivity = new CalendarDetailActivity();

                    //받아온 일정 화면에 표시
                    textView5.setText(detailActivity.scheduleName);
                    System.out.println(detailActivity.scheduleName);
                    //System.out.println(todoText);
                    textView6.setText(detailActivity.scheduleName);
                    System.out.println(detailActivity.scheduleDate);



                } catch (Exception e) {
                }

            }
        }.start();


    }




}
