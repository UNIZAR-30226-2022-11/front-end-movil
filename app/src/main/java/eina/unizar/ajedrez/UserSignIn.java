package eina.unizar.ajedrez;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import eina.unizar.ajedrez.databinding.UserSignInBinding;

public class UserSignIn extends AppCompatActivity{

    private UserSignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UserSignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.login.setOnClickListener(vista -> {
            String username = binding.username.getEditText().getText().toString();
            String password = binding.password.getEditText().getText().toString();

            // Verify user account
            // If exists { go to menu } else { displayError }
            displayMenu();
        });

        binding.register.setOnClickListener(vista -> registerUser());
    }

    private void displayMenu() {
        Intent i = new Intent(this, MainPage.class);
        startActivity(i);
    }

    private void registerUser() {
        Intent i = new Intent(this, UserSignUp.class);
        startActivity(i);
    }
}
