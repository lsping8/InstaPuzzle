package fusion.com.soicalrpgpuzzle;

import android.app.Activity;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Brian on 5/10/2016.
 */
public class CustomSetup {

    // howManyRows = rowsCount, howManyPerRow = pieceByRow, inverstNum = Maximum ID of the linear layout in puzzle layout
    private int chunkNumbers, howManyRows, howManyPerRow, ownInverstNum;
    private ArrayList<Integer> ownRows = new ArrayList<>();

    public CustomSetup(String pieces) {
        switch (pieces) {
            case "4x4":
                chunkNumbers = 16;
                howManyRows = 4;
                howManyPerRow = 4;
                ownInverstNum = 3;
                for (int i = 0; i < howManyRows; i++) {
                    Log.d(GoogleServiceApi.TAG, "AssignOwnRows: " + i);
                    ownRows.add(i);
                }
                break;

            case "5x5":
                chunkNumbers = 25;
                howManyRows = 5;
                howManyPerRow = 5;
                ownInverstNum = 4;
                for (int i = 0; i < howManyRows; i++) {
                    Log.d(GoogleServiceApi.TAG, "AssignOwnRows: " + i);
                    ownRows.add(i);
                }
                break;

            case "6x6":
                chunkNumbers = 36;
                howManyRows = 6;
                howManyPerRow = 6;
                ownInverstNum = 5;
                for (int i = 0; i < howManyRows; i++) {
                    Log.d(GoogleServiceApi.TAG, "AssignOwnRows: " + i);
                    ownRows.add(i);
                }
                break;
        }
    }

    public int getChunkNumbers() {
        return chunkNumbers;
    }

    public int getHowManyRows() {
        return howManyRows;
    }

    public int getHowManyPerRow() {
        return howManyPerRow;
    }

    public int getOwnInverstNum() {
        return ownInverstNum;
    }

    public ArrayList<Integer> getOwnRows() {
        return ownRows;
    }
}
