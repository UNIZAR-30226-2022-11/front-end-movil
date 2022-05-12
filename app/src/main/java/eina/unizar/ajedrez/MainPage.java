package eina.unizar.ajedrez;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainPage extends AppCompatActivity {

    String nickname;
    Button online3Button;
    Button online10Button;
    Button online30Button;
    Button onlineNoTimeButton;
    Button ai3Button;
    Button ai10Button;
    Button ai30Button;
    Button aiNoTimeButton;
    Button myFriends;
    Button playFriends;
    Button requests;
    Button store;
    Button gameRecord;
    Button ranking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        Random ran = new Random();int x = 0;
        for(int i = 0; i<100;i++){
            int randomNum = ThreadLocalRandom.current().nextInt(0, 1 + 1);
            Log.d("d: ", "Pre comprobacion x" + randomNum);
        }
        nickname = getIntent().getExtras().getString("nickname");

        online3Button = findViewById(R.id.online3);
        online10Button = findViewById(R.id.online10);
        online30Button = findViewById(R.id.online30);
        onlineNoTimeButton = findViewById(R.id.onlineNoTime);

        ai3Button = findViewById(R.id.playAI3);
        ai10Button = findViewById(R.id.playAI10);
        ai30Button = findViewById(R.id.playAI30);
        aiNoTimeButton = findViewById(R.id.playAINoTime);

        myFriends = findViewById(R.id.buttonSeeFriends);
        requests = findViewById(R.id.buttonRequests);

        gameRecord = findViewById((R.id.Record));
        ranking = findViewById((R.id.ranking));

        store = findViewById((R.id.shop));

        //online3Button.setOnClickListener(view -> playAgainstOnlineRival(3));
        online3Button.setOnClickListener(view -> seeStore());
        online10Button.setOnClickListener(view -> playAgainstOnlineRival(10));
        online30Button.setOnClickListener(view -> playAgainstOnlineRival(30));
        onlineNoTimeButton.setOnClickListener(view -> playAgainstOnlineRival(30));

        ai3Button.setOnClickListener(view -> playAgainstAI(3));
        ai10Button.setOnClickListener(view -> playAgainstAI(10));
        ai30Button.setOnClickListener(view -> playAgainstAI(30));
        aiNoTimeButton.setOnClickListener(view -> playAgainstAI(0));

        myFriends.setOnClickListener((view -> seeFriendsList(nickname)));
        requests.setOnClickListener((view -> seeRequests(nickname)));

        store.setOnClickListener((view -> seeStore()));
        gameRecord.setOnClickListener((view -> seeRecord()));
        ranking.setOnClickListener((view-> seeRanking()));
    }

    private void playAgainstOnlineRival(int min) {
        Intent i = new Intent(this, OnlineActivity.class);//OnlineActivity
        i.putExtra("nickname", nickname);
        i.putExtra("time", min);
        startActivity(i);
    }

    private void playAgainstAI(int min) {
        Intent i = new Intent(this, AiActivity.class);
        i.putExtra("time", min);
        startActivity(i);
    }

    private void seeFriendsList(String nickname){
        Log.d("res","Cambio actividad");
        Intent i = new Intent(this, FriendsList.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void seeRequests(String nickname){
        Log.d("res","Cambio actividad");
        Intent i = new Intent(this, friendRequests.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void seeStore(){
        Intent i = new Intent(this, Store.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void seeRecord(){
        Intent i = new Intent(this, GameRecord.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void seeRanking(){
        Intent i = new Intent(this, Ranking.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }
}