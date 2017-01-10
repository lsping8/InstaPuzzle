package fusion.com.soicalrpgpuzzle;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Brian on 7/10/2016.
 */
public class ProfileDialogFragment extends DialogFragment {

    FrameLayout main_layer;
    ImageView loading_logo, close_button;
    TextView win_num, lose_num;
    GlobalState state;
    private de.hdodenhof.circleimageview.CircleImageView profilePicView;
    TextView profile_name;
    Bitmap profilePic;
    String instaName,uid, instaId;
    Record recordActivity;
    DatabaseReference mDatabase;
    RecyclerView mRecyclerView;
    ArrayList<ImageModel> data;
    GalleryAdapter mAdapter;
    ArrayList<String> playerImageList;

    public ProfileDialogFragment(Record recordActivity, DatabaseReference mDatabase, String instaId, String uid, Bitmap profilePic, String name) {
        this.profilePic = profilePic;
        this.instaName = name;
        this.recordActivity = recordActivity;
        this.mDatabase = mDatabase;
        this.uid = uid;
        this.data =  new ArrayList<>();
        this.instaId = instaId;
        mAdapter = new GalleryAdapter(recordActivity.getActivity(), data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        profilePicView = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.profile_pic);
        win_num = (TextView)v.findViewById(R.id.win_num);
        lose_num = (TextView)v.findViewById(R.id.lose_num);
        profile_name = (TextView) v.findViewById(R.id.profile_name);
        close_button = (ImageView) v.findViewById(R.id.close_button);

        state  = ((GlobalState) this.getActivity().getApplicationContext());

        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        mRecyclerView.setHasFixedSize(true);

        profilePicView.setImageBitmap(profilePic);
        profile_name.setText(instaName);

        mDatabase.child("Player").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(GoogleServiceApi.TAG, "uid: " + uid);
                win_num.setText(dataSnapshot.getValue(Player.class).win + "");
                lose_num.setText(dataSnapshot.getValue(Player.class).lose + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RetrieveInstaImage task = new RetrieveInstaImage(recordActivity.getActivity());
        task.execute();

      /*  if (state.getSelfProfilePic() != null) {
            setSelfProfilePic();
        } else {
            Bitmap icon = BitmapFactory.decodeResource(this.getActivity().getResources(),
                    R.drawable.no_profile);
            profilePicView.setImageBitmap(icon);
        }
*/
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dialog.show();

        dialog.getWindow().getDecorView().setSystemUiVisibility(
                recordActivity.getActivity().getWindow().getDecorView().getSystemUiVisibility());

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dialog.setCanceledOnTouchOutside(false);

        return dialog;


    }

    @Override
    public void onStart() {
        super.onStart();

        final View decorView = getDialog()
                .getWindow()
                .getDecorView();

        final ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));


        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
               // dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        scaleDown.setDuration(500);
        scaleDown.start();

        close_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });

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
            playerImageList = session.getPlayerImageList(instaId);

            return playerImageList;
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

            addFinishLogo();

            mRecyclerView.setAdapter(mAdapter);

        }

        private void addFinishLogo() {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + playerImageList.size());
            imageModel.setUrl("finishLogo");
            data.add(imageModel);
        }

    }
}
