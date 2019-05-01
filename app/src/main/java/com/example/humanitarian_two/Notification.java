package com.example.humanitarian_two;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.humanitarian_two.Home.MyPREFERENCES;

public class Notification extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final int BROADCAST_NOTIFICATION_ID = 1;
    String dataType;
    SharedPreferences sharedpreferences;
    String subject;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        subject=sharedpreferences.getString("subject",null);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            Log.d(TAG, "sendRegistrationToServer: sending token to server: " + s);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if(subject.equals("users"))
            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .update("token", s);
            else if(subject.equals("ngos"))
            {
                db.collection("ngos").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .update("token", s);
            }
        }
    }

    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationBody = "";
        String notificationTitle = "";
        String notificationData = "";
        try{
//            notificationData = remoteMessage.getData().toString();
            if (remoteMessage.getNotification() != null) {
                notificationBody = remoteMessage.getNotification().getBody();
                notificationTitle = remoteMessage.getNotification().getTitle();
            }



        }catch (NullPointerException e){
            Log.e(TAG, "onMessageReceived: NullPointerException: " + e.getMessage() );
        }
//        Log.d(TAG, "onMessageReceived: data: " + notificationData);
        Log.d(TAG, "onMessageReceived: notification body: " + notificationBody);
        Log.d(TAG, "onMessageReceived: notification title: " + notificationTitle);


         dataType = remoteMessage.getData().get(getString(R.string.data_type));
            if(dataType.equals("follow")){
            Log.d(TAG, "onMessageReceived: new incoming message.");

            String title = remoteMessage.getData().get("title");
                Log.i(TAG, title);
            String message = remoteMessage.getData().get("message");
            String messageId = remoteMessage.getData().get("messageId");
            sendMessageNotification(title, message, messageId);
        }
        else if(dataType.equals("donation")){
            Log.d(TAG, "onMessageReceived: new incoming message.");

            String title = remoteMessage.getData().get("title");
            Log.i(TAG, title);
            String message = remoteMessage.getData().get("message");
            String messageId = remoteMessage.getData().get("messageId");
            sendMessageNotification(title, message, messageId);
        }
        else if(dataType.equals("volunteerRequest")){
                Log.d(TAG, "onMessageReceived: new incoming message.");

                String title = remoteMessage.getData().get("title");
                Log.i(TAG, title);
                String message = remoteMessage.getData().get("message");
                String messageId = remoteMessage.getData().get("messageId");
                sendMessageNotification(title, message, messageId);
            }

            else if(dataType.equals("requestAccepted")){
                Log.d(TAG, "onMessageReceived: new incoming message.");

                String title = remoteMessage.getData().get("title");
                Log.i(TAG, title);
                String message = remoteMessage.getData().get("message");
                String messageId = remoteMessage.getData().get("messageId");
                sendMessageNotification(title, message, messageId);
            }
            else if(dataType.equals("donationDelivered")){
                Log.d(TAG, "onMessageReceived: new incoming message.");

                String title = remoteMessage.getData().get("title");
                Log.i(TAG, title);
                String message = remoteMessage.getData().get("message");
                String messageId = remoteMessage.getData().get("messageId");
                sendMessageNotification(title, message, messageId);
            }
    }

    /**
     * Build a push notification for a chat message
     * @param title
     * @param message
     */
    private void sendMessageNotification(String title, String message, String messageId){
        Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");

        //get the notification id
        int notificationId = buildNotificationId(messageId);
        NotificationCompat.Builder builder=null;
        if(dataType.equals("follow")){
            NotificationChannelForFollow();
             builder = new NotificationCompat.Builder(this, "follow")
                    .setSmallIcon(R.drawable.ic_donation)
                    .setContentTitle(title)
                    .setContentText(title)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        else if(dataType.equals("donation")){
            NotificationChannelForDonation();
            builder = new NotificationCompat.Builder(this, "donation")
                    .setSmallIcon(R.drawable.ic_donation)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        }else if(dataType.equals("volunteerRequest"))
        {
            NotificationChannelForVolunteer();

            Intent intent=new Intent(getApplicationContext(),VolunteerRequest.class);
            PendingIntent contentIntent=PendingIntent.getActivity(getApplicationContext(),11,intent,0);
            builder = new NotificationCompat.Builder(this, "volunteerRequest")
                    .setSmallIcon(R.drawable.ic_donation)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(contentIntent);
        }
        else if(dataType.equals("requestAccepted")||dataType.equals("donationDelivered"))
        {
            NotificationChannelForVolunteer();
            builder = new NotificationCompat.Builder(this, "donation")
                    .setSmallIcon(R.drawable.ic_donation)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        }


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());

    }

    private void NotificationChannelForFollow() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name ="follow";
            String description = "follow";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("follow", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void NotificationChannelForDonation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name ="donation";
            String description = "Sends notification when new donation is made or other alers realted to donation";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("donation", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void NotificationChannelForVolunteer() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name ="volunteer Request";
            String description = "Sends notification when selected for volunteer";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("volunteerRequest", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private int buildNotificationId(String id){
        Log.d(TAG, "buildNotificationId: building a notification id.");
        Log.d(TAG, "buildNotificationId: id: " + id);
//        int notificationId = Integer.parseInt(id);
//        for(int i = 0; i < 9; i++){
//            notificationId = notificationId + id.charAt(0);
//        }

//        Log.d(TAG, "buildNotificationId: notification id:" + notificationId);
        return 10;
    }

}