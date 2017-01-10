package fusion.com.soicalrpgpuzzle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Brian on 6/23/2016.
 */
public class InstagramSelfInfo extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private Intent mlogin;
    private Activity mActivity;
    private TotalGallery totalFrag;
    private InitialLoading.SignInGoogleService currActivityThread = null;

    public InstagramSelfInfo(Context context, Intent login, Activity activity, InitialLoading.SignInGoogleService currActivityThread) {
        this.mContext = context;
        this.mlogin = login;
        this.mActivity = activity;
        this.currActivityThread = currActivityThread;
    }

    public InstagramSelfInfo(Context context,  Activity activity, TotalGallery totalFrag) {
        this.mContext = context;
        this.mActivity = activity;
        this.totalFrag = totalFrag;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(GoogleServiceApi.TAG, "-----------------InstagramSelfInfo:doInBackground----------------");
        //currActivityThread.
        InstagramSession session = new InstagramSession(mContext);
        String selfProfilePicUrl = session.getSelfProfilePic();
        GlobalState state = ((GlobalState) mContext.getApplicationContext());
        GeneralImage generalImage = new GeneralImage();
        state.setGeneralImage(generalImage);
        state.setSelfProfilePicUrl(selfProfilePicUrl);
        Bitmap selfProfilePic = loadImageUrl(selfProfilePicUrl);
        state.setSelfProfilePic(selfProfilePic);
        state.setName(session.getName());
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (currActivityThread != null && mlogin != null) {
            //currActivityThread.updateFinalProgressBar();
            mActivity.startActivity(mlogin);
            mActivity.finish();
        }

        if (totalFrag != null) {
           // totalFrag.setSelfProfilePic();
            //totalFrag.setName();
        }

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
