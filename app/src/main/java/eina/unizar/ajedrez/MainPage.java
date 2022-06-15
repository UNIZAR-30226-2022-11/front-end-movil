package eina.unizar.ajedrez;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

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

import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainPage extends AppCompatActivity {

    String nickname;
    String avatar;

    Button online3Button;
    Button online10Button;
    Button online30Button;
    Button onlineNoTimeButton;
    Button ai3Button;
    Button ai10Button;
    Button ai30Button;
    Button aiNoTimeButton;
    Button myFriends;
    Button createTournament;
    Button joinTournament;
    Button requests;
    Button store;
    Button gameRecord;
    Button ranking;
    Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        nickname = getIntent().getExtras().getString("nickname");
        avatar = getIntent().getExtras().getString("avatar");

        online3Button = findViewById(R.id.online3);
        online10Button = findViewById(R.id.online10);
        online30Button = findViewById(R.id.online30);
        onlineNoTimeButton = findViewById(R.id.onlineNoTime);

        ai3Button = findViewById(R.id.playAI3);
        ai10Button = findViewById(R.id.playAI10);
        ai30Button = findViewById(R.id.playAI30);
        aiNoTimeButton = findViewById(R.id.playAINoTime);

        myFriends = findViewById(R.id.buttonSeeFriends);
        requests = findViewById(R.id.buttonRequests);

        gameRecord = findViewById(R.id.Record);
        ranking = findViewById(R.id.ranking);

        store = findViewById(R.id.shop);

        logOut = findViewById(R.id.buttonLogOut);

        createTournament = findViewById(R.id.buttonCreateTournament);
        joinTournament = findViewById(R.id.buttonJoinTournament);

        online3Button.setOnClickListener(view -> playAgainstOnlineRival(3));
        online10Button.setOnClickListener(view -> playAgainstOnlineRival(10));
        online30Button.setOnClickListener(view -> playAgainstOnlineRival(30));
        onlineNoTimeButton.setOnClickListener(view -> playAgainstOnlineRival(0));

        ai3Button.setOnClickListener(view -> playAgainstAI(3));
        ai10Button.setOnClickListener(view -> playAgainstAI(10));
        ai30Button.setOnClickListener(view -> playAgainstAI(30));
        aiNoTimeButton.setOnClickListener(view -> playAgainstAI(0));

        myFriends.setOnClickListener((view -> seeFriendsList(nickname)));
        requests.setOnClickListener((view -> seeRequests(nickname)));

        store.setOnClickListener((view -> seeStore()));
        gameRecord.setOnClickListener((view -> seeRecord()));
        ranking.setOnClickListener((view-> seeRanking()));
        logOut.setOnClickListener((view -> logOut()));

        createTournament.setOnClickListener(view -> createTournament(nickname));
        joinTournament.setOnClickListener(view -> joinTournament());
    }

    private void createTournament(String nickname) {
        Intent i = new Intent(getApplicationContext(), CreateTournament.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void joinTournament() {
        Intent i = new Intent(getApplicationContext(), JoinTournament.class);
        startActivity(i);
    }

    private void playAgainstOnlineRival(int min) {
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getBoard?nickname="+nickname;
        Log.d("Exito: ", URL );
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {

                    JSONArray obj = new JSONArray(response);
                    JSONObject tablero = obj.getJSONObject(0);
                    String board = tablero.getString("tablero");
                    String pieces = tablero.getString("piezas");
                    Intent i = new Intent(getApplicationContext(), OnlineActivity.class);
                    i.putExtra("nickname", nickname);
                    i.putExtra("time", min);
                    i.putExtra("avatar", avatar);
                    i.putExtra("board", board);
                    i.putExtra("pieces", pieces);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse: ", error.getLocalizedMessage() == null ? "" : error.getLocalizedMessage());
            }
        }){
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


    private void playAgainstAI(int min) {
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getBoard?nickname="+nickname;
        Log.d("Exito: ", URL );
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {

                    JSONArray obj = new JSONArray(response);
                    JSONObject tablero = obj.getJSONObject(0);
                    String board = tablero.getString("tablero");
                    String pieces = tablero.getString("piezas");
                    Intent i = new Intent(getApplicationContext(), AiActivity.class);
                    i.putExtra("nickname", nickname);
                    i.putExtra("time", min);
                    i.putExtra("avatar", avatar);
                    i.putExtra("board", board);
                    i.putExtra("pieces", pieces);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse: ", error.getLocalizedMessage() == null ? "" : error.getLocalizedMessage());
            }
        }){
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

    private void seeFriendsList(String nickname){
        Log.d("res","Cambio actividad");
        Intent i = new Intent(this, FriendsList.class);
        i.putExtra("nickname", nickname);
        i.putExtra("avatar", avatar);
        startActivity(i);
    }

    private void seeRequests(String nickname){
        Log.d("res","Cambio actividad");
        Intent i = new Intent(this, friendRequests.class);
        i.putExtra("nickname", nickname);
        i.putExtra("avatar", avatar);
        startActivity(i);
    }

    private void seeStore(){
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getBoard?nickname="+nickname;
        Log.d("Exito: ", URL );
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {

                    JSONArray obj = new JSONArray(response);
                    JSONObject tablero = obj.getJSONObject(0);
                    String board = tablero.getString("tablero");
                    String pieces = tablero.getString("piezas");
                    Intent i = new Intent(getApplicationContext(), Store.class);
                    i.putExtra("nickname", nickname);
                    i.putExtra("avatar", avatar);
                    i.putExtra("board", board);
                    i.putExtra("pieces", pieces);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse: ", error.getLocalizedMessage() == null ? "" : error.getLocalizedMessage());
            }
        }){
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

    private void seeRecord(){
        Intent i = new Intent(this, GameRecord.class);
        i.putExtra("nickname", nickname);
        i.putExtra("avatar", avatar);
        startActivity(i);
    }

    private void seeRanking(){
        Intent i = new Intent(this, Ranking.class);
        i.putExtra("nickname", nickname);
        i.putExtra("avatar", avatar);
        startActivity(i);
    }

    private void logOut() {
        Intent i = new Intent(this, UserSignIn.class);
        eina.unizar.ajedrez.UserSignIn.mSocket.off();
        eina.unizar.ajedrez.UserSignIn.mSocket.disconnect();
        eina.unizar.ajedrez.UserSignIn.mSocket.close();
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

   /* private void esperarPartida(){
        Log.d("SignIn: ", "Esperando invita");
        eina.unizar.ajedrez.UserSignIn.mSocket.on("getGameInvites", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("SignIn: ", "Llega invita");
                runOnUiThread(new Runnable() {
                    public void run() {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
                        builder.setMessage("Aceptar invitacion");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                JSONObject data = (JSONObject) args[0];
                                try {
                                    Log.d("SignIn: ", "Llega invitacion");
                                    String nomAmigo = data.getString("nick");
                                    //eina.unizar.ajedrez.UserSignIn.mSocket.emit("confirmGameFriend",nickname,nomAmigo);
                                   /* Intent j = new Intent(getApplicationContext(), OnlineActivity.class);
                                    j.putExtra("nickname", nickname);
                                    j.putExtra("avatar", avatar);
                                    j.putExtra("board", board);
                                    j.putExtra("pieces", pieces);
                                    j.putExtra("time", 0);
                                    j.putExtra("nomAmigo", nomAmigo);
                                    startActivity(j);
                                    // Toast.makeText(FriendsList.this,"Funciona", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("Socket: ", data.toString());
                            }
                        });
                        builder.show();
                    }
                });

                //here the data is in JSON Format
            }
        });
    }*/
}