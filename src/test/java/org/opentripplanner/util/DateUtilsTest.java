package org.opentripplanner.util;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import static org.junit.Assert.*;

public class DateUtilsTest {
    @Test
    public final void testToDate() {
        Date date = DateUtils.toDate("1970-01-01", "00:00", TimeZone.getTimeZone("UTC"));
        assertEquals(0, date.getTime());

        date = DateUtils.toDate(null, "00:00", TimeZone.getTimeZone("UTC"));
        assertEquals(0, date.getTime() % DateUtils.ONE_DAY_MILLI);
    }


    @Test
    public final void testParseTime() {

        /**
         * Test valid hh:mm:ss inputs
         */
        assertArrayEquals(new int[]{1, 2, 3}, DateUtils.parseTime("1:2:3"));
        assertArrayEquals(new int[]{1, 2, 3}, DateUtils.parseTime("1:02:03"));
        assertArrayEquals(new int[]{1, 2, 3}, DateUtils.parseTime("01:02:03"));
        assertArrayEquals(new int[]{1, 2, 3}, DateUtils.parseTime("01:02:03am"));
        assertArrayEquals(new int[]{1, 2, 3}, DateUtils.parseTime("01:02:03 am"));
        assertArrayEquals(new int[]{1, 2, 3}, DateUtils.parseTime("01:02:03  am"));
        assertArrayEquals(new int[]{1, 2, 3}, DateUtils.parseTime("01:02:03AM"));
        assertArrayEquals(new int[]{1, 2, 3}, DateUtils.parseTime("01:02:03 AM"));
        assertArrayEquals(new int[]{13, 2, 3}, DateUtils.parseTime("01:02:03PM"));
        assertArrayEquals(new int[]{13, 2, 3}, DateUtils.parseTime("01:02:03 PM"));
        assertArrayEquals(new int[]{13, 2, 0}, DateUtils.parseTime("01:02PM"));


        /**
         * Test valid seconds-from-midnight inputs
         */
        assertArrayEquals(new int[]{0, 0, 1}, DateUtils.parseTime("1"));
        assertArrayEquals(new int[]{0, 1, 0}, DateUtils.parseTime("60"));
        assertArrayEquals(new int[]{1, 1, 1}, DateUtils.parseTime("3661"));

        /**
         * Test invalid inputs
         */
        assertNull(DateUtils.parseTime(""));
        assertNull(DateUtils.parseTime("-1"));
        assertNull(DateUtils.parseTime("86400"));
        assertNull(DateUtils.parseTime("AM"));
        assertNull(DateUtils.parseTime("01PM"));
        assertNull(DateUtils.parseTime("01:PM"));
        assertNull(DateUtils.parseTime("01:02:PM"));
        assertNull(DateUtils.parseTime("01:02:PM"));
    }
}
