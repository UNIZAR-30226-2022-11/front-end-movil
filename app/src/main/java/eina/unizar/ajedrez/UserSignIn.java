package eina.unizar.ajedrez;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import eina.unizar.ajedrez.databinding.UserSignInBinding;

public class UserSignIn extends AppCompatActivity{

    private UserSignInBinding binding;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UserSignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        queue = Volley.newRequestQueue(this);
        binding.login.setOnClickListener(vista -> {
            String username = binding.username.getEditText().getText().toString();
            String password = binding.password.getEditText().getText().toString();

            // Verify user account
            // If exists { go to menu } else { displayError }
            try {
                displayMenu(username,password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        binding.register.setOnClickListener(vista -> registerUser());
    }

    private boolean obtenerDatos(String username, String password) throws JSONException {
        Intent i = new Intent(this, MainPage.class);
        if(username ==  "" || password == "" || username ==  null || password == null){
            Log.d("d: ", "Esta vacio" );
            return false;
        }
       String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/login";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("nickname", username);
        jsonBody.put("password", password);
        final String requestBody = jsonBody.toString();
      StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("res", response);
                try {
                    //JSONArray obj1 = new JSONArray((response));
                   //JSONObject ex =  obj1.getJSONObject(0);
                   //Log.d("Exito: ", ex.getString("exito")+ "y username" +username);
                    JSONObject obj = new JSONObject(response);
                    String nombre = obj.getString("nickname").toString();
                    if( nombre.equals(username)){
                        i.putExtra("nickname", nombre);
                        startActivity(i);
                        Log.d("Exito: ", obj.getString("nickname")+ "y username" +username);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserSignIn.this,"Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
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

        return false;
    }

    private void displayMenu(String username, String password) throws JSONException {
        queue = Volley.newRequestQueue(this);
        obtenerDatos(username, password);

    }

    private void registerUser() {
        Intent i = new Intent(this, UserSignUp.class);
        startActivity(i);
    }

    /*private void validarUsuario(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    if(!response.isEmpty()){
                        Intent i = new Intent(getApplicationContext(), MainPage.class);
                        startActivity(i);
                    }else{
                        Toast.makeText(UserSignIn.this,"Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserSignIn.this,error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user",edtUsuario.get)
                return super.getParams();
            }
        };
    }*/
}