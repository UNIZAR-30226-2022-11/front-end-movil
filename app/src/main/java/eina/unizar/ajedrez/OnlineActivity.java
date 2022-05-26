package eina.unizar.ajedrez;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

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
    String side, side2;
    String idSocket,idSocket2;
    String nickname;
    String avatar;
    String board;
    String pieces;
    boolean finPartida =  false;
    boolean finTiempo = false;
    ProgressDialog dialog;

    String cInicial,cFinal,fInicial,fFinal;

    private Socket mSocket, mSocket2;
    {
        try {
           //mSocket = IO.socket("http://10.0.2.2:3000");
           // mSocket2 = IO.socket("http://10.0.2.2:3000");
           mSocket = IO.socket("http://ec2-18-206-137-85.compute-1.amazonaws.com:3000");
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
        //mSocket2.disconnect();
        mSocket.connect();
        //mSocket2.connect();



         dialog=new ProgressDialog(this);
        dialog.setMessage("Esperando rival");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(true);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("d: ", "Finalizando actividad");
                mSocket.disconnect();
               // stopTimer();
                Intent i = new Intent(getApplicationContext(), MainPage.class);
                i.putExtra("nickname", nickname);
                i.putExtra("avatar",avatar);
                startActivity(i);
            }
        });
        dialog.show();
        pieces =  getIntent().getExtras().getString("pieces");
        nickname = getIntent().getExtras().getString("nickname");
        avatar = getIntent().getExtras().getString("avatar");
        time = getIntent().getExtras().getInt("time");
        board = getIntent().getExtras().getString("board");
        if(time != 0 )timeLeftInMilliseconds = (long) time*60*1000;
        timeLeftInMillisecondsRival = (long) time*60*1000;
        mSocket.emit("buscarPartida",nickname,Integer.toString(time),avatar,"0");
       //mSocket2.emit("buscarPartida","Juan",Integer.toString(time),avatar,"0");
       TextView nameUser = findViewById(R.id.nombreUser);
        nameUser.setText(nickname);
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
                     if(turno == 'w' && side.equals("1") || turno == 'b' && side.equals("0")){
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
                            // if(side.equals("0")) side = "1";
                          //   else side = "0";
                             if (!myCanvas.isMate()) {
                                 runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         startTimer();
                                         stopTimerRival();
                                     }
                                 });

                             }else{
                                 mSocket.disconnect();
                                 runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         stopTimer();
                                         Log.d("d: ", "Fin partida");
                                         Toast.makeText(getApplicationContext(), "Derrota" , Toast.LENGTH_SHORT).show();
                                         AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
                                         builder.setMessage("Derrota :(");
                                         builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialogInterface, int i) {
                                                 Intent x = new Intent(getApplicationContext(), MainPage.class);
                                                 x.putExtra("nickname",nickname);
                                                 x.putExtra("avatar",avatar);
                                                 startActivity(x);
                                             }
                                         });
                                         builder.show();
                                     }
                                 });
                             }
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                         Log.d("Socket: ", data.toString());
                     }

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
                        Log.d("d: ", "Comprobando emit");
                        mSocket.emit("sendGameMove",pos[1],pos[0],pos[3],pos[2]);
                        if (turno == 'w')  turno = 'b';
                        else turno = 'w';
                        if (!myCanvas.isMate()) {
                            stopTimer();
                            startTimerRival();
                            playGame();
                        } else { // Enviar datos de la partida al server y liberar el socket
                            mSocket.disconnect();
                            stopTimer();
                            AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
                            builder.setMessage("Victoria!");
                            builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    guardarPartida();

                                    Intent x = new Intent(getApplicationContext(), MainPage.class);
                                    x.putExtra("nickname",nickname);
                                    x.putExtra("avatar",avatar);
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
                    myCanvas = new ChessBoard(getApplicationContext(), "0",board,pieces);
                    LinearLayout layout = (LinearLayout) findViewById(R.id.tablero);
                    layout.addView(myCanvas);
                }
                else{
                    myCanvas = new ChessBoard(getApplicationContext(), "1",board,pieces);
                    LinearLayout layout = (LinearLayout) findViewById(R.id.tablero);
                    layout.addView(myCanvas);
                    playGame();
                }
                startStop();
            }
        });
    }

    private void recuperarPartida(String[][]oldBoard,String turnoActual,String tiempo, String tiempoRival){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (side.equals("0")){
                    Log.d("Tablero : ", "Tablero blanco " + side);
                    myCanvas = new ChessBoard(getApplicationContext(), oldBoard,"0", turnoActual,board);
                    LinearLayout layout = (LinearLayout) findViewById(R.id.tablero);
                    layout.addView(myCanvas);
                }
                else{
                    myCanvas = new ChessBoard(getApplicationContext(),oldBoard, "1",turnoActual,board);
                    LinearLayout layout = (LinearLayout) findViewById(R.id.tablero);
                    layout.addView(myCanvas);
                    playGame();
                }
                TextView timerUser = findViewById(R.id.timerUser);
                timerUser.setText(tiempo);
                TextView timerRival = findViewById(R.id.timerRival);
                timerRival.setText(tiempoRival);
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
                           idSocket = data.getString("opNick");
                           side = String.valueOf(data.getInt("side"));
                           String load = String.valueOf((data.getInt("load")));
                           if(load.equals("1")){
                               String turnoActual = String.valueOf(data.getString("turn"));
                               String tiempo = String.valueOf(data.getString("t1"));
                               String tiempoRival = String.valueOf(data.getString("t1"));

                               JSONArray myBoard = data.getJSONArray("board");
                               JSONObject casilla;
                               String boardMtx[][] = new String[8][8];
                               for(int i = 0;i < myBoard.length();i++){
                                   casilla =  myBoard.getJSONObject(i);
                                   int posI =  casilla.getInt("i");
                                   int posJ =  casilla.getInt("j");
                                   boardMtx[posI][posJ] =  casilla.getString("pieza");
                               }
                               recuperarPartida(boardMtx,turnoActual,tiempo,tiempoRival);
                           }else{
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       TextView rival = findViewById(R.id.nombreRival);
                                       rival.setText(idSocket);
                                   }
                               });

                               //   mSocket.emit("sendGameMove",0,0,2,2);
                               cambiarTabler(side);
                           }

                           dialog.dismiss();
                           esperarAbandono();
                           esperarRecuperacion();

                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                       Log.d("Socket: ", data.toString());
                   }
       });

    /*Log.d("Socket2: ", "Socket conectado");
       Log.d("Socket2: ", "Esperando rival");
       mSocket2.on("getOpponent", new Emitter.Listener() {
           @Override
           public void call(Object... args) {
               JSONObject data = (JSONObject) args[0];
//here the data is in JSON Format
               try {
                   idSocket2 = data.getString("opNick");
                   side2 = data.getString("side");
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           TextView rival = findViewById(R.id.nombreRival);
                           rival.setText(idSocket2);
                       }
                   });
                   //mSocket2.emit("sendGameMove",0,0,2,2);
                   //playGame();
                   guardarPartida();
                   cambiarTabler(side);
                   esperarRecuperacion();
                   dialog.dismiss();
               } catch (JSONException e) {
                   e.printStackTrace();
               }
               Log.d("Socket2: ", data.toString());
               // Toast.makeText(FriendsList.this, data.toString(), Toast.LENGTH_SHORT).show();
           }
       });*/
    }
    private void esperarAbandono(){
        mSocket.on("oponenteDesconectado", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                mSocket.disconnect();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopTimer();
                        Log.d("d: ", "Fin partida");
                        Toast.makeText(getApplicationContext(), "El rival ha abandonado la partida" , Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
                        builder.setMessage("El rival ha abandonado la partida");
                        builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                guardarPartida();
                                Intent x = new Intent(getApplicationContext(), MainPage.class);
                                x.putExtra("nickname",nickname);
                                x.putExtra("avatar",avatar);
                                startActivity(x);
                            }
                        });
                        builder.show();
                    }
                });
            }
        });
    }

    private void esperarRecuperacion() throws JSONException {
        JSONArray infoTablero = new JSONArray();
        String tablero[][] = new String[8][8];   ;//= myCanvas.devolverTablero();

        for(int i = 0;i < 8;i++){
            for(int j = 0; j < 8;j++) {
                tablero[i][j] = "--";
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("i", i);
                    jsonBody.put("j", j);
                    jsonBody.put("pieza", tablero[i][j]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                infoTablero.put(jsonBody);
            }    }


                final String requestBody2 = infoTablero.toString();
                Log.d("d: " ,"Info tablero "+requestBody2);
        JSONObject casilla;
        String boardMtx[][] = new String[8][8];
        for(int i = 0;i < infoTablero.length();i++){
            casilla =  infoTablero.getJSONObject(i);
            int posI =  casilla.getInt("i");
            int posJ =  casilla.getInt("j");
            boardMtx[posI][posJ] =  casilla.getString("pieza");
        }
        for(int i = 0;i < 8;i++) {
            for (int j = 0; j < 8; j++) {
                Log.d("d: ", boardMtx[i][j]);
            }
        }
        mSocket.on("enviarTablero", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                int minutes =  (int) timeLeftInMilliseconds / 60000;
                int seconds =  (int) timeLeftInMilliseconds % 60000 / 1000;
                String myTime = Integer.toString(minutes);//
                myTime +=  ":";
                myTime += Integer.toString(seconds);
                int minutesRival =  (int) timeLeftInMillisecondsRival / 60000;
                int secondsRival =  (int) timeLeftInMillisecondsRival % 60000 / 1000;
                String timeRival = Integer.toString(minutesRival);//
                timeRival +=  ":";
                timeRival += Integer.toString(secondsRival);
                String [][] tablero = myCanvas.devolverTablero();
                JSONArray infoTablero = new JSONArray();


                for(int i = 0;i < 8;i++){
                    for(int j = 0; j < 8;j++){
                       // tablero[i][j] = "--";
                        JSONObject jsonBody = new JSONObject();
                        try {
                            jsonBody.put("i", i);
                            jsonBody.put("j", j);
                            jsonBody.put("pieza",tablero[i][j]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        infoTablero.put(jsonBody);
                    }
                }

                mSocket.emit("recibirTablero",side, infoTablero,turno,myTime,timeRival);
            }
        });
    }
    private void guardarPartida(){
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/saveMatchResult";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nickname", nickname);
            jsonBody.put("rival", "jbuil");
            jsonBody.put("result", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBody.toString();;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse: ", error.getLocalizedMessage() == null ? "" : error.getLocalizedMessage());
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.d("d: " ,"Falla aqui");
                    return null;
                }
            }
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("content-type","application/json");
                params.put("Access-Control-Allow-Origin","*");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
       // mSocket2.disconnect();
        Log.d("backPressed: " ,"Sale de la actividad");
        mSocket.disconnect();
      //  stopTimer();
        Intent i = new Intent(getApplicationContext(), MainPage.class);//OnlineActivity
        i.putExtra("nickname", nickname);
        i.putExtra("avatar", avatar);
        startActivity(i);
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
           // mSocket2.disconnect();
            mSocket.disconnect();
            stopTimer();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }*/


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
        if(minutes == 0 && seconds == 0){
            finTiempo = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
            builder.setMessage("Tiempo superado. Derrota");
            builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent x  = new Intent(getApplicationContext(),MainPage.class);
                    x.putExtra("nickname",nickname);
                    x.putExtra("avatar",avatar);
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
    public void updateTimerRival(){
        int minutesRival =  (int) timeLeftInMillisecondsRival / 60000;
        int secondsRival =  (int) timeLeftInMillisecondsRival % 60000 / 1000;
        if(minutesRival == 0 && secondsRival == 0){
            finTiempo = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
            builder.setMessage("Victoria!");
            builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent x  = new Intent(getApplicationContext(),MainPage.class);
                    guardarPartida();
                    x.putExtra("nickname",nickname);
                    x.putExtra("avatar",avatar);
                    startActivity(x);
                }
            });
            builder.show();
        }
        String timeLeftText = "" + minutesRival + ":";
        if(secondsRival < 10) timeLeftText += "0";
        timeLeftText += secondsRival;
        countdownTextRival.setText(timeLeftText);
    }


}