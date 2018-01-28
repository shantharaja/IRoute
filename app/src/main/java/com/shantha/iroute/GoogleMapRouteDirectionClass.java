package com.shantha.iroute;

import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



class GoogleMapRouteDirectionClass
{
    public final static String MODE_DRIVING = "driving";
    public final String MODE_WALKING = "walking";

    public GoogleMapRouteDirectionClass()
    {
        responseStrBuilder=new StringBuilder();

    }

    public Document getDocument(LatLng start, LatLng end, String mode)
    {
        if(start!=null&&end!=null)
            try
            {
                URL url = new URL("http://maps.googleapis.com/maps/api/directions/xml?"
                        + "origin=" + start.latitude + "," + start.longitude
                        + "&destination=" + end.latitude + "," + end.longitude
                        + "&sensor=false&units=metric&mode=driving");



                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document doc = builder.parse(in);
                    return doc;
                } finally {
                    urlConnection.disconnect();

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        return null;
    }

    StringBuilder responseStrBuilder = new StringBuilder();
    public JSONObject getSnappedPoints(ArrayList<LatLng> latLngs)
    {
        if(latLngs!=null)
            try
            {
                StringBuilder urlBuilder=new StringBuilder();
                urlBuilder.append("https://roads.googleapis.com/v1/snapToRoads?");
                urlBuilder.append("path=");
                int pointCount=latLngs.size();
                int start=0;

                if(pointCount>100){
                    getSnappedPoints((ArrayList<LatLng>) latLngs.subList(0,100));
                    pointCount=latLngs.subList(100,latLngs.size()-1).size();
                    start=100;
                }

                for(int i=start;i<pointCount;i++){
                    LatLng point= latLngs.get(i);
                    urlBuilder.append(point.latitude+",");
                    urlBuilder.append(point.longitude+"");
                    if(i!=pointCount-1) {
                        urlBuilder.append("|");
                    }
                }
                urlBuilder.append("&interpolate=true");
                urlBuilder.append("&key=AIzaSyD3Icr1zhUKHBeWMQzpNmyOPjdrJVdYU6I");
                URL url = new URL(urlBuilder.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    String response="{}";

                    BufferedReader bR = new BufferedReader(  new InputStreamReader(in));
                    String line = "";
                    while((line =  bR.readLine()) != null){

                        responseStrBuilder.append(line);
                    }
                    in.close();

                    JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());

                    return jsonObject;
                } finally {
                    urlConnection.disconnect();

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        return null;
    }





    public JSONObject getNearestRoadPoints(ArrayList<LatLng> latLngs)
    {
        if(latLngs!=null)
            try
            {
                StringBuilder urlBuilder=new StringBuilder();
                urlBuilder.append("https://roads.googleapis.com/v1/nearestRoads?");
                urlBuilder.append("points=");
                int pointCount=latLngs.size();
                int start=0;

                if(pointCount>100){
                    getNearestRoadPoints((ArrayList<LatLng>) latLngs.subList(0,100));
                    pointCount=latLngs.subList(100,latLngs.size()-1).size();
                    start=100;
                }

                for(int i=start;i<pointCount;i++){
                    LatLng point= latLngs.get(i);
                    urlBuilder.append(point.latitude+",");
                    urlBuilder.append(point.longitude+"");

                    if(i!=pointCount-1) {
                        urlBuilder.append("|");
                    }

                }
                urlBuilder.append("&interpolate=true");
                urlBuilder.append("&key=AIzaSyD3Icr1zhUKHBeWMQzpNmyOPjdrJVdYU6I");
                URL url = new URL(urlBuilder.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bR = new BufferedReader(  new InputStreamReader(in));
                    String line = "";
                    while((line =  bR.readLine()) != null){
                        responseStrBuilder.append(line);
                    }
                    in.close();
                    JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
                    return jsonObject;
                } finally {
                    urlConnection.disconnect();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        return null;
    }

    public String getDurationText(Document doc)
    {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        Log.i("DurationText", node2.getTextContent());
        return node2.getTextContent();
    }

    public int getDurationValue(Document doc)
    {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DurationValue", node2.getTextContent());
        return Integer.parseInt("0"+node2.getTextContent());
    }

    public String getDistanceText(Document doc)
    {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        Log.i("DistanceText", node2.getTextContent());
        return node2.getTextContent();
    }

    public int getDistanceValue(Document doc)
    {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DistanceValue", node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    }

    public int getDistanceValue(JSONObject doc)
    {
        int totalDistance=0;
        JSONArray snappedPoints, nl2, nl3;
        if(doc!=null)
            try
            {

                snappedPoints = doc.getJSONArray("snappedPoints");
                if (snappedPoints.length() > 0)
                {
                    for (int i = 0; i < snappedPoints.length()-1; i++)
                    {

                        try
                        {
                            JSONObject locationObject1 = snappedPoints.getJSONObject(i);

                            JSONObject location1=locationObject1.getJSONObject("location");

                            JSONObject locationObject2 = snappedPoints.getJSONObject(i+1);

                            JSONObject location2=locationObject2.getJSONObject("location");

                            double latitude1 =location1.getDouble("latitude");
                            double longitude1 =location1.getDouble("longitude");

                            Location loc1 = new Location("source1");
                            loc1.setLatitude(latitude1);
                            loc1.setLongitude(longitude1);


                            double latitude2 =location2.getDouble("latitude");
                            double longitude2 =location2.getDouble("longitude");

                            Location loc2 = new Location("source2");
                            loc2.setLatitude(latitude2);
                            loc2.setLongitude(longitude2);
                            float distanceInMeters = loc1.distanceTo(loc2);
                            totalDistance=totalDistance+(int) distanceInMeters;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        return totalDistance;
    }

    public String getStartAddress(Document doc)
    {
        NodeList nl1 = doc.getElementsByTagName("start_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    }

    public String getEndAddress(Document doc)
    {
        NodeList nl1 = doc.getElementsByTagName("end_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    }

    public String getCopyRights(Document doc)
    {
        NodeList nl1 = doc.getElementsByTagName("copyrights");
        Node node1 = nl1.item(0);
        Log.i("CopyRights", node1.getTextContent());
        return node1.getTextContent();
    }

    public ArrayList<LatLng> getDirection(Document doc)
    {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        if(doc!=null)
            try
            {

                nl1 = doc.getElementsByTagName("step");
                if (nl1.getLength() > 0)
                {
                    for (int i = 0; i < nl1.getLength(); i++)
                    {

                        try
                        {
                            Node node1 = nl1.item(i);
                            nl2 = node1.getChildNodes();

                            Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
                            nl3 = locationNode.getChildNodes();
                            Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                            double lat = Double.parseDouble(latNode.getTextContent());
                            Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                            double lng = Double.parseDouble(lngNode.getTextContent());
                            listGeopoints.add(new LatLng(lat, lng));

                            locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                            nl3 = locationNode.getChildNodes();
                            latNode = nl3.item(getNodeIndex(nl3, "points"));
                            ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                            for (int j = 0; j < arr.size(); j++)
                            {
                                listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
                            }

                            locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                            nl3 = locationNode.getChildNodes();
                            latNode = nl3.item(getNodeIndex(nl3, "lat"));
                            lat = Double.parseDouble(latNode.getTextContent());
                            lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                            lng = Double.parseDouble(lngNode.getTextContent());
                            listGeopoints.add(new LatLng(lat, lng));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        for(LatLng latLng: listGeopoints) {
            JSONArray jsonArray=new JSONArray();
            try{
                JSONObject jsonObject =new JSONObject();
                jsonObject.put("lat",latLng.latitude);
                jsonObject.put("lon",latLng.longitude);
                jsonArray.put(jsonObject);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return listGeopoints;
    }




    public ArrayList<LatLng> getSnappedDirection(JSONObject doc)
    {
        JSONArray snappedPoints, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        if(doc!=null)
            try
            {

                snappedPoints = doc.getJSONArray("snappedPoints");
                if (snappedPoints.length() > 0)
                {
                    for (int i = 0; i < snappedPoints.length(); i++)
                    {

                        try
                        {
                            JSONObject locationObject = snappedPoints.getJSONObject(i);
                            JSONObject location=locationObject.getJSONObject("location");
                            double latitude =location.getDouble("latitude");
                            double longitude =location.getDouble("longitude");
                            listGeopoints.add(new LatLng(latitude, longitude));

//						locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
//						nl3 = locationNode.getChildNodes();
//						latNode = nl3.item(getNodeIndex(nl3, "points"));
//						ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
//						for (int j = 0; j < arr.size(); j++)
//						{
//							listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
//						}
//
//						locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
//						nl3 = locationNode.getChildNodes();
//						latNode = nl3.item(getNodeIndex(nl3, "lat"));
//						lat = Double.parseDouble(latNode.getTextContent());
//						lngNode = nl3.item(getNodeIndex(nl3, "lng"));
//						lng = Double.parseDouble(lngNode.getTextContent());
//						listGeopoints.add(new LatLng(lat, lng));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        for(LatLng latLng: listGeopoints) {
            JSONArray jsonArray=new JSONArray();
            try{
                JSONObject jsonObject =new JSONObject();
                jsonObject.put("lat",latLng.latitude);
                jsonObject.put("lon",latLng.longitude);
                jsonArray.put(jsonObject);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return listGeopoints;
    }

    private int getNodeIndex(NodeList nl, String nodename)
    {
        for (int i = 0; i < nl.getLength(); i++)
        {
            if (nl.item(i).getNodeName().equals(nodename))
            {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<LatLng> decodePoly(String encoded)
    {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len)
        {
            int b, shift = 0, result = 0;
            do
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

}
