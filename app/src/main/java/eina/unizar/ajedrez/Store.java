package eina.unizar.ajedrez;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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

import java.util.HashMap;
import java.util.Map;

public class Store extends AppCompatActivity {
    private String nickname;
    private String monedas;
    private RequestQueue queue;
    private String actual = "1";
    private String compradoBlue, compradoBrown;
    Button comprarGris, equiparGris;
    Button comprarAzul, equiparAzul;
    Button comprarMarron, equiparMarron;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);
        queue = Volley.newRequestQueue(this);

        comprarGris = findViewById(R.id.buyGris);
        comprarGris.setText("Comprado");
        comprarGris.setBackgroundColor(Color.GREEN);

        comprarAzul = findViewById(R.id.buyBlue);
        comprarMarron = findViewById(R.id.buyBrown);

        equiparGris = findViewById((R.id.equiparGris));
        equiparAzul = findViewById((R.id.equiparBlue));
        equiparMarron = findViewById((R.id.equiparBrown));

        nickname = getIntent().getExtras().getString("nickname");
        TextView usuarioTienda = findViewById(R.id.nomUser);
        usuarioTienda.setText(nickname+": ");
        infoTienda();
        if(actual == "1"){
            equiparGris.setText("Equipado");
            equiparGris.setBackgroundColor(Color.GREEN);
        }else if(actual == "2"){
            equiparAzul.setText("Equipado");
            equiparAzul.setBackgroundColor(Color.GREEN);
        }else{
            equiparMarron.setText("Equipado");
            equiparMarron.setBackgroundColor(Color.GREEN);
        }
        if(compradoBlue == "1"){
            comprarAzul.setText("Comprado");
            comprarAzul.setBackgroundColor(Color.GREEN);
        }
        if(compradoBrown == "1"){
            comprarMarron.setText("Comprado");
            comprarMarron.setBackgroundColor(Color.GREEN);
        }
        TextView numMonedas =  findViewById(R.id.monedas);
        numMonedas.setText(monedas);

        //comprarGris.setOnClickListener(view -> buyBoard(""));
        comprarAzul.setOnClickListener(view -> buyBoard("Azul"));
        comprarMarron.setOnClickListener(view -> buyBoard("Marron"));

        equiparGris.setOnClickListener(view -> equiparBoard("Gris"));
        equiparAzul.setOnClickListener(view -> equiparBoard("Azul"));
        equiparMarron.setOnClickListener(view -> equiparBoard("Marron"));
    }

    void infoTienda(){
       /* String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getStoreInfo?nickname="+nickname;
        Log.d("Enviando: ", URL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {
                    JSONObject obj = new JSONObject(response);
                    monedas = obj.getString("monedas");
                    Log.d("Numero monedas: ", monedas);

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
        queue.add(stringRequest);*/
        monedas="4";
    }

    private void buyBoard(String color){
        if(Integer.parseInt(monedas) < 10 && color == "Marron" || Integer.parseInt(monedas) < 5 && color == "Azul"){
            Toast.makeText(Store.this,"No hay sufcientes monedas", Toast.LENGTH_SHORT).show();
        }else if(compradoBlue == "1" && color == "Azul" || compradoBrown == "1" && color == "Marron"){
            Toast.makeText(Store.this,"El tablero ya esta adquirido", Toast.LENGTH_SHORT).show();
        }
        else {
             String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/buyBoard?nickname=&color="+nickname;
        Log.d("Enviando: ", URL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {
                    JSONObject obj = new JSONObject(response);
                    monedas = obj.getString("monedas");
                    String res = obj.getString("resultado");
                    Log.d("Numero monedas: ", monedas);
                    Toast.makeText(Store.this,res, Toast.LENGTH_SHORT).show();
                    comprarGris = findViewById(R.id.buyGris);
                    comprarGris.setText("Comprado");
                    comprarGris.setBackgroundColor(Color.GREEN);

                    TextView numMonedas =  findViewById(R.id.monedas);
                    numMonedas.setText(monedas);

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
    }
    private void equiparBoard(String color){
        if(color == "Gris" && actual == "1" || color == "Azul" && actual == "2" || color == "Marron" && actual == "3"){
            Toast.makeText(Store.this,"Este color ya esta elegido", Toast.LENGTH_SHORT).show();
        }else if(compradoBlue != "1" && color == "Azul" || compradoBrown != "1" && color == "Marron"){
            Toast.makeText(Store.this,"Hay que comprar el tablero para escogerlo", Toast.LENGTH_SHORT).show();
        }
        else{
            String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/setBoard?nickname=&color="+nickname;
            Log.d("Enviando: ", URL);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Exito: ", response );
                    try {
                        JSONObject obj = new JSONObject(response);
                        monedas = obj.getString("monedas");
                        String res = obj.getString("resultado");
                        Log.d("Numero monedas: ", monedas);
                        Toast.makeText(Store.this,res, Toast.LENGTH_SHORT).show();
                        equiparGris = findViewById((R.id.equiparGris));
                        equiparGris.setText("Equipado");
                        equiparGris.setBackgroundColor(Color.GREEN);

                        TextView numMonedas =  findViewById(R.id.monedas);
                        numMonedas.setText(monedas);

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
    }

}
