package eina.unizar.ajedrez;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eina.unizar.ajedrez.databinding.UserSignUpBinding;

public class UserSignUp extends AppCompatActivity {

    private final String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/register";
    private UserSignUpBinding binding;
    private String username;
    private String fullName;
    private String email;
    private String password;
    private String repeatPassword;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        binding = UserSignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.button.setOnClickListener(vista -> {
            username = Objects.requireNonNull(binding.username.getEditText()).getText().toString();
            fullName = Objects.requireNonNull(binding.fullName.getEditText()).getText().toString();
            email    = Objects.requireNonNull(binding.email.getEditText()).getText().toString();
            password = Objects.requireNonNull(binding.password.getEditText()).getText().toString();
            repeatPassword = Objects.requireNonNull(binding.repeatPassword.getEditText()).getText().toString();
            // Validar los campos introducidos por el usuario
            if(validFields()) {
                // Registrar el usuario
                try {
                    registrarUsuario();
                    Toast.makeText(getApplicationContext(), "Usuario registrado", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean validNickname() {
        if(username.isEmpty()) {
            binding.username.setError("Introduzca su nickname.");
            return false;
        }
        binding.username.setError(null);
        return true;
    }

    private boolean validFullName() {
        if(fullName.isEmpty()) {
            binding.fullName.setError("Introduzca su nombre completo.");
            return false;
        }
        binding.fullName.setError(null);
        return true;
    }

    private boolean validEmail() {
        if(email.isEmpty()) {
            binding.email.setError("Introduzca su correo electr??nico.");
            return false;
        }
        else {
            Pattern pat = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
            Matcher mat = pat.matcher(email);
            if(!mat.matches()) {
                binding.email.setError("Formato de email inv??lido. Ej: psoft@unizar.es");
                return false;
            }
            binding.email.setError(null);
            return true;
        }
    }

    private boolean validPassword() {
        boolean ok = true;
        if(password.isEmpty()) {
            binding.password.setError("Introduzca su contrase??a.");
            ok = false;
        }
        else {
            String regex = "^(?=.*[0-9])"
                    + "(?=.*[a-z])(?=.*[A-Z])"
                    + "(?=.*[@#$|%^&+=!?])"
                    + "(?=\\S+$).{8,20}$";
            Pattern pat = Pattern.compile(regex);
            Matcher mat = pat.matcher(password);
            if(!mat.matches()) {
                binding.password.setError("La contrase??a debe contener entre 8 y 20 caracteres " +
                        "con al menos una min??scula, una may??scula, un d??gito y " +
                        "un caracter especial.");
                ok = false;
            }
            else {
                binding.password.setError(null);
            }
        }
        if(!Objects.equals(password, repeatPassword)) {
            binding.repeatPassword.setError("Las contrase??as no coinciden.");
            ok = false;
        }
        else {
            binding.repeatPassword.setError(null);
        }
        return ok;
    }

    private boolean validFields() {
        Log.d("Valor campos", username + ", " + email + ", " + password + ", " + repeatPassword);
        boolean ok = validNickname() && validFullName() && validEmail() && validPassword();
        Log.d("Resultado validaci??n", String.valueOf(ok));
        return ok;
    }

    private void registrarUsuario() throws JSONException {
        Intent i = new Intent(this, UserSignIn.class);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("nickname", username);
        jsonBody.put("password", password);
        jsonBody.put("fullname", fullName);
        jsonBody.put("email", email);
        final String requestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.d("res", response);
            try {
                JSONObject obj = new JSONObject(response);
                boolean exito = obj.getBoolean("exito");
                Log.d("res", "Resultado registrar usuario = " + exito);
                compararElems();
                seleccionarElems();
                if(exito) {
                    startActivity(i);
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
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public void compararElems(){
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/buyItem";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nickname", username);
            jsonBody.put("nombre", "BoardGris");
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
                    }else  Toast.makeText(UserSignUp.this,"Ha habido un error durante la creacion", Toast.LENGTH_SHORT).show();

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

         jsonBody = new JSONObject();
        try {
            jsonBody.put("nickname", username);
            jsonBody.put("nombre", "knight_avatar");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody2 = jsonBody.toString();
         stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {

                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("exito")){
                    }else  Toast.makeText(UserSignUp.this,"Ha habido un error durante la creacion", Toast.LENGTH_SHORT).show();

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
                    return requestBody2 == null ? null : requestBody2.getBytes("utf-8");
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

        jsonBody = new JSONObject();
        try {
            jsonBody.put("nickname", username);
            jsonBody.put("nombre", "default_Piezas");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody3 = jsonBody.toString();
        stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {

                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("exito")){
                    }else  Toast.makeText(UserSignUp.this,"Ha habido un error durante la creacion", Toast.LENGTH_SHORT).show();

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
                    return requestBody3 == null ? null : requestBody3.getBytes("utf-8");
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
    private void seleccionarElems(){
        JSONObject jsonBody = new JSONObject();
        String URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/updateTable";
        try {
            jsonBody.put("nickname", username);
            jsonBody.put("table", "BoardGris");
        } catch (JSONException e) {
            e.printStackTrace();
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

                    }else  Toast.makeText(UserSignUp.this,"Ha habido un error durante la creacion", Toast.LENGTH_SHORT).show();

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

         jsonBody = new JSONObject();
        URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/updateAvatar";
        try {
            jsonBody.put("nickname", username);
            jsonBody.put("avatar", "knight_avatar");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody2 = jsonBody.toString();
        Log.d("Enviando: ", requestBody);

         stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("exito")){

                    }else  Toast.makeText(UserSignUp.this,"Ha habido un error durante la creacion", Toast.LENGTH_SHORT).show();

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
                    return requestBody2 == null ? null : requestBody2.getBytes("utf-8");
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

        jsonBody = new JSONObject();
        URL = "http://ec2-18-206-137-85.compute-1.amazonaws.com:3000/updatePieces";
        try {
            jsonBody.put("nickname", username);
            jsonBody.put("pieces", "default_Piezas");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody3 = jsonBody.toString();
        Log.d("Enviando: ", requestBody);

        stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Exito: ", response );
                try {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getBoolean("exito")){

                    }else  Toast.makeText(UserSignUp.this,"Ha habido un error durante la creacion", Toast.LENGTH_SHORT).show();

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
                    return requestBody3 == null ? null : requestBody3.getBytes("utf-8");
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