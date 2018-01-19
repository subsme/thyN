/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thyn.backend.gcm;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.thyn.backend.datastore.DatastoreHelpers;
import com.thyn.backend.utilities.Logger;
import com.thyn.backend.entities.users.Device;

import java.util.Iterator;
import java.util.List;

// NOTE:
// This class emulates a server for the purposes of this sample,
// but it's not meant to serve as an example for a production app server.
// This class should also not be included in the client (Android) application
// since it includes the server's API key. For information on GCM server
// implementation see: https://developers.google.com/cloud-messaging/server
public class GcmSender {

    public static final String API_KEY = "AIzaSyAYxYfUFaU8q9RvTz5sknciVuAyB0gmcnw";
    public static final String SENDER_ID = "953474522001";

    public static void main(String[] args) {
        //listAllDevicesFromDeviceGroups();
        //createDeviceGroup("subu17-5660980839186432","dV5ce0PriOc:APA91bG-fadA4vVq_IQivSJLIEiMx2cm9KIcSjJ7eoTt-vBctDfbRNpcLoJsxMVxTDTd6C8eczS-RSkDPGc-PXexjcHN6f9X2fVv_JRqRJmLDwCFNEmeponQ2LQFVlNozCnxZfbUHEOe");
        //removeDeviceFromDeviceGroup("Subu-5763263606292480","APA91bEyiB0BFLFu9P_XEkKkOoxUc_lJu3L81HQnIyQwFy3a_H5Y7WwhfFo-Z7wOknifMs3M58G9k1AOmYFTDQwgZsIZXeIoAR_bPtNnr5f0qdfC86KMCUdejXBC1MUZRe8MgmwkQa7K","eaSKx_2UPZc:APA91bFwRBzgw8KcUdTR0rB2JRNWhGeywx8mTRLACf1L5pZdUKF16xhPyWWyHrzYzwoLMURsU2ebp8V2fc-9gKw7JZnz-Zann_rFHYH4Ops2Rbz_dqoLJSvB6pgv5k-fBf76juBXVH8l");
        //removeDeviceFromDeviceGroup("shalu-6192449487634432","APA91bEdwNUehYOSj4cRqJyK1eY42nPtRMo8SKIk1X0_n0ku3lz8XG85vSbLCLAVSFwvBJUdrN06d8LOu51LKLvLwkg-QSpjjxDKpXLabyzO0R4nKF5ol7IpZe0cPXp2n609Z6P8MbRC","f38JKKETDJQ:APA91bE_afJyHvrRET_bZM-HzjQvsdCJBIW7tW-2DJC9pduBOpxp6foOb46BV8TBktX-M4XnmwwlUsqNFtI5kYJXVRMzlSCJz1QtnxCxCH1GYaC70ksLFMmiyA_qFLsbgUbDCz3eSeUL");
        //removeDeviceFromDeviceGroup("subu13-5629499534213120", "APA91bFq-gcDZlyk-FGnZrjR2ASjJXO_jvT5KzxiJWO8Fu1sg4MCMQuTlVvtCYf5GrMTtS-lQMVwBtJCGBTYaCjO5pGriIH6Pv3wCUHySl22KKKousFMg2vlA_rNM7h97B4r4h8gNJJi", "dV5ce0PriOc:APA91bG-fadA4vVq_IQivSJLIEiMx2cm9KIcSjJ7eoTt-vBctDfbRNpcLoJsxMVxTDTd6C8eczS-RSkDPGc-PXexjcHN6f9X2fVv_JRqRJmLDwCFNEmeponQ2LQFVlNozCnxZfbUHEOe");
        //removeDeviceFromDeviceGroup("subu8-5629499534213120", "APA91bFU_wBBwiYTvpgRkTZEB2uBZWBhOFwfaRsn3IxQXL54Gy3rWcvmercvY74uG07mhlPyGD4Qk4pVE0TpJFYg74X_yJ58yNWOidLGVRFw5yXTksDQhwilt2OW5apPjLkAMEeBN3Zt", "f38JKKETDJQ:APA91bE_afJyHvrRET_bZM-HzjQvsdCJBIW7tW-2DJC9pduBOpxp6foOb46BV8TBktX-M4XnmwwlUsqNFtI5kYJXVRMzlSCJz1QtnxCxCH1GYaC70ksLFMmiyA_qFLsbgUbDCz3eSeUL");
        //removeDeviceFromDeviceGroup("subu3-5629499534213120", "APA91bFG045JmP6uhmTWLYbJFu63pzt07UWeJkfBLhBOHnjxU3JOy7mrxYSX6HMoSCn7S-OlYdPwHazDSs501mbEL4RvHMTVm3Mc90LgcMjmJKjax_H2qyyUUmqqsegUD8MhSVGqIhPe", "f38JKKETDJQ:APA91bE_afJyHvrRET_bZM-HzjQvsdCJBIW7tW-2DJC9pduBOpxp6foOb46BV8TBktX-M4XnmwwlUsqNFtI5kYJXVRMzlSCJz1QtnxCxCH1GYaC70ksLFMmiyA_qFLsbgUbDCz3eSeUL");
        //deleteAllDevicesFromDeviceGroups();
       // sendMessageToDeviceGroup(
       //         "APA91bGWxap-fwoH8s1lWFyvka7WBu-weQ8CIWuqCxfSIpasPiCxHixD70oVDOMXAkWBH-5PZJFdRe1zrAzXNzMVGZ034Tzva8eSYIIA3rVJ90NrnPDi7l4MjCKTLBWd1NziWU6EF90V","To infinity and beyond.");
        //removeDeviceFromDeviceGroup(args[0],args[1],args[2]);
        //sendMessageToDeviceGroup("APA91bEyiB0BFLFu9P_XEkKkOoxUc_lJu3L81HQnIyQwFy3a_H5Y7WwhfFo-Z7wOknifMs3M58G9k1AOmYFTDQwgZsIZXeIoAR_bPtNnr5f0qdfC86KMCUdejXBC1MUZRe8MgmwkQa7K", "infinity and beyond");
      sendMessageToTopic("Jingcha", "hello there");
       /* if (args.length < 1 || args.length > 2 || args[0] == null) {
            System.err.println("usage: ./gradlew run -Pmsg=\"MESSAGE\" [-Pto=\"DEVICE_TOKEN\"]");
            System.err.println("");
            System.err.println("Specify a test message to broadcast via GCM. If a device's GCM registration token is\n" +
                    "specified, the message will only be sent to that device. Otherwise, the message \n" +
                    "will be sent to all devices subscribed to the \"global\" topic.");
            System.err.println("");
            System.err.println("Example (Broadcast):\n" +
                    "On Windows:   .\\gradlew.bat run -Pmsg=\"<Your_Message>\"\n" +
                    "On Linux/Mac: ./gradlew run -Pmsg=\"<Your_Message>\"");
            System.err.println("");
            System.err.println("Example (Unicast):\n" +
                    "On Windows:   .\\gradlew.bat run -Pmsg=\"<Your_Message>\" -Pto=\"<Your_Token>\"\n" +
                    "On Linux/Mac: ./gradlew run -Pmsg=\"<Your_Message>\" -Pto=\"<Your_Token>\"");
            System.exit(1);
        }
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("message", args[0].trim());
            // Where to send GCM message.
            if (args.length > 1 && args[1] != null) {
                jGcmData.put("to", args[1].trim());
            } else {
                jGcmData.put("to", "/topics/global");
            }
            // What to send in GCM message.
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            System.out.println(resp);
            System.out.println("Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the GCM message.");
        } catch (IOException e) {
            System.out.println("Unable to send GCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        }
        */

    }

