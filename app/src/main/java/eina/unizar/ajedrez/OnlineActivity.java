package eina.unizar.ajedrez;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OnlineActivity extends AppCompatActivity {
    private TextView countdownText,countdownTextRival;
    ChessBoard myCanvas;
    private CountDownTimer countDownTimer,countDownTimerRival;
    private long timeLeftInMilliseconds;
    private long timeLeftInMillisecondsRival;
    private boolean timerRunning, timerRunningRival;
    char turno = 'w';
    boolean pulsado = false;
    int time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCanvas = new ChessBoard(this);
        setContentView(R.layout.activity_online);

        time = getIntent().getExtras().getInt("time");
        timeLeftInMilliseconds = (long) time*60*1000;
        timeLeftInMillisecondsRival = (long) time*60*1000;
        TextView timerUser = findViewById(R.id.timerUser);
        timerUser.setText(time+":00");
        TextView timerRival = findViewById(R.id.timerRival);
        timerRival.setText(time+":00");

        countdownText = findViewById(R.id.timerUser);
        countdownTextRival = findViewById(R.id.timerRival);
        startStop();

        LinearLayout layout = (LinearLayout) findViewById(R.id.tablero);
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
            public void onFinish() { }
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
            public void onFinish() { }
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
