package com.vectorr.vectorrmapping;

import java.util.ArrayList;

/**
 * Created by paul on 12/14/2015.
 */
public class database {
    private ArrayList<Map> data;
    public ArrayList<Map> getData() {return data;}
    public void setData(ArrayList<Map> data) {this.data = data;}

    private static final database holder = new database();
    public static database getInstance() {return holder;}
}
