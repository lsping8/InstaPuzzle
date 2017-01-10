package fusion.com.soicalrpgpuzzle;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
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
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.common.images.ImageManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Brian on 6/17/2016.
 */
public class TotalGallery extends Fragment {

    private FragmentTabHost mTabHost;
    FragmentManager fm;
    ArrayList<ImageModel> data;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private RelativeLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private FrameLayout mFrameLayout;
    private de.hdodenhof.circleimageview.CircleImageView profilePicView;
    GeneralImage generalImage;
    InstaGallery instaFragment;
    GlobalState state;
    TextView name;
    float width, height;
    View v;
    private SharedPreferences sharedPref;
    RelativeLayout relativeLayout, challengerStatus, oppInv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(GoogleServiceApi.TAG, "-----------------TotalGallery:onCreate-----------------");
        super.onCreate(savedInstanceState);
        /*if (savedInstanceState == null) {
            setRetainInstance(true);
        }*/

        state = ((GlobalState) this.getActivity().getApplicationContext());
        generalImage = state.getGeneralImage();

        sharedPref = getActivity().getSharedPreferences(GoogleServiceApi.SHARED, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(GoogleServiceApi.TAG, "-----------------TotalGallery:onCreateView-----------------");
        v = inflater.inflate(R.layout.activity_total_gallery_2, container, false);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.relative);
        fm = this.getActivity().getSupportFragmentManager();

        //instaFragment = new InstaGallery();
        //getChildFragmentManager().beginTransaction().add(R.id.tabFrameLayout, instaFragment).commit();

        //name = (TextView) v.findViewById(R.id.name);

        String[] dimension = state.getDimensionScreen().split(",");
        height = Float.valueOf(dimension[1]);
        width = Float.valueOf(dimension[0]);

