package com.tugalsan.api.stream.client;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class TGS_StreamUtils {

    public static Consumer<Object> doNothing() {
        Consumer<Object> NOOP = whatever -> {
        };
        return NOOP;
    }

    public static <T> List<T> toList(Stream<T> map) {
        return map.collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<Integer> toList(IntStream map) {
        return map.boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<Long> toList(LongStream map) {
        return map.boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<Double> toList(DoubleStream map) {
        return map.boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    public static <T> Stream<T> of(Iterable<T> iterable) {
        return TGS_StreamUtils.of(iterable.spliterator());
    }

    public static <T> Stream<T> of(Spliterator<T> spliterator) {
        return StreamSupport.stream(spliterator, false);
    }

    @FunctionalInterface
    public static interface FunctionWithException<T, R, E extends Exception> {

        R apply(T t) throws E;
    }

    public static <T, R, E extends Exception> Function<T, R> thr1(FunctionWithException<T, R, E> fe) {//USE IT FOR CATCING EXCP IN STREAMS
        return arg -> {
            try {
                return fe.apply(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static IntStream iterate(int from0, int to10_notEnclosed) {
        return iterate(from0, to10_notEnclosed, 1);
    }

    public static IntStream iterate(int from0, int to10_notEnclosed, int by) {
        if (from0 <= to10_notEnclosed) {
            if (by < 0) {
                by = -by;
            }
            return forward(from0, to10_notEnclosed, by);
        } else {
            if (by > 0) {
                by = -by;
            }
            return reverse(from0, to10_notEnclosed, by);
        }
    }

    public static IntStream countDownTo0(int from) {
        return reverse(0, from + 1);
    }

    public static IntStream forward(int from0, int to10_notEnclosed, int by) {
        if (to10_notEnclosed <= from0) {
            to10_notEnclosed = from0;
        }
        return IntStream.iterate(from0, i -> i + by).limit(to10_notEnclosed - from0);
    }

    public static IntStream forward(int from0, int to10_notEnclosed) {
        return forward(from0, to10_notEnclosed, 1);
    }

    public static IntStream reverse(int from0, int to10_notEnclosed, int by) {
        if (to10_notEnclosed <= from0) {
            to10_notEnclosed = from0;
        }
        return IntStream.iterate(to10_notEnclosed - 1, i -> i - by).limit(to10_notEnclosed - from0);
    }

    public static IntStream reverse(int from0, int to10_notEnclosed) {
//        System.out.println("reverse from/to:" + from + "/" + to);
        return reverse(from0, to10_notEnclosed, 1);
    }

    public static <T> Stream<T> of(Enumeration<T> enumeration) {
        return of(new Iterator<>() {//FIX for GWT: as in enumeration.asIterator() 
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return enumeration.nextElement();
            }
        });
    }

    public static <T> Stream<T> of(Iterator<T> sourceIterator) {
        return of(sourceIterator, false);
    }

    public static <T> Stream<T> of(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    public static Stream<Boolean> of(boolean[] array) {
        Stream.Builder<Boolean> builder = Stream.builder();
        IntStream.range(0, array.length).forEachOrdered(i -> builder.add(array[i]));
        return builder.build();
    }

    public static class Options {

        public void stop() {
            isStop = true;
        }
        private boolean isStop = false;

        boolean isStop() {
            return isStop;
        }

        //-----------------------------------
        public void setOffet(int newValue) {
            value = newValue;
        }
        private int value = 0;

        int getOffset() {
            return value;
        }

        public void incOffset() {
            value++;
        }

        public void decOffset() {
            value--;
        }
    }

    public static void forEachOptions(IntStream stream, BiConsumer<Integer, Options> consumer) {
        Spliterator<Integer> s = stream.spliterator();
        var hadNext = true;
        var b = new Options();
        while (hadNext && !b.isStop()) {
            hadNext = s.tryAdvance(i -> consumer.accept(i, b));
        }
    }

    public static <T> void forEachOptions(Stream<T> stream, BiConsumer<T, Options> consumer) {
        Spliterator<T> s = stream.spliterator();
        var hadNext = true;
        var b = new Options();
        while (hadNext && !b.isStop()) {
            hadNext = s.tryAdvance(o -> consumer.accept(o, b));
        }
    }
}
