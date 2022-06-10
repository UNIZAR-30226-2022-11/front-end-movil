package eina.unizar.ajedrez;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import io.socket.client.IO;
import io.socket.client.Socket;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eina.unizar.ajedrez.databinding.FriendsListBinding;
import eina.unizar.ajedrez.databinding.UserSignInBinding;
import io.socket.emitter.Emitter;

public class FriendsList extends AppCompatActivity {
    private RequestQueue queue;
    private String nickname;
    private FriendsListBinding binding;
    private Socket mSocket2;
    String board, pieces,avatar;
    ProgressDialog dialog;
    int veces = 0;
    private List<String> pendientes = new ArrayList<>();;
    Map<String, Integer> totUsers = new HashMap<String, Integer>();
    Map<String, Integer> amigos = new HashMap<String, Integer>();
    /*{
        try {
            mSocket = IO.socket("http://ec2-18-206-137-85.compute-1.amazonaws.com:3000");
        } catch (URISyntaxException e) {
            Log.d("Socket: ",   e.toString());
        }
    }*/
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       // mSocket.connect();
        binding = FriendsListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        nickname = getIntent().getExtras().getString("nickname");
        avatar = getIntent().getExtras().getString("avatar");
        Log.d("FriendsList ", "Estoy en onCreate");
        obtenerInfo();
        queue = Volley.newRequestQueue(this);
        obtenerUsers();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    fillData(nickname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 3000);   //5 seconds
       /* try {
            fillData(nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/



        binding.searchfriend.setOnClickListener(vista -> {
            String username = binding.username.getText().toString();
            try {
                if(!username.isEmpty()) searchForUser(username,nickname);
                else  Toast.makeText(FriendsList.this,"Introduzca un nombre de usuario", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Verify user account
            // If exists { go to menu } else { displayError }
        });
    }

    private void obtenerUsers(){
        Log.d("FriendsList ", "Numero de veces" + veces);
        if(veces == 0) {
            eina.unizar.ajedrez.UserSignIn.mSocket.on("usuariosConectados", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    //if(veces == 0) {
                        JSONObject data = (JSONObject) args[0];
                        Log.d("FriendsList ", "llega info users conectados" + data + " numVeces: " + veces);
                        JSONArray users = null;
                        JSONObject user;
                        try {
                            users = data.getJSONArray("usuarios");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < users.length(); i++) {
                            try {
                                user = users.getJSONObject(i);
                                Log.d("FriendsList ", "Siguiente usuario conectado" + user.get("nickname"));
                                totUsers.put(user.getString("nickname"), user.getInt("partida"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
               // }
            });
           veces = 1;//
        }
    }

    private void obtenerInfo(){
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
                     board = tablero.getString("tablero");
                     pieces = tablero.getString("piezas");

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
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "Invitar a partida");
      /*  menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, "Invitar a partida 10 mins");
        menu.add(Menu.NONE, Menu.FIRST+2, Menu.NONE, "Invitar a partida 30 mins");
        menu.add(Menu.NONE, Menu.FIRST+3, Menu.NONE, "Invitar a partida sin tiempo");*/
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
       // TextView tv = ((TextView)info.targetView.findViewById(R.id.YOUR_TEXTVIEW_ID)).getText().toString();
        //Log.d("d: ", "Finalizando actividad" + tv.toString());
        eina.unizar.ajedrez.UserSignIn.mSocket.emit("inviteFriend",nickname,"David");
         dialog=new ProgressDialog(this);
        dialog.setMessage("Esperando amigo");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(true);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("d: ", "Finalizando actividad");
                // stopTimer();
                Intent i = new Intent(getApplicationContext(), MainPage.class);
                i.putExtra("nickname", nickname);
                i.putExtra("avatar",avatar);
                startActivity(i);
            }
        });
        dialog.show();
        switch(item.getItemId()) {
            case Menu.FIRST:
                esperarResp(3);
                return true;
           /* case Menu.FIRST+1:
                esperarResp(10);
                return true;
            case Menu.FIRST+2:
                esperarResp(30);
                return true;
            case Menu.FIRST+3:
                esperarResp(0);
                return true;*/

        }
        return super.onContextItemSelected(item);
    }
    private void esperarResp(int time){
        Log.d("Socket: ", "Esperando rival");
       eina.unizar.ajedrez.UserSignIn.mSocket.on("getFriendOpponent", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("FriendsList ", "llega resupesta del rival");
                JSONObject data = (JSONObject) args[0];
                //here the data is in JSON Format
                try {
                    String nomAmigo = data.getString("nick");
                    dialog.dismiss();
                    Intent i = new Intent(getApplicationContext(), OnlineActivity.class);
                    i.putExtra("nickname", nickname);
                    i.putExtra("avatar", avatar);
                    i.putExtra("board", board);
                    i.putExtra("pieces", pieces);
                    i.putExtra("time",0);
                    i.putExtra("nomAmigo", nomAmigo);
                    startActivity(i);
                    //Toast.makeText(FriendsList.this,"Funciona", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Socket: ", data.toString());
            }
        });
    }


    private void fillData(String nickname) throws JSONException {

        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getFriendList?nickname="+nickname;
        Log.d("Exito: ", "Se va a buscar a  "+ URL);

        LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.Listfriends);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(35, 15, 5, 0);
     /*   ImageView conexion = new ImageView(this);
        conexion.getLayoutParams().height = 50;
        conexion.getLayoutParams().width = 50;*/
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject respuesta = new JSONObject((response));
                    JSONArray friendRequests =respuesta.getJSONArray("friendList");
                    Log.d("Amigo: ", friendRequests.toString());
                    JSONObject requester;

                    for(int i = 0;i < friendRequests.length();i++){
                        requester = friendRequests.getJSONObject(i);
                        LinearLayout layout2 = new LinearLayout(getApplicationContext());
                        layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        layout2.setOrientation(LinearLayout.HORIZONTAL);

                        Log.d("Amigo: ", requester.getString("valor"));
                        String nombre =  requester.getString("valor");
                        TextView textView = new TextView(getApplicationContext());
                        textView.setLayoutParams(params);
                        textView.setPadding(20,10,10,10);
                        textView.setText(requester.getString("valor"));
                        textView.setTextColor(Color.parseColor("#FFFFFFFF"));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);

                        amigos.put(nombre,i);

                        //conexion.setLayoutParams(params);

                     //   conexion.setPadding(20,10,10,10);
                        if(totUsers.containsKey(nombre)){
                            int partida = totUsers.get(requester.getString("valor"));
                            if(partida == 0){
                               // conexion.setImageResource(R.drawable.green_dot);
                                textView.setText(nombre+"   Conectado");
                               textView.setTextColor(Color.GREEN);
                                registerForContextMenu(textView);
                            }else{
                               // conexion.setImageResource(R.drawable.orange_dot);
                                textView.setText(nombre+"   En partida");
                                textView.setTextColor(Color.YELLOW);
                            }

                        }else{
                            textView.setText(nombre+"   Desconectado");
                            textView.setTextColor(Color.RED);
                            //conexion.setImageResource(R.drawable.red_dot);
                        }
                       /* conexion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));*/
                        //layout2.addView(textView);
                        //relativeLayout.addView(conexion);
                     //   textView.setId
                       // relativeLayout.addView(conexion);
                        relativeLayout.addView(textView);


                    }
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

    private void searchForUser(String friend, String username) throws JSONException {

        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getFriendRequest";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("nickname", username);
        jsonBody.put("amigo", friend);

        final String requestBody = jsonBody.toString();
        Log.d("Enviando: ", requestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {
                    JSONObject obj = new JSONObject((response));
                    String res = obj.getString("resultado");
                    if(res.equals("El amigo no existe")){
                        Toast toast = Toast.makeText(FriendsList.this, "Este usuario no existe" , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(FriendsList.this,"Este usuario no existe", Toast.LENGTH_SHORT).show();
                    }else if(res.equals("El usuario existe pero ya es amigo tuyo")){
                        Toast toast = Toast.makeText(FriendsList.this, "Este usuario ya es amigo tuyo" , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        // Toast.makeText(FriendsList.this,"Este usuario ya es amigo tuyo", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast toast = Toast.makeText(FriendsList.this, obj.getString("resultado") , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        //Toast.makeText(FriendsList.this,obj.getString("resultado"), Toast.LENGTH_SHORT).show();
                    }
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
