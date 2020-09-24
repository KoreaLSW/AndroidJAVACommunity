package com.sangwoo.simplecommunity.ShootingGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.sangwoo.simplecommunity.Activity.LoginActivity;
import com.sangwoo.simplecommunity.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GameView extends View implements SensorEventListener {

    Context context;
    int width, height;
    Bitmap back1, back2, unit;
    int back1_y, back2_y;
    int unitW, unitH, unitX, unitY;
    Canvas canvas;

    // 토끼 이미지 관련
    Bitmap[] rabbit = new Bitmap[2];
    int imageIndx;
    int rabbitX = 100;
    int rabbitY = 100;
    int rabbitH, rabbitW;
    int speedX = 5;
    int speedY = 5;
    int MaxSpeedCheck = 0;

    //센서관련
    SensorManager sensorM;

    int h_int;

    // 미사일 관련
    Bitmap missile;

    // 미사일 객체를 ArrayList저장
    ArrayList<Missile> missList = new ArrayList<>();
    //미사일의 크기, 위치값
    int missileW, missileH, missileX, missileY;

    int Score = 0;

    //게이지 객체와 게이지의 위치, 크기
    Gauge gauge;
    int gaugeX, gaugeY, gaugeW, gaugeH;
    boolean finish; // 게임이 종료된 후 다이얼로그가 중복되서 나오지 않도록 하기 위한 변수

    boolean youdie = false; // 토끼와 유닛이 충돌했을때 죽은지 살았는지 확인하는 변수
    boolean handdie = true; // 손가락을 떗을떄 죽었는지 확인하는 변수
    boolean GameStartBoolean = false; // 게임 스타트 변수
    public GameView(Context context) {
        super(context);

        this.context = context;
        //현재 뷰의 크기를 알아낸다.
        WindowManager wm = (WindowManager)context.getSystemService( Context.WINDOW_SERVICE );
        //디스플레이의 일반적인 정보를 담을수 있도록 만들어진 클래스
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        // 현재 뷰(휴대폰의 화면크기)의 크기를 변수에 담는다.
        width = dm.widthPixels;
        height = dm.heightPixels;

        // 필요한 이미지 로드
        back1 = BitmapFactory.decodeResource(getResources(), R.mipmap.space);
        back2 = BitmapFactory.decodeResource(getResources(), R.mipmap.space);
        unit = BitmapFactory.decodeResource(getResources(), R.mipmap.gunship);
        rabbit[0] = BitmapFactory.decodeResource(getResources(), R.mipmap.rabbit);
        rabbit[1] = BitmapFactory.decodeResource(getResources(), R.mipmap.rabbit2);
        missile = BitmapFactory.decodeResource(getResources(), R.mipmap.missile);

        // 배경 1, 2의 y좌표값
        back1_y = 0;
        back2_y = -height; // 휴대폰의 높이만큼 올라가야 하기때문에 -height로 설정한다.

        StartAlertDialog();

        init();
        initSensor();
        initGauge();

            handler.sendEmptyMessage(0);
            handler2.sendEmptyMessage(0);
            handler3.sendEmptyMessage(0);
    }// 생성자

    //에너지 초기화
    private void initGauge(){

        //게이지 너비
        gaugeW = (width / 10) * 9;
        //게이지 높이
        gaugeH = height / 20;

        // 게이지 시작위치
        gaugeX = (width - gaugeW) / 2;
        gaugeY = gaugeX;

        //게이지 객체
        gauge = new Gauge(gaugeX, gaugeY, gaugeW, gaugeH);

        gauge.initGauge();

        //에너지 감소량 설정
        gauge.setStep( 30 );
    }//initGauge()

    private void initSensor(){

        sensorM = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensorM.registerListener(this,
                                            sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                            SensorManager.SENSOR_DELAY_GAME);

    }//initSensor()

    //초기화
    private void init(){

        // 배경과 비행기 등의 사이즈를 화면에 맞게 재설정
        back1 = Bitmap.createScaledBitmap(back1, width, height + 15, false);
        back2 = Bitmap.createScaledBitmap(back2, width, height + 15, false);

        rabbit[0] = Bitmap.createScaledBitmap(rabbit[0], width/6, height/6, false);
        rabbit[1] = Bitmap.createScaledBitmap(rabbit[1], width/6, height/6, false);

        rabbitW = rabbit[0].getWidth(); //토끼이미지의 너비
        rabbitH = rabbit[0].getHeight(); //토끼이미지의 높이

        missileW = missile.getWidth(); // 원본 미사일 이미지의 너비
        missileH = missile.getHeight(); // 원본 미사일 이미지의 높이

        unitW = unit.getWidth(); // 원본 이미지의 너비
        unitH = unit.getHeight(); // 원본 이미지의 높이

        unitX = width / 2;
        unitY = height - 300;

    }// init();

    //게임 진행 핸들러
    Handler handler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {

            if(GameStartBoolean) {
                h_int++;
                if (h_int % 20 == 0) {
                    imageIndx++;
                    if (imageIndx == 2) {
                        imageIndx = 0;
                    }
                }

                invalidate(); // 화면갱신 - onDraw();
            }
            handler.sendEmptyMessageDelayed(0, 10);


        }
    };

    //자동 미사일 발사
    Handler handler2 = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            if(GameStartBoolean) {
                Missile m1 = new Missile(unitX, unitY);
                missList.add(m1);
                invalidate(); // 화면갱신 - onDraw();
            }
            handler2.sendEmptyMessageDelayed(0, 100);


        }
    };

    //토끼의 속도가 빨라진다
    Handler handler3 = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {

            if (MaxSpeedCheck < 35) {
                if (GameStartBoolean) {
                    if (speedX < 0) {
                        speedX -= 1;

                    } else if (speedX > 0) {
                        speedX += 1;
                    }

                    if (speedY < 0) {
                        speedY -= 1;
                    } else if (speedY > 0) {
                        speedY += 1;
                    }
                    Log.d("SpeedCheck", speedX + " / " + speedY + " / " + MaxSpeedCheck);
                    invalidate(); // 화면갱신 - onDraw();
                }
            }
            MaxSpeedCheck++;
            handler3.sendEmptyMessageDelayed(0, 1000);
        }

    };

    //캔버스에 이미지를 추가하는 메서드
    private void progressState(){

        // 배경화면
        canvas.drawBitmap(back1,  0, back1_y, null);
        canvas.drawBitmap(back2,  0, back2_y, null);

        //에너지 그리기
        // canvas.drawBitmap(gauge.imgGauge, gauge.x, gauge.y , null);

        // 토끼
        canvas.drawBitmap(rabbit[imageIndx], rabbitX, rabbitY, null);

        // 비행기
        canvas.drawBitmap(unit, unitX, unitY, null);


        Paint p = new Paint();
        p.setTextSize(100);
        p.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(Score), gaugeX, gaugeY + 50, p);

        // 미사일을 그려준다.
        for(int i = 0; i < missList.size(); i++) {
            Missile ms = missList.get(i);
            canvas.drawBitmap(missile, ms.x, ms.y, null);
        }

        scrollBack();
        doRabbit();
        checkMissile();
        MissileCrash();
        UnitCrash();
    }//progressState()

    // 토끼 이미지를 움직이는 메서드
    private void doRabbit(){

        rabbitX += speedX;
        rabbitY += speedY;

        // 벽에 부딛혔을 때 방향전환
        if(rabbitX <= 0){
            speedX *= -1;
        }else if(rabbitX >= width - rabbitW){
            speedX *= -1;
        }

        if(rabbitY <= 0){
            speedY *= -1;
        }else if(rabbitY >= height - rabbitH){
            speedY *= -1;
        }

    }// doRabbit()

    // 미사일과 토끼이미지의 충돌체크 메서드
    private void MissileCrash(){

        //모든 미사일 객체는 x좌표를 가지고 있다.
        //적 기체 또한 좌표를 가지고 있기 때문에 미사일의 좌표와 적 기체의 영역을 비교하여 충돌을 알아낼수 있다.
        for( int i = 0; i < missList.size(); i++){
            Missile ms = missList.get(i);

            if(ms.x > rabbitX && ms.x < rabbitX + rabbitW && ms.y > rabbitY && ms.y < rabbitY + rabbitH){
                Score += 10;
                missList.remove(ms);

                /*if( gauge.isTimeout() ){
                    if(!finish){
                        finish = true;

                        // 게임종료시 다이얼로그 호출
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setMessage("restart?");
                        dialog.setTitle("GameOver");

                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //개임 재시작
                                Intent i = new Intent(context, BackScrollActivity.class);
                                context.startActivity(i);
                                ((Activity)context).finish();
                            }
                        });

                        dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((Activity)context).finish();
                            }
                        });

                        //다이얼로그를 휴대폰의 뒤로가기 버튼으로 취소하지 못하게 하는 속성
                        dialog.setCancelable(false);

                        dialog.show();
                    }
                }//if(gauge.isTimeout())*/

            }
        }//for

    }//crash()

    // 토끼와 유닛이 충돌했는지 체크하는 메서드
    private void UnitCrash(){

            if(unitX > rabbitX && unitX < rabbitX + rabbitW && unitY > rabbitY && unitY < rabbitY + rabbitH){
                youdie = true;
                handdie = false;
                if( youdie ){
                    if(!finish){
                        finish = true;
                        GameStartBoolean = false;
                        // 게임종료시 다이얼로그 호출
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setMessage("안타깝게도 죽었습니다. \n 다시 시작 하시겠습니까? \n(점수는 저장됩니다.)");
                        dialog.setTitle("GameOver");

                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //개임 재시작
                                Intent i = new Intent(context, BackScrollActivity.class);
                                context.startActivity(i);
                                ((Activity)context).finish();
                            }
                        });

                        dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("점수확인", Score + "");
                                ((Activity)context).finish();
                            }
                        });

                        //다이얼로그를 휴대폰의 뒤로가기 버튼으로 취소하지 못하게 하는 속성
                        dialog.setCancelable(false);

                        dialog.show();
                    }
                    youdie = false;
                }//if(gauge.isTimeout())


                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String getTime = simpleDate.format(mDate);
                getTime.replace("-","");
                getTime.replace(" ","");
                Log.d("time", getTime);

                Response.Listener<String> responseLister = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success) {
                                Toast.makeText(context, "점수가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "점수 저장 실패", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };

                ScoreRequest scoreRequest = new ScoreRequest(LoginActivity.userID , Score, getTime ,responseLister);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(scoreRequest);

            }

    }//crash()


    //배경을 움직이기 위한 메서드
    private void scrollBack(){

        back1_y += 5;
        back2_y += 5;

        // 화면을 벗어난 배경을 다시 화면 꼭대기로 이동
        if(back1_y >= height){
            back1_y = -height;
        }

        if(back2_y >= height){
            back2_y = -height;
        }

    }//scrollBack()

    @Override
    protected void onDraw(Canvas canvas) {

        this.canvas = canvas;
        progressState();

    }

    // 센서 관련 메서드
    @Override
    public void onSensorChanged(SensorEvent event) {
        /*//센서의 변경사항이 감지되면 호출되는 메서드
        //x축의 값을 unitX에 저장
        unitX -= (int)event.values[0] * 5;

        //화면을 벗어나지 않도록 제어
        if(unitX <= 0){
            unitX = 0;
        }

        if(unitX >= width - unitW){
            unitX = width - unitW;
        }

        //비행기의 y좌표값
        unitY += (int)event.values[1] * 5;
        if(unitY < 0){
            unitY = 0;
        }

        if(unitY >= height - unitH){
            unitY = height - unitH;
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //센서의 반응속도
    }

    //화면 터치 감지
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                unitX = (int)event.getX();
                unitY = (int)event.getY();
                break;
            case MotionEvent.ACTION_UP:
                // 게임종료시 다이얼로그 호출
                if(handdie) {
                    GameStartBoolean = false;
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("손가락을 때서 죽었습니다.\n다시 하시겠습니까? \n점수는 저장되지 않습니다ㅠㅠ");
                    dialog.setTitle("GameOver");

                    dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //개임 재시작
                            Intent i = new Intent(context, BackScrollActivity.class);
                            context.startActivity(i);
                            ((Activity) context).finish();
                        }
                    });

                    dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Activity) context).finish();
                        }
                    });

                    //다이얼로그를 휴대폰의 뒤로가기 버튼으로 취소하지 못하게 하는 속성
                    dialog.setCancelable(false);

                    dialog.show();
                }
                break;
        }

        /*//화면이 터치되면 미사일을 발사
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            Missile m1 = new Missile(unitX, unitY);
            missList.add(m1);
        }*/
        return true;
    }

    // 미사일을 이동
    private void checkMissile(){

        if(missList.size() > 0){
            for(int i = 0; i < missList.size(); i++){
                Missile ms = missList.get(i);
                ms.move();

                if(ms.isDead()){
                    //미사일의 y좌표가 화면을 벗어났다라고 판단된다면
                    //ArrayList에서 미사일을 제거
                    missList.remove(ms);
                }
            }//for
        }

    }//checkMissile()

    // 시작하기전 다이얼로그로 알려줍니다
    private void StartAlertDialog(){
        // 게임종료시 다이얼로그 호출
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("손가락을 때면 죽습니다!");
        dialog.setTitle("알림!!!!");

        dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GameStartBoolean = true;
            }
        });

        dialog.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "허허...", Toast.LENGTH_SHORT).show();
                ((Activity)context).finish();
            }
        });

        //다이얼로그를 휴대폰의 뒤로가기 버튼으로 취소하지 못하게 하는 속성
        dialog.setCancelable(false);

        dialog.show();
    }
}











