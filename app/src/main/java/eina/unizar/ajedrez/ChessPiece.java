package eina.unizar.ajedrez;

import android.graphics.Bitmap;
import android.util.Log;

public class ChessPiece {
    Bitmap piece;
    int col;
    int fila;
    String type;
    int size;
    boolean alreadyMoved;
    String color;



    public ChessPiece(int col, int fila, String type, int size, String color, Bitmap piece){
        this.col = col;
        this.fila = fila;
        this.type = type;
        this.color = color;
        this.piece = piece;
        this.alreadyMoved =false;
    }
    // Filas: 1-8
    // Columnas: 1-8
    public boolean checkPos(int col, int fila){
        String TAG = "d";
        Log.d(TAG, "Cols:" + col + "-" + (this.col-65)/80);
        Log.d(TAG, "Filas:" + fila + "-" + (this.fila-185)/80);
        return col ==  (this.col-65)/80 && fila == (this.fila-185)/80;
    }

    public void newCoord(int col, int fila) {
        String TAG = "d";
        if(checkMove(col,fila)){
            this.col = 65 + (col*80);
            this.fila = 185 + (fila*80);
            Log.d(TAG, "Nueva pos:" + this.col + " y " + this.fila+ " de "+this.type);
        }
    }

    public int getX() {
        return col;
    }

    public int getY() {
        return fila;
    }

    public Bitmap getPiece() {
        return piece;
    }

    private boolean checkMove(int col, int fila){
        switch (this.type){
            case "Pawn":
                return checkPawn(col,fila);
            case "Rook":
                return checkRook(col, fila);
            case "Knight":
                Log.d("d", "Comprueba knight" );
                return checkKnight(col, fila);
            case "Bishop":
                return checkBishop(col, fila);
            case "Queen":
                break;
            case "King":
        }
        return false;
    }

    private boolean checkPawn(int col, int fila){

        return false;
    }
    private boolean checkRook(int col, int fila){
        if(col == (this.col-65)/80 || fila == (this.fila-185)/80){
            Log.d("d", "Valid move " );
            return true;
        }
        return false;
    }

    private boolean checkBishop(int col, int fila){
        if(Math.abs(((this.col-65)/80) -  col) ==  Math.abs(((this.fila-185)/80) - fila)){
            return true;
        }
        return false;
    }

    private boolean checkKnight(int col, int fila){
        if(Math.abs(((this.col-65)/80) -  col) == 2 &&  Math.abs(((this.fila-185)/80) - fila) == 1
            || Math.abs(((this.col-65)/80) -  col) == 1 &&  Math.abs(((this.fila-185)/80) - fila) == 2){
            return true;
        }
        return false;
    }
}
