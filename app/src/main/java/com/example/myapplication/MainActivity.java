package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.myapplication.Frag1;
import com.example.myapplication.Frag2;
import com.example.myapplication.Frag3;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Frag1 frag1;
    Frag2 frag2;
    Frag3 frag3;
    Frag4 frag4;
    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList = new ArrayList<>();
    Map<String, Location> geofenceData = new HashMap<>();
    PendingIntent geofencePendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent= PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        geofencingClient = LocationServices.getGeofencingClient(this);
        //프래그먼트 생성
        frag1 = new Frag1();
        frag2 = new Frag2();
        frag3 = new Frag3();
        frag4 = new Frag4();

        Location location1 = new Location("");
        location1.setLatitude(37.4215317);
        location1.setLongitude(-122.084085);
        geofenceData.put("geofence1", location1);

        Location location2 = new Location("");
        location2.setLatitude(37.4229999);
        location2.setLongitude(-122.0850575);
        geofenceData.put("geofence2", location2);
        bottomNavigationView = findViewById(R.id.bottomNavi);

        for (Map.Entry<String, Location> entry : geofenceData.entrySet()) {
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().getLatitude(),
                            entry.getValue().getLongitude(),
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
        //제일 처음 띄워줄 뷰를 세팅해줍니다. commit();까지 해줘야 합니다.
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,frag1).commitAllowingStateLoss();


        //bottomnavigationview의 아이콘을 선택 했을때 원하는 프래그먼트가 띄워질 수 있도록 리스너를 추가합니다.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();//menu_bottom.xml에서 지정해줬던 아이디 값을 받아와서 각 아이디값마다 다른 이벤트를 발생시킵니다.
                if (itemId == R.id.tab1) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_layout, frag1).commitAllowingStateLoss();
                    return true;
                } else if (itemId == R.id.tab2) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_layout, frag2).commitAllowingStateLoss();
                    return true;
                } else if (itemId == R.id.tab3) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_layout, frag3).commitAllowingStateLoss();
                    return true;
                }
                else if (itemId == R.id.tab4) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_layout, frag4).commitAllowingStateLoss();
                    return true;
                }
                return false;

            }

        });



    }
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

        private PendingIntent getGeofencePendingIntent() {
            // Reuse the PendingIntent if we already have it.
            if (geofencePendingIntent != null) {
                return geofencePendingIntent;
            }
            Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
            // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
            // calling addGeofences() and removeGeofences().
            geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                    FLAG_UPDATE_CURRENT);
            return geofencePendingIntent;
        }
}