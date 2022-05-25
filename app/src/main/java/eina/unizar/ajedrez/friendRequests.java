package eina.unizar.ajedrez;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eina.unizar.ajedrez.databinding.FriendsRequestsBinding;
import io.socket.client.Socket;

public class friendRequests extends AppCompatActivity {
    private RequestQueue queue;
    private String nickname;
    private FriendsRequestsBinding binding;
    //private Socket mSocket;
    private List<String> pendientes = new ArrayList<>();;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mSocket.connect();
        binding = FriendsRequestsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        nickname = getIntent().getExtras().getString("nickname");
        queue = Volley.newRequestQueue(this);

        try {
            searchRequests(nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void searchRequests( String username) throws JSONException {
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getFriendRequest?nickname="+username;
        Log.d("Enviando: ", URL);

        LinearLayout layoutInterno = (LinearLayout) findViewById(R.id.ListRequests);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(35, 15, 5, 0);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {
                    JSONObject respuesta = new JSONObject((response));
                    JSONArray friendRequests = respuesta.getJSONArray("friendRequests");
                    JSONObject requester;

                    for(int i = 0;i < friendRequests.length();i++){
                        GradientDrawable border = new GradientDrawable();
                        border.setStroke(1, 0xFFFFFFFF); //black border with full opacity
                        requester = friendRequests.getJSONObject(i);
                        LinearLayout layout2 = new LinearLayout(getApplicationContext());
                        layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        layout2.setOrientation(LinearLayout.HORIZONTAL);
                        Log.d("Amigo: ", requester.getString("valor"));
                        pendientes.add( requester.getString("valor")); // Añadir a lista para luego saber que solicitud ha sido aceptada
                        TextView textView = new TextView(getApplicationContext());
                        textView.setLayoutParams(params);
                        textView.setPadding(20,20,10,20);
                        textView.setText(requester.getString("valor"));
                        textView.setTextColor(Color.parseColor("#FFFFFFFF"));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                        layout2.addView(textView);
                        Button aceptar = new Button(getApplicationContext());
                        aceptar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        // aceptar.setMargins(300,10,10,10);
                        aceptar.setText("Aceptar");
                        aceptar.setId(i);
                        layout2.addView(aceptar);
                        layout2.setBackground(border);
                        aceptar.setOnClickListener(view -> {
                            try {
                                aceptarSolicitud(nickname,pendientes.get(aceptar.getId()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                        Button rechazar = new Button(getApplicationContext());
                        rechazar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        // aceptar.setMargins(300,10,10,10);
                        rechazar.setText("Rechazar");
                        rechazar.setId(i);
                        layout2.addView(rechazar);
                        layout2.setBackground(border);
                        layoutInterno.addView(layout2);
                        rechazar.setOnClickListener(view -> {
                            try {
                                rechazarSolicitud(nickname,pendientes.get(aceptar.getId()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
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
    void aceptarSolicitud(String nickname, String  nuevoAmigo) throws JSONException {// La peticion al back-end está todavía por hacer

        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/acceptFriendRequest";
        Log.d("Enviando: ", URL);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("nickname", nuevoAmigo);
        jsonBody.put("amigo", nickname);
        Log.d("Enviando: ", nuevoAmigo + " " + nickname);
        final String requestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("d: ", "Nuevo amigo añadido" +response );
                Toast.makeText(friendRequests.this,"Nuevo amigo " + nuevoAmigo, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainPage.class);
                i.putExtra("nickname", nickname);
                startActivity(i);
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
                //  params.put("Access-Control-Allow-Origin","*");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

        // Volver a cargar pantalla
    }
    void rechazarSolicitud(String nickname, String  nuevoAmigo) throws JSONException {// La peticion al back-end está todavía por hacer

        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/declineFriendRequest";
        Log.d("Enviando: ", URL);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("nickname", nuevoAmigo );
        jsonBody.put("amigo", nickname);
        Log.d("Enviando: ", nuevoAmigo + " " + nickname);
        final String requestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("d: ", "Peticion rechazada a " +response );
                Toast.makeText(friendRequests.this,"Peticion rechazada a " + nuevoAmigo + " añadido.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainPage.class);
                i.putExtra("nickname", nickname);
                startActivity(i);
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
                //  params.put("Access-Control-Allow-Origin","*");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

        // Volver a cargar pantalla
    }
}
