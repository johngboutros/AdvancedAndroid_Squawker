package android.example.com.squawker.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by john on 17/04/18.
 */

public class SquawkFirebaseMessagingService extends FirebaseMessagingService {
    private static final String NOTIFICATION_CHANNEL_ID = "main";
    private static final int NEW_SQUAWK_NOTIFICATION_ID = 234;

    // DONE (1) Make a new Service in the fcm package that extends from FirebaseMessagingService.
        // DONE (2) As part of the new Service - Override onMessageReceived. This method will
        // be triggered whenever a squawk is received. You can get the data from the squawk
        // message using getData(). When you send a test message, this data will include the
        // following key/value pairs:
            // test: true
            // author: Ex. "TestAccount"
            // authorKey: Ex. "key_test"
            // message: Ex. "Hello world"
            // date: Ex. 1484358455343
        // TODO (3) As part of the new Service - If there is message data, get the data using
        // the keys and do two things with it :
            // TODO 1. Display a notification with the first 30 character of the message
            // TODO 2. Use the content provider to insert a new message into the local database
            // Hint: You shouldn't be doing content provider operations on the main thread.
            // If you don't know how to make notifications or interact with a content provider
            // look at the notes in the classroom for help.


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        String test = data.get("test");
        String author = data.get("author");
        String authorKey = data.get("authorKey");
        String message = data.get("message");
        String date = data.get("date");

        // Skip test messages
        if (test == null) {

            /*
             * Insert message in db
             */
            ContentValues values = new ContentValues();
            values.put(SquawkContract.COLUMN_AUTHOR, author);
            values.put(SquawkContract.COLUMN_AUTHOR_KEY, authorKey);
            values.put(SquawkContract.COLUMN_MESSAGE, message);
            values.put(SquawkContract.COLUMN_DATE, date);

            getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI,
                    values);

            /*
             * Display notification
             */

            if (message != null) {

                NotificationManager notificationManager = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Create the pending intent to launch the activity
                PendingIntent pendingIntent = PendingIntent.getActivity(this,
                        0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    NotificationChannel mChannel = new NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            "General", // move to strings.xml
                            NotificationManager.IMPORTANCE_HIGH);

                    notificationManager.createNotificationChannel(mChannel);
                }

                String trimmedMessage = message;

                if (message.length() > 30) {
                    trimmedMessage = message.substring(0, 30).concat("...");
                }

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_duck)
                                .setContentTitle(author)
                                .setContentText(trimmedMessage)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(message))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                }
                notificationManager.notify(NEW_SQUAWK_NOTIFICATION_ID, builder.build());
            }
        }
    }
}
