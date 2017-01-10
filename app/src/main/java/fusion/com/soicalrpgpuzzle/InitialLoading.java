package fusion.com.soicalrpgpuzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InitialLoading extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private GoogleServiceApi googleServiceApi;
    InitialLoading.SignInGoogleService signInThread;
    ProgressBar progressBar;
    private SharedPreferences sharedPref;
    private static final String API_ACCESS_TOKEN = "access_token";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private SharedPreferences.Editor editor;
    Context context;
    Intent login;
    InitialLoading currActivity;
    RelativeLayout main_layout;
    GlobalState state;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleApiClient firebaseGoogleApiClient;
    final static int RC_SIGN_IN_FIREBASE = 8999;
    static boolean calledAlready = false;
    List<String> challengerList;
    Player playerRef;
    MyValueListener myValueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_initial_loading);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        main_layout = (RelativeLayout) this.findViewById(R.id.main_layout);

        progressBar.setMax(100);

        this.currActivity = this;
        this.context = this.getApplicationContext();
        state = ((GlobalState) getApplicationContext());

        sharedPref = context.getSharedPreferences(GoogleServiceApi.SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        googleServiceApi = new GoogleServiceApi(this.getApplicationContext(), InitialLoading.this);
        state.setmGoogleApi(googleServiceApi);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        state.setDimensionScreen(size.x, size.y);

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(currActivity.getString(R.string.firebase_web_client_id))
                .requestEmail()
                .build();

        firebaseGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();


        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }


        mDatabase =  FirebaseDatabase.getInstance().getReference();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(GoogleServiceApi.TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(GoogleServiceApi.TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        new FireBaseSignIn().execute();

       /* } else {
            ObjectAnimator progress = ObjectAnimator.ofInt(progressBar, "progress", 0, 1000);
            progress.setDuration(250);
            progress.setInterpolator(new LinearInterpolator());
            progress.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    login = new Intent(InitialLoading.this, MainMenu.class);
                    startActivity(login);
                    state = null;
                    finish();
                }
            });
            progress.start();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    protected class FireBaseSignIn extends  AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(10);
            signIn();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (progressBar != null) {
                progressBar.setProgress(values[0]);
            }
        }
    }

    protected class SignInGoogleService extends AsyncTask<Void, Integer, Void> implements UniversalDialogFragment.Listener, ImageManager.OnImageLoadedListener{


        @Override
        protected Void doInBackground(Void... obj) {
         //   googleServiceApi.setInitialLoadingListener(this);
            googleServiceApi.setCurrActThread(signInThread);

            publishProgress(30);

            googleServiceApi.getmGoogleApiClient().connect();

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (progressBar != null) {
                progressBar.setProgress(values[0]);
            }
        }

        public void updateProgressBar() {

            Log.d(GoogleServiceApi.TAG, "-----------------IntialLoading:updateProgressBar----------------");

            publishProgress(50);

            if (googleServiceApi.getmGoogleApiClient().isConnected()) {
                Log.d(GoogleServiceApi.TAG, "googleServiceApi.getmGoogleApiClient().isConnected()");
                loadDataFromFrireBase();
            } else {
                Log.d(GoogleServiceApi.TAG, "googleapi is not connected!");
            }
        }


        public void loadDataFromFrireBase() {
            Log.d(GoogleServiceApi.TAG, "-----------------InitialLoading:onStartIntent----------------");

            publishProgress(70);

            login = new Intent(InitialLoading.this, MainMenu.class);


            mDatabase.child("Player").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new MyValueListener());

        }

        @Override
        public void startMainMenu() {
            publishProgress(100);
            newPlayerTutorial();
            //promptInstaLoginBox();
            /*startActivity(login);
            finish();*/
        }

        @Override
        public void onImageLoaded(Uri uri, Drawable drawable, boolean b) {

            BitmapDrawable profilePicDrawable = (BitmapDrawable) drawable;
            if (profilePicDrawable != null) {

                com.google.android.gms.games.Player currentPlayer = Games.Players.getCurrentPlayer(googleServiceApi.getmGoogleApiClient());
                Log.d(GoogleServiceApi.TAG, "Bitmap: " + profilePicDrawable.getBitmap());

                Player player = new Player(playerRef.getRank(), playerRef.getWin(), playerRef.getLose(), playerRef.getGooglePlayId(), currentPlayer.getDisplayName(), profilePicDrawable.getBitmap());
                player.setChallengerList(playerRef.getChallengerList());

                // Set Colour
                player.setColour(playerRef.getColour());

                // Set GooglePlayName
                player.setGooglePlayName(playerRef.getGooglePlayName());

                state.setPlayer(player);

                myValueEventListener.startLoadResources();

                //state.checkStartMainMenu();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(GoogleServiceApi.TAG, "-----------------InitialLoading:onActivityResult----------------");

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GoogleServiceApi.RC_SIGN_IN:
                googleServiceApi.getmGoogleApiClient().connect();
                //    signInThread.updateProgressBar();
                break;
            case RC_SIGN_IN_FIREBASE:
                Log.d(GoogleServiceApi.TAG, "success signinfirebase");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.d(GoogleServiceApi.TAG, result.getStatus().toString());
                if (result.isSuccess()) {
                    Log.d(GoogleServiceApi.TAG, "result is sucess");
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    Log.d(GoogleServiceApi.TAG, String.valueOf(resultCode));
                    Log.d(GoogleServiceApi.TAG, String.valueOf(data));
                    // Google Sign In failed, update UI appropriately
                    // ...
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        Log.d(GoogleServiceApi.TAG, "-----------------InitialLoading:onResume----------------");
        super.onResume();
        checkPlayServices();
    }

    private boolean checkPlayServices() {
        Log.d(GoogleServiceApi.TAG, "-----------------InitialLoading:checkPlayServices----------------");
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            Log.d(GoogleServiceApi.TAG, "result != ConnectionResult.SUCCESS");
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    private void promptInstaLoginBox() {
        UniversalDialogFragment dialog = new UniversalDialogFragment(this, signInThread, googleServiceApi.getmGoogleApiClient(), mDatabase, mAuth,"login");
        dialog.show(this.getFragmentManager(), null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(GoogleServiceApi.TAG, "-----------------InitialLoading:onPause----------------");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(GoogleServiceApi.TAG, "-----------------InitialLoading:onStop----------------");

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // clearMemory();
        //  finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(GoogleServiceApi.TAG, "-----------------InitialLoading:onDestroy----------------");
        System.gc();
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(firebaseGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_FIREBASE);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(GoogleServiceApi.TAG, "firebaseAuthWithGoogle:" + acct.getId());
      //  Log.d(GoogleServiceApi.TAG, "profileUrl:" + acct.getDisplayName());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Log.d(GoogleServiceApi.TAG, "credential:" + credential.getProvider());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(GoogleServiceApi.TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        mDatabase.keepSynced(true);

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(GoogleServiceApi.TAG, "signInWithCredential", task.getException());
                        }

                        signInThread = (SignInGoogleService) new SignInGoogleService().execute();
                        // ...
                    }

                });

    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    private class MyValueListener implements ValueEventListener {

        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            state.setInitialLoadingActivity(signInThread);

            if (dataSnapshot.exists()) {

                Log.d(GoogleServiceApi.TAG, "datasnapshot exist");

                state.setUid(mAuth.getCurrentUser().getUid());

                playerRef = dataSnapshot.getValue(Player.class);

                if (playerRef.getInstaId() != null) {
                    Log.d(GoogleServiceApi.TAG, "have access token");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            InstagramSession session = new InstagramSession(currActivity);
                            ArrayList<String> selfInfo = session.getSelfInfo();
                            Bitmap profilePic = loadImageUrl(selfInfo.get(0));
                            Player player = new Player(playerRef.getRank(), playerRef.getWin(), playerRef.getLose(), profilePic, selfInfo.get(1), playerRef.getInstaId());

                            // Set Challenger List
                            player.setChallengerList(playerRef.getChallengerList());

                            // Set Colour
                            player.setColour(playerRef.getColour());

                            state.setPlayer(player);

                            // Set general Image class
                            GeneralImage generalImage = new GeneralImage();
                            state.setGeneralImage(generalImage);

                            startLoadResources();

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

                    }).start();


                } else {
                    com.google.android.gms.games.Player currentPlayer = Games.Players.getCurrentPlayer(googleServiceApi.getmGoogleApiClient());

                    ImageManager mgr = ImageManager.create(currActivity);

                    myValueEventListener = this;

                    mgr.loadImage(signInThread, currentPlayer.getHiResImageUri());

                }


                //   startLoadChallenger(playerRef.getRank());





            } else {
                promptInstaLoginBox();
            }
        }

        public void startLoadResources() {
            mDatabase.child("Resources").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(GoogleServiceApi.TAG, "load resources");
                    state.setGameResources(dataSnapshot.getValue(GameResources.class));
                    startLoadMatchDetails();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void startLoadMatchDetails() {

            mDatabase.child("MatchDetails").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        state.setMatchDetails(dataSnapshot.getValue(MatchDetails.class));
                    }

                    signInThread.startMainMenu();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(GoogleServiceApi.TAG, "player:onCancelled", databaseError.toException());
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void newPlayerTutorial(){
        UniversalDialogFragment dialog = new UniversalDialogFragment(InitialLoading.this,"tutorial");
        dialog.show(getFragmentManager(), null);
    }
}
