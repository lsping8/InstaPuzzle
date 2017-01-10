package fusion.com.soicalrpgpuzzle;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.print.PrintHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Brian on 7/6/2016.
 */
public class UniversalDialogFragment extends DialogFragment implements ImageManager.OnImageLoadedListener {

    private InstagramApp instaObj;
    private static final String CLIENT_ID = "8f3abe953f1f4bbf96e5f9e6d30e4d25";
    private static final String CLIENT_SECRET = "95d3014c64d74850ac0ef466bd372657";
    private static final String CALLBACK_URL = "https://www.facebook.com/gamegrabme";
    private InitialLoading activity;
    private MainMenu mainMenu;
    private NewPuzzleActivity newPuzzleActivity;
    private InitialLoading.SignInGoogleService currActivityThread = null;
    private Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private static final String SHARED = "Instagram_Preferences";
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private GlobalState state;
    private UniversalDialogFragment currFragment = null;
    Listener mListener = null;
    Player currPlayer;
    float width, height;
    RelativeLayout relative;
    String dialogType;
    View view;
    RelativeLayout promptRelative;

    public UniversalDialogFragment(InitialLoading activity, InitialLoading.SignInGoogleService currActivityThread, GoogleApiClient mGoogleApiClient, DatabaseReference mDatabase, FirebaseAuth mAuth, String string) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.currActivityThread = currActivityThread;
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        this.mGoogleApiClient = mGoogleApiClient;
        this.mDatabase = mDatabase;
        this.mAuth = mAuth;
        this.currFragment = this;
        dialogType = string;
        state = (GlobalState) activity.getApplicationContext();
    }

    public UniversalDialogFragment(InitialLoading activity, String string) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        dialogType = string;
        state = (GlobalState) activity.getApplicationContext();
    }

    public UniversalDialogFragment(MainMenu activity,String string){
        this.context = activity.getApplicationContext();
        this.mainMenu = activity;
        dialogType = string;
        state = (GlobalState) mainMenu.getApplicationContext();
    }

    public UniversalDialogFragment(NewPuzzleActivity activity,String string){
        this.context = activity.getApplicationContext();
        this.newPuzzleActivity = activity;
        dialogType = string;
        state = (GlobalState) newPuzzleActivity.getApplicationContext();
    }

    private void askForTutorial() {
        promptRelative = (RelativeLayout) view.findViewById(R.id.UniversalRelative);
        promptRelative.setScaleY(0);
        promptRelative.setScaleX(0);
        ViewGroup.LayoutParams lp = promptRelative.getLayoutParams();
        lp.height = (int) (height / 2 - height / 9.6);
        lp.width = (int) (width - width / 10.8);
        promptRelative.setBackgroundResource(R.drawable.prompt_background);
        promptRelative.animate().scaleY(1).scaleX(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                ImageView yes_btn = new ImageView(context);
                ImageView no_btn = new ImageView(context);
                TextView instruction = new TextView(context);
                yes_btn.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.yes_button)).getBitmap(), (int) (width / 5.4f), (int) (height / 19.2), true));
                no_btn.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.no_button)).getBitmap(), (int) (width / 5.4f), (int) (height / 19.2), true));
                promptRelative.addView(yes_btn);
                promptRelative.addView(no_btn);
                promptRelative.addView(instruction);
                yes_btn.setY(height / 2 - height / 9.6f - height / 19.2f - height / 38.4f);
                yes_btn.setX(((width - width / 10.8f) / 4) - ((width / 5.4f) / 2));
                no_btn.setY(height / 2 - height / 9.6f - height / 19.2f - height / 38.4f);
                no_btn.setX((width - width / 10.8f) - ((width - width / 10.8f) / 4) - ((width / 5.4f) / 2));

                instruction.setMinimumWidth((int) (width - width / 10.8));
                instruction.setY(height / 38.4f);
                instruction.setGravity(Gravity.CENTER);
                Typeface textFont = Typeface.createFromAsset(context.getAssets(), "JELLYBELLY.ttf");
                instruction.setTypeface(textFont);
                instruction.setTextColor(Color.BLACK);
                instruction.setText("Do you want a tutorial?");
                instruction.setTextSize(30);
                setYes(yes_btn, promptRelative);
                setNo(no_btn);
            }
        }).setStartDelay(100).start();
    }

    private void askQuitGame(){
        promptRelative = (RelativeLayout) view.findViewById(R.id.UniversalRelative);
        promptRelative.setScaleY(0);
        promptRelative.setScaleX(0);
        ViewGroup.LayoutParams lp = promptRelative.getLayoutParams();
        lp.height = (int) (height / 2 - height / 9.6);
        lp.width = (int) (width - width / 10.8);
        promptRelative.setBackgroundResource(R.drawable.prompt_background);
        promptRelative.animate().scaleY(1).scaleX(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                ImageView yes_btn = new ImageView(context);
                ImageView no_btn = new ImageView(context);
                TextView instruction = new TextView(context);
                yes_btn.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.yes_button)).getBitmap(), (int) (width / 5.4f), (int) (height / 19.2), true));
                no_btn.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.no_button)).getBitmap(), (int) (width / 5.4f), (int) (height / 19.2), true));
                promptRelative.addView(yes_btn);
                promptRelative.addView(no_btn);
                promptRelative.addView(instruction);
                yes_btn.setY(height / 2 - height / 9.6f - height / 19.2f - height / 38.4f);
                yes_btn.setX(((width - width / 10.8f) / 4) - ((width / 5.4f) / 2));
                no_btn.setY(height / 2 - height / 9.6f - height / 19.2f - height / 38.4f);
                no_btn.setX((width - width / 10.8f) - ((width - width / 10.8f) / 4) - ((width / 5.4f) / 2));

                instruction.setMinimumWidth((int) (width - width / 10.8));
                instruction.setY(height / 38.4f);
                instruction.setGravity(Gravity.CENTER);
                Typeface textFont = Typeface.createFromAsset(context.getAssets(), "JELLYBELLY.ttf");
                instruction.setTypeface(textFont);
                instruction.setTextColor(Color.BLACK);
                instruction.setText("Exit the game?");
                instruction.setTextSize(30);
                yes_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
                no_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDialog().cancel();
                        getDialog().dismiss();
                    }
                });
            }
        }).setStartDelay(100).start();
    }

    private void askSurrender(){
        promptRelative = (RelativeLayout) view.findViewById(R.id.UniversalRelative);
        promptRelative.setScaleY(0);
        promptRelative.setScaleX(0);
        ViewGroup.LayoutParams lp = promptRelative.getLayoutParams();
        lp.height = (int) (height / 2 - height / 9.6);
        lp.width = (int) (width - width / 10.8);
        promptRelative.setBackgroundResource(R.drawable.prompt_background);
        promptRelative.animate().scaleY(1).scaleX(1).setDuration(250).setInterpolator(new OvershootInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                ImageView yes_btn = new ImageView(context);
                ImageView no_btn = new ImageView(context);
                TextView instruction = new TextView(context);
                yes_btn.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.yes_button)).getBitmap(), (int) (width / 5.4f), (int) (height / 19.2), true));
                no_btn.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.no_button)).getBitmap(), (int) (width / 5.4f), (int) (height / 19.2), true));
                promptRelative.addView(yes_btn);
                promptRelative.addView(no_btn);
                promptRelative.addView(instruction);
                yes_btn.setY(height / 2 - height / 9.6f - height / 19.2f - height / 38.4f);
                yes_btn.setX(((width - width / 10.8f) / 4) - ((width / 5.4f) / 2));
                no_btn.setY(height / 2 - height / 9.6f - height / 19.2f - height / 38.4f);
                no_btn.setX((width - width / 10.8f) - ((width - width / 10.8f) / 4) - ((width / 5.4f) / 2));

                instruction.setMinimumWidth((int) (width - width / 10.8));
                instruction.setY(height / 38.4f);
                instruction.setGravity(Gravity.CENTER);
                Typeface textFont = Typeface.createFromAsset(context.getAssets(), "JELLYBELLY.ttf");
                instruction.setTypeface(textFont);
                instruction.setTextColor(Color.BLACK);
                instruction.setText("Surrender?");
                instruction.setTextSize(30);
                yes_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newPuzzleActivity.surrender();
                    }
                });
                no_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDialog().cancel();
                        getDialog().dismiss();
                    }
                });
            }
        }).setStartDelay(100).start();
    }

    private void setYes(View v, final RelativeLayout promptRelative) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptRelative.animate().translationY(height).setDuration(250).setInterpolator(new AnticipateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            for (int i = 0; i < promptRelative.getChildCount(); i++) {
                                ((BitmapDrawable) ((ImageView) (promptRelative.getChildAt(i))).getDrawable()).getBitmap().recycle();
                            }
                        } catch (Exception e) {
                            Log.d("Ignore_this", "setYes");
                        }
                        ((BitmapDrawable) promptRelative.getBackground()).getBitmap().recycle();
                        promptRelative.setBackground(null);
                        promptRelative.removeAllViews();
                        Intent startTutorial = new Intent(getActivity(),TutorialActivity.class);
                        getActivity().startActivity(startTutorial);
                        getDialog().cancel();
                        getDialog().dismiss();
                        getActivity().finish();
                    }
                }).start();
            }
        });
    }

    private void setNo(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitialLoading initialLoading = (InitialLoading) getActivity();
                initialLoading.startActivity(initialLoading.login);
                getDialog().cancel();
                getDialog().dismiss();
                initialLoading.finish();
            }
        });
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

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
        view = inflater.inflate(R.layout.fragment_universaldialog, container, false);

        String[] dimension = state.getDimensionScreen().split(",");
        height = Float.valueOf(dimension[1]);
        width = Float.valueOf(dimension[0]);

        RelativeLayout backGround = (RelativeLayout) view.findViewById(R.id.backGround);
        backGround.setBackgroundColor(Color.TRANSPARENT);
        promptRelative = (RelativeLayout) view.findViewById(R.id.UniversalRelative);

        Button login = (Button) view.findViewById(R.id.login);
        Button skip = (Button) view.findViewById(R.id.skip);
        setCancelable(false);

        switch (dialogType) {
            case "login":
                ViewGroup.LayoutParams lp = promptRelative.getLayoutParams();
                lp.height = (int)(height/4.8);
                lp.width = (int)(width/2.7);
                login.setText("Login");
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        instaObj = new InstagramApp(activity, null, CLIENT_ID,
                                CLIENT_SECRET, CALLBACK_URL);

                        InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {

                            @Override
                            public void onSuccess() {
                                Log.d(GoogleServiceApi.TAG, "SUCESS INSTA");

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        // New player Info
                                        Bitmap profilePic = loadImageUrl(instaObj.getUserPicture());

                                        assignNewColour(null, null, profilePic);

                                        // New General Image
                                        GeneralImage generalImage = new GeneralImage();
                                        state.setGeneralImage(generalImage);

                                        // New Game Resources
                                        GameResources resources = new GameResources(0, 0, 0);
                                        mDatabase.child("Resources").child(mAuth.getCurrentUser().getUid()).setValue(resources);
                                        state.setGameResources(resources);

                                        // Set player ownUID
                                        state.setUid(mAuth.getCurrentUser().getUid());

                                        // New Track info
                                        List<String> colour = new ArrayList<String>();
                                        colour.add("white");
                                        Inventory inventory = new Inventory(colour);
                                        state.setInventory(inventory);
                                        Handler handler = new Handler();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                activity.newPlayerTutorial();
                                            }
                                        });
                                        //currActivityThread.startMainMenu();
                                    }
                                }).start();
                            }

                            @Override
                            public void onFail(String error) {
                                Log.d(GoogleServiceApi.TAG, "SUCESS Fail");
                                Toast.makeText(activity, error, Toast.LENGTH_SHORT)
                                        .show();
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

                        };
                        instaObj.setListener(listener);
                        instaObj.authorize(false);
                    }
                });
                skip.setY(height/9.6f);
                skip.setText("Skip");
                skip.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        com.google.android.gms.games.Player currentPlayer = Games.Players.getCurrentPlayer(mGoogleApiClient);

                        ImageManager mgr = ImageManager.create(activity);
                        mgr.loadImage(currFragment, currentPlayer.getHiResImageUri());
                        activity.newPlayerTutorial();

                    }
                });
                break;

            case "tutorial":
                skip.setVisibility(View.INVISIBLE);
                login.setVisibility(View.INVISIBLE);
                askForTutorial();
                break;

            case "quitGame":
                skip.setVisibility(View.INVISIBLE);
                login.setVisibility(View.INVISIBLE);
                askQuitGame();
                break;

            case "surrender":
                skip.setVisibility(View.INVISIBLE);
                login.setVisibility(View.INVISIBLE);
                askSurrender();
                break;
        }
        return view;
    }

    @Override
    public void onImageLoaded(Uri uri, Drawable drawable, boolean b) {

        BitmapDrawable profilePicDrawable = (BitmapDrawable) drawable;
        if (profilePicDrawable != null) {

            final com.google.android.gms.games.Player googlePlayPlayer = Games.Players.getCurrentPlayer(mGoogleApiClient);

            assignNewColour(googlePlayPlayer, profilePicDrawable, null);

            // New Game Resources
            GameResources resources = new GameResources(0, 0, 0);
            mDatabase.child("Resources").child(mAuth.getCurrentUser().getUid()).setValue(resources);
            state.setGameResources(resources);

            // Set player ownUID
            state.setUid(mAuth.getCurrentUser().getUid());

            // New Track info
            List<String> colour = new ArrayList<String>();
            colour.add("white");
            Inventory inventory = new Inventory(colour);
            state.setInventory(inventory);


            currActivityThread.startMainMenu();
        }
    }

    private void addNewChallenger() {
        String key = mDatabase.child("Challenger").child(state.getPlayer().getRank()).push().getKey();
        Map<String, Object> challengerUpdates = new HashMap<>();
        challengerUpdates.put("/Challenger/" + state.getPlayer().getRank() + "/" + key, mAuth.getCurrentUser().getUid());
        mDatabase.updateChildren(challengerUpdates);
        key = mDatabase.child("Challenger").child(state.getPlayer().getRank()).push().getKey();
        challengerUpdates.put("/Challenger/" + state.getPlayer().getRank() + "/" + key, "MQArRbxargNOCkAN7PYq0HLnGun1");
        mDatabase.updateChildren(challengerUpdates);
    }

    private void assignNewColour(final com.google.android.gms.games.Player googlePlayPlayer, final BitmapDrawable profilePicDrawable, final Bitmap instaProfilePic) {


        mDatabase.child("GlobalColour").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String colour = dataSnapshot.getValue().toString();

                    // Set player database info
                    if (instaProfilePic == null) {

                        currPlayer = new Player("Novice", 0, 0, googlePlayPlayer.getPlayerId(), googlePlayPlayer.getDisplayName(), profilePicDrawable.getBitmap());

                        // Set googleplay Name
                        currPlayer.setGooglePlayName(googlePlayPlayer.getDisplayName());

                        Player databasePlayer = new Player("Novice", googlePlayPlayer.getPlayerId(), 0, 0, colour, googlePlayPlayer.getDisplayName());
                        mDatabase.child("Player").child(mAuth.getCurrentUser().getUid()).setValue(databasePlayer);

                    } else {

                        currPlayer = new Player("Novice", 0, 0, instaProfilePic, instaObj.getName(), instaObj.getId());

                        Player databasePlayer = new Player("Novice", 0, 0, instaObj.getId(), colour);
                        mDatabase.child("Player").child(mAuth.getCurrentUser().getUid()).setValue(databasePlayer);
                    }

                    // Set state Player
                    currPlayer.setColour(colour);
                    state.setPlayer(currPlayer);


                    // Set database next colour
                    switch (colour) {
                        case "Red":
                            mDatabase.child("GlobalColour").setValue("Blue");
                            break;
                        case "Blue":
                            mDatabase.child("GlobalColour").setValue("Green");
                            break;
                        case "Green":
                            mDatabase.child("GlobalColour").setValue("Yellow");
                            break;
                        case "Yellow":
                            mDatabase.child("GlobalColour").setValue("Red");
                            break;
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface Listener {
        void startMainMenu();
    }
}
