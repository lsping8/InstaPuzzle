package fusion.com.soicalrpgpuzzle;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Brian on 7/14/2016.
 */
public class Track {

    public List<String> loseToPlayer, ownProfileList, colour;

    public Track() {
        this.ownProfileList = new ArrayList<>();
        this.loseToPlayer = new ArrayList<>();
        this.colour = new ArrayList<>();
    }

    public List<String> getOwnProfileList() {
        return ownProfileList;
    }

    public List<String> getLoseToPlayer() {
        return loseToPlayer;
    }

    public List<String> getColour() {
        return colour;
    }

    public void setOwnProfileList(List<String> ownProfileList) {
        this.ownProfileList = ownProfileList;
    }

    public void setLostToPlayer(List<String> loseToPlayer) {
        this.loseToPlayer = loseToPlayer;
    }

    public void setColour(List<String> colour) {
        this.colour = colour;
    }
}
