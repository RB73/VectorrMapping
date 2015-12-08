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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_route);

        //Get extras
        Intent intent = getIntent();
        Bundle locations = intent.getExtras();

        //Extract data
        String startMap = locations.getString("startMap");
        String startPoint = locations.getString("startPoint");
        String endMap = locations.getString("endMap");
        String endPoint = locations.getString("endPoint");

        //Create string to display
        StringBuilder message = new StringBuilder();
        message.append(startMap + "  ");
        message.append(startPoint + "\n");
        message.append(endMap + "  ");
        message.append(endPoint + "\n");

        //Display string
        TextView textView = (TextView) findViewById(R.id.text_box);
        textView.setTextSize(20);
        textView.setText(message);

        TextView direction = (TextView) findViewById(R.id.directions);
        direction.setTextSize(20);

        //Select Map to display
        //Get Map Number
        int img = locations.getInt("mapNum");

        //Set ImageView
        final ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        switch(img) {
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

        //Draw route onto map
        generateData();
        Point one = new Point("1", "first" , 0, 0);
        Point two = new Point("2", "second" , .25, .1);
        Point three = new Point("3", "third" , .1, .25);
        Point four = new Point("4", "fourth" , .5, .5);
        Point five = new Point("5", "fifth" , .75, .85);
        Point six = new Point("6", "sixth" , 1, 1);
        path.add(one);
        path.add(two);
        path.add(three);
        path.add(four);
        path.add(five);
        path.add(six);

        //Check colorblind mode
        if(locations.getBoolean("colorBlind")){
            before = Color.rgb(182, 109, 255);
            after = Color.rgb(0, 146, 146);
        }

        //Load Canvas, ImageView, Bitmap and other data for drawing
        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Bitmap drawBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(drawBitmap);
        float maxX = canvas.getWidth();
        float maxY = canvas.getHeight();
        Paint myPaint = new Paint();
        myPaint.setColor(after);
        myPaint.setStrokeWidth(40);
        //Draw map onto canvas
        canvas.drawBitmap(bitmap, 0, 0, null);

        //Draw route to map
        for(int i = 0; i < path.size() - 1; i++) {
            canvas.drawLine((float) path.get(i).getLocX() * maxX, (float) path.get(i).getLocY() * maxY, (float) path.get(i + 1).getLocX() * maxX, (float) path.get(i + 1).getLocY() * maxY, myPaint);
        }
        //Generate Text Directions
        GenTextDir gentextdir = new GenTextDir();
        ArrayList<Directions> tempDir = gentextdir.genTextDir(path, 2.8);
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
        //Draw current step
        myPaint.setColor(current);
        myPaint.setStrokeWidth(60);
        canvas.drawLine((float) path.get(path.size() - 1).getLocX() * maxX, (float) path.get(path.size() - 1).getLocY() * maxY, (float) path.get(path.size() - 2).getLocX() * maxX, (float) path.get(path.size() - 2).getLocY() * maxY, myPaint);
        canvas.drawCircle((float) path.get(path.size()-1).getLocX() * maxX, (float) path.get(path.size() - 1).getLocY() * maxY, 50, myPaint);
        //Draw to ImageView
        imageView.setImageDrawable(new BitmapDrawable(getResources(), drawBitmap));
    }

    //Button Responses
    public void nextPath(View view) {
        //Load Canvas, ImageView, Bitmap and other data for drawing
        ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        Bitmap drawBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(drawBitmap);
        float maxX = canvas.getWidth();
        float maxY = canvas.getHeight();
        Paint myPaint = new Paint();
        myPaint.setColor(before);
        myPaint.setStrokeWidth(40);
        //Draw map onto canvas
        canvas.drawBitmap(bitmap, 0, 0, null);

        //Set Next Text
        TextView direction = (TextView) findViewById(R.id.directions);
        if(textPos < textDir.get(0).size()-1){
            textPos++;
            direction.setText(textDir.get(0).get(textPos));
        }
        else{
            direction.setText("You have arrived at your destination");
        }
        //Draw before current step
        for(int i = 0; i < textPos; i++) {
            canvas.drawLine((float) path.get(path.size()-1-i).getLocX() * maxX, (float) path.get(path.size()-1-i).getLocY() * maxY, (float) path.get(path.size()-2-i).getLocX() * maxX, (float) path.get(path.size()-2-i).getLocY() * maxY, myPaint);
        }
        //Draw current step
        myPaint.setColor(current);
        myPaint.setStrokeWidth(60);
        canvas.drawLine((float) path.get(path.size() - textPos - 1).getLocX() * maxX, (float) path.get(path.size() - textPos - 1).getLocY() * maxY, (float) path.get(path.size() - textPos - 2).getLocX() * maxX, (float) path.get(path.size() - textPos - 2).getLocY() * maxY, myPaint);
        canvas.drawCircle((float) path.get(path.size() - textPos - 1).getLocX() * maxX, (float) path.get(path.size() - textPos - 1).getLocY() * maxY, 50, myPaint);
        //Draw after current setp
        myPaint.setColor(after);
        myPaint.setStrokeWidth(40);
        for(int i = textPos+1; i < path.size() - 1; i++) {
            canvas.drawLine((float) path.get(path.size()-1-i).getLocX() * maxX, (float) path.get(path.size()-1-i).getLocY() * maxY, (float) path.get(path.size()-2-i).getLocX() * maxX, (float) path.get(path.size()-2-i).getLocY() * maxY, myPaint);
        }
        //Draw to ImageView
        imageView.setImageDrawable(new BitmapDrawable(getResources(), drawBitmap));

    }
    public void previousPath(View view) {
        //Load Canvas, ImageView, Bitmap and other data for drawing
        ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        Bitmap drawBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(drawBitmap);
        float maxX = canvas.getWidth();
        float maxY = canvas.getHeight();
        Paint myPaint = new Paint();
        myPaint.setColor(before);
        myPaint.setStrokeWidth(40);
        //Draw map onto canvas
        canvas.drawBitmap(bitmap, 0, 0, null);

        //Set Previous Text
        TextView direction = (TextView) findViewById(R.id.directions);
        if(textPos > 0){
            if(!direction.getText().toString().equals("You have arrived at your destination")){
                textPos--;
            }
            direction.setText(textDir.get(0).get(textPos));
        }
        else{
            direction.setText(textDir.get(0).get(0));
        }
        //Draw before current step
        for(int i = 0; i < textPos; i++) {
            canvas.drawLine((float) path.get(path.size()-1-i).getLocX() * maxX, (float) path.get(path.size()-1-i).getLocY() * maxY, (float) path.get(path.size()-2-i).getLocX() * maxX, (float) path.get(path.size()-2-i).getLocY() * maxY, myPaint);
        }
        //Draw current step
        myPaint.setColor(current);
        myPaint.setStrokeWidth(60);
        canvas.drawLine((float) path.get(path.size() - textPos - 1).getLocX() * maxX, (float) path.get(path.size() - textPos - 1).getLocY() * maxY, (float) path.get(path.size() - textPos - 2).getLocX() * maxX, (float) path.get(path.size() - textPos - 2).getLocY() * maxY, myPaint);
        canvas.drawCircle((float) path.get(path.size() - textPos - 1).getLocX() * maxX, (float) path.get(path.size() - textPos - 1).getLocY() * maxY, 50, myPaint);
        //Draw after current setp
        myPaint.setColor(after);
        myPaint.setStrokeWidth(40);
        for(int i = textPos+1; i < path.size() - 1; i++) {
            canvas.drawLine((float) path.get(path.size()-1-i).getLocX() * maxX, (float) path.get(path.size()-1-i).getLocY() * maxY, (float) path.get(path.size()-2-i).getLocX() * maxX, (float) path.get(path.size()-2-i).getLocY() * maxY, myPaint);
        }
        //Draw to ImageView
        imageView.setImageDrawable(new BitmapDrawable(getResources(), drawBitmap));

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
