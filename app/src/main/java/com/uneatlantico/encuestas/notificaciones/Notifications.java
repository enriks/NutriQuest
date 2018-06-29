package com.uneatlantico.encuestas.notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uneatlantico.encuestas.Inicio.NutriQuestMain;
import com.uneatlantico.encuestas.R;

import java.util.Map;

public class Notifications extends FirebaseMessagingService {
    //private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            sendNotification(remoteMessage.getData());

        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(Map<String, String> messageBody) {


        PendingIntent encuestaIntent = encuestaIntent(messageBody.get("idEncuesta"));

        String channelId = "nose";//getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)

                        //TODO cambiar icono de flecha hacia atras a uno adecuado
                        .setSmallIcon(R.drawable.baseline_arrow_back_24)
                        .setContentTitle(messageBody.get("title"))
                        .setContentText(messageBody.get("message"))
                        .addAction( R.drawable.baseline_arrow_back_24,"SI", encuestaIntent) // #0
                        .addAction(R.drawable.baseline_arrow_back_24,"NO", noIntent())
                        .setSound(defaultSoundUri)
                        .setContentIntent(encuestaIntent).setAutoCancel(true);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Encuesta rapida",
                    NotificationManager.IMPORTANCE_DEFAULT);


            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private PendingIntent encuestaIntent(String punteroEncuesta){
        Intent intent = new Intent(this, NutriQuestMain.class);
        intent.putExtra("idEncuesta", Integer.valueOf(punteroEncuesta));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
    }

    private PendingIntent noIntent(){
        Intent noIntent = new Intent(this, ButtonReceiver.class);

        return PendingIntent.getActivity(this, 0, noIntent, 0);
    }

    // [END receive_message]



    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    /*private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }*/

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    /*private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }*/
}


