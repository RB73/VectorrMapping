package com.vectorr.vectorrmapping;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayTable extends Activity {

    TextView tvResult;
    Button populateEdges;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        MySQLiteHelper myHelper = new MySQLiteHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_table);

        tvResult = (TextView)findViewById(R.id.tvResult);
        populateEdges = (Button)findViewById(R.id.populateEdgeButton);

        final ArrayList<Map> allMaps = myHelper.getPointlessMaps();

        populateEdges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTextview(allMaps);
            }
        });
    }

    public void updateTextview(ArrayList<Map> allMaps) {
        final ArrayList<Map> maps = allMaps;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < maps.size(); j++) {
                    Map tempMap = maps.get(j);
                    tvResult.append("ID: " + tempMap.getMapId() + "\n");
                    tvResult.append("Name: " + tempMap.getMapName() + "\n\n");
                }
            }
        });
    }
}