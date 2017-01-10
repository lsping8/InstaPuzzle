package fusion.com.soicalrpgpuzzle;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Brian on 6/21/2016.
 */
public class CustomNestedScrollView extends NestedScrollView {

    public CustomNestedScrollView(Context context) {
        super(context);
    }

    public CustomNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        dispatchNestedPreScroll(dx,dy,consumed,null);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.d(GoogleServiceApi.TAG, "onNestedPreFling");
        dispatchNestedPreFling(velocityX,velocityY);
        return true;
    }
}
