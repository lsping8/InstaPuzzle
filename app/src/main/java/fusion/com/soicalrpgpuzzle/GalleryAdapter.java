package fusion.com.soicalrpgpuzzle;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Brian on 6/16/2016.
 */
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ImageModel> data = new ArrayList<>();
    View v;
    boolean access;
    float width;
    Bitmap theBitmap;
    ImageView instaLogoView;
    Boolean firstTime;

    public GalleryAdapter(Context context, List<ImageModel> data) {
        this.context = context;
        this.data = data;
        this.access = true;
        this.firstTime = true;
        GlobalState state = ((GlobalState) context.getApplicationContext());
        String[] dimension = state.getDimensionScreen().split(",");
        width = Float.valueOf(dimension[0]);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, parent, false);
        viewHolder = new MyItemHolder(v, context);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        (holder).setIsRecyclable(false);

        if (holder.getAdapterPosition() == 20) {
            Log.d(GoogleServiceApi.TAG, "ontouchinstalogo");

                ((MyItemHolder) holder).mImg.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        InstagramSession session = new InstagramSession(context);
                        Log.d(GoogleServiceApi.TAG, "sessionUsername: " + session.getUsername());
                        Uri uri = Uri.parse("http://instagram.com/_u/" + session.getUsername());
                        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                        likeIng.setPackage("com.instagram.android");

                        try {
                            context.startActivity(likeIng);
                        } catch (ActivityNotFoundException e) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://instagram.com/" + session.getUsername())));
                        }

                        return false;
                    }
                });


                /*instaLogoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                    *//**//*InstagramSession session = new InstagramSession(context);
                    Log.d(GoogleServiceApi.TAG, "sessionUsername: " + session.getUsername());
                    Uri uri = Uri.parse("http://instagram.com/_u/" + session.getUsername());
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        context.startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://instagram.com/" + session.getUsername())));
                    }*//**//*
                        Log.d(GoogleServiceApi.TAG, "v" + v);
                        Log.d(GoogleServiceApi.TAG, "ontouchinstalogo");
                        return false;
                    }
                });*/



        }

       // Log.d(GoogleServiceApi.TAG, "position: " + position + "stopload:" + stopload);

            /*int id = v.getResources().getIdentifier("fusion.com.soicalrpgpuzzle:drawable/" + "view_more_insta", null, null);
            ((MyItemHolder) holder).mImg.setImageResource(id);
            ((MyItemHolder) holder).mImg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InstagramSession session = new InstagramSession(context);
                    Log.d(GoogleServiceApi.TAG, "sessionUsername: " + session.getUsername());
                    Uri uri = Uri.parse("http://instagram.com/_u/" + session.getUsername());
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        context.startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://instagram.com/" + session.getUsername())));
                    }
                    return false;
                }
            });*/


        if (!data.get(position).getUrl().equals("finishLogo")) {
            Log.d(GoogleServiceApi.TAG, "positionloadUrl:" + position);
            Glide.with(context).load(data.get(position).getUrl())
                    .thumbnail(0.5f)
                    .override(200, 200)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(((MyItemHolder) holder).mImg);


            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        theBitmap = Glide.
                                with(context).
                                load(data.get(position).getUrl()).
                                asBitmap().
                                into(100, 100). // Width and height
                                get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }).start();*/

           // (((MyItemHolder) holder).mImg).setImageBitmap(roundEdge(theBitmap));

        } else {

            Log.d(GoogleServiceApi.TAG, "mmImg" + ((MyItemHolder) holder).mImg);
            Log.d(GoogleServiceApi.TAG, "adapterposition" + holder.getAdapterPosition());
            Glide.with(context).load(R.drawable.view_more_insta).into(((MyItemHolder) holder).mImg);

        }

           /* int id = v.getResources().getIdentifier("fusion.com.soicalrpgpuzzle:drawable/" + "view_more_insta", null, null);
            ((MyItemHolder) holder).mImg.setImageResource(id); */

            /*if (holder.getAdapterPosition() == 20) {
                if (access) {
                    ((MyItemHolder) holder).mImg.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Log.d(GoogleServiceApi.TAG, "finnalmmImg" + ((MyItemHolder) holder).mImg);
                        InstagramSession session = new InstagramSession(context);
                        Log.d(GoogleServiceApi.TAG, "sessionUsername: " + session.getUsername());
                        Uri uri = Uri.parse("http://instagram.com/_u/" + session.getUsername());
                        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                        likeIng.setPackage("com.instagram.android");

                        try {
                            context.startActivity(likeIng);
                        } catch (ActivityNotFoundException e) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://instagram.com/" + session.getUsername())));
                        }
                            return false;
                        }
                    });
                    access = false;
                }

            }*/
        /*if ((((MyItemHolder) holder).mImg.getDrawable()) != null) {
            Drawable bmp = (((MyItemHolder) holder).mImg.getDrawable());
            Log.d(GoogleServiceApi.TAG, "drawable: " + bmp);
        }

        if (((MyItemHolder) holder).mImg.getDrawable() == context.getResources().getDrawable(R.drawable.view_more_insta)) {
            ((MyItemHolder) holder).mImg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(GoogleServiceApi.TAG, "finnalmmImg" + ((MyItemHolder) holder).mImg);
                    InstagramSession session = new InstagramSession(context);
                    Log.d(GoogleServiceApi.TAG, "sessionUsername: " + session.getUsername());
                    Uri uri = Uri.parse("http://instagram.com/_u/" + session.getUsername());
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        context.startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://instagram.com/" + session.getUsername())));
                    }
                    return false;
                }
            });
        }*/



    }

    private Bitmap roundEdge(Bitmap bitmap) {

        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        float roundPx = width / 50;
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        Context context;

        public MyItemHolder(View itemView, Context context) {
            super(itemView);

            mImg = (ImageView) itemView.findViewById(R.id.item_img);

            this.context = context;

        }

    }

    public static class InstaLogoHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        Context context;

        public InstaLogoHolder(View itemView, Context context) {
            super(itemView);

            mImg = (ImageView) itemView.findViewById(R.id.insta_img);

            this.context = context;

        }

    }
}

