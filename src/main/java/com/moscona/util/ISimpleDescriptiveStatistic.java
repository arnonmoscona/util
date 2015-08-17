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
