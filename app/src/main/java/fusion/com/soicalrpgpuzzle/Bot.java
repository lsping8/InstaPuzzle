package fusion.com.soicalrpgpuzzle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;

/**
 * Created by Pandora on 6/7/2016.
 */
public class Bot {

    ArrayList<Integer> arrayRow;
    int column, rows, currentRow, currentArray, oppComboCount, botCompleteCount = 0;
    Handler handler = new Handler();
    NewPuzzleActivity puzzleActivity;
    long delay, bestTime = -1, worstTime = -1;
    Random random = new Random();
    Runnable runnable;
    GlobalState state;
    GeneralImage generalImage;
    boolean starting = true;
    LinearLayout current_linearLayout;

    public Bot(NewPuzzleActivity puzzleActivity, GlobalState state, GeneralImage generalImage) {
        this.puzzleActivity = puzzleActivity;
        this.state = state;
        this.generalImage = generalImage;
        String gameMode = state.getGameMode(puzzleActivity.oppCurrPicCounter);
        column = Integer.parseInt(gameMode.substring(0, 1));
        rows = Integer.parseInt(gameMode.substring(0, 1));
        this.puzzleActivity.new LoadExtraPicture("opp", rows, column).execute(generalImage);
        oppComboCount = 0;
    }

    private void getGameMode() {
        String gameMode = state.getGameMode(puzzleActivity.oppCurrPicCounter);
        arrayRow = new ArrayList<>();
        column = Integer.parseInt(gameMode.substring(0, 1));
        rows = Integer.parseInt(gameMode.substring(0, 1));

        for (int i = 0; i < rows; i++) {
            arrayRow.add(i);
        }
    }

    public void initialiseBot() {
        getGameMode();
        currentArray = 0;
        currentRow = 0;
        Collections.shuffle(arrayRow);
        puzzleActivity.oppGenerateHashMap(column * rows, column);
        puzzleActivity.createHighLight();
        botStart();
    }

    public void botStart() {
        if (currentArray == rows) {
            finalTimeBot();
        } else {
            currentRow = arrayRow.get(currentArray);
            puzzleActivity.createImageOpp(currentRow);
            botPlayingRow();
        }
    }

    public void botPlayingRow() {
        current_linearLayout = (LinearLayout) puzzleActivity.opp_puzzle.findViewWithTag("oppRows_" + currentRow);
        puzzleActivity.startCheckOppPuzzle(currentRow);
        setDelay();
    }

    private void setDelay() {
        if (bestTime == -1 || worstTime == -1) {
            delay = 1000 * column + 1000;
        } else {
            delay = worstTime;
        }
        timeBot();
    }

    private void timeBot() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (random.nextInt(100) <= 20) {
                    oppComboCount = 0;
                }
                oppComboCount++;
                currentArray++;
                puzzleActivity.reArrangeColumn(currentRow);
                puzzleActivity.updateOppScore(current_linearLayout.getChildCount(), oppComboCount,1000);
                botStart();
            }
        };
        handler.postDelayed(runnable, delay);
    }

    private void finalTimeBot() {
        runnable = new Runnable() {
            @Override
            public void run() {
                puzzleActivity.reArrangeRow();
                oppComboCount++;
                puzzleActivity.updateOppScore(current_linearLayout.getChildCount(), oppComboCount,1000);
                reStartBot();
            }
        };
        handler.postDelayed(runnable, delay);
    }

    private void reStartBot() {
        runnable = new Runnable() {
            @Override
            public void run() {
                puzzleActivity.updateOppCurrentPuzzleCounter("next_image");
                botCompleteCount++;
                puzzleActivity.updateOppImageClear(botCompleteCount);
                if (botCompleteCount != 4) {
                    state.setFirst_complete("opp");
                    initialiseBot();
                }
                else
                    puzzleActivity.endGameDialogue();
            }
        };
        handler.postDelayed(runnable, 500);
    }


    public void timeRecord(long timeRecord) {
        if (timeRecord < delay) {
            bestTime = timeRecord;
        } else if (timeRecord < 5000)
            worstTime = timeRecord;
        Log.d("delay", worstTime + "");
    }

    public void stopBot() {
        handler.removeCallbacks(runnable);
    }
}
