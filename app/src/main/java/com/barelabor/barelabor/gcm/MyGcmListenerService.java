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

package com.barelabor.barelabor.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.activity.MainActivity;
import com.barelabor.barelabor.activity.MenuActivity;
import com.barelabor.barelabor.activity.ParentActivity;
import com.barelabor.barelabor.activity.needtire.ChartActivity;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    private String title;
    private String message;
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        message = data.getString("data");
        title = data.getString("alert");
        Log.d(TAG, "message: " + message);
        Log.d(TAG, "title: " + title);

        Log.d(TAG, "lowPrice: " + data.get("lowCost"));
        String repairArrayString = data.getString("repariArray");
        String highCostArrayString = data.getString("highCostArray");
        String averageCostArrayString = data.getString("averageCostArray");
        String lowCostArrayString = data.getString("lowCostArray");
        String lowPrice = data.getString("lowCost");
        String highPrice = data.getString("highCost");
        String avgPrice = data.getString("averageCost");
        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(repairArrayString, highCostArrayString, averageCostArrayString, lowCostArrayString, lowPrice, highPrice, avgPrice);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String repairArrayString, String highCostArrayString, String averageCostArrayString, String lowCostArrayString, String lowPrice, String highPrice, String avgPrice) {

//        String lowPrice = "0", highPrice = "0", avgPrice = "0";
//        try {
//            JSONArray data = new JSONArray(message);
//            lowPrice = data.getJSONObject(0).getString("lowCost");
//            avgPrice = data.getJSONObject(0).getString("averageCost");
//            highPrice = data.getJSONObject(0).getString("highCost");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("low_price", lowPrice);
        intent.putExtra("high_price", highPrice);
        intent.putExtra("avg_price", avgPrice);
        intent.putExtra("repairArrayString", repairArrayString);
        intent.putExtra("highCostArrayString", highCostArrayString);
        intent.putExtra("averageCostArrayString", averageCostArrayString);
        intent.putExtra("lowCostArrayString", lowCostArrayString);
        intent.putExtra("notification", true);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("BareLabor")
                .setContentText("The pricing for your printed estimate has arrived")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
