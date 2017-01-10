package fusion.com.soicalrpgpuzzle;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import fusion.com.soicalrpgpuzzle.util.IabHelper;

/**
 * Created by Brian on 4/23/2016.
 */

public class WaitingRoom {

    private SharedPreferences sharedPref;
    GoogleServiceApi googleServiceApi;
    GeneralImage generalImage;
    float height, width;
    StartMatch currentActivity;
    GlobalState state;
    Runnable runnable;
    Handler handler;
    String imageOwner = null, googlePlayID, instaID;
    RelativeLayout statusLayout, startMatch, skillBackground;
    ImageView start_button;
    GetImage getImage;
    RoundCornerProgressBar time;
    String[] offenceSkill = {"None", "blackOut", "numberFloat", "flipAll", "box", "invisibleTouch", "touchRandom", "alpha", "flying"};
    String[] supportSkill = {"revealSolution"};


    public WaitingRoom(StartMatch curActivity, GlobalState globalState) {
        currentActivity = curActivity;
        state = globalState;
        handler = new Handler();

        String[] dimension = state.getDimensionScreen().split(",");
        height = Float.valueOf(dimension[1]);
        width = Float.valueOf(dimension[0]);
    }

    public void startFindingOpp(RelativeLayout statusLayout, ImageView start_button, RelativeLayout startMatch) {
        this.statusLayout = statusLayout;
        this.start_button = start_button;
        this.startMatch = startMatch;
        getImage = new GetImage();
        getImage.execute();
    }

