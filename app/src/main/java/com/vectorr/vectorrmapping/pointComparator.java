package com.vectorr.vectorrmapping;

import java.util.Comparator;

/**
 * Created by Alexi on 12/13/2015.
 */
public class pointComparator implements Comparator<Point>{
    @Override
    public int compare (Point p1, Point p2)
    {
        if (p1.getIndex() > p2.getIndex() )
            return 1;
        else if (p2.getIndex() < p2.getIndex())
            return -1;
        return 0;
    }
}
