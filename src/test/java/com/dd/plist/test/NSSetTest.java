package com.dd.plist.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dd.plist.NSSet;
import com.dd.plist.NSString;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link NSSet} class.
 *
 * @author Daniel Dreibrodt
 */
public class NSSetTest {
  /** Nulls passed to the constructor must be stored the same way {@link NSSet#addObject} does. */
  @Test
  public void init_arrayContainingNull_treatsNullLikeAddObject() {
    NSSet set = new NSSet(new NSString("a"), null);

    assertTrue(set.containsObject(null));
    set.addObject(null);
    assertEquals(2, set.count());
    assertEquals(new NSSet(new NSString("a"), null), set);
  }

  @Test
  public void addObject_null_doesNotThrow() {
    NSSet set = new NSSet();
    assertDoesNotThrow(() -> set.addObject(null));
  }

  @Test
  public void anyObject_onlyObjectIsNull_returnsNull() {
    NSSet set = new NSSet();
    set.addObject(null);

    assertNull(set.anyObject());
  }

  @Test
  public void allObjects_setContainsNullObject_returnsArrayWithNull() {
    NSSet set = new NSSet();
    set.addObject(null);

    assertNull(set.allObjects()[0]);
  }
}
