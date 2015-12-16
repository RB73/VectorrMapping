package com.vectorr.vectorrmapping;

/**
 * Created by Alexi on 12/12/2015.
 */


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MySQLiteHelper extends SQLiteOpenHelper {
    //------------------------------------------------------------Constants--------------------------------------------------------------------
    private static String MAP_TABLE_NAME = "AddedMaps";
    private static String POINT_TABLE_NAME = "Points";
    private static String EDGE_TABLE_NAME = "WeightedEdges";

    private static String MAP_SCHEMA = "id INTEGER, name VARCHAR(30), xTopLeft DOUBLE, yTopLeft DOUBLE, "
            + " xBotRight DOUBLE, yBotRight DOUBLE, rotation DOUBLE, pointIDIndex INTEGER";
    private static String POINT_SCHEMA = "id VARCHAR(30), mapId INTEGER, name VARCHAR(30), localIndex INTEGER, "
            + "locX DOUBLE, locY DOUBLE, globX INTEGER, globY INTEGER, numEdges INTEGER, isStairs BOOL, isOutside BOOL"
            +" idEdge1 VARCHAR(30),"
            + " idEdge2 VARCHAR(30), idEdge3 VARCHAR(30), idEdge4 VARCHAR(30), idEdge5 VARCHAR(30), idEdge6 VARCHAR(30), idEdge7 VARCHAR(30), idEdge8 VARCHAR(30),"
            + "idEdge9 VARCHAR(30), idEdge10 VARCHAR(30)";
    private static String EDGE_SCHEMA = "id VARCHAR(30), idPoint1 VARCHAR(30), idPoint2 VARCHAR(30), weight INTEGER, isOutside BOOLEAN, isStairs INTEGER";

    private static final String[] MAP_COLUMNS = {"id", "name", "xTopLeft", "yTopLeft",
            "xBotRight", "yBotRight", "rotation", "pointIDIndex"};
    private static final String[] EDGE_COLUMNS = {"id", "idPoint1", "idPoint2", "weight", "isOutside", "isStairs"};

    //Path to the device folder with databases
    public static String DB_PATH;

    //Database file name
    public static String DB_NAME = "mapdatabase.sqlite";
    public SQLiteDatabase database;
    public Context context;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    private static ArrayList<Map> allMaps = new ArrayList<Map>();
    private static ArrayList<Point> allPoints = new ArrayList<Point>();
    private static ArrayList<Edge> allEdges = new ArrayList<Edge>();

    private final boolean DEBUG = false;
    private boolean databaseChanged = true;
    //-------------------------------------------------------Class Functions------------------------------------------------------

    public SQLiteDatabase getDb() {
        return database;
    }

    public MySQLiteHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        //Write a full path to the databases of your application
        String packageName = context.getPackageName();
        DB_PATH = String.format("//data//data//%s//databases//", packageName);
        //createDataBase();
        openDataBase();
    }

    //This piece of code will create a database if it’s not yet created
    public void createDataBase() {
        System.out.println("==========Creating Database========");
        System.out.println("Clearing old db");
        context.deleteDatabase(DB_NAME);
        boolean dbExist = false;//checkDataBase();
        if (!dbExist) {
            System.out.println("Getting readable db");
            this.getReadableDatabase();
            System.out.println("Got readable db");
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Copying error");
                throw new Error("Error copying database!");
            }
        } else {
            Log.i(this.getClass().toString(), "Database already exists");
        }
    }

    //Performing a database existence check
    private boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        try {
            String path = DB_PATH + DB_NAME;
            checkDb = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            Log.e(this.getClass().toString(), "Error while checking db");
        }
        //Android doesn’t like resource leaks, everything should
        // be closed
        if (checkDb != null) {
            checkDb.close();
        }
        return checkDb != null;
    }

    //Method for copying the database
    private void copyDataBase() throws IOException {
        System.out.println("=======Copying Database======");
        //Open a stream for reading from our ready-made database
        //The stream source is located in the assets
        InputStream externalDbStream = context.getAssets().open(DB_NAME);

        //Path to the created empty database on your Android device
        String outFileName = DB_PATH + DB_NAME;

        //Now create a stream for writing the database byte by byte
        OutputStream localDbStream = new FileOutputStream(outFileName);

        //Copying the database
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = externalDbStream.read(buffer)) > 0) {
            localDbStream.write(buffer, 0, bytesRead);
        }
        //Don’t forget to close the streams
        localDbStream.close();
        externalDbStream.close();
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        String path = DB_PATH + DB_NAME;
        if (database == null) {
            createDataBase();
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return database;
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // create fresh tables
        //  this.onCreate(db);
    }

    //----------------------------------------------------Retrieval Functions----------------------------------------------------
    public ArrayList<Map> getPointlessMaps(){

        ArrayList<Map> retMaps = new ArrayList<Map>();

        // 1. get reference to readable DB
        SQLiteDatabase db = openDataBase();

        // 2. build query
        Cursor cursor =
                db.query(MAP_TABLE_NAME,                                // a. table
                        MAP_COLUMNS,                                    // b. column names
                        null,                                           // c. selections
                        null,                                           // d. selections args
                        null,                                           // e. group by
                        null,                                           // f. having
                        null,                                           // g. order by
                        null);                                          // h. limit

        if (cursor != null)
            cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Map newMap = new Map(Integer.parseInt(cursor.getString(0)),    //ID
                    cursor.getString(1),                                    //Name
                    Double.parseDouble(cursor.getString(2)),                //xTopLeft
                    Double.parseDouble(cursor.getString(3)),                //yTopLeft
                    Double.parseDouble(cursor.getString(4)),                //xBotRight
                    Double.parseDouble(cursor.getString(5)),                //yBotRight
                    Double.parseDouble(cursor.getString(6)),                //roatation
                    Integer.parseInt(cursor.getString(7)));                 //pointIDIndex

            Log.d("Found map:", newMap.getMapName());
            retMaps.add(newMap);
            cursor.moveToNext();
        }

        return retMaps;
    }

    public ArrayList<com.vectorr.vectorrmapping.Map> getMaps()
    {
        System.out.println("==========Populating From Database==========");
        try {
            populateFromDatabase();
        } catch (PopulateErrorException e1) {
            e1.printStackTrace();
        } catch (java.sql.SQLException e1) {
            e1.printStackTrace();
        }
        System.out.println("==========Finished populating==========");
        int counter = 0;
        System.out.println("==========Adding points to all maps==========");
        for (counter = 0; counter< allMaps.size(); counter++)
        {
            com.vectorr.vectorrmapping.Map tempMap = allMaps.get(counter);
            try {
                tempMap.setPointList(getPointsFromServer(tempMap));
            } catch (PopulateErrorException e) {
                System.out.println("Failed to get points for map:"+allMaps.get(counter).getMapId() + ", Map Name: " + allMaps.get(counter).getMapName());
                e.printStackTrace();
            }
            allMaps.set(counter, tempMap);
        }
        return allMaps;
    }

    public ArrayList<Point> getPointsFromServer(com.vectorr.vectorrmapping.Map map) throws PopulateErrorException
    {
        SQLiteDatabase db = openDataBase();
        if (DEBUG)
            System.out.println("getPointsFromServer called");
        ArrayList<Point> retArray = new ArrayList<Point>();
        String TABLE_NAME = "";
        TABLE_NAME += ("Map"+map.getMapId()+"Points");

        String newPtId;
        int newPtMapId;
        String newPtName;
        int newPtIndex;
        Double newPtLocX;
        Double newPtLocY;
        int newPtGlobX;
        int newPtGlobY;
        int newPtNumberEdges;
        boolean newPtIsStairs;
        boolean newPtIsOutside;
        ArrayList<Edge> newPtEdges = new ArrayList<Edge>();

        Cursor pointCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        pointCursor.moveToFirst();
        while (!pointCursor.isAfterLast())
        {
            newPtId = pointCursor.getString(0);
            newPtMapId = pointCursor.getInt(1);
            newPtName = pointCursor.getString(2);
            newPtIndex = pointCursor.getInt(3);
            newPtLocX = pointCursor.getDouble(4);
            newPtLocY = pointCursor.getDouble(5);
            newPtGlobX = pointCursor.getInt(6);
            newPtGlobY = pointCursor.getInt(7);
            newPtNumberEdges = pointCursor.getInt(8);
            newPtIsStairs = (pointCursor.getInt(9)>0);
            newPtIsOutside = (pointCursor.getInt(10)>0);
            newPtEdges = new ArrayList<Edge>();

            String edgeSelect = "idEdge";
            String edgeId= "";
            int counter = 0;
            int edgeCounter = 0;
            for (counter = 0; counter<newPtNumberEdges; counter++)
            {
                edgeSelect = ("idEdge"+String.valueOf(counter+1));
                edgeId = pointCursor.getString(counter+1+10);            //+1 because indexed from zero, +8 because of previous calls
                boolean foundEdge = false;
                if (DEBUG)
                    System.out.println("Searching for edge:"+edgeId);
                for (edgeCounter = 0; edgeCounter<allEdges.size(); edgeCounter++)
                {
                    if (allEdges.get(edgeCounter).getID().contentEquals(edgeId))
                    {
                        foundEdge = true;
                        newPtEdges.add(allEdges.get(edgeCounter));
                        if (DEBUG)
                            System.out.println("found:"+allEdges.get(edgeCounter).getId());
                    }
                }

                if (foundEdge == false)
                {
                    throw new PopulateErrorException("Couldn't find edgeId:"+edgeId+" in allEdges");
                }
            }

            Point newPt = new Point(newPtId, newPtMapId, newPtName, newPtIndex,
                    newPtLocX, newPtLocY, newPtGlobX, newPtGlobY, newPtNumberEdges, newPtIsStairs, newPtIsOutside);
            newPt.setEdges(newPtEdges);
            retArray.add(newPt);
            pointCursor.moveToNext();
        }
        pointCursor.close();
        if (DEBUG)
            System.out.println("Finished getting points");
        return retArray;
    }

    private static Point getPointFromLocal(String pointId) throws DoesNotExistException
    {
        int counter = 0;
        Point retPt = null;
        boolean found = false;
        for (counter = 0; counter<allPoints.size(); counter++)
        {
            //System.out.println("Comparing point "+pointId+" to "+allPoints.get(counter).getId());
            if (allPoints.get(counter).getId().contentEquals(pointId))
            {
                retPt = allPoints.get(counter);
                found = true;
                break;
            }
        }

        if (found)
        {
            return retPt;
        }
        else
        {
            System.out.println("Failed to get point:" + pointId);
            System.exit(0);
            throw new DoesNotExistException("Point does not exist in object database");
        }
    }

    private void populateFromDatabase() throws PopulateErrorException, java.sql.SQLException {
        if (databaseChanged) {
            SQLiteDatabase db = openDataBase();

            allMaps.clear();
            allPoints.clear();
            allEdges.clear();

            String newPtId;
            int newPtMapId;
            String newPtName;
            int newPtIndex;
            Double newPtLocX;
            Double newPtLocY;
            int newPtGlobX;
            int newPtGlobY;
            int newPtNumberEdges;
            boolean newPtIsStairs;
            boolean newPtIsOutside;

            String newEdgeId;
            String newEdgePt1;
            String newEdgePt2;
            int newEdgeWeight;
            int newEdgeOutside;
            int newEdgeStairs;

            Cursor tableCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);

            System.out.println("Got tableNames");
            ArrayList<String> tableNames = new ArrayList<String>();
            tableCursor.moveToFirst();
            while (!tableCursor.isAfterLast()) {
                System.out.println("Adding tableName:" + tableCursor.getString(0) + " to tableName Array");
                if (tableCursor.getString(0).toLowerCase().contains("Points".toLowerCase()) ||
                        tableCursor.getString(0).toLowerCase().contains("Edges".toLowerCase()) ||
                        tableCursor.getString(0).toLowerCase().contentEquals(MAP_TABLE_NAME.toLowerCase()))
                    tableNames.add(tableCursor.getString(0));
                tableCursor.moveToNext();
            }
            System.out.println("Added " + tableNames.size() + " table names to tableNames ArrayList");
            tableCursor.close();

            pointComparator pc = new pointComparator();

            Collections.sort(tableNames);

            for (int z = 0; z < tableNames.size(); z++) {
                System.out.println("Sorted tableName " + z + ": " + tableNames.get(z));
            }
            int nameCounter = 0;
            for (nameCounter = 0; nameCounter < tableNames.size(); nameCounter++) {
                //----------------------------------------Populate Map--------------------------------------------
                if (tableNames.get(nameCounter).toLowerCase().contentEquals(MAP_TABLE_NAME.toLowerCase())) {
                    System.out.println("Populating from table: " + tableNames.get(nameCounter) + " which is at row: " + nameCounter);

                    Cursor mapCursor = db.rawQuery("SELECT * FROM " + tableNames.get(nameCounter), null);
                    mapCursor.moveToFirst();
                    while (!mapCursor.isAfterLast()) {
                        int newMapId = mapCursor.getInt(0);                                 //id
                        String newMapName = mapCursor.getString(1);                         //name
                        double newXTopLeft = mapCursor.getDouble(2);                        //xTopLeft
                        double newYTopLeft = mapCursor.getDouble(3);                        //yTopLeft
                        double newXBotRight = mapCursor.getDouble(4);                       //xBotRight
                        double newYBotRight = mapCursor.getDouble(5);                       //yBotRight
                        double newRotationAngle = mapCursor.getDouble(6);                   //rotation
                        int newPointIDIndex = mapCursor.getInt(7);                          //pointIDIndex
                        com.vectorr.vectorrmapping.Map newMap =
                                new com.vectorr.vectorrmapping.Map(newMapId,
                                        newMapName, newXTopLeft, newYTopLeft, newXBotRight, newYBotRight,
                                        newRotationAngle, newPointIDIndex);
                        allMaps.add(newMap);
                        mapCursor.moveToNext();
                    }
                    mapCursor.close();
                }
                //------------------------------------------Populate Points-------------------------------------------
                else if (tableNames.get(nameCounter).toLowerCase().contains("Points".toLowerCase())) {
                    String tableName = tableNames.get(nameCounter);
                    System.out.println("Populating from table: " + tableNames.get(nameCounter) + " which is at row: " + nameCounter);
                    int mapID = Integer.parseInt(tableName.substring(3, 4));                                                            //Gets the mapId
                    int j = 0;
                    com.vectorr.vectorrmapping.Map currentMap = null;
                    for (j = 0; j < allMaps.size(); j++) {
                        if (allMaps.get(j).getMapId() == mapID) {
                            currentMap = allMaps.get(j);
                        }
                    }
                    if (currentMap == null) {
                        throw new PopulateErrorException("Couldn't find map object to add point to");
                    }

                    Cursor pointCursor = db.rawQuery("SELECT * FROM " + tableNames.get(nameCounter), null);
                    pointCursor.moveToFirst();
                    while (!pointCursor.isAfterLast()) {
                        newPtId = pointCursor.getString(0);
                        newPtMapId = pointCursor.getInt(1);
                        newPtName = pointCursor.getString(2);
                        newPtIndex = pointCursor.getInt(3);
                        newPtLocX = pointCursor.getDouble(4);
                        newPtLocY = pointCursor.getDouble(5);
                        newPtGlobX = pointCursor.getInt(6);
                        newPtGlobY = pointCursor.getInt(7);
                        newPtNumberEdges = 0;                                                            //This should be automatically rectified when adding in edges
                        newPtIsStairs = (pointCursor.getInt(9) > 0);
                        newPtIsOutside = (pointCursor.getInt(10) > 0);
                        Point newPt = new Point(newPtId, newPtMapId, newPtName, newPtIndex, newPtLocX,
                                newPtLocY, newPtGlobX, newPtGlobY, newPtNumberEdges, newPtIsStairs, newPtIsOutside);
                        currentMap.addPoint(newPt);
                        //System.out.println("Adding point: " + newPt.getId() + " to map: " + newPt.getMapId());
                        allPoints.add(newPt);
                        pointCursor.moveToNext();
                    }
                    pointCursor.close();
                }
                //------------------------------------------Populate Edges-------------------------------------------
                else if (tableNames.get(nameCounter).toLowerCase().contains("Edges".toLowerCase())) {
                    System.out.println("Populating from table: " + tableNames.get(nameCounter) + " which is at row: " + nameCounter);

                    Cursor edgeCursor = db.rawQuery("SELECT * FROM " + tableNames.get(nameCounter), null);
                    edgeCursor.moveToFirst();
                    while (!edgeCursor.isAfterLast()) {
                        newEdgeId = edgeCursor.getString(0);
                        newEdgePt1 = edgeCursor.getString(1);
                        Point pt1 = new Point();
                        Point pt2 = new Point();
                        try {
                            pt1 = getPointFromLocal(newEdgePt1);
                        } catch (DoesNotExistException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        newEdgePt2 = edgeCursor.getString(2);
                        try {
                            pt2 = getPointFromLocal(newEdgePt2);
                        } catch (DoesNotExistException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        newEdgeWeight = edgeCursor.getInt(3);
                        newEdgeOutside = edgeCursor.getInt(4);
                        newEdgeStairs = edgeCursor.getInt(5);
                        boolean out = (newEdgeOutside == 1);
                        boolean stairs = (newEdgeStairs == 1);

                        Edge newEdge = new Edge(pt1, pt2, newEdgeWeight, out, stairs);                        //Automatically adds to points
                        allEdges.add(newEdge);
                        edgeCursor.moveToNext();
                    }
                    edgeCursor.close();
                } else {
                    System.out.println("Couldn't resolve table name");
                    throw new PopulateErrorException("Invalid table type. Can't resolve name:" + tableNames.get(nameCounter));
                }
            }
            databaseChanged = false;
        }
    }
}
