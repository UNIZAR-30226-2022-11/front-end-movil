package eina.unizar.ajedrez;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import eina.unizar.ajedrez.databinding.UserSignInBinding;

public class UserSignIn extends AppCompatActivity{

    private final String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/login";

    private UserSignInBinding binding;
    private String username;
    private String password;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UserSignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        queue = Volley.newRequestQueue(this);
        binding.login.setOnClickListener(vista -> {
            username = Objects.requireNonNull(binding.username.getEditText()).getText().toString();
            password = Objects.requireNonNull(binding.password.getEditText()).getText().toString();
            // Verify user account
            // If exists { go to menu } else { displayError }
            try {
                tryLogin(username, password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        binding.register.setOnClickListener(vista -> registerUser());
    }

    private boolean validFields() {
        boolean usernameEmpty = username.isEmpty();
        boolean passwordEmpty = password.isEmpty();
        if(usernameEmpty) {
            binding.username.setError("El nickname no debe ser vacío.");
        }
        else {
            binding.username.setError(null);
        }
        if(passwordEmpty) {
            binding.password.setError("La contraseña no debe ser vacía.");
        }
        else {
            binding.password.setError(null);
        }
        return !usernameEmpty && !passwordEmpty;
    }

    private void login(String username, String password) throws JSONException {
        Intent i = new Intent(this, MainPage.class);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("nickname", username);
        jsonBody.put("password", password);
        final String requestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.d("res", response);
            try {
                JSONObject obj = new JSONObject(response);
                String nombre = obj.getString("nickname");
                String monedas = obj.getString("monedas");
                String avatar = obj.getString("avatar");
                if(nombre.equals(username)){
                    i.putExtra("nickname", nombre);
                    i.putExtra("monedas", monedas);
                    i.putExtra("avatar", avatar);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast toast = Toast.makeText(UserSignIn.this, "Usuario o contrseña incorrectos", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            //Toast.makeText(UserSignIn.this,"Usuario o contraseña incorrectos", Toast.LENGTH_SHORT,).show();
            Log.e("onErrorResponse", error.getLocalizedMessage() == null ? "" : error.getLocalizedMessage());
        }){
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

    private void tryLogin(String username, String password) throws JSONException {
        queue = Volley.newRequestQueue(this);
        if(validFields()) {
            login(username, password);
        }
    }

    private void registerUser() {
        Intent i = new Intent(this, UserSignUp.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}