    public void setPlayerFound() {
        statusLayout.removeAllViews();
        ImageView playerFound = new ImageView(currentActivity.getActivity());
        playerFound.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(currentActivity.getActivity(), R.drawable.player_found)).getBitmap(), (int) (width - width / 54), (int) height / 8, true));
        statusLayout.addView(playerFound);
        playerFound.setY(statusLayout.getHeight() / 2 - height / 16);
        playerFound.setX(statusLayout.getWidth() / 2 - (width - width / 54) / 2);
        start_button.setEnabled(false);
        start_button.animate().scaleX(0).scaleY(0).setInterpolator(new AnticipateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                start_button.setImageDrawable(null);
            }
        }).start();
    }

    public void setProgress() {
        statusLayout.removeAllViews();
        time = new RoundCornerProgressBar(currentActivity.getActivity(), null);
        time.setMax(16);
        time.setRadius((int) width / 50);
        time.setProgressColor(Color.parseColor("#FF00FF"));
        time.setPadding((int) height / 192);
        statusLayout.addView(time);
        ViewGroup.LayoutParams layoutLP = time.getLayoutParams();
        layoutLP.height = (int) ((height / 20));
        layoutLP.width = (int) (width - width / 5.4);
        time.setY(statusLayout.getHeight() - height / 9.6f);
        time.setX(width / 10.8f);
        statusLayout.animate().translationY(height - statusLayout.getHeight() - height / 12.8f).setDuration(250).withStartAction(new Runnable() {
            @Override
            public void run() {

            }
        }).setInterpolator(new AnticipateInterpolator()).start();
    }

    public void setSkill() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                skillBackground = new RelativeLayout(currentActivity.getActivity());
                startMatch.addView(skillBackground);
                ViewGroup.LayoutParams lp = skillBackground.getLayoutParams();
                lp.width = (int) (width - width / 10.8);
                lp.height = (int) (height / 2 + height / 6);
                skillBackground.setX(width / 21.6f);
                skillBackground.setY(-(height / 2 + height / 6));
                skillBackground.setBackgroundResource(R.drawable.result_backgound);

                TextView skillName = new TextView(currentActivity.getActivity());
                Typeface textFont = Typeface.createFromAsset(currentActivity.getActivity().getAssets(), "JELLYBELLY.ttf");
                skillName.setTypeface(textFont);
                skillName.setTextColor(Color.BLACK);
                skillName.setTextSize(50);
                skillName.setMinimumWidth((int) (width - width / 10.8));
                skillName.setGravity(Gravity.CENTER);
                skillName.setX(width / 21.6f);
                skillName.setY(height / 48);
                int randomSkillName = new Random().nextInt(offenceSkill.length);
                skillName.setText(offenceSkill[randomSkillName]);
                skillBackground.addView(skillName);

                state.setSkillName("", 100, false);

                skillBackground.animate().translationY(0).setDuration(500).start();
            }
        });
    }

    public void demo() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ScrollView scrollView = new ScrollView(currentActivity.getActivity());
                GridLayout gridLayout = new GridLayout(currentActivity.getActivity());
                gridLayout.setColumnCount(1);
                scrollView.addView(gridLayout);
                skillBackground.addView(scrollView);
                ViewGroup.LayoutParams lp = scrollView.getLayoutParams();
                lp.width = (int) (width - width / 10.8);
                lp.height = (int) (height / 2 + height / 12);
                scrollView.setY(height / 12.8f);

                for (int i = 0; i < offenceSkill.length; i++) {
                    Button button = new Button(currentActivity.getActivity());
                    button.setText(offenceSkill[i]);
                    button.setTag(offenceSkill[i]);
                    gridLayout.addView(button);
                    setClick(button);
                }

                for (int i = 0; i < supportSkill.length; i++) {
                    Button button = new Button(currentActivity.getActivity());
                    button.setText(supportSkill[i]);
                    button.setTag(supportSkill[i]);
                    gridLayout.addView(button);
                    setSupportClick(button);
                }
            }
        });
    }

    private void setClick(View button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.setSkillName(v.getTag().toString(), 100, false);
                googleServiceApi.demo();
            }
        });
    }

    private void setSupportClick(View button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.setSkillName(v.getTag().toString(), 100, true);
                googleServiceApi.demo();
            }
        });
    }

    public void restoreStartMatch() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                startMatch.removeView(skillBackground);
                currentActivity.restorePage();
            }
        });
    }

    public void reset() {
        MainMenu mainMenu = (MainMenu) currentActivity.getActivity();
        mainMenu.resetAllFragment();
    }

    public String getFireBaseId() {
        return sharedPref.getString("uid", null);
    }

    public String getInstagramId() {
        return instaID;
    }

    public String getGoogleId() {
        return googlePlayID;
    }

    public String getOppImageSeq() {
        return imageOwner;
    }

    public void cancelGame() {
        getImage.cancel(true);
        googleServiceApi.cancelGame();
        googleServiceApi = null;
        getImage = null;
    }

    private class GetImage extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            sharedPref = currentActivity.getActivity().getSharedPreferences(GoogleServiceApi.SHARED, Context.MODE_PRIVATE);
            int totalPictureToWin = 4;
            final ArrayList<SetOwnerShip> setOwnerShip = new ArrayList<>();

            if (state.getPlayer().getGooglePlayProfile() != null && state.getPlayer().getInstaProfilePic() != null) {
                InstagramSession session = new InstagramSession(currentActivity.getActivity());
                ArrayList<String> ownPicURL = session.getSelfImageList();
                Log.d("ownPicture", ownPicURL + "");
                for (int i = 0; i < ownPicURL.size(); i++) {
                    if (i > 8)
                        break;
                    setOwnerShip.add(new SetOwnerShip("opp", ownPicURL.get(new Random().nextInt(ownPicURL.size()))));
                }
            }

            if (setOwnerShip.size() != 8) {
                int remain = 8 - setOwnerShip.size();
                for (int i = 0; i < remain; i++) {
                    setOwnerShip.add(new SetOwnerShip("bot", new Random().nextInt(6) + ""));
                }
            }

            Collections.shuffle(setOwnerShip);

            generalImage = new GeneralImage();
            state.setGeneralImage(generalImage);

            Player player = state.getPlayer();
            if (state.getPlayer().getGooglePlayProfile() != null) {
                googlePlayID = player.getGooglePlayId();
            } else {
                InstagramSession session = new InstagramSession(currentActivity.getActivity());
                instaID = session.getId();
            }

            googleServiceApi = state.getmGoogleApi();

            googleServiceApi.setCurrActivity(currentActivity.getActivity());
            String[] gameMode = {"4x4"};
            Random random = new Random();

            for (int i = 0; i < totalPictureToWin * 2; i++) {
                state.storeGameMode(gameMode[random.nextInt(gameMode.length)]);
                generalImage.setOppPopularImageList(setOwnerShip.get(i).botURL);
                if (i == 0) {
                    imageOwner = setOwnerShip.get(i).owner;
                } else {
                    imageOwner = imageOwner + "," + setOwnerShip.get(i).owner;
                }
            }

            Log.d("Wait_OPPIMAGEOWNER", imageOwner);

            setOwnerShip.clear();
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if (new Random().nextInt(100) < 100) {
                googleServiceApi.startSinglePlayer(generalImage, WaitingRoom.this);
            } else {
                googleServiceApi.startQuickGame(generalImage, WaitingRoom.this);
            }
            super.onPostExecute(strings);
        }
    }

    private class SetOwnerShip {
        String owner = null;
        String botURL = null;

        public SetOwnerShip(String whoIsTheOwner, String botURL) {
            this.owner = whoIsTheOwner;
            this.botURL = botURL;
        }
    }
}
