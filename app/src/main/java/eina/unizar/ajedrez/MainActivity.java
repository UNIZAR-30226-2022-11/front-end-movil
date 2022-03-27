package eina.unizar.ajedrez;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import javax.security.auth.callback.Callback;

public class MainActivity extends AppCompatActivity {

    LinearLayout linlay;
    ImageView imgView;
    Paint p;
    int width,height;
    int posX,posY;
    int posFinX,posFinY;
    boolean pulsado = false;
    ChessBoard myCanvas ;//= new ChessBoard(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  myCanvas = new ChessBoard(this);
        setContentView(R.layout.user_sign_in);

      //  LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

      //  layout.addView(myCanvas);
    }
   /* @Override
    public boolean onTouchEvent(MotionEvent e){
        String TAG ="d: ";
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!pulsado){
                    //pulsado = true;
                    posX = Math.round(e.getX());
                    posY = Math.round(e.getY());
                   // if()
                    pulsado = myCanvas.checkClick(posX,posY);
                   // Log.d(TAG,"Tocado en pos:"+posX+" y "+posY);
                }
                else{
                    Log.d(TAG,"Suelta en pos:"+posX+" y "+posY);
                    posFinX = Math.round(e.getX());
                    posFinY = Math.round(e.getY());
                   // myCanvas.dibujarPieza( posFinX,posFinY);
                    LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
                    layout.invalidate();
                    pulsado = false;

                }
        }
     //  invalidate()
        return super.onTouchEvent(e);
    }*/
}