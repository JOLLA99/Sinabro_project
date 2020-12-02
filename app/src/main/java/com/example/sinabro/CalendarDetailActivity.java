package com.example.sinabro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class CalendarDetailActivity extends AppCompatActivity {
    final String url = "https://webhook.site/b2f72fe2-41ca-429a-a2e6-bdc3fd0d4c05";
    private static final String TAG="CalendarDetailActivity";

    private TextView theDate, theTime;
    private Button btonGoCalendar, btnTime;
    private EditText cal_name, cal_content;
    TextClock textClock;
    String selected_date="";
    String name, content;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_detail);
        theDate = (TextView)findViewById(R.id.date);
        btonGoCalendar = (Button)findViewById(R.id.gotocal);
        btnTime = (Button)findViewById(R.id.btn_time);
        theTime = (EditText)findViewById(R.id.timetxt);

        Intent incomingintent = getIntent();
        String date = incomingintent.getStringExtra("date");
        theDate.setText(date);


        btonGoCalendar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                cal_name = (EditText)findViewById(R.id.editTextTextPersonName2);
                name = cal_name.getText().toString();
                cal_content = (EditText)findViewById(R.id.editTextTextPersonName);
                content = cal_content.getText().toString();
                calendar_send(url, name, content, selected_date);
                Intent intent = new Intent(CalendarDetailActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                if(view==btnTime){
                    final Calendar c = Calendar.getInstance();
                    int mHour = c.get(Calendar.HOUR);
                    int mMin = c.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(CalendarDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            theTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                            selected_date = String.format("%02d:%02d", hourOfDay, minute);
                        }
                    },mHour, mMin, false);
                    timePickerDialog.show();
                }
            }
        });


    }

    public void calendar_send(final String urls, final String cal_name, final String cal_content, final String selected_date){
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
                    sendData.put("id", cal_name);
                    sendData.put("passwd", cal_content);
                    sendData.put("name", selected_date);
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
                        Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
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

    //알람 설정
    public void setAlarm(){

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("no", alarmIdx);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmIdx, intent, 0);

        //알람 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        switch (month){
            case 1:
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                break;
            case 2:
                calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
                break;
            case 3:
                calendar.set(Calendar.MONTH, Calendar.MARCH);
                break;
            case 4:
                calendar.set(Calendar.MONTH, Calendar.APRIL);
                break;
            case 5:
                calendar.set(Calendar.MONTH, Calendar.MAY);
                break;
            case 6:
                calendar.set(Calendar.MONTH, Calendar.JUNE);
                break;
            case 7:
                calendar.set(Calendar.MONTH, Calendar.JULY);
                break;
            case 8:
                calendar.set(Calendar.MONTH, Calendar.AUGUST);
                break;
            case 9:
                calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
                break;
            case 10:
                calendar.set(Calendar.MONTH, Calendar.OCTOBER);
                break;
            case 11:
                calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
                break;
            case 12:
                calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                break;

        }
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long aTime = System.currentTimeMillis();
        long bTime = calendar.getTimeInMillis();

        //하루의 시간을 나타냄
        long interval = 1000 * 60 * 60  * 24;

        //만일 내가 설정한 시간이 현재 시간보다 작다면 알람이 바로 울려버리기 때문에 이미 시간이 지난 알람은 다음날 울려야 한다.
        while(aTime>bTime){
            bTime += interval;
        }

        //알람 매니저를 통한 반복알람 설정
        alarmManager.setRepeating(AlarmManager.RTC, bTime, interval, pendingIntent);

    }

    public void cancelAlarm(){

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, CalendarDetailActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmIdx, intent, 0);
        alarmManager.cancel(pendingIntent);

    }





}
