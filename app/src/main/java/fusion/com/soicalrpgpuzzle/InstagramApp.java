package fusion.com.soicalrpgpuzzle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class InstagramApp extends Activity{

    private InstagramSession mSession;
    private InstagramDialog mDialog;
    private InstagramDialogFrag mDialogFrag;
    private OAuthAuthenticationListener mListener;
    private ProgressDialog mProgress;
    private String mAuthUrl;
    private String mTokenUrl;
    private String mAccessToken;
    private InitialLoading mInitialLoading;
    private MainMenu mMenu;

    private String mClientId;
    private String mClientSecret;

    private static int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private static int WHAT_FETCH_INFO = 2;
    private static int WHAT_HASHTAG = 3;

    /**
     * Callback url, as set in 'Manage OAuth Costumers' page
     * (https://developer.github.com/)
     */

    public static String mCallbackUrl = "https://www.facebook.com/gamegrabme";
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String API_URL = "https://api.instagram.com/v1";
    private static String response = null;
    private static final String TAG = "InstagramAPI";

    public InstagramApp(InitialLoading initiaLoadingAct, MainMenu mainMenuAct, String clientId, String clientSecret,
                        String callbackUrl) {

        if (initiaLoadingAct != null) {
            mInitialLoading = initiaLoadingAct;
            mSession = new InstagramSession(initiaLoadingAct);
        } else if (mainMenuAct != null) {
            mMenu = mainMenuAct;
            mSession = new InstagramSession(mainMenuAct);
        }

        mClientId = clientId;
        mClientSecret = clientSecret;


        mAccessToken = mSession.getAccessToken();
        mCallbackUrl = callbackUrl;
        mTokenUrl = TOKEN_URL + "?client_id=" + clientId + "&client_secret="
                + clientSecret + "&redirect_uri=" + mCallbackUrl
                + "&grant_type=authorization_code";
        mAuthUrl = AUTH_URL
                + "?client_id="
                + clientId
                + "&redirect_uri="
                + mCallbackUrl
                + "&response_type=code&scope=basic+public_content+follower_list+comments+relationships+likes";
        //           + "&response_type=code&display=touch&scope=likes+comments+relationships";

        InstagramDialogFrag.OAuthDialogListener listener = new InstagramDialogFrag.OAuthDialogListener() {
            @Override
            public void onComplete(String code) {
                getAccessToken(code);
            }

            @Override
            public void onError(String error) {
                Log.d(GoogleServiceApi.TAG, "error:" + error);
                mListener.onFail("Authorizatieon failed");
            }
        };

        if (initiaLoadingAct != null) {
            mDialogFrag = new InstagramDialogFrag(initiaLoadingAct, mAuthUrl, listener);
            mProgress = new ProgressDialog(initiaLoadingAct);
        } else if (mainMenuAct != null) {
            mDialogFrag = new InstagramDialogFrag(mainMenuAct, mAuthUrl, listener);
            mProgress = new ProgressDialog(mainMenuAct);
        }

        //  mDialog = new InstagramDialog(context, mAuthUrl, listener);



        mProgress.setCancelable(false);
    }


    private void getAccessToken(final String code) {

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Getting access token");
                int what = WHAT_FETCH_INFO;
                try {
                    URL url = new URL(TOKEN_URL);
                    // URL url = new URL(mTokenUrl + "&code=" + code);
                    Log.d(GoogleServiceApi.TAG, "Opening Token URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    // urlConnection.connect();
                    OutputStreamWriter writer = new OutputStreamWriter(
                            urlConnection.getOutputStream());
                    writer.write("client_id=" + mClientId + "&client_secret="
                            + mClientSecret + "&grant_type=authorization_code"
                            + "&redirect_uri=" + mCallbackUrl + "&code=" + code);
                    writer.flush();
                    String response = streamToString(urlConnection
                            .getInputStream());
                    Log.i(GoogleServiceApi.TAG, "response " + response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();

                    mAccessToken = jsonObj.getString("access_token");
                    // Log.i(TAG, "Got access token: " + mAccessToken);

                    String id = jsonObj.getJSONObject("user").getString("id");
                    String user = jsonObj.getJSONObject("user").getString(
                            "username");
                    String name = jsonObj.getJSONObject("user").getString(
                            "full_name");
                    String userImage = jsonObj.getJSONObject("user").getString(
                            "profile_picture");
                    mSession.storeAccessToken(mAccessToken, id, user, name,
                            userImage);

                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start();
    }

    private void fetchUserName() {
        mProgress.setMessage("Finalizing ...");

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Fetching user info");
                int what = WHAT_FINALIZE;
                try {
                    URL url = new URL(API_URL + "/users/" + mSession.getId()
                            + "/?access_token=" + mAccessToken);
                    Log.d(TAG, "Opening URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    String response = streamToString(urlConnection
                            .getInputStream());
                    System.out.println(response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();
                    String name = jsonObj.getJSONObject("data").getString(
                            "full_name");
                    // String bio =
                    // jsonObj.getJSONObject("data").getString("bio");
                    Log.i(TAG, "Got name: " + name);
                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
            }
        }.start();

    }

    public void getHashTagPic(final String accessToken1) {

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Getting hashtag picture");
                int what = WHAT_HASHTAG;
                try {
                    String urlString = API_URL + "/media/popular?access_token=" + accessToken1;
                    URL url = new URL(urlString);


                    InputStream inputStream = url.openConnection().getInputStream();
                    response = streamToString(inputStream);


                    mSession.storeResponse(response);

                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_ERROR) {
                mProgress.dismiss();
                if (msg.arg1 == 1) {
                    mListener.onFail("Failed to get access token");
                } else if (msg.arg1 == 2) {
                    mListener.onFail("Failed to get user information");
                }
            } else if (msg.what == WHAT_FETCH_INFO) {
                mProgress.dismiss();
                mListener.onSuccess();
                // fetchUserName();
            }  else {
                // mProgress.dismiss();
                // mListener.onSuccess();
            }
        }
    };

    public boolean hasAccessToken() {
        return (mAccessToken == null) ? false : true;
    }

    public void setListener(OAuthAuthenticationListener listener) {
        mListener = listener;
    }

    // getting username
    public String getUserName() {
        return mSession.getUsername();
    }

    // getting user id
    public String getId() {
        return mSession.getId();
    }

    // getting username
    public String getName() {
        return mSession.getName();
    }

    // getting user image
    public String getUserPicture() {
        return mSession.getUserImage();
    }

    // getting accesstoken
    public String getAccessToken() {
        return mSession.getAccessToken();
    }
    public String getResponse() {
        return mSession.getResponse();
    }

    public void authorize(Boolean inMainMenu) {
        // Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
        // webAuthIntent.setData(Uri.parse(AUTH_URL));
        // mCtx.startActivity(webAuthIntent);

        if (inMainMenu)
         mDialogFrag.show(mMenu.getFragmentManager(), null);
        else
        mDialogFrag.show(mInitialLoading.getFragmentManager(), null);
    }

    private String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    public void resetAccessToken() {
        if (mAccessToken != null) {
            mSession.resetAccessToken();
            mAccessToken = null;
        }
    }

    public interface OAuthAuthenticationListener {
        public abstract void onSuccess();

        public abstract void onFail(String error);
    }



}
