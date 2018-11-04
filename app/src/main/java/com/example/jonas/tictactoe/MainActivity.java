package com.example.jonas.tictactoe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import 	android.preference.PreferenceManager;

import java.lang.reflect.Array;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private TicTacToeConsole mGame;
    public enum DifficultyLevel {Easy, Harder, Expert};
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;
    static final int DIALOG_QUIT_ID = 1;
    private BoardView mBoardView;
    private boolean mGameOver = false;
    public int winner;
    private boolean mSoundOn = true;
    private int mScores[];
    private String mVictoryMessage;

    private SharedPreferences mPrefs;


    //sounds
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    //alert
    AlertDialog.Builder builder;
    AlertDialog dialog;
    public void setAudio (){
        Context context = MainActivity.this;
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch( audio.getRingerMode() ){
            case AudioManager.RINGER_MODE_NORMAL:
                mSoundOn=true;
                break;
            case AudioManager.RINGER_MODE_SILENT:
                mSoundOn=false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                mSoundOn=false;
                break;
        }
    }

    public void setSettings(){
        mVictoryMessage = getResources().getString(R.string.result_human_wins);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficutly_level",
                getResources().getString(R.string.difficutly_harder));
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        mGame = new TicTacToeConsole();
        setAudio();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);
        builder= new AlertDialog.Builder(MainActivity.this);
        dialog=builder.create();
        setSettings();
        startNewGame();
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mScores = savedInstanceState.getIntArray("mScores");
        setScores();
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver",mGameOver);
        outState.putIntArray("mScores",mScores);

    }
    public void setScores(){
        TextView ts=(TextView) findViewById(R.id.ties_score);
        ts.setText(String.valueOf(mScores[1]));
        TextView hs=(TextView) findViewById(R.id.human_score);
        hs.setText(String.valueOf(mScores[2]));
        TextView as=(TextView) findViewById(R.id.android_score);
        as.setText(String.valueOf(mScores[3]));
    }
    public void startNewGame(){
        mGameOver=false;
        mGame.clearBoard();
        mBoardView.invalidate();
    }

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate();
            return true;
        }
        return false;
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            String str;
            setMove(TicTacToeConsole.HUMAN_PLAYER, pos);
            mHumanMediaPlayer.start();
            winner = mGame.checkForWinner();
            mScores=mGame.getScores();
            switch (winner){
                case 0:
                    computerTimeHandler();
                    winner=mGame.checkForWinner();
                    break;
                case 1:
                    mGameOver=true;
                    builder.setTitle("Fin del Juego");
                    str=getResources().getString(R.string.result_tie);
                    builder.setMessage(str);
                    builder.show();
                    setScores();
                    break;
                case 2:
                    mGameOver=true;
                    builder.setTitle("Fin del Juego");
                    builder.setMessage(mVictoryMessage);
                    builder.show();
                    setScores();
                    break;
                case 3:
                    mGameOver=true;
                    builder.setTitle("Fin del Juego");
                    str=getResources().getString(R.string.result_android_wins);
                    builder.setMessage(str);
                    builder.show();
                    setScores();
                    break;
                default:
                    break;
            }
            return false;
        }
    };


@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.inflateMenu(R.menu.options_menu);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.new_game:
                        startNewGame();
                        break;
                    case R.id.settings:
                        startActivityForResult(new Intent(MainActivity.this,Settings.class),0);
                        break;
                    case R.id.quit:
                        showDialog(DIALOG_QUIT_ID);
                        break;
                }
                return true;
            }
        });
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CANCELED) {
            mSoundOn = mPrefs.getBoolean("sound", true);
            String difficultyLevel = mPrefs.getString("difficutly_level",
                    getResources().getString(R.string.difficutly_harder));
            mVictoryMessage = mPrefs.getString("victory_message",
                    getResources().getString(R.string.result_human_wins));



        }

    }
    public void setVictoryMessage(){

    }

    public void computerTimeHandler(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int move = mGame.getComputerMove();
                setMove(TicTacToeConsole.COMPUTER_PLAYER, move);
                mComputerMediaPlayer.start();
            }
        }, 1000);
    }
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound1);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound2);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.commit();
    }



}
