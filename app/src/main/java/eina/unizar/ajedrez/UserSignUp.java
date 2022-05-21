package eina.unizar.ajedrez;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
            binding.email.setError("Introduzca su correo electrónico.");
            return false;
        }
        else {
            Pattern pat = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
            Matcher mat = pat.matcher(email);
            if(!mat.matches()) {
                binding.email.setError("Formato de email inválido. Ej: psoft@unizar.es");
                return false;
            }
            binding.email.setError(null);
            return true;
        }
    }

    private boolean validPassword() {
        boolean ok = true;
        if(password.isEmpty()) {
            binding.password.setError("Introduzca su contraseña.");
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
                binding.password.setError("La contraseña debe contener entre 8 y 20 caracteres " +
                        "con al menos una minúscula, una mayúscula, un dígito y " +
                        "un caracter especial.");
                ok = false;
            }
            else {
                binding.password.setError(null);
            }
        }
        if(!Objects.equals(password, repeatPassword)) {
            binding.repeatPassword.setError("Las contraseñas no coinciden.");
            ok = false;
        }
        else {
            binding.repeatPassword.setError(null);
        }
        return ok;
    }

    private boolean validFields() {
        Log.d("Valor campos", username + ", " + email + ", " + password + ", " + repeatPassword);
        boolean ok = validNickname();
        ok &= validFullName();
        ok &= validEmail();
        ok &= validPassword();
        Log.d("Resultado validación", String.valueOf(ok));
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
}