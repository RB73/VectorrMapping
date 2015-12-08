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
                setCurrentImage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        startPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
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
                setCurrentImage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        endPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
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

        //Turn the ArrayLists to Adapters
        ArrayAdapter startAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter startPointAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        startPointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter endAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter endPointAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
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
        Intent intent = new Intent(this, displayRoute.class);

        //Create Spinners
        Spinner start = (Spinner) findViewById(R.id.start_spinner);
        Spinner startPoint = (Spinner) findViewById(R.id.point1_spinner);
        Spinner end = (Spinner) findViewById(R.id.end_spinner);
        Spinner endPoint = (Spinner) findViewById(R.id.point2_spinner);

        //Get strings from Spinners
        String sMap = start.getSelectedItem().toString();
        String sPoint = startPoint.getSelectedItem().toString();
        String eMap = end.getSelectedItem().toString();
        String ePoint = endPoint.getSelectedItem().toString();

        //Store the Strings as extra data
        intent.putExtra("startMap", sMap);
        intent.putExtra("startPoint", sPoint);
        intent.putExtra("endMap", eMap);
        intent.putExtra("endPoint", ePoint);
        intent.putExtra("mapNum", img);
        intent.putExtra("colorBlind", colorBlind);
        //Start the activity
        startActivity(intent);
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
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.campus_center_1, imageView.getWidth(), imageView.getMaxHeight()));
                break;
            case 3:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.project_center_1, imageView.getWidth(), imageView.getMaxHeight()));
                break;
            case 4:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.stratton_hall_1, imageView.getWidth(), imageView.getMaxHeight()));
                break;
            case 5:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.higgins_house_1, imageView.getWidth(), imageView.getMaxHeight()));
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

    }
}
