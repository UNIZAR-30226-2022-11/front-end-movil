package eina.unizar.ajedrez;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import java.util.Map;

import eina.unizar.ajedrez.databinding.UserSignUpBinding;

public class UserSignUp extends AppCompatActivity {

    private UserSignUpBinding binding;
    String username;
    String fullName;
    String email;
    String password;
    String repeatPassword;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        binding = UserSignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.button.setOnClickListener(vista -> {
            username = binding.username.getEditText().getText().toString();
            fullName = binding.fullName.getEditText().getText().toString();
            email = binding.email.getEditText().getText().toString();
            password = binding.password.getEditText().getText().toString();
            repeatPassword = binding.repeatPassword.getEditText().getText().toString();
            Log.d("res", "probado");
            // Register new user account
            try {
                registrarUsuario();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setResult(RESULT_OK);
            finish();
        });
    }

    private boolean registrarUsuario() throws JSONException {
        Intent i = new Intent(this, UserSignIn.class);
      /*  if(password != repeatPassword || username == "" || username == null){ // No coincide la contraseña
            return false;
        }*/
        Log.d("res", "llega "+ username + " " + repeatPassword + " " +password);
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/register";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("nickname", username);
        jsonBody.put("password", password);
        jsonBody.put("email", email);
        final String requestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("res", response);
                try {
                   // JSONArray obj1 = new JSONArray((response));
                    //JSONObject exito =  obj1.getJSONObject(0);
                   // JSONObject datos =  obj1.getJSONObject(1);
                  //  Log.d("Exito: ", exito.getString("exito")+ "y username" +username);
                    JSONObject obj = new JSONObject(response);
                    boolean bien = obj.getBoolean("exito");
                    if(bien) {
                        startActivity(i);
                        Log.d("res", "Cambio actividad " + bien);
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
}