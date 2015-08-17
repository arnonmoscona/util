/*
 * Copyright (c) 2015. Arnon Moscona
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.moscona.util.monitoring.stats;

import com.moscona.util.ISimpleDescriptiveStatistic;

import java.text.DecimalFormat;

/**
 * Created: Apr 7, 2010 2:02:04 PM
 * By: Arnon Moscona
 * A class for efficient collection of numeric samples made of longs or ints
 * Note: a virtual copy of DoubleSampleAccumulator, except that generics cannot be used effectively here.
 */
public class LongSampleAccumulator implements ISimpleDescriptiveStatistic, Cloneable {
    private long count;
    private long sum;
    private long sumSquares;
    private long min;
    private long max;
    private long first;
    private long last;

    public LongSampleAccumulator() {
        clear();
    }

    @Override
    public void clear() {
        count = 0;
        sum = 0;
        sumSquares = 0;
        min = 0; // a spec
        max = 0; // a spec
        first = 0;
        last = 0;
    }

    public void addSample(long n) {
        count++;
        sum += n;
        sumSquares += n*n;
        if (count==1) {
            // first one
            min = n;
            max = n;
            first = n;
            last = n;
        }
        else {
            // all the rest
            if (n<min) {
                min = n;
            }
            if (n>max) {
                max = n;
            }
            last = n;
        }
    }

    public void addSample(int n) {
        addSample((long)n);
    }

    public long getCount() {
        return count;
    }

    @Override
    public long count() {
        return count;
    }

    public long getSum() {
        return sum;
    }

    @Override
    public double sum() {
        return (double)sum;
    }

    @Override
    public double sumSquares() {
        return sumSquares;
    }

    public long getSumSquares() {
        return sumSquares;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    @Override
    public double max() {
        return max;
    }

    @Override
    public double min() {
        return min;
    }


    @Override
    public double mean() {
        return (count==0)?0.0:((double)sum) /count;
    }

    @Override
    public double average() {
        return mean();
    }

    @Override
    public double first() {
        return first;
    }

    @Override
    public double last() {
        return last;
    }

    @Override
    public double stdev() {
        return (count==0)?0.0:Math.sqrt(variance());
    }

    private double variance() {
        double mean = mean();
        return (count==0)?0.0:((double)sumSquares - 2.0*mean*sum + count*mean*mean)/count;
    }

    public String toString() {
        return getClass().getName()+"(count:"+count+" , mean:"+mean()+" , min:"+min+" , max:"+max+" , sum:"+sum+" , stdev:"+stdev()+")";
    }

    @Override
    public String toString(DecimalFormat format) {
        return "(count:" + format.format(count) + " , mean:" + format.format(mean()) + " , min:" + format.format(min) + " , max:" + format.format(max) + " , sum:" + format.format(sum) + " , stdev:" + format.format(stdev()) + ")";
    }

    @Override
    public double get(ISimpleDescriptiveStatistic.STATISTIC stat) {
        switch(stat) {
            case COUNT:
                return getCount();
            case SUM:
                return getSum();
            case SUM_SQUARES:
                return getSumSquares();
            case MAX:
                return getMax();
            case MIN:
                return getMin();
            case MEAN:
                return average();
            case STDEV:
                return stdev();
            case VARIANCE:
                return variance();
            default:
                return 0.0;

        }
    }

    @Override
    public LongSampleAccumulator clone() throws CloneNotSupportedException {
        return (LongSampleAccumulator)super.clone();
    }
}
