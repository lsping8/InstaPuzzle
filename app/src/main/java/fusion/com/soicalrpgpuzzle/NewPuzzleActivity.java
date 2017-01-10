package fusion.com.soicalrpgpuzzle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.FlipHorizontalToAnimation;
import com.easyandroidanimations.library.HighlightAnimation;
import com.easyandroidanimations.library.PuffInAnimation;
import com.easyandroidanimations.library.ScaleInAnimation;
import com.easyandroidanimations.library.SlideInAnimation;
import com.github.lzyzsd.circleprogress.ArcProgress;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.plattysoft.leonids.ParticleSystem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import im.delight.android.audio.SoundManager;


public class NewPuzzleActivity extends Activity implements RoomUpdateListener, ImageManager.OnImageLoadedListener {

    int chunkNumbers, delayApply, howManyRows, howManyPerRow, colorSelected, ownCurrPicCounter = 0, oppCurrPicCounter = 0, completePictureCount, oppCompletePictureCount;
    int oppRowNum = -1, highestCombo = 0;
    Context context;
    Bitmap ownProfilePicture = null, oppProfilePicture = null;
    ArrayList<Integer> ownRows, oppTemp, temp = new ArrayList<>();
    int checker = 0, backGroundCounter = 0, ownGiveUpCounter;
    int id = -1;
    int image_counter = 0, ownInverstNum;
    int comboCount;
    float height, width, stageSkillPercentage;
    String currProgress, stageSkill, gameMODE, hitOrNoHit;
    LinearLayout row;
    private SoundManager mSoundManager;
    PuzzleCreate create;
    SuperPower power;
    boolean isBot = false;
    boolean readyState = false;
    boolean firstTime = true;
    boolean pictureCompleted = false;
    boolean swapActivate = false;
    boolean threadRunning = true;
    boolean supportSkill = false;
    boolean allowBack = false;
    boolean comboStart = false;
    ArrayList<Bitmap> ownChunkedImages, oppChunkedImages;
    ArrayList<String> ownImageSeq, oppImageSeq, timeForChallenger;
    int oppRowCount = 0, tokenCount;
    HashMap<String, ArrayList<Integer>> ownRandomPick, oppHashMap;
    LinearLayout own_puzzle;
    RelativeLayout relativeLayout, opp_puzzle, blackScreen, powerLayout, color_row;
    GoogleServiceApi googleServiceApi;
    GlobalState state;
    Handler handler = new Handler();
    GeneralImage imageObj;
    Thread ownThread = null, oppThread = null;
    Bot bot;
    long startTime;
    ObjectAnimator timer;

    private GoogleApiClient client;

    public NewPuzzleActivity() {
        ownImageSeq = new ArrayList<>();
        oppImageSeq = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_new_puzzle);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Intent intent = getIntent();
        isBot = intent.getBooleanExtra("BotHere", false);

        if (!isBot) {
            Intent intent2 = getIntent();
            supportSkill = intent2.getBooleanExtra("supportSkill", false);
        }

        RelativeLayout backGround = (RelativeLayout) findViewById(R.id.backGround);
        backGround.setBackgroundResource(R.drawable.color_background3);
        own_puzzle = (LinearLayout) NewPuzzleActivity.this.findViewById(R.id.drag_row);
        opp_puzzle = (RelativeLayout) NewPuzzleActivity.this.findViewById(R.id.opponent_screen);
        relativeLayout = (RelativeLayout) findViewById(R.id.puzzle_activity);
        blackScreen = (RelativeLayout) findViewById(R.id.gesture);
        powerLayout = (RelativeLayout) findViewById(R.id.bottom_screen);
        color_row = (RelativeLayout) findViewById(R.id.color_row);
        state = ((GlobalState) getApplicationContext());
        imageObj = state.getGeneralImage();

        googleServiceApi = state.getmGoogleApi();
        googleServiceApi.setPuzzleActivity(NewPuzzleActivity.this);
        ownImageSeq = googleServiceApi.getOwnImageOwner();
        oppImageSeq = googleServiceApi.getOppImageOwner();

        context = this.getBaseContext();
        create = new PuzzleCreate();

        fusion.com.soicalrpgpuzzle.MusicManager.getInstance().play(NewPuzzleActivity.this, R.raw.epicbattle);

        String[] dimension = state.getDimensionScreen().split(",");
        height = Float.valueOf(dimension[1]);
        width = Float.valueOf(dimension[0]);

        final ImageView imageView = new ImageView(NewPuzzleActivity.this.getApplicationContext());
        RelativeLayout.LayoutParams imageLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageLP.setMargins(0, (int) height / 400, (int) width / 200, 0);
        imageView.setLayoutParams(imageLP);
        imageView.setId(R.id.selection);
        relativeLayout.addView(imageView);

        ViewGroup.LayoutParams lp = own_puzzle.getLayoutParams();
        lp.height = (int) (height / 2);
        lp = powerLayout.getLayoutParams();
        lp.height = (int) (height / 2);
        lp = blackScreen.getLayoutParams();
        lp.height = (int) (height / 2);
        blackScreen.setAlpha(0);

