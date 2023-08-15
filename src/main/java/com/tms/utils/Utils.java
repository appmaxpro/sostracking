package com.tms.utils;

import com.tms.utils.primitive.PrimitiveLongCollection;
import com.tms.utils.primitive.PrimitiveLongIterator;

import java.time.LocalDate;
import java.util.function.Predicate;

public class Utils {

    private Utils(){}

    public static <V> V findOne(Iterable<V> iters, Predicate<V> predicate) {
        if (iters != null) {
            synchronized (iters) {
                for (var v : iters) {
                    if (predicate.test(v))
                        return v;
                }
            }
        }
        return null;
    }

    public static <V> boolean anyMatches(Iterable<V> iters, Predicate<V> predicate) {
        if (iters != null) {
            synchronized (iters) {
                for (var v : iters) {
                    if (predicate.test(v))
                        return true;
                }
            }
        }
        return false;
    }

    public static boolean isAfter(LocalDate date1, LocalDate date2) {
        return date1 == null || date2 == null || date1.compareTo(date2) >= 0;
    }

    public static boolean acceptDistance(Double d1, Double d2) {
        return d1 == null || d2 == null || d1.compareTo(d2) >= 0;
    }

    public static long[] toLongArray(PrimitiveLongCollection set) {
        long[] result = new long[set.size()];
        PrimitiveLongIterator iter = set.iterator();
        int index = 0;
        while (iter.hasNext()) {
            result[index++] = iter.next();
        }
        return result;
    }

}
