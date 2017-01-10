package fusion.com.soicalrpgpuzzle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Pandora on 5/28/2016.
 */
public class LeaderBoards extends Fragment {

    Listener mListener = null;

    public LeaderBoards() {
        // Required empty public constructor
    }

    public interface Listener  {
         void onShowLeaderboardsRequested();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_player_leader_board, container, false);
    }

    public void setListener(Listener l) {
        mListener = l;
    }


    public void showLeaderBoard() {
        mListener.onShowLeaderboardsRequested();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(GoogleServiceApi.TAG, "leaderboardonstop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(GoogleServiceApi.TAG, "leaderboardonpause");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(GoogleServiceApi.TAG, "leaderboardonstart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(GoogleServiceApi.TAG, "leaderboardsonresume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(GoogleServiceApi.TAG, "leaderboardsondestroy");

    }
}
