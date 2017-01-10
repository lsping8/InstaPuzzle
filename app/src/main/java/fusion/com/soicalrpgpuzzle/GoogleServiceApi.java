package fusion.com.soicalrpgpuzzle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.graphics.BitmapCompat;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.internal.StreamContentsRequest;
import com.google.android.gms.games.*;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.android.gms.phenotype.Flag;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Brian on 4/21/2016.
 */
public class GoogleServiceApi implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener, RealTimeMultiplayer.ReliableMessageSentCallback {

    public final static String TAG = "TestGoogleAPI";

    private GoogleApiClient mGoogleApiClient = null;
    private Activity currActivity = null;
    private InitialLoading.SignInGoogleService currActivityThread = null;
    final static int RC_SIGN_IN = 9001;
    final String leaderBoardId = "CgkI5-PLgpcDEAIQAQ";
    private boolean mSignInClicked, mResolvingConnectionFailure, isAllConnected = false;
    private ArrayList<Participant> mParticipants = null;
    private boolean playerHost;
    public static String mRoomId, mMyId, oppId = null;
    private Context context;
    GeneralImage generalImage;
    GlobalState state;
    private NewPuzzleActivity puzzleActivity = null;
    private static final int APP_STATE_KEY = 0;
    private ProgressDialog mProgressDialog;
    MainMenu menuActivity;
    //Listener mListener = null;
    float width, height;
    long startTime, startTime2;
    boolean threadRunning = true;
    String fullURL = null;
    SinglePlayer singlePlayer;
    boolean botEnable = false, cancelRoom = false;
    ArrayList<String> ownImageOwner, oppImageOwner;
    WaitingRoom waitingRoom;
    protected static final String SHARED = "Instagram_Preferences";
    String oppInstagramID, oppFireBaseId, oppGoogleID;
    StartMatch startMatch;
    Handler handler;
    Runnable runnable;


    public GoogleServiceApi(Context context, Activity currActivity) {

        this.context = context;
        this.currActivity = currActivity;
        state = (GlobalState) currActivity.getApplicationContext();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .setViewForPopups(currActivity.findViewById(android.R.id.content))
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d(GoogleServiceApi.TAG, "-----------------GooglServiceApi:onConnected----------------");

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mSignInClicked = true;
            currActivityThread.updateProgressBar();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d(GoogleServiceApi.TAG, "-----------------GooglServiceApi:onConnectionSuspended----------------");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(GoogleServiceApi.TAG, "-----------------GooglServiceApi:onConnectionFailed----------------");
        Log.d("onConnectionFailed", connectionResult.getErrorCode() + "");
        mResolvingConnectionFailure = true;

        // Attempt to resolve the connection failure using BaseGameUtils.
        // The R.string.signin_other_error value should reference a generic
        // error string in your strings.xml file, such as "There was
        // an issue with sign in, please try again later."
        if (!BaseGameUtils.resolveConnectionFailure(currActivity,
                mGoogleApiClient, connectionResult,
                RC_SIGN_IN, context.getString(R.string.signin_other_error))) {
            Log.d(GoogleServiceApi.TAG, "!BaseGameUtils.resolveConnectionFailure");
            mResolvingConnectionFailure = false;
        }
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setCurrActivity(Activity currActivity) {
        this.currActivity = currActivity;
    }


    public void setCurrActThread(InitialLoading.SignInGoogleService currActivityThread) {
        this.currActivityThread = currActivityThread;
    }

    public void setMainMenuAct(MainMenu menuActivity) {
        this.menuActivity = menuActivity;
    }

    public void startQuickGame(GeneralImage generalImage, WaitingRoom waitingRoom) {
        cancelRoom = false;

        Log.d(GoogleServiceApi.TAG, "-----------------GooglServiceApi:startQuickGame----------------");

        this.generalImage = generalImage;
        this.waitingRoom = waitingRoom;
        this.mParticipants = null;

        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);

        Log.d(TAG, "quickgame!");
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
    }

