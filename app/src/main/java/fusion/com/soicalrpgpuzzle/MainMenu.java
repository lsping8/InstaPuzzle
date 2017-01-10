package fusion.com.soicalrpgpuzzle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.games.Games;

import fusion.com.soicalrpgpuzzle.util.IabHelper;
import fusion.com.soicalrpgpuzzle.util.IabResult;


public class MainMenu extends AppCompatActivity implements LeaderBoards.Listener, Store.Listener {

    SampleFragmentPagerAdapter adapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    int currFragmentPage;
    GoogleServiceApi googleServiceApi;
    GlobalState state;
    LeaderBoards mLeaderBoards;
    StartMatch mStartMatch;
    Level mLevel;
    InstaGallery mInstaGallery;
    Shop mShop;
    Store mStore;
    Record mRecord;
    TotalGallery mTotalGallery;
    private static final int RC_UNUSED = 5001;
    String base64EncodedPublicKey;
    IabHelper mHelper;
    Boolean galleryFirstAccess, disableMoveUp;
    RelativeLayout main_layout, top_layout;
    ImageView profile_logo, history_logo, level_logo, shop_logo, battle_logo;
    Context context;
    float height, width;
    View current_logo;
    LinearLayout coinText, cashText, energyText;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private static final String SHARED = "Instagram_Preferences";
    private static final String API_ACCESS_TOKEN = "access_token";
    AnimationDrawable anim;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        context = this.getApplicationContext();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Log.d(GoogleServiceApi.TAG, "oncreatemainmenu");

        galleryFirstAccess = true;
        disableMoveUp = false;

