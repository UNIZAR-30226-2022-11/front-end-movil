package eina.unizar.ajedrez;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
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

public class JoinTournament extends AppCompatActivity {

        private final String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/obtenerTorneos";
        private RequestQueue queue;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.join_tournament);

            LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.listTournaments);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(35, 15, 5, 0);
            queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, response -> {
                    Log.d("Exito: ", response);
                    try {
                        JSONArray array = new JSONArray(response);
                        System.out.println("Longitud " + array.length());
                        for(int i = 0;i < array.length();i++){
                            JSONObject requester = array.getJSONObject(i);
                            System.out.println(requester);

                            LinearLayout layout2 = new LinearLayout(getApplicationContext());
                            layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            layout2.setOrientation(LinearLayout.HORIZONTAL);

                            String nombre =  requester.getString("creador");
                            int jugadores = requester.getInt("jugadores");
                            TextView textView = new TextView(getApplicationContext());
                            textView.setLayoutParams(params);
                            textView.setPadding(20,10,10,10);
                            if(jugadores == 1) {
                                textView.setText(nombre + ": 1 jugador");
                            } else {
                                textView.setText(nombre + ": " + jugadores + " jugadores");
                            }
                            textView.setTextColor(Color.parseColor("#FFFFFFFF"));
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                            registerForContextMenu(textView);
                            relativeLayout.addView(textView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);
        }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "Unirse al torneo");
      /*  menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, "Invitar a partida 10 mins");
        menu.add(Menu.NONE, Menu.FIRST+2, Menu.NONE, "Invitar a partida 30 mins");
        menu.add(Menu.NONE, Menu.FIRST+3, Menu.NONE, "Invitar a partida sin tiempo");*/
    }
}