        createColorRow();
        opp_puzzle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    opp_puzzle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    opp_puzzle.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                if (!isBot) {
                    threadRunning = true;
                    ownThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String sendCompleteCreate = "onCreateComplete:" + "";
                            byte[] sendCompleteCreateString = sendCompleteCreate.getBytes();
                            Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendCompleteCreateString,
                                    googleServiceApi.getRoomId(), googleServiceApi.getOppId());
                            while (threadRunning) {
                                if (state.getStartGame() != null) {
                                    if (state.getStartGame().equals("StartGame") && state.getOppProfilePic() != null && state.getOppName() != null) {
                                        threadRunning = false;
                                        state.setStartGame(null);

                                        if (state.getPlayer().getGooglePlayProfile() != null) {
                                            ownProfilePicture = state.getPlayer().getGooglePlayProfile();
                                        } else {
                                            ownProfilePicture = state.getPlayer().getInstaProfilePic();
                                        }
                                        if (state.getOppProfilePic().substring(0, 5).equals("https")) {
                                            Log.d("http", "http");
                                            oppProfilePicture = loadProfilePictureUrl(state.getOppProfilePic());
                                        } else {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loadOppGoogleImage();
                                                }
                                            });
                                        }
                                        threadRunning = true;
                                        while (threadRunning) {
                                            if (oppProfilePicture != null && ownProfilePicture != null) {
                                                threadRunning = false;
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        previewStage();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                    ownThread.start();
                } else {
                    if (state.getPlayer().getGooglePlayProfile() != null) {
                        ownProfilePicture = state.getPlayer().getGooglePlayProfile();
                    } else {
                        ownProfilePicture = state.getPlayer().getInstaProfilePic();
                    }

                    ownThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            oppProfilePicture = loadProfilePictureUrl(state.getOppProfilePic());
                            while (threadRunning) {
                                if (oppProfilePicture != null && ownProfilePicture != null) {
                                    threadRunning = false;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            previewStage();
                                        }
                                    });
                                }
                            }
                        }
                    });
                    ownThread.start();
                }
            }
        });
    }

    private void loadOppGoogleImage() {
        ImageManager mgr = ImageManager.create(context);
        mgr.loadImage(NewPuzzleActivity.this, googleServiceApi.getOppGoogleURI());
    }

    @Override
    public void onImageLoaded(Uri uri, Drawable drawable, boolean b) {
        BitmapDrawable profilePicDrawable = (BitmapDrawable) drawable;
        Log.d("onImage", profilePicDrawable + "");
        if (profilePicDrawable != null) {
            Log.d("profile", "profile");
            oppProfilePicture = profilePicDrawable.getBitmap();
        }
    }

    private void previewStage() {
        Log.d("SKILLNAME", "SKILLNAME");
        getSkillname();
        customSetup();

        final ImageView versus = new ImageView(context);
        Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.versus)).getBitmap();
        versus.setImageBitmap(create.getCircleBitmap(Bitmap.createScaledBitmap(bitmap, (int) width / 6, (int) width / 6, true), 0));
        relativeLayout.addView(versus);
        versus.setX(width / 2 - width / 12);
        versus.setY(-width / 6);

        final LinearLayout ownProfileLayout = new LinearLayout(context);
        ownProfileLayout.setOrientation(LinearLayout.VERTICAL);
        final ImageView ownProfile = new ImageView(context);
        TextView ownName = new TextView(context);
        ownProfileLayout.addView(ownProfile);
        ownProfileLayout.addView(ownName);
        ownProfile.setImageBitmap(create.getCircleBitmap(Bitmap.createScaledBitmap(ownProfilePicture, (int) width / 3, (int) width / 3, true), (int) width / 216));
        ownName.setText(state.getPlayer().getName());
        ownName.setTextColor(Color.BLACK);
        ownName.setTypeface(null, Typeface.BOLD);
        ownName.setTextSize(25);
        ownName.setSingleLine(true);
        ownName.setEllipsize(TextUtils.TruncateAt.END);
        ownName.setMinimumWidth((int) width);
        ownName.setGravity(Gravity.CENTER_HORIZONTAL);
        relativeLayout.addView(ownProfileLayout);
        ownProfileLayout.setX(-width);
        ownProfileLayout.setY((height / 2) + (height / 4) - width / 6);

        final LinearLayout oppProfileLayout = new LinearLayout(context);
        oppProfileLayout.setOrientation(LinearLayout.VERTICAL);
        final ImageView oppProfile = new ImageView(context);
        TextView oppName = new TextView(context);
        oppProfileLayout.addView(oppProfile);
        oppProfileLayout.addView(oppName);
        oppProfile.setImageBitmap(create.getCircleBitmap(Bitmap.createScaledBitmap(oppProfilePicture, (int) width / 3, (int) width / 3, true), (int) width / 216));
        oppName.setText(state.getOppName());
        oppName.setTextColor(Color.BLACK);
        oppName.setTypeface(null, Typeface.BOLD);
        oppName.setTextSize(25);
        oppName.setSingleLine(true);
        oppName.setEllipsize(TextUtils.TruncateAt.END);
        oppName.setMinimumWidth((int) width);
        oppName.setGravity(Gravity.CENTER_HORIZONTAL);
        relativeLayout.addView(oppProfileLayout);
        oppProfileLayout.setX(width);
        oppProfileLayout.setY((height / 4) - width / 6);

        ObjectAnimator anime1 = ObjectAnimator.ofFloat(ownProfileLayout, "x", ownProfileLayout.getX(), 0);
        ObjectAnimator anime2 = ObjectAnimator.ofFloat(oppProfileLayout, "x", oppProfileLayout.getX(), 0);
        ObjectAnimator anime3 = ObjectAnimator.ofFloat(versus, "y", versus.getY(), (height / 2) - (width / 12));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(anime1).with(anime2).after(anime3);
        animatorSet.setDuration(1000);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator anime1 = ObjectAnimator.ofFloat(ownProfileLayout, "x", ownProfileLayout.getX(), width);
                ObjectAnimator anime2 = ObjectAnimator.ofFloat(oppProfileLayout, "x", oppProfileLayout.getX(), -width);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(anime1).with(anime2);
                animatorSet.setStartDelay(300);
                animatorSet.setDuration(700);
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        versus.animate().rotationBy(360).scaleX(0).scaleY(0).setDuration(700).start();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ((BitmapDrawable) versus.getDrawable()).getBitmap().recycle();
                        versus.setImageDrawable(null);
                        ((BitmapDrawable) ownProfile.getDrawable()).getBitmap().recycle();
                        ownProfile.setImageDrawable(null);
                        ((BitmapDrawable) oppProfile.getDrawable()).getBitmap().recycle();
                        oppProfile.setImageDrawable(null);
                        ownProfileLayout.removeAllViews();
                        oppProfileLayout.removeAllViews();
                        relativeLayout.removeView(ownProfileLayout);
                        relativeLayout.removeView(oppProfileLayout);
                        relativeLayout.removeView(versus);
                        showPreview();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animatorSet.start();
            }
        });
    }

    private void showPreview() {
        final int size = (int) width / 216;
        final RoundCornerProgressBar time = new RoundCornerProgressBar(context, null);
        time.setRadius((int) width / 50);
        time.setProgressColor(Color.parseColor("#FF00FF"));
        time.setPadding((int) height / 192);
        time.setY(height / 2);

        final RelativeLayout textRelative = new RelativeLayout(context);
        final GridLayout previewPic = new GridLayout(context);
        relativeLayout.addView(time);
        relativeLayout.addView(previewPic);
        relativeLayout.addView(textRelative);
        previewPic.setColumnCount(2);

        ViewGroup.LayoutParams layoutLP = previewPic.getLayoutParams();
        layoutLP.width = (int) (width / 1.25f);
        previewPic.setY(height / 20);
        previewPic.setX(width / 2 - (width / 1.25f) / 2);

        layoutLP = time.getLayoutParams();
        layoutLP.height = (int) ((height / 20));
        layoutLP.width = (int) width;
        time.setY(0);

        layoutLP = textRelative.getLayoutParams();
        layoutLP.height = (int) height;
        layoutLP.width = (int) width;

        int ownImage = 0, botImage = 0;
        for (int i = 0; i < ownImageSeq.size(); i++) {
            if (ownImageSeq.get(i).equals("own")) {
                final ImageView imageView = new ImageView(context);
                imageView.setPadding(size, size, size, size);
                imageView.setImageBitmap(create.roundEdge(imageObj.getOwnPreviewBitmapList().get(ownImage)));
                previewPic.addView(imageView);
                imageView.setVisibility(View.INVISIBLE);
                imageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setVisibility(View.VISIBLE);
                        new ScaleInAnimation(imageView).setInterpolator(new OvershootInterpolator()).animate();
                    }
                }, 150 * i);
                ownImage++;
            } else {
                final ImageView imageView = new ImageView(context);
                imageView.setPadding(size, size, size, size);
                imageView.setImageBitmap(create.roundEdge(imageObj.getBotPreviewBitmapList().get(botImage)));
                previewPic.addView(imageView);
                imageView.setVisibility(View.INVISIBLE);
                imageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setVisibility(View.VISIBLE);
                        new ScaleInAnimation(imageView).setInterpolator(new OvershootInterpolator()).animate();
                    }
                }, 150 * i);
                botImage++;
            }
        }

        previewPic.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    previewPic.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    previewPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);


                int ownPictureCounter = 0, rowCounter = 0;
                int[] drawable = {R.drawable.count_1, R.drawable.count_2, R.drawable.count_3, R.drawable.count_4, R.drawable.count_5, R.drawable.count_6
                        , R.drawable.count_7, R.drawable.count_8};
                for (int i = 0; i < 8; i++) {
                    if (ownImageSeq.get(i).equals("own")) {
                        final ImageView imageView = new ImageView(context);
                        imageView.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, drawable[i])).getBitmap(), (int) (width / 10.8), (int) (width / 10.8), true));
                        textRelative.addView(imageView);
                        if (i % 2 != 0 || i == 0)
                            imageView.setY(previewPic.getY() + ((width / 2.5f + size) * rowCounter));
                        else {
                            rowCounter++;
                            imageView.setY(previewPic.getY() + ((width / 2.5f + size) * rowCounter));
                        }
                        imageView.setX(previewPic.getChildAt(ownPictureCounter).getX() + width / 2 - (width / 1.25f) / 2);
                        ownPictureCounter++;
                        imageView.setVisibility(View.INVISIBLE);
                        imageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setVisibility(View.VISIBLE);
                                new PuffInAnimation(imageView).animate();
                            }
                        }, 150 * i);
                    } else {
                        final ImageView imageView = new ImageView(context);
                        imageView.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, drawable[i])).getBitmap(), (int) (width / 10.8), (int) (width / 10.8), true));
                        textRelative.addView(imageView);
                        if (i % 2 != 0 || i == 0)
                            imageView.setY(previewPic.getY() + ((width / 2.5f + size) * rowCounter));
                        else {
                            rowCounter++;
                            imageView.setY(previewPic.getY() + ((width / 2.5f + size) * rowCounter));
                        }
                        imageView.setX(previewPic.getChildAt(ownPictureCounter).getX() + width / 2 - (width / 1.25f) / 2);
                        ownPictureCounter++;
                        imageView.setVisibility(View.INVISIBLE);
                        imageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setVisibility(View.VISIBLE);
                                new PuffInAnimation(imageView).animate();
                            }
                        }, 150 * i);
                    }
                }
            }
        });

        ObjectAnimator animeTime = ObjectAnimator.ofFloat(time, "progress", 0, 100);
        animeTime.setDuration(1050);
        animeTime.start();
        animeTime.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator animeTime = ObjectAnimator.ofFloat(time, "progress", time.getProgress(), 0);
                animeTime.setDuration(5000);
                animeTime.start();
                animeTime.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ObjectAnimator remove1 = ObjectAnimator.ofFloat(previewPic, "y", previewPic.getY(), height);
                        ObjectAnimator remove3 = ObjectAnimator.ofFloat(time, "y", time.getY(), -time.getHeight());
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.play(remove1).with(remove3);
                        animatorSet.setDuration(500);
                        animatorSet.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                textRelative.removeAllViews();
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                for (int i = 0; i < previewPic.getChildCount(); i++) {
                                    ((BitmapDrawable) ((ImageView) previewPic.getChildAt(i)).getDrawable()).getBitmap().recycle();
                                    ((ImageView) previewPic.getChildAt(i)).setImageDrawable(null);
                                }
                                previewPic.removeAllViews();
                                relativeLayout.removeView(previewPic);
                                relativeLayout.removeView(time);
                                imageObj.clearPreviewBitmap();
                                relativeLayout.removeView(textRelative);
                                startEverything();
                                System.gc();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        animatorSet.start();
                    }
                });
            }
        });
    }

    private void startEverything() {
        generateSequenceHashMap();
        own_puzzle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                nextClass();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    own_puzzle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    own_puzzle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private Bitmap loadProfilePictureUrl(String imageUrl) {
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

    public void getSkillname() {
        Log.d("SKILLNAME", state.getSkillName() + "");
        String[] getSkill = state.getSkillName().split(",");
        stageSkill = getSkill[0];
        stageSkillPercentage = Float.valueOf(getSkill[1]);
        supportSkill = state.getSupportSkill();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.gc();
        //bot.botStart();
        int maxSimultaneousStreams = 3;
        /*mSoundManager = new SoundManager(this, maxSimultaneousStreams);
        mSoundManager.start();
        mSoundManager.load(R.raw.pickup1);
        mSoundManager.load(R.raw.drop1);*/
        fusion.com.soicalrpgpuzzle.MusicManager.getInstance().resumeMusic();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSoundManager != null) {
            mSoundManager.cancel();
            mSoundManager = null;
        }
        fusion.com.soicalrpgpuzzle.MusicManager.getInstance().pauseMusic();
    }

    /*private void playSound1() {
        if (mSoundManager != null) {
            mSoundManager.play(R.raw.pickup1);
        }
    }

    private void playSound2() {
        if (mSoundManager != null) {
            mSoundManager.play(R.raw.drop1);
        }
    }

    private void playSound3() {
        if (mSoundManager != null) {
            mSoundManager.play(R.raw.button_click);
        }
    }*/

    private void nextClass() {
        new PuzzleOperation().execute(state.getGeneralImage());
        if (isBot) {
            bot = new Bot(NewPuzzleActivity.this, state, imageObj);
        }
    }

    private void createColorRow() {
        ViewGroup.LayoutParams colorLP = color_row.getLayoutParams();
        int colorRowHeight = (int) ((height / 2) / 7);
        colorLP.height = colorRowHeight;
        color_row.setX(-width);
        createOppScreen(colorRowHeight);
    }

    private void createOppScreen(int colorRowHeight) {
        int oppWidth = (int) (width / 1.5f);
        int oppHeight = (int) (height / 2.66f);
        Drawable backgroundPicture = ContextCompat.getDrawable(context, R.drawable.opponent_back);
        opp_puzzle.setBackground(backgroundPicture);
        ViewGroup.LayoutParams params = opp_puzzle.getLayoutParams();
        params.width = oppWidth;
        params.height = oppHeight;
        opp_puzzle.setLayoutParams(params);
        opp_puzzle.setX(width / 10);
        opp_puzzle.setY(colorRowHeight / 4);
        opp_puzzle.setVisibility(View.INVISIBLE);
        createStatusLayout(oppWidth, colorRowHeight);
    }

    private void createStatusLayout(int oppWidth, int colorRowHeight) {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.status);
        ViewGroup.LayoutParams lp = relativeLayout.getLayoutParams();
        lp.height = (int) height / 2 - colorRowHeight;
        lp.width = (int) (width - width / 10 - oppWidth);
        relativeLayout.setX(width / 10 + oppWidth);
    }

    private void clearStatusLayout() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.status);
        relativeLayout.removeAllViews();
    }

    public void oppGenerateHashMap(int oppChunkNumber, final int columnNum) {
        oppThread = new Thread(new Runnable() {
            @Override
            public void run() {
                create.oppSplitImage(columnNum, columnNum);
            }
        });
        oppThread.start();
        oppTemp = new ArrayList<>();
        oppHashMap = new HashMap<>();
        int rowNum = 0;
        for (int i = 0; i < oppChunkNumber; i++) {
            oppTemp.add(i);
            if (oppTemp.size() == columnNum) {
                oppHashMap.put("oppHash" + rowNum, oppTemp);
                rowNum++;
                oppTemp = new ArrayList<>();
            }
        }

        try {
            oppThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createImageOpp(int currentRow) {
        try {
            int check;
            // Set opponenet puzzle linear layout
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);
            ll.setTag("oppRows_" + currentRow);

            do {
                check = 0;
                Collections.shuffle(oppHashMap.get("oppHash" + currentRow));
                for (int j = 0; j < oppHashMap.get("oppHash" + currentRow).size() - 1; j++) {

                    if (oppHashMap.get("oppHash" + currentRow).get(j + 1) - oppHashMap.get("oppHash" + currentRow).get(j) != 1) {
                        check++;
                    }
                }
            } while (check != oppHashMap.get("oppHash" + currentRow).size() - 1);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int marGins;
            if ((int) width / 500 < 1)
                marGins = 1;
            else
                marGins = (int) width / 500;
            lp.setMargins(marGins, (int) height / 400, marGins, 0);
            for (int i = 0; i < oppHashMap.get("oppHash" + currentRow).size(); i++) {
                int position = oppHashMap.get("oppHash" + currentRow).get(i);
                final ImageView imageView = new ImageView(context);
                imageView.setImageBitmap(oppChunkedImages.get(position));
                imageView.setLayoutParams(lp);
                imageView.setTag("oppChunk" + position);
                ll.addView(imageView);
            }
            final int spacing = oppChunkedImages.get(currentRow).getHeight() + (int) height / 400;
            final int current = oppRowCount + 1;
            ll.setX(width / 72);
            ll.setY(opp_puzzle.getHeight() - (spacing * current + width / 36));
            opp_puzzle.addView(ll);
        } catch (Exception e) {
            Log.d("createImageOpp CRASH", e + "");
        }
    }

    public void reArrangeColumn(int currentRow) {
        TreeSet<Integer> treeSet = new TreeSet<>();
        ArrayList<Float> pointX = new ArrayList<>();
        oppTemp = new ArrayList<>();
        LinearLayout linearLayout = (LinearLayout) opp_puzzle.findViewWithTag("oppRows_" + currentRow);
        Log.d("reArrangeColumn", currentRow + "");
        for (int i = 0; i < oppHashMap.get("oppHash" + currentRow).size(); i++) {
            pointX.add(linearLayout.getChildAt(i).getX());
            treeSet.add(oppHashMap.get("oppHash" + currentRow).get(i));
        }
        oppTemp.addAll(treeSet);

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            ImageView imageView = (ImageView) linearLayout.findViewWithTag("oppChunk" + oppTemp.get(i));
            TranslateAnimation translateAnimation = new TranslateAnimation(imageView.getX(), pointX.get(i), imageView.getY(), imageView.getY());
            translateAnimation.setDuration(250);
            imageView.startAnimation(translateAnimation);
            imageView.setX(pointX.get(i));
        }
        oppRowCount++;
        Log.d("TEST", oppRowCount + "");
        Log.d("TEST", oppHashMap.size() + "");
        if (oppRowCount == oppHashMap.size()) {
            ImageView highLight = (ImageView) findViewById(R.id.highlight);
            ((AnimationDrawable) highLight.getBackground()).stop();
            highLight.setImageDrawable(null);
            opp_puzzle.removeView(highLight);
        }
    }

    public void reArrangeRow() {
        try {
            for (int i = 0; i < opp_puzzle.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) opp_puzzle.findViewWithTag("oppRows_" + i);
                linearLayout.animate().translationY((oppChunkedImages.get(i).getHeight() + (int) height / 400) * (i) + width / 36).start();
            }
            oppRowCount = 0;
        } catch (Exception e) {
            Log.d("reArrangeRow CRASH", e + "");
        }
    }

    public void createHighLight() {
        try {
            final ImageView checkHighlight = (ImageView) findViewById(R.id.highlight);
            if (checkHighlight == null) {
                ImageView highlight = new ImageView(NewPuzzleActivity.this.getApplication());
                highlight.setBackground(ContextCompat.getDrawable(context, R.drawable.animation_highlight));
                highlight.setId(R.id.highlight);
                opp_puzzle.addView(highlight);
                ViewGroup.LayoutParams lp = highlight.getLayoutParams();
                lp.height = oppChunkedImages.get(0).getHeight();
                lp.width = opp_puzzle.getWidth();
                highlight.setVisibility(View.INVISIBLE);
                highlight.setScaleX(1.1f);
                highlight.setScaleY(1.6f);
            }
        } catch (Exception e) {
            Log.d("createHighLight CRASH", e + "");
        }
    }

    private void highlightControl() {
        try {
            LinearLayout temp = (LinearLayout) opp_puzzle.findViewWithTag("oppRows_" + oppRowNum);
            final ImageView highlight = (ImageView) findViewById(R.id.highlight);
            if (highlight != null) {
                highlight.bringToFront();
                highlight.animate().translationY(temp.getY()).withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        highlight.setVisibility(View.VISIBLE);
                        ((AnimationDrawable) highlight.getBackground()).start();
                    }
                }).start();
            } else {
                createHighLight();
                highlightControl();
            }
        } catch (Exception e) {
            Log.d("highlightControl CRASH", e + "");
        }
    }

    /*public void updateImageOppRow() {
        LinearLayout temp = (LinearLayout) opp_puzzle.findViewWithTag("oppRows_" + oppRowNum);
        temp.setVisibility(View.VISIBLE);
        oppRowCount++;
    }*/

    public void startCheckOppPuzzle(int rowNumber) {
        this.oppRowNum = rowNumber;
        highlightControl();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d(GoogleServiceApi.TAG, "onstop!");
    }

    @Override
    public void onRoomCreated(int i, Room room) {

    }

    @Override
    public void onJoinedRoom(int i, Room room) {

    }

    @Override
    public void onLeftRoom(int i, String s) {
        Log.d("Puzzle_Left", "Puzzle_Left");
        googleServiceApi.clearPuzzle();
        state.setFirst_complete("own");
        endGameDialogue();
    }

    @Override
    public void onRoomConnected(int i, Room room) {

    }

    private void customSetup() {

        gameMODE = state.getGameMode(ownCurrPicCounter);
        power = new SuperPower(own_puzzle, powerLayout, context, NewPuzzleActivity.this, width, height);
        String gameMode = null;
        if (supportSkill) {
            if (stageSkill.equals("dif-1"))
                gameMode = power.startGamePhase(stageSkill, gameMODE, stageSkillPercentage);
        } else if (stageSkill.equals("dif+1"))
            gameMode = power.startGamePhase(stageSkill, gameMODE, stageSkillPercentage);

        if (gameMode != null) {
            delayApply = 150;
        } else {
            gameMode = gameMODE;
            delayApply = 150;
        }

        CustomSetup setup = new CustomSetup(gameMode);
        chunkNumbers = setup.getChunkNumbers();
        howManyPerRow = setup.getHowManyPerRow();
        howManyRows = setup.getHowManyRows();
        ownInverstNum = setup.getOwnInverstNum();
        ownRows = setup.getOwnRows();
        ownRandomPick = new HashMap<>();
        ownChunkedImages = new ArrayList<>();
        oppChunkedImages = new ArrayList<>();
        timeForChallenger = new ArrayList<>();
        currProgress = "";
        ownGiveUpCounter = 4;
        completePictureCount = 0;
        oppCompletePictureCount = 0;
        highestCombo = 0;
        tokenCount = 1;
    }

    private void generateSequenceHashMap() {
        if (!isBot) {
            String sendGenerateHash = "GenerateHash:" + chunkNumbers + "," + howManyPerRow + "";
            byte[] sendGenerateHashString = sendGenerateHash.getBytes();
            Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendGenerateHashString,
                    googleServiceApi.getRoomId(), googleServiceApi.getOppId());
        }
        int check;
        do {
            check = 0;
            Collections.shuffle(ownRows);
            for (int i = 0; i < ownRows.size(); i++) {
                if (ownRows.get(i) != i) {
                    check++;
                }
            }
        } while (check != ownRows.size());

        int counter = 0, ownNumber = 0;

        for (int i = 0; i < chunkNumbers; i++) {
            temp.add(i);

            counter++;

            if (counter == howManyPerRow) {
                ownRandomPick.put("hashmap_num" + ownNumber, temp);
                temp = new ArrayList<>();
                ownNumber++;
                counter = 0;
            }
        }
    }

    private void oppClearMemory() {
        clearOppChunkedImages();
    }

    private void ownClearMemory() {

        ownRows.clear();
        temp.clear();
        ownRandomPick.clear();
        clearOwnChunkedImages();
    }

    private void clearGeneralImageMemory() {
        imageObj.clearMemory();
        state.setGeneralImageNull();
    }

    private void clearOwnChunkedImages() {
        if (ownChunkedImages != null) {

            for (int i = 0; i < ownChunkedImages.size(); i++) {
                ownChunkedImages.get(i).recycle();
            }
            ownChunkedImages.clear();
            ownChunkedImages = null;
        }
    }

    private void clearOppPuzzle() {
        ImageView highlight = (ImageView) findViewById(R.id.highlight);
        if (highlight != null) {
            opp_puzzle.removeView(highlight);
        }

        for (int i = 0; i < opp_puzzle.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) opp_puzzle.getChildAt(i);
            for (int j = 0; j < linearLayout.getChildCount(); j++) {
                ((BitmapDrawable) ((ImageView) linearLayout.getChildAt(j)).getDrawable()).getBitmap().recycle();
                ((ImageView) linearLayout.getChildAt(j)).setImageDrawable(null);
            }
        }
        opp_puzzle.removeAllViews();
    }

    private void clearOwnPuzzle() {
        for (int i = 0; i < own_puzzle.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) own_puzzle.getChildAt(i);
            for (int j = 0; j < linearLayout.getChildCount(); j++) {
                ((BitmapDrawable) ((ImageView) linearLayout.getChildAt(j)).getDrawable()).getBitmap().recycle();
                ((ImageView) linearLayout.getChildAt(j)).setImageDrawable(null);
            }
        }
        own_puzzle.removeAllViews();
    }

    private void clearOppChunkedImages() {
        if (oppChunkedImages != null) {

            for (int i = 0; i < oppChunkedImages.size(); i++) {
                oppChunkedImages.get(i).recycle();
            }

            oppChunkedImages.clear();
            oppChunkedImages = null;
        }
        oppChunkedImages = new ArrayList<>();
    }

    private void updateOwnCurrentPuzzleCounter(final String instruction) {

        if (!isBot) {
            String sendUpdate = "UpdateCurrentPuzzle:" + instruction;
            byte[] sendUpdateString = sendUpdate.getBytes();
            Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendUpdateString,
                    googleServiceApi.getRoomId(), googleServiceApi.getOppId());
        }

        long endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime);
        updateOwnImageClear();
        timeForChallenger.add(totalTime + " - " + instruction);
        ownCurrPicCounter++;

        if (ownGiveUpCounter != 0) {
            if (instruction.equals("give_up") || instruction.equals("skip"))
                ownGiveUpCounter--;
            if (ownImageSeq.get(0).equals("own")) {
                imageObj.getOwnPopularBitmapList().get(0).recycle();
                imageObj.getOwnPopularBitmapList().remove(0);
            } else {
                imageObj.getBotPopularBitmapList().get(0).recycle();
                imageObj.getBotPopularBitmapList().remove(0);
            }

            ownImageSeq.remove(0);

            if (ownImageSeq.size() >= 4) {
                new LoadExtraPicture("own", 0, 0).execute(imageObj);
            }
        } else {
            if (ownImageSeq.get(0).equals("own")) {
                imageObj.getOwnPopularBitmapList().get(0).recycle();
                imageObj.getOwnPopularBitmapList().remove(0);
            } else {
                imageObj.getBotPopularBitmapList().get(0).recycle();
                imageObj.getBotPopularBitmapList().remove(0);
            }

            if (instruction.equals("give_up") || instruction.equals("skip"))
                ownGiveUpCounter--;
            ownImageSeq.remove(0);
        }
    }

    public void updateOppCurrentPuzzleCounter(String instruction) {

        clearOppPuzzle();
        ParticleSystem ps;
        switch (instruction) {

            case "give_up":
                clearOppPuzzle();
                ps = new ParticleSystem(NewPuzzleActivity.this, 1, R.drawable.give_up, 500);
                ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.6f, 0.6f).oneShot(opp_puzzle, 1);
                break;

            case "skip":
                clearOppPuzzle();
                ps = new ParticleSystem(NewPuzzleActivity.this, 1, R.drawable.skip, 500);
                ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.6f, 0.6f).oneShot(opp_puzzle, 1);
                break;

            case "fail":
                clearOppPuzzle();
                ps = new ParticleSystem(NewPuzzleActivity.this, 1, R.drawable.fail, 500);
                ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.5f, 0.5f).oneShot(opp_puzzle, 1);
                break;

            case "next_image":
                ps = new ParticleSystem(NewPuzzleActivity.this, 1, R.drawable.nice2, 500);
                ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.6f, 0.6f).oneShot(opp_puzzle, 1);
                break;
        }
        oppCurrPicCounter++;
        oppClearMemory();
        if (oppImageSeq.get(0).equals("opp")) {
            Log.d("bmp", imageObj.getOppPopularBitmapList().get(0).hasAlpha() + "");
            imageObj.getOppPopularBitmapList().get(0).recycle();
            imageObj.getOppPopularBitmapList().remove(0);
        } else {
            Log.d("bmp", imageObj.getOppBotPopularBitmapList().get(0).hasAlpha() + "");
            imageObj.getOppBotPopularBitmapList().get(0).recycle();
            imageObj.getOppBotPopularBitmapList().remove(0);
        }
        oppImageSeq.remove(0);
        if (imageObj.getOppPopularImageList().size() != 0)
            new LoadExtraPicture("opp", 0, 0).execute(imageObj);
    }

    private void updateOwnImageClear() {
        if (!isBot) {
            String sendUpdate = "UpdateOwnImageClear:" + completePictureCount;
            byte[] sendUpdateString = sendUpdate.getBytes();
            Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendUpdateString,
                    googleServiceApi.getRoomId(), googleServiceApi.getOppId());
        }

        int[] drawable = {R.drawable.ko_0, R.drawable.ko_1, R.drawable.ko_2, R.drawable.ko_3, R.drawable.ko_4};
        ImageView ownCounter = (ImageView) findViewById(R.id.own_indicator);
        ownCounter.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, drawable[completePictureCount])).getBitmap(), (int) (width / 10.8), (int) (width / 10.8), true));
    }

    public void updateOppImageClear(int completePicCount) {
        oppCompletePictureCount = completePicCount;
        int[] drawable = {R.drawable.ko_0, R.drawable.ko_1, R.drawable.ko_2, R.drawable.ko_3, R.drawable.ko_4};
        ImageView oppCounter = (ImageView) findViewById(R.id.opp_indicator);
        oppCounter.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, drawable[oppCompletePictureCount])).getBitmap(), (int) (width / 10.8), (int) (width / 10.8), true));
    }

    private void createProfileIndicator() {
        RelativeLayout secondScreen = (RelativeLayout) findViewById(R.id.secondScreen);
        RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.status);
        int remainderWidth = (int) ((width - opp_puzzle.getWidth()) / 2 - width / 54);

        final IconRoundCornerProgressBar roundBar = new IconRoundCornerProgressBar(context, null);

        roundBar.setPivotX(roundBar.getX());
        roundBar.setPivotY(roundBar.getY());
        roundBar.setRotation(90);
        roundBar.setX(remainderWidth / 2 + width / 108);
        roundBar.setY(width / 108);
        secondScreen.addView(roundBar);
        ViewGroup.LayoutParams lp2 = roundBar.getLayoutParams();
        lp2.height = remainderWidth / 2;
        lp2.width = opp_puzzle.getHeight();
        roundBar.setProgressColor(Color.GREEN);
        roundBar.setRadius((int) width / 50);
        roundBar.setPadding((int) width / 108);
        roundBar.setIconSize((int) (width / 16.875f));
        roundBar.setIconImageResource(R.drawable.timer);
        roundBar.setId(R.id.timer_progress);
        roundBar.setMax(15000);
        roundBar.setReverse(true);

        RelativeLayout own_layout = new RelativeLayout(context);
        statusLayout.addView(own_layout);
        ViewGroup.LayoutParams layoutParams = own_layout.getLayoutParams();
        layoutParams.height = opp_puzzle.getHeight() / 3;
        layoutParams.width = statusLayout.getWidth();
        own_layout.setY(opp_puzzle.getY() + opp_puzzle.getHeight() / 8);
        own_layout.setX(statusLayout.getWidth());
        own_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.back_black));

        Bitmap bitmap = create.getCircleBitmap(ownProfilePicture, 0);
        ImageView own_profile = new ImageView(context);
        own_profile.setImageBitmap(Bitmap.createScaledBitmap(bitmap, (int) width / 8, (int) width / 8, true));
        own_layout.addView(own_profile);
        own_profile.setX(width / 108);
        own_profile.setY(width / 108);
        ImageView own_koPoint = new ImageView(context);
        TextView own_score = new TextView(context);
        own_score.setTextSize(15);
        own_score.setTypeface(null, Typeface.BOLD);
        own_layout.addView(own_koPoint);
        own_layout.addView(own_score);
        own_koPoint.setX(own_profile.getX() + width / 8);
        own_koPoint.setY(own_profile.getY());
        own_score.setX(own_profile.getX());
        own_score.setY(own_profile.getY() + width / 8);
        own_score.setText(0 + "");
        own_score.setId(R.id.own_score);
        own_koPoint.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ko_0)).getBitmap(), (int) (width / 10.8), (int) (width / 10.8), true));
        own_koPoint.setId(R.id.own_indicator);

        final RelativeLayout opp_layout = new RelativeLayout(context);
        statusLayout.addView(opp_layout);
        layoutParams = opp_layout.getLayoutParams();
        layoutParams.height = opp_puzzle.getHeight() / 3;
        layoutParams.width = statusLayout.getWidth();
        opp_layout.setY(opp_puzzle.getY() + opp_puzzle.getHeight() / 2 + opp_puzzle.getHeight() / 8);
        opp_layout.setX(statusLayout.getWidth());
        opp_layout.setBackground(ContextCompat.getDrawable(context, R.drawable.back_black));
        ImageView opp_prifile = new ImageView(context);
        bitmap = create.getCircleBitmap(oppProfilePicture, 0);
        opp_prifile.setImageBitmap(Bitmap.createScaledBitmap(bitmap, (int) width / 8, (int) width / 8, true));
        opp_layout.addView(opp_prifile);
        opp_prifile.setX(width / 108);
        opp_prifile.setY(width / 108);
        ImageView opp_koPoint = new ImageView(context);
        TextView opp_score = new TextView(context);
        opp_score.setTextSize(15);
        opp_score.setTypeface(null, Typeface.BOLD);
        opp_layout.addView(opp_koPoint);
        opp_layout.addView(opp_score);
        opp_koPoint.setX(opp_prifile.getX() + width / 8);
        opp_koPoint.setY(opp_prifile.getY());

        opp_score.setX(opp_prifile.getX());
        opp_score.setY(opp_prifile.getY() + width / 8);

        opp_score.setText(0 + "");
        opp_score.setId(R.id.opp_score);

        opp_koPoint.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ko_0)).getBitmap(), (int) (width / 10.8), (int) (width / 10.8), true));
        opp_koPoint.setId(R.id.opp_indicator);

        own_layout.animate().translationX(width / 108).withEndAction(new Runnable() {
            @Override
            public void run() {
                opp_layout.animate().translationX(width / 108).start();
            }
        }).start();
    }

    private void updateOwnScore(int bonusScore) {
        if (!isBot) {
            String sendScore = "UpdateScore:" + howManyPerRow + "," + comboCount + "," + bonusScore;
            byte[] sendScoreString = sendScore.getBytes();
            Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendScoreString,
                    googleServiceApi.getRoomId(), googleServiceApi.getOppId());
        }

        int[] comboDrawable = {R.drawable.combo_0, R.drawable.combo_1, R.drawable.combo_2, R.drawable.combo_3, R.drawable.combo_4, R.drawable.combo_5, R.drawable.combo_6,
                R.drawable.combo_7, R.drawable.combo_8, R.drawable.combo_9};

        final TextView own_score = (TextView) findViewById(R.id.own_score);
        int textNum = Integer.parseInt(own_score.getText().toString());

        ValueAnimator value = new ValueAnimator();
        value.setObjectValues(textNum, (textNum + (50 * howManyPerRow) + ((bonusScore * comboCount) / 25) + ((50 * howManyPerRow * comboCount) / 5)));
        //value.setObjectValues(textNum, 99999);

        value.setDuration(250);
        value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                own_score.setText("" + (int) animation.getAnimatedValue());
            }
        });
        value.start();

        final LinearLayout comboLayout = new LinearLayout(context);
        comboLayout.bringToFront();
        comboLayout.setOrientation(LinearLayout.HORIZONTAL);
        comboLayout.setGravity(Gravity.CENTER);
        relativeLayout.addView(comboLayout);
        ViewGroup.LayoutParams comboLP = comboLayout.getLayoutParams();
        comboLP.height = (int) (height / 8);
        comboLP.width = (int) width;
        comboLayout.setY(own_puzzle.getY() + own_puzzle.getHeight() / 2 - height / 8);
        comboLayout.setX(-width);
        comboLayout.setScaleX(0);
        comboLayout.setScaleY(0);
        final ImageView combo = new ImageView(context);
        final ImageView comboNum = new ImageView(context);
        combo.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.combo)).getBitmap(), (int) width / 2, (int) height / 8, true));
        comboLayout.addView(combo);

        if (comboCount >= highestCombo)
            highestCombo = comboCount;

        if (comboCount >= 10) {
            ImageView comboNum2 = new ImageView(context);
            comboNum2.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, comboDrawable[comboCount / 10])).getBitmap(), (int) width / 4, (int) height / 8, true));
            comboLayout.addView(comboNum2);
        }

        comboNum.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, comboDrawable[comboCount % 10])).getBitmap(), (int) width / 4, (int) height / 8, true));
        comboLayout.addView(comboNum);
        comboLayout.animate().translationX(0).scaleX(0.8f).scaleY(0.8f).withEndAction(new Runnable() {
            @Override
            public void run() {
                comboLayout.animate().setStartDelay(150).translationX(width).scaleX(0).scaleY(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        combo.setImageDrawable(null);
                        comboNum.setImageDrawable(null);
                        relativeLayout.removeView(comboLayout);
                    }
                }).start();
            }
        }).start();
    }

    public void updateOppScore(int oppHowManyPerRow, int oppComboCount, int bonusScore) {
        final TextView opp_score = (TextView) findViewById(R.id.opp_score);
        int textNum = Integer.parseInt(opp_score.getText().toString());

        Log.d("updateOppScore", oppHowManyPerRow + "");
        Log.d("updateOppScore", oppComboCount + "");

        ValueAnimator value = new ValueAnimator();
        value.setObjectValues(textNum, (textNum + (50 * oppHowManyPerRow) + ((bonusScore * comboCount) / 25) + ((50 * oppHowManyPerRow * oppComboCount) / 5)));
        value.setDuration(250);
        value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                opp_score.setText("" + (int) animation.getAnimatedValue());
            }
        });
        value.start();
    }

    public void skillActiavtedAnimation(String whoisThis, String instruction) {
        if (whoisThis.equals("own")) {
            if (instruction.equals("activate")) {
                own_puzzle.setBackgroundColor(Color.parseColor("#cc000000"));
                ParticleSystem ps = new ParticleSystem(NewPuzzleActivity.this, 1, R.drawable.skill_activate, 1000);
                ps.setSpeedModuleAndAngleRange(0, 0, 90, 90).setScaleRange(1.5f, 1.5f).setFadeOut(100).oneShot(powerLayout, 1);
                if (!isBot) {
                    String sendSkillActivated = "SkillActivated:" + instruction;
                    byte[] sendSkillActivatedString = sendSkillActivated.getBytes();
                    Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendSkillActivatedString,
                            googleServiceApi.getRoomId(), googleServiceApi.getOppId());
                }
            } else {
                own_puzzle.setBackgroundColor(Color.parseColor("#00000000"));
                if (!isBot) {
                    String sendSkillActivated = "SkillDeActivated:" + instruction;
                    byte[] sendSkillActivatedString = sendSkillActivated.getBytes();
                    Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendSkillActivatedString,
                            googleServiceApi.getRoomId(), googleServiceApi.getOppId());
                }
            }
        }
        if (whoisThis.equals("opp")) {
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondScreen);
            if (instruction.equals("activate")) {
                relativeLayout.setBackgroundColor(Color.parseColor("#cc000000"));
            } else {
                relativeLayout.setBackgroundColor(Color.parseColor("#00000000"));
            }
        }
    }

    public class PuzzleOperation extends AsyncTask<GeneralImage, Void, GeneralImage> {

        @Override
        protected GeneralImage doInBackground(GeneralImage... imageObj) {
            create.ownSplitImage();
            if (!isBot) {
                String splitImage = "FirstSplitImage:" + howManyRows + "," + howManyPerRow;
                byte[] splitImageString = splitImage.getBytes();
                Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), splitImageString,
                        googleServiceApi.getRoomId(), googleServiceApi.getOppId());
            }
            return imageObj[0];
        }

        @Override
        protected void onPostExecute(final GeneralImage imageObj) {

            createProfileIndicator();
            ObjectAnimator colorAnim = ObjectAnimator.ofFloat(color_row, "x", color_row.getX(), 0);
            ObjectAnimator oppAnim = ObjectAnimator.ofFloat(opp_puzzle, "y", -opp_puzzle.getHeight(), opp_puzzle.getY());
            ObjectAnimator alpha = ObjectAnimator.ofFloat(blackScreen, "alpha", 0, 1);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(colorAnim).before(oppAnim).before(alpha);
            animatorSet.setDuration(1000);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    final ImageView imageView = new ImageView(context);
                    imageView.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ready)));
                    blackScreen.addView(imageView);
                    imageView.setScaleY(0);
                    imageView.setScaleX(0);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    imageView.animate().scaleX(1).scaleY(1).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            threadRunning = true;
                            ownThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isBot) {
                                        String StartTheGame = "StartTheGame:" + "";
                                        byte[] StartTheGameString = StartTheGame.getBytes();
                                        Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), StartTheGameString,
                                                googleServiceApi.getRoomId(), googleServiceApi.getOppId());
                                        while (threadRunning) {
                                            if (state.getStartGame() != null) {
                                                if (state.getStartGame().equals("FinallyStartTheGame")) {
                                                    threadRunning = false;
                                                }
                                            }
                                        }
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            blackScreen.animate().alpha(0).withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    blackScreen.removeView(imageView);
                                                    ParticleSystem ps = new ParticleSystem(NewPuzzleActivity.this, 1, R.drawable.start1, 800);
                                                    ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).oneShot(blackScreen, 1);
                                                    create.taskAsign(ownChunkedImages);
                                                }
                                            }).start();
                                        }
                                    });
                                }
                            });
                            ownThread.start();
                        }
                    }).start();

                }
            });
            animatorSet.start();
            oppAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    opp_puzzle.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public class LoadExtraPicture extends AsyncTask<GeneralImage, Void, GeneralImage> {

        String whoExcess = null;
        int oppRows, oppColumn;

        public LoadExtraPicture(String whoExcess, int oppRows, int oppColumn) {
            this.whoExcess = whoExcess;
            if (whoExcess.equals("opp")) {
                this.oppRows = oppRows;
                this.oppColumn = oppColumn;
            }
        }

        @Override
        protected GeneralImage doInBackground(GeneralImage... imageObj) {
            float oppPuzzleWidth = (width / 1.55f);
            float oppPuzzleHeight = (height / 2.88f);

            if (whoExcess.equals("own")) {
                Log.d("SIZE", ownImageSeq.size() + "");
                if (ownImageSeq.get(3).equals("own")) {
                    Bitmap ownScaledBitmap = scaleBitmap(loadImageUrl(imageObj[0].getOwnPopularImageList().get(0)), width, (height / 2));
                    imageObj[0].setOwnPopularBitmapList(ownScaledBitmap);
                    imageObj[0].getOwnPopularImageList().remove(0);
                } else {
                    downloadImageForBot(width, (height / 2), imageObj[0].getBotPopularImageList().get(0), "botPopularBitmap");
                    imageObj[0].getBotPopularImageList().remove(0);
                }
            }

            if (whoExcess.equals("opp")) {
                if (oppCurrPicCounter == 0) {
                    create.oppSplitImage(oppRows, oppColumn);
                } else {
                    if (oppImageSeq.get(3).equals("opp")) {
                        Bitmap oppScaledBitmap = scaleBitmap(loadImageUrl(imageObj[0].getOppPopularImageList().get(0)), oppPuzzleWidth, oppPuzzleHeight);
                        imageObj[0].setOppPopularBitmapList(oppScaledBitmap);
                    } else {
                        downloadImageForBot(oppPuzzleWidth, oppPuzzleHeight, imageObj[0].getOppPopularImageList().get(0), "oppPopularBitmap");
                    }
                    imageObj[0].getOppPopularImageList().remove(0);
                }
            }
            return imageObj[0];
        }

        private void downloadImageForBot(final float reqWidth, final float reqHeight, final String randomNumber, final String setBitmapToWhere) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://socialrpgpuzzle-85412621.appspot.com");
            final StorageReference spaceRef = storageRef.child("socialpuzzle/skitterphoto_" + randomNumber + ".jpg");
            Log.d(GoogleServiceApi.TAG, "Picture Taken : " + spaceRef.getPath());

            final long ONE_MEGABYTE = 500 * 1000; //Maximum 500KB
            spaceRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    switch (setBitmapToWhere) {
                        case "oppPopularBitmap":
                            Log.d("oppPopularBitmap", "oppPopularBitmap");
                            imageObj.setOppBotPopularBitmapList(Bitmap.createScaledBitmap(bmp, (int) reqWidth, (int) reqHeight, true));
                            if (bmp != null) {
                                bmp.setHasAlpha(false);
                                bmp.recycle();
                            }
                            break;

                        case "botPopularBitmap":
                            Log.d("botPopularBitmap", "botPopularBitmap");
                            imageObj.setBotPopularBitmapList(Bitmap.createScaledBitmap(bmp, (int) reqWidth, (int) reqHeight, true));
                            if (bmp != null) {
                                bmp.setHasAlpha(false);
                                bmp.recycle();
                            }
                            break;
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(GoogleServiceApi.TAG, "exception" + exception);
                    downloadImageForBot(reqWidth, reqHeight, randomNumber, setBitmapToWhere);
                }
            });
        }

        private Bitmap scaleBitmap(Bitmap bitmapToScale, float newWidth, float newHeight) {

            if (bitmapToScale == null) {
                Log.d(GoogleServiceApi.TAG, "nullscalebitmap: " + bitmapToScale.toString());
                return null;
            }

            Log.d(GoogleServiceApi.TAG, "bitmapToScale:" + bitmapToScale.toString());
            int width = bitmapToScale.getWidth();
            int height = bitmapToScale.getHeight();

            Log.d(GoogleServiceApi.TAG, "newwidth:" + width);
            Log.d(GoogleServiceApi.TAG, "newheight:" + height);

            Matrix matrix = new Matrix();

            matrix.postScale(newWidth / width, newHeight / height);

            return Bitmap.createBitmap(bitmapToScale, 0, 0, bitmapToScale.getWidth(), bitmapToScale.getHeight(), matrix, true);
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
        protected void onPostExecute(GeneralImage generalImage) {
            if (whoExcess.equals("opp") && oppCurrPicCounter == 0)
                createHighLight();
        }
    }

    public class PuzzleCreate {

        private void taskAsign(final ArrayList<Bitmap> chunkedImages) {
            allowBack = false;
            String startPhase = null;
            setDrag(own_puzzle, "", null);
            initializeLinearPuzzle();
            Log.d("STAGESKILL", stageSkill);
            if (!stageSkill.equals("dif+1") && !stageSkill.equals("dif-1")) {
                startPhase = power.startGamePhase(stageSkill, null, stageSkillPercentage);
            }
            if (startPhase == null) {
                createColor();
                createImage(chunkedImages, image_counter);
            }
            if (firstTime) {
                if (oppRowNum != -1) {
                    highlightControl();
                    firstTime = false;
                }
                if (isBot) {
                    bot.initialiseBot();
                    firstTime = false;
                }
            }
        }

        public void createColor() {
            final int[] give_up_1 = {R.drawable.red_slider, R.drawable.green_slider, R.drawable.blue_slider, R.drawable.yellow_slider};
            final int[] giveUpCounte = {R.drawable.give_up0, R.drawable.give_up1, R.drawable.give_up2, R.drawable.give_up3, R.drawable.give_up4};
            final RelativeLayout sliderView = new RelativeLayout(context);
            sliderView.setId(R.id.slider);
            sliderView.setGravity(Gravity.CENTER);
            Random random = new Random();
            final int randomColor = random.nextInt(give_up_1.length);
            colorSelected = randomColor;
            final ImageView giveUpCounter = new ImageView(context);
            sliderView.setBackground(ContextCompat.getDrawable(context, give_up_1[randomColor]));
            Log.d("GIVE_UP", ownGiveUpCounter + "");
            giveUpCounter.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, giveUpCounte[ownGiveUpCounter])).getBitmap(), (int) width / 10, (int) width / 10, true));
            //giveUpCounter.setImageResource(giveUpCounte[ownGiveUpCounter]);
            sliderView.addView(giveUpCounter);
            color_row.addView(sliderView);
            ObjectAnimator animX = ObjectAnimator.ofFloat(sliderView, "x", 0 - width / 4, 0);
            animX.start();
            sliderView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    color_row.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                sliderView.setX(event.getX() - (sliderView.getWidth() / 2));
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (event.getX() >= (width - sliderView.getWidth())) {
                                    skillActiavtedAnimation("own", "de-activate");
                                    sliderView.setX(width - sliderView.getWidth());
                                    color_row.setOnTouchListener(null);
                                    giveUpCounter.setImageDrawable(null);
                                    sliderView.setBackground(null);
                                    color_row.removeAllViews();

                                    if (pictureCompleted && swapActivate) {

                                        if (!isBot) {
                                            String sendImageComplete = "ImageComplete:" + "";
                                            byte[] sendImageCompleteString = sendImageComplete.getBytes();
                                            Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendImageCompleteString,
                                                    googleServiceApi.getRoomId(), googleServiceApi.getOppId());
                                        }

                                        if (powerLayout.getChildCount() != 0) {
                                            ImageView imageView = (ImageView) powerLayout.getChildAt(0);
                                            ((AnimationDrawable) imageView.getDrawable()).stop();
                                            imageView.setImageDrawable(null);
                                        }
                                        final IconRoundCornerProgressBar progressBar = (IconRoundCornerProgressBar) findViewById(R.id.timer_progress);
                                        int bonusScore = (int) progressBar.getProgress();
                                        if (comboStart) {
                                            comboStart = false;
                                            manageCombo("stop");
                                        }
                                        comboCount++;
                                        updateOwnScore(bonusScore);
                                        powerLayout.removeAllViews();
                                        swapActivate = false;
                                        backGroundCounter = 0;
                                        if (!stageSkill.equals("breakGlass"))
                                            delayApply = power.imageComplete(stageSkill, delayApply, stageSkillPercentage);
                                        if (supportSkill)
                                            power.imageComplete(stageSkill, delayApply, stageSkillPercentage);

                                        for (int i = own_puzzle.getChildCount() - 1; i >= 0; i--) {
                                            final LinearLayout linearLayout = (LinearLayout) own_puzzle.getChildAt(i);
                                            linearLayout.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ObjectAnimator animator = ObjectAnimator.ofFloat(linearLayout, "y", linearLayout.getY(), own_puzzle.getHeight());
                                                    animator.setDuration(500);
                                                    animator.addListener(new AnimatorListenerAdapter() {
                                                        @Override
                                                        public void onAnimationEnd(Animator animation) {
                                                            super.onAnimationEnd(animation);
                                                            backGroundCounter++;
                                                            if (backGroundCounter == howManyRows) {
                                                                if (stageSkill.equals("breakGlass")) {
                                                                    backGroundCounter = power.imageComplete(stageSkill, delayApply, stageSkillPercentage);
                                                                }
                                                                if (backGroundCounter != 0)
                                                                    prepareNextImage();
                                                            }
                                                        }
                                                    });
                                                    animator.start();
                                                }
                                            }, delayApply * (own_puzzle.getChildCount() - i));
                                        }
                                    } else if (swapActivate) {
                                        if (powerLayout.getChildCount() != 0) {
                                            ImageView imageView = (ImageView) powerLayout.getChildAt(0);
                                            ((AnimationDrawable) imageView.getDrawable()).stop();
                                            imageView.setImageDrawable(null);
                                        }
                                        swapActivate = false;
                                        powerLayout.removeAllViews();
                                        failPicture();
                                    } else {
                                        if (powerLayout.getChildCount() != 0) {
                                            ImageView imageView = (ImageView) powerLayout.getChildAt(0);
                                            ((AnimationDrawable) imageView.getDrawable()).stop();
                                            imageView.setImageDrawable(null);
                                        }
                                        if (readyState)
                                            skip();
                                        else {
                                            powerLayout.removeAllViews();
                                            giveUp();
                                        }
                                    }
                                } else {
                                    sliderView.setX(0);
                                    color_row.setOnTouchListener(null);
                                }
                            }
                            return true;
                        }
                    });
                    return false;
                }
            });
        }

        private void initializeLinearPuzzle() {
            // Set own puzzle linear layout
            for (int i = 0; i < howManyRows; i++) {
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setGravity(Gravity.CENTER_HORIZONTAL);
                ll.setTag("ownRows_" + i);
                Log.d(GoogleServiceApi.TAG, "startOwnlinearTag:" + ownRows + i);
                own_puzzle.addView(ll);
            }
        }

        public void createImage(final ArrayList<Bitmap> chunkedImages, final int i) {

            final IconRoundCornerProgressBar progressBar = (IconRoundCornerProgressBar) findViewById(R.id.timer_progress);

            if (comboStart && !readyState)
                manageCombo("prepare");

            if (i != howManyRows) {
                int check;
                checker = 0;
                final String whichRow = "hashmap_num" + ownRows.get(ownInverstNum);

                if (!isBot && completePictureCount != 4) {
                    String sendCreateImage = "CreateImage:" + ownRows.get(ownInverstNum);
                    byte[] sendCreateImageString = sendCreateImage.getBytes();
                    Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendCreateImageString,
                            googleServiceApi.getRoomId(), googleServiceApi.getOppId());
                }

                if (!stageSkill.equals("flipAll") || power.skillActivated) {
                    do {
                        check = 0;
                        Collections.shuffle(ownRandomPick.get(whichRow));
                        for (int j = 0; j < ownRandomPick.get(whichRow).size() - 1; j++) {

                            if (ownRandomPick.get(whichRow).get(j + 1) - ownRandomPick.get(whichRow).get(j) != 1) {
                                check++;
                            }
                        }
                    } while (check != ownRandomPick.get(whichRow).size() - 1);
                }
                row = (LinearLayout) own_puzzle.findViewWithTag("ownRows_" + ownInverstNum);

                final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int marGins;
                if ((int) width / 500 < 1)
                    marGins = 1;
                else
                    marGins = (int) width / 500;
                lp.setMargins(marGins, (int) height / 400, marGins, 0);

                Log.d(GoogleServiceApi.TAG, "clickingwhichrow:" + whichRow);

                for (int j = 0; j < howManyPerRow; j++) {
                    final int position = ownRandomPick.get(whichRow).get(j);
                    final ImageView image = new ImageView(NewPuzzleActivity.this.getApplicationContext());
                    image.setImageBitmap(chunkedImages.get(position));
                    image.setLayoutParams(lp);
                    image.setTag(position + "");
                    image.setId(position);
                    backGroundCounter = 0;
                    image.setVisibility(View.INVISIBLE);
                    image.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new SlideInAnimation(image)
                                    .setDirection(com.easyandroidanimations.library.Animation.DIRECTION_UP)
                                    .setListener(new AnimationListener() {
                                        @Override
                                        public void onAnimationEnd(com.easyandroidanimations.library.Animation animation) {
                                            backGroundCounter++;
                                            if (backGroundCounter == howManyPerRow) {
                                                if (supportSkill && stageSkill.equals("revealNextRow") && (ownInverstNum - 1) >= 0) {
                                                    power.getForNextRow(chunkedImages, ownRandomPick.get("hashmap_num" + ownRows.get(ownInverstNum - 1)));
                                                    power.rowCreatedPhase(stageSkill, row, stageSkillPercentage, whichRow);
                                                }
                                                power.rowCreatedPhase(stageSkill, row, stageSkillPercentage, whichRow);
                                                if (progressBar.getProgress() != 0)
                                                    comboStart = true;

                                                if (!readyState && comboStart) {
                                                    timer.start();
                                                }
                                            }
                                        }
                                    })
                                    .animate();
                        }
                    }, delayApply * j);
                    row.addView(image);
                    if (!stageSkill.equals("rotateAll") || power.skillActivated) {
                        setTouch(image);
                        setDrag(image, whichRow, row);
                    }
                }
                allowBack = true;
                if (comboCount > 1) {
                    manageCombo("prepare");
                }
                startTime = System.currentTimeMillis();

            } else {
                comboStart = false;
                swapActivate = true;
                backGroundCounter = 0;
                powerLayout.removeAllViews();
                int childCount = own_puzzle.getChildCount();
                for (int j = 0; j < childCount; j++) {
                    final View child = own_puzzle.getChildAt(j);
                    final ScaleAnimation scaleUp = new ScaleAnimation(1, 1.2f, 1, 1.2f, child.getWidth() / 2, child.getHeight() / 2);
                    final ScaleAnimation scaleDown = new ScaleAnimation(1.2f, 1, 1.2f, 1, child.getWidth() / 2, child.getHeight() / 2);
                    scaleUp.setDuration(100);
                    scaleDown.setDuration(100);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            child.startAnimation(scaleUp);
                            scaleUp.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    backGroundCounter++;
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    child.startAnimation(scaleDown);
                                    if (backGroundCounter == own_puzzle.getChildCount()) {
                                        if (progressBar.getProgress() != 0)
                                            comboStart = true;
                                        if (comboStart)
                                            timer.start();
                                        dragLayout();
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        }
                    }, delayApply * j);
                }
            }
        }

        public boolean editArrayList(ArrayList<Integer> temp, int dragged2, int target2, String whichRow) {
            int store_target2 = 0, store_dragged2 = 0, check = 0;

            for (int i = 0; i < howManyPerRow; i++) {
                if (temp.get(i) == dragged2) {
                    store_dragged2 = i;
                }

                if (temp.get(i) == target2) {
                    store_target2 = i;
                }
            }
            temp.set(store_dragged2, target2);
            temp.set(store_target2, dragged2);
            ownRandomPick.put(whichRow, temp);

            for (int i = 0; i < howManyPerRow - 1; i++) {
                if (ownRandomPick.get(whichRow).get(i + 1) - ownRandomPick.get(whichRow).get(i) == 1)
                    check++;
                if (check == howManyPerRow - 1)
                    return true;
            }
            return false;
        }

        private void ownSplitImage() {
            allowBack = false;
            //For the number of rows and columns of the grid to be displayed
            int rows, cols;

            //For height and width of the small image chunks
            int ownChunkHeight, ownChunkWidth;

            //Getting the scaled bitmap of the source image
            /*BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = drawable.getBitmap();*/
            Bitmap ownScaledBitmap;
            if (ownImageSeq.get(0).equals("own")) {
                ownScaledBitmap = imageObj.getOwnPopularBitmapList().get(0);
                Log.d("Image_split", ownImageSeq.get(0) + " : " + imageObj.getOwnPopularBitmapList());
            } else {
                ownScaledBitmap = imageObj.getBotPopularBitmapList().get(0);
                Log.d("Image_split", ownImageSeq.get(0) + " : " + imageObj.getBotPopularBitmapList());
            }
            //rows = cols = (int) Math.sqrt(chunkNumbers);
            rows = howManyRows;
            cols = howManyPerRow;
            ownChunkHeight = (ownScaledBitmap.getHeight() / rows - ((int) height / 400));
            ownChunkWidth = (ownScaledBitmap.getWidth() / cols - ((int) width / 210));

            int ownxCoord = 0, ownyCoord = 0;

            for (int x = 0; x < rows; x++) {
                ownxCoord = 0;
                for (int y = 0; y < cols; y++) {
                    ownChunkedImages.add(roundEdge(Bitmap.createBitmap(ownScaledBitmap, ownxCoord, ownyCoord, ownChunkWidth, ownChunkHeight)));
                    //  chunkedImages.add(scaledBitmap);
                    ownxCoord += ownChunkWidth;
                }
                ownyCoord += ownChunkHeight;
            }
            allowBack = true;
            ownScaledBitmap.setHasAlpha(false);
            ownScaledBitmap.recycle();
        }

        private void oppSplitImage(int oppRows, int oppColumn) {
            try {
                allowBack = false;
                //For the number of rows and columns of the grid to be displayed
                int rows, cols;

                //For height and width of the small image chunks
                int oppChunkHeight, oppChunkWidth;

                Bitmap oppScaledBitmap;
                if (oppImageSeq.get(0).equals("opp")) {
                    oppScaledBitmap = imageObj.getOppPopularBitmapList().get(0);
                    Log.d("Image_split", oppImageSeq.get(0) + " : " + imageObj.getOppPopularBitmapList().get(0));
                } else {
                    oppScaledBitmap = imageObj.getOppBotPopularBitmapList().get(0);
                    Log.d("Image_split", oppImageSeq.get(0) + " : " + imageObj.getOppBotPopularBitmapList().get(0));
                }
                //rows = cols = (int) Math.sqrt(chunkNumbers);
                rows = oppRows;
                cols = oppColumn;
                oppChunkHeight = (oppScaledBitmap.getHeight() / rows - (int) height / 400);
                oppChunkWidth = oppScaledBitmap.getWidth() / cols - ((int) width / 210);

                int oppxCoord = 0, oppyCoord = 0;

                for (int x = 0; x < rows; x++) {
                    oppxCoord = 0;
                    for (int y = 0; y < cols; y++) {
                        oppChunkedImages.add(roundEdge(Bitmap.createBitmap(oppScaledBitmap, oppxCoord, oppyCoord, oppChunkWidth, oppChunkHeight)));
                        //  chunkedImages.add(scaledBitmap);
                        oppxCoord += oppChunkWidth;
                    }
                    oppyCoord += oppChunkHeight;
                }
                oppScaledBitmap.setHasAlpha(false);
                oppScaledBitmap.recycle();
                allowBack = true;
            } catch (Exception e) {
                Log.d("OPP_SPLIT_ERROR", e + "");
            }
        }

        private Bitmap roundEdge(Bitmap bitmap) {

            final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);

            final int color = Color.RED;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            float roundPx = width / 50;
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
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

        private Bitmap scaleBitmap(Bitmap bitmapToScale, float newWidth, float newHeight) {

            if (bitmapToScale == null) {
                Log.d(GoogleServiceApi.TAG, "nullscalebitmap: " + bitmapToScale.toString());
                return null;
            }
            Log.d(GoogleServiceApi.TAG, "bitmapToScale:" + bitmapToScale.toString());
            int width = bitmapToScale.getWidth();
            int height = bitmapToScale.getHeight();

            Log.d(GoogleServiceApi.TAG, "newwidth:" + width);
            Log.d(GoogleServiceApi.TAG, "newheight:" + height);

            Matrix matrix = new Matrix();

            matrix.postScale(newWidth / width, newHeight / height);

            return Bitmap.createBitmap(bitmapToScale, 0, 0, bitmapToScale.getWidth(), bitmapToScale.getHeight(), matrix, true);
        }

        /**
         * Stores the index as the View's tag.
         */
        private void setImageIndex(View image, int index) {
            image.setTag(index);
        }

        /**
         * @return the index stored as the View's tag, or -1 if a tag is not present.
         */
        private int getImageIndex(View image) {
            final Object tag = image.getTag();
            if (null == tag) return -1;

            return (Integer) tag;
        }

        private void failPicture() {
            if (comboStart)
                timer.end();
            color_row.removeAllViews();
            clearOwnPuzzle();
            updateOwnCurrentPuzzleCounter("fail");
            ownClearMemory();
            restoreVariable();
            ownThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ownSplitImage();
                }
            });
            ownThread.start();
            blackScreen.setAlpha(1);
            ParticleSystem ps = new ParticleSystem(NewPuzzleActivity.this, 1, R.drawable.fail, 500);
            ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.7f, 0.7f).oneShot(blackScreen, 1);
            try {
                ownThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            blackScreen.postDelayed(new Runnable() {
                @Override
                public void run() {
                    blackScreen.setAlpha(0);
                    taskAsign(ownChunkedImages);
                }
            }, 700);
        }

        public void prepareNextImage() {

            completePictureCount++;
            readyState = true;
            clearOwnPuzzle();
            updateOwnCurrentPuzzleCounter("next_image");
            ownThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    pictureCompleted = false;
                    ownClearMemory();
                    if (completePictureCount != 4) {
                        restoreVariable();
                        ownSplitImage();
                    } else {
                        if (!isBot) {
                            String sendWinDialog = "YOULOSE:" + "";
                            byte[] sendWinDialogString = sendWinDialog.getBytes();
                            Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendWinDialogString,
                                    googleServiceApi.getRoomId(), googleServiceApi.getOppId());
                        }
                        state.setFirst_complete("own");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("ENDGAME", "ENDGAME");
                                endGameDialogue();
                            }
                        });
                    }
                }
            });
            ownThread.start();

            blackScreen.setAlpha(1);
            ParticleSystem ps = new ParticleSystem(NewPuzzleActivity.this, 1, R.drawable.nice2, 500);
            ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.7f, 0.7f).oneShot(blackScreen, 1);
            try {
                ownThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            blackScreen.postDelayed(new Runnable() {
                @Override
                public void run() {
                    blackScreen.setAlpha(0);
                    taskAsign(ownChunkedImages);
                }
            }, 700);
        }

        private void giveUp() {
            if (comboStart)
                timer.end();
            color_row.removeAllViews();
            clearOwnPuzzle();
            updateOwnCurrentPuzzleCounter("give_up");
            ownClearMemory();
            if ((ownGiveUpCounter + 1) == 0) {
                if (!isBot) {
                    String sendWinDialog = "YOUWIN:" + "";
                    byte[] sendWinDialogString = sendWinDialog.getBytes();
                    Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendWinDialogString,
                            googleServiceApi.getRoomId(), googleServiceApi.getOppId());
                }
                //this should be "opp"!!!!
                state.setFirst_complete("own");
                endGameDialogue();
                return;
            }
            restoreVariable();
            ownThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ownSplitImage();
                }
            });
            ownThread.start();
            blackScreen.setAlpha(1);
            ParticleSystem ps = new ParticleSystem(NewPuzzleActivity.this, 1, R.drawable.give_up, 500);
            ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.7f, 0.7f).oneShot(blackScreen, 1);
            try {
                ownThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            blackScreen.postDelayed(new Runnable() {
                @Override
                public void run() {
                    blackScreen.setAlpha(0);
                    taskAsign(ownChunkedImages);
                }
            }, 700);
        }

        private void skip() {
            color_row.removeAllViews();
            clearOwnPuzzle();
            updateOwnCurrentPuzzleCounter("skip");
            ownClearMemory();
            if ((ownGiveUpCounter + 1) == 0) {
                if (!isBot) {
                    String sendWinDialog = "YOUWIN:" + "";
                    byte[] sendWinDialogString = sendWinDialog.getBytes();
                    Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendWinDialogString,
                            googleServiceApi.getRoomId(), googleServiceApi.getOppId());
                }
                state.setFirst_complete("opp");
                endGameDialogue();
                return;
            }
            restoreVariable();
            ownThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ownSplitImage();
                }
            });
            ownThread.start();
            blackScreen.setAlpha(1);
            ParticleSystem ps = new ParticleSystem(NewPuzzleActivity.this, 1, R.drawable.skip, 500);
            ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.7f, 0.7f).oneShot(blackScreen, 1);
            try {
                ownThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            blackScreen.postDelayed(new Runnable() {
                @Override
                public void run() {
                    blackScreen.setAlpha(0);
                    taskAsign(ownChunkedImages);
                }
            }, 700);
        }

        private void restoreVariable() {
            gameMODE = state.getGameMode(ownCurrPicCounter);
            String gameMode = null;
            if (stageSkill.equals("dif+1")) {
                gameMode = power.startGamePhase(stageSkill, gameMODE, stageSkillPercentage);
            }
            if (supportSkill) {
                if (stageSkill.equals("dif-1")) {
                    gameMode = power.startGamePhase(stageSkill, gameMODE, stageSkillPercentage);
                }
            }
            if (gameMode != null) {
                if (gameMode.equals("speedUP")) {
                    delayApply = 50;
                }
                gameMODE = gameMode;
            } else {
                delayApply = 150;
            }
            CustomSetup setup = new CustomSetup(gameMODE);
            pictureCompleted = false;
            image_counter = 0;
            chunkNumbers = setup.getChunkNumbers();
            ownInverstNum = setup.getOwnInverstNum();
            chunkNumbers = setup.getChunkNumbers();
            howManyPerRow = setup.getHowManyPerRow();
            howManyRows = setup.getHowManyRows();
            ownInverstNum = setup.getOwnInverstNum();
            ownRows = setup.getOwnRows();
            ownRandomPick = new HashMap<>();
            ownChunkedImages = new ArrayList<>();
            power.skillActivated = false;
            generateSequenceHashMap();
        }

        private void manageCombo(String instruction) {
            final IconRoundCornerProgressBar progressBar = (IconRoundCornerProgressBar) findViewById(R.id.timer_progress);
            switch (instruction) {

                case "prepare":
                    float progress = (1000 * howManyPerRow) + (500 * howManyPerRow) - (200 * comboCount);
                    progressBar.setMax(progress);
                    progressBar.setProgress(progress);
                    timer = ObjectAnimator.ofFloat(progressBar, "progress", progressBar.getProgress(), 0);
                    timer.setDuration((long) progress).setInterpolator(new LinearInterpolator());
                    timer.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (comboStart) {
                                comboStart = false;
                                comboCount = 0;
                                tokenCount = 1;
                                readyState = false;
                            }
                        }
                    });
                    break;

                case "stop":
                    progress = progressBar.getProgress();
                    timer.cancel();
                    timer = ObjectAnimator.ofFloat(progressBar, "progress", progressBar.getProgress(), 0);
                    timer.setDuration((long) progress).setInterpolator(new LinearInterpolator());
                    timer.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (comboStart) {
                                comboStart = false;
                                comboCount = 0;
                                tokenCount = 1;
                                readyState = false;
                            }
                        }
                    });
                    break;
            }
        }

        private Bitmap highlightImage(Bitmap src) {
            // create new bitmap, which will be painted and becomes result image
            Bitmap bmOut = Bitmap.createBitmap(src.getWidth() + (int) width / 36, src.getHeight() + (int) width / 36, Bitmap.Config.ARGB_8888);
            // setup canvas for painting
            Canvas canvas = new Canvas(bmOut);
            // setup default color
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            // create a blur paint for capturing alpha
            Paint ptBlur = new Paint();
            ptBlur.setMaskFilter(new BlurMaskFilter(width / 54, BlurMaskFilter.Blur.NORMAL));
            int[] offsetXY = new int[2];
            // capture alpha into a bitmap
            Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
            // create a color paint
            Paint ptAlphaColor = new Paint();
            ptAlphaColor.setColor(0xFFFFFFFF);
            // paint color for captured alpha region (bitmap)
            canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
            // free memory
            bmAlpha.recycle();

            // paint the image source
            canvas.drawBitmap(src, 0, 0, null);

            // return out final image
            return bmOut;
        }

