package com.example.queder;

import android.content.Context;
import android.util.Log;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

public class initialisePusher {

    private static Pusher pusher;
    private static Channel channel;

    public static void init(Context context) {
        if (pusher == null) {
            PusherOptions options = new PusherOptions();
            options.setCluster(BuildConfig.CLUSTER);
            pusher = new Pusher(BuildConfig.KEY, options);
            pusher.connect(new ConnectionEventListener() {
                @Override
                public void onConnectionStateChange(ConnectionStateChange change) {
                    Log.i("Pusher", "State changed from " + change.getPreviousState() + " to " + change.getCurrentState());
                }

                @Override
                public void onError(String message, String code, Exception e) {
                    Log.i("Pusher", "There was a problem connecting! " + "\ncode: " + code + "\nmessage: " + message + "\nException: " + e);
                }
            }, ConnectionState.ALL);
        }
    }

    public static Channel getChannel(String channelName) {
        if (pusher == null) {
            throw new IllegalStateException("Pusher is not initialized. Call InitialisePusher.init() before accessing channels.");
        }

        if (channel == null || !channel.getName().equals(channelName)) {
            if (channel != null) {
                pusher.unsubscribe(channel.getName());
            }

            channel = pusher.subscribe(channelName);
            Log.i("Pusher", "Subscribed to channel: " + channelName);
        }

        return channel;
    }

    public static void disconnect() {
        if (pusher != null) {
            pusher.disconnect();
            pusher = null;
            channel = null;
        }
    }


}
