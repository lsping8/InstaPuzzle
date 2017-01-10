package fusion.com.soicalrpgpuzzle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.media.Image;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TreeSet;

/**
 * Created by Pandora on 6/9/2016.
 */
public class SuperPower extends Activity {

    RelativeLayout powerLayout;
    LinearLayout drag_row, currentRow;
    Handler handler = new Handler();
    Random random = new Random();
    Context context;
    NewPuzzleActivity puzzleActivity;
    boolean firstTouch = true;
    boolean skillActivated = false;
    View previousView, getView;
    RelativeLayout ownIndicator, oppIndicator;
    ArrayList<Integer> resultStorage;
    ArrayList<Float> currentRowX;
    ArrayList<Bitmap> bitmap;
    ArrayList<Integer> whichRowInt;
    Thread thread = null;
    boolean xAxis_OK = false, yAxis_OK = false, supportSkill = false;
    ObjectAnimator objectAnimator;
    int compareTime, counter;
    float width, height;
    boolean threadRunning = true;


    public SuperPower(LinearLayout linearLayout, RelativeLayout relativeLayout, Context puzzleContext, NewPuzzleActivity newPuzzleActivity, float width, float height) {
        this.drag_row = linearLayout;
        powerLayout = relativeLayout;
        this.context = puzzleContext;
        this.puzzleActivity = newPuzzleActivity;
        this.width = width;
        this.height = height;
    }

    public String startGamePhase(String skillName, String gameMODE, float percentage) {
        String gameMode = null;
        if (random.nextFloat() * 100f <= percentage) {
            switch (skillName) {

                case "blackOut":
                    powerLayout.setAlpha(0.9f);
                    powerLayout.setBackgroundColor(Color.parseColor("#000000"));
                    puzzleActivity.skillActiavtedAnimation("own", "activate");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            powerLayout.setAlpha(0);
                            puzzleActivity.skillActiavtedAnimation("own", "de-activate");
                        }
                    }, 10000);
                    break;

                case "dif+1":
                    int mode = Integer.parseInt(gameMODE.substring(0, 1));
                    if (mode != 6)
                        mode++;
                    String Mode = mode + "";
                    gameMode = Mode + "x" + Mode;
                    break;

