package fusion.com.soicalrpgpuzzle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.PlayerBuffer;
import com.google.android.gms.games.Players;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectStreamException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Brian on 7/2/2016.
 */
public class Record extends Fragment implements ImageManager.OnImageLoadedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context context;
    DatabaseReference mDatabase;
    InstagramSession session;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    LinearLayout record_container;
    int curr_record_count;
    boolean firstAccess;
    GlobalState state;
    ArrayList<Bitmap> profilePicBitmapList = null;
    ArrayList<String> profileNameList = null;
    Record currActivity;
    ArrayList<String> historyList, historyInstaList;
    ArrayList<String> URIList;
    TextView rank, win_num, lose_num, condition_view;
    RelativeLayout record;
    float width, height;
    GridLayout gridLayout;

    public Record() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
// ...
        super.onCreate(savedInstanceState);

        context = this.getActivity();
        session = new InstagramSession(context);

        currActivity = this;

        curr_record_count = -1;
        firstAccess = true;

        state = ((GlobalState) this.getActivity().getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPref = context.getSharedPreferences(GoogleServiceApi.SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        profilePicBitmapList = new ArrayList<>();
        profileNameList = new ArrayList<>();
        historyInstaList = new ArrayList<>();

    }

    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        /*if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_record, container, false);

        String[] dimension = state.getDimensionScreen().split(",");
        height = Float.valueOf(dimension[1]);
        width = Float.valueOf(dimension[0]);

        record = (RelativeLayout) v.findViewById(R.id.record_relative);
        gridLayout = new GridLayout(getActivity());
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.matches_banner)).getBitmap(), (int) (width - width / 10.8), (int) (height / 9.6), true));
        record.addView(imageView);
        imageView.setX(width / 21.6f);
        return v;
    }

    public void resetRecord() {
        if (gridLayout.getChildCount() != 0) {
            Log.d("record_child", record.getChildCount() + "");
            URIList.clear();
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                RelativeLayout gridChild = ((RelativeLayout) gridLayout.getChildAt(i));
                for (int j = 0; j < gridChild.getChildCount(); j++) {
                    try {
                        //((BitmapDrawable) ((ImageView) gridChild.getChildAt(j)).getDrawable()).getBitmap().recycle();
                        ((ImageView) gridChild.getChildAt(j)).setImageDrawable(null);
                    } catch (Exception e) {

                    }
                }
                gridChild.removeAllViews();
                //((BitmapDrawable) gridChild.getBackground()).getBitmap().recycle();
                gridChild.setBackground(null);
            }
            gridLayout.removeAllViews();
            ScrollView scrollView = (ScrollView) gridLayout.getParent();
            scrollView.removeAllViews();
            record.removeView(scrollView);
            gridLayout = new GridLayout(getActivity());
            System.gc();
            Log.d("record_child", record.getChildCount() + "");
        }
    }

    public void updateData() {
        if (gridLayout.getChildCount() == 0)
            setLayout();
    }

    private void setLayout() {
        gridLayout = new GridLayout(getActivity());
        ScrollView scrollView = new ScrollView(getActivity());
        record.addView(scrollView);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setVerticalScrollBarEnabled(false);
        ViewGroup.LayoutParams lp = scrollView.getLayoutParams();
        lp.width = (int) width;
        lp.height = (int) (height - height / 5.5652);
        scrollView.setY(height / 9.6f);

        gridLayout.setColumnCount(1);
        scrollView.addView(gridLayout);
        try {
            if (state.getMatchDetails().getRank() != null) {
                Log.d("test", state.getMatchDetails().getRank().size() - 1 + "");
                for (int i = state.getMatchDetails().getRank().size() - 1; i >= 0; i--) {
                    if (i > state.getMatchDetails().getRank().size() - 11) {
                        setLayoutHistory(gridLayout, i);
                    }
                }
                getProfileByFireBaseId();
            }
        } catch (Exception e) {
            Log.d("Fail", "Fail");
        }
        fancyAnimation();
    }

    public void fancyAnimation() {
        Handler handler = new Handler();
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            gridLayout.getChildAt(i).setVisibility(View.INVISIBLE);
            final int curr = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gridLayout.getChildAt(curr).setVisibility(View.VISIBLE);
                    TranslateAnimation translateAnimation = new TranslateAnimation(width, 0, 0, 0);
                    translateAnimation.setDuration(250);
                    translateAnimation.setInterpolator(new OvershootInterpolator());
                    gridLayout.getChildAt(curr).startAnimation(translateAnimation);
                }
            }, 150 * (i + 1));
        }
    }

    private void setLayoutHistory(GridLayout gridLayout, int i) {
        RelativeLayout backGround = new RelativeLayout(getActivity());
        backGround.setVisibility(View.INVISIBLE);
        gridLayout.addView(backGround);
        ViewGroup.LayoutParams lp = backGround.getLayoutParams();
        lp.width = (int) (width);
        lp.height = (int) (height / 3.2 - height / 19.2);
        backGround.setBackgroundResource(R.drawable.record_back);
        setHistory(backGround, i);
    }

    private void setHistory(RelativeLayout backGround, int i) {

        ImageView status = new ImageView(getActivity());
        ImageView rank = new ImageView(getActivity());
        TextView date = new TextView(getActivity());
        TextView score = new TextView(getActivity());

        backGround.addView(status);
        setVictoryOrDefeat(status, i);
        backGround.addView(rank);
        backGround.addView(date);
        backGround.addView(score);
        setRankAndScore(score, rank, i);

        //backGround.addView(circle);

        setDate(date, i);
        //getOppImage(profile);
    }

    private void setRankAndScore(TextView score, ImageView rank, int i) {
        int[] rankDrawable = {R.drawable.ss_rank, R.drawable.s_rank, R.drawable.a_rank, R.drawable.b_rank, R.drawable.c_rank, R.drawable.d_rank};
        String[] color = {"SS", "S", "A", "B", "C", "D"};

        for (int j = 0; j < color.length; j++) {
            if (state.getMatchDetails().getRank().get(i).equals(color[j])) {
                rank.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), rankDrawable[j])).getBitmap(), (int) (height / 12.8), (int) (height / 12.8), true));
            }
        }
        rank.setX(width / 2 + (height / 12.8f) / 2);
        rank.setY(height / 38.4f + height / 12.8f + height / 19.2f);
        rank.setRotationY(-90);
        score.setRotationY(-90);
        rank.setVisibility(View.INVISIBLE);

        score.setX(width / 3 + width / 21.6f);
        score.setY(height / 38.4f + height / 12.8f + height / 19.2f);
        score.setMinimumWidth((int) width / 2);
        score.setGravity(Gravity.CENTER);
        Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "JELLYBELLY.ttf");
        score.setTypeface(textFont);
        score.setTextColor(Color.WHITE);
        score.setTextSize(40);
        score.setText(state.getMatchDetails().getScore().get(i).toString());
        animateScore(score, rank);
    }

    private void animateScore(final TextView score, final ImageView rank) {
        score.setVisibility(View.VISIBLE);
        score.animate().rotationYBy(90).setDuration(500).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                score.animate().rotationYBy(90).setDuration(500).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        score.setVisibility(View.INVISIBLE);
                        score.setRotationY(-90);
                        animateRank(score, rank);
                    }
                }).setStartDelay(250).start();
            }
        }).start();
    }

    private void animateRank(final TextView score, final ImageView rank) {
        rank.setVisibility(View.VISIBLE);
        rank.animate().rotationYBy(90).setDuration(500).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                rank.animate().rotationYBy(90).setDuration(500).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        rank.setVisibility(View.INVISIBLE);
                        rank.setRotationY(-90);
                        animateScore(score, rank);
                    }
                }).setStartDelay(250).start();
            }
        }).start();
    }

    private void setDate(TextView date, int i) {

        date.setMinimumWidth((int) width);
        date.setGravity(Gravity.CENTER);
        Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "JELLYBELLY.ttf");
        date.setTypeface(textFont);
        date.setTextColor(Color.WHITE);
        date.setTextSize(30);
        date.setText(state.getMatchDetails().getDate().get(i));
        date.setY(height / 76.8f);
    }

    private void setOppImage(final ImageView profile, final Bitmap profilePicture) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                profile.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(profilePicture, (int) (width / 3.8571), (int) (width / 3.8571), true), 0));
                profile.setPadding((int) height / 192, (int) height / 192, (int) height / 192, (int) height / 192);
                profile.setY(height / 38.4f + height / 96 + height / 24);
                profile.setX(width / 21.6f + height / 96);
            }
        });
    }

    private void setVictoryOrDefeat(ImageView status, int i) {
        if (state.getMatchDetails().getCondition().get(i) == 1) {
            status.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.victory)).getBitmap(), (int) (width / 2), (int) (height / 12.8), true));
        } else {
            status.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.defeat)).getBitmap(), (int) (width / 2), (int) (height / 12.8), true));
        }
        status.setX(width / 2 - width / 13.5f);
        status.setY(height / 38.4f + height / 19.2f);
    }

    private void setCircleColor(ImageView circle, String colorPlayer) {
        String[] color = {"Red", "Green", "Blue", "Yellow"};
        int[] drawable = {R.drawable.red_circle, R.drawable.green_circle, R.drawable.blue_circle, R.drawable.yellow_circle};
        float profileHeight = height / 3.2f;
        for (int i = 0; i < color.length; i++) {
            if (colorPlayer.equals(color[i]))
                circle.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), drawable[i])).getBitmap(), (int) (profileHeight / 1.9 + height / 96), (int) (profileHeight / 1.9 + height / 96), true));
        }
        circle.setY(height / 38.4f + height / 24);
        circle.setX(width / 21.6f);
        ObjectAnimator animator = ObjectAnimator.ofFloat(circle, "rotation", 0, 360);
        animator.setDuration(new Random().nextInt(500) + 500).setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        animator.start();
    }

    protected void callHistory() {

        /*if (state.getMatchDetails() == null) {
            if (firstAccess) {
                firstAccess = false;
                ImageView noHistoryLogo = new ImageView(this.getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                params.setMargins(0, 60, 0, 0);
                noHistoryLogo.setLayoutParams(params);
                noHistoryLogo.setImageResource(R.drawable.nohistorylogo);
                record_container.addView(noHistoryLogo);
            }

        } else {
            Log.d(GoogleServiceApi.TAG, "history have something!");
            historyList = (ArrayList<String>) state.getMatchDetails().getHistory();
            if (curr_record_count == -1) {
                getInstaIdFromFirebase(historyList);
                curr_record_count = historyList.size();
            } else {
               *//* if (curr_record_count != instaList.size()) {
                   // displayHistoryRecords();
                  // curr_record_count = historyList.size(historyList);
                }*//*
            }
        }*/
    }

    /*private void getMatchDetails(final ArrayList<String> historyList) {
        mDatabase =  FirebaseDatabase.getInstance().getReference();

        mDatabase.child("MatchDetails").child(sharedPref.getString("uid", null)).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ko = dataSnapshot.getValue(MatchDetails.class).getKO();
                maxCombo = dataSnapshot.getValue(MatchDetails.class).getMaxCombo();
                score = dataSnapshot.getValue(MatchDetails.class).getScore();
                condition = dataSnapshot.getValue(MatchDetails.class).getCondition();

                getInstaIdFromFirebase(historyList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/


    private void getInstaIdFromFirebase(final ArrayList<String> historyList) {

        Log.d(GoogleServiceApi.TAG, "displayHistoryRecords");


        for (int i = 0; i < historyList.size(); i++) {
            mDatabase.child("InstaId").child(historyList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(GoogleServiceApi.TAG, "iterate:" + dataSnapshot.getValue());
                    // infoList.add(session.getProfileNameByInstaId((String) dataSnapshot.getValue()).get(0));

                    historyInstaList.add(dataSnapshot.getValue().toString());

                    if (historyInstaList.size() == historyList.size()) {
                        new loadInsta().execute();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }


        // profilePic.setImage
        // Log.d(GoogleServiceApi.TAG, "record:" + state.getMatchDetails().getKO().get(0));


    }

    private Bitmap getCircleBitmap(Bitmap bitmap, int borderWidth) {

        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        final int width = bitmap.getWidth() + borderWidth;
        final int height = bitmap.getHeight() + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }

    private void getProfileByFireBaseId() {
        URIList = new ArrayList<>();
        curr_record_count = 0;
        final int matchDetailsSize = state.getMatchDetails().getOppFireBaseId().size();
        for (int i = 0; i < state.getMatchDetails().getOppFireBaseId().size(); i++) {
            Log.d(GoogleServiceApi.TAG, "check: " + state.getMatchDetails().getOppFireBaseId().get(i));
        }

        if (matchDetailsSize == 0) {
            Log.d("no_match", "no_match");
            // Ui for no match details available
        } else {
            for (int i = matchDetailsSize - 1; i >= 0; i--) {
                if (curr_record_count == 10)
                    return;
                final int curr = curr_record_count;
                curr_record_count++;
                mDatabase.child("Player").child(state.getMatchDetails().getOppFireBaseId().get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Player player = dataSnapshot.getValue(Player.class);

                            if (player.getInstaId() != null) {

                                // Instagram

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ImageView profile = new ImageView(getActivity());
                                        ((RelativeLayout) gridLayout.getChildAt(curr)).addView(profile);
                                        InstagramSession session = new InstagramSession(currActivity.getContext());
                                        String profilePicStr = session.getProfilePicByInstaId(player.getInstaId());

                                        Log.d(GoogleServiceApi.TAG, "recordInstaStr: " + profilePicStr);
                                        Bitmap instaProfilePic = loadImageUrl(profilePicStr);

                                        updateProfilePic(instaProfilePic, null, profile);
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

                                }).start();
                                ImageView circle = new ImageView(getActivity());
                                ((RelativeLayout) gridLayout.getChildAt(curr)).addView(circle);
                                setCircleColor(circle, player.getColour());

                            } else {
                                // GoogleService

                                ImageView circle = new ImageView(getActivity());
                                ((RelativeLayout) gridLayout.getChildAt(curr)).addView(circle);
                                setCircleColor(circle, player.getColour());

                                Games.Players.loadPlayer(state.getmGoogleApi().getmGoogleApiClient(), player.getGooglePlayId()).setResultCallback(new ResultCallback<Players.LoadPlayersResult>() {
                                    @Override
                                    public void onResult(Players.LoadPlayersResult loadPlayersResult) {
                                        if (loadPlayersResult != null) {
                                            if (loadPlayersResult.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK) {
                                                if (loadPlayersResult.getPlayers() != null) {
                                                    PlayerBuffer playerBuffer = loadPlayersResult.getPlayers();

                                                    ImageView profile = new ImageView(getActivity());
                                                    ((RelativeLayout) gridLayout.getChildAt(curr)).addView(profile);
                                                    for (int i = 0; i < playerBuffer.getCount(); ++i) {
                                                        Uri googlePlayProfilePic = playerBuffer.get(i).getHiResImageUri();
                                                        URIList.add(googlePlayProfilePic + "");
                                                        updateProfilePic(null, googlePlayProfilePic, profile);
                                                    }
                                                    playerBuffer.close();
                                                }
                                            } else {
                                                // Set Name & ?? profile picture here
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Cancel", "Cancel");
                    }
                });
            }
        }
    }

    // Update profile Pic Ui here!
    private void updateProfilePic(Bitmap instaProfilePic, Uri googlePlayProfile, ImageView profile) {
        if (instaProfilePic != null) {
            setOppImage(profile, instaProfilePic);
        } else {
            ImageManager mgr = ImageManager.create(currActivity.getContext());
            mgr.loadImage(Record.this, googlePlayProfile);
        }
    }

    @Override
    public void onImageLoaded(Uri uri, Drawable drawable, boolean b) {
        BitmapDrawable profilePicDrawable = (BitmapDrawable) drawable;
        Log.d("URI2", uri + "");
        if (profilePicDrawable != null) {
            for (int i = 0; i < URIList.size(); i++) {
                if (URIList.get(i).equals(String.valueOf(uri))) {
                    ImageView profile = new ImageView(getActivity());
                    ((RelativeLayout) gridLayout.getChildAt(i)).addView(profile);
                    setOppImage(profile, profilePicDrawable.getBitmap());
                }
            }
            profilePicDrawable = null;
        }
    }

    private class loadInsta extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < historyInstaList.size(); i++) {
                ArrayList<String> infoIdList = session.getProfileNameByInstaId(historyInstaList.get(i));
                profilePicBitmapList.add(loadImageUrl(infoIdList.get(0)));
                profileNameList.add(infoIdList.get(1));
            }
            return null;
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
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);

            /*if (state.getMatchDetails().getHistory().size() == profilePicBitmapList.size()) {
                for (int i = 0; i < profilePicBitmapList.size(); i++) {
                    setTabLayout(i);
                }
            }*/
        }

        private void setTabLayout(final int counter) {

            mDatabase.child("Player").child(historyList.get(counter)).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    LayoutInflater inflater = LayoutInflater.from(context);
                    RelativeLayout tabLayout = (RelativeLayout) inflater.inflate(R.layout.record_tab, null, false);

                    Log.d(GoogleServiceApi.TAG, "CHECK: " + dataSnapshot.getValue(Player.class).rank);
                    de.hdodenhof.circleimageview.CircleImageView profilePic = (CircleImageView) tabLayout.findViewById(R.id.profile_pic);
                    rank = (TextView) tabLayout.findViewById(R.id.rank);
                    win_num = (TextView) tabLayout.findViewById(R.id.win_num);
                    lose_num = (TextView) tabLayout.findViewById(R.id.lose_num);
                    condition_view = (TextView) tabLayout.findViewById(R.id.condition);

                    rank.setText(dataSnapshot.getValue(Player.class).rank);
                    win_num.setText("Win: " + dataSnapshot.getValue(Player.class).win);
                    lose_num.setText("Lose: " + dataSnapshot.getValue(Player.class).lose);

                    profilePic.setTag("profile_" + counter);
                    TextView profile_name = (TextView) tabLayout.findViewById(R.id.profile_name);
                    //  ImageView condition = (ImageView) tabLayout.findViewById(R.id.condition);

                    profilePic.setImageBitmap(profilePicBitmapList.get(counter));
                    profile_name.setText(profileNameList.get(counter));

                    if (state.getMatchDetails().getCondition().get(counter) == 1) {
                        condition_view.setText("Win!");
                    } else {
                        condition_view.setText("Lose!");
                    }

                    //  profilePic.setOnTouchListener(this);

                    record_container.addView(tabLayout);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

               /* @Override
                public boolean onTouch(View v, MotionEvent event) {
                    String view_tag = v.getTag().toString();
                    String[] view_tag_counter = view_tag.split("_");

                    FragmentManager ft = getActivity().getFragmentManager();

                    ProfileDialogFragment dialog = new ProfileDialogFragment(currActivity, mDatabase, historyInstaList.get(Integer.parseInt(view_tag_counter[1])), historyList.get(Integer.parseInt(view_tag_counter[1])), profilePicBitmapList.get(Integer.parseInt(view_tag_counter[1])), profileNameList.get(Integer.parseInt(view_tag_counter[1])));
                    //     ProfileDialogFragment.getDialog().setCanceledOnTouchOutside(true);
                    dialog.show(ft, "profileFrag");
                    return false;
                }*/

            });
        }


    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        record.removeAllViews();
    }
}
