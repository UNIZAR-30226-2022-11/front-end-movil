package eina.unizar.ajedrez;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class UserSignIn extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sign_in);

        Button loginButton = findViewById(R.id.login);
        Button registerButton = findViewById(R.id.register);

        loginButton.setOnClickListener(view -> {
            // TODO
        });

        registerButton.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        Intent i = new Intent(this, UserSignUp.class);
        startActivity(i);
    }
}
