package eina.unizar.ajedrez;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CreateTournament extends AppCompatActivity {

    private final String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/crearTorneo";
    String nickname;
    String code;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_tournament);

        queue = Volley.newRequestQueue(this);

        nickname = getIntent().getExtras().getString("nickname");
        System.out.println("tournament: " + nickname);

        Button buttonCreateTournament = findViewById(R.id.crearTorneo);
        Button buttonDeleteTournament = findViewById(R.id.borrarTorneo);
        buttonCreateTournament.setOnClickListener(view -> createTournament());
        buttonDeleteTournament.setOnClickListener(view -> deleteTournament());
    }

    private void deleteTournament() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nickname", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();
        System.out.println("requestbody " + requestBody);
        final String oURL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/borrarTorneo";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, oURL, response -> {
            Log.d("res", response);
            try {
                JSONObject obj = new JSONObject(response);
                boolean exito = obj.getBoolean("exito");
                Log.d("res", "Resultado borrar torneo = " + exito);
                if(exito) {
                    Toast.makeText(CreateTournament.this,"Torneo borrado", Toast.LENGTH_SHORT).show();
                    //startActivity(i);
                }
                else {
                    Toast.makeText(CreateTournament.this,"No se ha podido borrar el torneo", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(CreateTournament.this,"No se ha podido borrar el torneo", Toast.LENGTH_SHORT).show()){
            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public Map<String,String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("content-type","application/json");
                params.put("Access-Control-Allow-Origin","*");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void createTournament() {
        //Intent i = new Intent(this, MainPage.class);
        EditText codTorneo = findViewById(R.id.codigoTorneo);
        code = codTorneo.getText().toString();
        if(code.equals("")) {
            Toast.makeText(CreateTournament.this,"Debe introducir un código", Toast.LENGTH_SHORT).show();
        }
        else {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("nickname", nickname);
                jsonBody.put("codigo", code);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String requestBody = jsonBody.toString();
            System.out.println("requestbody " + requestBody);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
                Log.d("res", response);
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean exito = obj.getBoolean("exito");
                    Log.d("res", "Resultado crear torneo = " + exito);
                    if(exito) {
                        Toast.makeText(CreateTournament.this,"Torneo creado", Toast.LENGTH_SHORT).show();
                        //startActivity(i);
                    }
                    else {
                        Toast.makeText(CreateTournament.this,"No se ha podido crear el torneo", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(CreateTournament.this,"¡Ya tienes un torneo activo!", Toast.LENGTH_SHORT).show()){
                @Override
                public byte[] getBody() {
                    return requestBody.getBytes(StandardCharsets.UTF_8);
                }
                @Override
                public Map<String,String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put("content-type","application/json");
                    params.put("Access-Control-Allow-Origin","*");
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);
        }

        /*StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.d("res", response);
            try {
                JSONObject obj = new JSONObject(response);
                boolean exito = obj.getBoolean("exito");
                Log.d("res", "Resultado crear torneo = " + exito);
                if(exito) {
                    Toast.makeText(CreateTournament.this,"Torneo creado", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                }
                else {
                    Toast.makeText(CreateTournament.this,"No se ha podido crear el torneo", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("onErrorResponse", error.getLocalizedMessage() == null ? "" : error.getLocalizedMessage())){
            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public Map<String,String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("content-type","application/json");
                params.put("Access-Control-Allow-Origin","*");
                return params;
            }
        };*/
    }
}
