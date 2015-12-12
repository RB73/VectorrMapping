package com.vectorr.vectorrmapping;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class displayRoute extends AppCompatActivity {
    ArrayList<Point> path = new ArrayList<Point>();
    ArrayList<Directions> finalDir = new ArrayList<Directions>();
    ArrayList<ArrayList<Directions>> MultiMapFinalDir = new ArrayList<ArrayList<Directions>>();
    ArrayList<ArrayList<String>> textDir = new ArrayList<ArrayList<String>>();
    int textPos = 0;
    Bitmap bitmap;
    int before = Color.rgb(255, 75, 75);
    int after = Color.rgb(51, 255, 51);
    int current = Color.rgb(219, 209, 0);
    ArrayList<Point> pointList = new ArrayList<Point>();
    ArrayList<Point> route = new ArrayList<Point>();
    int mapPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_route);

        //Get extras
        Intent intent = getIntent();
        Bundle locations = intent.getExtras();

        //Extract data
        int startMap = locations.getInt("startMap");
        String startPoint = locations.getString("startPoint");
        int endMap = locations.getInt("endMap");
        String endPoint = locations.getString("endPoint");

        //Create string to display
        /*
        StringBuilder message = new StringBuilder();
        message.append(startMap + "  ");
        message.append(startPoint + "\n");
        message.append(endMap + "  ");
        message.append(endPoint + "\n");


        //Display string
        TextView textView = (TextView) findViewById(R.id.text_box);
        textView.setTextSize(20);
        textView.setText(message);
*/
        TextView direction = (TextView) findViewById(R.id.directions);
        direction.setTextSize(20);

        //Select Map to display
        //Get Map Number
        int img = locations.getInt("mapNum");

        //Set ImageView
        final TouchImageView imageView = (TouchImageView) findViewById(R.id.imageDisplay);
        imageView.setMaxZoom(5);
 /*       switch(img) {
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
*/
        switch (startMap) {
            case 0:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.vectorr_logo, imageView.getWidth(), imageView.getMaxHeight()));
                break;
            case 1:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.campus, imageView.getWidth(), imageView.getMaxHeight()));
                break;
            case 2:
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.gordon_library_2, imageView.getWidth(), imageView.getMaxHeight()));
                break;
        }
        //Draw route onto map
        generateData();
        Point one = new Point("1", "first", 0, 0);
        Point two = new Point("2", "second", .25, .1);
        Point three = new Point("3", "third", .1, .25);
        Point four = new Point("4", "fourth", .5, .5);
        Point five = new Point("5", "fifth", .75, .85);
        Point six = new Point("6", "sixth", 1, 1);
        path.add(one);
        path.add(two);
        path.add(three);
        path.add(four);
        path.add(five);
        path.add(six);

        AStar astar = new AStar();
        astar.reset();
        Point startLoc = null;
        Point endLoc = null;
        for (Point point : pointList) {
            if (point.getName().equals(startPoint) && point.getMapId() == startMap) {
                startLoc = point;
            }
        }
        for (Point point : pointList) {
            if (point.getName().equals(endPoint) && point.getMapId() == endMap) {
                endLoc = point;
            }
        }
        route = astar.PathFind(startLoc, endLoc, 0, 0);

        //Check colorblind mode
        if (locations.getBoolean("colorBlind")) {
            before = Color.rgb(182, 109, 255);
            after = Color.rgb(0, 146, 146);
        }

        //Generate Text Directions
        GenTextDir gentextdir = new GenTextDir();
        ArrayList<Directions> tempDir = gentextdir.genTextDir(route, 2.8);
        try {
            finalDir = gentextdir.generateDirections(tempDir);
        } catch (MalformedDirectionException e) {
            e.printStackTrace();
        }

        MultiMapFinalDir = gentextdir.genMultiMapDirections(finalDir);
        try {
            textDir = gentextdir.genDirStrings(MultiMapFinalDir);
        } catch (MalformedDirectionException e) {
            e.printStackTrace();
        }
        //Set Text
        direction.setText(textDir.get(0).get(0));

        //Load Canvas, ImageView, Bitmap and other data for drawing
        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap drawBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(drawBitmap);
        float maxX = canvas.getWidth();
        float maxY = canvas.getHeight();
        Paint myPaint = new Paint();
        myPaint.setColor(after);
        myPaint.setStrokeWidth(40);
        //Draw map onto canvas
        canvas.drawColor(Color.rgb(255, 255, 255));
        canvas.drawBitmap(bitmap, 0, 0, null);

        //Draw route to map
        for (int i = 0; i < MultiMapFinalDir.get(0).size(); i++) {
            canvas.drawLine((float) MultiMapFinalDir.get(0).get(i).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(0).get(i).getOrigin().getLocY() * maxY, (float) MultiMapFinalDir.get(0).get(i).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(0).get(i).getDestination().getLocY() * maxY, myPaint);
            canvas.drawCircle((float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocY() * maxY, 20, myPaint);
        }
        //Draw current step
        myPaint.setColor(current);
        myPaint.setStrokeWidth(60);
        canvas.drawLine((float) MultiMapFinalDir.get(0).get(0).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(0).get(0).getOrigin().getLocY() * maxY, (float) MultiMapFinalDir.get(0).get(0).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(0).get(0).getDestination().getLocY() * maxY, myPaint);
        canvas.drawCircle((float) MultiMapFinalDir.get(0).get(0).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(0).get(0).getOrigin().getLocY() * maxY, 50, myPaint);
        canvas.drawCircle((float) MultiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocY() * maxY, 30, myPaint);
        //Draw to ImageView
        imageView.setImageDrawable(new BitmapDrawable(getResources(), drawBitmap));
    }

    //Button Responses
    public void nextPath(View view) {
        //Load ImageView
        TouchImageView imageView = (TouchImageView) findViewById(R.id.imageDisplay);

        //Set Next Text
        TextView direction = (TextView) findViewById(R.id.directions);
        if (textDir == null) {

        } else {
            // Checks if incrementing textPos will set the array out of bounds
            // If it will, the user is at the end, display a message accordingly


            if (textPos < MultiMapFinalDir.get(mapPos).size()) {
                textPos++;

                if (textPos != MultiMapFinalDir.get(mapPos).size()) {
                    direction.setText(textDir.get(mapPos).get(textPos));
                } else if (mapPos != MultiMapFinalDir.size() - 1) {
                    direction.setText("Enter " + getMapName(mapPos + 2));
                } else {
                    textPos = MultiMapFinalDir.get(mapPos).size() - 1;
                    //mapPos = multiMapFinalDir.size() - 1;
                    direction.setText("You have arrived at your destination");
                }
            } else if (textPos == MultiMapFinalDir.get(mapPos).size() && mapPos != MultiMapFinalDir.size() - 1) {
                textPos = 0; // For route coloring
                mapPos++;

                direction.setText(textDir.get(mapPos).get(textPos));
                imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), getMap(mapPos + 1), imageView.getWidth(), imageView.getMaxHeight()));
                bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            }
        }
        //Load Canvas, Bitmap and other data for drawing
        Bitmap drawBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(drawBitmap);
        float maxX = canvas.getWidth();
        float maxY = canvas.getHeight();
        Paint myPaint = new Paint();
        myPaint.setColor(before);
        myPaint.setStrokeWidth(40);
        //Draw map onto canvas
        canvas.drawColor(Color.rgb(255, 255, 255));
        canvas.drawBitmap(bitmap, 0, 0, null);

        //Draw before current step
        for (int i = 0; i < textPos; i++) {
            canvas.drawLine((float) MultiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY() * maxY, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocY() * maxY, myPaint);
            canvas.drawCircle((float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocY() * maxY, 20, myPaint);
        }
        //Draw after current setp
        myPaint.setColor(after);
        myPaint.setStrokeWidth(40);
        for (int i = textPos + 1; i < MultiMapFinalDir.get(mapPos).size(); i++) {
            canvas.drawLine((float) MultiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY() * maxY, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocY() * maxY, myPaint);
            canvas.drawCircle((float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocY() * maxY, 20, myPaint);
        }
        //Draw current step
        if (textPos < MultiMapFinalDir.get(mapPos).size()) {
            myPaint.setColor(current);
            myPaint.setStrokeWidth(60);
            canvas.drawLine((float) MultiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocY() * maxY, (float) MultiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocY() * maxY, myPaint);
            canvas.drawCircle((float) MultiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocY() * maxY, 50, myPaint);
            canvas.drawCircle((float) MultiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocY() * maxY, 30, myPaint);
        }
        //Draw to ImageView
        imageView.setImageDrawable(new BitmapDrawable(getResources(), drawBitmap));

    }

    public void previousPath(View view) {
        //Load ImageView
        TouchImageView imageView = (TouchImageView) findViewById(R.id.imageDisplay);

        //Set Previous Text
        TextView direction = (TextView) findViewById(R.id.directions);
        if ((textPos == 0 && mapPos == 0) || textDir == null) {

        } else if (textPos == 0) {
            mapPos--;
            if (MultiMapFinalDir.get(mapPos).size() == 0) {
                mapPos++;
            } else if (direction.getText().equals("You have arrived at your destination")) {
                direction.setText(textDir.get(mapPos).get(textPos));
            } else {

                textPos = MultiMapFinalDir.get(mapPos).size();
                //directionsText.setText(textDir.get(mapPos).get(textPos));
                direction.setText("Enter " + getMapName(mapPos + 2));
            }
            imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), getMap(mapPos + 1), imageView.getWidth(), imageView.getMaxHeight()));
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            if (!direction.getText().equals("You have arrived at your destination")) {
                textPos--;
            }
            direction.setText(textDir.get(mapPos).get(textPos));
        }
        //Load Canvas, Bitmap and other data for drawing
        Bitmap drawBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(drawBitmap);
        float maxX = canvas.getWidth();
        float maxY = canvas.getHeight();
        Paint myPaint = new Paint();
        myPaint.setColor(before);
        myPaint.setStrokeWidth(40);

        //Draw map onto canvas
        canvas.drawColor(Color.rgb(255, 255, 255));
        canvas.drawBitmap(bitmap, 0, 0, null);

        //Draw before current step
        for (int i = 0; i < textPos; i++) {
            canvas.drawLine((float) MultiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY() * maxY, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocY() * maxY, myPaint);
            canvas.drawCircle((float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocY() * maxY, 20, myPaint);
        }
        //Draw after current setp
        myPaint.setColor(after);
        myPaint.setStrokeWidth(40);
        for (int i = textPos + 1; i < MultiMapFinalDir.get(mapPos).size(); i++) {
            canvas.drawLine((float) MultiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY() * maxY, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocY() * maxY, myPaint);
            canvas.drawCircle((float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(i).getDestination().getLocY() * maxY, 20, myPaint);
        }
        //Draw current step
        if (textPos < MultiMapFinalDir.get(mapPos).size()) {
            myPaint.setColor(current);
            myPaint.setStrokeWidth(60);
            canvas.drawLine((float) MultiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocY() * maxY, (float) MultiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocY() * maxY, myPaint);
            canvas.drawCircle((float) MultiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocY() * maxY, 50, myPaint);
            canvas.drawCircle((float) MultiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocX() * maxX, (float) MultiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocY() * maxY, 30, myPaint);
        }
        //Draw to ImageView
        imageView.setImageDrawable(new BitmapDrawable(getResources(), drawBitmap));

    }

    public void fullText(){
        StringBuilder message = new StringBuilder();
        message.append("hey" + "  ");
        message.append("you" + "\n");
        message.append("Whatcha" + "  ");
        message.append("up to?" + "\n");
        Intent intent = new Intent(this, FullText.class);
        startActivity(intent);
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

    private void generateData() {
        Point p1 = new Point("0", 1, "Fountain", 0, 0.5845601750907536, 0.5209838991465665, 1345, 826, 0);
        Point p2 = new Point("1", 1, "Path", 1, 0.6324252357410155, 0.5329244227699498, 1455, 845, 0);
        Point p3 = new Point("2", 1, "Path", 2, 0.6315588998016441, 0.5445507220874546, 1453, 864, 0);
        Point p4 = new Point("3", 1, "GL Entrance", 3, 0.6915526636031035, 0.5561770214049594, 1591, 882, 0);
        Point p5 = new Point("4", 2, "GL Entrance", 4, 0.5301687037355828, 0.8486486486486486, 117, 557, 0);
        Point p6 = new Point("5", 2, "Hallway", 4, 0.5301687037355828, 0.8486486486486486, 117, 557, 0);
        Point p7 = new Point("6", 2, "Hallway", 5, 0.5276639697021863, 0.6675675675675675, 401, 585, 0);
        Point p8 = new Point("7", 2, "Help Desk", 6, 0.6854622138061629, 0.6675675675675675, 365, 743, 0);

        Edge e1 = new Edge(p1, p2);
        Edge e2 = new Edge(p2, p3);
        Edge e3 = new Edge(p3, p4);
        Edge e4 = new Edge(p4, p5);
        Edge e5 = new Edge(p5, p6);
        Edge E6 = new Edge(p6, p7);
        Edge E7 = new Edge(p7, p8);

        pointList.add(p1);
        pointList.add(p2);
        pointList.add(p3);
        pointList.add(p4);
        pointList.add(p5);
        pointList.add(p6);
        pointList.add(p7);
        pointList.add(p8);
    }

    private int getMap(int n) {
        switch (n) {
            case 0:
                return R.drawable.vectorr_logo;
            case 1:
                return R.drawable.campus;
            case 2:
                return R.drawable.gordon_library_2;
        }
        return 0;
    }

    private String getMapName(int n) {
        switch (n) {
            case 0:
                return "Logo";
            case 1:
                return "Campus";
            case 2:
                return "Gordon Library";
        }
        return null;
    }
}
