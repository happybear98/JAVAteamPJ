package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofenceStatusCodes;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Log.i("GeofenceReceiver", "Intent received: " + intent);
        if (geofencingEvent == null) {
            Log.e("GeofenceErr", "GeofencingEvent is null");
            return;
        }
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("GeofenceErr", errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String transitionMsg;
            switch (geofenceTransition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    transitionMsg = "Enter";
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    transitionMsg = "Exit";
                    break;
                default:
                    transitionMsg = "-";
                    break;
            }

            for (Geofence geofence : triggeringGeofences) {
                Log.i("Geofence", geofence.getRequestId() + " - " + transitionMsg);
            }
        } else {
            Log.e("Geofence", "Unknown");
        }
    }
}
