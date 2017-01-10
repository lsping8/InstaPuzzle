package fusion.com.soicalrpgpuzzle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Brian on 6/13/2016.
 */
public class LoadingGame extends Activity {

    float height,width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_game);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        Bitmap stage_circle_bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.stage_circle);
        Bitmap scale_bitmap = scaleBitmap(stage_circle_bitmap, width / 5, height / 5);

        ImageView view_stage_circle = (ImageView) this.findViewById(R.id.stage_circle_1);
        view_stage_circle.setImageBitmap(scale_bitmap);

    }

    private Bitmap scaleBitmap(Bitmap bitmapToScale, float newWidth, float newHeight) {

        int width = bitmapToScale.getWidth();
        int height = bitmapToScale.getHeight();

        Log.d(GoogleServiceApi.TAG, "newwidth:" + width);
        Log.d(GoogleServiceApi.TAG, "newheight:" + height);

        Matrix matrix = new Matrix();

        matrix.postScale(newWidth / width, newHeight / height);

        return Bitmap.createBitmap(bitmapToScale, 0, 0, bitmapToScale.getWidth(), bitmapToScale.getHeight(), matrix, true);
    }
}
