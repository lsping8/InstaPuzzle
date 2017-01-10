package fusion.com.soicalrpgpuzzle;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Brian on 4/14/2016.
 */
public class GeneralImage {

    private ArrayList<String> ownPopularImageList, oppPopularImageList, botPopularImageList;
    private ArrayList<Bitmap> ownPopularBitmapList, oppPopularBitmapList , oppBotPopularBitmapList;
    private ArrayList<Bitmap> ownPreviewBitmapList,botPopularBitmapList,botPreviewBitmapList;

    public GeneralImage() {
        ownPopularImageList = new ArrayList<>();
        oppPopularImageList = new ArrayList<>();
        botPopularImageList = new ArrayList<>();
        ownPopularBitmapList = new ArrayList<>();
        oppPopularBitmapList = new ArrayList<>();
        ownPreviewBitmapList = new ArrayList<>();
        botPopularBitmapList = new ArrayList<>();
        botPreviewBitmapList = new ArrayList<>();
        oppBotPopularBitmapList = new ArrayList<>();
    }

    public void setOwnPopularImageList(String popularImageList) {
        this.ownPopularImageList.add(popularImageList);
    }

    public void setBotPopularImageList(String popularImageList) {
        botPopularImageList.add(popularImageList);
    }

    public void setOppPopularImageList(String popularImageList) {
        this.oppPopularImageList.add(popularImageList);
    }

    public void setOwnPopularBitmapList(Bitmap popularBitmap) {
        this.ownPopularBitmapList.add(popularBitmap);
    }

    public void setOwnPreviewBitmapList(Bitmap bitmap) {
        ownPreviewBitmapList.add(bitmap);
    }

    public void setBotPreviewBitmapList(Bitmap bitmap){
        botPreviewBitmapList.add(bitmap);
    }

    public ArrayList<Bitmap> getBotPreviewBitmapList(){
        return botPreviewBitmapList;
    }

    public void setBotPopularBitmapList(Bitmap bitmap) {
        botPopularBitmapList.add(bitmap);
    }

    public ArrayList<Bitmap> getOwnPreviewBitmapList() {
        return ownPreviewBitmapList;
    }

    public void clearPreviewBitmap() {
        if (ownPreviewBitmapList != null && botPreviewBitmapList != null) {
            for (int i = 0; i < ownPreviewBitmapList.size(); i++) {
                ownPreviewBitmapList.get(i).setHasAlpha(false);
                ownPreviewBitmapList.get(i).recycle();
            }
            ownPreviewBitmapList.clear();
            for (int i = 0; i < botPreviewBitmapList.size(); i++) {
                botPreviewBitmapList.get(i).setHasAlpha(false);
                botPreviewBitmapList.get(i).recycle();
            }
            botPreviewBitmapList.clear();
        }
    }

    public ArrayList<Bitmap> getOwnPopularBitmapList() {
        return this.ownPopularBitmapList;
    }

    public void setOppPopularBitmapList(Bitmap popularBitmap) {
        this.oppPopularBitmapList.add(popularBitmap);
    }

    public ArrayList<Bitmap> getOppPopularBitmapList() {
        return this.oppPopularBitmapList;
    }

    public void setOppBotPopularBitmapList(Bitmap bitmap){
        oppBotPopularBitmapList.add(bitmap);
    }

    public ArrayList<Bitmap> getOppBotPopularBitmapList(){
        return oppBotPopularBitmapList;
    }

    public ArrayList<Bitmap> getBotPopularBitmapList(){
        return botPopularBitmapList;
    }


    public ArrayList<String> getOwnPopularImageList() {
        return ownPopularImageList;
    }

    public ArrayList<String> getOppPopularImageList() {
        return oppPopularImageList;
    }

    public ArrayList<String> getBotPopularImageList(){
        return botPopularImageList;
    }

    public void reInitializeOppPopularImageList() {
        if (oppPopularImageList != null)
            oppPopularImageList.clear();
    }

    public void reInitializeOwnPopularImageList() {
        if (ownPopularImageList != null)
            ownPopularImageList.clear();
    }

    public void clearOwnPopularBitmapList() {

        if (ownPopularBitmapList != null) {

            for (int i = 0; i < ownPopularBitmapList.size(); i++) {
                ownPopularBitmapList.get(i).recycle();
            }
            ownPopularBitmapList.clear();
        }
    }

    public void clearBotPopularBitmapList() {
        if (botPopularBitmapList != null) {

            for (int i = 0; i < botPopularBitmapList.size(); i++) {
                botPopularBitmapList.get(i).recycle();
            }
            botPopularBitmapList.clear();
        }
    }

    public void clearOppPopularBitmapList() {

        if (oppPopularBitmapList != null) {

            for (int j = 0; j < oppPopularBitmapList.size(); j++) {
                oppPopularBitmapList.get(j).recycle();
            }
            oppPopularBitmapList.clear();
        }
    }

    public void oppClearPopularImageList() {
        if (oppPopularImageList != null) {
            oppPopularImageList.remove(0);
        }
    }

    public void clearMemory() {
        oppPopularImageList.clear();
        ownPopularImageList.clear();
        botPopularImageList.clear();
        clearOppPopularBitmapList();
        clearOwnPopularBitmapList();
        clearBotPopularBitmapList();
        reInitializeOppPopularImageList();
        reInitializeOwnPopularImageList();
    }
}
