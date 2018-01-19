package com.thyn.user.notification.dummy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thyn.android.backend.myTaskApi.model.MyTask;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {
public static String TAG = "DummyContent";

    /**
     * An array of sample (dummy) items.
     */
    public static final List<MessageItem> ITEMS = new ArrayList<MessageItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, MessageItem> ITEM_MAP = new HashMap<String, MessageItem>();

    private static final int COUNT = 25;

   /* static {

        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }*/

   /* private static void addItem(MessageItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }*/

   /* private static MessageItem createDummyItem(int position) {
        return new MessageItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }*/

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class MessageItem {
        public final String id;
        public final String datetime;
        public final String content;
        public final String recipientImage;

        public MessageItem(String id, String datetime, String content, String recipientImage) {
            this.id = id;
            this.datetime = datetime;
            this.content = content;
            this.recipientImage = recipientImage;

        }

        @Override
        public String toString() {
            return content;
        }
    }

}
