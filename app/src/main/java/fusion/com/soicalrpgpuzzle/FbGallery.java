package fusion.com.soicalrpgpuzzle;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Brian on 6/16/2016.
 */
public class FbGallery extends Fragment {

    GalleryAdapter mAdapter;
    RecyclerView mRecyclerView;
    Context context;
    ArrayList<ImageModel> data;
    boolean firstAccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getParentFragment().getActivity();

        data =  new ArrayList<>();

        Log.d(GoogleServiceApi.TAG, "oncreatefbgallery");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_fb_gallery, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.fb_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        mRecyclerView.setHasFixedSize(true);

      /*  if (data.size() > 0) {
            mAdapter = new GalleryAdapter(context, data);
            mRecyclerView.setAdapter(mAdapter);
        }*/

        Log.d(GoogleServiceApi.TAG, "oncreateviewfbgallery");

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(GoogleServiceApi.TAG, "onactivitycreatedfbgallery");

        if (savedInstanceState != null) {
            Log.d(GoogleServiceApi.TAG, "havesavedinstancestate");
            data = savedInstanceState.getParcelableArrayList("data");
            setAdapter();
        } else {
            Log.d(GoogleServiceApi.TAG, "instancestateisnull");
          //  callSocialDownload();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(GoogleServiceApi.TAG, "onsavedinstanceGalleryfb");
        Log.d(GoogleServiceApi.TAG, "Savedinstanestatefb" + outState);
        outState.putParcelableArrayList("data", data);
    }



    public void callSocialDownload() {
        RetrieveInstaImage task = new RetrieveInstaImage(context);
        task.execute();
    }

    public boolean getFirstAccess() {
        return firstAccess;
    }

    public void setFirstAccess() {
        firstAccess = true;
    }

    public void setAdapter() {
        mAdapter = new GalleryAdapter(context, data);
        mRecyclerView.setAdapter(mAdapter);
    }


    private class RetrieveInstaImage extends AsyncTask<Void, Void, ArrayList<String>> {

        private Context mContext;

        public RetrieveInstaImage (Context context){
            mContext = context;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            Log.d(GoogleServiceApi.TAG, "contextIndoinbackground: " + mContext);
            InstagramSession session = new InstagramSession(mContext);
            ArrayList<String> selfImageList = session.getSelfImageList();

            return selfImageList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(ArrayList<String> selfImageList) {

            for (int i = 0; i < selfImageList.size(); i++) {

                ImageModel imageModel = new ImageModel();
                imageModel.setName("Image " + i);
                imageModel.setUrl(selfImageList.get(i));
                data.add(imageModel);

            }

            setAdapter();
            firstAccess = false;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(GoogleServiceApi.TAG, "ondestroyviewFBGallery");
    }
}
