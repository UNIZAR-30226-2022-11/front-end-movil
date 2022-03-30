package eina.unizar.ajedrez;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;

public class ChessBoard extends View {
    Paint p,q;
    Rect Rec;
    int NUM_FILCOL = 8;
    int x0=65;
    int squareSize = 80;
    int x1= x0 +squareSize;
    HashMap<Integer,ChessPiece> pieceSet  = new HashMap<Integer,ChessPiece>();
    int numPieza;
    int y0 = 185;
    int posX,posY;
    int posFinX,posFinY;
    boolean pulsado = false;
    String turno;
    String boardMtx[][] = new String[NUM_FILCOL][NUM_FILCOL];
    /**************************************************************
     * ___________________________
     * | bR Kn bB bQ bK bB Kn bR |
     * | p  p  p  p  p  p  p  p  |
     * |                         |
     * |                         |
     * |                         |
     * |                         |
     * | p  p  p  p  p  p  p  p  |
     * | wR Kn wB wQ wK wB Kn wR |
     * ___________________________
     **************************************************************/

    @Override
    public boolean onTouchEvent(MotionEvent e){
        String TAG ="d: ";
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!pulsado) {
                    posX = Math.round(e.getX());
                    posY = Math.round(e.getY());
                    if (posX > x0 && posX < x0 + (squareSize * 8)
                            && posY > y0 && posY < y0 + (squareSize * 8)) { // Comprobar que se ha pulsado en el tablero
                        posX = (posX - x0) / squareSize;
                        posY = (posY - y0) / squareSize;
                        pulsado = checkClick(posX, posY);
                        Log.d(TAG, "Tocado en pos:" + posX + " y " + posY);
                    }
                }
                else{
                    posFinX = Math.round(e.getX()); posFinX = (posFinX-x0) / squareSize;
                    posFinY = Math.round(e.getY()); posFinY = (posFinY-y0) / squareSize;

                    Log.d(TAG, "Soltado en pos:" + posFinX + " y " + posFinY);
                    if (isAClick(posX, posFinX, posY, posFinY)) {
                        ChessPiece changePos = pieceSet.get(numPieza);
                        // Comprobar si el movimiento es valido (choque con otras piezas)
                        if(checkValidMove(changePos, posFinX,posFinY)) {
                            int piezaDown = eatsPiece(posFinX,posFinY);
                            if (piezaDown != -1) pieceSet.remove(piezaDown);
                            changePos.newCoord(posFinX, posFinY);
                            pieceSet.put(numPieza, changePos);
                            pulsado = false;
                            if (turno == "w") turno = "b";
                            else turno = "w";
                            invalidate();
                        }
                        else{
                            pulsado = false;
                            pieceSet.put(numPieza,changePos);// Devolver pieza sin cambios
                        }
                    }
                }
        }
        return super.onTouchEvent(e);
    }

    private int eatsPiece(int col, int fil){
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            ChessPiece p = entry.getValue();
            if(p.checkPos(col,fil)) {
               Log.d("d: ", "Borrando en pos " + boardMtx[fil][col]);
               return entry.getKey();
            }
        }
        return -1;
    }

    private boolean isAClick(int startX, int endX, int startY, int endY) {

        return startX!=endX || startY != endY;
    }

    public boolean checkClick(int col, int fil){ // Comprueba si se ha pulsado una pieza
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            ChessPiece p = entry.getValue();
            if(p.checkPos(col,fil)) {
                numPieza = entry.getKey();

                if (turno == "w" && p.getColor() == "w") return true; // Le toca a las blancas y el click es sobre una blanca
                else if (turno == "b" && p.getColor() == "b") return true; // Le toca a las negras y el click es sobre una negra
                else return false; // El color pulsado no coincide con el de las piezas que toca mover
            }
        }
        return false;
    }

    boolean checkValidMove(ChessPiece changePos,int col, int fila){
         // Si es un movimiento correcto para esa pieza, comprobar posibles bloqueos
        if(changePos.getType() == "Pawn" && changePos.checkMove(col,fila)){
           return checkPawn(col,fila,changePos);
        }else if (changePos.getType() == "Rook" && changePos.checkMove(col,fila)){
            return checkRook(col, fila, changePos);
        }
        else if (changePos.getType() == "Knight" && changePos.checkMove(col,fila)){
            return checkKnight(col, fila, changePos);
        }
        else if (changePos.getType() == "Bishop" && changePos.checkMove(col,fila)){
            return checkBishop(col, fila, changePos);
        }
        else if (changePos.getType() == "Queen" && changePos.checkMove(col,fila)){
            return checkQueen(col, fila, changePos);
        }
        else if (changePos.getType() == "King" && changePos.checkMove(col,fila)){
            return checkKing(col, fila, changePos);
        }
        return false;
    }
     private boolean checkQueen(int col, int fila, ChessPiece changePos){
        // Comprobar si hace un movimiento de Torre o de Alfil(válidos para reina)
         if(checkRook(col,fila,changePos) || checkBishop(col,fila,changePos)){
             if (turno == "w") boardMtx[fila][col] = "wQ";
             else boardMtx[fila][col] = "bQ";
             boardMtx[changePos.getFila()][changePos.getCol()] = "--";
             return true;
         }
        return false;
     }
    private boolean checkKing(int col, int fila, ChessPiece changePos){

        if(checkRook(col,fila,changePos)){ // Si hace un movimiento tipo torre

            if (turno == "w") boardMtx[fila][col] = "wK"; // Dibujar nueva pos en función del color
            else boardMtx[fila][col] = "bK";
            boardMtx[changePos.getFila()][changePos.getCol()] = "--";
            return true;
        }
        else if(checkBishop(col,fila,changePos)){ // Si hace movimiento tipo Alfil

            if (turno == "w") boardMtx[fila][col] = "wK"; // Dibujar nueva pos en función del color
            else boardMtx[fila][col] = "bK";
            boardMtx[changePos.getFila()][changePos.getCol()] = "--";
            return true;
        }
        return false;
    }
    private boolean checkBishop(int col, int fila, ChessPiece changePos){
        int numDesplazados =  Math.abs(col -changePos.getCol());
        int numDesplazados2 = Math.abs(fila -changePos.getFila());

        if(numDesplazados == 0 || numDesplazados2 == 0) return false; // Para el rey/reina si hacen movimiento de torre.

        for(int i = 1;i <= numDesplazados;i++){
            if(col > changePos.getCol() && fila > changePos.getFila() && i != numDesplazados) { // Hacia la derecha abajo
                if(boardMtx[changePos.getFila()+i][changePos.getCol()+i] != "--") return false;
            }else if(col > changePos.getCol() && fila < changePos.getFila() && i != numDesplazados){ // Hacia la derecha arriba
                if(boardMtx[changePos.getFila()-i][changePos.getCol()+i] != "--") return false;
            }else if(col < changePos.getCol() && fila > changePos.getFila() && i != numDesplazados){ // Hacia la izda abajo
                if(boardMtx[changePos.getFila()+i][changePos.getCol()-i] != "--") return false;
            }else if(col < changePos.getCol() && fila < changePos.getFila() && i != numDesplazados){ // Hacia la dcha arriba
                if(boardMtx[changePos.getFila()-i][changePos.getCol()-i] != "--") return false;
            }
            else { // Comprobar que en la posicion final no haya una del mismo color
                if(boardMtx[changePos.getFila()][changePos.getCol()].charAt(0) == boardMtx[fila][col].charAt(0)) {
                    return false;
                }
            }
        }
        if (turno == "w" && changePos.getType() == "Bishop") boardMtx[fila][col] = "wB"; // Alfil y turno de blancas, dibujar alfil

        else if(changePos.getType() == "Bishop")  boardMtx[fila][col] = "bB";// Alfil negro, turno de negras, dibujar alfil

        boardMtx[changePos.getFila()][changePos.getCol()] = "--"; // Posicion antigua del alfil
        return true;

    }

    private boolean checkKnight(int col, int fila, ChessPiece changePos){
        // Basta con comprobar que en la casilla destino no hay una pieza del mismo color
        if(boardMtx[changePos.getFila()][changePos.getCol()].charAt(0) == boardMtx[fila][col].charAt(0)) {
            return false;
        }

        if (turno == "w") boardMtx[fila][col] = "wKn";// Turno blancas -> Dibujar caballo blanco
        else boardMtx[fila][col] = "bKn";// Turno negras -> Dibujar caballo negro

        boardMtx[changePos.getFila()][changePos.getCol()] = "--";
        return true;
    }
    private boolean checkRook(int col, int fila, ChessPiece changePos){
        int numDesplazados =  Math.abs(col -changePos.getCol());
        int numDesplazadosFila = Math.abs(fila -changePos.getFila());
        if(numDesplazados != 0 && numDesplazadosFila != 0){ // Para el rey/reina si hacen movimiento en diagonal
            return false;
        }
        if(changePos.getFila() == fila && changePos.getCol() != col ){ // Se esta moviendo la columna, comprobar eso
            // Hallar numero de celdas desplazadas y para cada una comprobar que no hay obstrucción hasta la penúltima
            numDesplazados =  Math.abs(col -changePos.getCol());
            for(int i = 1;i <= numDesplazados;i++){
                if(col > changePos.getCol() && i != numDesplazados) { // Hacia la derecha
                    if(boardMtx[changePos.getFila()][changePos.getCol()+i] != "--") return false;// Pieza impide el paso
                }else if(i != numDesplazados){ // Hacia la izquierda
                    if(boardMtx[changePos.getFila()][changePos.getCol()-i] != "--") return false;// Pieza impide el paso
                }else { // Comprobar que en la posicion final no haya una del mismo color
                    if(boardMtx[changePos.getFila()][changePos.getCol()].charAt(0) == boardMtx[fila][col].charAt(0)) {
                        return false;
                    }
                }
            }
        }else if(changePos.getCol() == col && changePos.getFila() != fila ){ // Se está moviendo la fila, comprobar eso
             numDesplazados =  Math.abs(fila -changePos.getFila()); // Numero de celdas desplazadas
            for(int i = 1;i <= numDesplazados;i++){
                if(fila > changePos.getFila() && i != numDesplazados) { // Hacia abajo
                    if(boardMtx[changePos.getFila()+i][changePos.getCol()] != "--") return false;
                }else if(i != numDesplazados){ // Hacia arriba
                    if(boardMtx[changePos.getFila()-i][changePos.getCol()] != "--") return false;
                }else{// Comprobar que en la posicion final no haya una del mismo color
                    if(boardMtx[changePos.getFila()][changePos.getCol()].charAt(0) == boardMtx[fila][col].charAt(0)) {
                        return false;
                    }
                }
            }
        }

        if (turno == "w" && changePos.getType() == "Rook") boardMtx[fila][col] = "wR";// Torre blanca y turno blancas -> Mover
        else if(changePos.getType() == "Rook") boardMtx[fila][col] = "bR";// Torre negras y turno negras -> Mover
        boardMtx[changePos.getFila()][changePos.getCol()] = "--"; // Posición anterior de la torre

        return true;
    }
    private boolean checkPawn(int col, int fila,ChessPiece changePos){
        String TAG ="d: ";
        if(turno == "w"){ // Turno de las blancas
            if(boardMtx[changePos.getFila()-1][changePos.getCol()] == "--" && col == changePos.getCol()){// No hay nadie delante
                if(fila == changePos.getFila()-1){ // Se quiere mover una hacia adelante
                    boardMtx[changePos.getFila()-1][changePos.getCol()] = "wp";
                    boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                    Log.d(TAG, "Puede avanzar uno");
                    return true;
                }
                if(changePos.getFila() == 6 && fila == 4 && boardMtx[fila][col] == "--"){ // Se quiere mover 2 adelante
                    boardMtx[changePos.getFila()-2][changePos.getCol()] = "wp";
                    boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                    Log.d(TAG, "Puede avanzar dos");
                    return true;
                }
            }
            if(boardMtx[fila][col].charAt(0) == 'b'){
                boardMtx[fila][col] = "wp";
                boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                Log.d(TAG, "Puede comer");
                return true;
            }
        }
        else{
            if(boardMtx[changePos.getFila()+1][changePos.getCol()] == "--" && col == changePos.getCol()){// No hay nadie delante
                Log.d(TAG, "Llega dentro");
                if(fila == changePos.getFila()+1){ // Se quiere mover una hacia adelante
                    Log.d(TAG, "Puede avanzar uno " +boardMtx[changePos.getFila()+1][changePos.getCol()]);
                    boardMtx[changePos.getFila()+1][changePos.getCol()] = "bp";
                    boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                    return true;
                }
                if(changePos.getFila() == 1 && fila == 3 && boardMtx[fila][col] == "--"){ // Se quiere mover 2 adelante
                    boardMtx[changePos.getFila()+2][changePos.getCol()] = "bp";
                    boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                    Log.d(TAG, "Puede avanzar dos");
                    return true;
                }
            }
            Log.d(TAG, "Puede comer "+ fila + " "+ col + " - "  + boardMtx[fila][col].charAt(0));
            if(boardMtx[fila][col].charAt(0) == 'w'){
                boardMtx[fila][col] = "bp";
                boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                Log.d(TAG, "Puede comer");
                return true;
            }
        }
        return false;
    }
    public ChessBoard(Context context){
        super(context);
        p = new Paint();
        q = new Paint();
        Rec = new Rect();
        setPieceSet();
        setMatrix();
        turno = "w";

    }
    void setPieceSet(){
        int num = 0;
        Bitmap bPawn = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.black_pawn);
        Bitmap wPawn = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.white_pawn);
        Bitmap bRook= android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.black_rook);
        Bitmap wRook = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.white_rook);
        Bitmap bKnight= android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.black_knight);
        Bitmap wKnight = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.white_knight);
        Bitmap bBishop= android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.black_bishop);
        Bitmap wBishop = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.white_bishop);
        Bitmap bQueen = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.black_queen);
        Bitmap wQueen = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.white_queen);
        Bitmap bKing = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.black_king);
        Bitmap wKing = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.white_king);

        for(int i= 0; i < NUM_FILCOL;i++){
            pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+squareSize,"Pawn",squareSize,"b",bPawn));
            num++;
            pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+(6*squareSize),"Pawn",squareSize,"w",wPawn));
            num++;
        }
        pieceSet.put(num,new ChessPiece(x0,y0,"Rook",squareSize,"b",bRook)); num++;
        pieceSet.put(num,new ChessPiece(x0+(7*squareSize),y0,"Rook",squareSize,"b",bRook)); num++;
        pieceSet.put(num,new ChessPiece(x0,y0+(7*squareSize),"Rook",squareSize,"w",wRook)); num++;
        pieceSet.put(num,new ChessPiece(x0+(7*squareSize),y0+(7*squareSize),"Rook",squareSize,"w",wRook)); num++;
                                        // left top
        pieceSet.put(num,new ChessPiece(x0+squareSize,y0,"Knight",squareSize,"b",bKnight)); num++;
        pieceSet.put(num,new ChessPiece(x0+(6*squareSize),y0,"Knight",squareSize,"b",bKnight)); num++;
        pieceSet.put(num,new ChessPiece(x0+squareSize,y0+(7*squareSize),"Knight",squareSize,"w",wKnight)); num++;
        pieceSet.put(num,new ChessPiece(x0+(6*squareSize),y0+(7*squareSize),"Knight",squareSize,"w",wKnight)); num++;

        pieceSet.put(num,new ChessPiece(x0+(2*squareSize),y0,"Bishop",squareSize,"b",bBishop)); num++;
        pieceSet.put(num,new ChessPiece(x0+(5*squareSize),y0,"Bishop",squareSize,"b",bBishop)); num++;
        pieceSet.put(num,new ChessPiece(x0+(2*squareSize),y0+(7*squareSize),"Bishop",squareSize,"w",wBishop)); num++;
        pieceSet.put(num,new ChessPiece(x0+(5*squareSize),y0+(7*squareSize),"Bishop",squareSize,"w",wBishop)); num++;

        pieceSet.put(num,new ChessPiece(x0+(3*squareSize),y0,"Queen",squareSize,"b",bQueen)); num++;
        pieceSet.put(num,new ChessPiece(x0+(3*squareSize),y0+(7*squareSize),"Queen",squareSize,"w",wQueen)); num++;

        pieceSet.put(num,new ChessPiece(x0+(4*squareSize),y0,"King",squareSize,"b",bKing)); num++;
        pieceSet.put(num,new ChessPiece(x0+(4*squareSize),y0+(7*squareSize),"King",squareSize,"w",wKing)); num++;
    }

    private void setMatrix(){
        for(int i = 0;i < NUM_FILCOL;i++){
            for(int j = 0; j < NUM_FILCOL;j++){
                boardMtx[i][j] = "--";
            }
        }
        for(int i = 0; i < NUM_FILCOL; i++){
            boardMtx[1][i] = "bp";
            boardMtx[6][i] = "wp";
        }
        boardMtx[0][0] = "bR"; boardMtx[0][7] = "bR";
        boardMtx[7][0] = "wR"; boardMtx[7][7] = "wR";

        boardMtx[0][1] = "bKn"; boardMtx[0][6] = "bKn";
        boardMtx[7][1] = "wKn"; boardMtx[7][6] = "wKn";

        boardMtx[0][2] = "bB"; boardMtx[0][5] = "bB";
        boardMtx[7][2] = "wB"; boardMtx[7][5] = "wB";

        boardMtx[0][3] = "bQ"; boardMtx[7][3] = "wQ";
        boardMtx[0][4] = "bK"; boardMtx[7][4] = "wK";

    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        addPieces(canvas);
    }

    protected void drawBoard(Canvas canvas){
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.LTGRAY);

        q.setStrokeWidth(3);
        q.setStyle(Paint.Style.FILL);
        q.setColor(Color.DKGRAY);
        for(int j = 0; j < NUM_FILCOL/2;j++) {
            for (int i = 0; i < 4; i++) {
                canvas.drawRect(x0 + (2*squareSize * i), y0+(2*squareSize*j), x1 + (2*squareSize * i), y0 + squareSize +(2*squareSize*j), p);
                canvas.drawRect(x1 + (2*squareSize * i), y0+(2*squareSize*j), x1 + squareSize + (2*squareSize * i), y0 + squareSize+(2*squareSize*j), q);
                canvas.drawRect(x1 + (2*squareSize * i), y0+(squareSize*(2*j+1)), x1 + squareSize + (2*squareSize * i), y0 + squareSize+(squareSize*(2*j+1)), p);
                canvas.drawRect(x0 + (2*squareSize * i), y0+(squareSize*(2*j+1)), x1 + (2*squareSize * i), y0 + squareSize+(squareSize*(2*j+1)), q);
            }
         }
    }

    void addPieces(Canvas canvas){
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            ChessPiece nextPiece = entry.getValue();
            canvas.drawBitmap(nextPiece.getPiece(), null, new Rect(nextPiece.getX(),nextPiece.getY(),nextPiece.getX()+squareSize,nextPiece.getY()+squareSize), p);
        }
    }
}