        ImageView challanger = new ImageView(getActivity());
        relativeLayout.addView(challanger);
        challanger.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.challenger_banner)).getBitmap(), (int) (width - width / 10.8), (int) (height / 7.68), true));
        challanger.setX(width / 21.6f);

        oppInv = new RelativeLayout(getActivity());
        relativeLayout.addView(oppInv);
        ViewGroup.LayoutParams lp = oppInv.getLayoutParams();
        lp.height = (int) (height / 6.4f);
        lp.width = (int) (width - (width / 27));
        oppInv.setBackgroundResource(R.drawable.start_match_inv);
        oppInv.setY(height - height / 8.7272f - height / 7.68f);
        oppInv.setX(width / 54);

        challengerStatus = new RelativeLayout(getActivity());
        relativeLayout.addView(challengerStatus);
        lp = challengerStatus.getLayoutParams();
        lp.width = (int) (width - (width / 27));
        lp.height = (int) (height / 5.0526f);
        challengerStatus.setBackgroundResource(R.drawable.challenger_status);
        challengerStatus.setX(width / 54);
        challengerStatus.setY(height / 32 + height / 12.8f + height / 96);
        setChallengerStatus();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(GoogleServiceApi.TAG, "-----------------TotalGallery:onDestroyView-----------------");
        //mTabHost = null;
    }

    private void setChallengerStatus() {
        TextView challengerName = new TextView(getActivity());
        TextView winCount = new TextView(getActivity());
        TextView loseCount = new TextView(getActivity());
        ImageView win = new ImageView(getActivity());
        ImageView lose = new ImageView(getActivity());
        Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "JELLYBELLY.ttf");
        challengerName.setTextSize(60);
        challengerName.setTypeface(textFont);
        challengerName.setTextColor(Color.BLACK);
        challengerName.setMinimumWidth((int) (width - (width / 5.4)));
        challengerName.setMaxWidth((int) (width - (width / 5.4) + 1));
        challengerName.setGravity(Gravity.CENTER_HORIZONTAL);
        challengerName.setX((int) (width - (width / 27) - (width - (width / 5.4))) / 2);
        challengerName.setY(height / 192);
        challengerName.setSingleLine(true);
        challengerName.setTag("challengerName");
        challengerStatus.addView(challengerName);
        winCount.setTypeface(textFont);
        winCount.setTextColor(Color.BLACK);
        winCount.setTextSize(30);
        winCount.setMinimumHeight((int) (height / 12.8));
        winCount.setGravity(Gravity.CENTER);
        winCount.setTag("winCount");
        loseCount.setTypeface(textFont);
        loseCount.setTextColor(Color.BLACK);
        loseCount.setTextSize(30);
        loseCount.setMinimumHeight((int) (height / 12.8));
        loseCount.setGravity(Gravity.CENTER);
        loseCount.setTag("loseCount");
        win.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.record_win)).getBitmap(), (int) (width / 5.4), (int) (height / 12.8), true));
        lose.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.record_lose)).getBitmap(), (int) (width / 5.4), (int) (height / 12.8), true));
        win.setTag("win");
        win.setVisibility(View.INVISIBLE);
        lose.setTag("lose");
        lose.setVisibility(View.INVISIBLE);
        challengerStatus.addView(win);
        challengerStatus.addView(lose);
        challengerStatus.addView(winCount);
        challengerStatus.addView(loseCount);
        win.setY(height / 5.0526f - height / 9.6f);
        win.setX(width / 5 - width / 13.5f);
        lose.setY(height / 5.0526f - height / 9.6f);
        lose.setX(width / 2 + width / 13.5f);
        winCount.setY(height / 5.0526f - height / 9.6f);
        winCount.setX(width / 5 + width / 5.4f - width / 13.5f);
        loseCount.setY(height / 5.0526f - height / 9.6f);
        loseCount.setX(width / 2 + width / 5.4f + width / 13.5f);
    }

    public void setUpdateChallanger() {
        if (relativeLayout.getChildCount() == 3)
            setLayout();
    }

    private void setLayout() {
        ScrollView scrollView = new ScrollView(getActivity());
        relativeLayout.addView(scrollView);
        ViewGroup.LayoutParams lp = scrollView.getLayoutParams();
        lp.width = (int) width;
        lp.height = (int) (height - (height / 32 + height / 12.8f + height / 128 + height / 5.0526f + height / 8.7272f + height / 6.4f) + height / 32);
        scrollView.setY(height / 32 + height / 12.8f + height / 192 + height / 5.0526f);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setVerticalScrollBarEnabled(false);
        setChallanger(scrollView);
    }

    private void setChallanger(ScrollView scrollView) {

        int[] drawable = {R.drawable.red_circle, R.drawable.green_circle, R.drawable.blue_circle, R.drawable.yellow_circle};
        RelativeLayout challengerList = new RelativeLayout(getActivity());
        GridLayout gridLayout = new GridLayout(getActivity());
        GridLayout playerLayout = new GridLayout(getActivity());
        gridLayout.setColumnCount(3);
        playerLayout.setColumnCount(3);
        challengerList.addView(gridLayout);
        challengerList.addView(playerLayout);
        scrollView.addView(challengerList);
        int padding = (int) width / 24;
        Handler handler = new Handler();
        for (int i = 0; i < 4; i++) {
            final ImageView circle = new ImageView(getActivity());
            final ImageView player = new ImageView(getActivity());
            circle.setScaleY(0);
            circle.setScaleX(0);
            player.setScaleX(0);
            player.setScaleY(0);
            player.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.rsz_sohai)).getBitmap(), (int) (width / 4 - width / 27), (int) (width / 4 - width / 27), true), 0));
            circle.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), drawable[new Random().nextInt(drawable.length)])).getBitmap(), (int) (width / 4), (int) (width / 4), true));
            player.setPadding(padding + (int) width / 54, (int) width / 54, padding + (int) width / 54, (int) width / 54);
            circle.setPadding(padding, 0, padding, 0);
            gridLayout.addView(circle);
            playerLayout.addView(player);
            ObjectAnimator animator = ObjectAnimator.ofFloat(circle, "rotation", 0, 360);
            animator.setDuration(new Random().nextInt(500) + 500).setInterpolator(new LinearInterpolator());
            animator.setRepeatCount(-1);
            animator.start();
            setClickAble(player);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    circle.animate().scaleX(1).scaleY(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            player.animate().scaleX(1).scaleY(1).setDuration(250).setInterpolator(new OvershootInterpolator()).setStartDelay(100).start();
                        }
                    }).start();
                }
            },250*i);
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

    private void setClickAble(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChallengerStatus();
            }
        });
    }

    private void displayChallengerStatus() {

        TextView challengerName = (TextView) v.findViewWithTag("challengerName");
        TextView winCount = (TextView) v.findViewWithTag("winCount");
        TextView loseCount = (TextView) v.findViewWithTag("loseCount");
        ImageView win = (ImageView) v.findViewWithTag("win");
        ImageView lose = (ImageView) v.findViewWithTag("lose");

        win.setVisibility(View.VISIBLE);
        lose.setVisibility(View.VISIBLE);

        setChallengerName(challengerName);
        setWinCount(winCount);
        setLoseCount(loseCount);
        setOppInv();
    }

    private void setChallengerName(TextView challengerName) {
    }

    private void setWinCount(TextView winCount) {

    }

    private void setLoseCount(TextView loseCount) {

    }

    private void setOppInv() {
        int[] drawable = {R.drawable.red_circle, R.drawable.green_circle, R.drawable.blue_circle, R.drawable.yellow_circle};
        if (oppInv.getChildCount() != 0) {
            for (int i = 0; i < oppInv.getChildCount(); i++) {
                oppInv.getChildAt(i).animate().cancel();
                ((ImageView) oppInv.getChildAt(i)).setImageDrawable(null);
            }
            oppInv.removeAllViews();
        }
        Handler handler = new Handler();
        for (int i = 0; i < 4; i++) {
            final ImageView circle = new ImageView(getActivity());
            final ImageView player = new ImageView(getActivity());
            circle.setScaleY(0);
            circle.setScaleX(0);
            player.setScaleX(0);
            player.setScaleY(0);
            player.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.rsz_sohai)).getBitmap(), (int) (width / 5 - width / 27), (int) (width / 5 - width / 27), true), 0));
            circle.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), drawable[new Random().nextInt(drawable.length)])).getBitmap(), (int) (width / 5), (int) (width / 5), true));
            oppInv.addView(circle);
            oppInv.addView(player);
            player.setPadding(20, 20, 20, 20);
            circle.setY((height / 6.4f) / 2 - width / 10);
            circle.setX(((width - (width / 27)) / 4) * (i) + width / 45);
            player.setY((height / 6.4f) / 2 - width / 10);
            player.setX(((width - (width / 27)) / 4) * (i) + width / 45);
            ObjectAnimator animator = ObjectAnimator.ofFloat(circle, "rotation", 0, 360);
            animator.setDuration(new Random().nextInt(500) + 500).setInterpolator(new LinearInterpolator());
            animator.setRepeatCount(-1);
            animator.start();
            setClickAble(player);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    circle.animate().scaleX(1).scaleY(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            player.animate().scaleX(1).scaleY(1).setDuration(250).setInterpolator(new OvershootInterpolator()).setStartDelay(100).start();
                        }
                    }).start();
                }
            },250*i);
        }
    }

    public void resetChallenger(){
        TextView challengerName = (TextView) v.findViewWithTag("challengerName");
        TextView winCount = (TextView) v.findViewWithTag("winCount");
        TextView loseCount = (TextView) v.findViewWithTag("loseCount");
        ImageView win = (ImageView) v.findViewWithTag("win");
        ImageView lose = (ImageView) v.findViewWithTag("lose");
        challengerName.setText("");
        winCount.setText("");
        loseCount.setText("");
        win.setVisibility(View.INVISIBLE);
        lose.setVisibility(View.INVISIBLE);
        for (int i=0;i<oppInv.getChildCount();i++){
            //((BitmapDrawable)((ImageView)oppInv.getChildAt(i)).getDrawable()).getBitmap().recycle();
            ((ImageView) oppInv.getChildAt(i)).setImageDrawable(null);
        }
        oppInv.removeAllViews();
        if (relativeLayout.getChildCount() == 4) {
            ScrollView scrollView = ((ScrollView) relativeLayout.getChildAt(relativeLayout.getChildCount() - 1));
            RelativeLayout container = (RelativeLayout) scrollView.getChildAt(0);
            GridLayout challengerList1 = (GridLayout) container.getChildAt(0);
            GridLayout challengerList2 = (GridLayout) container.getChildAt(1);

            for (int i = 0; i < challengerList1.getChildCount(); i++) {
                //((BitmapDrawable)((ImageView) challengerList1.getChildAt(i)).getDrawable()).getBitmap().recycle();
                ((ImageView) challengerList1.getChildAt(i)).setImageDrawable(null);
            }
            for (int i = 0; i < challengerList2.getChildCount(); i++) {
                //((BitmapDrawable)((ImageView) challengerList2.getChildAt(i)).getDrawable()).getBitmap().recycle();
                ((ImageView) challengerList2.getChildAt(i)).setImageDrawable(null);
            }
            challengerList1.removeAllViews();
            challengerList2.removeAllViews();
            container.removeAllViews();
            scrollView.removeAllViews();
            relativeLayout.removeView(scrollView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        relativeLayout.removeAllViews();
    }
}
