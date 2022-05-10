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

public class GameRecord  extends AppCompatActivity {

    private String nickname;
    private RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_record);
        queue = Volley.newRequestQueue(this);

        nickname = getIntent().getExtras().getString("nickname");
        TextView setName =  findViewById(R.id.userRecord);
        setName.setText(nickname);

        previousGames();
    }

    void previousGames() {
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/previousGames?nickname=" + nickname;
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
                    JSONObject partida;

                    for (int i = 0; i < gamesRequests.length(); i++) {
                        GradientDrawable border = new GradientDrawable();
                        border.setStroke(1, 0xFFFFFFFF); //black border with full opacity
                        partida = gamesRequests.getJSONObject(i);
                        LinearLayout layout2 = new LinearLayout(getApplicationContext());
                        layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        layout2.setOrientation(LinearLayout.HORIZONTAL);
                        Log.d("Amigo: ", partida.getString("rival"));

                        TextView yo = new TextView(getApplicationContext());
                        yo.setLayoutParams(params);
                        yo.setPadding(20, 20, 10, 20);
                        yo.setText(nickname);
                        yo.setTextColor(Color.parseColor("#FFFFFFFF"));
                        yo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        layout2.addView(yo);

                        TextView rival = new TextView(getApplicationContext());
                        rival.setLayoutParams(params);
                        rival.setPadding(20, 20, 10, 20);
                        rival.setText(partida.getString("rival"));
                        rival.setTextColor(Color.parseColor("#FFFFFFFF"));
                        rival.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        layout2.addView(rival);

                        TextView resultado = new TextView(getApplicationContext());
                        resultado.setLayoutParams(params);
                        resultado.setPadding(20, 20, 10, 20);
                        resultado.setText(partida.getString("resultado"));
                        resultado.setTextColor(Color.parseColor("#FFFFFFFF"));
                        resultado.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        layout2.addView(resultado);

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
