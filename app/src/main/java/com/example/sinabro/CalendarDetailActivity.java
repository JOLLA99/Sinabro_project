package com.example.sinabro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarDetailActivity extends AppCompatActivity {
    final String url = "http://34.64.77.135:3000/calendar/";
    private static final String TAG="CalendarDetailActivity";
    private EditText name, content;
    public String selected_date;

    private AlarmManager alarmManager;
    private TimePicker timePicker;
    private Button btn;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_detail);

        this.alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        this.timePicker = findViewById(R.id.timePicker);

        findViewById(R.id.btnStart).setOnClickListener(mClickListener);

        btn = (Button)findViewById(R.id.btnGoCal);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("??", "dd");
                Intent intent = new Intent(CalendarDetailActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });


        MainActivity.btn_STOP.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("??", "ff");
                // 알람 중지
                stop();

            }



        });
    }

    //json 통신
    public void calendar_send(final String urls, final String cal_date, final String cal_name, final String cal_content){
        new Thread(){
            @Override
            public void run() {
                try{
                    //수영이가 알려줘야 수정 가능...
                    URL url = new URL(urls);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST"); //전송방식
                    connection.setDoOutput(true);       //데이터를 쓸 지 설정
                    connection.setDoInput(true);        //데이터를 읽어올지 설정

                    OutputStream os = connection.getOutputStream();
                    JSONObject sendData = new JSONObject();
                    sendData.put("scheduleDate", cal_date);
                    sendData.put("scheduleName", cal_name);
                    sendData.put("scheduleContents", cal_content);
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
                    Log.d("받아온 리절트",obj.getString("result"));
                    String selected_result = obj.getString("result");

                    //TODO 로그인이 success 면 화면 전환, 로그인이 fail 이나 error 면 오류처리
                    if(selected_result=="success")
                    {
                        Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CalendarDetailActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        return;

                    }

                }catch (Exception e){

                }

            }
        }.start();


    }


    /* 알람 시작 */
    private void start() {
        // 시간 설정
        Calendar calendar = Calendar.getInstance();

        name= (EditText)findViewById(R.id.editTextTextMultiLine);
        content = (EditText)findViewById(R.id.timetxt);

        String name1 =name.getText().toString();
        String content1 =content.getText().toString();


        calendar.set(Calendar.HOUR_OF_DAY, this.timePicker.getHour());
        calendar.set(Calendar.MINUTE, this.timePicker.getMinute());
        calendar.set(Calendar.SECOND, 0);

        // 현재시간보다 이전이면
        if (calendar.before(Calendar.getInstance())) {
            // 다음날로 설정
            calendar.add(Calendar.DATE, 1);
        }

        // Receiver 설정
        Intent intent = new Intent(this, AlarmReceiver.class);
        // state 값이 on 이면 알람시작, off 이면 중지
        intent.putExtra("state", "on");

        this.pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 알람 설정
        this.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Toast 보여주기 (알람 시간 표시)
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Toast.makeText(this, "Alarm : " + format.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
        selected_date = format.format(calendar.getTime()).toString();

        calendar_send(url, selected_date, name1, content1);
    }

    /* 알람 중지 */
    private void stop() {
        if (this.pendingIntent == null) {
            return;
        }

        // 알람 취소
        this.alarmManager.cancel(this.pendingIntent);

        // 알람 중지 Broadcast
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("state","off");

        sendBroadcast(intent);

        this.pendingIntent = null;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStart:
                    // 알람 시작
                    start();
                    Log.d("dd", "dd");

                    break;



            }
        }
    };
}