        main_layout = (RelativeLayout) this.findViewById(R.id.main_layout);
        profile_logo = (ImageView) this.findViewById(R.id.profile_logo);
        history_logo = (ImageView) this.findViewById(R.id.history_logo);
        level_logo = (ImageView) this.findViewById(R.id.level_logo);
        shop_logo = (ImageView) this.findViewById(R.id.shop_logo);
        battle_logo = (ImageView) this.findViewById(R.id.battle_logo);
        top_layout = (RelativeLayout) findViewById(R.id.top_bar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        /*anim = (AnimationDrawable) main_layout.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);*/

        state = ((GlobalState) getApplicationContext());
        googleServiceApi = state.getmGoogleApi();

        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        String[] dimension = state.getDimensionScreen().split(",");
        height = Float.valueOf(dimension[1]);
        width = Float.valueOf(dimension[0]);

        int paddingSize = (int) width / 72;

        coinText = new LinearLayout(context);
        coinText.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        coinText.setPadding(0, 0, paddingSize, 0);

        cashText = new LinearLayout(context);
        cashText.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        cashText.setPadding(0, 0, paddingSize, 0);

        energyText = new LinearLayout(context);
        energyText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        energyText.setPadding(0, 0, paddingSize, 0);

        coinText.setBackground(ContextCompat.getDrawable(context, R.drawable.layout_back));
        cashText.setBackground(ContextCompat.getDrawable(context, R.drawable.layout_back));
        energyText.setBackground(ContextCompat.getDrawable(context, R.drawable.layout_back));
        energyText.setId(R.id.energy_bar);

        googleServiceApi.setMainMenuAct(MainMenu.this);
        googleServiceApi.setCurrActivity(this);

        mLeaderBoards = new LeaderBoards();
        mStartMatch = new StartMatch();
        mLevel = new Level();
        mStore = new Store();
        mInstaGallery = new InstaGallery();
        mTotalGallery = new TotalGallery();
        mShop = new Shop();
        mRecord = new Record();

        mLeaderBoards.setListener(this);
        mStore.setListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(2, false);
        viewPager.setOffscreenPageLimit(3);

        setTopLayout();
        setText();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab;

                tab = tabLayout.getTabAt(position);

                moveDownLogo(tab, position);

                switch (position) {

                    case 0:
                        // setBackground(R.drawable.rsz_shop_background);
                        top_layout.animate().translationY(0).start();
                        break;
                    case 1:
                        // setBackground(R.drawable.rsz_level_background);
                        top_layout.animate().translationY(-top_layout.getHeight()).start();
                        break;
                    case 2:
                        top_layout.animate().translationY(0).start();
                        //   setBackground(R.drawable.main_menu_background);
                        break;
                    case 3:
                        top_layout.animate().translationY(-top_layout.getHeight()).start();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mTotalGallery.setUpdateChallanger();
                            }
                        },100);
                        /*if (galleryFirstAccess) {
                            Log.d(GoogleServiceApi.TAG, "CallSocialDownload");
                            galleryFirstAccess = false;

                            if (state.getPlayer().getInstaId() != null) {
                                //mTotalGallery.callSocialDownload();
                            } else {
                                Log.d(GoogleServiceApi.TAG, "callLoginInstaLogo");
                                //mTotalGallery.callLoginInstaLogo();
                            }
                        }*/
                        break;
                    case 4:
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecord.updateData();

                            }
                        },100);
                        top_layout.animate().translationY(-top_layout.getHeight()).start();
                        //mRecord.callHistory();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        setupTabIcons();

        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        /*for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }*/

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new fusion.com.soicalrpgpuzzle.util.IabHelper(this, base64EncodedPublicKey);
    }

    private void setTopLayout() {

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout coinLayout = new RelativeLayout(context);
        RelativeLayout cashLayout = new RelativeLayout(context);
        RelativeLayout energyLayout = new RelativeLayout(context);

        coinLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.base_layout));
        cashLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.base_layout));
        energyLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.base_layout));

        linearLayout.addView(energyLayout);
        linearLayout.addView(coinLayout);
        linearLayout.addView(cashLayout);

        ViewGroup.LayoutParams layoutParams = coinLayout.getLayoutParams();
        layoutParams.width = (int) width / 3;
        layoutParams.height = (int) height / 15;

        layoutParams = cashLayout.getLayoutParams();
        layoutParams.width = (int) width / 3;
        layoutParams.height = (int) height / 15;

        layoutParams = energyLayout.getLayoutParams();
        layoutParams.width = (int) width / 3;
        layoutParams.height = (int) height / 15;

        coinLayout.addView(coinText);
        cashLayout.addView(cashText);
        energyLayout.addView(energyText);

        layoutParams = coinText.getLayoutParams();
        layoutParams.width = (int) (width / 4);
        layoutParams.height = (int) height / 20;
        coinText.setX(width / 3 - width / 4 - width / 54);
        coinText.setY(height / 30 - height / 40);

        layoutParams = cashText.getLayoutParams();
        layoutParams.width = (int) (width / 4);
        layoutParams.height = (int) height / 20;
        cashText.setX(width / 3 - width / 4 - width / 54);
        cashText.setY(height / 30 - height / 40);

        layoutParams = energyText.getLayoutParams();
        layoutParams.width = (int) (width / 4);
        layoutParams.height = (int) height / 20;
        energyText.setX(width / 3 - width / 4 - width / 54);
        energyText.setY(height / 30 - height / 40);

        ImageView coinIcon = new ImageView(context);
        ImageView cashIcon = new ImageView(context);
        ImageView energyIcon = new ImageView(context);

        coinIcon.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.coin_icon)).getBitmap(), (int) width / 10, (int) height / 15, true));
        cashIcon.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.cash_icon)).getBitmap(), (int) width / 10, (int) height / 15, true));
        energyIcon.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.energy_icon)).getBitmap(), (int) width / 10, (int) height / 15, true));

        coinLayout.addView(coinIcon);
        cashLayout.addView(cashIcon);
        energyLayout.addView(energyIcon);

        top_layout.addView(linearLayout);
    }

    private void setText() {

        Typeface textFont = Typeface.createFromAsset(getAssets(), "our_font.ttf");
        setCoin(textFont);
        setCash(textFont);
        setEnergy(textFont);
    }

    private void setCoin(Typeface textFont) {
        final TextView coinBar = new TextView(context);
        coinBar.setTextSize(20);
        coinBar.setPadding(0,0,(int)width/108,0);
        coinBar.setTypeface(textFont);
        coinText.addView(coinBar);
        coinBar.setId(R.id.coin_bar);

        ValueAnimator value = new ValueAnimator();
        value.setObjectValues(0, 999999);
        value.setDuration(250);
        value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                coinBar.setText("" + (int) animation.getAnimatedValue());
            }
        });
        value.start();
    }

    private void setCash(Typeface textFont) {
        final TextView cashBar = new TextView(context);
        cashBar.setTextSize(20);
        cashBar.setPadding(0,0,(int)width/108,0);
        cashBar.setTypeface(textFont);
        cashText.addView(cashBar);
        cashBar.setId(R.id.cash_bar);

        ValueAnimator value = new ValueAnimator();
        value.setObjectValues(0, 999999);
        value.setDuration(250);
        value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                cashBar.setText("" + (int) animation.getAnimatedValue());
            }
        });
        value.start();
    }

    private void setEnergy(Typeface textFont) {
        final TextView energyBar = new TextView(context);
        energyBar.setTextSize(20);
        energyBar.setPadding(0,0,(int)width/108,0);
        energyBar.setTypeface(textFont);
        energyText.addView(energyBar);
        energyBar.setId(R.id.cash_bar);

        ValueAnimator value = new ValueAnimator();
        value.setObjectValues(0, 5);
        value.setDuration(250);
        value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                energyBar.setText(animation.getAnimatedValue() + " / " + "5");
            }
        });
        value.start();
    }

    private void setBackground(final int drawable) {
        main_layout.animate().alpha(0.7f).setDuration(150).withEndAction(new Runnable() {
            @Override
            public void run() {
                main_layout.setBackground(ContextCompat.getDrawable(context, drawable));
                main_layout.animate().alpha(1).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                    }
                }).start();
            }
        }).start();
    }

    private void setupTabIcons() {
        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(adapter.getTabView(i, false));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new SampleFragmentPagerAdapter(getSupportFragmentManager(), this.getApplicationContext());

        adapter.addFrag(mShop, "shop");
        adapter.addFrag(mLevel, "playerInventory");
        adapter.addFrag(mStartMatch, "startMatch");
        adapter.addFrag(mTotalGallery, "gallery");
        adapter.addFrag(mRecord, "record");

        viewPager.setAdapter(adapter);
    }

    public StartMatch getStartMatch() {
        return mStartMatch;
    }

    protected void onStop() {
        super.onStop();
        Log.d(GoogleServiceApi.TAG, "mainmenuonstop!");
    }

    public void setData(String data) {
        Log.d(GoogleServiceApi.TAG, "SETdATA");
    }

    public void onShowLeaderboardsRequested() {
        if (googleServiceApi.getmSignInClicked() && googleServiceApi.getmGoogleApiClient().isConnected()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(googleServiceApi.getmGoogleApiClient()),
                    RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(GoogleServiceApi.TAG, "onactivityresult start!");
        Log.d(GoogleServiceApi.TAG, "requestCode:" + requestCode);
        Log.d(GoogleServiceApi.TAG, "resultCode:" + resultCode);
        Log.d(GoogleServiceApi.TAG, "data:" + data);

        if (requestCode == RC_UNUSED) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(GoogleServiceApi.TAG, "mainmenuonresume");
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        /*if (anim != null && !anim.isRunning())
            anim.start();*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(GoogleServiceApi.TAG, "ondestroymainmenu");
        /*googleServiceApi.getmGoogleApiClient().disconnect();

        // IAB unbind
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.d(GoogleServiceApi.TAG, "IabAsyncInProgressException");
            e.printStackTrace();
        }
        mHelper = null;*/
        finishAffinity();
    }


    @Override
    public void startSetup() {
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(GoogleServiceApi.TAG, "Problem setting up In-app Billing: " + result);
                } else {
                    // Hooray, IAB is fully set up!
                    Log.d(GoogleServiceApi.TAG, "Hororay setup IAB: " + result);
                }

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.d(GoogleServiceApi.TAG, "onwidowschange,currPage: " + currFragmentPage);
        Log.d(GoogleServiceApi.TAG, "width: " + tabLayout.getWidth());

        if (adapter.getItem(currFragmentPage) instanceof IOnFocusListenable) {
            ((IOnFocusListenable) adapter.getItem(currFragmentPage)).onWindowFocusChanged(hasFocus);
        }

        setMarginForLogo();

        if (!disableMoveUp) {
            moveUpLogo(tabLayout.getTabAt(2), 2, true);
            disableMoveUp = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(GoogleServiceApi.TAG, "onsavedInstanceMainMENU");
        super.onSaveInstanceState(outState);
    }

    private void setMarginForLogo() {

        TabLayout.Tab tab;
        tab = tabLayout.getTabAt(0);
        int marginSize;

        for (int i = 0; i < 5; i++) {
            marginSize = ((tab.getCustomView().getWidth()) * i) + (tab.getCustomView().getWidth() / 2) - (tabLayout.getHeight() / 2);
            switch (i) {
                case 0:
                    ViewGroup.LayoutParams lp = shop_logo.getLayoutParams();
                    lp.height = (int) (tabLayout.getHeight() - width / 108);
                    lp.width = (int) (tabLayout.getHeight() - width / 108);
                    shop_logo.setX(marginSize);
                    shop_logo.setY(height - tabLayout.getHeight());

                    break;
                case 1:
                    lp = level_logo.getLayoutParams();
                    lp.height = (int) (tabLayout.getHeight() - width / 108);
                    lp.width = (int) (tabLayout.getHeight() - width / 108);
                    level_logo.setX(marginSize);
                    level_logo.setY(height - tabLayout.getHeight());
                    break;
                case 2:
                    lp = battle_logo.getLayoutParams();
                    lp.height = (int) (tabLayout.getHeight() - width / 108);
                    lp.width = (int) (tabLayout.getHeight() - width / 108);
                    battle_logo.setX(marginSize);
                    battle_logo.setY(height - tabLayout.getHeight());
                    break;
                case 3:
                    lp = profile_logo.getLayoutParams();
                    lp.height = (int) (tabLayout.getHeight() - width / 108);
                    lp.width = (int) (tabLayout.getHeight() - width / 108);
                    profile_logo.setX(marginSize);
                    profile_logo.setY(height - tabLayout.getHeight());
                    break;
                case 4:
                    lp = history_logo.getLayoutParams();
                    lp.height = (int) (tabLayout.getHeight() - width / 108);
                    lp.width = (int) (tabLayout.getHeight() - width / 108);
                    history_logo.setX(marginSize);
                    history_logo.setY(height - tabLayout.getHeight());
                    break;
            }
        }
    }

    public static int convertDpToPixel(float dp, Context context) {

        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics());
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    private void moveUpLogo(final TabLayout.Tab tab, final int position, final boolean initialState) {
        //   float movePx = -(tabLayout.getHeight() / 2);
        Log.d(GoogleServiceApi.TAG, "oriposition: " + battle_logo.getY());
        float movePx = -((int) (tabLayout.getHeight() - width / 108) / 2);

        Log.d(GoogleServiceApi.TAG, "movePx: " + movePx);
        Log.d(GoogleServiceApi.TAG, "movedp: " + convertPixelsToDp(movePx, this.getApplicationContext()));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (initialState)
                    tab.setCustomView(adapter.getTabView(position, false));
            }
        };

        //  startAnimateLogo(movePx, position, runnable);
        startAnimateLogo(movePx, position);
    }

    private void moveDownLogo(final TabLayout.Tab tab, final int position) {
        // float movePx = (tabLayout.getHeight() / 6);
        float movePx = 0;

        /*Runnable runnable = new Runnable() {
            @Override
            public void run() {
                moveUpLogo(tab, position, false);
                tab.setCustomView(adapter.getTabView(position, true));
            }
        };*/

        Log.d(GoogleServiceApi.TAG, "adapterGetPrev: " + adapter.getPrevFragmentPage());
        startAnimateLogo(movePx, adapter.getPrevFragmentPage());
        moveUpLogo(tab, position, false);
        tab.setCustomView(adapter.getTabView(position, true));
    }

    private void startAnimateLogo(final float movePx, int position) {
        switch (position) {
            case 0:
                current_logo = shop_logo;
                break;
            case 1:
                current_logo = level_logo;
                break;
            case 2:
                current_logo = battle_logo;
                break;
            case 3:
                current_logo = profile_logo;
                break;
            case 4:
                current_logo = history_logo;
                break;
        }
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, movePx);
        anim.setDuration(250);
        anim.setInterpolator(new BounceInterpolator());
        anim.setFillAfter(true);
        current_logo.startAnimation(anim);
    }

    public void resetAllFragment() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mRecord.resetRecord();
                mTotalGallery.resetChallenger();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    @Override
    public void onBackPressed() {
        UniversalDialogFragment dialog = new UniversalDialogFragment(MainMenu.this,"quitGame");
        dialog.show(getFragmentManager(), null);
    }
}
