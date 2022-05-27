package eina.unizar.ajedrez;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AiActivity extends AppCompatActivity {
    private TextView countdownText;
    ChessBoard myCanvas;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds;

    int x0=65;
    int y0 = 185;
    int squareSize = 80;
    int x1= x0 +squareSize;
    int posX, posY, posFinX, posFinY;
    private boolean timerRunning;
    char turno = 'w';
    boolean pulsado, noTime, finTiempo = false;
    int time;
    String nickname;
    String avatar;
    String side;
    String board;
    String pieces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int randomNum = ThreadLocalRandom.current().nextInt(0, 1 + 1);
        side = String.valueOf(randomNum);
        Log.d("d: ", "Side " + side);
        nickname = getIntent().getExtras().getString("nickname");
        avatar = getIntent().getExtras().getString("avatar");
        time = getIntent().getExtras().getInt("time");
        board = getIntent().getExtras().getString("board");
        pieces =  getIntent().getExtras().getString("pieces");
        Log.d("d: ", "Side " + pieces);
        Log.d("d: ", "Board " + board);
        if(side.equals("0")) myCanvas = new ChessBoard(this,"0", board, pieces);
        else myCanvas = new ChessBoard(this,"1",board, pieces);
        setContentView(R.layout.activity_ai);
        //guardarPartida();
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

        /*TextView chat = findViewById(R.id.pantallaChat);
        chat.setMovementMethod(new ScrollingMovementMethod());
        chat.append("Hola\n");
        chat.append("Hola\n");
        chat.append("Hola\n");
        chat.append("Hola\n");
        chat.append("Hola\n");
        chat.append("Hola\n");*/
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
        int x = 0,y = 0;
        int x0=65;
        int y0 = 185;
        int squareSize = 80;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!pulsado) {
                    pulsado = myCanvas.getCheckClick();
                    Log.d("AiActivity", "Resultado click" + pulsado);
                   /* Log.d("AiActivity", "No pulsado");
                    pulsado = true;x = 0; y = 0;
                    posX = Math.round(e.getX()); // Coordenadas x e y donde se toca
                    posY = Math.round(e.getY());
                    Log.d("AiActivity", "No pulsado" + posX +
                            " " + posY+ " " +x0 + " " + y0 + " " + squareSize);
                    if (posX > x0 && posX < x0 + (squareSize * 8) // Comprobar que se ha pulsado en el tablero
                            && posY > y0 && posY < y0 + (squareSize * 8)) {
                        x = (posX - x0) / squareSize; // Fila y columna donde se toca en función de los px
                        y = (posY - y0) / squareSize;
                        pulsado = myCanvas.checkClick(x, y); // Ver si es casilla con pieza del color que toca
                        Log.d("AiActivity", "Tocado en pos:" + posX + " y " + posY + " movimiento valido "+ pulsado); Log.d("AiActivity", "Tocado en pos:" + posX + " y " + posY + " movimiento valido "+ pulsado);
                    }else {
                        Log.d("AiActivity", "No ebtre");
                    }*/
                }
                else {
                    /*posFinX = Math.round(e.getX());
                    posFinX = (posFinX - x0) / squareSize;
                    posFinY = Math.round(e.getY());
                    posFinY = (posFinY - y0) / squareSize;
                    Log.d("d: ", "Probar click "+ x );*/
                    if (myCanvas.getIsAClick()){
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Log.d("d: ", "Pre comprobacion");
                                if (myCanvas.checkCorrectMov(turno)) {
                                    Log.d("d: ", "Movimiento correcto");
                                    if (turno == 'w') {
                                        if (!noTime) stopTimer();
                                        turno = 'b';
                                    } else {
                                        if (!noTime) startTimer();
                                        turno = 'w';
                                    }
                                    if (!myCanvas.isMate()) {
                                        myCanvas.makeAIMove();
                                        if(myCanvas.isMate()){
                                           if(!noTime) stopTimer();
                                            // Toast.makeText(getApplicationContext(), "Fin de partida" + "", Toast.LENGTH_SHORT).show();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(AiActivity.this);
                                            builder.setMessage("Te ha ganado la maquina");
                                            builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    guardarPartida();
                                                    Intent j = new Intent(getApplicationContext(), MainPage.class);
                                                    Log.d("d: ", "Volviendo " + nickname);
                                                    j.putExtra("nickname", nickname);
                                                    j.putExtra("avatar", avatar);
                                                    startActivity(j);
                                                }
                                            });
                                            builder.show();
                                        }
                                        else  if (!noTime)startTimer();
                                    } else {
                                        if(!noTime) stopTimer();
                                        Log.d("d: ", "Fin partida");
                                        Toast.makeText(getApplicationContext(), "Fin de partida" + "", Toast.LENGTH_SHORT).show();
                                        //Intent i  = new Intent(getApplicationContext(),PopActivity.class);
                                        //startActivity(i);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(AiActivity.this);
                                        builder.setMessage("Fin de la partida");
                                        builder.setPositiveButton("Volver", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                guardarPartida();
                                                Intent j = new Intent(getApplicationContext(), MainPage.class);
                                                Log.d("d: ", "Volviendo " + nickname);
                                                j.putExtra("nickname", nickname);
                                                j.putExtra("avatar", avatar);
                                                startActivity(j);
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
                                    if (turno == 'b') turno = 'w';
                                    else turno = 'b';
                                }

                            }
                        }, 500);   //5 seconds
                    }
                    pulsado = false;
                }
        }
        return super.onTouchEvent(e);
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (!noTime)stopTimer();
        Intent i = new Intent(getApplicationContext(), MainPage.class);//OnlineActivity
        i.putExtra("nickname", nickname);
        i.putExtra("avatar", avatar);
        startActivity(i);
        //this.finish();
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            stopTimer();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }*/

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

    private void guardarPartida(){
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/saveMatchResult";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nickname", nickname);
            jsonBody.put("rival", "c");
            jsonBody.put("result", "win");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBody.toString();;
        Log.d("guardarPartida: ", requestBody );
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

}
