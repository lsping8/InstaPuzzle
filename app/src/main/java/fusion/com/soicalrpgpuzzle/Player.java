package fusion.com.soicalrpgpuzzle;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Brian on 7/8/2016.
 */
@IgnoreExtraProperties
public class Player {

    public int win, lose;
    public String rank, name, instaId, googlePlayId, colour, googlePlayName;
    public Bitmap instaProfilePic, googlePlayProfile;
    public ArrayList<String> challengerList, inventory;

    public Player() {
    }

    public Player( String rank, int win, int lose, String googlePlayId, String name, Bitmap googlePlayProfile) {
        this.rank = rank;
        this.win = win;
        this.lose = lose;
        this.googlePlayId = googlePlayId;
        this.googlePlayProfile = googlePlayProfile;
        this.name = name;
        this.instaProfilePic = null;
        this.challengerList = new ArrayList<>();
        this.inventory = new ArrayList<>();
    }

    public Player( String rank, int win, int lose, Bitmap profilePic, String name, String instaId) {
        this.rank = rank;
        this.win = win;
        this.lose = lose;
        this.instaProfilePic = profilePic;
        this.name = name;
        this.googlePlayId = null;
        this.instaId = instaId;
        this.challengerList = new ArrayList<>();
        this.inventory = new ArrayList<>();
    }


    // For Instagram (New)
    public Player( String rank, int win, int lose, String instaId, String colour) {
        this.rank = rank;
        this.win = win;
        this.lose = lose;
        this.instaId = instaId;
        this.colour = colour;
        this.challengerList = new ArrayList<>();
        this.inventory = new ArrayList<>();
    }

    // For GooglePlay (New)
    public Player( String rank, String googlePlayId, int win, int lose, String colour , String googlePlayName) {
        this.rank = rank;
        this.win = win;
        this.lose = lose;
        this.googlePlayId = googlePlayId;
        this.colour = colour;
        this.challengerList = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.googlePlayName = googlePlayName;
    }


    public Bitmap getInstaProfilePic() {
        return this.instaProfilePic;
    }

    public String getName() {
        return this.name;
    }

    public int getWin() {
        return this.win;
    }

    public int getLose() {
        return this.lose;
    }

    public String getInstaId() {
        return this.instaId;
    }

    public String getRank() {
        return this.rank;
    }

    public String getGooglePlayId() {
        return googlePlayId;
    }

    public Bitmap getGooglePlayProfile() {
        return this.googlePlayProfile;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public void setInstaId(String instaId) {
        this.instaId = instaId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstaProfilePic(Bitmap instaProfilePic) {
        this.instaProfilePic = instaProfilePic;
    }

    public void setGooglePlayId(String googlePlayId) {
        this.googlePlayId = googlePlayId;
    }

    public void setGooglePlayProfile(Bitmap googlePlayProfile) {
        this.googlePlayProfile = googlePlayProfile;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public ArrayList<String> getChallengerList() {
        return this.challengerList;
    }

    public void setChallengerList(ArrayList<String> challengerList) {
        this.challengerList = challengerList;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getColour() {
        return this.colour;
    }

    public void setInventory(ArrayList<String> inventory) {
        this.inventory = inventory;
    }

    public ArrayList<String> getInventory() {
        return this.inventory;
    }

    public void setGooglePlayName(String googlePlayName) {
        this.googlePlayName = googlePlayName;
    }

    public String getGooglePlayName() {
        return this.googlePlayName;
    }

}