package eina.unizar.ajedrez;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import javax.security.auth.callback.Callback;

public class MainActivity extends AppCompatActivity {
    private TextView countdownText,countdownTextRival;
    ChessBoard myCanvas ;//= new ChessBoard(this);
    private CountDownTimer countDownTimer,countDownTimerRival;
    private long timeLeftInMilliseconds = 600000;
    private long timeLeftInMillisecondsRival = 600000;
    private boolean timerRunning, timerRunningRival;
    char turno = 'w';
    boolean pulsado = false;
    int time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCanvas = new ChessBoard(this);
        setContentView(R.layout.activity_main);
        countdownTextRival = findViewById(R.id.timerRival);
        countdownText = findViewById(R.id.timerUser);
        startStop();
        LinearLayout layout = (LinearLayout) findViewById(R.id.tablero);

        time = getIntent().getExtras().getInt("time");

        layout.addView(myCanvas);
        //playGame();
    }

   /* public void playGame(){
        while(true){
            if(turno == 'w'){
                Log.d("d: ", "Entra aqui");
            }else{
                myCanvas.makeAIMove();
            }
        }
    }*/
    @Override
    public boolean onTouchEvent(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("d: ", "Pre comprobacion");
                if (myCanvas.checkCorrectMov(turno)){
                    Log.d("d: ", "Movimiento correcto");
                    if(turno == 'w'){
                        stopTimer();
                        startTimerRival();
                        turno = 'b';
                    }else{
                        stopTimerRival();
                        startTimer();
                        turno = 'w';
                    }
                    if(!myCanvas.isMate()) myCanvas.makeAIMove();
                    stopTimerRival();
                    startTimer();
                    turno = 'w';
                }
                pulsado = false;
        }
        return super.onTouchEvent(e);
    }
    public void startStop(){
        if(timerRunning){
            stopTimer();
        }else{
            startTimer();
        }
    }
    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds,1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds = l;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();
        timerRunning = true;
    }

    public void stopTimer(){
        countDownTimer.cancel();
        timerRunning = true;
    }

    public void startTimerRival(){
        countDownTimerRival = new CountDownTimer(timeLeftInMillisecondsRival,1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillisecondsRival = l;
                updateTimerRival();
            }

            @Override
            public void onFinish() {

            }
        }.start();
        timerRunningRival = true;
    }

    public void stopTimerRival(){
        countDownTimerRival.cancel();
        timerRunningRival = true;
    }
    public void updateTimer(){

        int minutes =  (int) timeLeftInMilliseconds / 60000;
        int seconds =  (int) timeLeftInMilliseconds % 60000 / 1000;

        String timeLeftText = "" + minutes + ":";
        if(seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;
        countdownText.setText(timeLeftText);
    }
    public void updateTimerRival(){
        int minutesRival =  (int) timeLeftInMillisecondsRival / 60000;
        int secondsRival =  (int) timeLeftInMillisecondsRival % 60000 / 1000;

        String timeLeftText = "" + minutesRival + ":";
        if(secondsRival < 10) timeLeftText += "0";
        timeLeftText += secondsRival;
        countdownTextRival.setText(timeLeftText);
    }
}
