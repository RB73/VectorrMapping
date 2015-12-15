package com.vectorr.vectorrmapping;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class GUI extends AppCompatActivity {
    //private ServerDB md = ServerDB.getInstance(); // Get the server
    private ArrayList<com.vectorr.vectorrmapping.Map> maps = new ArrayList<com.vectorr.vectorrmapping.Map>();
    int img = 0;
    private Menu menu;
    ArrayList<Point> pointList = new ArrayList<Point>();
    ArrayList<String> startPoints = new ArrayList<String>();
    ArrayList<String> endPoints = new ArrayList<String>();
    int startMap = -1;
    int startPointNum = -1;
    int endMap = -1;
    int endPointNum = -1;
    ArrayList<Map> allMaps = new ArrayList<Map>();
    ArrayList<Point> startFill = new ArrayList<Point>();
    ArrayList<Point> endFill = new ArrayList<Point>();
    SearchLocation search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setInitialImage();
// Start spinner fill code
        generateData();
        MySQLiteHelper myHelper = new MySQLiteHelper(this);
        allMaps = myHelper.getMaps();
        search = new SearchLocation();
        database.getInstance().setData(allMaps);
        //Create Spinners
        Spinner start = (Spinner) findViewById(R.id.start_spinner);
        Spinner startPoint = (Spinner) findViewById(R.id.point1_spinner);
        Spinner end = (Spinner) findViewById(R.id.end_spinner);
        Spinner endPoint = (Spinner) findViewById(R.id.point2_spinner);

        //Set Spinner Listeners
        start.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                img = position;
                if(startMap != position - 1) {
                    Spinner startPoint = (Spinner) findViewById(R.id.point1_spinner);
                    startPoint.setSelection(0);
                }
                startMap = position - 1;
                setCurrentImage();
                startPoints.clear();
                startPoints.add("Select Point");
                startFill.clear();
                if(position > 0) {
                    for (int i = 0; i < allMaps.get(position - 1).getPointList().size(); i++) {
                        if (!allMaps.get(position - 1).getPointList().get(i).getName().equalsIgnoreCase("Hallway") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().contains("Stair") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().equalsIgnoreCase("Path") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().contains("stair") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().equalsIgnoreCase("room") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().contains("Elevator") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().contains("elevator")) {

                            startFill.add(allMaps.get(position - 1).getPointList().get(i));
                        }
                    }
                }
                for(int i = 0; i < startFill.size(); i++){
                    startPoints.add(startFill.get(i).getName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        startPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                startPointNum = position - 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        end.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                img = position;
                if(endMap != position - 1) {
                    Spinner endPoint = (Spinner) findViewById(R.id.point2_spinner);
                    endPoint.setSelection(0);
                }
                endMap = position - 1;
                setCurrentImage();
                endPoints.clear();
                endPoints.add("Select Point");
                endFill.clear();
                if(position > 0) {
                    for (int i = 0; i < allMaps.get(position - 1).getPointList().size(); i++) {
                        if (!allMaps.get(position - 1).getPointList().get(i).getName().equalsIgnoreCase("Hallway") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().contains("Stair") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().equalsIgnoreCase("Path") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().contains("stair") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().equalsIgnoreCase("room") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().contains("Elevator") &&
                                !allMaps.get(position - 1).getPointList().get(i).getName().contains("elevator")) {

                            endFill.add(allMaps.get(position - 1).getPointList().get(i));
                        }
                    }
                }
                for(int i = 0; i < endFill.size(); i++){
                    endPoints.add(endFill.get(i).getName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        endPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                endPointNum = position - 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //Populate ArrayLists for Spinners
/*        ArrayList<String> rooms = new ArrayList<String>();
        rooms.add("Select room #");
        rooms.add("Please choose building first");
        ArrayList<String> startRooms = rooms;
        ArrayList<String> endRooms = rooms;

        maps = md.getMapsFromLocal();
        ArrayList<String> mapString = new ArrayList<String>();
        for(int i = 0; i < maps.size(); i++){
            mapString.add(maps.get(i).getMapName());
        }*/
        ArrayList<String> countries = new ArrayList<String>();
        countries.add("Australia");
        countries.add("Canada");
        countries.add("China");
        countries.add("India");
        countries.add("Sri Lanka");
        countries.add("United States");

        ArrayList<String> maps = new ArrayList<String>();
        maps.add("Select Map");
        Collections.sort(allMaps);
        ArrayList<Point> pointList = new ArrayList<Point>();
        for(int i = 0; i < allMaps.size(); i++){
            Collections.sort(allMaps.get(i).getPointList());
            pointList.addAll(allMaps.get(i).getPointList());
        }
        search.prepData(pointList);
        for(int i = 0; i < allMaps.size(); i++){
            String toAdd = "";
            boolean prevIsUnderscore = true;
            for(int j = 0; j < allMaps.get(i).getMapName().length(); j++){
                char tempChar;
                if(prevIsUnderscore){
                    tempChar = allMaps.get(i).getMapName().charAt(j);
                    //converts to upper case
                    tempChar = Character.toUpperCase(tempChar);
                    prevIsUnderscore = false;
                }
                else if (allMaps.get(i).getMapName().charAt(j) == ('_')){
                    prevIsUnderscore = true;
                    tempChar = ' ';
                }
                else{
                    tempChar = allMaps.get(i).getMapName().charAt(j);
                    prevIsUnderscore = false;
                }
                toAdd += tempChar;
            }
            maps.add(toAdd);
        }

        startPoints.add("Select Point");
        endPoints.add("Select Point");

        //Turn the ArrayLists to Adapters
        ArrayAdapter startAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, maps);
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter startPointAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, startPoints);
        startPointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter endAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, maps);
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter endPointAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, endPoints);
        endPointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Put Adapters into Spinners
        start.setAdapter(startAdapter);
        startPoint.setAdapter(startPointAdapter);
        end.setAdapter(endAdapter);
        endPoint.setAdapter(endPointAdapter);
// End spinner fill code
        //Search Fields
        final AutoCompleteTextView startSearch = (AutoCompleteTextView) findViewById(R.id.start_search);
        startSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startSearching(v.getText().toString());
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(startSearch.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });
        final AutoCompleteTextView endSearch = (AutoCompleteTextView) findViewById(R.id.end_search);
        endSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    endSearching(v.getText().toString());
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(endSearch.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });
        ArrayList<String> keys = search.getKeys();
        ArrayAdapter<String> sSearchAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, keys);
        startSearch.setAdapter(sSearchAdapter);
        ArrayAdapter<String> eSearchAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, keys);
        endSearch.setAdapter(eSearchAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_gui, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, preferences.class);
            startActivity(intent);
        } else if(id == R.id.action_help) {
            Intent intent = new Intent(this, Help.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    private void setOptionTitle(int id, String title)
    {
        MenuItem item = menu.findItem(id);
        item.setTitle(title);
    }

    public void generatePath(View view) {
        if(startMap != -1 && startPointNum != -1 && endMap !=-1 && endPointNum !=-1) {
            Intent intent = new Intent(this, displayRoute.class);

            //Create Spinners
            Spinner start = (Spinner) findViewById(R.id.start_spinner);
            Spinner startPoint = (Spinner) findViewById(R.id.point1_spinner);
            Spinner end = (Spinner) findViewById(R.id.end_spinner);
            Spinner endPoint = (Spinner) findViewById(R.id.point2_spinner);

            //Get strings from Spinners
            /*
            String sMap = start.getSelectedItem().toString();
            String sPoint = startPoint.getSelectedItem().toString();
            String eMap = end.getSelectedItem().toString();
            String ePoint = endPoint.getSelectedItem().toString();
            */

            //Store the Strings as extra data
            intent.putExtra("startMap", startMap);
            String startPointName = new String();
            startPointName = startPoint.getSelectedItem().toString();
            for(int i = 0; i < allMaps.get(startMap).getPointList().size(); i++){
                if(allMaps.get(startMap).getPointList().get(i).getName().contains(startPointName)){
                    startPointNum = i;
                    break;
                }
            }
            intent.putExtra("startPoint", startPointNum);
            intent.putExtra("endMap", endMap);
            String endPointName = new String();
            endPointName = endPoint.getSelectedItem().toString();
            for(int i = 0; i < allMaps.get(endMap).getPointList().size(); i++){
                if(allMaps.get(endMap).getPointList().get(i).getName().contains(endPointName)){
                    endPointNum = i;
                    break;
                }
            }
            intent.putExtra("endPoint", endPointNum);
            intent.putExtra("mapNum", img);
            //Start the activity
            startActivity(intent);
        }
    }
    //Change Image

    private void setInitialImage() {
        setCurrentImage();
    }

    private void setCurrentImage() {

        final TouchImageView imageView = (TouchImageView) findViewById(R.id.imageDisplay);
        if (img == 0) {
            imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.vectorr_logo, imageView.getWidth(), imageView.getMaxHeight()));
        } else {
            String mapName = allMaps.get(img - 1).getMapName();
            if(mapName.contains("157_west")){
                if(mapName.contains("_1")){
                    imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.west_157_1, imageView.getWidth(), imageView.getMaxHeight()));
                } else if(mapName.contains("_2")){
                    imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.west_157_2, imageView.getWidth(), imageView.getMaxHeight()));
                } else if(mapName.contains("_b")){
                    imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.west_157_basement, imageView.getWidth(), imageView.getMaxHeight()));
                }
            }
            else {
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), getResources().getIdentifier(mapName , "drawable", getPackageName()), imageView.getWidth(), imageView.getMaxHeight()));
            }
        }
    }
    //Image loading
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        inSampleSize *= 2.5;
        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    private void generateData(){
        Point p1 = new Point("0", 1, "Fountain", 0, 0.5845601750907536, 0.5209838991465665, 1345, 826, 0);
        Point p2 = new Point("1", 1, "Path", 1, 0.6324252357410155, 0.5329244227699498, 1455, 845, 0);
        Point p3 = new Point("2", 1, "Path", 2, 0.6315588998016441, 0.5445507220874546, 1453, 864, 0);
        Point p4 = new Point("3", 1, "GL Entrance", 3, 0.6915526636031035, 0.5561770214049594, 1591, 882, 0);
        Point p5 = new Point("4", 4, "GL Entrance", 4, 0.5301687037355828, 0.8486486486486486, 117, 557, 0);
        Point p6 = new Point("5", 4, "Hallway", 5, 0.5276639697021863, 0.6675675675675675, 401, 585, 0);
        Point p7 = new Point("6", 4, "Help Desk", 6, 0.6854622138061629, 0.6675675675675675, 365, 743, 0);

        Edge e1 = new Edge(p1, p2);
        Edge e2 = new Edge(p2, p3);
        Edge e3 = new Edge(p3, p4);
        Edge e4 = new Edge(p4, p5);
        Edge e5 = new Edge(p5, p6);
        Edge E6 = new Edge(p6, p7);

        pointList.add(p1);
        pointList.add(p2);
        pointList.add(p3);
        pointList.add(p4);
        pointList.add(p5);
        pointList.add(p6);
        pointList.add(p7);
    }
    public void swap(View view){
        Spinner start = (Spinner) findViewById(R.id.start_spinner);
        Spinner startPoint = (Spinner) findViewById(R.id.point1_spinner);
        Spinner end = (Spinner) findViewById(R.id.end_spinner);
        Spinner endPoint = (Spinner) findViewById(R.id.point2_spinner);

        int startPos = start.getSelectedItemPosition();
        int startPointPos = startPoint.getSelectedItemPosition();
        int endPos = end.getSelectedItemPosition();
        int endPointPos = endPoint.getSelectedItemPosition();

        startMap = endPos - 1;
        startPoints.clear();
        startPoints.add("Select Point");
        startFill.clear();
        if(endPos > 0) {
            for (int i = 0; i < allMaps.get(endPos - 1).getPointList().size(); i++) {
                if (!allMaps.get(endPos - 1).getPointList().get(i).getName().equalsIgnoreCase("Hallway") &&
                        !allMaps.get(endPos - 1).getPointList().get(i).getName().contains("Stair") &&
                        !allMaps.get(endPos - 1).getPointList().get(i).getName().equalsIgnoreCase("Path") &&
                        !allMaps.get(endPos - 1).getPointList().get(i).getName().contains("stair") &&
                        !allMaps.get(endPos - 1).getPointList().get(i).getName().equalsIgnoreCase("room") &&
                        !allMaps.get(endPos - 1).getPointList().get(i).getName().contains("Elevator") &&
                        !allMaps.get(endPos - 1).getPointList().get(i).getName().contains("elevator")) {

                    startFill.add(allMaps.get(endPos - 1).getPointList().get(i));
                }
            }
        }
        for(int i = 0; i < startFill.size(); i++){
            startPoints.add(startFill.get(i).getName());
        }
        endMap = startPos - 1;
        setCurrentImage();
        endPoints.clear();
        endPoints.add("Select Point");
        endFill.clear();
        if(startPos > 0) {
            for (int i = 0; i < allMaps.get(startPos - 1).getPointList().size(); i++) {
                if (!allMaps.get(startPos - 1).getPointList().get(i).getName().equalsIgnoreCase("Hallway") &&
                        !allMaps.get(startPos - 1).getPointList().get(i).getName().contains("Stair") &&
                        !allMaps.get(startPos - 1).getPointList().get(i).getName().equalsIgnoreCase("Path") &&
                        !allMaps.get(startPos - 1).getPointList().get(i).getName().contains("stair") &&
                        !allMaps.get(startPos - 1).getPointList().get(i).getName().equalsIgnoreCase("room") &&
                        !allMaps.get(startPos - 1).getPointList().get(i).getName().contains("Elevator") &&
                        !allMaps.get(startPos - 1).getPointList().get(i).getName().contains("elevator")) {

                    endFill.add(allMaps.get(startPos - 1).getPointList().get(i));
                }
            }
        }
        for(int i = 0; i < endFill.size(); i++){
            endPoints.add(endFill.get(i).getName());
        }

        start.setSelection(endPos);
        startPoint.setSelection(endPointPos);
        end.setSelection(startPos);
        endPoint.setSelection(startPointPos);
    }
    public void startSearching(String name){
        Spinner start = (Spinner) findViewById(R.id.start_spinner);
        Spinner startPoint = (Spinner) findViewById(R.id.point1_spinner);

        Point point = search.getPointFromName(name);
        for(int i = 0; i < allMaps.size(); i++){
            if(allMaps.get(i).getMapId() == point.getMapId()){
                startMap = i;
                startPoints.clear();
                startPoints.add("Select Point");
                startFill.clear();
                if(i > 0) {
                    for (int j = 0; j < allMaps.get(i).getPointList().size(); j++) {
                        if (!allMaps.get(i).getPointList().get(j).getName().equalsIgnoreCase("Hallway") &&
                                !allMaps.get(i).getPointList().get(j).getName().contains("Stair") &&
                                !allMaps.get(i).getPointList().get(j).getName().equalsIgnoreCase("Path") &&
                                !allMaps.get(i).getPointList().get(j).getName().contains("stair") &&
                                !allMaps.get(i).getPointList().get(j).getName().equalsIgnoreCase("room") &&
                                !allMaps.get(i).getPointList().get(j).getName().contains("Elevator") &&
                                !allMaps.get(i).getPointList().get(j).getName().contains("elevator")) {

                            startFill.add(allMaps.get(i).getPointList().get(j));
                        }
                    }
                }
                for(int j = 0; j < startFill.size(); j++){
                    startPoints.add(startFill.get(j).getName());
                }
                start.setSelection(i+1);
                break;
            }
        }
        for(int i = 0; i < startPoints.size(); i++){
            if(startFill.get(i).getId().contains(point.getId())){
                startPoint.setSelection(i + 1);
                break;
            }
        }
    }
    public void endSearching(String name){
        Spinner end = (Spinner) findViewById(R.id.end_spinner);
        Spinner endPoint = (Spinner) findViewById(R.id.point2_spinner);

        Point point = search.getPointFromName(name);
        for(int i = 0; i < allMaps.size(); i++){
            if(allMaps.get(i).getMapId() == point.getMapId()){
                endMap = i;
                endPoints.clear();
                endPoints.add("Select Point");
                endFill.clear();
                if(i > 0) {
                    for (int j = 0; j < allMaps.get(i).getPointList().size(); j++) {
                        if (!allMaps.get(i).getPointList().get(j).getName().equalsIgnoreCase("Hallway") &&
                                !allMaps.get(i).getPointList().get(j).getName().contains("Stair") &&
                                !allMaps.get(i).getPointList().get(j).getName().equalsIgnoreCase("Path") &&
                                !allMaps.get(i).getPointList().get(j).getName().contains("stair") &&
                                !allMaps.get(i).getPointList().get(j).getName().equalsIgnoreCase("room") &&
                                !allMaps.get(i).getPointList().get(j).getName().contains("Elevator") &&
                                !allMaps.get(i).getPointList().get(j).getName().contains("elevator")) {

                            endFill.add(allMaps.get(i).getPointList().get(j));
                        }
                    }
                }
                for(int j = 0; j < endFill.size(); j++){
                    endPoints.add(endFill.get(j).getName());
                }
                end.setSelection(i+1);
                break;
            }
        }
        for(int i = 0; i < endPoints.size(); i++){
            if(endFill.get(i).getId().contains(point.getId())){
                endPoint.setSelection(i + 1);
                break;
            }
        }
    }
}
