package com.thyn.backend.utilities.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.thyn.backend.Ids;
import com.thyn.backend.WPT.UserRole;
import com.thyn.backend.datastore.DatastoreHelpers;
import com.thyn.backend.entities.users.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * Created by shalu on 9/21/16.
 */
public class ExternalAuthentication {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ExternalAuthentication.class.getName());
    public static User getInfoFromFacebookForLogin(String idTokenString) throws MalformedURLException, IOException, NoSuchAlgorithmException, InvalidKeySpecException{

       // IMPORTANT: Validating if the token string sent belongs to a specific user id and a specific app id.
       // Step 1: get the Access Token id by supplying facebook Graph a app id and app secret id. This access
        // token id will be later used along with the token than came from the client to find the user id.
        String myAppId = "1771008673113982";
        String myAppSecret = "5bbf32ae837631bf22b0443ec2af42b3";

        String uri = "https://graph.facebook.com/oauth/access_token?" +
                        "client_id=" + myAppId +
                        "&client_secret=" + myAppSecret +
                        "&grant_type=client_credentials";


        URL newURL = new URL(uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = newURL.openStream();
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }
        String response = new String(baos.toByteArray());
        is.close();
        baos.close();

        String TOKEN_INDEX = "access_token=";
        String token = response.substring(TOKEN_INDEX.length());
        // Step 2: check if the access_token id and input_token(came from the app) correspond to the user id.
        // Access token and input token are used interchangeably. Please dont get confused. For example
        // access token in the next graphy api call (scroll below) is actually input token in this api call. CONFUSING!!!
        String debug_user_token_url = "https://graph.facebook.com/debug_token?"
                                        + "input_token=" + idTokenString
                                        + "&access_token=" + token;
        logger.info("going to access this url: " + debug_user_token_url);
        URL graphURL = new URL(debug_user_token_url);
        HttpURLConnection c = (HttpURLConnection) graphURL.openConnection();
        int status = c.getResponseCode();
        String message = null;
        switch(status){
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                message = sb.toString();
        }

        logger.info(message);
        JSONObject myObject = new JSONObject(message);
        JSONObject data = myObject.getJSONObject("data");
        String userId= data.getString("user_id");
        logger.info(userId);

        //Step 3: we need to get ther user email, name, id, gender, picture from the token
        //https://graph.facebook.com/me?fields=email,name,id,gender,picture&access_token=EAAZAKuRba534BANC9VAUadfXMVPympggExlc6eYUQ13VZBtMpPZCG059X3ZATbJJRrG335ts0o5ZBe0e19dTKi6I6EDsXlYV4pUx4xcjexmSZBKZBEQz9izNDZCIihVmcrL6KKjQj8cEy8bEzIwyMdIKfnXoTuG0N22FPUqt6EIp3yv4vY8gJZBgjkKe0dXpWBjgZCj7wHwZB62rXhCjgE2Orfj

        String user_info_url = "https://graph.facebook.com/me?fields=email,name,id,gender,picture"
                + "&access_token=" + idTokenString;
        logger.info("going to access this url: " + user_info_url);
        URL userInfoGraphURL = new URL(user_info_url);
        c = (HttpURLConnection) userInfoGraphURL.openConnection();
        status = c.getResponseCode();

        switch(status){
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                message = sb.toString();
        }

        logger.info(message);
        myObject = new JSONObject(message);
        String email= myObject.getString("email");
        String name = myObject.getString("name");
        String gender = myObject.getString("gender");
        logger.info("email: " +  email + ", name: " + name);

        JSONObject picturedata = myObject.getJSONObject("picture");
        JSONObject pdata = picturedata.getJSONObject("data");
        String imageurl = pdata.getString("url");

        User user = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "fbUserId ==", userId);

        if(user == null) {
            logger.info("The user " + userId + " doesnt exist in the database. Creating one.");
            user = User.createNewUserOnDatastore(UserRole.USER, email, name, LoginType.Facebook, userId, imageurl, gender, null);
        }
        else{
            logger.info("The user " + userId + " exists in the database. Not creating the user again.");
            return user;
        }

        return null;
    }
    public static User getInfoFromGoogleForLogin(String idTokenString) throws GeneralSecurityException, IOException{

        JsonFactory jsonFactory = new JacksonFactory();
        NetHttpTransport transport = new NetHttpTransport();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Arrays.asList(Ids.WEB_CLIENT_ID))
                        // If you retrieved the token on Android using the Play Services 8.3 API or newer, set
                        // the issuer to "https://accounts.google.com". Otherwise, set the issuer to
                        // "accounts.google.com". If you need to verify tokens from multiple sources, build
                        // a GoogleIdTokenVerifier for each issuer and try them both.
                .setIssuer("https://accounts.google.com")
                .build();

// (Receive idTokenString by HTTPS POST)
        logger.info("The token received from the app is: " + idTokenString);
        GoogleIdToken idToken = verifier.verify(idTokenString);
        User user = null;
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String gender = (String) payload.get("gender");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");


            // Use or store profile information
            // ...
            user = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "googUserId ==", userId);

            if(user == null) {
                logger.info("The user " + userId + " doesnt exist in the database. Creating one.");
                user = User.createNewUserOnDatastore(UserRole.USER, email, name, LoginType.Google, userId, pictureUrl, gender, null);
            }
            else{
                logger.info("User already exists in the system. Wont create another one.");
            }

        } else {
            System.out.println("Invalid ID token.");
        }
        return user;
    }

    public static User extractUserFromDataStore(int socialType, String idTokenString) {
        User user = null;

        if(socialType == 1) {
            user = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "googUserId ==", idTokenString);
        }
        else{
            user = DatastoreHelpers.tryLoadEntityFromDatastore(User.class, "fbUserId ==", idTokenString);
        }
        if(user == null) {
            logger.info("The user " + idTokenString + " doesnt exist in the database.");
            }
        else{
            logger.info("User exists in the database");
        }
        return user;
    }
}
