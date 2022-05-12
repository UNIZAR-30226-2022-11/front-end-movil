package eina.unizar.ajedrez;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AiActivity extends AppCompatActivity {
    private TextView countdownText;
    ChessBoard myCanvas;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds;
    private boolean timerRunning;
    char turno = 'w';
    boolean pulsado, noTime, finTiempo = false;
    int time;
    String nickname;
    String side;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int randomNum = ThreadLocalRandom.current().nextInt(0, 1 + 1);
        side = String.valueOf(randomNum);
        Log.d("d: ", "Side " + side);
        if(side.equals("0")) myCanvas = new ChessBoard(this,"0");
        else myCanvas = new ChessBoard(this,"1");
        setContentView(R.layout.activity_ai);

        nickname = getIntent().getExtras().getString("nickname");
        time = getIntent().getExtras().getInt("time");
        if(time == 3) time =1;
        if(time != 0 )timeLeftInMilliseconds = (long) time*60*1000;
        TextView timerUser = findViewById(R.id.timerUser);
        if(time != 0) timerUser.setText(time+":00");
        else{
            timerUser.setText("--:--");
            noTime = true;
        }

        countdownText = findViewById(R.id.timerUser);
        if(!noTime) startStop();

        LinearLayout layout = (LinearLayout) findViewById(R.id.tablero);
        layout.addView(myCanvas);

        if(side.equals("1")) {
            myCanvas.makeAIMove();
            turno = 'b';
        }
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
                        if(!noTime) stopTimer();
                        turno = 'b';
                    }else{
                        if(!noTime) startTimer();
                        turno = 'w';
                    }
                    if(!myCanvas.isMate()){
                        myCanvas.makeAIMove();
                        startTimer();
                    } else {
                        stopTimer();
                        Log.d("d: ", "Fin partida");
                        Toast.makeText(this, "Fin de partida" + "", Toast.LENGTH_SHORT).show();
                        //Intent i  = new Intent(getApplicationContext(),PopActivity.class);
                        //startActivity(i);
                        AlertDialog.Builder builder = new AlertDialog.Builder(AiActivity.this);
                        builder.setMessage("Fin de la partida");
                        builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent x  = new Intent(getApplicationContext(),MainPage.class);
                                x.putExtra("nickname",nickname);
                                startActivity(x);
                            }
                        });
                        /*final AlertDialog dialog = builder.create();
                        dialog.show();
                        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                        positiveButtonLL.gravity = Gravity.CENTER;
                        positiveButton.setLayoutParams(positiveButtonLL);*/
                        builder.show();

                    }
                    if (turno == 'b')turno = 'w';
                    else turno = 'b';
                }
                pulsado = false;
        }
        return super.onTouchEvent(e);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("d: ", "Fin actividad");
        stopTimer();
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            stopTimer();
            finish();
        }
        return super.onKeyDown(keyCode, event);
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

    public void updateTimer(){

        int minutes =  (int) timeLeftInMilliseconds / 60000;
        int seconds =  (int) timeLeftInMilliseconds % 60000 / 1000;
        if(minutes == 0 && seconds == 0){
            finTiempo = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(AiActivity.this);
            builder.setMessage("Tiempo superado");
            builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent x  = new Intent(getApplicationContext(),MainPage.class);
                    startActivity(x);
                }
            });
            builder.show();
        }
        String timeLeftText = "" + minutes + ":";
        if(seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;
        countdownText.setText(timeLeftText);
    }
}