package eina.unizar.ajedrez;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

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
    String side,side2;
    String idSocket,idSocket2;
    String nickname;
    boolean finPartida =  false;
    ProgressDialog dialog;

    String cInicial,cFinal,fInicial,fFinal;

    private Socket mSocket, mSocket2;
    {
        try {
            //mSocket = IO.socket("http://10.0.2.2:3000");
           // mSocket2 = IO.socket("http://10.0.2.2:3000");
            mSocket = IO.socket("http://ec2-18-206-137-85.compute-1.amazonaws.com:3000");
           // mSocket2 = IO.socket("http://ec2-18-206-137-85.compute-1.amazonaws.com:3000");
        } catch (URISyntaxException e) {
            Log.d("Socket: ",   e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //myCanvas = new ChessBoard(this,"0");
        setContentView(R.layout.activity_online);
        //mSocket.disconnect();
       // mSocket2.disconnect();
        mSocket.connect();
      //  mSocket2.connect();



         dialog=new ProgressDialog(this);
        dialog.setMessage("Esperando rival");
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(true);
        dialog.show();

        nickname = getIntent().getExtras().getString("nickname");
        time = getIntent().getExtras().getInt("time");
        if(time != 0 )timeLeftInMilliseconds = (long) time*60*1000;
        timeLeftInMillisecondsRival = (long) time*60*1000;
      //  TextView nameUser = findViewById(R.id.nomUser);
       // nameUser.setText(nickname);
        TextView timerUser = findViewById(R.id.timerUser);
        timerUser.setText(time+":00");
        TextView timerRival = findViewById(R.id.timerRival);
        timerRival.setText(time+":00");
       // TextView timerUser = findViewById(R.id.timerUser);
        if(time != 0) timerUser.setText(time+":00");
        else{
            timerUser.setText("--:--");
        }
        countdownText = findViewById(R.id.timerUser);
        countdownTextRival = findViewById(R.id.timerRival);
        esperaRival();
    }

    public void playGame(){
             Log.d("d: ", "Esperando movimiento");
            mSocket.on("getGameMove", new Emitter.Listener() {
                 @Override
                 public void call(Object... args) {
                     JSONObject data = (JSONObject)args[0];
                     Log.d("Socket: ", "Despues de esperar " + data.toString());
//here the data is in JSON Format
                     try {
                         cInicial = data.getString("cI");
                         fInicial = data.getString("fI");
                         cFinal = data.getString("cF");
                         fFinal = data.getString("fF");
                         myCanvas.hacerMovimientoRival(Integer.parseInt(fInicial),Integer.parseInt(cInicial),Integer.parseInt(fFinal),Integer.parseInt(cFinal));
                         if(turno == 'w') turno = 'b';
                         else turno = 'w';
                         if(side.equals("0")) side = "1";
                         else side = "0";
                         if (!myCanvas.isMate()) {
                             runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     startTimer();
                                     stopTimerRival();
                                 }
                             });


                         }else{
                             runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {stopTimer();}
                             });

                             Log.d("d: ", "Fin partida");
                             Toast.makeText(getApplicationContext(), "Derrota" , Toast.LENGTH_SHORT).show();
                             AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
                             builder.setMessage("Derrota :(");
                             builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                     Intent x = new Intent(getApplicationContext(), MainPage.class);
                                     startActivity(x);
                                 }
                             });
                             builder.show();
                         }
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                     Log.d("Socket: ", data.toString());

                 }
             });

     }
    @Override
    public boolean onTouchEvent(MotionEvent e){

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("d: ", "Pre comprobacion");
                if(turno == 'w' && side.equals("0") || turno == 'b' && side.equals( "1")){
                //Pulsa un boton, comprobar si es mi turno. Sino, sudar de la comprobación
                    if (myCanvas.checkCorrectMov(turno)) { // Pasar parametro donde se guarde movimiento correcto
                        int[] pos = myCanvas.getPos();
                        mSocket.emit("sendGameMove",idSocket,pos[0],pos[1],pos[2],pos[3]);
                        if (turno == 'w')  turno = 'b';
                        else turno = 'w';
                        if (!myCanvas.isMate()) {
                            stopTimer();
                            startTimerRival();
                        } else { // Enviar datos de la partida al server y liberar el socket
                            stopTimer();
                            AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
                            builder.setMessage("Victoria!");
                            builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    guardarPartida();
                                    Intent x = new Intent(getApplicationContext(), MainPage.class);
                                    startActivity(x);
                                }
                            });
                            builder.show();
                        }
                   }
                    pulsado = false;
                }
        }
        return super.onTouchEvent(e);
    }
    private void cambiarTabler(String side){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (side.equals("0")){
                    Log.d("Tablero : ", "Tablero blanco " + side);
                    myCanvas = new ChessBoard(getApplicationContext(), "0");
                    LinearLayout layout = (LinearLayout) findViewById(R.id.tablero);
                    layout.addView(myCanvas);
                }
                else{
                    myCanvas = new ChessBoard(getApplicationContext(), "1");
                    LinearLayout layout = (LinearLayout) findViewById(R.id.tablero);
                    layout.addView(myCanvas);
                    playGame();
                }
                startStop();
            }
        });

    }
   private void esperaRival(){
        Log.d("Socket: ", "Esperando rival");
       mSocket.on("getOpponent", new Emitter.Listener() {

                   @Override
                   public void call(Object... args) {
                       JSONObject data = (JSONObject) args[0];
                       //here the data is in JSON Format
                       try {
                           idSocket = data.getString("id");
                           side = String.valueOf(data.getInt("side"));
                           dialog.dismiss();
                           cambiarTabler(side);

                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                       Log.d("Socket: ", data.toString());
                   }
       });

  /*    Log.d("Socket2: ", "Socket conectado");
       Log.d("Socket2: ", "Esperando rival");
       mSocket2.on("getOpponent", new Emitter.Listener() {
           @Override
           public void call(Object... args) {
               JSONObject data = (JSONObject) args[0];
//here the data is in JSON Format
               try {
                   idSocket2 = data.getString("id");
                   side2 = data.getString("side");
                   playGame();
                   dialog.dismiss();
               } catch (JSONException e) {
                   e.printStackTrace();
               }
               Log.d("Socket2: ", data.toString());
               // Toast.makeText(FriendsList.this, data.toString(), Toast.LENGTH_SHORT).show();
           }
       });*/

    }

    private void guardarPartida(){

    }

    public void startStop(){
        if(timerRunning){
            stopTimer();
        }else{
            if(side.equals("0")){
                startTimer();
            }else{
                startTimerRival();
            }

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