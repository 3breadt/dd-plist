package com.dd.plist.test;

import com.dd.plist.NSObject;
import com.dd.plist.NSSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NSSetTests {
    @Test
    public void init_arrayContainingNull_doesNotThrow() {
        assertDoesNotThrow(() -> new NSSet(new NSObject[] { null }));
    }

    @Test
    public void init_ordered_arrayContainingNull_doesNotThrow() {
        assertDoesNotThrow(() -> new NSSet(true, new NSObject[] { null }));
    }

    @Test
    public void add_null_doesNotThrow() {
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
