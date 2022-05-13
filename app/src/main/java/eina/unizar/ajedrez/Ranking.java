package eina.unizar.ajedrez;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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

public class Ranking extends AppCompatActivity {
    String nickname;
    RequestQueue queue;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);
        queue = Volley.newRequestQueue(this);

        nickname = getIntent().getExtras().getString("nickname");

        getRanking();
    }

    private void getRanking(){
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/getRankingList?nickname=" + nickname;
        Log.d("Enviando: ", URL);

        LinearLayout layoutInterno = (LinearLayout) findViewById(R.id.listGames);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(35, 15, 5, 0);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response);
                try {
                    JSONArray gamesRequests = new JSONArray((response));
                    JSONObject posJugador;

                    LinearLayout layout2 = new LinearLayout(getApplicationContext());
                    layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    layout2.setOrientation(LinearLayout.HORIZONTAL);

                    TextView header = new TextView(getApplicationContext());
                    header.setLayoutParams(params);
                    header.setPadding(20, 20, 10, 20);
                    header.setText("Jugador");
                    header.setTextColor(Color.parseColor("#FFFFFFFF"));
                    header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    layout2.addView(header);

                    header = new TextView(getApplicationContext());
                    header.setLayoutParams(params);
                    header.setPadding(20, 20, 10, 20);
                    header.setTextColor(Color.parseColor("#FFFFFFFF"));
                    header.setText("Puntos");
                    header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    layout2.addView(header);
                    layoutInterno.addView(layout2);
                    for (int i = 0; i < gamesRequests.length(); i++) {
                       GradientDrawable border = new GradientDrawable();
                        border.setStroke(1, 0xFFFFFFFF); //black border with full opacity
                        posJugador = gamesRequests.getJSONObject(i);
                         layout2 = new LinearLayout(getApplicationContext());
                        layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        layout2.setOrientation(LinearLayout.HORIZONTAL);
                        Log.d("Amigo: ", posJugador.getString("Nickname"));

                        /*TextView position = new TextView(getApplicationContext());
                        position.setLayoutParams(params);
                        position.setPadding(20, 20, 10, 20);
                        position.setText(posJugador.getString("position"));
                        position.setTextColor(Color.parseColor("#FFFFFFFF"));
                        position.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        layout2.addView(position);*/

                        TextView username = new TextView(getApplicationContext());
                        username.setLayoutParams(params);
                        username.setPadding(20, 20, 10, 20);
                        username.setText(posJugador.getString("Nickname"));
                        username.setTextColor(Color.parseColor("#FFFFFFFF"));
                        username.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        layout2.addView(username);

                        TextView puntos = new TextView(getApplicationContext());
                        puntos.setLayoutParams(params);
                        puntos.setPadding(100, 20, 10, 20);
                        puntos.setTextColor(Color.parseColor("#FFFFFFFF"));
                        puntos.setText(posJugador.getString("puntos"));
                        puntos.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        layout2.addView(puntos);
                        layout2.setBackground(border);
                        layoutInterno.addView(layout2);
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("content-type", "application/json");
                //  params.put("Access-Control-Allow-Origin","*");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
}