    private void onSinglePlayer() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                botEnable = true;
                Games.RealTimeMultiplayer.leave(mGoogleApiClient, GoogleServiceApi.this, mRoomId);
                currActivityThread.cancel(true);
                singlePlayer = new SinglePlayer();
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    public void startSinglePlayer(GeneralImage generalImage, WaitingRoom waitingRoom) {
        if (this.generalImage == null)
            this.generalImage = generalImage;
        if (this.waitingRoom == null)
            this.waitingRoom = waitingRoom;
        if (handler == null)
            handler = new Handler();
        GoogleServiceApi.this.waitingRoom.setPlayerFound();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                singlePlayer = new SinglePlayer();
                singlePlayer.execute(GoogleServiceApi.this.generalImage);
            }
        }, 100);
    }

    public void cancelGame() {
        if (singlePlayer != null) {
            singlePlayer.cancel(true);
            generalImage = null;
            state.resetAll();
            Log.d("CANCEL", "CANCEL");
        } else {
            if (mRoomId != null) {
                handler.removeCallbacks(runnable);
                Games.RealTimeMultiplayer.leave(mGoogleApiClient, GoogleServiceApi.this, mRoomId);
                currActivityThread.cancel(true);
            } else {
                currActivityThread.cancel(true);
                cancelRoom = true;
            }
        }
    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {

        if (cancelRoom) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, GoogleServiceApi.this, room.getRoomId());
            currActivityThread.cancel(true);
            return;
        }

        startMatch = ((MainMenu) currActivity).getStartMatch();

        Log.d(TAG, currActivity.toString());

        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");

        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            BaseGameUtils.makeSimpleDialog(currActivity, context.getString(R.string.game_problem));
            return;
        }

        mRoomId = room.getRoomId();

        updateRoom(room);
        onSinglePlayer();
    }

    void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
            for (int i = 0; i < mParticipants.size(); i++) {
                Log.d(TAG, "Participants " + i + ": " + mParticipants.get(i).getParticipantId() + ";" + mParticipants.get(i).getDisplayName());
            }
            if (mParticipants.size() == 2) {
                handler.removeCallbacks(runnable);
                if (isHost()) {
                    waitingRoom.setSkill();
                    playerHost = true;
                } else {
                    playerHost = false;
                }
                waitingRoom.setPlayerFound();
            }
        }
    }

    public void clearPuzzle() {
        this.puzzleActivity = null;
        ownImageOwner.clear();
        oppImageOwner.clear();
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
    }

    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        Log.d(GoogleServiceApi.TAG, "onLeftRoom invoked!");
        if (singlePlayer != null && botEnable) {
            startSinglePlayer(generalImage, waitingRoom);
        }
    }

    @Override
    public void onRoomConnected(int i, Room room) {

    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] receiveMessageBytes = realTimeMessage.getMessageData();

        String receiveMessage = new String(receiveMessageBytes);
        Log.d(GoogleServiceApi.TAG, "IncomingMessage:" + receiveMessage);

        if (receiveMessage.length() > 12) {
            Log.d(GoogleServiceApi.TAG, "substring" + receiveMessage.substring(0, 12));
        }

        if (receiveMessage.substring(0, 4).equals("full")) {
            String[] fullOppUrl = receiveMessage.split("~");
            switch (fullOppUrl[0]) {
                case "fullOppUrl":
                    String[] oppUrl = fullOppUrl[1].split(",");
                    for (int i = 0; i < ownImageOwner.size(); i++) {
                        if (ownImageOwner.get(i).equals("opp")) {
                            Log.d("Owner", oppUrl[i]);
                            generalImage.setOwnPopularImageList(oppUrl[i]);
                        } else {
                            Log.d("Owner", oppUrl[i]);
                            generalImage.setBotPopularImageList(oppUrl[i]);
                        }
                    }
                    Log.d(GoogleServiceApi.TAG, "Going Start game!");
                    if (handler == null)
                        handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new ReceivedNetworkOperation().execute(generalImage);
                        }
                    }, 100);
                    break;
            }
        }

        if (receiveMessage.length() > 8) {
            if (receiveMessage.substring(0, 8).equals("ImageSeq")) {
                Log.d("ImageSeq", receiveMessage);
                String[] receiveMessageSplt = receiveMessage.split(":");
                String[] imageOwner = receiveMessageSplt[1].split(",");
                for (int i = 0; i < imageOwner.length; i++)
                    Log.d("IMAGEOWNER", imageOwner[i]);
                Collections.addAll(ownImageOwner, imageOwner);
            }
        }

        if (receiveMessage.equals("YOUWIN:") || receiveMessage.equals("YOULOSE:")) {
            switch (receiveMessage) {

                case "YOUWIN:":
                    state.setFirst_complete("own");
                    puzzleActivity.endGameDialogue();
                    break;

                case "YOULOSE:":
                    state.setFirst_complete("opp");
                    puzzleActivity.endGameDialogue();
                    break;
            }
        }

        if (receiveMessage.length() > 9) {
            if (!receiveMessage.equals("checkFinish") && !receiveMessage.substring(0, 10).equals("fullOppUrl")) {
                Log.d("Message", receiveMessage + "");
                String[] receiveMessageSplit = receiveMessage.split(":");
                int column, row;
                switch (receiveMessageSplit[0]) {

                    case "SkillName":
                        Log.d("SkillName",receiveMessageSplit[1]);
                        state.setSkillName(receiveMessageSplit[1],100,false);
                        break;

                    case "FireBaseID":
                        Log.d("FireBaseID", receiveMessageSplit[1]);
                        oppFireBaseId = receiveMessageSplit[1];
                        break;

                    case "InstaGramID":
                        Log.d("InstaGramID", receiveMessageSplit[1]);
                        oppInstagramID = receiveMessageSplit[1];
                        break;

                    case "GoogleID":
                        Log.d("GoogleID", receiveMessageSplit[1]);
                        oppGoogleID = receiveMessageSplit[1];
                        break;

                    case "checkFinish":
                        Log.d(TAG, "checkFinish reach");
                        state.setImReady("ready");
                        break;

                    case "profilePicture":
                        Log.d("PROFILE", receiveMessageSplit[1] + ":" + receiveMessageSplit[2]);
                        state.setOppProfilePic(receiveMessageSplit[1] + ":" + receiveMessageSplit[2]);
                        break;

                    case "googleProfilePicture":
                        Log.d("PROFILE", receiveMessageSplit[1] + ":" + receiveMessageSplit[2]);
                        state.setOppProfilePic(receiveMessageSplit[1] + ":" + receiveMessageSplit[2]);
                        break;

                    case "profileName":
                        Log.d("Name", receiveMessageSplit[1]);
                        state.setOppName(receiveMessageSplit[1]);
                        break;

                    case "onCreateComplete":
                        Log.d("ONCREAETE", "ONCREATE");
                        state.setStartGame("StartGame");
                        break;

                    case "StartTheGame":
                        state.setStartGame("FinallyStartTheGame");
                        break;

                    case "FirstSplitImage":
                        Log.d("Message", receiveMessageSplit[0] + "," + receiveMessageSplit[1]);
                        String[] rowAndColumn = receiveMessageSplit[1].split(",");
                        column = Integer.parseInt(rowAndColumn[1]);
                        row = Integer.parseInt(rowAndColumn[0]);
                        puzzleActivity.new LoadExtraPicture("opp", row, column).execute(generalImage);
                        break;

                    case "GenerateHash":
                        Log.d("Message", receiveMessageSplit[0] + "," + receiveMessageSplit[1]);
                        String[] totalChunkAndColumn = receiveMessageSplit[1].split(",");
                        int totalChunkNum = Integer.parseInt(totalChunkAndColumn[0]);
                        column = Integer.parseInt(totalChunkAndColumn[1]);
                        puzzleActivity.oppGenerateHashMap(totalChunkNum, column);
                        break;

                    case "CreateImage":
                        Log.d("Message", receiveMessageSplit[0] + "," + receiveMessageSplit[1]);
                        int currentRow = Integer.parseInt(receiveMessageSplit[1]);
                        puzzleActivity.createImageOpp(currentRow);
                        puzzleActivity.startCheckOppPuzzle(currentRow);
                        break;

                    case "CompleteRow":
                        Log.d("Message", receiveMessageSplit[0] + "," + receiveMessageSplit[1]);
                        String[] currentRowAndCombo = receiveMessageSplit[1].split(",");
                        currentRow = Integer.parseInt(currentRowAndCombo[0]);
                        puzzleActivity.reArrangeColumn(currentRow);
                        break;

                    case "UpdateScore":
                        String[] rowAndCombo = receiveMessageSplit[1].split(",");
                        int howManyColumn = Integer.parseInt(rowAndCombo[0]);
                        int combo = Integer.parseInt(rowAndCombo[1]);
                        int bonusScore = Integer.parseInt(rowAndCombo[2]);
                        puzzleActivity.updateOppScore(howManyColumn, combo, bonusScore);
                        break;

                    case "SkillDeActivated":
                        puzzleActivity.skillActiavtedAnimation("opp", receiveMessageSplit[1]);
                        break;

                    case "SkillActivated":
                        puzzleActivity.skillActiavtedAnimation("opp", receiveMessageSplit[1]);
                        break;

                    case "ImageComplete":
                        puzzleActivity.reArrangeRow();
                        break;

                    case "UpdateOwnImageClear":
                        int completeImageCount = Integer.parseInt(receiveMessageSplit[1]);
                        puzzleActivity.updateOppImageClear(completeImageCount);
                        break;

                    case "UpdateCurrentPuzzle":
                        puzzleActivity.updateOppCurrentPuzzleCounter(receiveMessageSplit[1]);
                        break;
                }
            }
        }
    }


    @Override
    public void onRoomConnecting(Room room) {
        Log.d(TAG, "onRoomConnecting...");
        updateRoom(room);
        onSinglePlayer();
    }

    @Override
    public void onRoomAutoMatching(Room room) {

    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {

    }

    @Override
    public void onPeerDeclined(Room room, List<String> list) {

    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {
        Log.d(TAG, "onPeerJoined...");

        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, list.get(i).toString());
        }
    }

    @Override
    public void onPeerLeft(Room room, List<String> list) {

    }

    @Override
    public void onConnectedToRoom(Room room) {

        Log.d(TAG, "onConnectedToRoom...");

        Log.d(TAG, "RoomID: " + mRoomId);

        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));
        Log.d(TAG, "MyId: " + mMyId);

        for (int i = 0; i < mParticipants.size(); i++) {
            if (!mParticipants.get(i).getParticipantId().equals(mMyId)) {
                oppId = mParticipants.get(i).getParticipantId();
                Log.d(TAG, "oppId: " + oppId);
                break;
            }
        }

        if (mParticipants.size() == 2) {
            Log.d(TAG, "Host: " + playerHost);
            new NetworkOperation().execute(generalImage);
        }
    }

    public Uri getOppGoogleURI() {
        Uri oppUri = null;
        for (int i = 0; i < mParticipants.size(); i++) {
            if (!mParticipants.get(i).getParticipantId().equals(mMyId)) {
                oppUri = mParticipants.get(i).getHiResImageUri();
                Log.d(TAG, "oppUri: " + oppId);
                break;
            }
        }
        return oppUri;
    }

    public boolean getPlayerHost() {
        return this.playerHost;
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        Log.d(TAG, "leave room");
        // leave the room
        Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
    }

    @Override
    public void onPeersConnected(Room room, List<String> list) {

    }

    @Override
    public void onPeersDisconnected(Room room, List<String> list) {

    }

    @Override
    public void onP2PConnected(String participantId) {
        Log.d(TAG, "onP2PConnected");
        Log.d(TAG, "participantId:" + participantId);
    }

    @Override
    public void onP2PDisconnected(String s) {

    }

    public boolean getmSignInClicked() {
        return mSignInClicked;
    }

    private Bitmap loadImageUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Log.d("error", e.toString());
            return null;
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmapToScale, float newWidth, float newHeight) {

        if (bitmapToScale == null) {
            Log.d(GoogleServiceApi.TAG, "nullscalebitmap: " + bitmapToScale.toString());
            return null;
        }

        Log.d(GoogleServiceApi.TAG, "bitmapToScale:" + bitmapToScale.toString());
        Log.d(GoogleServiceApi.TAG, "newwidth:" + newWidth);
        Log.d(GoogleServiceApi.TAG, "newheight:" + newHeight);
        return Bitmap.createScaledBitmap(bitmapToScale, (int) newWidth, (int) newHeight, true);
    }

    @Override
    public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {

        if (statusCode == GamesStatusCodes.STATUS_OK) {
           /* Intent startGame = new Intent(currActivity, NewPuzzleActivity.class);
             currActivity.startActivity(startGame);*/
            Intent startGame = new Intent(currActivity, NewPuzzleActivity.class);
            currActivity.startActivity(startGame);
        }
    }

    public void setPuzzleActivity(NewPuzzleActivity puzzleActivity) {
        this.puzzleActivity = puzzleActivity;
        Log.d("setPuzzleActivity", "setPuzzleActivity_done");
    }

    public ArrayList<String> getOwnImageOwner() {
        return ownImageOwner;
    }

    public ArrayList<String> getOppImageOwner() {
        return oppImageOwner;
    }

    public String getOppFirebaseId() {
        return oppFireBaseId;
    }

    public String getOppInstagramID() {
        return oppInstagramID;
    }

    public String getOppGoogleID() {
        return oppGoogleID;
    }

    private class NetworkOperation extends AsyncTask<GeneralImage, Void, GeneralImage> implements RealTimeMultiplayer.ReliableMessageSentCallback {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            handler.removeCallbacks(runnable);
            waitingRoom.setProgress();
        }

        @Override
        protected GeneralImage doInBackground(final GeneralImage... generalImage) {

            startTime = System.currentTimeMillis();
            ownImageOwner = new ArrayList<>();
            oppImageOwner = new ArrayList<>();
            String[] dimension = state.getDimensionScreen().split(",");
            height = Float.valueOf(dimension[1]);
            width = Float.valueOf(dimension[0]);

            for (int i = 0; i < generalImage[0].getOppPopularImageList().size(); i++) {
                if (i == 0) {
                    fullURL = generalImage[0].getOppPopularImageList().get(i) + ",";
                } else if (i == generalImage[0].getOppPopularImageList().size() - 1) {
                    fullURL = fullURL + generalImage[0].getOppPopularImageList().get(i);
                } else {
                    fullURL = fullURL + generalImage[0].getOppPopularImageList().get(i) + ",";
                }
            }

            Log.d("FULLURL", fullURL.length() + "");

            String sendImageSeq = "ImageSeq:" + waitingRoom.getOppImageSeq();
            byte[] sendImageSeqString = sendImageSeq.getBytes();
            Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendImageSeqString,
                    mRoomId, oppId);

            return generalImage[0];
        }

        @Override
        public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
            if (statusCode == GamesStatusCodes.STATUS_OK) {
                if (tokenId == 1) {

                    String sendFireBseId = "FireBaseID:" + waitingRoom.getFireBaseId();
                    byte[] sendFireBseIdString = sendFireBseId.getBytes();
                    Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendFireBseIdString,
                            mRoomId, oppId);

                    if (state.getPlayer().getGooglePlayProfile() != null) {
                        String sendInstaId = "GoogleID:" + waitingRoom.getGoogleId();
                        byte[] sendInstaIdString = sendInstaId.getBytes();
                        Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendInstaIdString,
                                mRoomId, oppId);
                    } else {
                        String sendInstaId = "InstaGramID:" + waitingRoom.getInstagramId();
                        byte[] sendInstaIdString = sendInstaId.getBytes();
                        Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendInstaIdString,
                                mRoomId, oppId);
                    }

                    String sendFullURL = "fullOppUrl~" + fullURL;
                    byte[] sendFullURLString = sendFullURL.getBytes();
                    Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendFullURLString,
                            mRoomId, oppId);
                    Log.d("fullOppUrl", fullURL + "");

                    if (playerHost) {
                        String sendSkillName = "SkillName:" + state.getSkillName();
                        byte[] sendSkillNameString = sendSkillName.getBytes();
                        Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendSkillNameString,
                                mRoomId, oppId);
                    }

                }
                Log.d("TokenId", tokenId + "");
                long endTime = System.currentTimeMillis();
                long totalTime = (endTime - startTime);
                Log.d("SEND_COMPLETE", totalTime + "");
            }

            if (statusCode == GamesStatusCodes.STATUS_REAL_TIME_MESSAGE_SEND_FAILED) {
                Log.d(GoogleServiceApi.TAG, tokenId + "-" + "FAiled!");
                switch (tokenId) {
                    case 1:
                        String sendImageSeq = "ImageSeq:" + waitingRoom.getOppImageSeq();
                        byte[] sendImageSeqString = sendImageSeq.getBytes();
                        Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendImageSeqString,
                                mRoomId, oppId);
                        break;

                    case 2:
                        String sendFireBseId = "FireBaseID:" + waitingRoom.getFireBaseId();
                        byte[] sendFireBseIdString = sendFireBseId.getBytes();
                        Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendFireBseIdString,
                                mRoomId, oppId);
                        break;

                    case 3:
                        if (state.getPlayer().getGooglePlayProfile() != null) {
                            String sendInstaId = "GoogleID:" + waitingRoom.getGoogleId();
                            byte[] sendInstaIdString = sendInstaId.getBytes();
                            Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendInstaIdString,
                                    mRoomId, oppId);
                        } else {
                            String sendInstaId = "InstaGramID:" + waitingRoom.getInstagramId();
                            byte[] sendInstaIdString = sendInstaId.getBytes();
                            Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendInstaIdString,
                                    mRoomId, oppId);
                        }
                        break;

                    case 4:
                        String sendFullURL = "fullOppUrl~" + fullURL;
                        byte[] sendFullURLString = sendFullURL.getBytes();
                        Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendFullURLString,
                                mRoomId, oppId);
                        break;

                    case 5:
                        if (playerHost) {
                            String sendSkillName = "SkillName:" + state.getSkillName();
                            byte[] sendSkillNameString = sendSkillName.getBytes();
                            Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, sendSkillNameString,
                                    mRoomId, oppId);
                        }
                        break;
                }
            }
        }
    }

    private class ReceivedNetworkOperation extends AsyncTask<GeneralImage, String, GeneralImage> {

        int progress = 0;

        @Override
        protected GeneralImage doInBackground(final GeneralImage... generalImage) {

            startTime2 = System.currentTimeMillis();
            int ownCounter = 0, botCounter = 0, imageToPlayCounter = 0;
            final float oppPuzzleWidth = (width / 1.55f);
            final float oppPuzzleHeight = (height / 2.88f);

            if (state.getPlayer().getGooglePlayProfile() != null) {

                com.google.android.gms.games.Player currentPlayer = Games.Players.getCurrentPlayer(GoogleServiceApi.this.getmGoogleApiClient());
                Log.d("googleProfilePic", currentPlayer.getHiResImageUri() + "");

                String sendImage = String.valueOf(currentPlayer.getHiResImageUri());
                String sendProfilePicture = "googleProfilePicture:" + sendImage;
                byte[] sendProfilePictureString = sendProfilePicture.getBytes();
                Games.RealTimeMultiplayer.sendUnreliableMessage(GoogleServiceApi.this.getmGoogleApiClient(), sendProfilePictureString,
                        GoogleServiceApi.this.getRoomId(), GoogleServiceApi.this.getOppId());

                String sendProfileName = "profileName:" + currentPlayer.getName();
                byte[] sendProfileNameString = sendProfileName.getBytes();
                Games.RealTimeMultiplayer.sendUnreliableMessage(GoogleServiceApi.this.getmGoogleApiClient(), sendProfileNameString,
                        GoogleServiceApi.this.getRoomId(), GoogleServiceApi.this.getOppId());
            } else {
                String sendProfilePicture = "profilePicture:" + state.getOwnProfileURL();
                byte[] sendProfilePictureString = sendProfilePicture.getBytes();
                Games.RealTimeMultiplayer.sendUnreliableMessage(GoogleServiceApi.this.getmGoogleApiClient(), sendProfilePictureString,
                        GoogleServiceApi.this.getRoomId(), GoogleServiceApi.this.getOppId());

                String sendProfileName = "profileName:" + state.getName();
                byte[] sendProfileNameString = sendProfileName.getBytes();
                Games.RealTimeMultiplayer.sendUnreliableMessage(GoogleServiceApi.this.getmGoogleApiClient(), sendProfileNameString,
                        GoogleServiceApi.this.getRoomId(), GoogleServiceApi.this.getOppId());
            }

            Log.d("IMAGEOWNER", ownImageOwner + "");

            for (int i = 0; i < 8; i++) {
                if (ownImageOwner.get(i).equals("opp")) {
                    ownImageOwner.set(i, "own");
                    final Bitmap bitmap = loadImageUrl(generalImage[0].getOwnPopularImageList().get(ownCounter));
                    ownCounter++;
                    if (imageToPlayCounter < 4) {
                        imageToPlayCounter++;
                        Log.d("ownDownload", generalImage[0].getOwnPopularImageList().get(0));
                        generalImage[0].setOwnPopularBitmapList(Bitmap.createScaledBitmap(bitmap, (int) width, (int) (height / 2), true));
                        progress++;
                        publishProgress(progress + "");
                    }
                    generalImage[0].setOwnPreviewBitmapList(scaleBitmap(bitmap, width / 2.5f, width / 2.5f));
                    progress++;
                    publishProgress(progress + "");
                    if (bitmap != null)
                        bitmap.recycle();
                } else {
                    generalImage[0].setBotPreviewBitmapList(null);
                    String currentBotImage = generalImage[0].getBotPopularImageList().get(botCounter);
                    downloadImageForBot(botCounter, imageToPlayCounter, currentBotImage, "ownBot");
                    botCounter++;
                    imageToPlayCounter++;
                }
            }

            for (int i = 0; i < 4; i++) {
                if (ownImageOwner.get(i).equals("own")) {
                    generalImage[0].getOwnPopularImageList().remove(0);
                } else {
                    generalImage[0].getBotPopularImageList().remove(0);
                }
            }

            String[] oppImageSeq = waitingRoom.getOppImageSeq().split(",");
            Collections.addAll(oppImageOwner, oppImageSeq);

            Log.d("IMAGEOWNER", oppImageOwner + "");

            Log.d("IMAGE", generalImage[0].getOppPopularImageList() + "");
            botCounter = 0;
            for (int i = 0; i < 4; i++) {
                if (oppImageOwner.get(i).equals("opp")) {
                    Bitmap oppBitmap = loadImageUrl(generalImage[0].getOppPopularImageList().get(0));
                    Log.d("oppDownload", generalImage[0].getOppPopularImageList().get(0));
                    generalImage[0].setOppPopularBitmapList(Bitmap.createScaledBitmap(oppBitmap, (int) oppPuzzleWidth, (int) oppPuzzleHeight, true));
                    progress++;
                    publishProgress(progress + "");
                    generalImage[0].oppClearPopularImageList();
                    if (oppBitmap != null)
                        oppBitmap.recycle();
                } else {
                    generalImage[0].setOppBotPopularBitmapList(null);
                    downloadImageForBot(botCounter, 4, generalImage[0].getOppPopularImageList().get(0), "oppBot");
                    generalImage[0].oppClearPopularImageList();
                    botCounter++;
                }
            }

            long endTime = System.currentTimeMillis();
            long totalTime = (endTime - startTime2);
            Log.d("FINISH_IMAGE", totalTime + "");
            return generalImage[0];
        }

        @Override
        protected void onPostExecute(final GeneralImage generalImage) {
            final GlobalState state = ((GlobalState) currActivity.getApplicationContext());
            state.setGeneralImage(generalImage);
            long endTime = System.currentTimeMillis();
            long totalTime = (endTime - startTime);
            Toast.makeText(context, "TotalTime Take : " + totalTime, Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (threadRunning) {
                        int ownCounter = 0, oppCounter = 0;
                        for (int i = 0; i < generalImage.getOwnPreviewBitmapList().size(); i++) {
                            if (generalImage.getOwnPreviewBitmapList().get(i) != null) {
                                ownCounter++;
                            }
                        }
                        for (int i = 0; i < generalImage.getBotPreviewBitmapList().size(); i++) {
                            if (generalImage.getBotPreviewBitmapList().get(i) != null) {
                                ownCounter++;
                            }
                        }
                        for (int i = 0; i < generalImage.getOppPopularBitmapList().size(); i++) {
                            if (generalImage.getOppPopularBitmapList().get(i) != null)
                                oppCounter++;
                        }
                        for (int i = 0; i < generalImage.getOppBotPopularBitmapList().size(); i++) {
                            if (generalImage.getOppBotPopularBitmapList().get(i) != null)
                                oppCounter++;
                        }
                        if (ownCounter == 8 && oppCounter == 4 && state.getSkillName() != null) {
                            threadRunning = false;
                        }
                    }
                    Log.d("finish_STILLCHECKING", "finish_STILLCHECKING");

                    String sendCheckFinish = "checkFinish:" + "";
                    byte[] sendCheckFinishString = sendCheckFinish.getBytes();
                    Games.RealTimeMultiplayer.sendUnreliableMessage(GoogleServiceApi.this.getmGoogleApiClient(), sendCheckFinishString,
                            GoogleServiceApi.this.getRoomId(), GoogleServiceApi.this.getOppId());

                    while (threadRunning) {
                        if (state.getImReady() != null && generalImage.getBotPreviewBitmapList().size() + generalImage.getOwnPreviewBitmapList().size() == 8) {
                            if (state.getImReady().equals("ready")) {
                                threadRunning = false;
                            }
                        }
                    }
                    Intent startGame = new Intent(currActivity, NewPuzzleActivity.class);
                    startGame.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    currActivity.startActivity(startGame);
                    waitingRoom.reset();
                    waitingRoom.restoreStartMatch();
                    singlePlayer = null;
                    waitingRoom = null;
                    System.gc();
                }
            }).start();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {
                waitingRoom.time.setProgress(Integer.parseInt(values[0]));
            } catch (Exception e) {
                Log.d("Progress", "Progress_ERROR");
                Log.d("Progress", waitingRoom.time + "");
            }
        }

        private void downloadImageForBot(final int position, final int imageToPlayCounter, final String randomNumber, final String putWhere) {
            final float oppPuzzleWidth = (width / 1.55f);
            final float oppPuzzleHeight = (height / 2.88f);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://socialrpgpuzzle-85412621.appspot.com");

            final StorageReference spaceRef = storageRef.child("socialpuzzle/skitterphoto_" + randomNumber + ".jpg");
            Log.d("BYTE", spaceRef + "");
            Log.d(GoogleServiceApi.TAG, "Picture Taken : " + spaceRef.getPath());

            if (imageToPlayCounter < 4) {
                Log.d("playCounter", imageToPlayCounter + "");
                generalImage.setBotPopularBitmapList(null);
            }

            final long ONE_MEGABYTE = 500 * 1000; //Maximum 500KB
            spaceRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    switch (putWhere) {
                        case "ownBot":
                            generalImage.getBotPreviewBitmapList().set(position, Bitmap.createScaledBitmap(bmp, (int) (width / 2.5f), (int) (width / 2.5f), true));
                            progress++;
                            publishProgress(progress + "");
                            if (imageToPlayCounter < 4) {
                                generalImage.getBotPopularBitmapList().set(position, Bitmap.createScaledBitmap(bmp, (int) width, (int) height / 2, true));
                                progress++;
                                publishProgress(progress + "");
                            }
                            break;
                        case "oppBot":
                            generalImage.getOppBotPopularBitmapList().set(position, Bitmap.createScaledBitmap(bmp, (int) oppPuzzleWidth, (int) oppPuzzleHeight, true));
                            progress++;
                            publishProgress(progress + "");
                            break;
                    }
                    if (bmp != null)
                        bmp.recycle();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(GoogleServiceApi.TAG, "exception" + exception);
                    downloadImageForBot(position, 4, randomNumber, putWhere);
                }
            });
        }
    }

    private class SinglePlayer extends AsyncTask<GeneralImage, String, String> {

        int progress = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waitingRoom.setProgress();
            waitingRoom.setSkill();
            Log.d("2", "2");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            singlePlayer.cancel(true);
            generalImage.clearMemory();
            state.resetAll();
        }

        private void downloadImageForBot(final int position, final int imageToPlayCounter, final String randomNumber, final String putWhere) {
            final float oppPuzzleWidth = (width / 1.55f);
            final float oppPuzzleHeight = (height / 2.88f);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://socialrpgpuzzle-85412621.appspot.com");

            final StorageReference spaceRef = storageRef.child("socialpuzzle/skitterphoto_" + randomNumber + ".jpg");
            Log.d("BYTE", spaceRef + "");
            Log.d(GoogleServiceApi.TAG, "Picture Taken : " + spaceRef.getPath());

            if (imageToPlayCounter < 4) {
                Log.d("playCounter", imageToPlayCounter + "");
                generalImage.setBotPopularBitmapList(null);
            }

            final long ONE_MEGABYTE = 500 * 1000; //Maximum 500KB
            spaceRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    switch (putWhere) {
                        case "ownBot":
                            generalImage.getBotPreviewBitmapList().set(position, Bitmap.createScaledBitmap(bmp, (int) (width / 2.5f), (int) (width / 2.5f), true));
                            progress++;
                            publishProgress(progress + "");
                            if (imageToPlayCounter < 4) {
                                generalImage.getBotPopularBitmapList().set(position, Bitmap.createScaledBitmap(bmp, (int) width, (int) height / 2, true));
                                progress++;
                                publishProgress(progress + "");
                            }
                            Log.d("DownloadImageComp", "DownloadImageComp");
                            break;
                        case "oppBot":
                            generalImage.getOppBotPopularBitmapList().set(position, Bitmap.createScaledBitmap(bmp, (int) oppPuzzleWidth, (int) oppPuzzleHeight, true));
                            progress++;
                            publishProgress(progress + "");
                            Log.d("DownloadImageComp", "DownloadImageComp");
                            break;
                    }
                    if (bmp != null)
                        bmp.recycle();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(GoogleServiceApi.TAG, "exception" + exception);
                    downloadImageForBot(position, 4, randomNumber, putWhere);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {
                waitingRoom.time.setProgress(Integer.parseInt(values[0]));
            } catch (Exception e) {
                Log.d("Progress", "Progress_ERROR");
                Log.d("Progress", waitingRoom.time + "");
            }
        }

        @Override
        protected String doInBackground(final GeneralImage... generalImage) {
            ownImageOwner = new ArrayList<>();
            oppImageOwner = new ArrayList<>();
            String[] dimension = state.getDimensionScreen().split(",");
            height = Float.valueOf(dimension[1]);
            width = Float.valueOf(dimension[0]);

            int ownCounter = 0, botCounter = 0, imageToPlayCounter = 0;
            float oppPuzzleWidth = (width / 1.55f);
            float oppPuzzleHeight = (height / 2.88f);
            Random random = new Random();
            long startTime = System.currentTimeMillis();

            generalImage[0].setOwnPopularImageList("https://scontent-kul1-1.cdninstagram.com/t51.2885-15/e35/13423618_1315886561773798_792806587_n.jpg?ig_cache_key=MTI4MDQzMTgzOTc1MjEyMTA3OQ%3D%3D.2");
            ownImageOwner.add("opp");
            generalImage[0].setOwnPopularImageList("https://scontent-kul1-1.cdninstagram.com/t51.2885-15/e35/14705105_396173344055567_7279397038989508608_n.jpg?ig_cache_key=MTQxNDQ2MDMyMjc4NTU3MTE4Mg%3D%3D.2");
            ownImageOwner.add("opp");
            generalImage[0].setOwnPopularImageList("https://scontent-kul1-1.cdninstagram.com/t51.2885-15/e35/13402560_1101680273222438_1111249213_n.jpg?ig_cache_key=MTI2NzM0OTcwMDA5MDUyNzcwOQ%3D%3D.2");
            ownImageOwner.add("opp");
            generalImage[0].setOwnPopularImageList("https://scontent-kul1-1.cdninstagram.com/t51.2885-15/e15/11262855_938187626231734_3955704_n.jpg?ig_cache_key=OTg1NDU4MzQ1OTA0NzAwNTcw.2");
            ownImageOwner.add("opp");

            if (ownImageOwner.size() != 8) {
                int remain = 8 - ownImageOwner.size();
                for (int i = 0; i < remain; i++) {
                    generalImage[0].setBotPopularImageList(random.nextInt(6) + "");
                    ownImageOwner.add("bot");
                }
            }

            Collections.shuffle(ownImageOwner);

            for (int i = 0; i < 8; i++) {
                if (ownImageOwner.get(i).equals("opp")) {
                    ownImageOwner.set(i, "own");
                    final Bitmap bitmap = loadImageUrl(generalImage[0].getOwnPopularImageList().get(ownCounter));
                    ownCounter++;
                    if (imageToPlayCounter < 4) {
                        imageToPlayCounter++;
                        Log.d("ownDownload", generalImage[0].getOwnPopularImageList().get(0));
                        generalImage[0].setOwnPopularBitmapList(Bitmap.createScaledBitmap(bitmap, (int) width, (int) (height / 2), true));
                        progress++;
                        publishProgress(progress + "");
                    }
                    generalImage[0].setOwnPreviewBitmapList(scaleBitmap(bitmap, width / 2.5f, width / 2.5f));
                    progress++;
                    publishProgress(progress + "");
                    if (bitmap != null){
                        bitmap.recycle();
                    }
                } else {
                    Log.d("bot_counter", botCounter + "");
                    generalImage[0].setBotPreviewBitmapList(null);
                    String currentBotImage = generalImage[0].getBotPopularImageList().get(botCounter);
                    downloadImageForBot(botCounter, imageToPlayCounter, currentBotImage, "ownBot");
                    botCounter++;
                    imageToPlayCounter++;
                }
            }

            for (int i = 0; i < 4; i++) {
                if (ownImageOwner.get(i).equals("own")) {
                    generalImage[0].getOwnPopularImageList().remove(0);
                } else {
                    generalImage[0].getBotPopularImageList().remove(0);
                }
            }

            String[] oppImageSeq = waitingRoom.getOppImageSeq().split(",");
            Collections.addAll(oppImageOwner, oppImageSeq);

            Log.d("IMAGEOWNER", oppImageOwner + "");

            Log.d("IMAGE", generalImage[0].getOppPopularImageList() + "");
            botCounter = 0;
            for (int i = 0; i < 4; i++) {
                if (oppImageOwner.get(i).equals("opp")) {
                    Bitmap oppBitmap = loadImageUrl(generalImage[0].getOppPopularImageList().get(0));
                    Log.d("oppDownload", generalImage[0].getOppPopularImageList().get(0));
                    generalImage[0].setOppPopularBitmapList(Bitmap.createScaledBitmap(oppBitmap, (int) oppPuzzleWidth, (int) oppPuzzleHeight, true));
                    generalImage[0].oppClearPopularImageList();
                    if (oppBitmap != null)
                        oppBitmap.recycle();
                } else {
                    generalImage[0].setOppBotPopularBitmapList(null);
                    downloadImageForBot(botCounter, 4, generalImage[0].getOppPopularImageList().get(0), "oppBot");
                    generalImage[0].oppClearPopularImageList();
                    botCounter++;
                }
            }

            state.setOppProfilePic("https://pbs.twimg.com/profile_images/552384688040329216/IrwkDF6V.png");
            state.setOppName("Online People");
            long endTime = System.currentTimeMillis();
            long totalTime = (endTime - startTime);
            Log.d("totalTime", totalTime + "");

            return null;
        }

        private Bitmap scaleBitmap(Bitmap bitmapToScale, float newWidth, float newHeight) {

            if (bitmapToScale == null) {
                Log.d(GoogleServiceApi.TAG, "nullscalebitmap: " + bitmapToScale.toString());
                return null;
            }

            Log.d(GoogleServiceApi.TAG, "bitmapToScale:" + bitmapToScale.toString());

            Log.d(GoogleServiceApi.TAG, "newwidth:" + newWidth);
            Log.d(GoogleServiceApi.TAG, "newheight:" + newHeight);

            return Bitmap.createScaledBitmap(bitmapToScale, (int) newWidth, (int) newHeight, true);
        }

        private Bitmap loadImageUrl(String imageUrl) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                Log.d("error", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String Void) {
            threadRunning = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (threadRunning) {
                        int ownCounter = 0, oppCounter = 0;
                        for (int i = 0; i < generalImage.getOwnPreviewBitmapList().size(); i++) {
                            if (generalImage.getOwnPreviewBitmapList().get(i) != null) {
                                ownCounter++;
                            }
                        }
                        for (int i = 0; i < generalImage.getBotPreviewBitmapList().size(); i++) {
                            if (generalImage.getBotPreviewBitmapList().get(i) != null) {
                                ownCounter++;
                            }
                        }
                        for (int i = 0; i < generalImage.getOppPopularBitmapList().size(); i++) {
                            if (generalImage.getOppPopularBitmapList().get(i) != null)
                                oppCounter++;
                        }
                        for (int i = 0; i < generalImage.getOppBotPopularBitmapList().size(); i++) {
                            if (generalImage.getOppBotPopularBitmapList().get(i) != null)
                                oppCounter++;
                        }
                        if (ownCounter == 8 && oppCounter == 4) {
                            threadRunning = false;
                        }
                    }

                    /*Intent startGame = new Intent(currActivity, NewPuzzleActivity.class);
                    startGame.putExtra("BotHere", true);
                    startGame.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    currActivity.startActivity(startGame);
                    waitingRoom.reset();
                    waitingRoom.restoreStartMatch();
                    singlePlayer = null;
                    generalImage = null;
                    waitingRoom = null;
                    System.gc();*/
                    waitingRoom.demo();
                }
            }).start();
        }
    }

    public void demo() {
        Intent startGame = new Intent(currActivity, NewPuzzleActivity.class);
        startGame.putExtra("BotHere", true);
        startGame.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        currActivity.startActivity(startGame);
        waitingRoom.reset();
        waitingRoom.restoreStartMatch();
        singlePlayer = null;
        generalImage = null;
        waitingRoom = null;
        System.gc();
    }

    private boolean isHost() {
        for (int i = 0; i < mParticipants.size(); i++) {
            if (mParticipants.get(i).getParticipantId().compareTo(mMyId) < 0) {
                return false;
            }
        }
        return true;
    }

    public String getRoomId() {
        return mRoomId;
    }


    public String getOppId() {
        return oppId;
    }

    private PendingResult<Snapshots.CommitSnapshotResult> writeSnapshot(Snapshot snapshot,
                                                                        byte[] data, Bitmap coverImage, String desc) {

        // Set the data payload for the snapshot
        snapshot.getSnapshotContents().writeBytes(data);

        // Create the change operation
        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setCoverImage(coverImage)
                .setDescription(desc)
                .build();

        // Commit the operation
        return Games.Snapshots.commitAndClose(mGoogleApiClient, snapshot, metadataChange);
    }

    public void savedGamesLoad(String snapshotName) {
        Log.d(GoogleServiceApi.TAG, "-----------------GoogleServiceApi:savedGamesLoad----------------");

        PendingResult<Snapshots.OpenSnapshotResult> pendingResult = Games.Snapshots.open(
                mGoogleApiClient, snapshotName, false);


        ResultCallback<Snapshots.OpenSnapshotResult> callback =
                new ResultCallback<Snapshots.OpenSnapshotResult>() {
                    @Override
                    public void onResult(Snapshots.OpenSnapshotResult openSnapshotResult) {
                        if (openSnapshotResult.getStatus().isSuccess()) {
                            byte[] data = new byte[0];
                            try {
                                data = openSnapshotResult.getSnapshot().getSnapshotContents().readFully();
                                String receiveMessage = new String(data);
                                Log.d(GoogleServiceApi.TAG, "LoadData:" + receiveMessage);
                            } catch (IOException e) {
                                Log.d(GoogleServiceApi.TAG, "Fail Read LoadData Snapshot");
                            }
                            //  menuActivity.setData(new String(data));
                            displaySnapshotMetadata(openSnapshotResult.getSnapshot().getMetadata());


                        } else {
                            Log.d(GoogleServiceApi.TAG, "openSnapshotResult.getStatus().isNotSuccess()");
                        }

                        //mListener.onStartIntent();

                        //  dismissProgressDialog();
                    }
                };
        pendingResult.setResultCallback(callback);
    }

    public void savedGamesLoad(String snapshotName, Intent login) {

    }

    public void savedGamesUpdate(String savedData) {

        final String snapshotName = makeSnapshotName(APP_STATE_KEY);
        final boolean createIfMissing = true;

        // Use the data from the EditText as the new Snapshot data.
        final byte[] data = savedData.getBytes();

        AsyncTask<Void, Void, Boolean> updateTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                showProgressDialog("Updating Saved Game");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Snapshots.OpenSnapshotResult open = Games.Snapshots.open(
                        mGoogleApiClient, snapshotName, createIfMissing).await();

                if (!open.getStatus().isSuccess()) {
                    Log.w(TAG, "Could not open Snapshot for update.");
                    return false;
                }

                // Change data but leave existing metadata
                Snapshot snapshot = open.getSnapshot();
                snapshot.getSnapshotContents().writeBytes(data);

                Snapshots.CommitSnapshotResult commit = Games.Snapshots.commitAndClose(
                        mGoogleApiClient, snapshot, SnapshotMetadataChange.EMPTY_CHANGE).await();

                if (!commit.getStatus().isSuccess()) {
                    Log.w(TAG, "Failed to commit Snapshot.");
                    return false;
                }

                // No failures
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    Log.d(GoogleServiceApi.TAG, "savegame done!");
                } else {
                    Log.d(GoogleServiceApi.TAG, "savegame fail!");
                }
                dismissProgressDialog();
            }
        };
        updateTask.execute();
    }

    private String makeSnapshotName(int appStateKey) {
        return "Snapshot-" + String.valueOf(appStateKey);
    }


    private void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(currActivity);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    private void displaySnapshotMetadata(SnapshotMetadata metadata) {


        String metadataStr = "Source: Saved Games" + '\n'
                + "Description: " + metadata.getDescription() + '\n'
                + "Name: " + metadata.getUniqueName() + '\n'
                + "Last Modified: " + String.valueOf(metadata.getLastModifiedTimestamp()) + '\n'
                + "Played Time: " + String.valueOf(metadata.getPlayedTime()) + '\n'
                + "Cover Image URL: " + metadata.getCoverImageUrl();


        Log.d(GoogleServiceApi.TAG, "metadata: " + metadataStr);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getSnapshotName() {
        return makeSnapshotName(APP_STATE_KEY);
    }

   /* public void setInitialLoadingListener(Listener l) {
        mListener = l;
    }

    public interface Listener {
        void onStartIntent();
    }*/

}