    public static boolean deleteAllDevicesFromDeviceGroups(){
        List l = DatastoreHelpers.tryGetAllEntitiesOfAClass(Device.class);
        if(l == null) return false;
        Iterator<Device> i = l.iterator();
        while (i.hasNext()) {
            Device d = i.next();
            System.out.println("Notification key" + d.getNotification_key());
            System.out.println("Notification key name" + d.getNotification_key_name());
            System.out.println("registration token" + d.getRegistration_token());
            System.out.println("Request to remove the above device from Device group");
            removeDeviceFromDeviceGroup(d.getNotification_key_name(), d.getNotification_key(), d.getRegistration_token());
        }
        return true;
    }
    public static boolean listAllDevicesFromDeviceGroups(){
        List l = DatastoreHelpers.tryGetAllEntitiesOfAClass(Device.class);
        if(l == null) return false;
        Iterator<Device> i = l.iterator();
        while (i.hasNext()) {
            Device d = i.next();
            System.out.println("Notification key" + d.getNotification_key() + ", Notification key name: " + d.getNotification_key_name() + ", Registration token: " + d.getRegistration_token());

        }
        return true;
    }
    public static void sendMessageToTopic(String topic, String message){
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("message", message);
            // Where to send GCM message.
            String topicName = "/topics/topic_thyN_" + topic.trim();
            if (topic != null) {
                jGcmData.put("to", topicName);
            } else {
                jGcmData.put("to", "/topics/global");
            }
            // What to send in GCM message.
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            System.out.println(resp);
            System.out.println("Sending message: '" + message + "' to " + topicName);
            System.out.println("Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the GCM message.");
        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Unable to send GCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        }
    }
    public static String sendMessageToDeviceGroup(String to, String message, String sender, Long profileID, Long taskID){
        String resp = null;
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("message", message);
            jData.put("sender", sender);
            jData.put("profileID", profileID);
            jData.put("taskID", taskID);
            // Where to send GCM message.
            if (to != null && message != null) {
                jGcmData.put("to", to.trim());
            } else {
                Logger.logError("Error", new NullPointerException());
                return null;
            }
            // What to send in GCM message.
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://gcm-http.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            System.out.println("The payload is: " +  jGcmData.toString());
            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            resp = IOUtils.toString(inputStream);

            if(resp == null || resp.trim().equals("")){
                System.out.println("Got a Null Response from the server. Unable to send GCM message.");
                return null;
            }
            else{
                System.out.println("the response from GCM server is: " + resp);
                System.out.println("Check your device/emulator for notification or logcat for " +
                        "confirmation of the receipt of the GCM message.");
            }
            JSONObject jResp = null;
            try {
                jResp = new JSONObject(resp);
            }
            catch(JSONException jsone){
                jsone.printStackTrace();
            }
            int rSuccess = jResp.getInt("success");
            int rFailure = jResp.getInt("failure");
            if(rSuccess==0 && rFailure>0)
                System.out.println("Failed sending message to the recipient. Total no. of devices for the user. " + rFailure);
            else if(rSuccess>0 && rFailure==0)
                System.out.println("Success sending message to all receipients. Total no. of devices for the user " + rSuccess);
            else
                System.out.println("Partial success. Success sending to " + rSuccess + " devices. And failed sending to " +  rFailure + " devices");
        } catch (IOException e) {
            Logger.logError("Unable to send GCM message.", e);
        }

        return resp;
    }

    public static String createDeviceGroup(String notification_key_name, String registration_token){
        String Response_Notification_Key = null;
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();

            // Where to send GCM message.
            if (notification_key_name != null && registration_token != null) {
                jGcmData.put("operation", "create");
                jGcmData.put("notification_key_name", notification_key_name.trim());
                jGcmData.put("registration_ids", new JSONArray("[\"" + registration_token + "\"]"));
            } else {
                Logger.logError("Error",new NullPointerException());
                return null;
            }
            Logger.logInfo(jGcmData.toString());

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/notification");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("project_id",SENDER_ID);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            Logger.logInfo("the response from GCM server is: " + resp);
            JSONObject jResp = new JSONObject(resp);
            Response_Notification_Key = jResp.getString("notification_key");

        } catch (IOException e) {
            Logger.logError("Unable to create a device group.", e);

        }
        return Response_Notification_Key;
    }
    public static String addDeviceToDeviceGroup(String notification_key_name, String notification_key, String registration_token){
        String resp = null;
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();

            // Where to send GCM message.
            if (notification_key_name != null && registration_token != null) {
                jGcmData.put("operation", "add");
                jGcmData.put("notification_key_name", notification_key_name.trim());
                jGcmData.put("notification_key", notification_key.trim());
                jGcmData.put("registration_ids", new JSONArray("[\"" + registration_token + "\"]"));
            } else {
                Logger.logError("Error",new NullPointerException());
                return null;
            }

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/notification");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("project_id",SENDER_ID);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            resp = IOUtils.toString(inputStream);

        } catch (IOException e) {
            Logger.logError("Unable to add Device to Device group.", e);

        }
        return resp;
    }
    public static String removeDeviceFromDeviceGroup(String notification_key_name, String notification_key, String registration_token){
        String Response_Notification_Key = null;
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();

            // Where to send GCM message.
            if (notification_key_name != null && registration_token != null) {
                jGcmData.put("operation", "remove");
                jGcmData.put("notification_key_name", notification_key_name.trim());
                jGcmData.put("notification_key", notification_key.trim());
                jGcmData.put("registration_ids", new JSONArray("[\"" + registration_token + "\"]"));
            } else {
                Logger.logError("Error",new NullPointerException());
                return null;
            }

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/notification");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("project_id",SENDER_ID);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            Logger.logInfo("the message sent is" + jGcmData.toString());
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            Logger.logInfo("the response from GSM server is: " + resp);
            JSONObject jResp = new JSONObject(resp);
            Response_Notification_Key = jResp.getString("notification_key");

        } catch (IOException e) {
            Logger.logError("Unable to remove Device from Device group.", e);

        }
        return Response_Notification_Key;
    }


}
