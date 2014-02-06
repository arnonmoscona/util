package com.moscona.util;

import java.text.DecimalFormat;

/**
 * Created: Apr 7, 2010 2:28:24 PM
 * By: Arnon Moscona
 */
public interface ISimpleDescriptiveStatistic {
    double first();

    double last();

    double sumSquares();

    String toString(DecimalFormat format);

    public enum STATISTIC {COUNT,MIN,MAX,MEAN,SUM,STDEV,VARIANCE,SUM_SQUARES}

    double get(STATISTIC stat);

    long count();

    double max();

    double min();

    double sum();

    double mean();

    double average();

    double stdev();

    void clear();
}
