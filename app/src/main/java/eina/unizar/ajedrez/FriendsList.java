package eina.unizar.ajedrez;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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

public class FriendsList extends AppCompatActivity {
    private RequestQueue queue;
    private String nickname;
    private FriendsListBinding binding;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://ec2-18-206-137-85.compute-1.amazonaws.com:3000");
        } catch (URISyntaxException e) {}
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

        try {
            fillData(nickname);
            searchRequests(nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.searchfriend.setOnClickListener(vista -> {
            String username = binding.username.getText().toString();
            try {
                searchForUser(username,nickname);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Verify user account
            // If exists { go to menu } else { displayError }
        });

    }

    public Socket getSocket(){
        return mSocket;
    }

    private void fillData(String nickname) throws JSONException {
        Log.d("Socket: ", "Comprobando conexion");

        if(mSocket.connected()){
            Log.d("Socket: ", "Socket conectado");
        }





       /* Intent i = new Intent(this, UserSignIn.class);
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getFriendList/?nickname="+nickname;
        Log.d("Exito: ", "Se va a buscar a  "+ URL);

        LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.Listfriends);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(35, 15, 5, 0);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray friendRequests = new JSONArray((response));
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
        queue.add(stringRequest);*/
    }

    private void searchForUser(String nickname, String username) throws JSONException {
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/friendRequest";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("nickname", username);
        jsonBody.put("amigo", nickname);
        Log.d("Enviando: ", username + " " + nickname);
        final String requestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                /*JSONObject obj = null;
                try {
                    obj = new JSONObject(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String nombre = obj.getString("resultado");
                    Log.d("res: ", nombre );
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                Toast.makeText(FriendsList.this,response, Toast.LENGTH_SHORT).show();
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

    private void searchRequests( String username) throws JSONException {
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/friendRequest?nickname="+username;
        Log.d("Enviando: ", URL);

        LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.Listfriends);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(35, 15, 5, 0);

       StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {
                    JSONArray friendRequests = new JSONArray((response));
                    JSONObject requester;

                    for(int i = 0;i < friendRequests.length();i++){
                        GradientDrawable border = new GradientDrawable();
                        border.setStroke(1, 0xFFFFFFFF); //black border with full opacity
                        requester = friendRequests.getJSONObject(i);
                        LinearLayout layout2 = new LinearLayout(getApplicationContext());
                        layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        layout2.setOrientation(LinearLayout.HORIZONTAL);
                        Log.d("Amigo: ", requester.getString("valor"));
                        TextView textView = new TextView(getApplicationContext());
                        textView.setLayoutParams(params);
                        textView.setPadding(20,10,10,10);
                        textView.setText(requester.getString("valor"));
                        textView.setTextColor(Color.parseColor("#FFFFFFFF"));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                        layout2.addView(textView);
                        Button aceptar = new Button(getApplicationContext());
                        aceptar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                       // btnTag.setMargins(300,10,10,10);
                        aceptar.setText("Aceptar");
                        aceptar.setId(i);
                        layout2.addView(aceptar);
                        layout2.setBackground(border);
                        relativeLayout.addView(layout2);
                        //aceptar.setOnClickListener(view -> aceptarSolicitud());
                        /*new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // put code on click operation
                            }
                        });*/
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
              //  params.put("Access-Control-Allow-Origin","*");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    /*void aceptarSolicitud(String nuevo amigo){

    }*/
}
