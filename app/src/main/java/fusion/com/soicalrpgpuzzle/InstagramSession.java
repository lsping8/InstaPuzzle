package fusion.com.soicalrpgpuzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Manage access token and user name. Uses shared preferences to store access
 * token and user name.
 *
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * @author Lorensius W. L T <lorenz@londatiga.net>
 *
 */
public class InstagramSession {

    private SharedPreferences sharedPref;
    private Editor editor;

    private static final String SHARED = "Instagram_Preferences";
    private static final String API_USERNAME = "username";
    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_ACCESS_TOKEN = "access_token";
    private static final String API_USER_IMAGE = "user_image";
    private static final String API_URL = "https://api.instagram.com/v1";

    public InstagramSession(Context context) {
        if (context == null) {
            Log.d(GoogleServiceApi.TAG, "context is null");
        }
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void storeAccessToken(String accessToken, String id,
                                 String username, String name, String image) {
        editor.putString(API_ID, id);
        editor.putString(API_NAME, name);
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.putString(API_USERNAME, username);
        editor.putString(API_USER_IMAGE, image);
        editor.commit();
    }

    public void storeAccessToken(String accessToken) {
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    public void storeResponse(String response) {
        editor.putString("Response", response);
        editor.commit();
    }

    /**
     * Reset access token and user name
     */
    public void resetAccessToken() {
        editor.putString(API_ID, null);
        editor.putString(API_NAME, null);
        editor.putString(API_ACCESS_TOKEN, null);
        editor.putString(API_USERNAME, null);
        editor.putString(API_USER_IMAGE, null);
        editor.commit();
    }

    /**
     * Get user name
     *
     * @return User name
     */
    public String getUsername() {
        return sharedPref.getString(API_USERNAME, null);
    }

    /**
     *
     * @return
     */
    public String getId() {
        return sharedPref.getString(API_ID, null);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return sharedPref.getString(API_NAME, null);
    }

    /**
     * Get access token
     *
     * @return Access token
     */
    public String getAccessToken() {
        return sharedPref.getString(API_ACCESS_TOKEN, null);
    }

    /**
     * Get userImage
     *
     * @return userImage
     */
    public String getUserImage() {
        return sharedPref.getString(API_USER_IMAGE, null);
    }

    public String getResponse() {
        return sharedPref.getString("Response", null);
    }

    public ArrayList<String> getSelfImageList() {

        ArrayList<String> selfImageList = new ArrayList<String>();

        try {
            Log.d("accesstoken:" ,  getAccessToken() + "");
            String urlString = API_URL + "/users/self/media/recent/?access_token=" + getAccessToken();
            URL url = new URL(urlString);

            InputStream inputStream = url.openConnection().getInputStream();
            String response = streamToString(inputStream);

          //  Log.d(GoogleServiceApi.TAG, "response: " + response);

            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray arrayData = jsonObj.getJSONArray("data");


            for (int i = 0; i < arrayData.length(); i++) {
                JSONObject arrayObject = (JSONObject) arrayData.get(i);
                JSONObject imageObj = arrayObject.getJSONObject("images");
                JSONObject low_reso_image = imageObj.getJSONObject("low_resolution");
                String imageUrl = low_reso_image.getString("url");
                Log.d("selfImageList url:", imageUrl.toString());
                selfImageList.add(imageUrl);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return selfImageList;
    }

    public ArrayList<String> getPlayerImageList(String instaId) {

        ArrayList<String> playerImageList = new ArrayList<String>();

        try {
            Log.d("accesstoken:" ,  getAccessToken() + "");
            String urlString = API_URL + "/users/" + instaId + "/media/recent/?access_token=" + getAccessToken();
            URL url = new URL(urlString);

            InputStream inputStream = url.openConnection().getInputStream();
            String response = streamToString(inputStream);

            //  Log.d(GoogleServiceApi.TAG, "response: " + response);

            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray arrayData = jsonObj.getJSONArray("data");


            for (int i = 0; i < arrayData.length(); i++) {
                JSONObject arrayObject = (JSONObject) arrayData.get(i);
                JSONObject imageObj = arrayObject.getJSONObject("images");
                JSONObject low_reso_image = imageObj.getJSONObject("low_resolution");
                String imageUrl = low_reso_image.getString("url");
                Log.d("playerImageList url:", imageUrl.toString());
                playerImageList.add(imageUrl);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return playerImageList;
    }


    public String getSelfProfilePic() {

        String self_profile_picture = "";

        try {
            String urlString = API_URL + "/users/self?access_token=" + getAccessToken();
            URL url = new URL(urlString);

            InputStream inputStream = url.openConnection().getInputStream();
            String response = streamToString(inputStream);

            Log.d(GoogleServiceApi.TAG, "response: " + response);

            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            JSONObject data = jsonObj.getJSONObject("data");

          //  user
            self_profile_picture = data.getString("profile_picture");

            Log.d(GoogleServiceApi.TAG, "self_profile:" + self_profile_picture);


        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return self_profile_picture;

    }

    public ArrayList<String> getProfileNameByInstaId(String instaId) {
        String profile_picture = "";
        String name = "";
        ArrayList<String> infoList = new ArrayList<>();

        try {
            String urlString = API_URL + "/users/" + instaId + "/?access_token=" + getAccessToken();

            URL url = new URL(urlString);

            InputStream inputStream = url.openConnection().getInputStream();
            String response = streamToString(inputStream);

            Log.d(GoogleServiceApi.TAG, "response: " + response);

            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            JSONObject data = jsonObj.getJSONObject("data");

            profile_picture = data.getString("profile_picture");
            name = data.getString("full_name");

            infoList.add(profile_picture);
            infoList.add(name);

        } catch (Exception e) {
            Log.d(GoogleServiceApi.TAG, "exception: " + e);
        }

        return infoList;
    }

    public String getProfilePicByInstaId(String instaId) {

        String profile_picture = "";
        String name = "";

        try {
            String urlString = API_URL + "/users/" + instaId + "/?access_token=" + getAccessToken();

            URL url = new URL(urlString);

            InputStream inputStream = url.openConnection().getInputStream();
            String response = streamToString(inputStream);

            Log.d(GoogleServiceApi.TAG, "response: " + response);

            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            JSONObject data = jsonObj.getJSONObject("data");

            profile_picture = data.getString("profile_picture");

        } catch (Exception e) {
            Log.d(GoogleServiceApi.TAG, "exception: " + e);
        }

        return profile_picture;
    }


    public ArrayList<String> getSelfInfo() {
        String profile_picture = "";
        String name = "";
        ArrayList<String> infoList = new ArrayList<>();

        try {
            String urlString = API_URL + "/users/self"  + "/?access_token=" + getAccessToken();

            URL url = new URL(urlString);

            InputStream inputStream = url.openConnection().getInputStream();
            String response = streamToString(inputStream);

            Log.d(GoogleServiceApi.TAG, "response: " + response);

            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            JSONObject data = jsonObj.getJSONObject("data");

            profile_picture = data.getString("profile_picture");
            name = data.getString("full_name");

            infoList.add(profile_picture);
            infoList.add(name);

        } catch (Exception e) {
            Log.d(GoogleServiceApi.TAG, "exception: " + e);
        }

        return infoList;

    }


    public String getProfilePicByLink(String instaId) {

        String link_profile_picture = "";

        try {
            String urlString = API_URL + "/users/search?access_token=" + getAccessToken() + "&q=instaId";
            URL url = new URL(urlString);

            InputStream inputStream = url.openConnection().getInputStream();
            String response = streamToString(inputStream);

            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray arrayData = jsonObj.getJSONArray("data");

            for (int i = 0; i < arrayData.length(); i++) {
                JSONObject arrayObject = (JSONObject) arrayData.get(i);
                link_profile_picture = arrayObject.getString("profile_picture");
                String id = "1598758882";

                if (arrayObject.getString("id").equals(id)) {
                    Log.d("link_profile_picture:", link_profile_picture);
                    break;
                }

            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return link_profile_picture;

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
}
