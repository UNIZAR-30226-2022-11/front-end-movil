package eina.unizar.ajedrez;

<<<<<<< HEAD
import android.content.DialogInterface;
import android.content.Intent;
=======
>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
<<<<<<< HEAD
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
=======

>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987
import androidx.appcompat.app.AppCompatActivity;

public class OnlineActivity extends AppCompatActivity {
    private TextView countdownText,countdownTextRival;
    ChessBoard myCanvas;
    private CountDownTimer countDownTimer,countDownTimerRival;
<<<<<<< HEAD
    private long timeLeftInMilliseconds;
=======
<<<<<<<< HEAD:app/src/main/java/eina/unizar/ajedrez/OnlineActivity.java
    private long timeLeftInMilliseconds;
========
    private long timeLeftInMilliseconds ;
>>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987:app/src/main/java/eina/unizar/ajedrez/MainActivity.java
>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987
    private long timeLeftInMillisecondsRival;
    private boolean timerRunning, timerRunningRival;
    char turno = 'w';
    boolean pulsado = false;
    int time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        myCanvas = new ChessBoard(this);
        setContentView(R.layout.activity_online);

=======
<<<<<<<< HEAD:app/src/main/java/eina/unizar/ajedrez/OnlineActivity.java
        myCanvas = new ChessBoard(this);
        setContentView(R.layout.activity_online);

========
        setContentView(R.layout.activity_main);
        time = getIntent().getExtras().getInt("time");
        Log.d("d: ", "Movimiento correcto ");
        TextView mTextView = (TextView) findViewById(R.id.timerRival);
        mTextView.setText(Integer.toString(time)+":00");
        mTextView = (TextView) findViewById(R.id.timerUser);
        mTextView.setText(Integer.toString(time)+":00");
        myCanvas = new ChessBoard(this);
        timeLeftInMilliseconds =  time *60*1000;
        timeLeftInMillisecondsRival =  time *60*1000;

        countdownTextRival = findViewById(R.id.timerRival);
        countdownText = findViewById(R.id.timerUser);
        startStop();
        LinearLayout layout = (LinearLayout) findViewById(R.id.tablero);
        time = getIntent().getExtras().getInt("time");
>>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987:app/src/main/java/eina/unizar/ajedrez/MainActivity.java
>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987
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

<<<<<<< HEAD
    /* public void playGame(){
         while(true){
             if(turno == 'w'){
                 Log.d("d: ", "Entra aqui");
             }else{
                 myCanvas.makeAIMove();
             }
         }
     }*/
=======
   /* public void playGame(){
        while(true){
            if(turno == 'w'){
                Log.d("d: ", "Entra aqui");
            }else{
                myCanvas.makeAIMove();
            }
        }
    }*/
>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987
    @Override
    public boolean onTouchEvent(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
<<<<<<< HEAD
                Log.d("d: ", "Pre comprobacion");
                if (myCanvas.checkCorrectMov(turno)){
                    Log.d("d: ", "Movimiento correcto");
=======
                //Log.d("d: ", "Pre comprobacion");
                if (myCanvas.checkCorrectMov(turno)){
                  //  Log.d("d: ", "Movimiento correcto");
>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987
                    if(turno == 'w'){
                        stopTimer();
                        startTimerRival();
                        turno = 'b';
                    }else{
                        stopTimerRival();
                        startTimer();
                        turno = 'w';
                    }
<<<<<<< HEAD
                    if(!myCanvas.isMate()) {
                        myCanvas.makeAIMove();
                        startTimer();
                    }else {
                        stopTimer();
                        Log.d("d: ", "Fin partida");
                        Toast.makeText(this, "Fin de partida" + "", Toast.LENGTH_SHORT).show();
                        //Intent i  = new Intent(getApplicationContext(),PopActivity.class);
                        //startActivity(i);
                        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
                        builder.setMessage("Fin de la partida");
                        builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent x  = new Intent(getApplicationContext(),MainPage.class);
                                startActivity(x);
                            }
                        });
                        builder.show();
                    }
=======

                    if(!myCanvas.isMate()) myCanvas.makeAIMove();
                    stopTimerRival();
                    startTimer();
>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987
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
<<<<<<< HEAD
}
=======
}
>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987
