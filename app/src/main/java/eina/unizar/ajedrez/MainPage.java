package eina.unizar.ajedrez;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {

    /*Button online3Button = findViewById(R.id.online3);
    Button online10Button = findViewById(R.id.online10);
    Button online30Button = findViewById(R.id.online10);
    Button onlineNoTimeButton = findViewById(R.id.onlineNoTime);*/
    Button ai3Button;
    Button ai10Button;
    Button ai5Button;
    Button aiNoTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        ai3Button = findViewById(R.id.playAI3);
        ai10Button = findViewById(R.id.playAI10);
        ai5Button = findViewById(R.id.playAI30);
        aiNoTimeButton = findViewById(R.id.playAINoTime);

        ai3Button.setOnClickListener(view -> {
            playAgainstAI(3);
        });

        ai10Button.setOnClickListener(view -> {
            playAgainstAI(10);
        });

        ai5Button.setOnClickListener(view -> {
            playAgainstAI(5);
        });

        aiNoTimeButton.setOnClickListener(view -> {
            playAgainstAI(0);
        });
    }

    // TODO -> Modificar nombre de esta clase por AIGame y crear una RivalGame equivalente
    /* Obtener el valor en la actividad a iniciar:
    *  int valor = getIntent().getExtras().getInt("time");
    *  Importante: obtener el valor en el método onCreate */

    private void playAgainstAI(int min) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("time", min);
        startActivity(i);
    }
}
