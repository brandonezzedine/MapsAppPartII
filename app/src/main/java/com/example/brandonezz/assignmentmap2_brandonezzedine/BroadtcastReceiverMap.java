package com.example.brandonezz.assignmentmap2_brandonezzedine;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

class BroadtcastReceiverMap extends BroadcastReceiver {

    private static final String MAP_TAG = "MAP_TAG";

    public static final String NEW_MAP_LOCATION_BROADCAST = "com.example.brandonezz.assignmentmap2_brandonezzedine.action.NEW_MAP_LOCATION_BROADCAST";
    public static final String EXTRA_LATITUDE = "LATITUDE";
    public static final String EXTRA_LONGITUDE = "LONGITUDE";
    public static final String MAP_LOCATION = "LOCATION";

    public static final int CHANNEL_ID = 1;
    public static final int CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;
    public static final String CHANNEL_DESCRIPTION = "BROADCAST MAP CHANNEL";
    public static final String CHANNEL_NAME = "MAPS";

    private NotificationManager notificationManager;

    private Notification.Builder builder;

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        Double latitude = intent.getDoubleExtra(EXTRA_LATITUDE, Double.NaN);
        Double longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, Double.NaN);
        String location = intent.getStringExtra(MAP_LOCATION);

        String hemisphere = getHemisphere(latitude);


        if (hemisphere.equals("NORTH") || hemisphere.equals("SOUTH") || hemisphere.equals("SOUTH")){

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new Notification.Builder(context, "MAPS");

            builder.setContentTitle(location);
            builder.setContentText(Double.isNaN(latitude) || Double.isNaN(longitude)
                    ? "Lcation Unknown" :
                    "Located at the " + hemisphere +
                            ", with coordinates (lat, lng): "+
                            latitude+", "+longitude);
            notificationManager.createNotificationChannel(getNotificationChannel());


            notificationManager.notify(CHANNEL_ID, builder.build());

        }else
            Log.d(MAP_TAG, String.valueOf(R.string.location_out));

    }



    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel getNotificationChannel(){

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_NAME, CHANNEL_DESCRIPTION, CHANNEL_IMPORTANCE);
        notificationChannel.setDescription(CHANNEL_DESCRIPTION);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.enableVibration(true);
        notificationChannel.setShowBadge(true);

        return notificationChannel;
    }

    private String getHemisphere(Double latitude){

        String hemisphere = "";
        boolean isCentralHemisphere = (latitude < 23 && latitude > -23);
        boolean isNorthHemisphere = (latitude > 23 && latitude <= 90);
        boolean isSouthHemisphere = (latitude < -23 && latitude >= -90);

        if(isCentralHemisphere)
            hemisphere = "CENTRAL";
        else{
            if (isNorthHemisphere) hemisphere = "NORTH";
            else{
                if (isSouthHemisphere)
                    hemisphere = "SOUTH";
            }
        }

        return hemisphere;
    }
}







