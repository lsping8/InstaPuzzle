package fusion.com.soicalrpgpuzzle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.PuffInAnimation;
import com.easyandroidanimations.library.ScaleInAnimation;
import com.easyandroidanimations.library.SlideInAnimation;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.games.Games;
import com.google.firebase.database.FirebaseDatabase;
import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.plattysoft.leonids.ParticleSystem;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Pandora on 8/18/2016.
 */
public class TutorialActivity extends Activity {

    GlobalState state;
    RelativeLayout relativeLayout, opp_puzzle, blackScreen, powerLayout, color_row, backGround, instructionRelative, secondScreen;
    LinearLayout own_puzzle;
    float height, width;
    HashMap<String, ArrayList<Integer>> ownRandomPick;
    ArrayList<Bitmap> ownChunkedImages;
    ArrayList<Integer> ownRows, temp = new ArrayList<>();
    int chunkNumbers, howManyRows, howManyPerRow, tokenCount, ownInverstNum, ownGiveUpCounter, textCounter, temp_counter;
    Handler handler = new Handler();
    Context context;
    Runnable runnable;
    CharSequence text;
    boolean tempBool;
    Thread thread;

    public TutorialActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_puzzle);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        state = ((GlobalState) getApplicationContext());

        String[] dimension = state.getDimensionScreen().split(",");
        height = Float.valueOf(dimension[1]);
        width = Float.valueOf(dimension[0]);

        RelativeLayout backGround = (RelativeLayout) findViewById(R.id.backGround);
        backGround.setBackgroundResource(R.drawable.color_background3);
        secondScreen = (RelativeLayout) findViewById(R.id.secondScreen);
        own_puzzle = (LinearLayout) findViewById(R.id.drag_row);
        opp_puzzle = (RelativeLayout) findViewById(R.id.opponent_screen);
        relativeLayout = (RelativeLayout) findViewById(R.id.puzzle_activity);
        blackScreen = (RelativeLayout) findViewById(R.id.gesture);
        powerLayout = (RelativeLayout) findViewById(R.id.bottom_screen);
        color_row = (RelativeLayout) findViewById(R.id.color_row);
        context = this.getApplicationContext();

        final ImageView imageView = new ImageView(TutorialActivity.this.getApplicationContext());
        RelativeLayout.LayoutParams imageLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageLP.setMargins(0, (int) height / 400, (int) width / 200, 0);
        imageView.setLayoutParams(imageLP);
        imageView.setId(R.id.selection);
        relativeLayout.addView(imageView);

        instructionRelative = new RelativeLayout(context);
        instructionRelative.setVisibility(View.INVISIBLE);
        relativeLayout.addView(instructionRelative);
        ViewGroup.LayoutParams lp = instructionRelative.getLayoutParams();
        lp.height = (int) (height / 2);
        lp.width = (int) width;
        instructionRelative.setBackgroundResource(R.drawable.record_back);
        instructionRelative.setX(width);
        instructionRelative.setScaleY(0);

        TextView instruction = new TextView(context);
        instructionRelative.addView(instruction);
        instruction.setTag("instruction");
        instruction.setTextSize(25);
        instruction.setTextColor(Color.WHITE);
        instruction.setMaxWidth((int) (width - 80));
        instruction.setX(width / 27);
        instruction.setY(height / 96);

        lp = own_puzzle.getLayoutParams();
        lp.height = (int) (height / 2);
        lp = powerLayout.getLayoutParams();
        lp.height = (int) (height / 2);
        lp = blackScreen.getLayoutParams();
        lp.height = (int) (height / 2);
        blackScreen.setAlpha(0);
        createColorRow();
        startTutorial();
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

    private void startTutorial() {
        showPreview();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void showPreview() {
        final int size = (int) width / 216;
        final RoundCornerProgressBar time = new RoundCornerProgressBar(context, null);
        time.setRadius((int) width / 50);
        time.setProgressColor(Color.parseColor("#FF00FF"));
        time.setPadding((int) height / 192);
        time.setY(height / 2);

        final RelativeLayout textRelative = new RelativeLayout(context);
        textRelative.setTag("textRelative");
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

        for (int i = 0; i < 8; i++) {
            final ImageView imageView = new ImageView(context);
            imageView.setVisibility(View.INVISIBLE);
            imageView.setPadding(size, size, size, size);
            imageView.setImageBitmap(roundEdge(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.tutorial_picture)).getBitmap(), (int) (width / 2.5f), (int) (width / 2.5f), true)));
            previewPic.addView(imageView);
            imageView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageView.setVisibility(View.VISIBLE);
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, (width / 2.5f) / 2, (width / 2.5f) / 2);
                    scaleAnimation.setDuration(300);
                    scaleAnimation.setInterpolator(new OvershootInterpolator());
                    imageView.startAnimation(scaleAnimation);

                    //new ScaleInAnimation(imageView).setInterpolator(new OvershootInterpolator()).animate();
                }
            }, 150 * (i + 1));
        }
        previewPic.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    previewPic.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    previewPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] drawable = {R.drawable.count_1, R.drawable.count_2, R.drawable.count_3, R.drawable.count_4, R.drawable.count_5, R.drawable.count_6
                        , R.drawable.count_7, R.drawable.count_8};
                int ownPictureCounter = 0, rowCounter = 0;
                for (int i = 0; i < 8; i++) {
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
        });

        ObjectAnimator animeTime = ObjectAnimator.ofFloat(time, "progress", 0, 100);
        animeTime.setDuration(1050);
        animeTime.start();
        animeTime.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator animeTime = ObjectAnimator.ofFloat(time, "progress", time.getProgress(), 0);
                animeTime.setDuration(100);
                setInstruction("reviewPhase", animeTime);
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
                                instructionRelative.animate().scaleX(0).translationY(-height).setInterpolator(new AnticipateInterpolator()).setDuration(250).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        previewPic.removeAllViews();
                                        relativeLayout.removeView(previewPic);
                                        relativeLayout.removeView(time);
                                        relativeLayout.removeView(textRelative);
                                        startEverything();
                                        System.gc();
                                    }
                                }).start();
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

    private void setInstruction(String phase, final ObjectAnimator animeTime) {
        final TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
        switch (phase) {
            case "reviewPhase":
                instructionRelative.bringToFront();
                instructionRelative.setY(height / 3);
                instructionRelative.setVisibility(View.VISIBLE);
                instructionRelative.animate().translationX(0).scaleY(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        text = "Welcome to THE GAME,as you notice there are 8 pictures at the back of this instruction.At the left corner of each picture attached a number.";
                        textCounter = 0;
                        tempBool = true;
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (textCounter <= text.length()) {
                                    instruction.setText(text.subSequence(0, textCounter));
                                    textCounter++;
                                    handler.postDelayed(runnable, 30);
                                }
                            }
                        };
                        handler.post(runnable);
                        relativeLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (instruction.getText().length() != text.length()) {
                                    handler.removeCallbacks(runnable);
                                    instruction.setText(text.subSequence(0, text.length()));
                                } else if (tempBool) {
                                    tempBool = false;
                                    instruction.setText("");
                                    text = "Those number are the sequence of the picture that you will play.The goal of this game is to beat your opponent by solving 4 picture before your opponent and earn token to rank up.";
                                    textCounter = 0;
                                    handler.post(runnable);
                                } else {
                                    relativeLayout.setEnabled(false);
                                    instruction.animate().alpha(0).setDuration(250).withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            instruction.setText("");
                                            instruction.setAlpha(1);
                                            instructionRelative.animate().scaleY(0).translationX(-width).setDuration(250).setInterpolator(new AnticipateInterpolator()).withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final RelativeLayout textRelative = (RelativeLayout) relativeLayout.findViewWithTag("textRelative");
                                                    temp_counter = 0;
                                                    for (int i = 0; i < textRelative.getChildCount(); i++) {
                                                        final ImageView number = (ImageView) textRelative.getChildAt(i);
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                ScaleAnimation scaleAnimation = new ScaleAnimation(1, 3, 1, 3, number.getX() + number.getWidth() / 2, number.getY() + number.getHeight() / 2);
                                                                scaleAnimation.setDuration(300);
                                                                scaleAnimation.setInterpolator(new OvershootInterpolator());
                                                                number.startAnimation(scaleAnimation);
                                                                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                                                    @Override
                                                                    public void onAnimationStart(Animation animation) {
                                                                        relativeLayout.setOnClickListener(null);
                                                                    }

                                                                    @Override
                                                                    public void onAnimationEnd(Animation animation) {
                                                                        ScaleAnimation scaleAnimation = new ScaleAnimation(3, 1, 3, 1, number.getX() + number.getWidth() / 2, number.getY() + number.getHeight() / 2);
                                                                        scaleAnimation.setDuration(300);
                                                                        scaleAnimation.setInterpolator(new AnticipateInterpolator());
                                                                        number.startAnimation(scaleAnimation);
                                                                        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                                                            @Override
                                                                            public void onAnimationStart(Animation animation) {

                                                                            }

                                                                            @Override
                                                                            public void onAnimationEnd(Animation animation) {
                                                                                temp_counter++;
                                                                                if (temp_counter == textRelative.getChildCount()) {
                                                                                    instructionRelative.animate().translationX(0).scaleY(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            text = "In this phase you are given 5 second to plan out which picture you are going to play. Let us get into the main gameplay.";
                                                                                            handler.post(runnable);
                                                                                            relativeLayout.setEnabled(true);
                                                                                            relativeLayout.setOnClickListener(new View.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(View v) {
                                                                                                    if (instruction.getText().length() != text.length()) {
                                                                                                        handler.removeCallbacks(runnable);
                                                                                                        instruction.setText(text.subSequence(0, text.length()));
                                                                                                    } else {
                                                                                                        relativeLayout.setEnabled(false);
                                                                                                        instruction.animate().alpha(0).setDuration(250).withEndAction(new Runnable() {
                                                                                                            @Override
                                                                                                            public void run() {
                                                                                                                instruction.setText("");
                                                                                                                instruction.setAlpha(1);
                                                                                                                animeTime.start();
                                                                                                            }
                                                                                                        }).start();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }).start();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onAnimationRepeat(Animation animation) {

                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onAnimationRepeat(Animation animation) {

                                                                    }
                                                                });
                                                            }
                                                        }, 150 * i);
                                                    }
                                                }
                                            }).start();
                                        }
                                    }).start();
                                }
                            }
                        });
                    }
                }).start();
                break;

            case "puzzle":
                instructionRelative.setY(0);
                instructionRelative.bringToFront();
                textCounter = 0;
                tempBool = true;
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (textCounter <= text.length()) {
                            instruction.setText(text.subSequence(0, textCounter));
                            textCounter++;
                            handler.postDelayed(runnable, 30);
                        }else{
                            textCounter = 0;
                        }
                    }
                };
                break;
        }
    }

    private void startEverything() {
        customSetup();
        generateSequenceHashMap();
        own_puzzle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                new Tutorial();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    own_puzzle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    own_puzzle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void generateSequenceHashMap() {
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

    private void customSetup() {
        CustomSetup setup = new CustomSetup("4x4");
        chunkNumbers = setup.getChunkNumbers();
        howManyPerRow = setup.getHowManyPerRow();
        howManyRows = setup.getHowManyRows();
        ownInverstNum = setup.getOwnInverstNum();
        ownRows = setup.getOwnRows();
        ownRandomPick = new HashMap<>();
        ownChunkedImages = new ArrayList<>();
        ownGiveUpCounter = 4;
        tokenCount = 1;
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

    public class Tutorial {

        int backGroundCounter, checker, comboCount, image_counter, highestCombo, colorSelected, completePictureCount;
        LinearLayout row;
        boolean comboStart, readyState, swapActivate, pictureCompleted;
        ObjectAnimator timer;
        Thread ownThread;

        public Tutorial() {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ownSplitImage();
                }
            }).start();

            image_counter = 0;
            highestCombo = 0;
            completePictureCount = 0;
            ObjectAnimator colorAnim = ObjectAnimator.ofFloat(color_row, "x", color_row.getX(), 0);
            //ObjectAnimator oppAnim = ObjectAnimator.ofFloat(opp_puzzle, "y", -opp_puzzle.getHeight(), opp_puzzle.getY());
            ObjectAnimator alpha = ObjectAnimator.ofFloat(blackScreen, "alpha", 0, 1);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(colorAnim).before(alpha);
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
                            blackScreen.animate().alpha(0).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    startMatch(imageView);
                                }
                            }).start();
                        }
                    }).start();
                }
            });
            animatorSet.start();
        }

        private void startMatch(final ImageView imageView) {
            blackScreen.animate().alpha(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    blackScreen.removeView(imageView);
                    ParticleSystem ps = new ParticleSystem(TutorialActivity.this, 1, R.drawable.start1, 800);
                    ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).oneShot(own_puzzle, 1);
                    tutorialBegin();
                }
            }).start();
        }

        private void tutorialBegin() {
            setDrag(own_puzzle, "", null);
            initializeLinearPuzzle();
            text = "Please try and solve the puzzle by dragging the chunk image below.";
            setInstruction("puzzle", null);
            createImage(ownChunkedImages, image_counter);
            ViewGroup.LayoutParams lp = instructionRelative.getLayoutParams();
            lp.height = (int) (height / 2 - (height / 2) / 7);
            instructionRelative.animate().scaleX(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    handler.post(runnable);
                }
            }).start();
        }

        private void tutorial2Begin() {
            final TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
            instructionRelative.animate().scaleY(0).scaleX(0).setDuration(250).setInterpolator(new AnticipateInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    instruction.setText("");
                    createProfileIndicator();
                    ViewGroup.LayoutParams lp = instructionRelative.getLayoutParams();
                    lp.height = (int) (height / 2);
                    instructionRelative.setY(height / 2);
                    instructionRelative.animate().scaleY(1).scaleX(1).setInterpolator(new OvershootInterpolator()).setDuration(250).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            handler.removeCallbacks(runnable);
                            text = "Now, this bar is an indicator timer for your combo.When the time runs out, your combo will break and reset again.";
                            final ImageView pointer = new ImageView(context);
                            pointer.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.arrow_left)).getBitmap(), (int) (width / 7.2), (int) (width / 7.2), true));
                            secondScreen.addView(pointer);
                            pointer.setY(secondScreen.getHeight() / 2 - width / 10.8f);
                            pointer.setX(width / 10.8f);
                            handler.post(runnable);
                            arrowAnimation(pointer,width / 10.8f,(width / 10.8f + width/54),(secondScreen.getHeight() / 2 - width / 10.8f),(secondScreen.getHeight() / 2 - width / 10.8f));
                            instructionRelative.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                                    if (instruction.getText().length() != text.length()) {
                                        handler.removeCallbacks(runnable);
                                        instruction.setText(text.subSequence(0, text.length()));
                                    } else {
                                        handler.removeCallbacks(runnable);
                                        instructionRelative.setOnClickListener(null);
                                        secondScreen.removeView(pointer);
                                        explainStatusBar();
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }).start();
        }

        private void tutorial3Begin(){

            instructionRelative.animate().scaleX(0).translationY(0).setDuration(250).setInterpolator(new AnticipateInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams lp = instructionRelative.getLayoutParams();
                    lp.height = (int) (height / 2 - (height / 2) / 7);
                    instructionRelative.setY(0);
                    instructionRelative.animate().scaleX(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                            instruction.setText( "In order to skip a picture, slide the slider bar without touching the picture.");
                            initializeLinearPuzzle();
                            createColor();
                            createImage(ownChunkedImages, image_counter);
                        }
                    }).start();
                }
            }).start();
        }

        private void tutorial4Begin(){
            createColor();
            handler.removeCallbacks(runnable);
            text = "This counter will drop every time you skip,give up or solve the picture wrongly.When the counter reaches 0,slide the bar wil directly surrender the game and your opponent" +
                    "will win the match so use it wisely.";
            final ImageView pointer = new ImageView(context);
            pointer.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.arrow_left)).getBitmap(), (int) (width / 7.2), (int) (width / 7.2), true));
            relativeLayout.addView(pointer);
            pointer.setY(relativeLayout.getHeight()/2 - height/12.8f);
            pointer.setX(150);
            handler.post(runnable);
            arrowAnimation(pointer,150,(150 + width/54),(relativeLayout.getHeight()/2 - height/12.8f),(relativeLayout.getHeight()/2 - height/12.8f));
            powerLayout.bringToFront();
            powerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                    if (instruction.getText().length() != text.length()) {
                        handler.removeCallbacks(runnable);
                        instruction.setText(text.subSequence(0, text.length()));
                    } else {
                        instruction.setText("");
                        handler.removeCallbacks(runnable);
                        relativeLayout.removeView(pointer);
                        blackScreen.setAlpha(0);
                        color_row.removeAllViews();
                        tutorial5Begin();
                    }
                }
            });
        }

        private void tutorial5Begin(){
            relativeLayout.removeView(powerLayout);
            own_puzzle.bringToFront();
            own_puzzle.setEnabled(true);
            TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
            instruction.setText("Touch the image and slide the slider bar to give up.");
            setDrag(own_puzzle, "", null);
            initializeLinearPuzzle();
            createColor();
            createImage(ownChunkedImages, image_counter);
        }

        private void tutorial6Begin(){
            text = "Give up will break you combo but skip will not.Give up is only use when the image is too difficult to solve and proceed to the next image.";
            handler.post(runnable);
            blackScreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                    if (instruction.getText().length() != text.length()) {
                        handler.removeCallbacks(runnable);
                        instruction.setText(text.subSequence(0, text.length()));
                    } else {
                        handler.removeCallbacks(runnable);
                        blackScreen.setOnClickListener(null);
                        tutorial7Begin();
                    }
                }
            });
        }

        private void tutorial7Begin(){
            text = "Again to win the game you will need to solve 4 picture faster than your opponent and get your score as high as possible. The next rank you will unlock Challenger." +
                    "Other players are able to steal your token away by playing the same match like now but archive more higher score than you.";
            handler.post(runnable);
            powerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                    if (instruction.getText().length() != text.length()) {
                        handler.removeCallbacks(runnable);
                        instruction.setText(text.subSequence(0, text.length()));
                    } else {
                        handler.removeCallbacks(runnable);
                        blackScreen.setOnClickListener(null);
                        blackScreen.setAlpha(0);
                        endGameDialogue();
                    }
                }
            });

        }

        private void explainStatusBar(){
            handler.removeCallbacks(runnable);
            text = "This number indicate how many picture had completed";
            final ImageView pointer = new ImageView(context);

            pointer.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.arrow_right)).getBitmap(), (int) (width / 7.2), (int) (width / 7.2), true));
            secondScreen.addView(pointer);
            pointer.setY(height/17.4545f);
            pointer.setX(width - width/5.4f);
            arrowAnimation(pointer,(width - width/5.4f),(width - width/5.4f - width/54),height/17.4545f,height/17.4545f);
            handler.post(runnable);
            instructionRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                    if (instruction.getText().length() != text.length()) {
                        handler.removeCallbacks(runnable);
                        instruction.setText(text.subSequence(0, text.length()));
                    } else {
                        handler.removeCallbacks(runnable);
                        instructionRelative.setOnClickListener(null);
                        secondScreen.removeView(pointer);
                        explainScore();
                    }
                }
            });
        }

        private void explainScore(){
            handler.removeCallbacks(runnable);
            text = "This is score";
            final ImageView pointer = new ImageView(context);
            pointer.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.arrow_right)).getBitmap(), (int) (width / 7.2), (int) (width / 7.2), true));
            secondScreen.addView(pointer);
            pointer.setY(230);
            pointer.setX(width - 350);
            handler.post(runnable);
            arrowAnimation(pointer,(width - 350),(width - 350 - width/54),230,230);
            instructionRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                    if (instruction.getText().length() != text.length()) {
                        handler.removeCallbacks(runnable);
                        instruction.setText(text.subSequence(0, text.length()));
                    } else {
                        handler.removeCallbacks(runnable);
                        instructionRelative.setOnClickListener(null);
                        secondScreen.removeView(pointer);
                        explainOppScreen();
                    }
                }
            });
        }

        private void explainOppScreen(){
            ObjectAnimator oppAnim = ObjectAnimator.ofFloat(opp_puzzle, "y", -opp_puzzle.getHeight(), opp_puzzle.getY());
            oppAnim.setDuration(1000).setInterpolator(new LinearInterpolator());
            oppAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    text = "This is your opponent screen";
                    handler.post(runnable);
                    instructionRelative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                            if (instruction.getText().length() != text.length()) {
                                handler.removeCallbacks(runnable);
                                instruction.setText(text.subSequence(0, text.length()));
                            } else {
                                handler.removeCallbacks(runnable);
                                instructionRelative.setOnClickListener(null);
                                tutorial3Begin();
                            }
                        }
                    });
                }
            });
            opp_puzzle.setVisibility(View.VISIBLE);
            oppAnim.start();
        }

        private void arrowAnimation(final View view,final float fromX,final float toX,final float fromY,final float toY){
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view,"x",fromX,toX);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(view,"y",fromY,toY);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(animator1).with(animator2);
            animatorSet.setDuration(500).setInterpolator(new OvershootInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ObjectAnimator animator1 = ObjectAnimator.ofFloat(view,"x",toX,fromX);
                    ObjectAnimator animator2 = ObjectAnimator.ofFloat(view,"y",toY,fromY);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(animator1).with(animator2);
                    animatorSet.setDuration(500).setInterpolator(new AnticipateInterpolator());
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            arrowAnimation(view,fromX,toX,fromY,toY);
                        }
                    });
                    animatorSet.start();
                }
            });
            animatorSet.start();
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

        private void createProfileIndicator() {

            Bitmap ownProfilePicture;

            if (state.getPlayer().getGooglePlayProfile() != null) {
                ownProfilePicture = state.getPlayer().getGooglePlayProfile();
            } else {
                ownProfilePicture = state.getPlayer().getInstaProfilePic();
            }

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

            Bitmap bitmap = getCircleBitmap(ownProfilePicture, 0);
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
            opp_prifile.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.rsz_sohai)).getBitmap(), (int) (width / 8), (int) (width / 8), true), 0));
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

        public void createImage(final ArrayList<Bitmap> chunkedImages, final int i) {

            final IconRoundCornerProgressBar progressBar = (IconRoundCornerProgressBar) findViewById(R.id.timer_progress);

            /*if (comboStart && !readyState)
                manageCombo("prepare");*/

            if (i != howManyRows) {
                int check;
                checker = 0;
                final String whichRow = "hashmap_num" + ownRows.get(ownInverstNum);

                do {
                    check = 0;
                    Collections.shuffle(ownRandomPick.get(whichRow));
                    for (int j = 0; j < ownRandomPick.get(whichRow).size() - 1; j++) {

                        if (ownRandomPick.get(whichRow).get(j + 1) - ownRandomPick.get(whichRow).get(j) != 1) {
                            check++;
                        }
                    }
                } while (check != ownRandomPick.get(whichRow).size() - 1);

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
                    final ImageView image = new ImageView(context);
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
                                    .animate();
                        }
                    }, 150 * j);
                    row.addView(image);
                    setTouch(image);
                    setDrag(image, whichRow, row);
                }
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
                                        /*if (progressBar.getProgress() != 0)
                                            comboStart = true;
                                        if (comboStart)
                                            timer.start();*/
                                        dragLayout();
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        }
                    }, 150 * j);
                }
            }
        }

        private void ownSplitImage() {
            //For the number of rows and columns of the grid to be displayed
            int rows, cols;

            //For height and width of the small image chunks
            int ownChunkHeight, ownChunkWidth;

            //Getting the scaled bitmap of the source image
            /*BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = drawable.getBitmap();*/
            Bitmap ownScaledBitmap;
            ownScaledBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.tutorial_picture)).getBitmap(), (int) (width), (int) (height / 2), true);

            //rows = cols = (int) Math.sqrt(chunkNumbers);
            rows = howManyRows;
            cols = howManyPerRow;

            Log.d("ownScaled", ownScaledBitmap.getHeight() + "");
            Log.d("ownScaled", ownScaledBitmap.getWidth() + "");
            Log.d("ownScaled", rows + "");
            Log.d("ownScaled", cols + "");

            ownChunkHeight = (ownScaledBitmap.getHeight() / rows - ((int) height / 400));
            ownChunkWidth = (ownScaledBitmap.getWidth() / cols - ((int) width / 210));

            int ownxCoord = 0, ownyCoord = 0;

            for (int x = 0; x < rows; x++) {
                ownxCoord = 0;
                for (int y = 0; y < cols; y++) {
                    ownChunkedImages.add(roundEdge(Bitmap.createBitmap(ownScaledBitmap, ownxCoord, ownyCoord, ownChunkWidth, ownChunkHeight)));
                    ownxCoord += ownChunkWidth;
                }
                ownyCoord += ownChunkHeight;
            }
            ownScaledBitmap.setHasAlpha(false);
            ownScaledBitmap.recycle();
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

        private void dragLayout() {
            text = "Nice, now try to drag any row up or down to swap and put in the exact place.";
            handler.post(runnable);
            backGroundCounter = 0;
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
                        if (backGroundCounter == 0) {
                            handler.removeCallbacks(runnable);
                            backGroundCounter++;
                            createColor();
                            text = "In this phase,user will have to determine weather the picture is completed.When you think it is complete just slide the middle slider to the right";
                            handler.post(runnable);
                        }
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

        public void setDrag(final View image, final String whichRow, final LinearLayout row) {
            image.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(final View view, DragEvent dragEvent) {
                    final View compare = (View) dragEvent.getLocalState();
                    LinearLayout parent = (LinearLayout) findViewById(R.id.drag_row);
                    ImageView selectView = (ImageView) findViewById(R.id.selection);
                    RelativeLayout sliderView = (RelativeLayout) findViewById(R.id.slider);
                    final int imageHeight = selectView.getHeight();

                    switch (dragEvent.getAction()) {

                        case DragEvent.ACTION_DRAG_STARTED:
                            try {
                                sliderView.setEnabled(false);
                            } catch (Exception e) {
                                Log.d("Ignore_this", "sliderView");
                            }
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
                            try {
                                sliderView.setEnabled(true);
                            } catch (Exception e) {
                                Log.d("Ignore_this", "sliderView");
                            }
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
                                                /*if (comboStart) {
                                                    comboStart = false;
                                                    manageCombo("stop");
                                                    tokenCount++;
                                                }*/
                                                //comboCount++;
                                                for (int i = 0; i < row.getChildCount(); i++) {
                                                    row.getChildAt(i).setEnabled(false);
                                                }

                                                ParticleSystem ps = new ParticleSystem(TutorialActivity.this, 100, R.drawable.particle_3, 1000);
                                                ps.setScaleRange(0.4f, 0.8f).setSpeedModuleAndAngleRange(0, 0.5f, 0, 360).setFadeOut(100).setRotationSpeedRange(5, 10).oneShot(row, 20, new LinearInterpolator());
                                                ParticleSystem ps1 = new ParticleSystem(TutorialActivity.this, 100, R.drawable.particle_star1, 1000);
                                                ps1.setScaleRange(0.1f, 0.3f).setSpeedModuleAndAngleRange(0, 0.5f, 0, 360).setFadeOut(100).setRotationSpeedRange(5, 10).oneShot(row, 20, new LinearInterpolator());

                                                /*final IconRoundCornerProgressBar progressBar = (IconRoundCornerProgressBar) findViewById(R.id.timer_progress);
                                                int bonusScore = (int) progressBar.getProgress();
                                                updateOwnScore(bonusScore);*/
                                                image_counter++;
                                                ownInverstNum--;
                                                //comboStart = true;
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
                        /*if (readyState && comboStart) {
                            readyState = false;
                            timer.start();
                        }*/
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

        private void updateOwnScore(int bonusScore) {

            int[] comboDrawable = {R.drawable.combo_0, R.drawable.combo_1, R.drawable.combo_2, R.drawable.combo_3, R.drawable.combo_4, R.drawable.combo_5, R.drawable.combo_6,
                    R.drawable.combo_7, R.drawable.combo_8, R.drawable.combo_9};

            final TextView own_score = (TextView) findViewById(R.id.own_score);
            int textNum = Integer.parseInt(own_score.getText().toString());

            ValueAnimator value = new ValueAnimator();
            value.setObjectValues(textNum, (textNum + (50 * howManyPerRow) + ((bonusScore * comboCount) / 25) + ((50 * howManyPerRow * comboCount) / 5)));

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
            giveUpCounter.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, giveUpCounte[ownGiveUpCounter])).getBitmap(), (int) width / 10, (int) width / 10, true));
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
                                    sliderView.setX(width - sliderView.getWidth());
                                    color_row.setOnTouchListener(null);
                                    giveUpCounter.setImageDrawable(null);
                                    sliderView.setBackground(null);
                                    color_row.removeAllViews();

                                    if (pictureCompleted && swapActivate) {

                                        if (powerLayout.getChildCount() != 0) {
                                            ImageView imageView = (ImageView) powerLayout.getChildAt(0);
                                            ((AnimationDrawable) imageView.getDrawable()).stop();
                                            imageView.setImageDrawable(null);
                                        }
                                        final IconRoundCornerProgressBar progressBar = (IconRoundCornerProgressBar) findViewById(R.id.timer_progress);
                                        //int bonusScore = (int) progressBar.getProgress();
                                        /*if (comboStart) {
                                            comboStart = false;
                                            manageCombo("stop");
                                        }
                                        comboCount++;
                                        updateOwnScore(bonusScore);*/
                                        powerLayout.removeAllViews();
                                        swapActivate = false;
                                        backGroundCounter = 0;

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
                                                                if (backGroundCounter != 0)
                                                                    prepareNextImage();
                                                            }
                                                        }
                                                    });
                                                    animator.start();
                                                }
                                            }, 150 * (own_puzzle.getChildCount() - i));
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

        private void failPicture() {
            /*if (comboStart)
                timer.end();*/
            handler.removeCallbacks(runnable);
            text = "Too bad, the picture is not correct.The time taken to solve this picture will be recorded and used in the game feature.So try your best to solve it as fast as possible";
            handler.post(runnable);
            color_row.removeAllViews();
            clearOwnPuzzle();
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
            ParticleSystem ps = new ParticleSystem(TutorialActivity.this, 1, R.drawable.fail, 500);
            ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.7f, 0.7f).oneShot(blackScreen, 1);
            try {
                ownThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            powerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                    if (instruction.getText().length() != text.length()) {
                        handler.removeCallbacks(runnable);
                        instruction.setText(text.subSequence(0, text.length()));
                    } else {
                        handler.removeCallbacks(runnable);
                        blackScreen.setAlpha(0);
                        tutorial2Begin();
                    }
                }
            });
        }

        public void prepareNextImage() {
            completePictureCount = 0;
            handler.removeCallbacks(runnable);
            text = "Nice!,you have completed the picture.The time taken to solve this picture will be recorded and used in the game feature.So try your best to solve it as fast as possible";
            handler.post(runnable);
            readyState = true;
            clearOwnPuzzle();
            ownThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    pictureCompleted = false;
                    ownClearMemory();
                    if (completePictureCount != 1) {
                        restoreVariable();
                        ownSplitImage();
                    } else {
                        //endGameDialogue();
                    }
                }
            });
            ownThread.start();

            blackScreen.setAlpha(1);
            ParticleSystem ps = new ParticleSystem(TutorialActivity.this, 1, R.drawable.nice2, 500);
            ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.7f, 0.7f).oneShot(blackScreen, 1);
            try {
                ownThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            powerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                    if (instruction.getText().length() != text.length()) {
                        handler.removeCallbacks(runnable);
                        instruction.setText(text.subSequence(0, text.length()));
                    } else {
                        handler.removeCallbacks(runnable);
                        blackScreen.setAlpha(0);
                        tutorial2Begin();
                    }
                }
            });
        }

        private void giveUp() {
            /*if (comboStart)
                timer.end();*/
            color_row.removeAllViews();
            clearOwnPuzzle();
            ownClearMemory();
            if ((ownGiveUpCounter + 1) == 0) {

                //this should be "opp"!!!!
                //endGameDialogue();
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
            ParticleSystem ps = new ParticleSystem(TutorialActivity.this, 1, R.drawable.give_up, 500);
            ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.7f, 0.7f).oneShot(blackScreen, 1);
            try {
                ownThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            powerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView instruction = (TextView) relativeLayout.findViewWithTag("instruction");
                    if (instruction.getText().length() != text.length()) {
                        handler.removeCallbacks(runnable);
                        instruction.setText(text.subSequence(0, text.length()));
                    } else {
                        handler.removeCallbacks(runnable);
                        powerLayout.setOnClickListener(null);
                        tutorial6Begin();
                    }
                }
            });
        }

        private void skip() {
            ownGiveUpCounter = 3;
            color_row.removeAllViews();
            clearOwnPuzzle();
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
            ParticleSystem ps = new ParticleSystem(TutorialActivity.this, 1, R.drawable.skip, 500);
            ps.setFadeOut(200).setSpeedModuleAndAngleRange(0.1f, 0.1f, 270, 270).setScaleRange(0.7f, 0.7f).oneShot(blackScreen, 1);
            try {
                ownThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tutorial4Begin();
        }

        private void restoreVariable() {
            CustomSetup setup = new CustomSetup("4x4");
            pictureCompleted = false;
            image_counter = 0;
            chunkNumbers = setup.getChunkNumbers();
            ownInverstNum = setup.getOwnInverstNum();
            howManyPerRow = setup.getHowManyPerRow();
            howManyRows = setup.getHowManyRows();
            ownRows = setup.getOwnRows();
            ownRandomPick = new HashMap<>();
            ownChunkedImages = new ArrayList<>();
            generateSequenceHashMap();
        }

        private void ownClearMemory() {
            ownRows.clear();
            temp.clear();
            ownRandomPick.clear();
            clearOwnChunkedImages();
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
    }

    public void endGameDialogue() {
        MyCustomDialog dialog = new MyCustomDialog();
        dialog.show(getFragmentManager(), null);
        opp_puzzle.removeAllViews();
        if (powerLayout.getChildCount() != 0)
            powerLayout.removeAllViews();
        blackScreen.setAlpha(1);
        clearStatusLayout();
        ownClearMemory();
    }

    private void clearStatusLayout(){
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.status);
        relativeLayout.removeAllViews();
    }

    private void ownClearMemory() {

        ownRows.clear();
        temp.clear();
        ownRandomPick.clear();
        clearOwnChunkedImages();
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
}
