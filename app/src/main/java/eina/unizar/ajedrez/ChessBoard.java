package eina.unizar.ajedrez;

/*******************************/
/*3 opciones:
    - Crear 64 botonoes que formen un tablero
    - Dibujar el tablero simplemente con java y sobre el colocar las piezas
    - Dibujar el tablero usando xml y sobre el colocar las piezas
*/

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

import java.util.HashMap;
import java.util.Map;

/*******************************/
public class ChessBoard extends View {
    Paint p,q;
    Rect Rec;
    int NUM_FILCOL = 8;
    int x0=65;
    int squareSize = 80;
    int x1= x0 +squareSize;
    HashMap<Integer,ChessPiece> pieceSet  = new HashMap<Integer,ChessPiece>();
    //int xEnd= 160;
    int numPieza;
    int y0 = 185;
    private Object BitmapFactory ;
    int posX,posY;
    int posFinX,posFinY;
    boolean pulsado = false;
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
    String boardMtx[][] = new String[NUM_FILCOL][NUM_FILCOL];

    @Override
    public boolean onTouchEvent(MotionEvent e){
        String TAG ="d: ";
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!pulsado) {
                    //pulsado = true;
                    posX = Math.round(e.getX());
                    posY = Math.round(e.getY());
                    if (posX > x0 && posX < x0 + (squareSize * 8)
                            && posY > y0 && posY < y0 + (squareSize * 8)) {
                        posX = (posX - x0) / squareSize;
                        posY = (posY - y0) / squareSize;
                        pulsado = checkClick(posX, posY);
                        Log.d(TAG, "Tocado en pos:" + posX + " y " + posY);
                    }
                    else{
                        Log.d(TAG, "Mala pos");
                    }
                }
                else{
                    posFinX = Math.round(e.getX());
                    posFinY = Math.round(e.getY());
                    posFinX = (posFinX-x0) / squareSize;
                    posFinY = (posFinY-y0) / squareSize;
                    Log.d(TAG, "Soltado en pos:" + posFinX + " y " + posFinY);
                    if (isAClick(posX, posFinX, posY, posFinY)) {
                        ChessPiece changePos = pieceSet.get(numPieza);
                        changePos.newCoord(posFinX, posFinY);
                        pieceSet.put(numPieza, changePos);
                        pulsado = false;
                        invalidate();
                    }
                }
                break;
        }
        return super.onTouchEvent(e);
    }
    private boolean isAClick(int startX, int endX, int startY, int endY) {

        return startX!=endX || startY != endY;
    }
    public boolean checkClick(int col, int fil){
        String TAG ="d: ";
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            ChessPiece p = entry.getValue();
            if(p.checkPos(col,fil)){
                numPieza = entry.getKey();
                Log.d(TAG,"Guarda pieza" +numPieza);
                return true;
            }
        }
        return false;
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
            pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+squareSize,"bpawn",squareSize,"b",bPawn));
            num++;
            pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+(6*squareSize),"wpawn",squareSize,"w",wPawn));
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
        pieceSet.put(num,new ChessPiece(x0+(3*squareSize),y0+(7*squareSize),"Queen",squareSize,"b",wQueen)); num++;

        pieceSet.put(num,new ChessPiece(x0+(4*squareSize),y0,"King",squareSize,"w",bKing)); num++;
        pieceSet.put(num,new ChessPiece(x0+(4*squareSize),y0+(7*squareSize),"King",squareSize,"w",wKing)); num++;
    }
    public ChessBoard(Context context){
        super(context);
        p = new Paint();
        q = new Paint();
        Rec = new Rect();
        setPieceSet();
        setMatrix();

    }
    private void setMatrix(){
        for(int i = 0;i < NUM_FILCOL;i++){
            for(int j = 0; j < NUM_FILCOL;j++){
                boardMtx[i][j] = " ";
            }
        }
        for(int i = 0; i < NUM_FILCOL; i++){
            boardMtx[1][i] = "bp";
            boardMtx[6][i] = "wp";
        }
        boardMtx[0][0] = "bR"; boardMtx[0][7] = "bR";
        boardMtx[7][0] = "wR"; boardMtx[7][7] = "wR";

        boardMtx[0][1] = "bKn"; boardMtx[0][6] = "bKn";
        boardMtx[7][1] = "wK"; boardMtx[7][6] = "wK";

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

    void initDraw(Canvas canvas, int x, int y){
        Bitmap wPawn = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.white_pawn);
        canvas.drawBitmap(wPawn,null,new Rect(x0+(squareSize),y0+squareSize,x0+squareSize+(squareSize),y0+(2*squareSize)),p);
    }
    void addPieces(Canvas canvas){
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            ChessPiece nextPiece = entry.getValue();
            canvas.drawBitmap(nextPiece.getPiece(), null, new Rect(nextPiece.getX(),nextPiece.getY(),nextPiece.getX()+squareSize,nextPiece.getY()+squareSize), p);
        }
    }
}


