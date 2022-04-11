package eina.unizar.ajedrez;

import java.util.ArrayList;

public class AiControl {
    int NUM_FILCOL = 8;
    int mate = 1000;
    int ahogado = 0;
    int puntKing = 0;
    int puntQueen = 10;
    int puntRook = 5;
    int puntBishopKnight = 3;
    int puntPawn = 1;

     public ChessBoard.Movimiento mejorMov(String[][] boardMtx, ArrayList<ChessBoard.Movimiento> movsValidos){
        int maxPunt = mate;
        int guarda = -1;
        String viejoIni;
        String viejoEnd;
      //  ChessBoard.Pos p = new ChessBoard.Pos(0,0);
        ChessBoard.Movimiento miMov = null;//new ChessBoard.Movimiento(new ChessBoard.Pos(0,0), new ChessBoard.Pos(0,0));
        for(int i = 0;i < movsValidos.size();i++){
            ChessBoard.Movimiento m =  movsValidos.get(i);
            //Log.d("d: ", "Mov valido para " + m.inicial.X + " " + m.inicial.Y);
            viejoEnd =  boardMtx[m.fin.X][m.fin.Y]; // Guardar posición final antes de nuevo movimiento
            viejoIni =  boardMtx[m.inicial.X][m.inicial.Y]; // Guardar posición inicial antes de nuevo movimiento
            boardMtx[m.inicial.X][m.inicial.Y] = "--";
            boardMtx[m.fin.X][m.fin.Y] = viejoIni;
            int punt =  obtenerPuntuacion(boardMtx);
          //  Log.d("d: ", "La puntuacion es  " + punt);
            if(punt <= maxPunt){
                maxPunt = punt;
                guarda = i;
              //  miMov.inicial.X =  m.inicial.X; miMov.inicial.Y =  m.inicial.Y;
              //  miMov.inicial.X =  m.inicial.Y; miMov.fin.Y =  m.fin.Y;
               // Log.d("d: ", "Mov despues de todo" + m.inicial.X + " " + m.inicial.Y);
            }
            boardMtx[m.fin.X][m.fin.Y] = viejoEnd;
            boardMtx[m.inicial.X][m.inicial.Y] = viejoIni;
        }
         Log.d("d: ",  " con guarda "+ guarda + " " +movsValidos.get(guarda).inicial.X + " "+ movsValidos.get(guarda).inicial.Y);
       // Log.d("d: ", "Pos elegida: " + miMov.fin.X + " "+miMov.fin.Y);
       if(guarda != -1) return movsValidos.get(guarda);
       return null;
    }

    private int devolverPunt(char tipo){
        switch (tipo){
            case 'K': return puntKing;
            case 'Q': return puntQueen;
            case 'R': return puntRook;
            case 'N': return puntBishopKnight;
            case 'B': return puntBishopKnight;
            case 'p': return puntPawn;
        }
        return 0;
    }

    private int obtenerPuntuacion(String[][] boardMtx){
        int punt = 0;
        for(int fila = 0; fila < NUM_FILCOL;fila++){
            for(int col = 0; col < NUM_FILCOL;col++){
                if(boardMtx[fila][col].charAt(0) == 'w'){// Sumar puntuaciones a blancas
                    punt += devolverPunt(boardMtx[fila][col].charAt(1));
                }else if(boardMtx[fila][col].charAt(0) == 'b'){ // Sumar puntuaciones a negras
                    punt -= devolverPunt(boardMtx[fila][col].charAt(1));
                }
            }
        }
        return punt;
    }
}
