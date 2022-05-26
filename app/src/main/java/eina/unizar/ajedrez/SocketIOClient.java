package eina.unizar.ajedrez;

import android.app.Activity;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIOClient {
    private static Socket mSocket;

    private static void initSocket(Activity activity) {
        try {
            mSocket = IO.socket("http://10.0.2.2:3001");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Socket getInstance(Activity activity) {
        if (mSocket != null) {
            return mSocket;
        } else {
            initSocket(activity);
            return mSocket;
        }
    }
}
