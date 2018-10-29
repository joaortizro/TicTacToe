package com.example.jonas.tictactoe;

/* TicTacToeConsole.java
 * By Frank McCown (Harding University)
 *
 * This is a tic-tac-toe game that runs in the console window.  The human
 * is x and the computer is o.
 */

import android.util.Log;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class TicTacToeConsole {

    private char mBoard[] = {'1','2','3','4','5','6','7','8','9'};
    public static final int BOARD_SIZE = 9;

    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';
    public enum DifficultyLevel {
        Easy, Harder, Expert
    }
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;
    private Random mRand;
    private int mScore[]=new int [4];
    public TicTacToeConsole() {

        // Seed the random number generator
        mRand = new Random();

    }



    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if x won
    //  3 if o won
    public int checkForWinner() {
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public int getComputerMove() {

        int move = -1;

        if (mDifficultyLevel == DifficultyLevel.Easy)
            move = getRandomMove();
        else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        } else if (mDifficultyLevel == DifficultyLevel.Expert) {
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }
        if(move != -1)
            return move;
        return move;
    }

    private int getWinningMove(){

        int move;
        move = getRandomMove();
        return move;
    }
    private int getBlockingMove(){
        int move;
        move = getRandomMove();
        return move;
    }
    private int getRandomMove() {
        int move = -1;
        do {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
        return move;
    }

    public boolean setMove(char player , int location){

        if(mBoard[location]==HUMAN_PLAYER || mBoard[location]==COMPUTER_PLAYER) return false;
        mBoard[location]=player;
        return true;
    }
    public void clearBoard(){
        for(int i=0;i<BOARD_SIZE;i++){
            mBoard[i]=OPEN_SPOT;
        }
    }
    public char getBoardOccupant(int i) {return mBoard[i]; }
    public char[] getBoardState(){
        return mBoard;
    }

    public void setBoardState(char[] _board){
        mBoard=_board ;
    }
    public int[] getScores(){
        int winner=checkForWinner();
        mScore[winner]+=1;
        return mScore;
    }

}