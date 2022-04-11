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
        return col ==  (this.col-65)/120 && fila == (this.fila-185)/120;
    }

    public void newCoord(int col, int fila) {
        String TAG = "d";
        if(checkMove(col,fila)){
            this.col = 65 + (col*120);
            this.fila = 185 + (fila*120);
            Log.d(TAG, "Nueva pos:" + (this.col -65) / 120 + " y " + (this.fila-185)/120+ " de "+this.type);
        }
        if(this.type == "Pawn" && !alreadyMoved){ // Si peon ha hecho algun movimiento ya
            alreadyMoved =  true;
            Log.d(TAG, "No puede saltar 2 mas");
        }
    }

    public int getX() {
        return col;
    }

    public int getY() {
        return fila;
    }

    public int getCol() {
        return (col-65)/120;
    }

    public int getFila() {
        return (fila-185)/120;
    }

    public Bitmap getPiece() {
        return piece;
    }

    public String getColor() {
        return color;
    }

    public String getType() {
        return type;
    }

    public boolean checkMove(int col, int fila){
        switch (this.type){
            case "Pawn":
                return checkPawn(col,fila);
            case "Rook":
                return checkRook(col, fila);
            case "Knight":
                return checkKnight(col, fila);
            case "Bishop":
                return checkBishop(col, fila);
            case "Queen":
                return checkBishop(col,fila) || checkRook(col,fila);
            case "King":
                return checkKing(col,fila);
        }
        return false;
    }

    private boolean checkKing(int col, int fila){
        if(Math.abs(((this.col-65)/120) -  col) ==  Math.abs(((this.fila-185)/120) - fila)
            && Math.abs(((this.col-65)/120) -  col) == 1) {
            return true;
        }
        else if((col == (this.col-65)/120 && Math.abs(((this.fila-185)/120) - fila) == 1) ||
                (fila == (this.fila-185)/120) && (Math.abs(((this.col-65)/120) -  col) == 1)) {
            return true;
        }
        Log.d("d", "King fallo " +(this.fila-185)/120 + " " + (this.col-65)/120+ "hasta" + +fila+ "  "+col);
        return false;
    }

    private boolean checkPawn(int col, int fila){
        if(this.color == "b") {
<<<<<<< HEAD
            if ((fila == ((this.fila-185)/80) + 1 || (fila == ((this.fila-185)/80) + 2 && !alreadyMoved)) && col == (this.col-65)/80) {
                Log.d("d", "Valid move black  pawn");
=======
            if ((fila == ((this.fila-185)/120) + 1 || (fila == ((this.fila-185)/120) + 2 && !alreadyMoved)) && col == (this.col-65)/120) {
                Log.d("d", "Valid move pawn");
>>>>>>> 5a418ac51eab0717b3c1a644cadfde1b3bfa5987
                return true;
            }

            if (fila == (this.fila-185)/120 + 1 && (col == (this.col-65)/120 + 1 || col == (this.col-65)/120 - 1)) {
                Log.d("d", "eat pawn");
                return true;
            }
        }
        if(this.color == "w"){
            Log.d("d", "Check white pawn "+ fila+ " y "+col + " / " + (this.fila-185)/120+ " y " +(this.col-65)/120);
            if ((fila == ((this.fila-185)/120) - 1 || (fila == ((this.fila-185)/120)-2 && !alreadyMoved)) && col == (this.col-65)/120) {
                Log.d("d", "Valid move pawn");
                return true;
            }

            if (fila == ((this.fila-185)/120) -1 && (col == (this.col-65)/120 + 1 || col == (this.col-65)/120 - 1)) {
                Log.d("d", "eat pawn");
                return true;
            }
        }
       // Log.d("d", "invalid move pawn");
        return false;
    }
    private boolean checkRook(int col, int fila){
        if(col == (this.col-65)/120 || fila == (this.fila-185)/120){
            Log.d("d", "Valid move " );
            return true;
        }
        return false;
    }

    private boolean checkBishop(int col, int fila){
        if(Math.abs(((this.col-65)/120) -  col) ==  Math.abs(((this.fila-185)/120) - fila)){
            return true;
        }
        return false;
    }

    private boolean checkKnight(int col, int fila){
        if(Math.abs(((this.col-65)/120) -  col) == 2 &&  Math.abs(((this.fila-185)/120) - fila) == 1
            || Math.abs(((this.col-65)/120) -  col) == 1 &&  Math.abs(((this.fila-185)/120) - fila) == 2){
            return true;
        }
        return false;
    }
}
