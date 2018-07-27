package com.gitee.qdbp.able.beans;

/**
 * 持续时间
 *
 * @author zhaohuihua
 * @version 170621
 */
public interface Duration {

    long toDay();

    long toHour();

    long toMinute();

    long toSecond();

    long toMillis();

    public abstract class BaseDuration implements Duration {

        protected static final long DAY = 24 * 60 * 60 * 1000;

        protected static final long HOUR = 60 * 60 * 1000;

        protected static final long MINUTE = 60 * 1000;

        protected static final long SECOND = 1000;

        protected static final long MILLIS = 1;

        protected final long unit;

        protected final long value;

        protected BaseDuration(long unit, long value) {
            this.unit = unit;
            this.value = value;
        }

        @Override
        public long toDay() {
            return value * unit / DAY;
        }

        @Override
        public long toHour() {
            return value * unit / HOUR;
        }

        @Override
        public long toMinute() {
            return value * unit / MINUTE;
        }

        @Override
        public long toSecond() {
            return value * unit / SECOND;
        }

        @Override
        public long toMillis() {
            return value * unit / MILLIS;
        }
    }

    public static final class Day extends BaseDuration {

        public Day(long value) {
            super(DAY, value);
        }
    }

    public static final class Hour extends BaseDuration {

        public Hour(long value) {
            super(HOUR, value);
        }
    }

    public static final class Minute extends BaseDuration {

        public Minute(long value) {
            super(MINUTE, value);
        }
    }

    public static final class Second extends BaseDuration {

        public Second(long value) {
            super(SECOND, value);
        }
    }

    public static final class Millis extends BaseDuration {

        public Millis(long value) {
            super(MILLIS, value);
        }
    }

    public static final class None extends BaseDuration {

        public None() {
            super(0, 0);
        }
    }

}
