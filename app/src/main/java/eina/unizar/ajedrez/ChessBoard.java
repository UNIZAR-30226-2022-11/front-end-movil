package eina.unizar.ajedrez;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChessBoard extends View {
    Paint p,q;
    Rect Rec;
    int NUM_FILCOL = 8;
    int x0=65;
    int y0 = 185;
    int squareSize = 80;
    int x1= x0 +squareSize;
    String boardColor;

    HashMap<Integer,ChessPiece> pieceSet  = new HashMap<Integer,ChessPiece>();
    int numPieza;
    int posX, posY, posFinX, posFinY;
    boolean pulsado, movimientoCorrecto,enroque = false;

    String turno;
    String side;
    AiControl controlador;

    String boardMtx[][] = new String[NUM_FILCOL][NUM_FILCOL];
    class PosibleClavada{
      int fila;
      int col;
      Pair dir;
    };
    class Pos
    {
        public int X;
        public int Y;

        public Pos(int x, int y){
            X = x;
            Y = y;
        }
    };
    public class Movimiento {
        public Pos inicial;
        public Pos fin;
        //char tipo;

        public Movimiento(Pos inicial, Pos fin) {
            this.inicial = inicial;
            this.fin = fin;
           // this.tipo = tipo;
        }
    }
    public class Pair {
        public final int x;
        public final int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    Pos posReyNegro;
    Pos posReyBlanco;
    Pos posVieja;
    PosibleClavada clavadas[] = null; // Numero de pieza del pieceSet que esta clavada para cuando se intente mover, comprobar.
    int numClavadas = 0;
    PosibleClavada[] jaques = null;
    int numJaques = 0;
    Pos[] movimValidos =  new Pos[100];
    int numMovimientos = 0;
    boolean jaque ,mate, dobleJaque, checkingMate,turnoIA = false;
    /**************************************************************
     * ___________________________
     * | bR bN bB bQ bK bB bN bR |
     * | p  p  p  p  p  p  p  p  |
     * |                         |
     * |                         |
     * |                         |
     * |                         |
     * | p  p  p  p  p  p  p  p  |
     * | wR wN wB wQ wK wB wN wR |      | wR wN wB wQ wK wB wN wR |
     * ___________________________      ___________________________
     **************************************************************/

    public ChessBoard(Context context, String side, String board){
        super(context);
        p = new Paint();
        q = new Paint();
        Rec = new Rect();
        this.side = side;
        setPieceSet();
        setMatrix();
        turno = "w";
        boardColor = board;
        if(side == "0"){
            Log.d("d: ", "Es side 0");
             posReyNegro = new Pos(0,4);
             posReyBlanco = new Pos(7,4);
        }else{
            turnoIA = true;
            posReyNegro = new Pos(7,3);
            posReyBlanco = new Pos(0,3);
        }
        controlador = new AiControl();
    }

    public boolean checkCorrectMov(char turno){ // Añadir parametro para devolver las posiciones
        boolean info = movimientoCorrecto && turno == this.turno.charAt(0);
        boolean guardaJaque = jaque;
        Pos[] guardaMovimientoValido = movimValidos;
        boolean guardaDobleJaque = dobleJaque;
        int guardaNumMov, guardanumClav = 0;
        int guardaNumJaques  = numJaques;
        guardaNumMov = numMovimientos; guardanumClav = numClavadas;
        Log.d("d: ", "mC " + movimientoCorrecto + " t " +  turno + " p "+ this.turno.charAt(0));
        if (checkForMate()) Log.d("d:", "Fin de partida");
        jaque = guardaJaque;
        movimValidos= guardaMovimientoValido;
        dobleJaque = guardaDobleJaque;
        numMovimientos = guardaNumMov;
        numClavadas = guardanumClav;
        numJaques = guardaNumJaques;
        if (this.turno == "w" && !mate && info) this.turno = "b"; // Cambio de turno
        else if (!mate && info) this.turno = "w";
        else if(mate)this.turno = "x";
        return info;
    }
    public int[] getPos(){
        int[] pos = new int[4];
        pos[0] = posX;
        pos[1] = posY;
        pos[2] = posFinX;
        pos[3] = posFinY;

        return pos;
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
        String TAG ="d: ";

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //if(turno == 'w' && side == "0" || turno == 'b' && side == "1"){ // Comprobar si es mi turno o no, sino, sudar
                    if (!pulsado) {
                        movimientoCorrecto = false;
                        posX = Math.round(e.getX()); // Coordenadas x e y donde se toca
                        posY = Math.round(e.getY());
                        if (posX > x0 && posX < x0 + (squareSize * 8) // Comprobar que se ha pulsado en el tablero
                                && posY > y0 && posY < y0 + (squareSize * 8)) {
                            posX = (posX - x0) / squareSize; // Fila y columna donde se toca en función de los px
                            posY = (posY - y0) / squareSize;
                            pulsado = checkClick(posX, posY); // Ver si es casilla con pieza del color que toca
                            Log.d(TAG, "Tocado en pos:" + posX + " y " + posY + " movimiento valido "+ pulsado);
                        }
                    } else {

                        posFinX = Math.round(e.getX());
                        posFinX = (posFinX - x0) / squareSize;
                        posFinY = Math.round(e.getY());
                        posFinY = (posFinY - y0) / squareSize;
                        boolean cuidadoEnroque = false;
                        Log.d(TAG, "Soltado en pos:" + posFinX + " y " + posFinY);
                        if (isAClick(posX, posFinX, posY, posFinY)) { // Fila o Col distinta al de la casilla inicial
                            ChessPiece changePos = pieceSet.get(numPieza);
                            // Comprobar si el movimiento es valido (choque con otras piezas)
                            Log.d(TAG, "Entra aqui");
                            if (checkValidMove(changePos, posFinX, posFinY)) {
                                movimientoCorrecto = true;
                                int piezaDown = eatsPiece(posFinX, posFinY);
                                if (piezaDown != -1 && !enroque) pieceSet.remove(piezaDown); // Comer pieza rival
                                else if(piezaDown != -1 && enroque){
                                    Log.d(TAG, "Enrocada"+posFinX);
                                    ChessPiece Torre = pieceSet.get(piezaDown);
                                    if(posFinX == 0){ // Enroque a la izda
                                        Log.d(TAG, "Enrocada"+posFinX);
                                        int posFinal = posY-1;
                                        Torre.newCoord(posX-1, posY);
                                        Log.d(TAG, "Rey en: " + posY + " y " + (posX-1) + " boardMtx: " + boardMtx[posY][posX-1]);

                                    }else{
                                        Log.d(TAG, "Rey en: " + posY + " y " + (posX+1) + " boardMtx: " + boardMtx[posY][posX+1]);
                                        Torre.newCoord(posX+1, posY);
                                    }
                                    changePos.setAlreadyMoved();
                                    Torre.setAlreadyMoved();
                                    pieceSet.put(piezaDown, Torre);
                                    enroque = false;
                                    cuidadoEnroque = true;
                                }
                                Log.d(TAG, "Pos pieza"+ posFinY+ " "+posFinX);
                                if(changePos.getType().equals("Pawn") && (posFinY == 0 || posFinY == 7)) { // Coronacion de peon
                                    if (turno.equals("w")) {
                                        Bitmap wQueen = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.white_queen);
                                        pieceSet.put(numPieza, new ChessPiece(x0 + (squareSize*posFinX), y0 +(squareSize * posFinY), "Queen", squareSize, "w", wQueen, side));

                                    }else{
                                        Bitmap bQueen = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.black_queen);
                                        pieceSet.put(numPieza, new ChessPiece(x0 + (squareSize*posFinX), y0 +(squareSize * posFinY), "Queen", squareSize, "b", bQueen, side));
                                    }
                                }else {
                                    if(turno == "w" && changePos.getType().equals("King")){
                                        if(cuidadoEnroque && posFinX == 0) {
                                            posReyBlanco.X = posVieja.X; posReyBlanco.Y = 1;
                                            posFinY = 1;
                                            ;
                                        }else if(cuidadoEnroque && posFinX == 7){
                                            posReyBlanco.X = posVieja.X; posReyBlanco.Y = 5;
                                            posFinY = 5;
                                        }else{
                                            posReyBlanco.X =  posVieja.X; posReyBlanco.Y = posVieja.Y;
                                        }
                                    }else if(changePos.getType().equals("King")){
                                        if(cuidadoEnroque && posFinX == 0) {
                                            posReyNegro.X = posVieja.X; posReyNegro.Y = 1;
                                            posFinX= 1;
                                            ;
                                        }else if(cuidadoEnroque && posFinX == 7){
                                            posReyNegro.X = posVieja.X; posReyNegro.Y = 5;
                                            posFinX = 5;
                                        }else {
                                            posReyNegro.X = posVieja.X;posReyNegro.Y = posVieja.Y;
                                        }
                                    }
                                        cuidadoEnroque = false;
                                    Log.d(TAG, "Rey en: " + posFinY + " y " + posFinX + " boardMtx: " + boardMtx[posFinY][posFinX]);
                                    changePos.newCoord(posFinX, posFinY); // Cambiar posición a la pieza
                                    pieceSet.put(numPieza, changePos);
                                }



                               /* if (

                             ()) Log.d(TAG, "Fin de partida");
                                if (turno == "w" && !mate) turno = "b"; // Cambio de turno
                                else if (!mate) turno = "w";
                                else turno = "x";*/
                                pulsado = false;
                                turnoIA = true;
                                invalidate();
                            } else {
                                pulsado = false;
                                pieceSet.put(numPieza, changePos);// Devolver pieza sin cambios, movimiento invalido
                            }
                        }
                        Log.d(TAG, "FInal comprobacion");
                        pulsado = false;
                    }
                //}
        }
        return super.onTouchEvent(e);
    }
    public boolean getCheckClick(){
        return checkClick(posX, posY);
    }

    public boolean getIsAClick(){
        return isAClick(posX, posFinX, posY, posFinY);
    }
    public void hacerMovimientoRival(int fI,int cI, int fF, int cF){
        int filaFin =  Math.abs(fF -7);
        int columnaFin =  Math.abs(cF -7);
        int filaIni =  Math.abs(fI -7);
        int columnaIni =  Math.abs(cI -7);
        Log.d("d", "Movimiento "+filaIni + " " + columnaIni + " hasta " +filaFin + " " + columnaFin +" turno " +turno);
        boolean esEnroque = false;
        boolean coronar = false;
        if(filaIni == 0 && (columnaFin == 0 || columnaFin == 7) && columnaIni == 4
                && boardMtx[filaIni][columnaIni].charAt(1) == 'K'){ // Enroque
            esEnroque = true;
            if(columnaFin == 0){
                boardMtx[filaFin][columnaFin+2] = boardMtx[filaIni][columnaIni];
                boardMtx[filaFin][columnaIni-1] = boardMtx[filaFin][columnaFin];
            }else{
                boardMtx[filaFin][columnaFin-1] = boardMtx[filaIni][columnaIni];
                boardMtx[filaFin][columnaIni+1] = boardMtx[filaFin][columnaFin];
            }
            boardMtx[filaIni][columnaIni] = "--";
            boardMtx[filaFin][columnaFin] = "--";
        }else if(boardMtx[filaIni][columnaIni].charAt(1) == 'p' && filaFin == 7){ // Coronar
            if(side.equals("1")) {
                boardMtx[filaFin][columnaFin] = "wQ";
                boardMtx[filaIni][columnaIni] = "--";
            }else{
                boardMtx[filaFin][columnaFin] = "bQ";
                boardMtx[filaIni][columnaIni] = "--";
            }
            coronar = true;
        }
        else{
            boardMtx[filaFin][columnaFin] = boardMtx[filaIni][columnaIni];
            boardMtx[filaIni][columnaIni] = "--";
        }

        int piezaDown = eatsPiece(columnaFin, filaFin);

        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()) {
            ChessPiece p = entry.getValue();
            if (p.checkPos(columnaIni, filaIni)) { // Se encuentra la pieza que ha movido el rival

                if (piezaDown != -1) pieceSet.remove(piezaDown);
                turnoIA = false;
                int val = entry.getKey();
                if(coronar){
                    coronar = false;
                    if (side.equals("1")) {
                        Bitmap wQueen = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.white_queen);
                        pieceSet.put(numPieza, new ChessPiece(x0 + (squareSize*columnaFin), y0 +(squareSize * filaFin), "Queen", squareSize, "w", wQueen, side));

                    }else{
                        Bitmap bQueen = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.black_queen);
                        pieceSet.put(numPieza, new ChessPiece(x0 + (squareSize*columnaFin), y0 +(squareSize * filaFin), "Queen", squareSize, "b", bQueen, side));
                    }
                }else if(esEnroque){
                    esEnroque = false;
                    for(Map.Entry<Integer,ChessPiece> entry2 : pieceSet.entrySet()) {
                        ChessPiece Torre = entry.getValue();
                        if (Torre.checkPos(columnaFin, filaFin)) { // Buscar torre para cambiar de lugar
                            int clave = entry2.getKey();
                           if(columnaFin == 0) Torre.newCoord(columnaIni-1, filaFin);
                           else Torre.newCoord(columnaIni+1, filaFin);
                            Torre.setAlreadyMoved();
                            pieceSet.put(clave, Torre);
                            //break;
                        }

                    }
                    if(columnaFin == 0) p.newCoord(columnaFin+2, filaFin);
                    else p.newCoord(columnaFin-1, filaFin);
                    pieceSet.put(val, p);

                } else{
                    p.newCoord(columnaFin, filaFin);
                    pieceSet.put(val, p);
                }
                Log.d("d", "Movimiento " + p.getType() + " " + p.getColor());
                break;

            }
        }
        if (piezaDown != -1) pieceSet.remove(piezaDown); // Comer pieza rival


        if (checkForMate()) Log.d("d", "Fin de partida");
        if (turno.equals("w") && !mate){
            turno = "b"; // Cambio de turno
            Log.d("d", "Turno negras");
        }
        else if (!mate){
            Log.d("d", "Turno blancas");
            turno = "w";
        }
        else turno = "x";
       pulsado = false;

        invalidate();

    }

    public boolean isMate(){
        return mate;
    }

    private void checkInfoChecks(){
        char rival;
        int filaIni, colIni, filaFin, colFin;
        Pair[] dirs = new Pair[8];

        dirs[0] = new Pair(-1,0); dirs[1] = new Pair(0,-1); dirs[2] = new Pair(1,0);
        dirs[3] = new Pair(0,1); dirs[4] = new Pair(-1,-1); dirs[5] = new Pair(-1,1);
        dirs[6] = new Pair(1,-1); dirs[7] = new Pair(1,1);
        if(turno.equals("w")) {
            rival = 'b';
            filaIni = posReyBlanco.X; colIni = posReyBlanco.Y ;
        } else{
            rival = 'w';
            filaIni = posReyNegro.X ; colIni = posReyNegro.Y ;
        }
        Log.d("checkInfoChecks: ", "turno " + turno + " filaIni: " + filaIni + " colIni: " + colIni);
       // Log.d("d: ", "pos ini " + filaIni + " " + colIni);
        for(int i =0; i < NUM_FILCOL;i++){
            Pair dirActual = dirs[i];
            PosibleClavada posibleClavada = null;
            for(int j= 1; j < NUM_FILCOL;j++){
                colFin =  colIni + (dirActual.y*j);
                filaFin =  filaIni + (dirActual.x*j);
               // Log.d("d: ", "pos fin: " + filaFin + " " + colFin);
                if (0 <= filaFin && filaFin<=7 && 0 <= colFin && colFin<=7){
                    //Log.d("d: ", "pieza " +boardMtx[filaFin][colFin] + " en "+ filaFin + " " + colFin);
                    String pieza = boardMtx[filaFin][colFin];
                    if(pieza.charAt(0) == turno.charAt(0) && pieza.charAt(1) != 'K'){
                        if(posibleClavada == null){
                            posibleClavada =  new PosibleClavada();
                            posibleClavada.fila = filaFin; posibleClavada.col = colFin;
                            posibleClavada.dir = dirActual;
                        }
                        else break;
                    }else if(pieza.charAt(0) == rival){
                        char tipo =  pieza.charAt(1);
                        if ((0 <= i && i <= 3  && tipo == 'R') ||
                                (4 <= i && i <= 7 && tipo == 'B') ||
                                (j == 1 && tipo == 'p' && (((rival == 'w' && side == "0" || rival == 'b' && side == "1") && 6 <= i && i <= 7) ||
                                        ((rival == 'b' && side == "0" || rival == 'w' && side == "1") && 4 <= i && i <= 5))) ||
                                (tipo == 'Q') || (j==1 && tipo == 'K')){
                            if(posibleClavada == null){
                                Log.d("checkInfoChecks: ", "Jaque sera true " + i + " " + rival + " " + side + " "+ tipo + " j " + j + " con pieza" + pieza);
                                jaque = true;
                                if(jaques == null) jaques = new PosibleClavada[15];
                                jaques[numJaques] = new PosibleClavada();
                                jaques[numJaques].fila = filaFin; jaques[numJaques].col = colFin;
                                jaques[numJaques].dir = dirActual;
                                Log.d("d: ", "Jaque sera true desde" + jaques[0].fila + " " + jaques[0].col + " direccion " + jaques[0].dir.x + " " +jaques[0].dir.y);
                                numJaques++;
                                break;
                            }
                            else{
                                Log.d("checkInfoChecks: ", "Clavada en fila: " + posibleClavada.fila + " col: " + posibleClavada.col);
                                if(clavadas == null) clavadas = new PosibleClavada[15];
                                clavadas[numClavadas] = posibleClavada;
                                numClavadas++;
                                break;
                            }
                        }else break;// No es una pieza que pueda hacer jaque en esta dirección
                    }
                } else break;
            }
        }
        dirs[0] = new Pair(-2,-1); dirs[1] = new Pair(-2,1); dirs[2] = new Pair(-1,-2);
        dirs[3] = new Pair(-1,2); dirs[4] = new Pair(1,-2); dirs[5] = new Pair(1,2);
        dirs[6] = new Pair(2,-1); dirs[7] = new Pair(2,1);
        for(int j= 0; j < NUM_FILCOL;j++){
            Pair dirActual = dirs[j];
            colFin =  colIni + dirActual.y;
            filaFin =  filaIni + dirActual.x;
            if (0 <= filaFin && filaFin<=7 && 0 <= colFin && colFin<=7){
                String pieza = boardMtx[filaFin][colFin];
                if(pieza.charAt(0) == rival && pieza.charAt(1) == 'N'){
                    jaque = true;
                    if(jaques == null) jaques = new PosibleClavada[15];
                    jaques[numJaques] = new PosibleClavada();
                    jaques[numJaques].fila = filaFin; jaques[numJaques].col = colFin;
                    jaques[numJaques].dir = dirActual;
                    numJaques++;
                }
            }
        }
        Log.d("checkInfoChecks: ", "Res final: " + jaque);
    }
    public void makeAIMove(){
        ArrayList<Movimiento> movs = generarTodosMovimientosValidos();
        Log.d("d: ", "Movs: " + movs.size());
        Movimiento m = controlador.mejorMov(boardMtx,movs,side);
        if(m == null) mate = true;
        Log.d("d: ", "Movimiento final: " + m.inicial.X + " " + m.inicial.Y);
        Log.d("d: ", "Hasta: " + m.fin.X + " " + m.fin.Y);
        //boardMtx[m.fin.X][m.fin.Y] = boardMtx[m.inicial.X][m.inicial.Y] ;
        //boardMtx[m.inicial.X][m.inicial.Y] = "--";
        int piezaDown = eatsPiece(m.fin.Y, m.fin.X);
        ChessPiece p;
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            p = entry.getValue();
            if(p.checkPos(m.inicial.Y,m.inicial.X) && !mate) { // Encontrar pieza a mover por la ia en el set de pieza
                Log.d("d: ", "Encontrada pieza");
                turnoIA = false;
                if(checkCertainPiece(p, m.fin.Y, m.fin.X)){
                    if(piezaDown != -1) pieceSet.remove(piezaDown);
                    int val = entry.getKey();

                    if(p.getType().equals("Pawn") && (m.fin.X == 0 || m.fin.X  == 7)) { // Coronacion de peon
                        Log.d("makeAIMove: ", "Convirtiendo a reina");
                        if (turno.equals("w")) {
                            Bitmap wQueen = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.white_queen);
                            pieceSet.put(val, new ChessPiece(x0 + (squareSize*m.fin.Y), y0 +(squareSize * m.fin.X), "Queen", squareSize, "w", wQueen, side));

                        }else{
                            Bitmap bQueen = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.black_queen);
                            pieceSet.put(val, new ChessPiece(x0 + (squareSize*m.fin.Y), y0 +(squareSize * m.fin.X), "Queen", squareSize, "b", bQueen, side));
                        }
                    }else{
                        p.newCoord(m.fin.Y, m.fin.X); // Cambiar posición a la pieza
                        pieceSet.put(val, p);
                    }

                    Log.d("d: ", "Movimiento en orden");
                    if(turno == "w" && p.getType().equals("King")){
                        posReyBlanco.X =  m.fin.X; posReyBlanco.Y = m.fin.Y;;
                    }else if(p.getType().equals("King")){
                        posReyNegro.X =  m.fin.X; posReyNegro.Y = m.fin.Y;
                    }
                    break;
                }
               /* if (checkCertainPiece(p, m.fin.Y, m.fin.X)) {
                    Log.d("d: ", "Movimiento en orden " + p.getType() + " color " + p.getColor() + " EN " + p.getFila() + " y  " + p.getCol());
                    int val = entry.getKey();
                    p.newCoord(m.fin.Y, m.fin.X); // Cambiar posición a la pieza
                    pieceSet.put(val, p);
                    Log.d("d: ", "Movimiento en orden");
                    break;
                }*/
            }
        }
        if (piezaDown != -1) pieceSet.remove(piezaDown); // Comer pieza rival

        if(checkForMate()) Log.d("d: ", "Fin de partida");
        if (turno == "w" && !mate) turno = "b"; // Cambio de turno
        else if(!mate) turno = "w";
        else turno = "x";
        pulsado = false;
        Log.d("d: ", "Antes de invalidar");
        invalidate();
    }
    // Devuelve pieza si en esa casilla hay una pieza rival
    private int eatsPiece(int col, int fil){
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            ChessPiece p = entry.getValue();
            if(p.checkPos(col,fil)) return entry.getKey();
        }
        return -1;
    }

    public boolean isAClick(int startX, int endX, int startY, int endY) {
        return startX!=endX || startY != endY;
    }

    public boolean checkClick(int col, int fil){ // Comprueba si se ha pulsado una pieza
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            ChessPiece p = entry.getValue();
            if(p.checkPos(col,fil)) {
                numPieza = entry.getKey();
                if (turno.equals("w") && p.getColor().equals("w") && side.equals("0")) return true; // Le toca a las blancas, el click es sobre una blanca y el jugador juega con blancas
                else if (turno.equals("b") && p.getColor().equals("b") && side.equals("1")) return true; // Le toca a las negras, el click es sobre una negra y el jugador juega con blancas
                else return false; // El color pulsado no coincide con el de las piezas que toca mover
            }
        }
        return false;
    }
    private boolean findValidMove(int fila, int col){
        for(int i = 0; i < numMovimientos;i++){
            if(movimValidos[i].X ==  fila && movimValidos[i].Y == col) return true;
        }
        return false;
    }
    private void movimientosParaPieza(ChessPiece p, ArrayList<Movimiento> movsValidos){
        Pair[] dirs = new Pair[8];
        dirs[0] = new Pair(-1,0); dirs[1] = new Pair(0,-1); dirs[2] = new Pair(1,0);
        dirs[3] = new Pair(0,1); dirs[4] = new Pair(-1,-1); dirs[5] = new Pair(-1,1);
        dirs[6] = new Pair(1,-1); dirs[7] = new Pair(1,1);
        if(p.getType().equals("King") || p.getType().equals("Rook")){
            p.setAlreadyMoved();
        }
        int colIni = p.getCol();
        int filaIni = p.getFila();
        for(int i =0; i < NUM_FILCOL;i++){
            Pair dirActual = dirs[i];
            for(int j= 1; j < NUM_FILCOL;j++){
                int colFin =  colIni + (dirActual.y*j);
                int filaFin =  filaIni + (dirActual.x*j);
                if (0 <= filaFin && filaFin<=7 && 0 <= colFin && colFin<=7 && p.getType() != "Knight"){
                    if(p.getType().equals("King")) Log.d("movPieza:", "check King: " + " en x: "+ p.getFila() + " y: "+ p.getCol() + " a dir x: "+ dirActual.x + " y:" +dirActual.y + " jaque: " +jaque);
                    if(checkCertainPiece(p,colFin,filaFin)){
                        Log.d("movPieza: ", "Mov valido para " + p.getType()+ " Color: "+ p.getColor()+" Fil: "+ p.getFila() + " Col " + p.getCol()+
                                "FilaFin " + filaFin + " columnaFin "+ colFin);
                        Movimiento nuevoMov = new Movimiento(new Pos(p.getFila(),p.getCol()),new Pos(filaFin,colFin));
                        movsValidos.add(nuevoMov);
                        //Log.d("d: ", "EEEEEEEEEEEEEEEEEEEEEEEEEy " + boardMtx[filaFin][colFin] + " fila: " +filaFin + " col: "+ colFin);
                    }
                }
            }
        }
        dirs[0] = new Pair(-2,-1); dirs[1] = new Pair(-2,1); dirs[2] = new Pair(-1,-2);
        dirs[3] = new Pair(-1,2); dirs[4] = new Pair(1,-2); dirs[5] = new Pair(1,2);
        dirs[6] = new Pair(2,-1); dirs[7] = new Pair(2,1);
        for(int i =0; i < NUM_FILCOL;i++){
            Pair dirActual = dirs[i];
            for(int j= 1; j < NUM_FILCOL;j++){
                int colFin =  colIni + (dirActual.y*j);
                int filaFin =  filaIni + (dirActual.x*j);
                if (0 <= filaFin && filaFin<=7 && 0 <= colFin && colFin<=7 && p.getType() == "Knight"){
                   if(p.getType().equals("King")) Log.d("movPieza:", "check King: ");
                    if(checkCertainPiece(p,colFin,filaFin)){
                        Log.d("movPieza: ", "Mov valido para " + p.getType()+ " Color: "+ p.getColor()+" Fil: "+ p.getFila() + " Col " + p.getCol());
                        Movimiento nuevoMov = new Movimiento(new Pos(p.getFila(),p.getCol()),new Pos(filaFin,colFin));
                        movsValidos.add(nuevoMov);
                        //Log.d("d: ", "EEEEEEEEEEEEEEEEEEEEEEEEEy " + boardMtx[filaFin][colFin] + " fila: " +filaFin + " col: "+ colFin);
                    }
                }
            }
        }
    }

    public ArrayList<Movimiento> generarTodosMovimientosValidos(){
        clavadas = null; jaques = null; jaque = false; movimValidos = null;
        numClavadas = 0; numJaques = 0; numMovimientos = 0;
        checkInfoChecks(); // Obtenemos si hay jaque, las clavadas y las casillas que hacen jaque una vez
        Pos[] guardaMovimientoValido = movimValidos;
        boolean guardaJaque = jaque;
        boolean guardaDobleJaque = dobleJaque;
        int guardaNumMov, guardanumClav = 0;
        int guardaNumJaques  = numJaques;
        guardaNumMov = numMovimientos; guardanumClav = numClavadas;
        if(guardaJaque){
            Log.d("genTodMovsVal:", "Hay jaque al rey con ");
            if(guardaNumJaques == 1) hallarMovimientosValidos(); // Solo una pieza hace jaque al rey
            else if(guardaNumJaques > 1) guardaDobleJaque = true; // Si esta es true, solo se puede mover el rey (doble jaque)
           /* if((numMovimientos == 0 && numClavadas == 0) || dobleJaque){ // Comprobar jaque mate
                if (checkForMate()) return true;
            }*/
        }
        ArrayList<Movimiento> movsValidos = new ArrayList<Movimiento>();
        Log.d("genTodMovsVal: ", "Turno " + turno + " jaque: " + jaque + " turnoIA: " + turnoIA + " numClavadas: "+ numClavadas);
        for(int fila = 0; fila < NUM_FILCOL;fila++){
            for(int col = 0; col<NUM_FILCOL;col++){
                if(boardMtx[fila][col].charAt(0) == turno.charAt(0)){
                    for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
                        ChessPiece p = entry.getValue();
                        if(p.checkPos(col,fila)) { // Encontrada pieza que se busca, comprobar movimientos validos
                            Log.d("genTodMovsVal: ", "Comprobando " + p.getType() + " fila: "+ p.getFila() + " col: " +p.getCol() + " jaque: "+jaque);
                            movimientosParaPieza(p,movsValidos);
                        }
                    }
                }
            }
        }
        Log.d("genTodMovsVal: ", "Movs generados");
        return movsValidos;
    }
    private void hallarMovimientosValidos(){
        Pair dirJaque = null;
        String piezaJaque;
        int filaIni, colIni, filaJaque, colJaque;
        if(turno.equals("w")) {
            Log.d("hallarMovValidos: ", "Turno " + turno);
            filaIni = posReyBlanco.X; colIni = posReyBlanco.Y;
        } else {
            filaIni = posReyNegro.X; colIni = posReyNegro.Y;
        }

        filaJaque = jaques[0].fila; colJaque = jaques[0].col;
        dirJaque = new Pair(jaques[0].dir.x,jaques[0].dir.y);
        piezaJaque = boardMtx[filaJaque][colJaque];
        if(piezaJaque.charAt(1) == 'N'){ // Pieza que hace jaque es el caballo ->  se mueve al rey o se come al caballo
            if(movimValidos == null) movimValidos =  new Pos[50];
            movimValidos[numMovimientos] =  new Pos(filaJaque,colJaque);
            numMovimientos++;
        }else{ // Se puede cubrir con una pieza o mover al rey
            for(int i = 1; i<NUM_FILCOL;i++){
                if(movimValidos == null) movimValidos =  new Pos[50];
                movimValidos[numMovimientos] =  new Pos(filaIni + dirJaque.x*i,colIni + dirJaque.y*i);
                Log.d("Posiciones buenas: ",  movimValidos[numMovimientos].X + " " +movimValidos[numMovimientos].Y);
                numMovimientos++;
                // Se llega a la casilla de la pieza que hace jaque, dejar de contar
                if(filaIni + dirJaque.x*i == filaJaque && colIni + dirJaque.y*i == colJaque) break;
            }
        }
    }
    private boolean checkCertainPiece(ChessPiece changePos,int col, int fila){
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
    boolean checkValidMove(ChessPiece changePos,int col, int fila){
         // Si es un movimiento correcto para esa pieza, comprobar posibles bloqueos y jaques
        clavadas = null; jaques = null; jaque = false; movimValidos = null;
        numClavadas = 0; numJaques = 0; numMovimientos = 0;
        checkInfoChecks(); // Obtenemos si hay jaque, las clavadas y las casillas que hacen jaque
        if(jaque){
            Log.d("d:", "Hay jaque al rey con " + changePos.getType() + " " + changePos.getColor());
            if(numJaques == 1) hallarMovimientosValidos(); // Solo una pieza hace jaque al rey
            else if(numJaques > 1) dobleJaque = true; // Si esta es true, solo se puede mover el rey (doble jaque)
           /* if((numMovimientos == 0 && numClavadas == 0) || dobleJaque){ // Comprobar jaque mate
                if (checkForMate()) return true;
            }*/
        }
        Log.d("d:", "Comprobando pieza " + changePos.getType() + " " + changePos.getColor() + " movs posibles"
                + numMovimientos + " y jaque: " + jaque);
        return checkCertainPiece(changePos, col, fila);
    }

    private boolean checkForMate(){
        clavadas = null; jaques = null; jaque = false; movimValidos = null; dobleJaque = false;
        numClavadas = 0; numJaques = 0; numMovimientos = 0;

        if(turno == "w") turno = "b";
        else turno = "w";
        checkInfoChecks();
        if(jaque){hallarMovimientosValidos();}
        if(turno == "w") turno = "b";
        else turno = "w";
        Log.d("checkForMate:", "Info -> jaque: " + jaque + " nummov "+ numMovimientos + " numCLav "+ numClavadas);
        checkingMate = true;
        Pos[] guardaMovimientoValido = movimValidos;
        boolean guardaJaque = jaque;
        boolean guardaDobleJaque = dobleJaque;
        int guardaNumMov, guardanumClav = 0;
        guardaNumMov = numMovimientos; guardanumClav = numClavadas;
        int guardaNumJaques = numJaques;
        ArrayList<Movimiento> movsValidos = new ArrayList<Movimiento>();
        Log.d("checkForMate: ", "Turno " + turno + " jaque: " + jaque + " turnoIA: " + turnoIA);
        if(numMovimientos > 0 ){
            if (turno.equals("w")) turno = "b";
            else turno = "w";
            for(int fila = 0; fila < NUM_FILCOL;fila++){
                for(int col = 0; col<NUM_FILCOL;col++){
                    if(boardMtx[fila][col].charAt(0) == turno.charAt(0)){
                        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
                            ChessPiece p = entry.getValue();
                            if(p.checkPos(col,fila)) { // Encontrada pieza que se busca, comprobar movimientos validos
                              //  Log.d("genTodMovsVal: ", "Comprobando " + p.getType() + " fila: "+ p.getFila() + " col: " +p.getCol() + " jaque: "+jaque);
                                movimientosParaPieza(p,movsValidos);
                                jaque = guardaJaque;
                                movimValidos= guardaMovimientoValido;
                                dobleJaque = guardaDobleJaque;
                                numMovimientos = guardaNumMov;
                                numClavadas = guardanumClav;
                                numJaques = guardaNumJaques;
                            }
                        }
                    }
                }
            }
            guardaNumMov = movsValidos.size();
            if (turno.equals("w")) turno = "b";
            else turno = "w";
            Log.d("checkForMate: ", "MovsValidos: " + movsValidos.size() );
        }

        if(guardaJaque && guardaNumMov == 0 && guardanumClav == 0) {//|| guardaDobleJaque) { // Puede ser mate
            for (Map.Entry<Integer, ChessPiece> entry : pieceSet.entrySet()) {
                ChessPiece p = entry.getValue();
                int key = entry.getKey();
                if (p.getType() == "King" && turno != p.getColor()) { // Ver si el rey puede deshacer el mate
                   /* Pair[] dirs = new Pair[8];
                    dirs[0] = new Pair(-1, 0); dirs[1] = new Pair(0, -1); dirs[2] = new Pair(1, 0);
                    dirs[3] = new Pair(0, 1); dirs[4] = new Pair(-1, -1); dirs[5] = new Pair(-1, 1);
                    dirs[6] = new Pair(1, -1);dirs[7] = new Pair(1, 1);

                    for (int i = 0; i < NUM_FILCOL; i++) {
                        Pair dirActual = dirs[i];
                        if(comprobarBordes(dirActual,p.getFila(),p.getCol())) {
                            if(turno == "w") turno = "b";
                            else turno = "w";

                            if(checkRook(dirActual.y + p.getCol(), dirActual.x + p.getFila(),p) || checkBishop(dirActual.y + p.getCol(), dirActual.x + p.getFila(),p)){ // Si hace un movimiento tipo torre o alfil

                                if (turno == "w") turno = "b";
                                else turno = "w";
                                Log.d("d:", "Rey todavia puede moverse");
                                checkingMate =  false;
                                return false;
                            }


                        if(turno == "w") turno = "b";
                        else turno = "w";
                        }
                    }*/
                    mate= true;
                    Log.d("d:", "Jaque mate con tomate");
                    pieceSet.remove(key);
                    break;
                }/*else if(turno != p.getColor() && !guardaDobleJaque && guardaNumMov == 1){ // Si otra pieza del color del turno puede comer a la pieza del jaque
                    if(checkCertainPiece(p, guardaMovimientoValido[0].Y,guardaMovimientoValido[0].X)) {checkingMate =  false; return false; }//  Pieza puede deshacer jaque. No es mate
                }*/
            }
        }else if(!guardaJaque || guardaNumMov >= 1 || guardanumClav != 0){ Log.d("Check for mate:", "No es mate esJaque: " + guardaJaque
                                                                    + " guardaNMov: " +  guardaNumMov + " guardaNClav: " + guardanumClav);checkingMate =  false; return false;}
        Log.d("checkForMate:", "Se confirma el mate");
        mate = true;
        return true;
    }
    private boolean comprobarBordes(Pair dir, int fila, int col){
        if((dir.x == -1 && fila == 0) || (dir.x ==  1 && fila == NUM_FILCOL -1)) return false;
        if((dir.y == -1 && col == 0) || (dir.y ==  1 && col == NUM_FILCOL -1)) return false;
        return true;
    }
    // Comprobar si hace un movimiento de Torre o de Alfil(válidos para reina)
     private boolean checkQueen(int col, int fila, ChessPiece changePos){
         if(checkRook(col,fila,changePos) || checkBishop(col,fila,changePos)){
             if (turno == "w" && !turnoIA) boardMtx[fila][col] = "wQ";
             else if(!turnoIA) boardMtx[fila][col] = "bQ";
             if (!turnoIA)boardMtx[changePos.getFila()][changePos.getCol()] = "--";
             return true;
         }
        return false;
     }
    private boolean checkKing(int col, int fila, ChessPiece changePos){

        boolean torreEncontrada = false;
        ChessPiece torre = null;
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            ChessPiece p = entry.getValue();
            if(p.checkPos(col,fila) && p.getType().equals("Rook")) {
                Log.d("checkKing:", "Torre encontrada ");
                torre = p;
                torreEncontrada = true;
            }
        }
        if(fila == changePos.getFila() && !changePos.alreadyMoved() && torreEncontrada && !torre.alreadyMoved()){ // Comprobación de enroque
            if((col == 0 && boardMtx[fila][1] == "--" && boardMtx[fila][2] == "--") ||
                    (col == 7 && boardMtx[fila][4] == "--" && boardMtx[fila][5] == "--" && boardMtx[fila][6] == "--") ){
                Log.d("checkKing:", "Todo en orden para enrqoue ");
                if(turno.equals("w")){
                    posVieja =  new Pos(posReyBlanco.X, posReyBlanco.Y);
                    posReyBlanco.X =  fila; posReyBlanco.Y = col;
                }else{
                    posVieja =  new Pos(posReyNegro.X, posReyNegro.Y);
                    posReyNegro.X =  fila; posReyNegro.Y = col;
                }
                    boolean guardaJaque = jaque;
                    Pos[] guardaMovimientoValido = movimValidos;
                    boolean guardaDobleJaque = dobleJaque;
                    int guardaNumMov, guardanumClav = 0;
                    int guardaNumJaques  = numJaques;
                    guardaNumMov = numMovimientos; guardanumClav = numClavadas;
                    Log.d("checkKing:", "El jaque es: " +
                            jaque);
                    jaque =  false;
                    checkInfoChecks();



                if(!jaque) { // Dibujar nueva pos en función del color
                    Log.d("checkKing:", "Nuevo mov correcto "+ posReyNegro.X + " " + posReyNegro.Y);
                    if (turno == "w" && !checkingMate && !turnoIA && col == 0) {
                        boardMtx[fila][1] = "wK";
                        boardMtx[fila][2] = "wR";
                        boardMtx[fila][0] = "--";
                    }
                    else if (turno == "w" && !checkingMate && !turnoIA && col == 7) {
                        boardMtx[fila][5] = "wK";
                        boardMtx[fila][4] = "wR";
                        boardMtx[fila][7] = "--";
                    }
                    else if(!checkingMate && !turnoIA && col ==  0){
                        boardMtx[fila][1] = "bK";
                        boardMtx[fila][2] = "bR";
                        boardMtx[fila][0] = "--";
                    }
                    else if(!checkingMate && !turnoIA && col ==  0){
                        boardMtx[fila][5] = "bK";
                        boardMtx[fila][4] = "bR";
                        boardMtx[fila][7] = "--";
                    }
                    else checkingMate =  false;
                    boardMtx[changePos.getFila()][3] = "--";
                    enroque= true;
                    Log.d("checkKing:", "Enroque bien "+ changePos.getType()+" fil: " + changePos.getFila() + " col:" + changePos.getCol());
                    if(turno.equals("w")){
                        posReyBlanco.X =  posVieja.X; posReyBlanco.Y = posVieja.Y;;
                    }else{
                        posReyNegro.X =  posVieja.X; posReyNegro.Y = posVieja.Y;
                    }
                    jaque = guardaJaque;
                    movimValidos= guardaMovimientoValido;
                    dobleJaque = guardaDobleJaque;
                    numMovimientos = guardaNumMov;
                    numClavadas = guardanumClav;
                    numJaques = guardaNumJaques;
                    return true;
                }else{ // El movimiento que se hace deriva en jaque, devolver la posición antigua del rey
                    if(turno.equals("w")){
                        posReyBlanco.X =  posVieja.X; posReyBlanco.Y = posVieja.Y;;
                    }else{
                        posReyNegro.X =  posVieja.X; posReyNegro.Y = posVieja.Y;
                    }
                }
                jaque = guardaJaque;
                movimValidos= guardaMovimientoValido;
                dobleJaque = guardaDobleJaque;
                numMovimientos = guardaNumMov;
                numClavadas = guardanumClav;
                numJaques = guardaNumJaques;
                }
        }
        if(checkRook(col,fila,changePos) || checkBishop(col, fila, changePos)){ // Si hace un movimiento tipo torre o alfil
            Log.d("checkKing:", "Todo en orden para sin enrqoue ");
            if(turno.equals("w")){
                posVieja =  new Pos(posReyBlanco.X, posReyBlanco.Y);
                posReyBlanco.X =  fila; posReyBlanco.Y = col;
            }else{
                posVieja =  new Pos(posReyNegro.X, posReyNegro.Y);
                posReyNegro.X =  fila; posReyNegro.Y = col;
            }
                boolean guardaJaque = jaque;

                Pos[] guardaMovimientoValido = movimValidos;
                boolean guardaDobleJaque = dobleJaque;
                int guardaNumMov, guardanumClav = 0;
                int guardaNumJaques  = numJaques;
                guardaNumMov = numMovimientos; guardanumClav = numClavadas;
                Log.d("checkKing:", "el jaque es: " + jaque);
                jaque =  false;
                checkInfoChecks();


            if(!jaque) { // Dibujar nueva pos en función del color
                Log.d("checkKing:", "Nuevo mov correcto "+ posReyBlanco.X + " " + posReyBlanco.Y);
                if (turno == "w" && !checkingMate && !turnoIA) boardMtx[fila][col] = "wK";
                else if(!checkingMate && !turnoIA) boardMtx[fila][col] = "bK";
                if(!checkingMate && !turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                else checkingMate =  false;
                if(turno.equals("w")){
                    posReyBlanco.X =  posVieja.X; posReyBlanco.Y = posVieja.Y;;
                }else{
                    posReyNegro.X =  posVieja.X; posReyNegro.Y = posVieja.Y;
                }
                jaque = guardaJaque;

                movimValidos= guardaMovimientoValido;
                dobleJaque = guardaDobleJaque;
                numMovimientos = guardaNumMov;
                numClavadas = guardanumClav;
                numJaques = guardaNumJaques;
                return true;
            }else{ // El movimiento que se hace deriva en jaque, devolver la posición antigua del rey
                if(turno.equals("w")){
                    posReyBlanco.X =  posVieja.X; posReyBlanco.Y = posVieja.Y;;
                }else{
                    posReyNegro.X =  posVieja.X; posReyNegro.Y = posVieja.Y;
                }
            }
            jaque = guardaJaque;

            movimValidos= guardaMovimientoValido;
            dobleJaque = guardaDobleJaque;
            numMovimientos = guardaNumMov;
            numClavadas = guardanumClav;
            numJaques = guardaNumJaques;
        }
        return false;
    }
    private boolean checkBishop(int col, int fila, ChessPiece changePos){
        int numDesplazados =  Math.abs(col -changePos.getCol());
        int numDesplazados2 = Math.abs(fila -changePos.getFila());
       // Log.d("d", "Valid move  bishop" + fila + " " + col );
        if(numDesplazados == 0 || numDesplazados2 == 0) return false; // Para el rey/reina si hacen movimiento de torre.

        boolean clavado = false;
        Pair dirClavada = new Pair(0,0);
        if(numClavadas != 0) {
            for (int i = 0; i < numClavadas; i++) {
                if (clavadas[i].fila == changePos.getFila() && clavadas[i].col == changePos.getCol()) { // Pieza clavada es la que se esta intentando mover
                    clavado = true;
                    dirClavada = new Pair(clavadas[i].dir.x, clavadas[i].dir.y);
                   // Log.d("d:", "Fila Torre !!!!!!!!!!!!!!!!!!!!!!! "+ jaque);
                }
            }
        }
        for(int i = 1;i <= numDesplazados;i++){
            if(col > changePos.getCol() && fila > changePos.getFila() && i != numDesplazados) { // Hacia la derecha abajo
                if(((!clavado && !jaque)|| (dirClavada.x == dirClavada.y &&  dirClavada.x != 0)) || (jaque && !clavado && findValidMove(fila, col))) {
                    if (boardMtx[changePos.getFila() + i][changePos.getCol() + i] != "--") return false;
                }else return false;
            }else if(col > changePos.getCol() && fila < changePos.getFila() && i != numDesplazados){ // Hacia la derecha arriba
                if(((!clavado&& !jaque) || (dirClavada.x != dirClavada.y && dirClavada.x != 0 && dirClavada.y != 0 )) || (jaque && !clavado && findValidMove(fila, col))){
                    if (boardMtx[changePos.getFila() - i][changePos.getCol() + i] != "--") return false;
                }else return false;
            }else if(col < changePos.getCol() && fila > changePos.getFila() && i != numDesplazados){ // Hacia la izda abajo
                if(((!clavado && !jaque)|| (dirClavada.x != dirClavada.y && dirClavada.x != 0 && dirClavada.y != 0 )) || (jaque && !clavado && findValidMove(fila, col))) {
                    if (boardMtx[changePos.getFila() + i][changePos.getCol() - i] != "--") return false;
                } else return false;
            }else if(col < changePos.getCol() && fila < changePos.getFila() && i != numDesplazados){ // Hacia la izda arriba
                if(((!clavado&& !jaque) || (dirClavada.x ==  dirClavada.y && dirClavada.x != 0 )) || (jaque && !clavado && findValidMove(fila, col))){
                    if (boardMtx[changePos.getFila() - i][changePos.getCol() - i] != "--") return false;
                } else return false;
            }
            // Direcciones 1 1 y -1 -1 son en la misma diagonal y direcciones 1 -1 y -1 1. Un alfil clavado se puede mover
            // únicamente en la diagonal en la que esta clavado
            else if(((!clavado && !jaque) || (col -changePos.getCol() == fila -changePos.getFila() && dirClavada.x == dirClavada.y && dirClavada.x != 0) ||
                    (col -changePos.getCol() != fila -changePos.getFila() && dirClavada.x != dirClavada.y && dirClavada.x != 0 && dirClavada.y != 0)) ||
                    (jaque && !clavado && (findValidMove(fila, col) || changePos.getType() == "King"))){ // Comprobar que en la posicion final no haya una del mismo color
                if(boardMtx[changePos.getFila()][changePos.getCol()].charAt(0) == boardMtx[fila][col].charAt(0)) {
                    return false;
                }
            }else return false;
        }

        if (turno == "w" && changePos.getType() == "Bishop" && !turnoIA) boardMtx[fila][col] = "wB"; // Alfil y turno de blancas, dibujar alfil
        else if(changePos.getType() == "Bishop" && !turnoIA)  boardMtx[fila][col] = "bB";// Alfil negro, turno de negras, dibujar alfil
       if (changePos.getType() == "Bishop" && !turnoIA)boardMtx[changePos.getFila()][changePos.getCol()] = "--"; // Posicion antigua del alfil

        return true;
    }

    private boolean checkKnight(int col, int fila, ChessPiece changePos){
        // Basta con comprobar que en la casilla destino no hay una pieza del mismo color
        boolean clavado = false;
        if(numClavadas != 0) {
            for (int i = 0; i < numClavadas; i++) {
                if (clavadas[i].fila == changePos.getFila() && clavadas[i].col == changePos.getCol()) { // Pieza clavada es la que se esta intentando mover
                    clavado = true;
                }
            }
        }
        if(clavado) return false;
        if(boardMtx[changePos.getFila()][changePos.getCol()].charAt(0) == boardMtx[fila][col].charAt(0)) {
            return false;
        }
        if (jaque && !clavado && !findValidMove(fila, col)) return false;

        if (turno == "w" && !turnoIA) boardMtx[fila][col] = "wN";// Turno blancas -> Dibujar caballo blanco
        else if(!turnoIA) boardMtx[fila][col] = "bN";// Turno negras -> Dibujar caballo negro
        if (!turnoIA)boardMtx[changePos.getFila()][changePos.getCol()] = "--";

        return true;
    }
    private boolean checkRook(int col, int fila, ChessPiece changePos){
        int numDesplazados =  Math.abs(col -changePos.getCol());
        int numDesplazadosFila = Math.abs(fila -changePos.getFila());

        if(numDesplazados != 0 && numDesplazadosFila != 0) return false; // Para el rey/reina si hacen movimiento en diagonal
        boolean clavado = false;
        Pair dirClavada = new Pair(0,0);
        if(numClavadas != 0) {
            for (int i = 0; i < numClavadas; i++) {
                if (clavadas[i].fila == changePos.getFila() && clavadas[i].col == changePos.getCol()) { // Pieza clavada es la que se esta intentando mover
                    clavado = true;
                    dirClavada = new Pair(clavadas[i].dir.x, clavadas[i].dir.y);
                   // Log.d("d:", "Fila Torre !!!!!!!!!!!!!!!!!!!!!!! "+ jaque);
                }
            }
        }

        if(changePos.getFila() == fila && changePos.getCol() != col ){ // Se esta moviendo la columna
            // Hallar numero de celdas desplazadas y para cada una comprobar que no hay obstrucción hasta la penúltima
            numDesplazados =  Math.abs(col -changePos.getCol());
            for(int i = 1;i <= numDesplazados;i++){
                if(((!clavado && !jaque) || ((dirClavada.y == -1 || dirClavada.y == 1) && dirClavada.x == 0))
                        || (jaque && !clavado && (findValidMove(fila, col) || changePos.getType() == "King"))) {
                    Log.d("d: ", "PUede moverse en la misma columna a la casilla: "+ boardMtx[fila][col]);
                    if (col > changePos.getCol() && i != numDesplazados) { // Hacia la derecha
                        if (boardMtx[changePos.getFila()][changePos.getCol() + i] != "--")
                            return false;// Pieza impide el paso
                    } else if (i != numDesplazados) { // Hacia la izquierda
                        if (boardMtx[changePos.getFila()][changePos.getCol() - i] != "--")
                            return false;// Pieza impide el paso
                    } else { // Comprobar que en la posicion final no haya una del mismo color
                        if (boardMtx[changePos.getFila()][changePos.getCol()].charAt(0) == boardMtx[fila][col].charAt(0)) {
                            return false;
                        }
                    }
                }else return false;
            }
        }else if(changePos.getCol() == col && changePos.getFila() != fila ){ // Se está moviendo la fila
             numDesplazados =  Math.abs(fila -changePos.getFila()); // Numero de celdas desplazadas
            for(int i = 1;i <= numDesplazados;i++){
                if(((!clavado && !jaque) || ((dirClavada.x == -1 || dirClavada.x == 1) && dirClavada.y == 0))
                        || (jaque && !clavado && (findValidMove(fila, col) || changePos.getType() == "King"))) {
                    if (fila > changePos.getFila() && i != numDesplazados) { // Hacia abajo
                        if (boardMtx[changePos.getFila() + i][changePos.getCol()] != "--")
                            return false;
                    } else if (i != numDesplazados) { // Hacia arriba
                        if (boardMtx[changePos.getFila() - i][changePos.getCol()] != "--")
                            return false;
                    } else {// Comprobar que en la posicion final no haya una del mismo color
                        if (boardMtx[changePos.getFila()][changePos.getCol()].charAt(0) == boardMtx[fila][col].charAt(0)) {
                            return false;
                        }
                    }
                }else return false;
            }
        }

        if (turno == "w" && changePos.getType() == "Rook" && !turnoIA) boardMtx[fila][col] = "wR";// Torre blanca y turno blancas -> Mover
        else if(changePos.getType() == "Rook" && !turnoIA) boardMtx[fila][col] = "bR";// Torre negras y turno negras -> Mover
        if(changePos.getType() == "Rook" && !turnoIA)boardMtx[changePos.getFila()][changePos.getCol()] = "--"; // Posición anterior de la torre
        Log.d("d:", turnoIA + " Fila Torre -----------"+ changePos.getFila() + " COl "+ changePos.getCol() +" "+ jaque);
        return true;
    }

    private boolean checkPawn(int col, int fila,ChessPiece changePos){
        boolean clavado = false;
        Pair dirClavada = new Pair(0,0);
        int orientacion = 1* Integer.parseInt(side);

      //  if(turno == "w" && side == "0" && turno == "b" && side == "1"){
            if(numClavadas != 0) {
                for (int i = 0; i < numClavadas; i++) {
                    if (clavadas[i].fila == changePos.getFila() && clavadas[i].col == changePos.getCol()) { // Pieza clavada es la que se esta intentando mover
                        clavado = true;
                        dirClavada = new Pair(clavadas[i].dir.x, clavadas[i].dir.y);
                    }
                }
            }

            if(turno == "w" && side == "0" || turno == "b" && side == "1"){ // Mi turno

                if(boardMtx[changePos.getFila()-1][changePos.getCol()] == "--" && col == changePos.getCol()){// No hay nadie delante // Añadir side
                    if(fila == changePos.getFila()-1){ // Se quiere mover una hacia adelante // añadir side
                        if(((!clavado && !jaque) || (dirClavada.x == -1 && dirClavada.y == 0)) || jaque && !clavado && findValidMove(fila, col)) { // No clavada o clavada en la dirección a la que se mueve
                                                                                                        // Añadir side dirClavada.x == +1
                            if(fila == 0 && side.equals("0")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "wQ";
                            else if(fila == 0 && side.equals("1")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "bQ";
                            else if(!turnoIA && side == "0") boardMtx[changePos.getFila() - 1][changePos.getCol()] = "wp"; // Añadir side
                            else if(!turnoIA && side == "1") boardMtx[changePos.getFila() - 1][changePos.getCol()] = "bp";
                            if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                            Log.d("d: ", "Puede avanzar uno");
                            return true;
                        }
                    }
                    if(changePos.getFila() == 6 && fila == 4 && boardMtx[fila][col] == "--"){ // Se quiere mover 2 adelante // Añadir side
                        if(((!clavado && ! jaque) || (dirClavada.x == -1 && dirClavada.y == 0)) || (jaque && !clavado && findValidMove(fila, col))) {
                            if(!turnoIA && side == "0") boardMtx[changePos.getFila() - 2][changePos.getCol()] = "wp";
                            else if(!turnoIA && side == "1") boardMtx[changePos.getFila() - 2][changePos.getCol()] = "bp";
                            if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                            Log.d("d: ", "Puede avanzar dos");
                            return true;
                        }
                    }
                } else if((boardMtx[fila][col].charAt(0) == 'b' && side == "0" || boardMtx[fila][col].charAt(0) == 'w' && side == "1") && col != changePos.getCol()){ // Hay pieza rival en diagonal y se mueve alli
                    if(((!clavado && !jaque) || (dirClavada.x == -1 && dirClavada.y == -1 && col == changePos.getCol()-1))|| jaque && !clavado && findValidMove(fila, col)) { // No clavada o clavada en esa dirección. Come hacia la izquierda
                        if(!turnoIA &&fila == 0 && side.equals("0")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "wQ";
                        else if(!turnoIA && fila == 0 && side.equals("1")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "bQ";
                        else if(!turnoIA && side == "0") boardMtx[fila][col] = "wp";
                        else if(!turnoIA && side == "1") boardMtx[fila][col] = "bp";
                        if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                        Log.d("d:", "Puede comer izda");
                        return true;
                    }
                    if(((!clavado && !jaque) || (dirClavada.x == -1 && dirClavada.y == 1 && col == changePos.getCol()+1)) || jaque && !clavado && findValidMove(fila, col)) { // No clavada o clavada en esa dirección. Come hacia la derecha
                        if(!turnoIA &&fila == 0 && side.equals("0")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "wQ";
                        else if(!turnoIA && fila == 0 && side.equals("1")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "bQ";
                        else if(!turnoIA && side == "0") boardMtx[fila][col] = "wp";
                        else if(!turnoIA && side == "1") boardMtx[fila][col] = "bp";
                        if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                        Log.d("d: ", "Puede comer dcha");
                        return true;
                    }
                }
            }
            else{ // Turno del rival
                Log.d("d: ", "Ia debe entrar aqui " + boardMtx[changePos.getFila()+1][changePos.getCol()]);
                Log.d("checkPawn: ", "El jaque es: "+  jaque);
                if(boardMtx[changePos.getFila()+1][changePos.getCol()] == "--" && col == changePos.getCol()){// No hay nadie delante
                    Log.d("d: ", "Camino libre a fila: "+fila + " col: "+col);
                    if(fila == changePos.getFila()+1){ // Se quiere mover una hacia adelante
                       Log.d("d: ", "Libre de avanzar 1 la negra clavado" +clavado + " y jaque "+ jaque);
                        if(((!clavado && !jaque) || (dirClavada.x == 1 && dirClavada.y == 0)) || jaque && !clavado && findValidMove(fila, col)) {
                            Log.d("d: ", "Puede avanzar uno");
                            if(!turnoIA &&  fila == 7 && side.equals("0")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "bQ";
                            else if(!turnoIA && fila == 7 && side.equals("1")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "wQ";
                            else if(!turnoIA && side == "0") boardMtx[changePos.getFila() + 1][changePos.getCol()] = "bp";
                            else if(!turnoIA && side == "1") boardMtx[changePos.getFila() + 1][changePos.getCol()] = "wp";
                            if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                            return true;
                        }
                    }
                    if(changePos.getFila() == 1 && fila == 3 && boardMtx[fila][col] == "--"){ // Se quiere mover 2 adelante
                        if(((!clavado && ! jaque) || (dirClavada.x == 1 && dirClavada.y == 0)) || jaque && !clavado && findValidMove(fila, col)) {
                            Log.d("d:", "Puede avanzar dos");

                           if(!turnoIA && side == "0") boardMtx[changePos.getFila() + 2][changePos.getCol()] = "bp";
                            else if(!turnoIA && side == "1") boardMtx[changePos.getFila() + 2][changePos.getCol()] = "wp";
                            if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                            return true;
                        }
                    }
                } else if((boardMtx[fila][col].charAt(0) == 'w' && side == "0" || boardMtx[fila][col].charAt(0) == 'b' && side == "1") && col != changePos.getCol()){ // Hay pieza rival en diagonal y se mueve alli

                    if(((!clavado && !jaque)|| (dirClavada.x == 1 && dirClavada.y == -1 && col == changePos.getCol()-1)) || (jaque && !clavado && findValidMove(fila, col))) { // No clavada o clavada en esa dirección. Come hacia la izquierda
                        if(!turnoIA &&  fila == 7 && side.equals("0")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "bQ";
                        else if(!turnoIA && fila == 7 && side.equals("1")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "wQ";
                        else if(!turnoIA && side == "0") boardMtx[fila][col] = "bp";
                        else if(!turnoIA && side == "1") boardMtx[fila][col] = "wp";
                        if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                        Log.d("d:", "Puede comer");
                        Log.d("checkPawn:", "jaque: " + jaque + " clavado: " + clavado+ " dirClavada: " + dirClavada.x + " " + dirClavada.y+ " col: " +col + " fila: " +fila);
                        for(int i = 0; i < numMovimientos;i++){
                            Log.d("checkPawn:", "Contenido:  " + movimValidos[i].X + " " + movimValidos[i].Y);

                        }
                        return true;
                    }
                    if(((!clavado && !jaque) || (dirClavada.x == 1 && dirClavada.y == 1 && col == changePos.getCol()+1))|| (jaque && !clavado && findValidMove(fila, col))) {
                        if(!turnoIA &&  fila == 7 && side.equals("0")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "bQ";
                        else if(!turnoIA && fila == 7 && side.equals("1")) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "wQ";
                        else if(!turnoIA && side == "0") boardMtx[fila][col] = "bp";
                        else if(!turnoIA && side == "1") boardMtx[fila][col] = "wp";
                        if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";

                        return true;
                    }
                }
            }
        //}
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
        if(side=="0"){ // Juega como blancas
            for(int i= 0; i < NUM_FILCOL;i++){
                pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+squareSize,"Pawn",squareSize,"b",bPawn,side)); num++;
                pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+(6*squareSize),"Pawn",squareSize,"w",wPawn,side)); num++;
            }
            pieceSet.put(num,new ChessPiece(x0,y0,"Rook",squareSize,"b",bRook,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(7*squareSize),y0,"Rook",squareSize,"b",bRook,side)); num++;
            pieceSet.put(num,new ChessPiece(x0,y0+(7*squareSize),"Rook",squareSize,"w",wRook,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(7*squareSize),y0+(7*squareSize),"Rook",squareSize,"w",wRook,side)); num++;
                                            // left top
            pieceSet.put(num,new ChessPiece(x0+squareSize,y0,"Knight",squareSize,"b",bKnight,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(6*squareSize),y0,"Knight",squareSize,"b",bKnight,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+squareSize,y0+(7*squareSize),"Knight",squareSize,"w",wKnight,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(6*squareSize),y0+(7*squareSize),"Knight",squareSize,"w",wKnight,side)); num++;

            pieceSet.put(num,new ChessPiece(x0+(2*squareSize),y0,"Bishop",squareSize,"b",bBishop,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(5*squareSize),y0,"Bishop",squareSize,"b",bBishop,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(2*squareSize),y0+(7*squareSize),"Bishop",squareSize,"w",wBishop,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(5*squareSize),y0+(7*squareSize),"Bishop",squareSize,"w",wBishop,side)); num++;

            pieceSet.put(num,new ChessPiece(x0+(3*squareSize),y0,"Queen",squareSize,"b",bQueen,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(3*squareSize),y0+(7*squareSize),"Queen",squareSize,"w",wQueen,side)); num++;

            pieceSet.put(num,new ChessPiece(x0+(4*squareSize),y0,"King",squareSize,"b",bKing,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(4*squareSize),y0+(7*squareSize),"King",squareSize,"w",wKing,side)); num++;
        }else{ // Juega como negras
            for(int i= 0; i < NUM_FILCOL;i++){
                pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+squareSize,"Pawn",squareSize,"w",wPawn,side)); num++;
                pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+(6*squareSize),"Pawn",squareSize,"b",bPawn,side)); num++;
            }
            pieceSet.put(num,new ChessPiece(x0,y0,"Rook",squareSize,"w",wRook,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(7*squareSize),y0,"Rook",squareSize,"w",wRook,side)); num++;
            pieceSet.put(num,new ChessPiece(x0,y0+(7*squareSize),"Rook",squareSize,"b",bRook,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(7*squareSize),y0+(7*squareSize),"Rook",squareSize,"b",bRook,side)); num++;
            // left top
            pieceSet.put(num,new ChessPiece(x0+squareSize,y0,"Knight",squareSize,"w",wKnight,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(6*squareSize),y0,"Knight",squareSize,"w",wKnight,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+squareSize,y0+(7*squareSize),"Knight",squareSize,"b",bKnight,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(6*squareSize),y0+(7*squareSize),"Knight",squareSize,"b",bKnight,side)); num++;

            pieceSet.put(num,new ChessPiece(x0+(2*squareSize),y0,"Bishop",squareSize,"w",wBishop,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(5*squareSize),y0,"Bishop",squareSize,"w",wBishop,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(2*squareSize),y0+(7*squareSize),"Bishop",squareSize,"b",bBishop,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(5*squareSize),y0+(7*squareSize),"Bishop",squareSize,"b",bBishop,side)); num++;

            pieceSet.put(num,new ChessPiece(x0+(3*squareSize),y0,"King",squareSize,"w",wKing,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(3*squareSize),y0+(7*squareSize),"King",squareSize,"b",bKing,side)); num++;

            pieceSet.put(num,new ChessPiece(x0+(4*squareSize),y0,"Queen",squareSize,"w",wQueen,side)); num++;
            pieceSet.put(num,new ChessPiece(x0+(4*squareSize),y0+(7*squareSize),"Queen",squareSize,"b",bQueen,side)); num++;
        }
    }

    private void setMatrix(){
        for(int i = 0;i < NUM_FILCOL;i++){
            for(int j = 0; j < NUM_FILCOL;j++) boardMtx[i][j] = "--";
        }
        if(side == "0"){ // Si juega como blancas
            for(int i = 0; i < NUM_FILCOL; i++){
                boardMtx[1][i] = "bp"; boardMtx[6][i] = "wp";
            }
            boardMtx[0][0] = "bR"; boardMtx[0][7] = "bR"; boardMtx[7][0] = "wR"; boardMtx[7][7] = "wR";
            boardMtx[0][1] = "bN"; boardMtx[0][6] = "bN"; boardMtx[7][1] = "wN"; boardMtx[7][6] = "wN";
            boardMtx[0][2] = "bB"; boardMtx[0][5] = "bB"; boardMtx[7][2] = "wB"; boardMtx[7][5] = "wB";
            boardMtx[0][3] = "bQ"; boardMtx[7][3] = "wQ";
            boardMtx[0][4] = "bK"; boardMtx[7][4] = "wK";
        }else{ // Si juega como negras
            for(int i = 0; i < NUM_FILCOL; i++){
                boardMtx[1][i] = "wp"; boardMtx[6][i] = "bp";
            }
            boardMtx[0][0] = "wR"; boardMtx[0][7] = "wR"; boardMtx[7][0] = "bR"; boardMtx[7][7] = "bR";
            boardMtx[0][1] = "wN"; boardMtx[0][6] = "wN"; boardMtx[7][1] = "bN"; boardMtx[7][6] = "bN";
            boardMtx[0][2] = "wB"; boardMtx[0][5] = "wB"; boardMtx[7][2] = "bB"; boardMtx[7][5] = "bB";
            boardMtx[0][3] = "wK"; boardMtx[7][3] = "bK";
            boardMtx[0][4] = "wQ"; boardMtx[7][4] = "bQ";
        }

    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        addPieces(canvas);
    }

    protected void drawBoard(Canvas canvas){
        p.setStrokeWidth(3);
        int VERY_LIGHT_BLUE = Color.rgb(0,255,255);
        int VERY_LIGHT_YELLOW = Color.rgb(255,255,153);
        p.setStyle(Paint.Style.FILL);
        if(boardColor.equals("BoardGris"))  p.setColor(Color.LTGRAY);
        else if(boardColor.equals("BoardAzul")) p.setColor(VERY_LIGHT_BLUE);
        else if(boardColor.equals("BOardMarron")) p.setColor(VERY_LIGHT_YELLOW);


        q.setStrokeWidth(3);
        int DARK_BLUE = Color.rgb(0,128,255);
        int DARK_BROWN = Color.rgb(120,63,16);
        q.setStyle(Paint.Style.FILL);
        if(boardColor.equals("BoardGris"))  q.setColor(Color.DKGRAY);
        else if(boardColor.equals("BoardAzul")) q.setColor(DARK_BLUE);
        else if(boardColor.equals("BoardMarron")) q.setColor(DARK_BROWN);
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