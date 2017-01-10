package fusion.com.soicalrpgpuzzle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easyandroidanimations.library.SlideInAnimation;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class MainPuzzleActivity extends Activity {

    Context context;
    RelativeLayout topRelativeLayout, action_menu;
    LinearLayout gameRowLayout, game_row;
    boolean firstRun;
    ImageView sourceProfileView, targetProfileView, versusView;
    Typewriter sourceName, targetName;
    AnimationSound soundEffect;
    Animation sourceProfileAnimation, versusAnimation, targetProfileAnimation, gameFrameAnimation;
    Bitmap sourceProfileBitmap, targetProfileBitmap, scaledBitmap;
    ArrayList<String> nameList;
    GeneralImage generalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_puzzle);
        context = this.getBaseContext();


        GlobalState state = ((GlobalState) getApplicationContext());
        generalImage = state.getGeneralImage();

        Log.d(GoogleServiceApi.TAG, generalImage.getPopularBitmapList().get(0).toString());

        // Create global configuration and initialize ImageLoader with this config
       // ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
       // ImageLoader.getInstance().init(config);

     //   mainPuzzleLayout = (LinearLayout) MainPuzzleActivity.this.findViewById(R.id.mainPuzzleLayout);
        topRelativeLayout = (RelativeLayout)MainPuzzleActivity.this.findViewById(R.id.topRelativeLayout);
        sourceProfileView = (ImageView)MainPuzzleActivity.this.findViewById(R.id.source_profile);
        versusView =  (ImageView)MainPuzzleActivity.this.findViewById(R.id.versus);
        targetProfileView = (ImageView)MainPuzzleActivity.this.findViewById(R.id.target_profile);
        sourceName = (Typewriter)MainPuzzleActivity.this.findViewById(R.id.sourceName);
        targetName = (Typewriter)MainPuzzleActivity.this.findViewById(R.id.targetName);
        action_menu = (RelativeLayout) MainPuzzleActivity.this.findViewById(R.id.action_menu);
        gameRowLayout = (LinearLayout) MainPuzzleActivity.this.findViewById(R.id.gameRowLayout);
        Log.d("HELO1", gameRowLayout.toString());

        ArrayList<String> imageList = new ArrayList<String>();
        nameList = new ArrayList<String>();

        nameList.add("brian.koey");
        nameList.add("lee._.yaoyao");

       //  Adding row into gameRowLayout
        for (int i = 0; i < 4; i++) {
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);
            ll.setId(i);
            gameRowLayout.addView(ll);
        }

        new getProfilePic().execute(generalImage);

        new processImage().execute(generalImage);


        /**
        if (savedInstanceState != null) {
            Log.e ("saveInstance firstrun", firstRun + "");
            firstRun = savedInstanceState.getBoolean("firstRun");
        } else
            firstRun = true;
        **/

     //   new ImageProcess().execute("https://scontent.cdninstagram.com/hphotos-xta1/t51.2885-15/s640x640/sh0.08/e35/12407596_928035977232090_857124673_n.jpg");


    }

    private int getIntResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return resId;
    }

    private class getProfilePic extends AsyncTask<GeneralImage, Void, ArrayList<Bitmap>> {

        @Override
        protected  ArrayList<Bitmap> doInBackground(GeneralImage... obj) {

            ArrayList<Bitmap> beforeScaleBitmap = convertUrltoImage(obj[0]);
            return beforeScaleBitmap;
        }


        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmapList) {

            sourceProfileBitmap = getRoundedBitmap(bitmapList.get(0));
            targetProfileBitmap = getRoundedBitmap(bitmapList.get(1));

            sourceProfileView.setImageBitmap(sourceProfileBitmap);

            soundEffect = new AnimationSound(getApplicationContext(), R.raw.info_swoop_in1);

            sourceProfileAnimation = new TranslateAnimation(-150, 0,0, 0);
            sourceProfileAnimation.setDuration(500);
            sourceProfileView.startAnimation(sourceProfileAnimation);
            soundEffect.startsound();

            sourceProfileAnimation.setAnimationListener(new myAnimationListener());

        }

        private ArrayList<Bitmap> convertUrltoImage(GeneralImage obj) {
            try {

                ArrayList <Bitmap> bitmapList = new ArrayList<Bitmap>();


                URL url = new URL(obj.getSelfProfilePic());
                //URL url = new URL("https://scontent.cdninstagram.com/hphotos-xfa1/t51.2885-19/s150x150/10890551_1664038823836732_1049704691_a.jpg");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                // connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Log.d("myBitmap: " , myBitmap.toString());
                bitmapList.add(myBitmap);

                url = new URL(obj.getLinkProfilePic());
                connection = (HttpsURLConnection) url.openConnection();
                // connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
                Log.d("myBitmap: " , myBitmap.toString());
                bitmapList.add(myBitmap);

                return bitmapList;

            } catch (Exception e) {
                Log.d("Error: ", e.toString());
                return null;
            }
        }

        private class  myAnimationListener implements Animation.AnimationListener {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                 if (animation.equals(sourceProfileAnimation)) {
                     versusView.setImageResource(R.drawable.versus);
                     versusAnimation = new TranslateAnimation(0,0, -150,0);
                     versusAnimation.setDuration(500);
                     versusView.startAnimation(versusAnimation);
                     soundEffect = new AnimationSound(getApplicationContext(), R.raw.info_swoop_in1);
                     soundEffect.startsound();
                     versusAnimation.setAnimationListener(new myAnimationListener());
                 } else if (animation.equals(versusAnimation)) {
                     targetProfileView.setImageBitmap(targetProfileBitmap);
                     targetProfileAnimation = new TranslateAnimation( 150 , 0 , 0, 0);
                     targetProfileAnimation.setDuration(500);
                     targetProfileView.startAnimation(targetProfileAnimation);
                     soundEffect = new AnimationSound(getApplicationContext(), R.raw.info_swoop_in1);
                     soundEffect.startsound();
                     targetProfileAnimation.setAnimationListener(new myAnimationListener());
                 } else if (animation.equals(targetProfileAnimation)) {

                     Typeface type = Typeface.createFromAsset(getAssets(), "Pacifico.ttf");

                     sourceName.setTextColor(Color.parseColor("#e1e1e1"));
                     sourceName.setTypeface(type, Typeface.BOLD);
                     sourceName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                     sourceName.setCharacterDelay(30);
                     sourceName.animateText(context, nameList.get(0), action_menu, false);

                     targetName.setTextColor(Color.parseColor("#e1e1e1"));
                     targetName.setTypeface(type, Typeface.BOLD);
                     targetName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                     targetName.setCharacterDelay(30);
                     targetName.animateText(context, nameList.get(1), action_menu, true);


                     sourceProfileAnimation = new TranslateAnimation(-150, 0,0, 0);
                     sourceProfileAnimation.setDuration(500);
                     sourceProfileView.startAnimation(sourceProfileAnimation);
                    // Bitmap bmpOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.puzzle_frame);
                    // BitmapDrawable bmpBackground = new BitmapDrawable(getResources(), bmpOriginal);
                     gameRowLayout.setBackground(getResources().getDrawable(R.drawable.puzzle_frame));

                 }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }

    }

    private class processImage extends AsyncTask<GeneralImage, Void, ArrayList<Bitmap>> {

        @Override
        protected ArrayList<Bitmap> doInBackground(GeneralImage... obj) {
            scaledBitmap = loadImageUrl(obj[0].getPopularImageList().get(0));
            ArrayList<Bitmap> chunkedImages = splitImage(scaledBitmap, 16);

            return chunkedImages;
        }

        private Bitmap loadImageUrl(String imageUrl) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Bitmap scaledBitmap = scaleBitmap(myBitmap, gameRowLayout.getWidth() - 60, gameRowLayout.getHeight() - 20);
                return scaledBitmap;
            } catch (Exception e) {
                Log.d("error", e.toString());
                return null;
            }
        }

        private Bitmap scaleBitmap(Bitmap bitmapToScale, float newWidth, float newHeight) {
            if (bitmapToScale == null)
                return null;

            int width = bitmapToScale.getWidth();
            int height = bitmapToScale.getHeight();

            Matrix matrix = new Matrix();

            matrix.postScale(newWidth / width, newHeight / height);

            return Bitmap.createBitmap(bitmapToScale, 0, 0, bitmapToScale.getWidth(), bitmapToScale.getHeight(), matrix, true);
        }

        private ArrayList<Bitmap> splitImage(Bitmap bitmap, int chunkNumbers) {

            //For the number of rows and columns of the grid to be displayed
            int rows, cols;

            //For height and width of the small image chunks
            int chunkHeight, chunkWidth;

            //To store all the small image chunks in bitmap format in this list
            ArrayList<Bitmap> chunkedImages = new ArrayList<>(chunkNumbers);

            //Getting the scaled bitmap of the source image
            /*BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = drawable.getBitmap();*/
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

            //rows = cols = (int) Math.sqrt(chunkNumbers);
            rows = 4;
            cols = 4;
            chunkHeight = bitmap.getHeight() / rows;
            chunkWidth = bitmap.getWidth() / cols;

            int yCoord = 0;
            for (int x = 0; x < rows; x++) {
                int xCoord = 0;
                for (int y = 0; y < cols; y++) {
                    chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                    xCoord += chunkWidth;
                }
                yCoord += chunkHeight;
            }
            return chunkedImages;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> chunkedImages) {

            Toast.makeText(MainPuzzleActivity
                    .this, "Done splitting image", Toast.LENGTH_LONG)
                    .show();

                    //dropImage(chunkedImages);
        }

        private void dropImage(ArrayList<Bitmap> chunkedImages) {

            int delayApply = 150;

            game_row = (LinearLayout) MainPuzzleActivity.this.findViewById(0);

            for (int i = 0; i < 4; i++) {

                final ImageView image = new ImageView(MainPuzzleActivity.this.getApplicationContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 5, 5, 0);

                image.setImageBitmap(chunkedImages.get(i));
                image.setLayoutParams(lp);
                image.setId(i + 100);
                image.setVisibility(View.INVISIBLE);

                int delay = delayApply * i;

                image.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        image.setEnabled(false);
                        image.setVisibility(View.VISIBLE);
                        new SlideInAnimation(image)
                                .setDirection(com.easyandroidanimations.library.Animation.DIRECTION_UP)
                                .animate();
                        image.setEnabled(true);
                    }
                }, delay);

                game_row.addView(image);
            }

        }
    }


    /**
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.e("firstRun: ", firstRun + "");

        if (firstRun) {
           // firstRun = false;

            if (hasFocus) {

                dialog = new Dialog(MainPuzzleActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.dialoglayout);

                // Prevent black background
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();

                // Dialog stretch full screen
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);




                DialogFragment newFragment = new DialogActivity();
                FragmentManager manager = getFragmentManager();
               // manager.executePendingTransactions();
            //    newFragment.getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            //    newFragment.setCancelable(false);
                newFragment.show(manager, "dialogbox");




            }
        }

    }
    **/

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("firstRun", firstRun);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        firstRun = false;
    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
