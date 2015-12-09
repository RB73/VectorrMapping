package com.vectorr.vectorrmapping;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;

public class GUI extends AppCompatActivity {
    //private ServerDB md = ServerDB.getInstance(); // Get the server
    private ArrayList<com.vectorr.vectorrmapping.Map> maps = new ArrayList<com.vectorr.vectorrmapping.Map>();
    int img = 0;
    Boolean colorBlind = false;
    private Menu menu;
    ArrayList<Point> pointList = new ArrayList<Point>();
    ArrayList<String> startPoints = new ArrayList<String>();
    ArrayList<String> endPoints = new ArrayList<String>();
    int startMap = 0;
    int startPointNum = 0;
    int endMap = 0;
    int endPointNum = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setInitialImage();
// Start spinner fill code
        generateData();
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
                startMap = position;
                setCurrentImage();
                switch(position){
                    case 0:
                        startPoints.clear();
                        startPoints.add("Select Point");
                        break;
                    case 1:
                        startPoints.clear();
                        startPoints.add("Select Point");
                        startPoints.add("Fountain");
                        startPoints.add("GL Entrance");
                        break;
                    case 2:
                        startPoints.clear();
                        startPoints.add("Select Point");
                        startPoints.add("GL Entrance");
                        startPoints.add("Help Desk");
                        break;
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
                startPointNum = position;
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
                endMap = position;
                setCurrentImage();
                switch(position) {
                    case 0:
                        endPoints.clear();
                        endPoints.add("Select Point");
                        break;
                    case 1:
                        endPoints.clear();
                        endPoints.add("Select Point");
                        endPoints.add("Fountain");
                        endPoints.add("GL Entrance");
                        break;
                    case 2:
                        endPoints.clear();
                        endPoints.add("Select Point");
                        endPoints.add("GL Entrance");
                        endPoints.add("Help Desk");
                        break;
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
                endPointNum = position;
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
        maps.add("Campus");
        maps.add("Gordon Library");

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
            if (colorBlind){
                colorBlind = false;
                setOptionTitle(R.id.action_settings, "Disable Color Blind Mode");
            }
            else{
                colorBlind = true;
                setOptionTitle(R.id.action_settings, "Enable Color Blind Mode");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setOptionTitle(int id, String title)
    {
        MenuItem item = menu.findItem(id);
        item.setTitle(title);
    }

    public void generatePath(View view) {
        if(startMap != 0 && startPointNum != 0 && endMap !=0 && endPointNum !=0) {
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
            intent.putExtra("startPoint", startPoint.getSelectedItem().toString());
            intent.putExtra("endMap", endMap);
            intent.putExtra("endPoint", endPoint.getSelectedItem().toString());
            intent.putExtra("mapNum", img);
            intent.putExtra("colorBlind", colorBlind);
            //Start the activity
            startActivity(intent);
        }
    }
    //Change Image

    private void setInitialImage() {
        setCurrentImage();
    }

    private void setCurrentImage() {

        final ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        switch(img){
            case 0:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.vectorr_logo, imageView.getWidth(), imageView.getMaxHeight()));
                break;
            case 1:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.campus, imageView.getWidth(), imageView.getMaxHeight()));
                break;
            case 2:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.gordon_library_2, imageView.getWidth(), imageView.getMaxHeight()));
                break;
            case 3:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.project_center_1, imageView.getWidth(), imageView.getMaxHeight()));
                break;
            case 4:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.stratton_hall_1, imageView.getWidth(), imageView.getMaxHeight()));
                break;
            case 5:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.higgins_1, imageView.getWidth(), imageView.getMaxHeight()));
                break;
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
        inSampleSize *= 2;
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
}
