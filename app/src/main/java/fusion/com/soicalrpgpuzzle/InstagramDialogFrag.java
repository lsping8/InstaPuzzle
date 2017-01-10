package fusion.com.soicalrpgpuzzle;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Brian on 7/6/2016.
 */
public class InstagramDialogFrag  extends DialogFragment {

    private ProgressDialog mSpinner;
    private Activity activity;
    private WebView mWebView;
    private OAuthDialogListener mListener;
    private String mUrl;
    WebView web_view;

    public InstagramDialogFrag(InitialLoading activity, String url,
                               OAuthDialogListener listener) {
        this.activity = activity;
        mUrl = url;
        mListener = listener;
    }

    public InstagramDialogFrag(MainMenu activity, String url,
                               OAuthDialogListener listener) {
        this.activity = activity;
        mUrl = url;
        mListener = listener;
    }
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
       // dialog.setOwnerActivity(getActivity());
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                activity.getWindow().getDecorView().getSystemUiVisibility());

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


        return dialog;

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
     //   showImmersive(getFragmentManager());
    }

    private void showImmersive(FragmentManager manager) {
        // It is necessary to call executePendingTransactions() on the FragmentManager
        // before hiding the navigation bar, because otherwise getWindow() would raise a
        // NullPointerException since the window was not yet created.
        manager.executePendingTransactions();

        // Copy flags from the activity, assuming it's fullscreen.
        // It is important to do this after show() was called. If we would do this in onCreateDialog(),
        // we would get a requestFeature() error.
        getDialog().getWindow().getDecorView().setSystemUiVisibility(
                getActivity().getWindow().getDecorView().getSystemUiVisibility()
        );

        // Make the dialogs window focusable again
        getDialog().getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instadialog, container, false);

        mSpinner = new ProgressDialog(activity);
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        web_view = (WebView) view.findViewById(R.id.web_view);
        setUpWebView();

        return view;
    }

    private void setUpWebView() {
      //  mWebView = new WebView(activity.getApplicationContext());
        web_view.setVerticalScrollBarEnabled(false);
        web_view.setHorizontalScrollBarEnabled(false);
        web_view.setWebViewClient(new OAuthWebViewClient());
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.loadUrl(mUrl);
       // mWebView.setLayoutParams(FILL);

        web_view.getSettings().setSavePassword(false);

      //  web_view.addView(mWebView);
    }

    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith(InstagramApp.mCallbackUrl)) {
                String urls[] = url.split("=");
                mListener.onComplete(urls[1]);
                InstagramDialogFrag.this.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
          //  Log.d(TAG, "Page error: " + description);

            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onError(description);
            InstagramDialogFrag.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
          //  Log.d(TAG, "Loading URL: " + url);

            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
         /*   String title = mWebView.getTitle();
            if (title != null && title.length() > 0) {
                mTitle.setText(title);
            }
            Log.d(TAG, "onPageFinished URL: " + url);*/
            mSpinner.dismiss();
        }

    }

    public interface OAuthDialogListener {
        public abstract void onComplete(String accessToken);

        public abstract void onError(String error);
    }


}
