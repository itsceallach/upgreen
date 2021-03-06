package com.example.upgreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {
    //Initialize Variables
    private Toolbar toolbar;
    EditText etSource, etDestination;
    TextView textView, textView2;
    String sType;
    double lat1 = 0, long1 = 0, lat2 = 0, long2 = 0;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Assign Variables
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Map");

        etSource = findViewById(R.id.et_source);
        etDestination = findViewById(R.id.et_destination);
        textView = findViewById(R.id.text_view);
        textView2 = findViewById(R.id.text_view2);

        //Initialize Places
        Places.initialize(getApplicationContext(), "AIzaSyC-ojVdEScnA3e1POn2VESQWS3eq35mfTw");

        etSource.setFocusable(false);
        etSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Define type
                sType = "source";
                //Initialize place field list
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fields).build(MapActivity.this);
                startActivityForResult(intent,100);
            }
        });
        etDestination.setFocusable(false);
        etDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Define type
                sType = "destination";
                //Initialize place field list
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fields).build(MapActivity.this);

                startActivityForResult(intent,100);
            }
        });

        textView.setText("0.0 Kilometers");
        textView2.setText(("0.0 kg of C02"));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check condition
        if (requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            if(sType.equals("source")){
                flag++;
                etSource.setText(place.getAddress());
                String sSource = String.valueOf(place.getLatLng());
                sSource = sSource.replaceAll("lat/lng: ", "");
                sSource = sSource.replace("(","");
                sSource = sSource.replace(")", "");
                String[] split = sSource.split(",");
                lat1 = Double.parseDouble(split[0]);
                long1 = Double.parseDouble(split[1]);
            }else {
                flag++;
                etDestination.setText(place.getAddress());
                String sDestination = String.valueOf(place.getLatLng());
                sDestination = sDestination.replaceAll("lat/lng: ", "");
                sDestination = sDestination.replace("(","");
                sDestination = sDestination.replace(")", "");
                String[] split = sDestination.split(",");
                lat2= Double.parseDouble(split[0]);
                long2 = Double.parseDouble(split[1]);
            }
            if (flag >=2){
                distance(lat1,long1,lat2,long2);
            }
        }else if(requestCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void distance(double lat1, double long1, double lat2, double long2) {
        double longDiff = long1 - long2;
        double distance = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(longDiff));
        distance = Math.acos(distance);

        distance = rad2deg(distance);

        distance = distance * 60 * 1.1515;

        distance = distance * 1.609344;

        textView.setText(String.format(Locale.US, "%.2f kilometers", distance));
        textView2.setText(String.format(Locale.US,"%.2f kg of CO2", (distance * 10)/365 ));



    }

    private double rad2deg(double distance) {
        return(distance*180.0/Math .PI);
    }

    private double deg2rad(double lat1) {
        return (lat1*Math.PI/180.0);
    }
}