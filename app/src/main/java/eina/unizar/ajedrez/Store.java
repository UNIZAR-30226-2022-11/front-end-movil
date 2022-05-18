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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Store extends AppCompatActivity {
    private String nickname;
    private int monedas;
    private RequestQueue queue;
    private String actual = "1";
    private String compradoBlue, compradoBrown,compradoKnight,compradoStar,compradoFootball, compradoHeart;
    Button comprarGris, equiparGris;
    Button comprarAzul, equiparAzul;
    Button comprarMarron, equiparMarron;
    Button comprarKnight, equiparKnight;
    Button comprarStar, equiparStar;
    Button comprarFootball, equiparFootball;
    Button comprarHeart, equiparHeart;
    Map<String, Integer> tableros = new HashMap<String, Integer>();
    Map<String, Integer>  avatares = new HashMap<String, Integer>();
    Set<String> tablerosComprados = new HashSet<String>();
    Set<String> avataresComprados = new HashSet<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);
        queue = Volley.newRequestQueue(this);

        nickname = getIntent().getExtras().getString("nickname");
        TextView usuarioTienda = findViewById(R.id.nomUser);
        usuarioTienda.setText(nickname+": ");
        setBoards();
        setAvatars();
        TextView numMonedas =  findViewById(R.id.monedas);
        numMonedas.setText(String.valueOf(monedas));

    }

    void setBoards(){
        comprarGris = findViewById(R.id.buyGris);
        comprarGris.setText("Comprado");
        comprarGris.setBackgroundColor(Color.GREEN);

        comprarAzul = findViewById(R.id.buyBlue);
        comprarMarron = findViewById(R.id.buyBrown);

        equiparGris = findViewById((R.id.equiparGris));
        equiparAzul = findViewById((R.id.equiparBlue));
        equiparMarron = findViewById((R.id.equiparBrown));

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
        if(tablerosComprados.contains("BoardAzul")){
            comprarAzul.setText("Comprado");
            comprarAzul.setBackgroundColor(Color.GREEN);
        }
        if(tablerosComprados.contains("BoardMarron")){
            comprarMarron.setText("Comprado");
            comprarMarron.setBackgroundColor(Color.GREEN);
        }

        //comprarGris.setOnClickListener(view -> buyBoard(""));
        comprarAzul.setOnClickListener(view -> buyBoard("Azul"));
        comprarMarron.setOnClickListener(view -> buyBoard("Marron"));

        equiparGris.setOnClickListener(view -> equiparItem("Gris", "tablero"));
        equiparAzul.setOnClickListener(view -> equiparItem("Azul","tablero"));
        equiparMarron.setOnClickListener(view -> equiparItem("Marron","tablero"));
    }

    void setAvatars(){
        comprarKnight = findViewById(R.id.buyKnight);
        comprarKnight.setText("Comprado");
        comprarKnight.setBackgroundColor(Color.GREEN);
        comprarFootball = findViewById(R.id.buyFootball);
        comprarStar = findViewById(R.id.buyStar);
        comprarHeart = findViewById(R.id.buyHeart);

        equiparKnight = findViewById((R.id.equiparKnight));
        equiparFootball = findViewById((R.id.equiparFootball));
        equiparStar = findViewById((R.id.equiparStar));
        equiparHeart = findViewById((R.id.equiparHeart));
        actual = "0";
        if(actual == "1"){
            equiparKnight.setText("Equipado");
            equiparKnight.setBackgroundColor(Color.GREEN);
        }else if(actual == "2"){
            equiparFootball.setText("Equipado");
            equiparFootball.setBackgroundColor(Color.GREEN);
        }else if(actual == "3"){
            equiparStar.setText("Equipado");
            equiparStar.setBackgroundColor(Color.GREEN);
        }else if(actual == "4"){
            equiparHeart.setText("Equipado");
            equiparHeart.setBackgroundColor(Color.GREEN);
        }
        if(avataresComprados.contains("knight_avatar")){
            comprarKnight.setText("Comprado");
            comprarKnight.setBackgroundColor(Color.GREEN);
        }
        if(avataresComprados.contains("soccer_avatar")){
            comprarFootball.setText("Comprado");
            comprarFootball.setBackgroundColor(Color.GREEN);
        }
        if(avataresComprados.contains("star_avatar")){
            comprarStar.setText("Comprado");
            comprarStar.setBackgroundColor(Color.GREEN);
        }
        if(avataresComprados.contains("heart_avatar")){
            comprarHeart.setText("Comprado");
            comprarHeart.setBackgroundColor(Color.GREEN);
        }


        //comprarGris.setOnClickListener(view -> buyBoard(""));
        comprarKnight.setOnClickListener(view -> buyBoard("Azul"));
        comprarFootball.setOnClickListener(view -> buyBoard("Marron"));
        comprarStar.setOnClickListener(view -> buyBoard("Azul"));
        comprarHeart.setOnClickListener(view -> buyBoard("Marron"));

        equiparKnight.setOnClickListener(view -> equiparItem("Knight","avatar"));
        equiparFootball.setOnClickListener(view -> equiparItem("Soccer","avatar"));
        equiparStar.setOnClickListener(view -> equiparItem("Star", "avatar"));
        equiparHeart.setOnClickListener(view -> equiparItem("Heart", "avatar"));
    }

    void infoTienda(){
       String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getCoins?nickname="+nickname;
        Log.d("Enviando: ", URL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {
                    JSONObject obj = new JSONObject(response);
                    monedas = obj.getInt("coins");
                    Log.d("Numero monedas: ", String.valueOf(monedas));

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

         URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getShop?nickname="+nickname;
        Log.d("Enviando: ", URL);

         stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito tienda: ", response );
                try {
                    JSONObject obj = new JSONObject(response);
                    //Log.d("Numero monedas: ", String.valueOf(monedas));
                    JSONArray shopItems = obj.getJSONArray("articulos");
                    JSONObject item;

                    for(int i = 0;i < shopItems.length();i++){
                        item = shopItems.getJSONObject(i);
                        Log.d("Tienda: ", item.toString() );
                        if(item.getString("tipo").equals("table")){
                            tableros.put(item.getString("nombre"),item.getInt("precio"));
                        }else if(item.getString("tipo").equals("avatar")){
                            avatares.put(item.getString("nombre"),item.getInt("precio"));
                        }
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
       // monedas="4";


        URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getInventory?nickname="+nickname;
        Log.d("Enviando: ", URL);

        stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito comprado: ", response );
                try {
                    JSONObject obj = new JSONObject(response);
                    //Log.d("Numero monedas: ", String.valueOf(monedas));
                    JSONArray shopItems = obj.getJSONArray("articulos");
                    JSONObject item;

                    for(int i = 0;i < shopItems.length();i++){
                        item = shopItems.getJSONObject(i);
                        Log.d("Comprado: ", item.toString() );
                        if(item.getString("tipo").equals("table")){
                            tablerosComprados.add(item.getString("nombre"));
                        }else if(item.getString("tipo").equals("avatar")){
                            Log.d("XQ: ", " Añade avatar" );
                            avataresComprados.add(item.getString("nombre"));
                        }
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

    private void buyBoard(String color){
        if(monedas < 10 && color == "Marron" || monedas < 5 && color == "Azul"){
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
                    monedas = obj.getInt("coins");
                    String res = obj.getString("resultado");
                    Log.d("Numero monedas: ", String.valueOf(monedas));
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
    private void equiparItem(String color,String tipo){
        if(color == "Gris" && actual == "1" || color == "Azul" && actual == "2" || color == "Marron" && actual == "3"){
            Toast.makeText(Store.this,"Este color ya esta elegido", Toast.LENGTH_SHORT).show();
        }else if(compradoBlue != "1" && color == "Azul" || compradoBrown != "1" && color == "Marron"){
            Toast.makeText(Store.this,"Hay que comprar el tablero para escogerlo", Toast.LENGTH_SHORT).show();
        }
        else{
            String URL = "";
            if(tipo.equals("tablero")) {
                 URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/setBoard?nickname=&color=" + nickname;
            }else if(tipo.equals("avatar")){

            }
            Log.d("Enviando: ", URL);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Exito: ", response );
                    try {
                        JSONObject obj = new JSONObject(response);
                        monedas = obj.getInt("monedas");
                        String res = obj.getString("resultado");
                        Log.d("Numero monedas: ", String.valueOf(monedas));
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
