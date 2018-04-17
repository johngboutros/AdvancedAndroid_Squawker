package android.example.com.squawker.fcm;

import android.content.ContentValues;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkDatabase;
import android.example.com.squawker.provider.SquawkProvider;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by john on 17/04/18.
 */

public class SquawkFirebaseMessagingService extends FirebaseMessagingService {

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

        // Skip inserting test messages
        if (test == null) {
            // Insert message in db
            ContentValues values = new ContentValues();
            values.put(SquawkContract.COLUMN_AUTHOR, author);
            values.put(SquawkContract.COLUMN_AUTHOR_KEY, authorKey);
            values.put(SquawkContract.COLUMN_MESSAGE, message);
            values.put(SquawkContract.COLUMN_DATE, date);

            getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI,
                    values);
        }
    }
}
