package fusion.com.soicalrpgpuzzle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by ksloh on 10/30/2015.
 */
public class MusicManager {

    private static MusicManager mInstance;
    private MediaPlayer mMediaPlayer;

    private MusicManager() {
    }

    /**
     * Returns the single instance of this class
     *
     * @return the instance
     */
    public static MusicManager getInstance() {
        if (mInstance == null) {
            mInstance = new MusicManager();
        }
        return mInstance;
    }

    /**
     * Plays the sound with the given resource ID
     *
     * @param context         a valid `Context` reference
     * @param soundResourceId the resource ID of the sound (e.g. `R.raw.my_sound`)
     */

    public synchronized void play(final Context context, final int soundResourceId) {
        // if there's an existing stream playing already
        if (mMediaPlayer != null) {
            // stop the stream in case it's still playing
            try {
                mMediaPlayer.stop();
            } catch (Exception e) {
            }

            // release the resources
            mMediaPlayer.release();

            // unset the reference
            mMediaPlayer = null;
        }

        // create a new stream for the sound to play
        mMediaPlayer = MediaPlayer.create(context.getApplicationContext(), soundResourceId);
        mMediaPlayer.setLooping(true);
        // if the instance could be created
        if (mMediaPlayer != null) {
            // start playback
            mMediaPlayer.start();
        }
    }

    public void pauseMusic() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public void stopMusic() {
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
    }
}