//-------------------------------------------------LISTENER---------------------------------------------------------------//

        private void dragLayout() {

            power.swapPhase(stageSkill, ownRows, stageSkillPercentage);
            DragLinearLayout dragLinearLayout = (DragLinearLayout) own_puzzle;
            for (int i = 0; i < dragLinearLayout.getChildCount(); i++) {
                final View child = dragLinearLayout.getChildAt(i);
                // the child will act as its own drag handle
                child.setEnabled(true);
                dragLinearLayout.setViewDraggable(child, child);
                dragLinearLayout.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
                    @Override
                    public void onSwap(View firstView, int firstPosition,
                                       View secondView, int secondPosition) {
                        int first = 0, second = 0;
                        first = ownRows.get(firstPosition);
                        second = ownRows.get(secondPosition);

                        ownRows.set(firstPosition, second);
                        ownRows.set(secondPosition, first);
                        for (int k = 0; k < (howManyRows - 1); k++) {
                            int counter1 = 0, counter2 = 0;
                            counter1 = ownRows.get(k);
                            counter2 = ownRows.get(k + 1);
                            if (counter2 - counter1 == 1) {
                                checker++;
                            }
                        }
                        if (checker == (howManyRows - 1)) {
                            pictureCompleted = true;
                            checker = 0;
                        } else {
                            pictureCompleted = false;
                            checker = 0;
                        }
                    }
                });
            }
        }

        public void setTouch(final ImageView image) {
            image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View view, MotionEvent motionEvent) {

                    for (int i = 0; i < row.getChildCount(); i++) {
                        row.getChildAt(i).setEnabled(true);
                    }

                    int[] selectionColor = {R.drawable.red_panel, R.drawable.green_panel, R.drawable.blue_panel, R.drawable.yellow_panel};
                    final ImageView imageView = (ImageView) findViewById(R.id.selection);
                    imageView.setVisibility(View.VISIBLE);
                    //playSound1();

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        Bitmap scaledBitmap = scaleBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), selectionColor[colorSelected])),
                                view.getWidth(), view.getHeight() + (int) height / 400);
                        imageView.setImageBitmap(scaledBitmap);
                        imageView.setX(view.getX());
                        imageView.setY(height - (imageView.getHeight() * (image_counter + 1)));

                        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, imageView.getX() + imageView.getWidth() / 2, imageView.getY() + imageView.getHeight() / 2);
                        scaleAnimation.setDuration(200);
                        scaleAnimation.setInterpolator(new OvershootInterpolator());
                        imageView.startAnimation(scaleAnimation);
                        firstTime = true;
                        if (readyState && comboStart) {
                            readyState = false;
                            timer.start();
                        }
                        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
                        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                        ClipData dragData = new ClipData(view.getTag().toString(), mimeTypes, item);
                        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);
                        view.startDrag(dragData,
                                myShadow,
                                view,
                                0
                        );
                    }

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        view.setAlpha(1);
                        imageView.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
            });
        }

        public void setDrag(final View image, final String whichRow, final LinearLayout row) {
            image.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(final View view, DragEvent dragEvent) {
                    final View compare = (View) dragEvent.getLocalState();
                    LinearLayout parent = (LinearLayout) findViewById(R.id.drag_row);
                    ImageView selectView = (ImageView) findViewById(R.id.selection);
                    RelativeLayout sliderView = (RelativeLayout) findViewById(R.id.slider);
                    final int imageHeight = selectView.getHeight();
                    boolean supportActive = false;

                    if (firstTime) {
                        firstTime = false;
                        if (supportSkill) {
                            supportActive = power.touchPhase(stageSkill, view.getWidth(), compare, ownRandomPick.get(whichRow), whichRow, stageSkillPercentage);
                        }
                        if (!supportActive) {
                            power.touchPhase(stageSkill, view.getWidth(), compare, ownRandomPick.get(whichRow), whichRow, stageSkillPercentage);
                        }
                    }

                    switch (dragEvent.getAction()) {

                        case DragEvent.ACTION_DRAG_STARTED:
                            sliderView.setEnabled(false);

                            if (compare != null)
                                compare.setAlpha(0.5f);
                            break;

                        case DragEvent.ACTION_DRAG_ENTERED:

                            if (view != parent) {
                                selectView.setX(view.getX());
                                selectView.setY(height - (imageHeight * (image_counter + 1)));
                                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, selectView.getX() + selectView.getWidth() / 2, selectView.getY() + selectView.getHeight() / 2);
                                scaleAnimation.setDuration(200);
                                scaleAnimation.setInterpolator(new OvershootInterpolator());
                                selectView.startAnimation(scaleAnimation);
                            }
                            //playSound1();
                            break;

                        case DragEvent.ACTION_DRAG_EXITED:
                            //playSound1();

                            break;

                        case DragEvent.ACTION_DRAG_ENDED:
                            if (compare != null) {
                                compare.setAlpha(1.0f);
                                compare.setEnabled(true);
                            }
                            sliderView.setEnabled(true);
                            selectView.setVisibility(View.INVISIBLE);
                            for (int i = 0; i < row.getChildCount(); i++) {
                                row.getChildAt(i).animate().alpha(1).setDuration(100).start();
                            }
                            view.setEnabled(true);
                            break;

                        case DragEvent.ACTION_DROP:
                            //playSound2();
                            try {
                                if (ownChunkedImages != null) {

                                    view.setEnabled(false);
                                    compare.setEnabled(false);
                                    compare.setAlpha(1.0f);
                                    view.setAlpha(1);
                                    ObjectAnimator point1 = ObjectAnimator.ofFloat(compare, "x", compare.getX(), view.getX());
                                    ObjectAnimator point2 = ObjectAnimator.ofFloat(view, "x", view.getX(), compare.getX());
                                    AnimatorSet animSet = new AnimatorSet();
                                    animSet.play(point1).with(point2);
                                    animSet.setDuration(100);

                                    temp = new ArrayList<>(ownRandomPick.get(whichRow));

                                    final boolean checker = editArrayList(temp, Integer.parseInt(compare.getTag().toString()), Integer.parseInt(view.getTag().toString()), whichRow);
                                    animSet.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);

                                            if (checker) {
                                                if (comboStart) {
                                                    comboStart = false;
                                                    manageCombo("stop");
                                                    tokenCount++;
                                                }
                                                comboCount++;
                                                for (int i = 0; i < row.getChildCount(); i++) {
                                                    row.getChildAt(i).setEnabled(false);
                                                }
                                                if (isBot) {
                                                    long endTime = System.currentTimeMillis();
                                                    long totalTime = endTime - startTime;
                                                    bot.timeRecord(totalTime);
                                                }
                                                if (stageSkill.equals("box") && powerLayout.getChildCount() != 0) {
                                                    ImageView imageView = (ImageView) powerLayout.getChildAt(0);
                                                    ((AnimationDrawable) imageView.getDrawable()).stop();
                                                    imageView.setImageDrawable(null);
                                                    powerLayout.removeAllViews();
                                                }

                                                ParticleSystem ps = new ParticleSystem(NewPuzzleActivity.this, 100, R.drawable.particle_3, 1000);
                                                ps.setScaleRange(0.4f, 0.8f).setSpeedModuleAndAngleRange(0, 0.5f, 0, 360).setFadeOut(100).setRotationSpeedRange(5, 10).oneShot(row, 20, new LinearInterpolator());
                                                ParticleSystem ps1 = new ParticleSystem(NewPuzzleActivity.this, 100, R.drawable.particle_star1, 1000);
                                                ps1.setScaleRange(0.1f, 0.3f).setSpeedModuleAndAngleRange(0, 0.5f, 0, 360).setFadeOut(100).setRotationSpeedRange(5, 10).oneShot(row, 20, new LinearInterpolator());


                                                if (!isBot) {
                                                    String doneRow = "CompleteRow:" + ownRows.get(ownInverstNum);
                                                    Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), doneRow.getBytes(),
                                                            googleServiceApi.getRoomId(), googleServiceApi.getOppId());
                                                }
                                                final IconRoundCornerProgressBar progressBar = (IconRoundCornerProgressBar) findViewById(R.id.timer_progress);
                                                int bonusScore = (int) progressBar.getProgress();
                                                updateOwnScore(bonusScore);
                                                image_counter++;
                                                ownInverstNum--;
                                                comboStart = true;
                                                createImage(ownChunkedImages, image_counter);
                                            } else {
                                                compare.setEnabled(true);
                                                view.setEnabled(true);
                                            }
                                        }
                                    });
                                    animSet.start();
                                } else
                                    compare.setAlpha(1.0f);
                            } catch (Exception e) {
                                if (compare != null) {
                                    compare.setEnabled(true);
                                    compare.setAlpha(1.0f);
                                    selectView.setVisibility(View.INVISIBLE);
                                }
                                view.setEnabled(true);
                            }
                            break;
                    }
                    return true;
                }
            });
        }
    }

    public void endGameDialogue() {
        if (isBot)
            bot.stopBot();
        MyCustomDialog dialog = new MyCustomDialog();
        dialog.show(getFragmentManager(), null);
        Log.d("timeForChallenger", timeForChallenger + "");
        opp_puzzle.removeAllViews();
        if (powerLayout.getChildCount() != 0)
            powerLayout.removeAllViews();
        blackScreen.setAlpha(1);
        clearStatusLayout();
        ownClearMemory();
        oppClearMemory();
        clearGeneralImageMemory();
    }

    @Override
    public void onBackPressed() {

        UniversalDialogFragment dialog = new UniversalDialogFragment(NewPuzzleActivity.this, "surrender");
        dialog.show(getFragmentManager(), null);
    }

    public void surrender() {
        if (!isBot) {
            String sendWinDialog = "YOUWIN:" + "";
            byte[] sendWinDialogString = sendWinDialog.getBytes();
            Games.RealTimeMultiplayer.sendUnreliableMessage(googleServiceApi.getmGoogleApiClient(), sendWinDialogString,
                    googleServiceApi.getRoomId(), googleServiceApi.getOppId());
        }
        //this should be "opp"!!!!
        state.setFirst_complete("own");
        endGameDialogue();
    }

    public String getOppFireBaseID() {
        return googleServiceApi.getOppFirebaseId();
    }

    public String getOppInstagramId() {
        return googleServiceApi.getOppInstagramID();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBot && bot != null) {
            bot.stopBot();
            googleServiceApi.clearPuzzle();
        }
        if (mSoundManager != null) {
            mSoundManager.cancel();
            mSoundManager = null;
        }
        fusion.com.soicalrpgpuzzle.MusicManager.getInstance().stopMusic();
        state.resetAll();
        state = null;
        googleServiceApi = null;
        //oppProfilePicture.recycle();
        imageObj = null;
        power = null;
        Log.d("onDestroy", "ondestroynewpuzzle");
        System.gc();
    }
}




