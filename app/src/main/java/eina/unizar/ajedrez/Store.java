package eina.unizar.ajedrez;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Store extends AppCompatActivity {
    private String nickname;
    private int monedas;
    private RequestQueue queue;
    private String actual = "BoardGris";
    String setActual = "default_Piezas";
    private String avatarActual = "";
    private  String compradoBlue, compradoBrown,compradoKnight,compradoStar,compradoFootball, compradoHeart,
    compradoDefault,compradoWhiteBlue,compradoWhiteRed, compradoRedBlue;
    Button comprarGris, equiparGris;
    Button comprarAzul, equiparAzul;
    Button comprarMarron, equiparMarron;
    Button comprarDefault, equiparDefault;
    Button comprarWhiteBlue, equiparWhiteBlue;
    Button comprarWhiteRed, equiparWhiteReed;
    Button comprarRedBlue, equiparRedBlue;
    Button comprarKnight, equiparKnight;
    Button comprarStar, equiparStar;
    Button comprarFootball, equiparFootball;
    Button comprarHeart, equiparHeart;
    Map<String, Integer> tableros = new HashMap<String, Integer>();
    Map<String, Integer>  piezas = new HashMap<String, Integer>();
    Map<String, Integer>  avatares = new HashMap<String, Integer>();
    Set<String> tablerosComprados = new HashSet<String>();
    Set<String> piezasComprados = new HashSet<String>();
    Set<String> avataresComprados = new HashSet<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);
        queue = Volley.newRequestQueue(this);

        nickname = getIntent().getExtras().getString("nickname");
        avatarActual =  getIntent().getExtras().getString("avatar");
        actual =  getIntent().getExtras().getString("board");
        setActual = getIntent().getExtras().getString("pieces");
        TextView usuarioTienda = findViewById(R.id.nomUser);
        usuarioTienda.setText(nickname+": ");
        usuarioTienda.setTextColor(this.getResources().getColor(R.color.white));
        infoTienda();
        //setBoards();
        //setAvatars();


    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), MainPage.class);//OnlineActivity
        i.putExtra("nickname", nickname);
        i.putExtra("avatar", avatarActual);
        startActivity(i);
        //this.finish();
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }*/


    void setBoards(){
        comprarGris = findViewById(R.id.buyGris);
        comprarGris.setText("Comprado");
        comprarGris.setBackgroundColor(Color.GREEN);

        comprarAzul = findViewById(R.id.buyBlue);
        comprarMarron = findViewById(R.id.buyBrown);

        equiparGris = findViewById((R.id.equiparGris));
        equiparAzul = findViewById((R.id.equiparBlue));
        equiparMarron = findViewById((R.id.equiparBrown));
        Log.d("Tableros: ","Comprobando");
        /*Iterator<String> setIterator = tablerosComprados.iterator();
        while(setIterator.hasNext()){
            Log.d("tableros comprados: ", "Uno mas");
        }*/


        Log.d("Tableros: ","Despues de comprobar");
        for (String s : tablerosComprados) {
            Log.d("tableros comprados: ", s);
        }
        if(actual.equals("BoardGris")){
            equiparGris.setText("Equipado");
            equiparGris.setBackgroundColor(Color.GREEN);
        }else if(actual.equals("BoardAzul")){
            equiparAzul.setText("Equipado");
            equiparAzul.setBackgroundColor(Color.GREEN);
        }else{
            equiparMarron.setText("Equipado");
            equiparMarron.setBackgroundColor(Color.GREEN);
        }
        if(tablerosComprados.contains("BoardAzul")){
            Log.d("Numero monedas: ", "Esta el azul");
            compradoBlue = "1";
            comprarAzul.setText("Comprado");
            comprarAzul.setBackgroundColor(Color.GREEN);
        }
        if(tablerosComprados.contains("BoardMarron")){
            compradoBrown ="1";
            comprarMarron.setText("Comprado");
            comprarMarron.setBackgroundColor(Color.GREEN);
        }

        comprarGris.setOnClickListener(view -> buyBoard("BoardGris","board"));
        comprarAzul.setOnClickListener(view -> buyBoard("BoardAzul","board"));
        comprarMarron.setOnClickListener(view -> buyBoard("BoardMarron","board"));

        equiparGris.setOnClickListener(view -> equiparItem("BoardGris", "board"));
        equiparAzul.setOnClickListener(view -> equiparItem("BoardAzul","board"));
        equiparMarron.setOnClickListener(view -> equiparItem("BoardMarron","board"));
    }

    void setPieces(){
        comprarDefault = findViewById(R.id.buyDefault);
        comprarDefault.setText("Comprado");
        comprarDefault.setBackgroundColor(Color.GREEN);
        comprarWhiteBlue = findViewById(R.id.buyBlueWhite);
        comprarWhiteRed = findViewById(R.id.buyWhiteRed);
        comprarRedBlue = findViewById(R.id.buyRedBlue);

        equiparDefault = findViewById((R.id.equiparDefault));
        equiparWhiteBlue = findViewById((R.id.equiparBlueWhite));
        equiparWhiteReed = findViewById((R.id.equiparWhiteRed));
        equiparRedBlue = findViewById((R.id.equiparRedBlue));
        //avatarActual = "knight_avatar";
        Log.d("tableros comprados: ",  "Pasa por aqui");
        for (String s : piezasComprados) {
            Log.d("tableros comprados: ", s);
        }
        if(setActual.equals("default_Piezas")){
            equiparDefault.setText("Equipado");
            equiparDefault.setBackgroundColor(Color.GREEN);
        }else if(setActual.equals("blancoAzul_Piezas")){
            equiparWhiteBlue.setText("Equipado");
            equiparWhiteBlue.setBackgroundColor(Color.GREEN);
        }else if(setActual.equals("blancoRojo_Piezas")){
            equiparWhiteReed.setText("Equipado");
            equiparWhiteReed.setBackgroundColor(Color.GREEN);
        }else if(setActual.equals("rojiAzul_Piezas")){
            equiparRedBlue.setText("Equipado");
            equiparRedBlue.setBackgroundColor(Color.GREEN);
        }
        if(piezasComprados.contains("default_Piezas")){
            compradoDefault = "1";
            comprarDefault.setText("Comprado");
            comprarDefault.setBackgroundColor(Color.GREEN);
        }
        if(piezasComprados.contains("blancoAzul_Piezas")){
            compradoWhiteBlue = "1";
            comprarWhiteBlue.setText("Comprado");
            comprarWhiteBlue.setBackgroundColor(Color.GREEN);
        }
        if(avataresComprados.contains("blancoRojo_Piezas")){
            compradoWhiteRed = "1";
            comprarWhiteRed.setText("Comprado");
            comprarWhiteRed.setBackgroundColor(Color.GREEN);
        }
        if(avataresComprados.contains("rojiAzul_Piezas")){
            compradoRedBlue = "1";
            comprarRedBlue.setText("Comprado");
            comprarRedBlue.setBackgroundColor(Color.GREEN);
        }



        //comprarGris.setOnClickListener(view -> buyBoard(""));
        comprarDefault.setOnClickListener(view -> buyBoard("default_Piezas","set"));
        comprarWhiteBlue.setOnClickListener(view -> buyBoard("blancoAzul_Piezas","set"));
        comprarWhiteRed.setOnClickListener(view -> buyBoard("blancoRojo_Piezas","set"));
        comprarRedBlue.setOnClickListener(view -> buyBoard("rojiAzul_Piezas","set"));

        equiparDefault.setOnClickListener(view -> equiparItem("default_Piezas","set"));
        equiparWhiteBlue.setOnClickListener(view -> equiparItem("blancoAzul_Piezas","set"));
        equiparWhiteReed.setOnClickListener(view -> equiparItem("blancoRojo_Piezas", "set"));
        equiparRedBlue.setOnClickListener(view -> equiparItem("rojiAzul_Piezas", "set"));
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
        //avatarActual = "knight_avatar";
        Log.d("tableros comprados: ",  "Pasa por aqui");
        for (String s : avataresComprados) {
            Log.d("tableros comprados: ", s);
        }
        if(avatarActual.equals("knight_avatar")){
            equiparKnight.setText("Equipado");
            equiparKnight.setBackgroundColor(Color.GREEN);
        }else if(avatarActual.equals("soccer_avatar")){
            equiparFootball.setText("Equipado");
            equiparFootball.setBackgroundColor(Color.GREEN);
        }else if(avatarActual.equals("star_avatar")){
            equiparStar.setText("Equipado");
            equiparStar.setBackgroundColor(Color.GREEN);
        }else if(avatarActual.equals("heart_avatar")){
            equiparHeart.setText("Equipado");
            equiparHeart.setBackgroundColor(Color.GREEN);
        }
        if(avataresComprados.contains("knight_avatar")){
            compradoKnight = "1";
            comprarKnight.setText("Comprado");
            comprarKnight.setBackgroundColor(Color.GREEN);
        }
        if(avataresComprados.contains("soccer_avatar")){
            compradoFootball = "1";
            comprarFootball.setText("Comprado");
            comprarFootball.setBackgroundColor(Color.GREEN);
        }
        if(avataresComprados.contains("star_avatar")){
            compradoStar = "1";
            comprarStar.setText("Comprado");
            comprarStar.setBackgroundColor(Color.GREEN);
        }
        if(avataresComprados.contains("heart_avatar")){
            compradoHeart = "1";
            comprarHeart.setText("Comprado");
            comprarHeart.setBackgroundColor(Color.GREEN);
        }


        //comprarGris.setOnClickListener(view -> buyBoard(""));
        comprarKnight.setOnClickListener(view -> buyBoard("knight_avatar","avatar"));
        comprarFootball.setOnClickListener(view -> buyBoard("soccer_avatar","avatar"));
        comprarStar.setOnClickListener(view -> buyBoard("star_avatar","avatar"));
        comprarHeart.setOnClickListener(view -> buyBoard("heart_avatar","avatar"));

        equiparKnight.setOnClickListener(view -> equiparItem("knight_avatar","avatar"));
        equiparFootball.setOnClickListener(view -> equiparItem("soccer_avatar","avatar"));
        equiparStar.setOnClickListener(view -> equiparItem("star_avatar", "avatar"));
        equiparHeart.setOnClickListener(view -> equiparItem("heart_avatar", "avatar"));
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
                    obj = obj.getJSONObject("coins");
                    monedas = obj.getInt("coins");
                    Log.d("Numero monedas: ", String.valueOf(monedas));
                    TextView numMonedas =  findViewById(R.id.monedas);
                    numMonedas.setText(String.valueOf(monedas) + " coins");
                    numMonedas.setTextColor(getResources().getColor(R.color.white));

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

        URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getBoard?nickname="+nickname;
        Log.d("Enviando: ", URL);

        stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {
                    JSONArray obj = new JSONArray(response);
                    JSONObject tablero = obj.getJSONObject(0);
                    //obj = obj.getJSONObject("board");
                    actual = tablero.getString("tablero");
                    setActual = tablero.getString("piezas");
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
                        }else if(item.getString("tipo").equals("pieces")){
                            piezas.put(item.getString("nombre"),item.getInt("precio"));
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


        URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/inventory?nickname="+nickname;
        Log.d("Enviando: ", URL);

        stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito comprado: ", response );
                try {
                    //  JSONObject obj = new JSONObject(response);
                    //Log.d("Numero monedas: ", String.valueOf(monedas));
                    JSONArray shopItems = new JSONArray(response);
                    JSONObject item;

                    for(int i = 0;i < shopItems.length();i++){
                        item = shopItems.getJSONObject(i);
                        Log.d("Comprado: ", item.toString() );
                        if(item.getString("tipo").equals("table")){
                            Log.d("XQ: ", " Añade table" );
                            tablerosComprados.add(item.getString("ARTICULO_nombre"));
                        }else if(item.getString("tipo").equals("avatar")){
                            Log.d("XQ: ", " Añade avatar" );
                            avataresComprados.add(item.getString("ARTICULO_nombre"));
                        }else if(item.getString("tipo").equals("pieces")){
                            Log.d("XQ: ", " Añade pieza" );
                            piezasComprados.add(item.getString("ARTICULO_nombre"));
                        }
                    }
                    setBoards();
                    setPieces();
                    setAvatars();
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
    private boolean checkBoards(String color){
        if(compradoBlue == "1" && color.equals("BoardAzul") || compradoBrown == "1" && color.equals("BoardMarron") || color.equals("BoardGris")){
            Toast.makeText(Store.this,"El tablero ya esta adquirido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(monedas < 10 && color.equals("BoardMarron") || monedas < 5 && color.equals("BoardAzul")){ // Luego que haya dinero suficiente
            Toast.makeText(Store.this,"No hay sufcientes monedas", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkSets(String color){

        if(color.equals("default_Piezas") || compradoWhiteBlue == "1" && color.equals("blancoAzul_Piezas") ||
                compradoWhiteRed == "1" && color.equals("blancoRojo_Piezas") || compradoRedBlue == "1" && color.equals("rojiAzul_Piezas") ){
            Toast.makeText(Store.this,"El set ya esta adquirido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(monedas < 10 && color.equals("blancoRojo_Piezas") || monedas < 5 && color.equals("blancoAzul_Piezas") ||
                monedas < 15 && color.equals("rojiAzul_Piezas")){ // Luego que haya dinero suficiente
            Toast.makeText(Store.this,"No hay sufcientes monedas", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkAvatars(String color){
        if(color.equals("knight_avatar") || compradoFootball == "1" && color.equals("soccer_avatar") ||
                compradoStar == "1" && color.equals("star_avatar") || compradoHeart == "1" && color.equals("star_avatar") ){
            Toast.makeText(Store.this,"El avatar ya esta adquirido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(monedas < 10 && color.equals("soccer_avatar") || monedas < 5 && color.equals("star_avatar") ||
                monedas < 15 && color.equals("heart_avatar")){ // Luego que haya dinero suficiente
            Toast.makeText(Store.this,"No hay sufcientes monedas", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void buyBoard(String color, String type){
        // Primero comprobar que no esta comprado
        if(type.equals("board") && checkBoards(color) || type.equals("avatar") && checkAvatars(color) || type.equals("set") && checkSets(color)){
            String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/buyItem";
            Log.d("Enviando: ", URL);
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("nickname", nickname);
                jsonBody.put("nombre", color);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String requestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Exito: ", response );
                    try {

                        JSONObject obj = new JSONObject(response);
                        if(obj.getBoolean("exito")){
                            Toast.makeText(Store.this,"Articulo adquirido", Toast.LENGTH_SHORT).show();
                            if(type.equals("board")){
                                tablerosComprados.add(color);
                                setBoards();
                            } else if(type.equals("avatar")){
                                avataresComprados.add(color);
                                setAvatars();
                            }else if(type.equals("set")){
                                piezasComprados.add(color);
                                setPieces();
                            }
                            if(color.equals("BoardAzul") || color.equals("star_avatar") || color.equals("blancoAzul_Piezas")){
                                monedas -= 5;
                            }else if(color.equals("BoardMarron") || color.equals("football_avatar") || color.equals("blancoRojo_Piezas")){
                                monedas -= 10;
                            }else if(color.equals("heart_avatar") || color.equals("rojiAzul_Piezas")){
                                monedas -= 15;
                            }
                            TextView numMonedas =  findViewById(R.id.monedas);
                            numMonedas.setText(String.valueOf(monedas));

                        }else  Toast.makeText(Store.this,"Ha habido un error durante la compra", Toast.LENGTH_SHORT).show();

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
                    //  params.put("Access-Control-Allow-Origin","*");
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);
        }
    }
    private boolean checkSelectedBoard(String color){
        if(actual.equals(color)){
            Toast.makeText(Store.this,"Este color ya esta elegido", Toast.LENGTH_SHORT).show();
            return false;
        }else if(compradoBlue != "1" && color == "BoardAzul" || compradoBrown != "1" && color == "BoardMarron"){
            Toast.makeText(Store.this,"Hay que comprar el tablero para escogerlo", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkSelectedSet(String color){
        if(setActual.equals(color)){
            Toast.makeText(Store.this,"Las piezas ya estan elegidas", Toast.LENGTH_SHORT).show();
            return false;
        }else if(compradoWhiteBlue != "1" && color.equals("blancoAzul_Piezas") || compradoWhiteRed != "1" && color.equals("blancoRojo_Piezas") ||
                compradoRedBlue != "1" && color.equals("rojiAzul_Piezas")){
            Toast.makeText(Store.this,"Hay que comprar las piezas para escogerlo", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkSelectedAvatar(String color){
        if(avatarActual.equals(color)){
            Toast.makeText(Store.this,"El avatar ya esta elegido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(compradoFootball != "1" && color.equals("soccer_avatar") || compradoStar != "1" && color.equals("star_avatar") ||
                compradoHeart != "1" && color.equals("heart_avatar") ){ // Luego que haya dinero suficiente
            Toast.makeText(Store.this,"Hay que comprar el avatar para escogerlo", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void equiparItem(String color,String tipo){
        if(tipo.equals("board") && checkSelectedBoard(color) || tipo.equals("avatar") && checkSelectedAvatar(color) || tipo.equals("set") && checkSelectedSet(color) ){
            String URL = "";
            JSONObject jsonBody = new JSONObject();

            if(tipo.equals("board")) {
                URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/updateTable";
                try {
                    jsonBody.put("nickname", nickname);
                    jsonBody.put("table", color);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(tipo.equals("avatar")){
                URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/updateAvatar";
                try {
                    jsonBody.put("nickname", nickname);
                    jsonBody.put("avatar", color);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (tipo.equals("set")){
                URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/updatePieces";
                try {
                    jsonBody.put("nickname", nickname);
                    jsonBody.put("pieces", color);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            final String requestBody = jsonBody.toString();
            Log.d("Enviando: ", requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Exito: ", response );
                    try {
                        JSONObject obj = new JSONObject(response);
                        if(obj.getBoolean("exito")){
                            Toast.makeText(Store.this,"Eleccion guardada", Toast.LENGTH_SHORT).show();
                            int DEFAULT_GREY = Color.rgb(90,90,90);
                            if(tipo.equals("board")){
                                if(actual.equals("BoardGris")){
                                    equiparGris.setText("SET DEFAULT");
                                    equiparGris.setBackgroundColor(DEFAULT_GREY);
                                }else if(actual.equals("BoardAzul")){
                                    equiparAzul.setText("SET DEFAULT");
                                    equiparAzul.setBackgroundColor(DEFAULT_GREY);
                                }else if(actual.equals("BoardMarron")){
                                    equiparMarron.setText("SET DEFAULT");
                                    equiparMarron.setBackgroundColor(DEFAULT_GREY);
                                }
                                actual = color;
                                setBoards();
                            } else if(tipo.equals("avatar")){
                                if(avatarActual.equals("knight_avatar")){
                                    equiparKnight.setText("SET DEFAULT");
                                    equiparKnight.setBackgroundColor(DEFAULT_GREY);
                                }else if(avatarActual.equals("soccer_avatar")){
                                    equiparFootball.setText("SET DEFAULT");
                                    equiparFootball.setBackgroundColor(DEFAULT_GREY);
                                }else if(avatarActual.equals("star_avatar")){
                                    equiparStar.setText("SET DEFAULT");
                                    equiparStar.setBackgroundColor(DEFAULT_GREY);
                                }else if(avatarActual.equals("heart_avatar")){
                                    equiparHeart.setText("SET DEFAULT");
                                    equiparHeart.setBackgroundColor(DEFAULT_GREY);
                                }
                                avatarActual = color;
                                setAvatars();
                            }else if(tipo.equals(("set"))){
                                if(setActual.equals("default_Piezas")){
                                    equiparDefault.setText("SET DEFAULT");
                                    equiparDefault.setBackgroundColor(DEFAULT_GREY);
                                }else if(setActual.equals("blancoAzul_Piezas")){
                                    equiparWhiteBlue.setText("SET DEFAULT");
                                    equiparWhiteBlue.setBackgroundColor(DEFAULT_GREY);
                                }else if(setActual.equals("blancoRojo_Piezas")){
                                    equiparWhiteReed.setText("SET DEFAULT");
                                    equiparWhiteReed.setBackgroundColor(DEFAULT_GREY);
                                }else if(setActual.equals("rojiAzul_Piezas")){
                                    equiparRedBlue.setText("SET DEFAULT");
                                    equiparRedBlue.setBackgroundColor(DEFAULT_GREY);
                                }
                                setActual = color;
                                setPieces();
                            }

                        }else  Toast.makeText(Store.this,"Ha habido un error durante la compra", Toast.LENGTH_SHORT).show();

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
                    //  params.put("Access-Control-Allow-Origin","*");
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);
        }
    }
}