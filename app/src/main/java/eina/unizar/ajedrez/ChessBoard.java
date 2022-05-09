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
    HashMap<Integer,ChessPiece> pieceSet  = new HashMap<Integer,ChessPiece>();
    int numPieza;
    int posX, posY, posFinX, posFinY;
    boolean pulsado, movimientoCorrecto = false;
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
    Pos posReyNegro = new Pos(0,4);
    Pos posReyBlanco = new Pos(7,4);
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
     * | wR wN wB wQ wK wB wN wR |
     * ___________________________
     **************************************************************/

    public ChessBoard(Context context, String side){
        super(context);
        p = new Paint();
        q = new Paint();
        Rec = new Rect();
        setPieceSet();
        setMatrix();
        turno = "w";
        this.side = side;
        controlador = new AiControl();
    }

    public boolean checkCorrectMov(char turno){ // Añadir parametro para devolver las posiciones
        Log.d("d: ", "mC " + movimientoCorrecto + " t " +  turno + " p "+ this.turno.charAt(0));
        return movimientoCorrecto ;
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

                        Log.d(TAG, "Soltado en pos:" + posFinX + " y " + posFinY);
                        if (isAClick(posX, posFinX, posY, posFinY)) { // Fila o Col distinta al de la casilla inicial
                            ChessPiece changePos = pieceSet.get(numPieza);
                            // Comprobar si el movimiento es valido (choque con otras piezas)
                            Log.d(TAG, "Entra aqui");
                            if (checkValidMove(changePos, posFinX, posFinY)) {
                                movimientoCorrecto = true;
                                int piezaDown = eatsPiece(posFinX, posFinY);
                                if (piezaDown != -1) pieceSet.remove(piezaDown); // Comer pieza rival
                                changePos.newCoord(posFinX, posFinY); // Cambiar posición a la pieza
                                pieceSet.put(numPieza, changePos);

                                if (checkForMate()) Log.d(TAG, "Fin de partida");
                                if (turno == "w" && !mate) turno = "b"; // Cambio de turno
                                else if (!mate) turno = "w";
                                else turno = "x";
                                pulsado = false;
                                turnoIA = true;
                                invalidate();
                            } else {
                                pulsado = false;
                                pieceSet.put(numPieza, changePos);// Devolver pieza sin cambios, movimiento invalido
                            }
                        }
                    }
                //}
        }
        return super.onTouchEvent(e);
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
        if(turno == "w") {
            rival = 'b';
            filaIni = posReyBlanco.X; colIni = posReyBlanco.Y;
        } else{
            rival = 'w';
            filaIni = posReyNegro.X; colIni = posReyNegro.Y;
        }
        for(int i =0; i < NUM_FILCOL;i++){
            Pair dirActual = dirs[i];
            PosibleClavada posibleClavada = null;
            for(int j= 1; j < NUM_FILCOL;j++){
                colFin =  colIni + (dirActual.y*j);
                filaFin =  filaIni + (dirActual.x*j);
                if (0 <= filaFin && filaFin<=7 && 0 <= colFin && colFin<=7){
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
                                (j == 1 && tipo == 'p' && ((rival == 'w' && 6 <= i && i <= 7) || (rival == 'b' && 4 <= i && i <= 5))) ||
                                (tipo == 'Q') || (j==1 && tipo == 'K')){
                            if(posibleClavada == null){
                                jaque = true;
                                if(jaques == null) jaques = new PosibleClavada[15];
                                jaques[numJaques] = new PosibleClavada();
                                jaques[numJaques].fila = filaFin; jaques[numJaques].col = colFin;
                                jaques[numJaques].dir = dirActual;
                                numJaques++;
                                break;
                            }
                            else{
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
    }
    public void makeAIMove(){
        ArrayList<Movimiento> movs = generarTodosMovimientosValidos();
        Log.d("d: ", "Movs: " + movs.size());
        Movimiento m = controlador.mejorMov(boardMtx,movs);
        if(m == null) mate = true;
        Log.d("d: ", "Movimiento final: " + m.inicial.X + " " + m.inicial.Y);
        Log.d("d: ", "Hasta: " + m.fin.X + " " + m.fin.Y);
        int piezaDown = eatsPiece(m.fin.Y, m.fin.X);
        ChessPiece p;
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            p = entry.getValue();
            if(p.checkPos(m.inicial.Y,m.inicial.X) && !mate) { // Encontrar pieza a mover por la ia en el set de pieza
                turnoIA = false;
                if (checkCertainPiece(p, m.fin.Y, m.fin.X)) {
                    Log.d("d: ", "Movimiento en orden " + p.getType() + " color " + p.getColor() + " EN " + p.getFila() + " y  " + p.getCol());
                    int val = entry.getKey();
                    p.newCoord(m.fin.Y, m.fin.X); // Cambiar posición a la pieza
                    pieceSet.put(val, p);
                    Log.d("d: ", "Movimiento en orden");
                    break;
                }
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

    private boolean isAClick(int startX, int endX, int startY, int endY) {
        return startX!=endX || startY != endY;
    }

    public boolean checkClick(int col, int fil){ // Comprueba si se ha pulsado una pieza
        for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
            ChessPiece p = entry.getValue();
            if(p.checkPos(col,fil)) {
                numPieza = entry.getKey();
                if (turno == "w" && p.getColor() == "w" && side == "0") return true; // Le toca a las blancas, el click es sobre una blanca y el jugador juega con blancas
                else if (turno == "b" && p.getColor() == "b" && side == "1") return true; // Le toca a las negras, el click es sobre una negra y el jugador juega con blancas
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
        int colIni = p.getCol();
        int filaIni = p.getFila();
        for(int i =0; i < NUM_FILCOL;i++){
            Pair dirActual = dirs[i];
            for(int j= 1; j < NUM_FILCOL;j++){
                int colFin =  colIni + (dirActual.y*j);
                int filaFin =  filaIni + (dirActual.x*j);
                if (0 <= filaFin && filaFin<=7 && 0 <= colFin && colFin<=7 && p.getType() != "Knight"){
                    if(checkValidMove(p,colFin,filaFin)){
                        Log.d("d: ", "Mov valido para " + p.getType()+ " Color: "+ p.getColor()+" Fil: "+ p.getFila() + " Col " + p.getCol());
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
                    if(checkValidMove(p,colFin,filaFin)){
                        Log.d("d: ", "Mov valido para " + p.getType()+ " Color: "+ p.getColor()+" Fil: "+ p.getFila() + " Col " + p.getCol());
                        Movimiento nuevoMov = new Movimiento(new Pos(p.getFila(),p.getCol()),new Pos(filaFin,colFin));
                        movsValidos.add(nuevoMov);
                        //Log.d("d: ", "EEEEEEEEEEEEEEEEEEEEEEEEEy " + boardMtx[filaFin][colFin] + " fila: " +filaFin + " col: "+ colFin);
                    }
                }
            }
        }
    }

    public ArrayList<Movimiento> generarTodosMovimientosValidos(){
        ArrayList<Movimiento> movsValidos = new ArrayList<Movimiento>();
        Log.d("d: ", "Turno " + turno);
        for(int fila = 0; fila < NUM_FILCOL;fila++){
            for(int col = 0; col<NUM_FILCOL;col++){
                if(boardMtx[fila][col].charAt(0) == turno.charAt(0)){
                    for(Map.Entry<Integer,ChessPiece> entry : pieceSet.entrySet()){
                        ChessPiece p = entry.getValue();
                        if(p.checkPos(col,fila)) { // Encontrada pieza que se busca, comprobar movimientos validos
                            movimientosParaPieza(p,movsValidos);
                        }
                    }
                }
            }
        }
        return movsValidos;
    }
    private void hallarMovimientosValidos(){
        Pair dirJaque = null;
        String piezaJaque;
        int filaIni, colIni, filaJaque, colJaque;
        if(turno == "w") {
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
            if(numJaques == 1) hallarMovimientosValidos(); // Solo una pieza hace jaque al rey
            else if(numJaques > 1) dobleJaque = true; // Si esta es true, solo se puede mover el rey (doble jaque)
           /* if((numMovimientos == 0 && numClavadas == 0) || dobleJaque){ // Comprobar jaque mate
                if (checkForMate()) return true;
            }*/
        }
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

        checkingMate = true;
        Pos[] guardaMovimientoValido = movimValidos;
        boolean guardaJaque = jaque;
        boolean guardaDobleJaque = dobleJaque;
        int guardaNumMov, guardanumClav = 0;
        guardaNumMov = numMovimientos; guardanumClav = numClavadas;

        if((guardaJaque && guardaNumMov <= 1 && guardanumClav == 0) || guardaDobleJaque) { // Puede ser mate
            for (Map.Entry<Integer, ChessPiece> entry : pieceSet.entrySet()) {
                ChessPiece p = entry.getValue();
                int key = entry.getKey();
                if (p.getType() == "King" && turno != p.getColor()) { // Ver si el rey puede deshacer el mate
                    Pair[] dirs = new Pair[8];
                    dirs[0] = new Pair(-1, 0); dirs[1] = new Pair(0, -1); dirs[2] = new Pair(1, 0);
                    dirs[3] = new Pair(0, 1); dirs[4] = new Pair(-1, -1); dirs[5] = new Pair(-1, 1);
                    dirs[6] = new Pair(1, -1);dirs[7] = new Pair(1, 1);

                    for (int i = 0; i < NUM_FILCOL; i++) {
                        Pair dirActual = dirs[i];
                        if(comprobarBordes(dirActual,p.getFila(),p.getCol())) {
                            if(turno == "w") turno = "b";
                            else turno = "w";
                            if (checkKing( dirActual.y + p.getCol(), dirActual.x + p.getFila(),p)) {
                                if (turno == "w") turno = "b";
                                else turno = "w";
                                Log.d("d:", "Rey todavia puede moverse");
                                checkingMate =  false;
                                return false;
                            }
                            if(turno == "w") turno = "b";
                            else turno = "w";
                        }
                    }
                    Log.d("d:", "Jaque mate con tomate");
                    pieceSet.remove(key);
                    break;
                }else if(turno != p.getColor() && !guardaDobleJaque && guardaNumMov == 1){ // Si otra pieza del color del turno puede comer a la pieza del jaque
                    if(checkCertainPiece(p, guardaMovimientoValido[0].Y,guardaMovimientoValido[0].X)) {checkingMate =  false; return false; }//  Pieza puede deshacer jaque. No es mate
                }
            }
        }else if(!guardaJaque || guardaNumMov > 1 || guardanumClav != 0){ checkingMate =  false; return false;}
        Log.d("d:", "Se confirma el mate");
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
        Pos posVieja;
        if(checkRook(col,fila,changePos) || checkBishop(col, fila, changePos)){ // Si hace un movimiento tipo torre o alfil
            if(turno == "w"){
                posVieja =  new Pos(posReyBlanco.X, posReyBlanco.Y);
                posReyBlanco.X =  fila; posReyBlanco.Y = col;
            }else{
                posVieja =  new Pos(posReyNegro.X, posReyNegro.Y);
                posReyNegro.X =  fila; posReyNegro.Y = col;
            }
            jaque =  false;
            checkInfoChecks();

            if(!jaque) { // Dibujar nueva pos en función del color
                Log.d("d:", "Nuevo mov correcto "+ posReyNegro.X + " " + posReyNegro.Y);
                if (turno == "w" && !checkingMate && !turnoIA) boardMtx[fila][col] = "wK";
                else if(!checkingMate && !turnoIA) boardMtx[fila][col] = "bK";
                if(!checkingMate && !turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                else checkingMate =  false;
                return true;
            }else{ // El movimiento que se hace deriva en jaque, devolver la posición antigua del rey
                if(turno == "w"){
                    posReyBlanco.X =  posVieja.X; posReyBlanco.Y = posVieja.Y;;
                }else{
                    posReyNegro.X =  posVieja.X; posReyNegro.Y = posVieja.Y;
                }
            }
        }
        return false;
    }
    private boolean checkBishop(int col, int fila, ChessPiece changePos){
        int numDesplazados =  Math.abs(col -changePos.getCol());
        int numDesplazados2 = Math.abs(fila -changePos.getFila());

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
      //  if(turno == "w" && side == "0" && turno == "b" && side == "1"){
            if(numClavadas != 0) {
                for (int i = 0; i < numClavadas; i++) {
                    if (clavadas[i].fila == changePos.getFila() && clavadas[i].col == changePos.getCol()) { // Pieza clavada es la que se esta intentando mover
                        clavado = true;
                        dirClavada = new Pair(clavadas[i].dir.x, clavadas[i].dir.y);
                    }
                }
            }

            if(turno == "w"){ // Turno de las blancas
                if(boardMtx[changePos.getFila()-1][changePos.getCol()] == "--" && col == changePos.getCol()){// No hay nadie delante
                    if(fila == changePos.getFila()-1){ // Se quiere mover una hacia adelante
                        if(((!clavado && !jaque) || (dirClavada.x == -1 && dirClavada.y == 0)) || jaque && !clavado && findValidMove(fila, col)) { // No clavada o clavada en la dirección a la que se mueve
                            if(!turnoIA) boardMtx[changePos.getFila() - 1][changePos.getCol()] = "wp";
                            if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                            Log.d("d: ", "Puede avanzar uno");
                            return true;
                        }
                    }
                    if(changePos.getFila() == 6 && fila == 4 && boardMtx[fila][col] == "--"){ // Se quiere mover 2 adelante
                        if(((!clavado && ! jaque) || (dirClavada.x == -1 && dirClavada.y == 0)) || (jaque && !clavado && findValidMove(fila, col))) {
                            if(!turnoIA) boardMtx[changePos.getFila() - 2][changePos.getCol()] = "wp";
                            if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                            Log.d("d: ", "Puede avanzar dos");
                            return true;
                        }
                    }
                } else if(boardMtx[fila][col].charAt(0) == 'b' && col != changePos.getCol()){ // Hay pieza rival en diagonal y se mueve alli
                    if(((!clavado && !jaque) || (dirClavada.x == -1 && dirClavada.y == -1 && col == changePos.getCol()-1))|| jaque && !clavado && findValidMove(fila, col)) { // No clavada o clavada en esa dirección. Come hacia la izquierda
                        if(!turnoIA) boardMtx[fila][col] = "wp";
                        if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                        Log.d("d:", "Puede comer izda");
                        return true;
                    }
                    if(((!clavado && !jaque) || (dirClavada.x == -1 && dirClavada.y == 1 && col == changePos.getCol()+1)) || jaque && !clavado && findValidMove(fila, col)) { // No clavada o clavada en esa dirección. Come hacia la derecha
                        if(!turnoIA) boardMtx[fila][col] = "wp";
                        if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                        Log.d("d: ", "Puede comer dcha");
                        return true;
                    }
                }
            }
            else{ // Turno de negras
                if(boardMtx[changePos.getFila()+1][changePos.getCol()] == "--" && col == changePos.getCol()){// No hay nadie delante
                    if(fila == changePos.getFila()+1){ // Se quiere mover una hacia adelante
                        if(((!clavado && !jaque) || (dirClavada.x == 1 && dirClavada.y == 0)) || jaque && !clavado && findValidMove(fila, col)) {
                            Log.d("d: ", "Puede avanzar uno");
                            if(!turnoIA) boardMtx[changePos.getFila() + 1][changePos.getCol()] = "bp";
                            if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                            return true;
                        }
                    }
                    if(changePos.getFila() == 1 && fila == 3 && boardMtx[fila][col] == "--"){ // Se quiere mover 2 adelante
                        if(((!clavado && ! jaque) || (dirClavada.x == 1 && dirClavada.y == 0)) || jaque && !clavado && findValidMove(fila, col)) {
                            Log.d("d:", "Puede avanzar dos");
                            if(!turnoIA) boardMtx[changePos.getFila() + 2][changePos.getCol()] = "bp";
                            if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                            return true;
                        }
                    }
                } else if(boardMtx[fila][col].charAt(0) == 'w' && col != changePos.getCol()){ // Hay pieza rival en diagonal y se mueve alli

                    if(((!clavado && !jaque)|| (dirClavada.x == 1 && dirClavada.y == -1 && col == changePos.getCol()-1)) || jaque && !clavado && findValidMove(fila, col)) { // No clavada o clavada en esa dirección. Come hacia la izquierda
                        if(!turnoIA) boardMtx[fila][col] = "bp";
                        if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                        Log.d("d:", "Puede comer");
                        return true;
                    }
                    if(((!clavado && !jaque) || (dirClavada.x == 1 && dirClavada.y == 11 && col == changePos.getCol()+1))|| jaque && !clavado && findValidMove(fila, col)) {
                        if(!turnoIA) boardMtx[fila][col] = "bp";
                        if(!turnoIA) boardMtx[changePos.getFila()][changePos.getCol()] = "--";
                        Log.d("d:", "Puede comer");
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
                pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+squareSize,"Pawn",squareSize,"b",bPawn)); num++;
                pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+(6*squareSize),"Pawn",squareSize,"w",wPawn)); num++;
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
        }else{ // Juega como negras
            for(int i= 0; i < NUM_FILCOL;i++){
                pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+squareSize,"Pawn",squareSize,"w",wPawn)); num++;
                pieceSet.put(num,new ChessPiece(x0+(i*squareSize),y0+(6*squareSize),"Pawn",squareSize,"b",bPawn)); num++;
            }
            pieceSet.put(num,new ChessPiece(x0,y0,"Rook",squareSize,"w",wRook)); num++;
            pieceSet.put(num,new ChessPiece(x0+(7*squareSize),y0,"Rook",squareSize,"w",wRook)); num++;
            pieceSet.put(num,new ChessPiece(x0,y0+(7*squareSize),"Rook",squareSize,"b",bRook)); num++;
            pieceSet.put(num,new ChessPiece(x0+(7*squareSize),y0+(7*squareSize),"Rook",squareSize,"b",bRook)); num++;
            // left top
            pieceSet.put(num,new ChessPiece(x0+squareSize,y0,"Knight",squareSize,"w",wKnight)); num++;
            pieceSet.put(num,new ChessPiece(x0+(6*squareSize),y0,"Knight",squareSize,"w",wKnight)); num++;
            pieceSet.put(num,new ChessPiece(x0+squareSize,y0+(7*squareSize),"Knight",squareSize,"b",bKnight)); num++;
            pieceSet.put(num,new ChessPiece(x0+(6*squareSize),y0+(7*squareSize),"Knight",squareSize,"b",bKnight)); num++;

            pieceSet.put(num,new ChessPiece(x0+(2*squareSize),y0,"Bishop",squareSize,"w",wBishop)); num++;
            pieceSet.put(num,new ChessPiece(x0+(5*squareSize),y0,"Bishop",squareSize,"w",wBishop)); num++;
            pieceSet.put(num,new ChessPiece(x0+(2*squareSize),y0+(7*squareSize),"Bishop",squareSize,"b",bBishop)); num++;
            pieceSet.put(num,new ChessPiece(x0+(5*squareSize),y0+(7*squareSize),"Bishop",squareSize,"b",bBishop)); num++;

            pieceSet.put(num,new ChessPiece(x0+(3*squareSize),y0,"King",squareSize,"w",wKing)); num++;
            pieceSet.put(num,new ChessPiece(x0+(3*squareSize),y0+(7*squareSize),"King",squareSize,"b",bKing)); num++;

            pieceSet.put(num,new ChessPiece(x0+(4*squareSize),y0,"Queen",squareSize,"w",wQueen)); num++;
            pieceSet.put(num,new ChessPiece(x0+(4*squareSize),y0+(7*squareSize),"Queen",squareSize,"b",bQueen)); num++;
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