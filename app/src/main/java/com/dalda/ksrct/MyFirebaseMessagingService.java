package com.dalda.ksrct;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> data = remoteMessage.getData();
        String body = Objects.requireNonNull(data.get("body"));
        sendNotification(body);
    }

    private void sendNotification(String body){
        Intent intent= new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultsoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this);
        notiBuilder.setSmallIcon(R.drawable.ksrctlogo);
        notiBuilder.setContentTitle("New Circular Updated - CT circulars");
        notiBuilder.setContentText(body);
        notiBuilder.setAutoCancel(true);
        notiBuilder.setSound(defaultsoundUri);
        notiBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelID = "KSRCT_circular";
            NotificationChannel channel = new NotificationChannel(channelID,"KSRCT",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notiBuilder.setChannelId(channelID);
            notiBuilder.setColor(getResources().getColor(R.color.colorAccent));
        }
        notificationManager.notify(0,notiBuilder.build());
    }
}
