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
    String side;
    int squareSize = 100;
    int x0 = 135;
    int y0 = 170;

    public ChessPiece(int col, int fila, String type, int size, String color, Bitmap piece, String side){
        this.col = col;
        this.fila = fila;
        this.type = type;
        this.color = color;
        this.piece = piece;
        this.alreadyMoved =false;
        this.side = side;
    }
    // Filas: 1-8
    // Columnas: 1-8
    public boolean checkPos(int col, int fila){
        return col ==  (this.col-x0)/squareSize && fila == (this.fila-y0)/squareSize;
    }

    public boolean alreadyMoved(){
        return alreadyMoved;
    }
    public void setAlreadyMoved(){
        this.alreadyMoved = true;
    }
    public void newCoord(int col, int fila) {
        String TAG = "d";
        if(checkMove(col,fila)){
            if(this.type.equals("King") && fila == (this.fila-y0)/squareSize && (col == 0 || col == 7)){
                Log.d(TAG, "El rey enroca:");
            }else{
                this.col = x0 + (col*squareSize);
                this.fila = y0 + (fila*squareSize);
            }

            Log.d(TAG, "Nueva pos:" + (this.col -x0) / squareSize + " y " + (this.fila-y0)/squareSize+ " de "+this.type);
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
        return (col-x0)/squareSize;
    }

    public int getFila() {
        return (fila-y0)/squareSize;
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
        if(Math.abs(((this.col-x0)/squareSize) -  col) ==  Math.abs(((this.fila-y0)/squareSize) - fila)
                && Math.abs(((this.col-x0)/squareSize) -  col) == 1) {
            return true;
        }
        else if((col == (this.col-x0)/squareSize && Math.abs(((this.fila-y0)/squareSize) - fila) == 1) ||
                (fila == (this.fila-y0)/squareSize) && (Math.abs(((this.col-x0)/squareSize) -  col) == 1)) {
            return true;
        }
        else if(!alreadyMoved &&(fila == (this.fila-y0)/squareSize) && (col == 0 || col == 7)){
            if(col == 0 && side.equals("1") && color.equals("w") || col == 0 && side.equals("1") && color.equals("b")){
                this.col = x0 + (1*squareSize);
            }else if(col == 7 && side.equals("1") && color.equals("w") || col == 7 && side.equals("1") && color.equals("b")){
                this.col = x0+(4*squareSize);
            }else if(col == 0 && side.equals("0") && color.equals("w")  || col == 0 && side.equals("0") && color.equals("b")){
                this.col = x0+(2*squareSize);
            }else if(col == 7 && side.equals("0") && color.equals("w") || col == 7 && side.equals("0") && color.equals("b")){
                this.col = x0+(6*squareSize);
            }
            Log.d("checkKing", " Devuelve correcto");
            return true;
        }
        if (alreadyMoved) Log.d("checkKing", " ya movido");
        else Log.d("checkKing", " No se ha movido "+ col + " fila: "+  fila);
        //Log.d("d", "King fallo " +(this.fila-y0)/squareSize + " " + (this.col-x0)/squareSize+ "hasta" + +fila+ "  "+col);
        return false;
    }

    private boolean checkPawn(int col, int fila){
        if(this.color == "b" && this.side == "0" || this.color == "w" && this.side == "1") {
            if ((fila == ((this.fila-y0)/squareSize) + 1 || (fila == ((this.fila-y0)/squareSize) + 2 && !alreadyMoved)) && col == (this.col-x0)/squareSize) {
                //Log.d("d", "Valid move black  pawn");
                return true;
            }

            if (fila == (this.fila-y0)/squareSize + 1 && (col == (this.col-x0)/squareSize + 1 || col == (this.col-x0)/squareSize - 1)) {
                // Log.d("Chess Piece", "eat pawn");
                return true;
            }
        }
        if(this.color == "w" && this.side == "0" || this.color == "b" && this.side == "1"){
            //Log.d("d", "Check white pawn "+ fila+ " y "+col + " / " + (this.fila-y0)/squareSize+ " y " +(this.col-x0)/squareSize);
            if ((fila == ((this.fila-y0)/squareSize) - 1 || (fila == ((this.fila-y0)/squareSize)-2 && !alreadyMoved)) && col == (this.col-x0)/squareSize) {
                Log.d("d", "Valid move pawn");
                return true;
            }

            if (fila == ((this.fila-y0)/squareSize) -1 && (col == (this.col-x0)/squareSize + 1 || col == (this.col-x0)/squareSize - 1)) {
                Log.d("d", "eat pawn");
                return true;
            }
        }
        // Log.d("d", "invalid move pawn");
        return false;
    }
    private boolean checkRook(int col, int fila){
        if(col == (this.col-x0)/squareSize || fila == (this.fila-y0)/squareSize){
            //   Log.d("d", "Valid move " );
            return true;
        }
        return false;
    }

    private boolean checkBishop(int col, int fila){
        //  Log.d("d", "Valid move  bishop" + fila + " " + col );
        if(Math.abs(((this.col-x0)/squareSize) -  col) ==  Math.abs(((this.fila-y0)/squareSize) - fila)){
            return true;
        }
        return false;
    }

    private boolean checkKnight(int col, int fila){
        if(Math.abs(((this.col-x0)/squareSize) -  col) == 2 &&  Math.abs(((this.fila-y0)/squareSize) - fila) == 1
                || Math.abs(((this.col-x0)/squareSize) -  col) == 1 &&  Math.abs(((this.fila-y0)/squareSize) - fila) == 2){
            return true;
        }
        return false;
    }
}