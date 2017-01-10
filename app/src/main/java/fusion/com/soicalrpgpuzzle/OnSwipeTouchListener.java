package fusion.com.soicalrpgpuzzle;

/**
 * Created by Pandora on 5/11/2016.
 */
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;
    View view = null;

    public OnSwipeTouchListener (Context ctx){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        Log.d(GoogleServiceApi.TAG, "sucess");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        view = v;
        Log.d(GoogleServiceApi.TAG, "sucess2.1");
        Log.d(GoogleServiceApi.TAG, event.getActionMasked() + "");
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDoubleTap(MotionEvent e){
            onDoubleClick(view);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if(e.getAction() == MotionEvent.ACTION_DOWN){
                onTouchDown(view);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e){
            Log.d(GoogleServiceApi.TAG, "sucess3");
            onLongClick(view);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(GoogleServiceApi.TAG, "sucess7");
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                Log.d(GoogleServiceApi.TAG, "diffY" + diffY + "");
                Log.d(GoogleServiceApi.TAG, "diffX" + diffX + "");
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight(view);
                        } else {
                            onSwipeLeft(view);
                        }
                    }
                    result = true;
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom(view);
                    } else {
                        onSwipeTop(view);
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight(final View v) {
        Log.d(GoogleServiceApi.TAG, "sucess4");
    }

    public void onSwipeLeft(final View v) {
    }

    public void onSwipeTop(final View v) {
    }

    public void onSwipeBottom(final View v) {
    }

    public void onTouchDown(final View v){
    }

    public void onDoubleClick(final View v){

    }

    public void onLongClick(final View v){
        Log.d(GoogleServiceApi.TAG, "sucess5");
    }
}
