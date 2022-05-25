package eina.unizar.ajedrez;

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
    private Socket mSocket;
    private List<String> pendientes = new ArrayList<>();;
    {
        try {
            mSocket = IO.socket("http://ec2-18-206-137-85.compute-1.amazonaws.com:3000");
        } catch (URISyntaxException e) {
            Log.d("Socket: ",   e.toString());
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mSocket.connect();
        binding = FriendsListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        nickname = getIntent().getExtras().getString("nickname");
        queue = Volley.newRequestQueue(this);


        // Actions to do after 5 seconds
        try {
            fillData(nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }



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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "Invitar a partida 3 mins");
        menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, "Invitar a partida 10 mins");
        menu.add(Menu.NONE, Menu.FIRST+2, Menu.NONE, "Invitar a partida 30 mins");
        menu.add(Menu.NONE, Menu.FIRST+3, Menu.NONE, "Invitar a partida sin tiempo");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Intent i = new Intent(getApplicationContext(), OnlineActivity.class);
        i.putExtra("nickname", nickname);
        switch(item.getItemId()) {
            case Menu.FIRST:
                i.putExtra("time",3);
                startActivity(i);
                Toast.makeText(FriendsList.this,"Funciona", Toast.LENGTH_SHORT).show();
                return true;
            case Menu.FIRST+1:
                i.putExtra("time",10);
                startActivity(i);
                Toast.makeText(FriendsList.this,"Funciona", Toast.LENGTH_SHORT).show();
                return true;
            case Menu.FIRST+2:
                i.putExtra("time",30);
                startActivity(i);
                Toast.makeText(FriendsList.this,"Funciona", Toast.LENGTH_SHORT).show();
                return true;
            case Menu.FIRST+3:
                i.putExtra("time",0);
                startActivity(i);
                Toast.makeText(FriendsList.this,"Funciona", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onContextItemSelected(item);
    }

    public Socket getSocket(){
        return mSocket;
    }

    private void fillData(String nickname) throws JSONException {
        Log.d("Socket: ", "Comprobando conexion " + mSocket.connected());

       /* if(mSocket.connected()){
            Log.d("Socket: ", "Socket conectado");
        }
     //   mSocket.emit("connection");
        mSocket.on("getOpponent", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject)args[0];
//here the data is in JSON Format
                Log.d("Socket: ", data.toString());
               // Toast.makeText(FriendsList.this, data.toString(), Toast.LENGTH_SHORT).show();
            }
        });*/

        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getFriendList?nickname="+nickname;
        Log.d("Exito: ", "Se va a buscar a  "+ URL);

        LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.Listfriends);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(35, 15, 5, 0);
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
                        Log.d("Amigo: ", requester.getString("valor"));
                        TextView textView = new TextView(getApplicationContext());
                        textView.setLayoutParams(params);
                        textView.setPadding(20,10,10,10);
                        textView.setText(requester.getString("valor"));
                        textView.setTextColor(Color.parseColor("#FFFFFFFF"));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                        relativeLayout.addView(textView);
                        registerForContextMenu(textView);

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
                        Toast.makeText(FriendsList.this,"Este usuario no existe", Toast.LENGTH_SHORT).show();
                    }else if(res.equals("El usuario existe pero ya es amigo tuyo")){
                        Toast.makeText(FriendsList.this,"Este usuario ya es amigo tuyo", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(FriendsList.this,obj.getString("resultado"), Toast.LENGTH_SHORT).show();
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
