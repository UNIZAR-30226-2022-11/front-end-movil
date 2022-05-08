package eina.unizar.ajedrez;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import eina.unizar.ajedrez.databinding.WaitingPageBinding;
import io.socket.emitter.Emitter;


public class WaitingPage extends AppCompatActivity {
    private WaitingPageBinding binding;
    private Socket mSocket;
    String nickname;
    int time;
    private RequestQueue queue;
    String idSocket;
    String side;
    {
        try {
            mSocket = IO.socket("http://ec2-18-206-137-85.compute-1.amazonaws.com:3000");
        } catch (URISyntaxException e) {
            Log.d("Socket: ",   e.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mSocket.connect();
        binding = WaitingPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent i = new Intent(this, OnlineActivity.class);

        nickname = getIntent().getExtras().getString("nickname");
        time = getIntent().getExtras().getInt("time");
        queue = Volley.newRequestQueue(this);

        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Esperando rival");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 5 seconds

                    i.putExtra("nickname", nickname);
                    startActivity(i);
                        if(mSocket.connected()){
                            Log.d("Socket: ", "Socket conectado");
                            mSocket.on("getOpponent", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    JSONObject data = (JSONObject)args[0];
//here the data is in JSON Format
                                    try {
                                         idSocket = data.getString("id");
                                         side = data.getString("side");
                                         i.putExtra("nickname", nickname);
                                         i.putExtra("time", time);
                                        i.putExtra("idSocket", idSocket);
                                        i.putExtra("side", side);
                                         startActivity(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("Socket: ", data.toString());
                                    // Toast.makeText(FriendsList.this, data.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                }
            }, 5000);
            // fillData(nickname);
    }
}
