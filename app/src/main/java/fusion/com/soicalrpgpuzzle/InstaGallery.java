package fusion.com.soicalrpgpuzzle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Brian on 6/16/2016.
 */
public class InstaGallery extends Fragment {

    GalleryAdapter mAdapter;
    RecyclerView mRecyclerView;
    Context context;
    ArrayList<ImageModel> data;
    boolean firstAccess;
    Bundle savedstate;
    TotalGallery totalFrag;
    ArrayList<String> selfImageList;
    FrameLayout main_layer;
    ImageView loading_logo;
    private static final String CLIENT_ID = "8f3abe953f1f4bbf96e5f9e6d30e4d25";
    private static final String CLIENT_SECRET = "95d3014c64d74850ac0ef466bd372657";
    private static final String CALLBACK_URL = "https://www.facebook.com/gamegrabme";
    MainMenu parentAct;
    private SharedPreferences sharedPref;
    private GlobalState state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(GoogleServiceApi.TAG, "-----------------InstaGallery:Oncreate-----------------");

        super.onCreate(savedInstanceState);
        context = getParentFragment().getActivity();
        parentAct = (MainMenu) this.getActivity();

        sharedPref = context.getSharedPreferences(GoogleServiceApi.SHARED, Context.MODE_PRIVATE);

        data =  new ArrayList<>();
        totalFrag = (TotalGallery) this.getParentFragment();
        mAdapter = new GalleryAdapter(context, data);

        state = (GlobalState) context.getApplicationContext();

     //   super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(GoogleServiceApi.TAG, "-----------------InstaGallery:onCreateView-----------------");

        View v = inflater.inflate(R.layout.activity_gallery, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        main_layer = (FrameLayout) v.findViewById(R.id.main_layer);
        loading_logo = (ImageView) v.findViewById(R.id.loading_logo);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        mRecyclerView.setHasFixedSize(true);

        loading_logo.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(GoogleServiceApi.TAG, "onTouch_loading_logo");
                final InstagramApp instaObj = new InstagramApp(null, parentAct, CLIENT_ID, CLIENT_SECRET, CALLBACK_URL);

                InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {

                    @Override
                    public void onSuccess() {

                        // Change Profile Picture & Name
                        new CallProfileBitmapName().execute();


                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                        Map<String, Object> updateInstaId = new HashMap<>();
                        updateInstaId.put("instaId", instaObj.getId());
                        updateInstaId.put("googlePlayId", null);
                        mDatabase.child("Player").child(state.getUid()).updateChildren(updateInstaId);

                        state.getPlayer().setInstaId(instaObj.getId());
                        state.getPlayer().setGooglePlayProfile(null);
                        state.getPlayer().setGooglePlayId(null);


                        loading_logo.setBackground(ContextCompat.getDrawable(parentAct, R.drawable.loading_logo));
                        callSocialDownload();
                    }

                    @Override
                    public void onFail(String error) {
                        Log.d(GoogleServiceApi.TAG, "SUCESS Fail");

                    }

                    class CallProfileBitmapName extends AsyncTask<Void, Void, Void> {

                        Bitmap instaProfilePic = null;

                        @Override
                        protected Void doInBackground(Void... params) {
                            instaProfilePic = loadImageUrl(instaObj.getUserPicture());
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            //totalFrag.setProfilePicName(instaProfilePic, instaObj.getName());
                            state.getPlayer().setName(instaObj.getName());
                            state.getPlayer().setInstaProfilePic(instaProfilePic);
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
                    }

                };


                instaObj.setListener(listener);

                instaObj.authorize(true);

                return false;
            }


        });



        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(GoogleServiceApi.TAG, "-----------------InstaGallery:onActivityCreated-----------------");

        super.onActivityCreated(savedInstanceState);

       /* if (savedInstanceState != null) {
            Log.d(GoogleServiceApi.TAG, "savedInstanceState!=null");
            data = savedInstanceState.getParcelableArrayList("data");
            setAdapter();
        } else {
            Log.d(GoogleServiceApi.TAG, "savedInstanceState==null");

            if (totalFrag.getData() != null) {
                Log.d(GoogleServiceApi.TAG, "totalfragGetData!=null");
                data = totalFrag.getData();
                setAdapter();
            }
           // callSocialDownload();
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(GoogleServiceApi.TAG, "-----------------InstaGallery:onSaveInstanceState-----------------");
        outState.putParcelableArrayList("data", data);
    }

    public Bundle getBundleState() {
        return savedstate;
    }



    public void callSocialDownload() {
        loading_logo.setVisibility(View.VISIBLE);
        RetrieveInstaImage task = new RetrieveInstaImage(context);
        task.execute();
    }

    public void callLoginInstaLogo() {
        loading_logo.setBackground(ContextCompat.getDrawable(this.getActivity(), R.drawable.log_into_insta));
        loading_logo.setVisibility(View.VISIBLE);

        firstAccess = false;

    }

    public boolean getFirstAccess() {
        return firstAccess;
    }

    public void setFirstAccess() {
        firstAccess = true;
    }

    public void setAdapter() {

        mRecyclerView.setAdapter(mAdapter);
    }


    private class RetrieveInstaImage extends AsyncTask<Void, Void, ArrayList<String>> {

        private Context mContext;

        public RetrieveInstaImage (Context context){
            mContext = context;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            Log.d(GoogleServiceApi.TAG, "-----------------InstaGallery/RetrieveInstaImage:doInBackground-----------------");

            InstagramSession session = new InstagramSession(mContext);
            selfImageList = session.getSelfImageList();

            return selfImageList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(ArrayList<String> selfImageList) {

            main_layer.setBackgroundResource(R.color.whiteColor);
            main_layer.removeView(loading_logo);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRecyclerView.setLayoutParams(layoutParams);

            for (int i = 0; i < selfImageList.size(); i++) {
                ImageModel imageModel = new ImageModel();
                imageModel.setName("Image " + i);
                imageModel.setUrl(selfImageList.get(i));
                data.add(imageModel);

            }

            addFinishLogo();

            setAdapter();
            firstAccess = false;

        }

        private void addFinishLogo() {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + selfImageList.size());
            imageModel.setUrl("finishLogo");
            data.add(imageModel);
        }

    }

    @Override
    public void onDestroyView() {
        Log.d(GoogleServiceApi.TAG, "-----------------InstaGallery:onDestroyView-----------------");
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        Log.d(GoogleServiceApi.TAG, "-----------------InstaGallery:onResume-----------------");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(GoogleServiceApi.TAG, "-----------------InstaGallery:onPause-----------------");
        //totalFrag.setImageModelData(data);
        super.onPause();
    }




}
