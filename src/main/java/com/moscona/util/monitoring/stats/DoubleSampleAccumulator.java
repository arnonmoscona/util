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
 * A class for efficient collection of numeric samples made of longs or ints.
 * Note: a virtual copy of LongSampleAccumulator, except that generics cannot be used effectively here.
 */
public class DoubleSampleAccumulator implements ISimpleDescriptiveStatistic {
    private long count;
    private double sum;
    private double sumSquares;
    private double min;
    private double max;
    private double first; // useful in OLHC summaries
    private double last; // useful in OLHC summaries

    public DoubleSampleAccumulator() {
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

    public void addSample(double n) {
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

    public void addSample(float n) {
        addSample((double)n);
    }

    public long getCount() {
        return count;
    }

    @Override
    public long count() {
        return count;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
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

    public double getSum() {
        return sum;
    }

    @Override
    public double sum() {
        return getSum();
    }

    @Override
    public double sumSquares() {
        return sumSquares;
    }

    public double getSumSquares() {
        return sumSquares;
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
    public double stdev() {
        return (count==0)?0.0:Math.sqrt(variance());
    }

    public double variance() {
        double mean = mean();
        // FIXME IT-681 this calculation should be changed from the sum squares method (which is easy but can be very inaccurate) to Welford's method
        return Math.max(
                (count==0)?0.0:((double)sumSquares - 2.0*mean*sum + count*mean*mean)/count,
                0.0);
    }

    @Override
    public double first() {
        return first;
    }

    @Override
    public double last() {
        return last;
    }

    public double getSharpeRatio() {
        return count <= 1 ? 0 : mean()/stdev();
    }

    public double getSharpeRatio(double defaultIfNan) {
        double ratio = getSharpeRatio();
        if(Double.isNaN(ratio) || Double.isInfinite(ratio)) {
            return defaultIfNan;
        }
        return ratio;
    }

    public double getModeratedSharpeRatio(double moderator) {
        return count <= 1 ? 0 : mean() / (moderator+stdev());
    }

    public double getModeratedSharpeRatio(double moderator, double defaultIfNan) {
        double ratio = getModeratedSharpeRatio(moderator);
        if (Double.isNaN(ratio) || Double.isInfinite(ratio)) {
            return defaultIfNan;
        }
        return ratio;
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
}
