package fusion.com.soicalrpgpuzzle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Type;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyandroidanimations.library.RotationAnimation;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.PlayerBuffer;
import com.google.android.gms.games.Players;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;


/**
 * Created by Pandora on 6/8/2016.
 */
public class MyCustomDialog extends DialogFragment {

    GlobalState state;
    float width, height;
    NewPuzzleActivity puzzleActivity;
    View view;
    RelativeLayout dialog,reserveInv,rankInv;
    String oppFireBaseID, oppInstagramID;
    DatabaseReference mDatabase;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPref;
    int totalScore = 0, s, a, b, c, d = 0;
    int rewardScore = 0, extraBonus = 0;
    ImageView backBtn;

    public MyCustomDialog() {

    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPref = getActivity().getSharedPreferences(GoogleServiceApi.SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        state = (GlobalState) this.getActivity().getApplicationContext();

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setOwnerActivity(getActivity());
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        showImmersive(getFragmentManager());
    }

    private void showImmersive(FragmentManager manager) {
        // It is necessary to call executePendingTransactions() on the FragmentManager
        // before hiding the navigation bar, because otherwise getWindow() would raise a
        // NullPointerException since the window was not yet created.
        manager.executePendingTransactions();

        // Copy flags from the activity, assuming it's fullscreen.
        // It is important to do this after show() was called. If we would do this in onCreateDialog(),
        // we would get a requestFeature() error.
        getDialog().getWindow().getDecorView().setSystemUiVisibility(
                getActivity().getWindow().getDecorView().getSystemUiVisibility()
        );

        // Make the dialogs window focusable again
        getDialog().getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        puzzleActivity = (NewPuzzleActivity) getActivity();
        String[] dimension = state.getDimensionScreen().split(",");
        height = Float.valueOf(dimension[1]);
        width = Float.valueOf(dimension[0]);
        oppFireBaseID = puzzleActivity.getOppFireBaseID();
        oppInstagramID = puzzleActivity.getOppInstagramId();

        view = inflater.inflate(R.layout.activity_win_dialog, container, false);

        setCancelable(false);

        RelativeLayout star_result = (RelativeLayout) view.findViewById(R.id.star_result);
        star_result.setScaleX(0);
        star_result.setScaleY(0);

        dialog = (RelativeLayout) view.findViewById(R.id.dialog);
        final ImageView gameEnd = new ImageView(puzzleActivity);
        gameEnd.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.game_end)).getBitmap(), (int) width, (int) height / 5, true));
        dialog.addView(gameEnd);
        gameEnd.setY(height / 2 - height / 10);
        gameEnd.setScaleX(0);
        gameEnd.setScaleY(0);
        gameEnd.animate().setStartDelay(500).setInterpolator(new OvershootInterpolator()).scaleX(1).scaleY(1).setDuration(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                ((BitmapDrawable)gameEnd.getDrawable()).getBitmap().recycle();
                gameEnd.setImageDrawable(null);
                dialog.removeView(gameEnd);
                puzzleActivity.relativeLayout.removeAllViews();
                puzzleActivity.relativeLayout.setBackgroundColor(Color.GRAY);
                totalScore();
            }
        }).start();

        backBtn = new ImageView(getActivity());
        backBtn.setScaleX(0);
        backBtn.setScaleY(0);
        return view;
    }

    private void totalScore() {
        int totalBonus, totalRow = puzzleActivity.howManyPerRow + 4;

        for (int i = 0; i < totalRow; i++) {
            totalBonus = (1000 * totalRow) + (500 * totalRow) - (200 * i);
            totalScore += (50 * totalRow) + ((totalBonus * i) / 25) + ((50 * totalRow * i) / 5);
            d += (50 * totalRow) + ((50 * totalRow) / 5);
        }
        Log.d("totalScore", totalScore + "");
        c = totalScore / 3;
        b = totalScore / 2;
        a = (int) (totalScore / 1.7f);
        s = (int) (totalScore / 1.3f);
        decideWinAndLose();
    }

    private void decideWinAndLose() {

        final ImageView ribbon = (ImageView) view.findViewById(R.id.ribbon);
        RelativeLayout star_result = (RelativeLayout) view.findViewById(R.id.star_result);
        ribbon.setY(ribbon.getY() + height / 14);
        ribbon.bringToFront();

        if (state.getFirst_complete().equals("own")) {
            Log.d(GoogleServiceApi.TAG, "winning condition!");
            //   updateData(true);
            final ImageView totalScore = new ImageView(getActivity());
            totalScore.setTag("totalScoreImage");
            totalScore.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.total_score)).getBitmap(), (int) (width / 2), (int) (height / 9.6f), true));
            totalScore.setScaleY(0);
            totalScore.setScaleX(0);
            final RelativeLayout winBackGround = (RelativeLayout) view.findViewById(R.id.winBackground);
            ViewGroup.LayoutParams winBackGroundLP = winBackGround.getLayoutParams();
            winBackGroundLP.height = (int) (height / 1.5f);
            winBackGroundLP.width = (int) (width - width / 10);
            winBackGround.addView(totalScore);
            totalScore.setX((width - width / 10) / 2 - width / 4);
            totalScore.setY(((height / 1.5f) / 2) - (height / 9.6f) / 2);
            ribbon.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.ribbon_win)).getBitmap(), (int) (width - width / 5), (int) height / 7, true));
            star_result.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    totalScore.animate().scaleY(1).scaleX(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            final TextView totalScore = new TextView(getActivity());
                            totalScore.setTag("totalScoreText");
                            totalScore.setTextSize((int) height / 38.4f);
                            Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "our_font.ttf");
                            totalScore.setTypeface(textFont);
                            totalScore.setTextColor(Color.WHITE);
                            winBackGround.addView(totalScore);
                            totalScore.setMinimumWidth((int) (width / 2));
                            totalScore.setGravity(Gravity.CENTER);
                            totalScore.setY(((height / 1.5f) / 2) + (height / 32));
                            totalScore.setX((width - width / 10) / 2 - width / 4);

                            final TextView own_score = (TextView) puzzleActivity.findViewById(R.id.own_score);
                            ValueAnimator value = new ValueAnimator();
                            //final int ownScore = Integer.parseInt(own_score.getText().toString());
                            final int ownScore = s + 1000;
                            value.setObjectValues(0, ownScore);
                            value.setDuration(500);
                            value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    totalScore.setText("" + (int) animation.getAnimatedValue());
                                }
                            });
                            value.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    comboCount(ownScore, winBackGround);
                                }
                            });
                            value.start();
                        }
                    }).start();
                }
            }).start();
        } else {
            //   updateData(false);
            final ImageView totalScore = new ImageView(getActivity());
            totalScore.setTag("totalScoreImage");
            totalScore.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.total_score)).getBitmap(), (int) (width / 2), (int) (height / 9.6f), true));
            totalScore.setScaleY(0);
            totalScore.setScaleX(0);
            final RelativeLayout winBackGround = (RelativeLayout) view.findViewById(R.id.winBackground);
            ViewGroup.LayoutParams winBackGroundLP = winBackGround.getLayoutParams();
            winBackGroundLP.height = (int) (height / 1.5f);
            winBackGroundLP.width = (int) (width - width / 10);
            winBackGround.addView(totalScore);
            totalScore.setX((width - width / 10) / 2 - width / 4);
            totalScore.setY(((height / 1.5f) / 2) - (height / 9.6f) / 2);
            ribbon.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.ribbon_lose)).getBitmap(), (int) (width - width / 5), (int) height / 7, true));
            star_result.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    totalScore.animate().scaleY(1).scaleX(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            final TextView totalScore = new TextView(getActivity());
                            totalScore.setTag("totalScoreText");
                            totalScore.setTextSize((int) height / 38.4f);
                            Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "our_font.ttf");
                            totalScore.setTypeface(textFont);
                            totalScore.setTextColor(Color.WHITE);
                            winBackGround.addView(totalScore);
                            totalScore.setMinimumWidth((int) (width / 2));
                            totalScore.setGravity(Gravity.CENTER);
                            totalScore.setY(((height / 1.5f) / 2) + (height / 32));
                            totalScore.setX((width - width / 10) / 2 - width / 4);

                            final TextView own_score = (TextView) puzzleActivity.findViewById(R.id.own_score);
                            ValueAnimator value = new ValueAnimator();
                            //final int ownScore = Integer.parseInt(own_score.getText().toString());
                            final int ownScore = s + 1000;
                            value.setObjectValues(0, ownScore);
                            value.setDuration(500);
                            value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    totalScore.setText("" + (int) animation.getAnimatedValue());
                                }
                            });
                            value.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    comboCount(ownScore, winBackGround);
                                }
                            });
                            value.start();
                        }
                    }).start();
                }
            }).start();
        }
    }

    private void comboCount(final int ownScore, final RelativeLayout winBackGround) {

        ImageView combo = new ImageView(getActivity());
        final TextView comboCount = new TextView(getActivity());
        comboCount.setVisibility(View.INVISIBLE);
        comboCount.setTextSize((int) height / 38.4f);
        Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "our_font.ttf");
        comboCount.setTypeface(textFont);
        comboCount.setTextColor(Color.WHITE);
        comboCount.setMinimumWidth((int) (width / 2));
        comboCount.setGravity(Gravity.CENTER);

        combo.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.combo_count)).getBitmap(), (int) (width / 2), (int) (height / 9.6f), true));
        winBackGround.addView(combo);
        winBackGround.addView(comboCount);
        combo.setX((width - width / 10) / 2 - width / 4);
        combo.setY(((height / 1.5f) / 2) + (height / 7.68f));

        comboCount.setY(((height / 1.5f) / 2) + (height / 7.68f) + (height / 12.8f));
        comboCount.setX((width - width / 10) / 2 - width / 4);

        combo.setScaleX(0);
        combo.setScaleY(0);

        combo.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).setDuration(250).withEndAction(new Runnable() {
            @Override
            public void run() {
                comboCount.setVisibility(View.VISIBLE);
                ValueAnimator value = new ValueAnimator();
                //final int ownScore = Integer.parseInt(own_score.getText().toString());
                value.setObjectValues(0, puzzleActivity.highestCombo);
                value.setDuration(500);
                value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        comboCount.setText("" + (int) animation.getAnimatedValue());
                    }
                });
                value.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        decideRank(ownScore, winBackGround);
                    }
                });
                value.start();
            }
        }).start();
    }

    private void decideRank(int ownScore, final RelativeLayout winBackGround) {

        final ImageView myRank = new ImageView(getActivity());
        winBackGround.addView(myRank);
        myRank.setScaleX(0);
        myRank.setScaleY(0);
        myRank.setY(height / 9.6f);
        myRank.setX((width - width / 10) / 2 - width / 8);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final RelativeLayout star_result = (RelativeLayout) view.findViewById(R.id.star_result);
                final ImageView ribbon = (ImageView) view.findViewById(R.id.ribbon);
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(winBackGround, "x", winBackGround.getX(), -winBackGround.getWidth());
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(ribbon, "x", ribbon.getX(), -ribbon.getWidth());
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(animator1).with(animator2);
                animatorSet.setInterpolator(new AnticipateInterpolator());
                animatorSet.setDuration(250).addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        for (int i =0;i<winBackGround.getChildCount();i++){
                            try{
                                ((BitmapDrawable)((ImageView)winBackGround.getChildAt(i)).getDrawable()).getBitmap().recycle();
                                ((ImageView)winBackGround.getChildAt(i)).setImageDrawable(null);
                            }catch (Exception e){
                                Log.d("Ignore_this","decide_rank");
                            }
                        }
                        winBackGround.removeAllViews();
                        ((BitmapDrawable)winBackGround.getBackground()).getBitmap().recycle();
                        winBackGround.setBackground(null);
                        ((BitmapDrawable)ribbon.getDrawable()).getBitmap().recycle();
                        ribbon.setImageDrawable(null);
                        star_result.removeView(winBackGround);
                        star_result.removeView(ribbon);
                        if (state.getFirst_complete().equals("own")) {
                            showOppProfile(star_result);
                        } else {
                            setInventory(star_result, null);
                        }
                    }
                });
                animatorSet.start();
            }
        };
        Log.d("ownScore", ownScore + "");

        if (ownScore <= d) {
            myRank.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.d_rank)).getBitmap(), (int) (width / 4), (int) width / 4, true));
            myRank.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).withEndAction(runnable).setDuration(250).start();
        } else if (ownScore <= c) {
            myRank.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.c_rank)).getBitmap(), (int) (width / 4), (int) width / 4, true));
            myRank.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).withEndAction(runnable).setDuration(250).start();
        } else if (ownScore <= b) {
            myRank.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.b_rank)).getBitmap(), (int) (width / 4), (int) width / 4, true));
            myRank.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).withEndAction(runnable).setDuration(250).start();
        } else if (ownScore <= a) {
            myRank.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.a_rank)).getBitmap(), (int) (width / 4), (int) width / 4, true));
            myRank.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).withEndAction(runnable).setDuration(250).start();
        } else if (ownScore <= s) {
            myRank.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.s_rank)).getBitmap(), (int) (width / 4), (int) width / 4, true));
            myRank.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).withEndAction(runnable).setDuration(250).start();
        } else if (ownScore <= totalScore) {
            myRank.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.ss_rank)).getBitmap(), (int) (width / 4), (int) width / 4, true));
            myRank.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).withEndAction(runnable).setDuration(250).start();
        }
        rewardScore = ownScore;
    }

    private void showOppProfile(final RelativeLayout star_result) {
        final ImageView oppProfile = new ImageView(getActivity());
        final ImageView circle = new ImageView(getActivity());
        circle.setTag("circle");
        oppProfile.setScaleX(0);
        oppProfile.setScaleY(0);
        oppProfile.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(puzzleActivity.oppProfilePicture, (int) (width / 2), (int) (width / 2), true), 0));
        star_result.addView(oppProfile);
        oppProfile.setY((height / 2) - (width / 4));
        oppProfile.setX((width / 2 - width / 4));
        oppProfile.animate().scaleY(1).scaleX(1).setDuration(250).withStartAction(new Runnable() {
            @Override
            public void run() {
                int[] circleDrawable = {R.drawable.blue_circle};

                circle.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, circleDrawable[0])).getBitmap(), (int) (width / 2 + width / 13.5), (int) (width / 2 + width / 13.5), true), 0));
                star_result.addView(circle);
                circle.setY((height / 2) - (width / 4) - (width / 27));
                circle.setX((width / 2 - width / 4) - (width / 27));
                ObjectAnimator animator = ObjectAnimator.ofFloat(circle, "rotation", 0, 360);
                animator.setDuration(1000).setInterpolator(new LinearInterpolator());
                animator.setRepeatCount(-1);
                animator.start();
            }
        }).withEndAction(new Runnable() {
            @Override
            public void run() {
                oppProfile.animate().translationY(height / 32).setDuration(250).setInterpolator(new AnticipateInterpolator()).withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        circle.animate().translationY(height / 32 - (width / 27)).setInterpolator(new AnticipateInterpolator()).setDuration(250).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                setInventory(star_result, oppProfile);
                                setGesture(oppProfile, star_result);
                            }
                        }).start();
                    }
                }).start();
            }
        }).start();
    }

    private void setGesture(ImageView oppProfile, final RelativeLayout star_result) {

        final ImageView circle = (ImageView) view.findViewWithTag("circle");
        final TextView oppName = (TextView) star_result.findViewWithTag("oppName");

        oppProfile.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {

            public void onSwipeLeft(final View v) {
                v.animate().translationX(-width / 2).scaleX(0).scaleY(0).setDuration(250).setInterpolator(new AnticipateInterpolator()).withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        circle.animate().translationX(-width / 2).scaleX(0).scaleY(0).setInterpolator(new AnticipateInterpolator()).setDuration(250).start();
                        oppName.animate().alpha(0).setDuration(250).start();
                    }
                }).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        ((BitmapDrawable)((ImageView)v).getDrawable()).getBitmap().recycle();
                        ((ImageView) v).setImageDrawable(null);
                        circle.animate().cancel();
                        ((BitmapDrawable)circle.getDrawable()).getBitmap().recycle();
                        circle.setImageDrawable(null);
                        star_result.removeView(circle);
                        star_result.removeView(v);
                        star_result.removeView(oppName);
                        extraBonus = (int) (rewardScore * 0.1f / 2);
                        Log.d("extraBonus", extraBonus + "");
                        rewardCoin(star_result);
                    }
                }).start();
            }

            public void onSwipeRight(final View v) {
                v.animate().translationX(width).scaleX(0).scaleY(0).setDuration(250).setInterpolator(new AnticipateInterpolator()).withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        circle.animate().translationX(width).scaleX(0).scaleY(0).setInterpolator(new AnticipateInterpolator()).setDuration(250).start();
                        oppName.animate().alpha(0).setDuration(250).start();
                    }
                }).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        ((BitmapDrawable)((ImageView)v).getDrawable()).getBitmap().recycle();
                        ((ImageView) v).setImageDrawable(null);
                        circle.animate().cancel();
                        ((BitmapDrawable)circle.getDrawable()).getBitmap().recycle();
                        circle.setImageDrawable(null);
                        star_result.removeView(circle);
                        star_result.removeView(v);
                        star_result.removeView(oppName);
                        extraBonus = (int) (rewardScore * 0.1f / 2);
                        Log.d("extraBonus", extraBonus + "");
                        rewardCoin(star_result);
                    }
                }).start();
            }
        });
    }

    private void updatePlayerData(boolean win) {

        // Dummy value (always LKS)
        oppFireBaseID = "MGQHa3PGGKQtwSAgzyWKZV9sL4B3";

        boolean duplicateChallenger = false;
        boolean duplicateInventory = false;

        if (win) {

            // Initialise inventory & challengerList
            ArrayList<String> challengerList = state.getPlayer().getChallengerList();
            ArrayList<String> inventory = state.getPlayer().getInventory();

            // check challenger List null & duplication
            if (challengerList == null) {
                challengerList = new ArrayList<>();
            } else {
                // Check not duplicate challenger
                for (int i = 0; i < challengerList.size(); i++) {
                    if (challengerList.get(i).equals("g05187252529177732990")) {
                        Log.d(GoogleServiceApi.TAG, "Duplicate challenger!");
                        duplicateChallenger = true;
                        break;
                    }
                }
            }

            // check inventory List null & duplication
            if (inventory == null) {
                inventory = new ArrayList<>();
            } else {
                // Check not duplicate challenger
                for (int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i).equals(oppFireBaseID)) {
                        Log.d(GoogleServiceApi.TAG, "Duplicate inventory!");
                        duplicateInventory = true;
                        break;
                    }
                }
            }

            // Update challenger list
            if (!duplicateChallenger) {
                challengerList.add("g05187252529177732990");
                state.getPlayer().setChallengerList(challengerList);

                Map<String, Object> challengerUpdates = new HashMap<>();
                challengerUpdates.put("/Player/" + state.getUid() + "/challengerList", state.getPlayer().getChallengerList());
                mDatabase.updateChildren(challengerUpdates);
            } else {
                // Update a message say duplicate challenger (UI PART)
            }

            // Update inventory list
            if (!duplicateInventory) {
                inventory.add(oppFireBaseID);
                state.getPlayer().setInventory(inventory);

                Map<String, Object> inventoryUpdates = new HashMap<>();
                inventoryUpdates.put("/Player/" + state.getUid() + "/inventory", state.getPlayer().getInventory());
                mDatabase.updateChildren(inventoryUpdates);
            } else {
                // Update a message say duplicate inventory (UI PART)
            }

        } else {
            // Lose condition
        }
        updateMatchDetails();
    }

    private void updateMatchDetails() {
        // Update Matches Details
        MatchDetails matchDetails = state.getMatchDetails();

        if (matchDetails == null) {
            matchDetails = new MatchDetails();
        } else {
            // check reach maximum match details = 10
            if (matchDetails.getOppFireBaseId().size() == 10) {
                matchDetails.getOppFireBaseId().remove(0);
                matchDetails.getCondition().remove(0);
                matchDetails.getDate().remove(0);
                matchDetails.getRank().remove(0);
                matchDetails.getScore().remove(0);
            }
        }

        // Condition
        List<Integer> condition = matchDetails.getCondition();
        condition.add(1);
        matchDetails.setCondition(condition);

        // Score
        List<Integer> score = matchDetails.getScore();
        score.add(9999);
        matchDetails.setScore(score);

        // oppFireBaseId
        List<String> oppFireBaseIdList = matchDetails.getOppFireBaseId();
        oppFireBaseIdList.add(oppFireBaseID);
        matchDetails.setOppFireBaseId(oppFireBaseIdList);

        // Rank
        List<String> rank = matchDetails.getRank();
        rank.add("SS");
        matchDetails.setRank(rank);

        // Date
        List<String> date = matchDetails.getDate();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        String datetime = dateformat.format(c.getTime());
        date.add(datetime);
        matchDetails.setDate(date);

        // Firebase Update
        state.setMatchDetails(matchDetails);
        mDatabase.child("MatchDetails").child(state.getUid()).setValue(matchDetails);
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

    private void setInventory(final RelativeLayout star_result, final View oppImage) {

        final TextView oppName = new TextView(getActivity());
        oppName.setTag("oppName");
        star_result.addView(oppName);

        rankInv = new RelativeLayout(getActivity());
        reserveInv = new RelativeLayout(getActivity());

        rankInv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        reserveInv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        star_result.addView(rankInv);
        star_result.addView(reserveInv);

        ViewGroup.LayoutParams lp = rankInv.getLayoutParams();
        lp.width = (int) (width - (width / 27));
        lp.height = (int) (height / 5.0526f);

        lp = reserveInv.getLayoutParams();
        lp.width = (int) (width - (width / 27));
        lp.height = (int) (height / 5.0526f);

        rankInv.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rank_inv));
        reserveInv.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.reserve_inv));
        ImageView rankName = new ImageView(getActivity());
        ImageView reserve = new ImageView(getActivity());
        rankName.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.rank_novice)).getBitmap(), (int) (width - width / 5), (int) (height / 7.68f), true));
        reserve.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.reserve1)).getBitmap(), (int) (width - width / 5), (int) (height / 7.68f), true));
        star_result.addView(rankName);
        star_result.addView(reserve);

        rankInv.setX(-width);
        rankInv.setY(height / 2 + height / 96);
        reserveInv.setX(width);
        reserveInv.setY(height - height / 5.0526f - height / 96);
        rankName.setY(height / 2 - height / 14);
        rankName.setX(width / 2 - (width - width / 5) / 2);
        reserve.setY(height / 2 + height / 5.0526f + height / 28 - height / 48);
        reserve.setX(width / 2 - (width - width / 5) / 2);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(rankInv, "x", -width, width / 54);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(reserveInv, "x", width, width / 54);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator1).with(animator2);
        animatorSet.setDuration(250).setInterpolator(new OvershootInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                int padding = (int) height / 48;
                final ImageView resEmptySlot = new ImageView(getActivity());
                resEmptySlot.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.white_circle)).getBitmap(), (int) (height / 7.68), (int) (height / 7.68), true));
                reserveInv.addView(resEmptySlot);
                resEmptySlot.setPadding(padding, padding, padding, padding);

                ObjectAnimator animator2 = ObjectAnimator.ofFloat(resEmptySlot, "rotation", 0, 360);
                animator2.setDuration(new Random().nextInt(1000) + 500);
                animator2.setInterpolator(new LinearInterpolator());
                animator2.setRepeatCount(-1);
                animator2.start();

                final ImageView circle = (ImageView) view.findViewWithTag("circle");

                oppName.setTextSize(22);
                Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "our_font.ttf");
                oppName.setTypeface(textFont);
                oppName.setTextColor(Color.WHITE);
                oppName.setMinimumWidth((int) (width));
                oppName.setGravity(Gravity.CENTER);
                oppName.setY(height / 32 + width / 2 + height / 32);
                oppName.setText(puzzleActivity.state.getOppName());

                rankInv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rankInv.setEnabled(false);
                        reserveInv.setEnabled(false);
                        //setMenuButton();
                    }
                });

                reserveInv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reserveInv.setEnabled(false);
                        rankInv.setEnabled(false);
                        oppImage.animate().scaleY(0).scaleX(0).setInterpolator(new AnticipateInterpolator()).setDuration(250).withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                circle.animate().scaleX(0).scaleY(0).setInterpolator(new AnticipateInterpolator()).setDuration(250).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        circle.animate().cancel();
                                    }
                                }).start();
                            }
                        }).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                star_result.removeView(oppName);
                                ImageView reserveSlot = (ImageView) reserveInv.getChildAt(0);
                                ImageView oppPic = new ImageView(getActivity());
                                reserveInv.addView(oppPic);
                                reserveSlot.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.blue_circle)).getBitmap(), (int) (height / 7.68), (int) (height / 7.68), true));
                                oppPic.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(puzzleActivity.oppProfilePicture, (int) (height / 7.68 - width / 27), (int) (height / 7.68 - width / 27), true), 0));
                                int padding = (int) (height / 48 + width / 54);
                                oppPic.setPadding(padding, padding, padding, padding);
                                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, reserveSlot.getWidth() / 2, reserveSlot.getHeight() / 2);
                                scaleAnimation.setDuration(500);
                                scaleAnimation.setInterpolator(new OvershootInterpolator());
                                reserveSlot.startAnimation(scaleAnimation);
                                rewardCoin(star_result);
                            }
                        }).start();
                    }
                });
            }
        });
        animatorSet.start();
    }

    private void rewardCoin(final RelativeLayout star_result) {
        star_result.removeView(backBtn);
        final TextView coin = new TextView(getActivity());
        ImageView reward = new ImageView(getActivity());
        reward.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.reward)).getBitmap(), (int) (width - width / 5), (int) (height / 7.68f), true));
        star_result.addView(reward);
        reward.setY(height / 192);
        reward.setX(width / 2 - (width - width / 5) / 2);
        coin.setTextSize(50);
        Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "JELLYBELLY.ttf");
        coin.setTypeface(textFont);
        coin.setTextColor(Color.WHITE);
        coin.setMinimumWidth((int) (width));
        coin.setGravity(Gravity.CENTER);
        star_result.addView(coin);
        coin.setY(height / 8);
        int rewardCoin;
        if (state.getFirst_complete().equals("own")) {
            rewardCoin = (int) (rewardScore * 0.1f);
        } else {
            rewardCoin = (int) (rewardScore * 0.01f);
        }

        ValueAnimator value = new ValueAnimator();
        value.setObjectValues(0, rewardCoin);
        value.setDuration(500);
        value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                coin.setText("" + (int) animation.getAnimatedValue());
            }
        });
        value.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (extraBonus != 0) {
                    final ImageView bonus = new ImageView(getActivity());
                    bonus.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(puzzleActivity, R.drawable.bonus)).getBitmap(), (int) (width / 2), (int) (height / 9.6f), true));
                    bonus.setScaleX(0);
                    bonus.setScaleY(0);
                    star_result.addView(bonus);
                    bonus.setY(height / 4);
                    bonus.setX(width);
                    bonus.animate().translationX(width / 2 - width / 4).scaleX(1).scaleY(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            bonus.animate().translationX(-width / 2).scaleX(0).scaleY(0).setDuration(250).setInterpolator(new AnticipateInterpolator()).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    ((BitmapDrawable)bonus.getDrawable()).getBitmap().recycle();
                                    bonus.setImageDrawable(null);
                                    star_result.removeView(bonus);
                                    int currentCoin = Integer.parseInt(coin.getText().toString());
                                    int reward = currentCoin + extraBonus;
                                    ValueAnimator value = new ValueAnimator();
                                    value.setObjectValues(currentCoin, reward);
                                    value.setDuration(500);
                                    value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            coin.setText("" + (int) animation.getAnimatedValue());
                                        }
                                    });
                                    value.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    int totalCoinEarn = Integer.parseInt(coin.getText().toString());

                                                    updatePlayerData(true);

                                                    removeCircle();
                                                    for (int i=0;i<star_result.getChildCount();i++){
                                                        try{
                                                            ((BitmapDrawable)(star_result.getChildAt(i)).getBackground()).getBitmap().recycle();
                                                        }catch (Exception e){
                                                            Log.d("Ignore_this","rewardCoin");
                                                        }
                                                    }
                                                    star_result.removeAllViews();
                                                    getDialog().cancel();
                                                    getDialog().dismiss();
                                                    getActivity().finish();
                                                }
                                            }, 300);
                                        }
                                    });
                                    value.start();
                                }
                            }).setStartDelay(100).start();
                        }
                    }).start();
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int totalCoinEarn = Integer.parseInt(coin.getText().toString());

                            updatePlayerData(true);

                            removeCircle();
                            star_result.removeAllViews();
                            getDialog().cancel();
                            getDialog().dismiss();
                            getActivity().finish();
                        }
                    }, 300);
                }
            }
        });
        value.start();
    }

    private void removeCircle(){
        for (int i=0;i<reserveInv.getChildCount();i++){
            reserveInv.getChildAt(i).animate().cancel();
            reserveInv.removeAllViews();
        }

        for (int i=0;i<rankInv.getChildCount();i++){
            rankInv.getChildAt(i).animate().cancel();
            rankInv.removeAllViews();
        }
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
        paint.setColor(Color.TRANSPARENT);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("onDestroy", "ondestroycustomdialog");

        System.gc();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
