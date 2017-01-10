package fusion.com.soicalrpgpuzzle;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.images.ImageManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;


public class StartMatch extends Fragment implements IOnFocusListenable {

    ImageView start_button, gameLogo;
    ViewPager viewPager;
    TextView name;
    GlobalState state;
    GoogleServiceApi googleServiceApi;
    GridLayout gridLayout, support;
    GeneralImage generalImage;
    TabLayout tabLayout;
    RelativeLayout allLogos, startMatch, top_layout, statusLayout;
    float height, width;
    View v;
    boolean finding;
    ImageView option_icon;
    WaitingRoom waitingRoom;
    String skillname;

    public StartMatch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        state = ((GlobalState) getActivity().getApplicationContext());
        googleServiceApi = state.getmGoogleApi();
        generalImage = state.getGeneralImage();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_start_match, container, false);

        String[] dimension = state.getDimensionScreen().split(",");
        height = Float.valueOf(dimension[1]);
        width = Float.valueOf(dimension[0]);

        startMatch = (RelativeLayout) v.findViewById(R.id.startMatch);
        gameLogo = (ImageView) v.findViewById(R.id.profile_pic);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        allLogos = (RelativeLayout) getActivity().findViewById(R.id.all_logos);
        start_button = (ImageView) v.findViewById(R.id.findingOpp);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        top_layout = (RelativeLayout) getActivity().findViewById(R.id.top_bar);

        gameLogo.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.company)).getBitmap(), (int) (width), (int) (height / 6.4), true));
        gameLogo.setY(height/8);
        start_button.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.play_button)).getBitmap(), (int) (width / 2), (int) (height / 8), true));
        start_button.setY(height/2 - height/16);
        setStar();
        setOptionIcon();

        String[] supportSkill = {"dif-1", "revealNextRow", "revealSolution", "autoSolve"};
        finding = false;
        start_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    start_button.animate().scaleY(0.9f).scaleX(0.9f).setDuration(100).start();
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    start_button.animate().scaleY(1).scaleX(1).setDuration(100).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            if (!finding) {
                                finding = true;
                                viewPager.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        return true;
                                    }
                                });
                                start_button.animate().translationY(height/2 + height / 4).setDuration(250).withStartAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        gameLogo.animate().alpha(0).setDuration(250).start();
                                        start_button.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.cancel_btn)).getBitmap(), (int) (width / 2), (int) (height / 8), true));
                                        ObjectAnimator anim1 = ObjectAnimator.ofFloat(tabLayout, "y", tabLayout.getY(), tabLayout.getY() + tabLayout.getHeight());
                                        ObjectAnimator anim2 = ObjectAnimator.ofFloat(allLogos, "y", allLogos.getY(), allLogos.getY() + (tabLayout.getHeight() * 2));
                                        ObjectAnimator anim4 = ObjectAnimator.ofFloat(top_layout, "y", top_layout.getY(), top_layout.getY() - top_layout.getHeight());
                                        ObjectAnimator anim5 = ObjectAnimator.ofFloat(option_icon, "x", option_icon.getX(), width);
                                        AnimatorSet animatorSet = new AnimatorSet();
                                        animatorSet.play(anim1).with(anim2).with(anim4).with(anim5);
                                        animatorSet.start();
                                    }
                                }).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        lookingForPlayer();
                                    }
                                }).start();
                            } else {
                                finding = false;
                                viewPager.setOnTouchListener(null);
                                gameLogo.animate().alpha(1).setDuration(250).start();
                                start_button.animate().translationY(height/2 - height/16).setDuration(250).withStartAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (waitingRoom != null)
                                            waitingRoom.cancelGame();
                                        startMatch.removeView(statusLayout);
                                        statusLayout.animate().translationX(width).setDuration(150).start();
                                        start_button.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.play_button)).getBitmap(), (int) (width / 2), (int) (height / 8), true));
                                        ObjectAnimator anim1 = ObjectAnimator.ofFloat(tabLayout, "y", tabLayout.getY(), tabLayout.getY() - tabLayout.getHeight());
                                        ObjectAnimator anim2 = ObjectAnimator.ofFloat(allLogos, "y", allLogos.getY(), allLogos.getY() - (tabLayout.getHeight() * 2));
                                        ObjectAnimator anim4 = ObjectAnimator.ofFloat(top_layout, "y", top_layout.getY(), top_layout.getY() + top_layout.getHeight());
                                        ObjectAnimator anim5 = ObjectAnimator.ofFloat(option_icon, "x", option_icon.getX(), width - width / 6);
                                        AnimatorSet animatorSet = new AnimatorSet();
                                        animatorSet.play(anim1).with(anim2).with(anim4).with(anim5);
                                        animatorSet.start();
                                    }
                                }).start();
                            }
                        }
                    }).start();
                }
                return true;
            }
        });
        return v;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(GoogleServiceApi.TAG, "startmatchwindosfocus");
    }

    public void restorePage(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finding = false;
                viewPager.setOnTouchListener(null);
                gameLogo.setAlpha(1f);
                start_button.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.play_button)).getBitmap(), (int) (width / 2), (int) (height / 8), true));
                start_button.setScaleX(1);
                start_button.setScaleY(1);
                start_button.setY(height/2 - height/16);
                start_button.setEnabled(true);
                startMatch.removeView(statusLayout);
                tabLayout.setY(tabLayout.getY() - tabLayout.getHeight());
                allLogos.setY(allLogos.getY() - (tabLayout.getHeight() * 2));
                top_layout.setY(top_layout.getY() + top_layout.getHeight());
                option_icon.setX(width - width / 6);
            }
        },500);
    }

    private void lookingForPlayer() {
        statusLayout = new RelativeLayout(getActivity());
        statusLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.start_match_bottompanel));
        startMatch.addView(statusLayout);
        ViewGroup.LayoutParams layoutParams = statusLayout.getLayoutParams();
        layoutParams.width = (int) width;
        layoutParams.height = (int) height / 4;
        statusLayout.setY(height / 2 - height / 8);
        statusLayout.setX(width);
        statusLayout.animate().translationX(0).setDuration(250).withEndAction(new Runnable() {
            @Override
            public void run() {
                ImageView lookingForPlayer = new ImageView(getActivity());
                lookingForPlayer.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.looking_for_player)).getBitmap(), (int) (width - width / 54), (int) height / 8, true));
                statusLayout.addView(lookingForPlayer);
                lookingForPlayer.setY((height / 4) / 2 - (height / 16));
                lookingForPlayer.setX(width / 2 - (width - width / 54) / 2);
                state.setSkillName(skillname, 100, false);
                waitingRoom = new WaitingRoom(StartMatch.this, state);
                waitingRoom.startFindingOpp(statusLayout, start_button, startMatch);
            }
        }).start();
    }

    private void setOptionIcon() {
        option_icon = new ImageView(getActivity());
        option_icon.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.option_icon)).getBitmap(), (int) (width / 6), (int) (width / 6), true));
        startMatch.addView(option_icon);
        option_icon.setX(width - width / 6);
        option_icon.setY(height / 15);
        ObjectAnimator anim = ObjectAnimator.ofFloat(option_icon, "rotation", 0, 360);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setDuration(5000);
        anim.setInterpolator(new LinearInterpolator());
        //anim.start();
        option_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.7f, 1, 0.7f, (width - width / 6) + (width / 12), (height / 15 + height / 30));
                scaleAnimation.setDuration(150);
                option_icon.startAnimation(scaleAnimation);
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1, 0.7f, 1, (width - width / 6) + (width / 12), (height / 15 + height / 30));
                        scaleAnimation.setDuration(150);
                        scaleAnimation.setInterpolator(new OvershootInterpolator());
                        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                optionLayout();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        option_icon.startAnimation(scaleAnimation);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

    private void optionLayout() {

    }

    private void setStar() {
        LinearLayout starLayout = new LinearLayout(getActivity());
        starLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(getActivity());
            star.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.start_match_star)).getBitmap(), (int) width / 8, (int) width / 8, true));
            starLayout.addView(star);
        }
        starLayout.setY(height / 9.6f);
    }

    private void setClick(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getParent() == gridLayout)
                    state.setSkillName(v.getTag().toString(), 50, false);
                if (v.getParent() == support)
                    state.setSkillName(v.getTag().toString(), 50, true);
                WaitingRoom waitingRoom = new WaitingRoom(StartMatch.this, state);
                waitingRoom.startFindingOpp(statusLayout, start_button, startMatch);
            }
        });
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


    @Override
    public void onStart() {
        super.onStart();
        Log.d(GoogleServiceApi.TAG, "startmatchonstart");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
