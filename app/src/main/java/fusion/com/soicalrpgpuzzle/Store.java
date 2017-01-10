package fusion.com.soicalrpgpuzzle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fusion.com.soicalrpgpuzzle.util.IabHelper;

/**
 * Created by Brian on 6/9/2016.
 */
public class Store extends Fragment {


    Listener mListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_player_leader_board, container, false);
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    public interface Listener  {
        void startSetup();
    }
}
