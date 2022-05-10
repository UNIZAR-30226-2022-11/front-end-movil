package eina.unizar.ajedrez;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);
        queue = Volley.newRequestQueue(this);

        nickname = getIntent().getExtras().getString("nickname");
        TextView usuarioTienda = findViewById(R.id.nomUser);
        usuarioTienda.setText(nickname+": ");
        infoTienda();
        TextView numMonedas =  findViewById(R.id.monedas);
        numMonedas.setText(monedas);
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
}
