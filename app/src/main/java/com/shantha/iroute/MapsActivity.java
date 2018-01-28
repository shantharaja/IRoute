package com.shantha.iroute;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import org.w3c.dom.Document;

import java.util.ArrayList;

import static com.google.maps.RoadsApi.snapToRoads;
import static com.shantha.iroute.R.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_ONE = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_TWO = 2;

    LatLng ll=null;
    LatLng sourceLatLng = null;
    LatLng destinationLatLng = null;
    Button sourceButton, destinButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(id.map);
//        mapFragment.getMapAsync(this);
        destinButton = (Button) findViewById(id.destinationBtn);
        MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(id.mapFragment);
        fragment.getMapAsync(this);
        ((TextView) findViewById(id.crow_fly_dist)).setVisibility(View.GONE);
        ((TextView) findViewById(id.actual_fly_dist)).setVisibility(View.GONE);


        destinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_ONE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }



            }
        });
         sourceButton = (Button) findViewById(id.sourceBtn);
        sourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_TWO);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }



            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if(sourceLatLng!=null){
            mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sourceLatLng));
            mMap.setMyLocationEnabled(true);
        }

    }

    private void goToSourceLocation(LatLng latLng){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
        mMap.moveCamera(cameraUpdate);
        mMap.addMarker(new MarkerOptions().position(latLng)
                .title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
    private void goToSDestinationLocation(LatLng latLng){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,8);
        mMap.moveCamera(cameraUpdate);
        mMap.addMarker(new MarkerOptions().position(latLng)
                .title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_ONE){
            if(resultCode==RESULT_OK){

                Place place1 = PlaceAutocomplete.getPlace(this, data);
                    destinationLatLng = place1.getLatLng();

                goToSourceLocation(destinationLatLng);
                destinButton.setText(place1.getAddress());
               Toast.makeText(MapsActivity.this,place1.getAddress()+" 111:" +destinationLatLng ,Toast.LENGTH_SHORT).show();
            }else {

                Toast.makeText(MapsActivity.this,"FAILED",Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_TWO){
            if(resultCode==RESULT_OK){

                Place place2 = PlaceAutocomplete.getPlace(this, data);
                sourceButton.setText(place2.getAddress());
                    sourceLatLng = place2.getLatLng();
                goToSDestinationLocation(sourceLatLng);

            }else {

                Toast.makeText(MapsActivity.this,"FAILED",Toast.LENGTH_SHORT).show();
            }
        }


        if(sourceLatLng!=null && destinationLatLng!=null){
            float[] distances={22f,222f};
            Location l1=new Location("source");
            l1.setLatitude(sourceLatLng.latitude);
            l1.setLongitude(sourceLatLng.longitude);

            Location l2=new Location("dest");
            l2.setLatitude(destinationLatLng.latitude);
            l2.setLongitude(destinationLatLng.longitude);


            ((TextView) findViewById(id.crow_fly_dist)).setText("Distance :"+ String.format("%2f",l1.distanceTo(l2))+" meters");
            ((TextView) findViewById(id.crow_fly_dist)).setTextColor(Color.RED);
            Toast.makeText(this,"Distance ::  "+l1.distanceTo(l2),Toast.LENGTH_SHORT);
            drawPolyLines(sourceLatLng,destinationLatLng);

        }

    }

    private void drawPolyLines(LatLng sourceLatLng, LatLng destinationLatLng) {
        PolylineOptions p=new PolylineOptions().add(sourceLatLng,destinationLatLng)
                .color(Color.RED).geodesic(true);

        if(mMap!=null){

            ((TextView) findViewById(id.crow_fly_dist)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(id.actual_fly_dist)).setVisibility(View.VISIBLE);

            mMap.addPolyline(p);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(destinationLatLng,8);
            mMap.moveCamera(cameraUpdate);
            GetRouteTask routeTask = new GetRouteTask();
            routeTask.execute();

        }

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    private void drawRoute() {


    }

    GoogleMapRouteDirectionClass directionClass = new GoogleMapRouteDirectionClass();
    Document document;
    private class GetRouteTask extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog;
        String response = "";
        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            document = directionClass.getDocument(sourceLatLng, destinationLatLng, GoogleMapRouteDirectionClass.MODE_DRIVING);
            response = "Success";
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            ArrayList<LatLng> directionPoints = directionClass.getDirection(document);
            mMap.addPolyline(new PolylineOptions().addAll(directionPoints).width(4).color(Color.BLUE).geodesic(true));

            ((TextView) findViewById(id.actual_fly_dist)).setText("Distance :"+getDistance(directionPoints)+" meters");
            ((TextView) findViewById(id.actual_fly_dist)).setTextColor(Color.BLUE);

        }
    }

    private String getDistance(ArrayList<LatLng> directionPoints) {
    int distance=0;

    for(int i=0;i<directionPoints.size()-1;i++){
            Location l1=new Location("source");
            l1.setLatitude(directionPoints.get(i).latitude);
            l1.setLongitude(directionPoints.get(i).longitude);

            Location l2=new Location("dest");
            l2.setLatitude(directionPoints.get(i+1).latitude);
            l2.setLongitude(directionPoints.get(i+1).longitude);
            distance= (int) (distance+l1.distanceTo(l2));



    }


    return distance+"";
    }



//     /* The number of points allowed per API request. This is a fixed value.
//     */
//    private static final int PAGE_SIZE_LIMIT = 100;
//
//    /**
//     * Define the number of data points to re-send at the start of subsequent requests. This helps
//     * to influence the API with prior data, so that paths can be inferred across multiple requests.
//     * You should experiment with this value for your use-case.
//     */
//    private static final int PAGINATION_OVERLAP = 5;
//
//    private ProgressBar mProgressBar;
//
//    List<SnappedPoint> mSnappedPoints;
//
//    AsyncTask<Void, Void, List<SnappedPoint>> mTaskSnapToRoads =
//            new AsyncTask<Void, Void, List<SnappedPoint>>() {
//                @Override
//                protected void onPreExecute() {
//                    mProgressBar.setVisibility(View.VISIBLE);
//                    mProgressBar.setIndeterminate(true);
//                }
//
//                @Override
//                protected List<SnappedPoint> doInBackground(Void... params) {
//                    try {
//                        return new ArrayList<>();
//                    } catch (final Exception ex) {
//
//                        ex.printStackTrace();
//                        return null;
//                    }
//                }
//
//                @Override
//                protected void onPostExecute(List<SnappedPoint> snappedPoints) {
//                    mSnappedPoints = snappedPoints;
//                    mProgressBar.setVisibility(View.INVISIBLE);
//
//                    com.google.android.gms.maps.model.LatLng[] mapPoints =
//                            new com.google.android.gms.maps.model.LatLng[mSnappedPoints.size()];
//                    int i = 0;
//                    LatLngBounds.Builder bounds = new LatLngBounds.Builder();
//                    for (SnappedPoint point : mSnappedPoints) {
//                        mapPoints[i] = new com.google.android.gms.maps.model.LatLng(point.location.lat,
//                                point.location.lng);
//                        bounds.include(mapPoints[i]);
//                        i += 1;
//                    }
//
//                    mMap.addPolyline(new PolylineOptions().add(mapPoints).color(Color.BLUE));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0));
//                }
//            };


}
