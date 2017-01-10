package fusion.com.soicalrpgpuzzle;

import android.animation.ObjectAnimator;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by Pandora on 5/28/2016.
 */
public class Level extends Fragment {

    float height, width;
    View v;
    ImageView circle;
    GlobalState state;
    GridLayout relativeLayout;

    public Level() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.activity_level, container, false);
        state = ((GlobalState) this.getActivity().getApplicationContext());

        String[] dimension = state.getDimensionScreen().split(",");
        height = Float.valueOf(dimension[1]);
        width = Float.valueOf(dimension[0]);

        relativeLayout = (GridLayout) v.findViewById(R.id.relative);

        setImage();
        setLayout();
        setInventory();
        return v;
    }

    private void setImage(){
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.matches_banner)).getBitmap(), (int) (width - width / 10.8), (int) (height/9.6), true));
        relativeLayout.addView(imageView);
        imageView.setX(width/2 - (width - width / 10.8f)/2);
    }

    private void setLayout() {
        RelativeLayout ownProfile = new RelativeLayout(getActivity());
        ScrollView scrollView = (ScrollView)v.findViewById(R.id.levelScrollView);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setVerticalScrollBarEnabled(false);
        relativeLayout.addView(ownProfile);
        ViewGroup.LayoutParams lp = ownProfile.getLayoutParams();
        lp.width = (int) (width - (width / 27));
        lp.height = (int) (height / 2.7429);
        lp = scrollView.getLayoutParams();
        lp.height = (int)(height - height/12.8);
        ownProfile.setY(height / 96);
        ownProfile.setX(width / 54);
        ownProfile.setBackgroundResource(R.drawable.record_back);
        setProfile(ownProfile);
    }

    private void setProfile(RelativeLayout ownProfile) {
        int[] drawable = {R.drawable.red_circle, R.drawable.green_circle, R.drawable.blue_circle, R.drawable.yellow_circle};
        String[] color = {"Red","Green","Blue","Yellow"};
        ImageView profile = new ImageView(getActivity());
        circle = new ImageView(getActivity());
        ownProfile.addView(profile);
        ownProfile.addView(circle);
        float profileHeight = height / 2.4f;
        for (int i=0;i<color.length;i++) {
            if (color[i].equals(state.getPlayer().getColour())) {
                circle.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), drawable[i])).getBitmap(), (int) (profileHeight / 1.9 + height / 96), (int) (profileHeight / 1.9 + height / 96), true));
                if (state.getPlayer().getGooglePlayProfile() != null) {
                    profile.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(state.getPlayer().getGooglePlayProfile(), (int) (profileHeight / 2.1), (int) (profileHeight / 2.1), true), 0));
                } else {
                    profile.setImageBitmap(getCircleBitmap(Bitmap.createScaledBitmap(state.getPlayer().getInstaProfilePic(), (int) (profileHeight / 2.1), (int) (profileHeight / 2.1), true), 0));
                }
            }
        }

        circle.setX(width / 27);
        circle.setY(width / 27);
        profile.setX(width / 27 + width / 36);
        profile.setY(width / 27 + width / 36);
        ObjectAnimator animator = ObjectAnimator.ofFloat(circle, "rotation", 0, 360);
        animator.setDuration(1000).setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        animator.start();
        setName(ownProfile);
    }

    private void setName(RelativeLayout ownProfile) {
        TextView ownName = new TextView(getActivity());
        ownName.setTextSize(50);
        Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "JELLYBELLY.ttf");
        ownName.setTypeface(textFont);
        ownName.setTextColor(Color.WHITE);
        ownName.setMinimumWidth((int) (width - (width / 27)));
        ownName.setGravity(Gravity.START);
        ownProfile.addView(ownName);
        ownName.setY(height / 2.7429f - height / 10.6667f);
        ownName.setX(width / 36);
        ownName.setText(state.getPlayer().getName());
        setRank(ownProfile);
    }

    private void setRank(RelativeLayout ownProfile) {
        ImageView rank = new ImageView(getActivity());
        rank.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.rank_expert)).getBitmap(), (int) (width / 2.5), (int) (height / 12.8f), true));
        ownProfile.addView(rank);
        rank.setY(height / 38.4f);
        rank.setX((width - (width / 27)) / 2);
        setWin(ownProfile);
        setLose(ownProfile);
    }

    private void setWin(RelativeLayout ownProfile) {
        LinearLayout win = new LinearLayout(getActivity());
        win.setGravity(Gravity.CENTER_VERTICAL);
        ImageView winLayout = new ImageView(getActivity());
        winLayout.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.record_win)).getBitmap(), (int) (width / 4), (int) (height / 14.7692), true));
        ownProfile.addView(win);
        win.setY(height / 19.2f + height / 14.7692f + height / 96);
        win.setX((width - (width / 27)) / 2);
        win.addView(winLayout);

        TextView winCount = new TextView(getActivity());
        Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "JELLYBELLY.ttf");
        winCount.setMinimumWidth((int) (width - (width / 27)) / 8);
        winCount.setTextSize(20);
        winCount.setGravity(Gravity.END);
        winCount.setTextColor(Color.WHITE);
        winCount.setTypeface(textFont);
        winCount.setText(state.getPlayer().getWin() + "");
        win.addView(winCount);
    }

    private void setLose(RelativeLayout ownProfile) {
        LinearLayout lose = new LinearLayout(getActivity());
        lose.setGravity(Gravity.CENTER_VERTICAL);
        ImageView loseLayout = new ImageView(getActivity());
        loseLayout.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.record_lose)).getBitmap(), (int) (width / 4), (int) (height / 14.7692), true));
        ownProfile.addView(lose);
        lose.setY(height / 19.2f + height / 7.3846f + height / 96);
        lose.setX((width - (width / 27)) / 2);
        lose.addView(loseLayout);

        TextView loseCount = new TextView(getActivity());
        Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "JELLYBELLY.ttf");
        loseCount.setMinimumWidth((int) (width - (width / 27)) / 8);
        loseCount.setTextSize(20);
        loseCount.setGravity(Gravity.END);
        loseCount.setTextColor(Color.WHITE);
        loseCount.setTypeface(textFont);
        loseCount.setText(state.getPlayer().getLose() + "");
        lose.addView(loseCount);
    }

    private void setInventory() {
        RelativeLayout rankRelative = new RelativeLayout(getActivity());
        RelativeLayout reserveRelative = new RelativeLayout(getActivity());
        final RelativeLayout rankInv = new RelativeLayout(getActivity());
        final RelativeLayout reserveInv = new RelativeLayout(getActivity());

        rankRelative.addView(rankInv);
        reserveRelative.addView(reserveInv);

        rankInv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        reserveInv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        relativeLayout.addView(rankRelative);
        relativeLayout.addView(reserveRelative);

        ViewGroup.LayoutParams lp = rankInv.getLayoutParams();
        lp.width = (int) (width - (width / 27));
        lp.height = (int) (height / 5.0526f);

        lp = reserveInv.getLayoutParams();
        lp.width = (int) (width - (width / 27));
        lp.height = (int) (height / 5.0526f);

        lp = rankRelative.getLayoutParams();
        lp.height = (int)(height / 5.0526f + ((height / 9.6)/2) + height/38.4);

        lp = reserveRelative.getLayoutParams();
        lp.height = (int)(height / 5.0526f + ((height / 9.6)/2) + height/38.4);

        rankInv.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.start_match_inv));
        reserveInv.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.start_match_inv));
        ImageView rankName = new ImageView(getActivity());
        ImageView reserve = new ImageView(getActivity());
        rankName.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.start_match_rank)).getBitmap(), (int) (width / 2), (int) (height / 9.6), true));
        reserve.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.reserve1)).getBitmap(), (int) (width / 2), (int) (height / 9.6), true));
        rankRelative.addView(rankName);
        reserveRelative.addView(reserve);


        reserveInv.setX(width / 54);
        reserveInv.setY((int) ((height / 9.6)/2)+ height/38.4f);

        rankInv.setX(width / 54);
        rankInv.setY((int) ((height / 9.6)/2)+ height/38.4f);

        rankName.setX(width / 2 - (width / 4));
        rankName.setY(height/38.4f);

        reserve.setX(width / 2 - (width / 4));
        reserve.setY(height/38.4f);

        setRankInv(rankInv);
        setReserveInv(reserveInv);
    }

    private void setRankInv(RelativeLayout rankInv){
        int[] drawable = {R.drawable.red_circle, R.drawable.green_circle, R.drawable.blue_circle, R.drawable.yellow_circle};

    }

    private void setReserveInv(RelativeLayout reserveInv){
        ImageView reserveCircle = new ImageView(getActivity());
        reserveCircle.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.white_circle)).getBitmap(), (int) (width / 4.32), (int) (width / 4.32), true));
        reserveCircle.setPadding((int)height/64,(int)height/64,(int)height/64,(int)height/64);
        reserveInv.addView(reserveCircle);
        ObjectAnimator animator = ObjectAnimator.ofFloat(reserveCircle,"rotation",0,360);
        animator.setDuration(new Random().nextInt(500)+500).setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        animator.start();
    }

    public Bitmap getCircleBitmap(Bitmap bitmap, int borderWidth) {
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
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        bitmap.recycle();
        return canvasBitmap;
    }

    public Bitmap getSelfProfilePic(String imageUrl) {

        try {
            URL url = new URL(imageUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.d("loadImage", "here");
            Log.d("loadImage", myBitmap.toString());
            return myBitmap;
        } catch (Exception e) {
            Log.d("loadImage", e.toString());
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(GoogleServiceApi.TAG, "onstartplayerInventory");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(GoogleServiceApi.TAG, "onresumeplayerInventory");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(GoogleServiceApi.TAG, "onstopplayerInventory");
        clearMemory();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(GoogleServiceApi.TAG, "ondestroyplayerInventory");
        relativeLayout.removeAllViews();
    }

    private void clearMemory() {
        /*for (int i = 0; i < inventory.getChildCount(); i++) {
            ImageView child = (ImageView) inventory.getChildAt(i);
            Bitmap bitmap = ((BitmapDrawable)child.getDrawable()).getBitmap();
            bitmap.recycle();
        }

        inventory.removeAllViews();*/
    }
}