                case "numberFloat":
                    gameMode = "NotNull";
                    final int[] number = {R.drawable.neon_1, R.drawable.neon_2, R.drawable.neon_3, R.drawable.neon_4, R.drawable.neon_5, R.drawable.neon_6, R.drawable.neon_7, R.drawable.neon_8, R.drawable.neon_9};
                    final int spawnNumber = random.nextInt(number.length) + 1;
                    final ArrayList<Float> yPoint = new ArrayList<>();
                    currentRowX = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        currentRowX.add((width / 10) * i);
                        yPoint.add((width / 11) * i);
                    }
                    Collections.shuffle(yPoint);
                    Collections.shuffle(currentRowX);
                    for (int i = 0; i < spawnNumber; i++) {
                        final int curr = i;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ImageView imageView = new ImageView(context);
                                imageView.setImageBitmap(scaleBitmap((int) width / 5, (int) width / 5, number[curr]));
                                imageView.setTag("number" + curr);
                                setClickNumber(imageView);
                                powerLayout.addView(imageView);
                                imageView.setPivotX(width / 10);
                                imageView.setPivotY(width / 10);

                                imageView.setX(currentRowX.get(curr));
                                imageView.setY(yPoint.get(curr));

                                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, currentRowX.get(curr) + width / 10, yPoint.get(curr) + width / 10);
                                scaleAnimation.setInterpolator(new OvershootInterpolator());
                                scaleAnimation.setDuration(500);
                                imageView.startAnimation(scaleAnimation);
                            }
                        }, 100 * i);
                    }
                    puzzleActivity.skillActiavtedAnimation("own", "activate");
                    break;
                //-----------------------------SUPPORT SKILL-------------------------------------------//
                case "dif-1":
                    mode = Integer.parseInt(gameMODE.substring(0, 1));
                    if (mode != 4)
                        mode--;
                    Mode = mode + "";
                    gameMode = Mode + "x" + Mode;
                    break;

                case "animationSpeedup":

                    break;
            }
        }
        return gameMode;
    }

    public void rowCreatedPhase(String skillName, final LinearLayout currentRow, float percentage, String whichRow) {
        this.currentRow = currentRow;
        if (skillActivated)
            return;
        else
            skillActivated = true;
        currentRowX = new ArrayList<>();
        for (int i = 0; i < currentRow.getChildCount(); i++) {
            currentRowX.add(currentRow.getChildAt(i).getX());
        }
        if (random.nextFloat() * 100f <= percentage) {
            switch (skillName) {
                case "blackRow":
                    final ImageView imageView = new ImageView(context);
                    powerLayout.addView(imageView);
                    imageView.setAlpha(0.9f);
                    ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                    lp.height = currentRow.getHeight();
                    lp.width = currentRow.getWidth();
                    imageView.setY(currentRow.getY() + currentRow.getHeight() / 8);
                    imageView.setImageResource(R.drawable.animation_block);
                    imageView.setScaleX(2.5f);
                    imageView.setScaleY(2.7f);
                    for (int i = 0; i < currentRow.getChildCount(); i++) {
                        currentRow.getChildAt(i).setEnabled(false);
                    }
                    final ImageView slider = (ImageView) puzzleActivity.color_row.getChildAt(0);
                    slider.setEnabled(false);
                    ((AnimationDrawable) imageView.getDrawable()).start();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < currentRow.getChildCount(); i++) {
                                currentRow.getChildAt(i).setEnabled(true);
                            }
                            ((AnimationDrawable) imageView.getDrawable()).stop();
                            imageView.setImageResource(0);
                            powerLayout.removeView(imageView);
                            slider.setEnabled(true);
                            puzzleActivity.skillActiavtedAnimation("own", "de-activate");
                        }
                    }, 5000);
                    skillActivateEffect();
                    break;

                case "rotateAll":

                    skillActivateEffect();
                    break;

                case "flipAll":
                    ArrayList<Boolean> booleanStorage = new ArrayList<>();
                    ArrayList<Boolean> booleanStorage2 = new ArrayList<>();

                    for (int i = 0; i < currentRow.getChildCount(); i++) {
                        booleanStorage.add(true);
                        booleanStorage2.add(true);
                        if (random.nextBoolean()) {
                            currentRow.getChildAt(i).animate().rotationXBy(180).start();
                            booleanStorage.set(i, false);
                        }
                        if (random.nextBoolean()) {
                            currentRow.getChildAt(i).animate().rotationYBy(180).start();
                            booleanStorage2.set(i, false);
                        }
                        setSwipe(currentRow.getChildAt(i), whichRow, booleanStorage, booleanStorage2);
                    }
                    skillActivateEffect();
                    break;

                case "holdRow":
                    final ImageView imageView2 = new ImageView(context);
                    powerLayout.addView(imageView2);
                    ViewGroup.LayoutParams lp2 = imageView2.getLayoutParams();
                    lp2.height = currentRow.getHeight();
                    lp2.width = currentRow.getWidth();
                    imageView2.setImageResource(R.drawable.animation_lightning);
                    imageView2.setY(currentRow.getY());
                    imageView2.setScaleX(1.5f);
                    for (int i = 0; i < currentRow.getChildCount(); i++)
                        currentRow.getChildAt(i).setEnabled(false);
                    setRowTouch(imageView2);
                    ((AnimationDrawable) imageView2.getDrawable()).start();
                    skillActivateEffect();
                    break;

                //-----------------------------SUPPORT SKILL-------------------------------------------//

                case "revealNextRow":
                    if (counter != currentRow.getChildCount() - 1) {
                        counter++;
                        final LinearLayout linearLayout = new LinearLayout(context);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                        linearLayout.setAlpha(0);
                        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        int marGins;
                        if ((int) width / 500 < 1)
                            marGins = 1;
                        else
                            marGins = (int) width / 500;
                        lp3.setMargins(marGins, (int) height / 400, marGins, 0);
                        for (int i = 0; i < currentRow.getChildCount(); i++) {
                            int position = whichRowInt.get(i);
                            ImageView imageView3 = new ImageView(context);
                            imageView3.setImageBitmap(bitmap.get(position));
                            linearLayout.addView(imageView3);
                            imageView3.setLayoutParams(lp3);
                        }
                        powerLayout.addView(linearLayout);
                        linearLayout.setY(currentRow.getY() - currentRow.getHeight() - (int) height / 400);
                        linearLayout.animate().alpha(1).setDuration(1000).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                linearLayout.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        powerLayout.removeView(linearLayout);
                                    }
                                }).start();
                            }
                        }).start();
                    } else {
                        bitmap = new ArrayList<>();
                        whichRowInt = new ArrayList<>();
                        counter = 0;
                    }
                    break;
            }
        }
    }

    public boolean touchPhase(String skillName, final int chunkWidth, View view, ArrayList<Integer> temp, String whichRow, float percentage) {
        boolean result = false;

        if (random.nextFloat() * 100f <= percentage) {
            try {
                switch (skillName) {
                    case "box":
                        int randomChild;
                        if (firstTouch || powerLayout.getChildCount() == 0) {
                            ImageView imageView = new ImageView(context);
                            imageView.setImageResource(R.drawable.animation_box);
                            powerLayout.addView(imageView);
                            ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                            lp.height = currentRow.getHeight();
                            lp.width = chunkWidth;
                            imageView.setScaleY(1.5f);
                            imageView.setScaleX(1.5f);
                            ((AnimationDrawable) imageView.getDrawable()).start();
                            firstTouch = false;
                            do {
                                randomChild = random.nextInt(currentRow.getChildCount());
                                getView = currentRow.getChildAt(randomChild);
                                imageView.setY(currentRow.getY());
                                imageView.setX(getView.getX());
                                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, getView.getX() + chunkWidth / 2, currentRow.getY() + currentRow.getHeight() / 2);
                                scaleAnimation.setInterpolator(new OvershootInterpolator());
                                scaleAnimation.setDuration(500);
                                imageView.startAnimation(scaleAnimation);
                            } while (getView == view);
                            getView.setEnabled(false);
                            skillActivateEffect();
                        } else {
                            final ImageView imageView = (ImageView) powerLayout.getChildAt(0);
                            do {
                                randomChild = random.nextInt(currentRow.getChildCount());
                                getView = currentRow.getChildAt(randomChild);
                            } while (getView == view);
                            getView.setEnabled(false);
                            ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0, getView.getX() + chunkWidth / 2, currentRow.getY() + currentRow.getHeight() / 2);
                            scaleAnimation.setInterpolator(new AnticipateInterpolator());
                            scaleAnimation.setDuration(250);
                            imageView.startAnimation(scaleAnimation);
                            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    imageView.setX(getView.getX());
                                    imageView.setY(currentRow.getY());
                                    ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, getView.getX() + chunkWidth / 2, currentRow.getY() + currentRow.getHeight() / 2);
                                    scaleAnimation.setInterpolator(new OvershootInterpolator());
                                    scaleAnimation.setDuration(500);
                                    imageView.startAnimation(scaleAnimation);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        }
                        break;

                    case "invisibleTouch":
                        for (int i = 0; i < currentRow.getChildCount(); i++) {
                            if (currentRow.getChildAt(i) != view) {
                                currentRow.getChildAt(i).animate().alpha(0).setDuration(100).start();
                            }
                        }
                        skillActivateEffect();
                        break;

                    case "touchRandom":
                        if (!supportSkill) {
                            View view1 = null, view2 = null;
                            do {
                                getView = currentRow.getChildAt(random.nextInt(currentRow.getChildCount()));
                                if (view != getView) {
                                    if (view1 == null) {
                                        view1 = getView;
                                    } else
                                        view2 = getView;
                                }
                            } while (view1 == null || view2 == null);

                            ObjectAnimator point1 = ObjectAnimator.ofFloat(view1, "x", view1.getX(), view2.getX());
                            ObjectAnimator point2 = ObjectAnimator.ofFloat(view2, "x", view2.getX(), view1.getX());
                            AnimatorSet animSet = new AnimatorSet();
                            animSet.play(point1).with(point2);
                            animSet.setDuration(100);
                            puzzleActivity.create.editArrayList(temp, Integer.parseInt(view1.getTag().toString()), Integer.parseInt(view2.getTag().toString()), whichRow);
                            animSet.start();
                        }
                        supportSkill = false;
                        break;

                    //-----------------------------SUPPORT SKILL-------------------------------------------//

                    case "revealSolution":
                        int currentViewTag = Integer.parseInt(view.getTag().toString());
                        int viewTag;
                        TreeSet<Integer> treeSet = new TreeSet<>();
                        resultStorage = new ArrayList<>();

                        final ImageView imageView = new ImageView(context);
                        powerLayout.addView(imageView);
                        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                        lp.width = chunkWidth;
                        lp.height = currentRow.getHeight();
                        imageView.setImageResource(R.drawable.animation_solution_reveal);
                        imageView.setScaleX(1.2f);
                        imageView.setScaleY(1.1f);

                        for (int i = 0; i < currentRow.getChildCount(); i++) {
                            viewTag = Integer.parseInt(currentRow.getChildAt(i).getTag().toString());
                            treeSet.add(viewTag);
                        }
                        resultStorage.addAll(treeSet);
                        for (int i = 0; i < resultStorage.size(); i++) {
                            if (resultStorage.get(i) == currentViewTag) {
                                imageView.setX(currentRowX.get(i));
                                imageView.setY(currentRow.getY());
                                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, currentRowX.get(i), currentRow.getY());
                                scaleAnimation.setInterpolator(new OvershootInterpolator());
                                imageView.setAnimation(scaleAnimation);
                                ((AnimationDrawable) imageView.getDrawable()).start();
                            }
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imageView.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((AnimationDrawable) imageView.getDrawable()).stop();
                                        imageView.setImageResource(0);
                                        powerLayout.removeView(imageView);
                                    }
                                }).start();
                            }
                        }, 500);
                        break;

                    case "touchProtection":

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void swapPhase(String skillName, ArrayList<Integer> ownRow, float percentage) {
        if (random.nextFloat() * 100f <= percentage) {
            switch (skillName) {
                case "rotateY":
                    for (int i = 0; i < drag_row.getChildCount(); i++) {

                        drag_row.getChildAt(i).animate().rotationYBy(180).start();
                    }
                    skillActivateEffect();
                    break;
                case "rotateX":
                    for (int i = 0; i < drag_row.getChildCount(); i++) {

                        drag_row.getChildAt(i).animate().rotationXBy(180).start();
                    }
                    skillActivateEffect();
                    break;
                case "scale":
                    for (int i = 0; i < drag_row.getChildCount(); i++) {

                        drag_row.getChildAt(i).animate().scaleX(0.7f).start();
                    }
                    skillActivateEffect();
                    break;
                case "alpha":
                    for (int i = 0; i < drag_row.getChildCount(); i++) {

                        drag_row.getChildAt(i).animate().alpha(0.3f).start();
                    }
                    skillActivateEffect();
                    break;
                case "flying":
                    for (int i = 0; i < drag_row.getChildCount(); i++) {
                        if (random.nextBoolean()) {
                            ObjectAnimator animator = ObjectAnimator.ofFloat(drag_row.getChildAt(i), "x", drag_row.getChildAt(i).getX(), -drag_row.getChildAt(i).getWidth(), drag_row.getChildAt(i).getWidth()
                                    , drag_row.getChildAt(i).getX());
                            animator.setDuration(10000);
                            animator.setRepeatCount(ValueAnimator.INFINITE);
                            animator.start();
                        }
                    }
                    skillActivateEffect();
                    break;

                case "smoke":

                    break;

                //-----------------------------SUPPORT SKILL-------------------------------------------//


                case "autoSolve":
                    ArrayList<Float> yRow = new ArrayList<>();
                    TreeSet<Float> treeSet = new TreeSet<>();
                    for (int i = 0; i < drag_row.getChildCount(); i++) {
                        treeSet.add(drag_row.getChildAt(i).getY());
                    }
                    yRow.addAll(treeSet);
                    for (int i = drag_row.getChildCount() - 1; i >= 0; i--) {
                        LinearLayout linearLayout = (LinearLayout) drag_row.findViewWithTag("ownRows_" + i);
                        linearLayout.setY(yRow.get(ownRow.get(i)));
                    }
                    puzzleActivity.pictureCompleted = true;
                    break;
            }
        }
    }

    public int imageComplete(String skillName, int delay, float percentage) {
        int returnValue = delay;
        if (random.nextFloat() * 100f <= percentage) {
            switch (skillName) {

                case "delayEnd":

                    break;

                case "delayAnimation":

                    break;

                case "breakGlass":
                    ImageView imageView = new ImageView(context);
                    powerLayout.addView(imageView);
                    ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                    lp.width = powerLayout.getWidth();
                    lp.height = powerLayout.getHeight();
                    imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.animation_break));
                    powerLayout.setBackgroundColor(Color.parseColor("#50f2696a"));
                    setTouch(imageView);
                    skillActivateEffect();
                    returnValue = 0;
                    break;

                //-----------------------------SUPPORT SKILL-------------------------------------------//

                case "extraWin":

                    break;

                case "increaseCounter":
                    puzzleActivity.ownGiveUpCounter++;
                    break;
            }
        }
        return returnValue;
    }

    private Bitmap scaleBitmap(int width, int height, int drawAble) {

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawAble);
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private Bitmap roundEdge(Bitmap bitmap) {

        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        float roundPx = drag_row.getWidth() / 50;
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void getForNextRow(ArrayList<Bitmap> bitmap, ArrayList<Integer> whichRow) {
        this.bitmap = new ArrayList<>();
        this.whichRowInt = new ArrayList<>();
        this.bitmap = bitmap;
        this.whichRowInt = whichRow;
    }

    private void setTouch(final View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                powerLayout.animate().alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        powerLayout.setBackgroundResource(0);
                        powerLayout.setAlpha(1);
                        final AnimationDrawable animationDrawable = ((AnimationDrawable) v.getBackground());
                        int delay = animationDrawable.getNumberOfFrames() * 50;
                        animationDrawable.start();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animationDrawable.stop();
                                v.setBackgroundResource(0);
                                powerLayout.removeView(v);
                                puzzleActivity.skillActiavtedAnimation("own", "de-activate");
                                puzzleActivity.create.prepareNextImage();
                            }
                        }, delay);
                    }
                }).setDuration(250).start();
            }
        });
    }

    private void setClickNumber(View view) {
        counter = 0;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (v.getTag().equals("number" + counter)) {
                    v.setEnabled(false);
                    v.setVisibility(View.INVISIBLE);
                    counter++;
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0, v.getX() + v.getWidth() / 2, v.getY() + v.getHeight() / 2);
                    scaleAnimation.setInterpolator(new AnticipateInterpolator());
                    scaleAnimation.setDuration(250);
                    v.startAnimation(scaleAnimation);
                    scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (counter == powerLayout.getChildCount()) {
                                powerLayout.removeAllViews();
                                puzzleActivity.skillActiavtedAnimation("own", "comeBack!");
                                puzzleActivity.create.createColor();
                                puzzleActivity.create.createImage(puzzleActivity.ownChunkedImages, puzzleActivity.image_counter);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                } else {
                    for (int i = 0; i < powerLayout.getChildCount(); i++) {
                        powerLayout.getChildAt(i).setVisibility(View.VISIBLE);
                        powerLayout.getChildAt(i).setEnabled(true);
                    }
                    counter = 0;
                }
            }
        });
    }

    private void setRowTouch(final View view) {
        final float scale = view.getScaleX();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                    final int timer = random.nextInt(5000) + 5000;
                    compareTime = 0;
                    objectAnimator = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 0);
                    objectAnimator.setDuration(timer);
                    objectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (view.getScaleX() <= 0) {
                                ((AnimationDrawable) ((ImageView) view).getDrawable()).stop();
                                ((ImageView) view).setImageResource(0);
                                powerLayout.removeView(view);
                                for (int i = 0; i < currentRow.getChildCount(); i++) {
                                    currentRow.getChildAt(i).setEnabled(true);
                                }
                                puzzleActivity.skillActiavtedAnimation("own", "de-activate");
                            }
                        }
                    });
                    objectAnimator.start();
                }
                if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
                    if (powerLayout.getChildCount() != 0) {
                        objectAnimator.cancel();
                        view.animate().scaleX(scale).setDuration(500).start();
                    }
                }
                return true;
            }
        });
    }

    private void setSwipe(View view, final String whichRow, final ArrayList<Boolean> booleanStorage, final ArrayList<Boolean> booleanStorage2) {
        view.setOnTouchListener(new OnSwipeTouchListener(context) {
                                    public void onTouchDown(final View v) {
                                    }

                                    public void onSwipeRight(final View v) {
                                        v.animate().rotationYBy(-180).setDuration(100).start();
                                        checkResult(v, "y");
                                    }

                                    public void onSwipeLeft(final View v) {
                                        v.animate().rotationYBy(180).setDuration(100).start();
                                        checkResult(v, "y");
                                    }

                                    public void onSwipeTop(final View v) {
                                        v.animate().rotationXBy(180).setDuration(100).start();
                                        checkResult(v, "x");
                                    }

                                    public void onSwipeBottom(final View v) {
                                        v.animate().rotationXBy(-180).setDuration(100).start();
                                        checkResult(v, "x");
                                    }

                                    private void checkResult(final View v, String axisMode) {
                                        if (axisMode.equals("x")) {
                                            thread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    int tempX = 0;
                                                    int tempY = 0;
                                                    for (int i = 0; i < currentRow.getChildCount(); i++) {
                                                        if (v == currentRow.getChildAt(i)) {
                                                            booleanStorage.set(i, !booleanStorage.get(i));
                                                        }
                                                        if (booleanStorage.get(i))
                                                            tempX++;
                                                        if (booleanStorage2.get(i))
                                                            tempY++;
                                                    }
                                                    Log.d("bool", booleanStorage + "");
                                                    Log.d("bool", booleanStorage2 + "");
                                                    xAxis_OK = tempX == booleanStorage.size();
                                                    yAxis_OK = tempY == booleanStorage2.size();
                                                }
                                            });
                                            thread.start();
                                        } else {
                                            thread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    int tempX = 0;
                                                    int tempY = 0;
                                                    for (int i = 0; i < currentRow.getChildCount(); i++) {
                                                        if (v == currentRow.getChildAt(i)) {
                                                            booleanStorage2.set(i, !booleanStorage2.get(i));
                                                        }
                                                        if (booleanStorage2.get(i))
                                                            tempY++;
                                                        if (booleanStorage.get(i))
                                                            tempX++;
                                                    }
                                                    Log.d("bool", booleanStorage + "");
                                                    Log.d("bool", booleanStorage2 + "");
                                                    xAxis_OK = tempX == booleanStorage.size();
                                                    yAxis_OK = tempY == booleanStorage2.size();
                                                }
                                            });
                                            thread.start();
                                        }
                                        try {
                                            thread.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            Log.d(GoogleServiceApi.TAG, "POWER_THREAD_EXIT");
                                        }
                                        if (xAxis_OK && yAxis_OK) {
                                            for (int i = 0; i < currentRow.getChildCount(); i++) {
                                                currentRow.getChildAt(i).setEnabled(false);
                                            }
                                            puzzleActivity.skillActiavtedAnimation("own", "de-activate");
                                            ParticleSystem ps = new ParticleSystem(puzzleActivity, 100, R.drawable.particle_3, 1000);
                                            ps.setScaleRange(0.4f, 0.8f).setSpeedModuleAndAngleRange(0, 0.5f, 0, 360).setFadeOut(100).setRotationSpeedRange(5, 10).oneShot(currentRow, 20, new LinearInterpolator());
                                            ParticleSystem ps1 = new ParticleSystem(puzzleActivity, 100, R.drawable.particle_star1, 1000);
                                            ps1.setScaleRange(0.1f, 0.3f).setSpeedModuleAndAngleRange(0, 0.5f, 0, 360).setFadeOut(100).setRotationSpeedRange(5, 10).oneShot(currentRow, 20, new LinearInterpolator());
                                            puzzleActivity.ownInverstNum--;
                                            puzzleActivity.image_counter++;
                                            puzzleActivity.create.createImage(puzzleActivity.ownChunkedImages, puzzleActivity.image_counter);
                                        }
                                    }
                                }
        );

    }

    private void skillActivateEffect() {
        puzzleActivity.skillActiavtedAnimation("own", "activate");
    }
}
