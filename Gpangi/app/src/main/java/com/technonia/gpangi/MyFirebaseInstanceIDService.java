package com.technonia.gpangi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Set;


public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private void sendNewTokenWithRegisteredDeviceIDsToServer(Context appContext, SharedPreferences sp, String token) {
        String baseURL = appContext.getString(R.string.baseURL);

        // get device_id_set, which contains all registered Device IDs
        String stringSetKey = appContext.getString(R.string.device_id_key);
        Set<String> device_id_set = sp.getStringSet(stringSetKey, null);

        // check if the device set is stored in the local storage
        if (device_id_set != null) {
            for (String device_id : device_id_set) {
                String result_of_registration = NetworkUtils.sendRequestForNewDeviceID(baseURL, device_id, token);

                Log.d("DEBUGGING_FIREBASE_PUSH_NOTIFICATION", result_of_registration);
                if (result_of_registration.contains("Exception")) {
                    Log.e("Exception", result_of_registration);
                    //TODO check if some exception occurred while sending the data to the server
                }
            }
        }
    }

    private void storeTokenPermanently(SharedPreferences sp, String token_key, String token) {
        Editor editor = sp.edit();
        editor.putString(token_key, token);

        // editor.commit() stores the data to the permanent storage immediately, whereas
        // editor.apply() will handle it in the background
        editor.apply();
    }

    /**
     * 구글 토큰을 얻는 값입니다.
     * 아래 토큰은 앱이 설치된 디바이스에 대한 고유값으로 푸시를 보낼때 사용됩니다.
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("Firebase", "FirebaseInstanceIDService : " + token);

        Context appContext = getApplicationContext();

        // get the SharedPreferences instance
        String fileName = appContext.getString(R.string.shared_preferences_file_name);
        SharedPreferences sp = appContext.getSharedPreferences(fileName, MODE_PRIVATE);

        // get a set of device IDs, and send the token to the server via POST request with all registered device IDs
        sendNewTokenWithRegisteredDeviceIDsToServer(appContext, sp, token);

        // store a token in the local storage
        String token_key = appContext.getString(R.string.fcm_token_key);
        storeTokenPermanently(sp, token_key, token);
    }

    /**
     * 메세지를 받았을 경우 그 메세지에 대하여 구현하는 부분입니다.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage);
        }
    }

    /**
     * remoteMessage 메세지 안애 getData 와 getNotification 이 있습니다.
     * 이부분은 차후 테스트 날릴때 설명 드리겠습니다.
     * **/
    private void sendNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");

        //TODO
        String latitude = "37.401782989502";
        String longitude = "126.7320098877";

        String channel = "gpangi_channel";      // channel id
        String channel_nm = "gpangi_channel_name";  // channel name
        String channel_description = "FCM channel for push notification";

        //TODO
        Context appContext = getApplicationContext();

        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra(appContext.getString(R.string.push_notification_key_push_notification), "yes");
        intent.putExtra(appContext.getString(R.string.push_notification_key_latitude), latitude);
        intent.putExtra(appContext.getString(R.string.push_notification_key_longitude), longitude);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //TODO

        // 오레오 버전부터는 "Notification Channel"이 없으면 푸시가 생성되지 않는 현상이 있습니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notification_channel = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // assertion to avoid NullPointerException
            assert notification_channel != null;

            NotificationChannel channelMessage = new NotificationChannel(channel, channel_nm, NotificationManager.IMPORTANCE_HIGH);
            channelMessage.setDescription(channel_description);
            channelMessage.enableLights(true);
            channelMessage.enableVibration(true);
            channelMessage.setShowBadge(false);
            channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
            channelMessage.setImportance(NotificationManager.IMPORTANCE_HIGH);

            notification_channel.createNotificationChannel(channelMessage);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channel)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setContentIntent(pendingIntent)
                            .setChannelId(channel)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // assertion to avoid the NullPointerException
            assert notificationManager != null;

            notificationManager.notify(9999, notificationBuilder.build());

        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channel)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setContentIntent(pendingIntent)
                            .setChannelId(channel)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(9999, notificationBuilder.build());
        }
    }
}
