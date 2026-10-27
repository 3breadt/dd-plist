package com.dd.plist.test;

import static org.junit.jupiter.api.Assertions.*;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSArray;
import com.dd.plist.NSDate;
import java.util.Date;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class NSDateTest {
  private static final long EPOCH = 978307200000L;

  private static NSDate binaryRoundTrip(NSDate date) throws Exception {
    return (NSDate) BinaryPropertyListParser.parse(BinaryPropertyListWriter.writeToArray(date));
  }

  @Test
  public void testTimeIntervalConstructor() {
    assertEquals(EPOCH + 1001, new NSDate(1.001).getDate().getTime());
    assertEquals(EPOCH - 1001, new NSDate(-1.001).getDate().getTime());
    assertEquals(EPOCH + 1, new NSDate(0.0006).getDate().getTime());
    assertEquals(EPOCH - 1, new NSDate(-0.0006).getDate().getTime());
    assertEquals(0.0006, new NSDate(0.0006).getTimeIntervalSinceReferenceDate());
  }

  @Test
  public void testTimeIntervalFromDate() {
    assertEquals(1.001, new NSDate(new Date(EPOCH + 1001)).getTimeIntervalSinceReferenceDate());
    assertEquals(-1.001, new NSDate(new Date(EPOCH - 1001)).getTimeIntervalSinceReferenceDate());
  }

  @Test
  public void testNonFiniteTimeIntervals() throws Exception {
    assertEquals(EPOCH, new NSDate(Double.NaN).getDate().getTime());
    assertEquals(Long.MAX_VALUE, new NSDate(Double.POSITIVE_INFINITY).getDate().getTime());
    assertTrue(new NSDate(Double.NEGATIVE_INFINITY).getDate().getTime() < 0);
    assertTrue(new NSDate(1e300).getDate().getTime() > 0);

    for (double value :
        new double[] {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1e300}) {
      assertEquals(
          Double.doubleToRawLongBits(value),
          Double.doubleToRawLongBits(
              binaryRoundTrip(new NSDate(value)).getTimeIntervalSinceReferenceDate()));
    }
  }

  @Test
  public void testBinaryRoundTripIsLossless() throws Exception {
    double[] values = {0.0, -0.0, 0.0006, -0.0006, 1e-9, 123456789.123456789, -987654321.987654};
    for (double value : values) {
      NSDate decoded = binaryRoundTrip(new NSDate(value));
      assertEquals(
          Double.doubleToRawLongBits(value),
          Double.doubleToRawLongBits(decoded.getTimeIntervalSinceReferenceDate()));
      assertEquals(new NSDate(value), decoded);
    }
  }

  @Test
  public void testDateBinaryRoundTripPreservesMilliseconds() throws Exception {
    Random random = new Random(106);
    for (int i = 0; i < 10000; i++) {
      // dates within roughly 1000 years of the reference date
      long millis = EPOCH + (long) ((random.nextDouble() * 2 - 1) * 3.2e13);
      assertEquals(millis, binaryRoundTrip(new NSDate(new Date(millis))).getDate().getTime());
    }
  }

  @Test
  public void testEqualityUsesTimeInterval() {
    NSDate a = new NSDate(0.0006);
    NSDate b = new NSDate(0.0007);
    assertEquals(a.getDate(), b.getDate());
    assertNotEquals(a, b);
    assertTrue(a.compareTo(b) < 0);
    assertTrue(b.compareTo(a) > 0);

    assertEquals(new NSDate(new Date(EPOCH + 1)), new NSDate(0.001));
    assertEquals(new NSDate(new Date(EPOCH + 1)).hashCode(), new NSDate(0.001).hashCode());
    assertEquals(0, new NSDate(new Date(EPOCH + 1)).compareTo(new NSDate(0.001)));

    assertEquals(new NSDate(Double.NaN), new NSDate(Double.NaN));
    assertNotEquals(new NSDate(0.0), new NSDate(-0.0));
    assertNotEquals(0, new NSDate(0.0).compareTo(new NSDate(-0.0)));
  }

  @Test
  public void testBinaryWriterKeepsDatesDifferingBelowOneMillisecond() throws Exception {
    NSArray array = new NSArray(new NSDate(0.0006), new NSDate(0.0007));
    NSArray decoded =
        (NSArray) BinaryPropertyListParser.parse(BinaryPropertyListWriter.writeToArray(array));
    assertEquals(0.0006, ((NSDate) decoded.objectAtIndex(0)).getTimeIntervalSinceReferenceDate());
    assertEquals(0.0007, ((NSDate) decoded.objectAtIndex(1)).getTimeIntervalSinceReferenceDate());
  }

  @Test
  public void testModifiedDateIsUsed() throws Exception {
    NSDate date = new NSDate(0.0006);
    date.getDate().setTime(EPOCH + 5000);
    assertEquals(5.0, date.getTimeIntervalSinceReferenceDate());
    assertEquals(new NSDate(5.0), date);
    assertEquals(new NSDate(5.0).hashCode(), date.hashCode());
    assertEquals(5.0, binaryRoundTrip(date).getTimeIntervalSinceReferenceDate());
  }

  @Test
  public void testCloneKeepsTimeInterval() {
    NSDate date = new NSDate(0.0006);
    NSDate clone = date.clone();
    assertEquals(date, clone);
    assertNotSame(date.getDate(), clone.getDate());
    assertEquals(0.0006, clone.getTimeIntervalSinceReferenceDate());
  }
}
