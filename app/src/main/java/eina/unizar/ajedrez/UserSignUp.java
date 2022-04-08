package eina.unizar.ajedrez;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import eina.unizar.ajedrez.databinding.UserSignInBinding;
import eina.unizar.ajedrez.databinding.UserSignUpBinding;

public class UserSignUp extends AppCompatActivity {

    private UserSignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UserSignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.button.setOnClickListener(vista -> {
            String username = binding.username.getEditText().getText().toString();
            String fullName = binding.fullName.getEditText().getText().toString();
            String email = binding.email.getEditText().getText().toString();
            String password = binding.password.getEditText().getText().toString();
            String repeatPassword = binding.repeatPassword.getEditText().getText().toString();

            // Register new user account

            setResult(RESULT_OK);
            finish();
        });
    }
}
