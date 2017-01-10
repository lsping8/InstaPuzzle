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
public class Inventory {

    public List<String>  ownProfileList, colourList;

    public Inventory() {
        this.ownProfileList = new ArrayList<>();
        this.colourList = new ArrayList<>();
    }

    public Inventory(List<String> colourList) {
        this.ownProfileList = new ArrayList<>();
        this.colourList = colourList;
    }

    public List<String> getOwnProfileList() {
        return ownProfileList;
    }

    public List<String> getColourList() {
        return colourList;
    }

    public void setOwnProfileList(List<String> ownProfileList) {
        this.ownProfileList = ownProfileList;
    }

    public void setColourList(List<String> colourList) {
        this.colourList = colourList;
    }
}
