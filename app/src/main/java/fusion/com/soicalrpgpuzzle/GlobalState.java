package fusion.com.soicalrpgpuzzle;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 4/24/2016.
 */
public class GlobalState extends Application {

    private GoogleServiceApi googleServiceApi = null;
    private GeneralImage generalImage;
    private String startGame = null,first_complete = null;
    private String skillName = null, imReady = null , ownName = null , oppName = null, googlePlayProfile = null;
    private float width, height;
    private float percentage;
    ArrayList<String> gameMode = new ArrayList<>();
    private boolean supportSkill,firstLoading = true;
    private String url_profile_pic, opp_url_profile_pic;
    private Bitmap self_profile_pic;
    private List<String> loseToPlayer = new ArrayList<>(), ownProfileList = new ArrayList<>();
    private List<String> challengerList = new ArrayList<>();
    public List<String> trackColourList = new ArrayList<>();
    private Inventory track = null;
    private Player player = null;
    private GameResources resources = null;
    private MatchDetails matchDetails = null;
    private Challenger challenger = null;
    private String uid;
    private InitialLoading.SignInGoogleService signInGoogleService;
    private Inventory inventory;

    public void setmGoogleApi(GoogleServiceApi mGoogleApiClient) {
        this.googleServiceApi = mGoogleApiClient;
    }

    public GoogleServiceApi getmGoogleApi() {
        return googleServiceApi;
    }

    public void setGeneralImage(GeneralImage generalImage) {
        this.generalImage = generalImage;
    }

    public GeneralImage getGeneralImage() {
        return generalImage;
    }

    public void setGeneralImageNull(){
        generalImage = null;
    }

    public void setSkillName(String skillname, float percentage, boolean supportSkill) {
        skillName = skillname;
        this.percentage = percentage;
        this.supportSkill = supportSkill;
    }

    public String getSkillName() {
        return skillName + "," + percentage;
    }

    public boolean getSupportSkill(){
        return supportSkill;
    }

    public void setImReady(String imReady){
        this.imReady = imReady;
    }

    public String getImReady(){
        return imReady;
    }

    public void storeGameMode(String gameMode) {
        this.gameMode.add(gameMode);
    }

    public String getGameMode(int curr) {
        return gameMode.get(curr);
    }

    public void clearGameMode(){
        gameMode.clear();
        gameMode = new ArrayList<>();
    }

    public void setDimensionScreen(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public String getDimensionScreen() {
        return (width + "," + height);
    }

    public void setStartGame(String string) {
        startGame = string;
    }

    public String getStartGame() {
        return startGame;
    }

    public void setName(String name){
        ownName = name;
    }

    public String getName(){
        return ownName;
    }

    public void setOppName(String oppName){
        this.oppName = oppName;
    }

    public String getOppName(){
        return oppName;
    }

    public void setFirstLoading(){
        firstLoading = false;
    }

    public boolean getFirstLoading(){
        return firstLoading;
    }

    public void setFirst_complete(String string){
        first_complete = string;
    }

    public String getFirst_complete(){
        return first_complete;
    }

    public void setSelfProfilePic(Bitmap self_profile_pic) {
        this.self_profile_pic = self_profile_pic;
    }

    public void setSelfProfilePicUrl(String link_profile_pic) {
        this.url_profile_pic = link_profile_pic;
    }

    public String getOwnProfileURL() {
        return url_profile_pic;
    }

    public void setOppProfilePic(String oppUrl) {
        opp_url_profile_pic = oppUrl;
    }

    public String getOppProfilePic() {
        return opp_url_profile_pic;
    }

    public Bitmap getSelfProfilePic() {
        return self_profile_pic;
    }

    public void resetAll() {
        startGame = null;
        skillName = null;
        this.percentage = 0;
        this.supportSkill = false;
        imReady = null;
        oppName = null;
        first_complete = null;
        opp_url_profile_pic = null;
        clearGameMode();
    }

    public void setMatchDetails(MatchDetails matchDetails) {
        this.matchDetails = matchDetails;
    }

    public void setLoseToPlayer(List<String> loseToPlayer) {
        this.loseToPlayer = loseToPlayer;
    }

    public List<String> getLoseToPlayer() {
        return this.loseToPlayer;
    }

    public void setOwnProfileList(List<String> ownProfileList) {
        this.ownProfileList = ownProfileList;
    }

    public List<String> getOwnProfileList() {
        return this.ownProfileList;
    }

    public MatchDetails getMatchDetails() {
        return this.matchDetails;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("TERMIANTE", "TERMINATE");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    public int getChallengerSlot(String rank) {

        int result = 0;

        switch (rank) {
            case "Novice":
                result = 1;
                break;
        }

        return result;
    }

    public void setTrack(Inventory track) {
        this.track = track;
    }

    public Inventory getTrack() {
        return this.track;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setGameResources(GameResources resources) {
        this.resources = resources;
    }

    public GameResources getGameResources() {
        return this.resources;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setChallenger(Challenger challenger) {
        this.challenger = challenger;
    }

    public Challenger getChallenger() {
        return this.challenger;
    }

    public void setGooglePlayProfile(String googlePlayProfile) {
        this.googlePlayProfile = googlePlayProfile;
    }

    public String getGooglePlayProfile() {
        return this.googlePlayProfile;
    }

    public void checkStartMainMenu() {
        Log.d(GoogleServiceApi.TAG, "checkStartMainMenu");
        if (track != null && player != null && resources != null) {
            Log.d(GoogleServiceApi.TAG, "start");
            signInGoogleService.startMainMenu();
        }
    }

    public void setInitialLoadingActivity(InitialLoading.SignInGoogleService signInGoogleService) {
        this.signInGoogleService = signInGoogleService;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
