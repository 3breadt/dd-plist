package com.dd.plist.test.model;

import java.sql.Timestamp;
import java.util.Date;

public class TestClass2 {

    private String aString;
    private Integer aInteger;
    private Long aLong;
    private Double aDouble;
    private Float aFloat;
    private Short aShort;
    private int aPrimitiveInteger;
    private long aPrimitiveLong;
    private double aPrimitiveDouble;
    private float aPrimitiveFloat;
    private short aPrimitiveShort;
    private Boolean aBoolean;
    private boolean aPrimitiveBoolean;
    private Date aDate;

    public String getString() {
        return aString;
    }

    public void setString(String aString) {
        this.aString = aString;
    }

    public Integer getInteger() {
        return aInteger;
    }

    public void setInteger(Integer aInteger) {
        this.aInteger = aInteger;
    }

    public Long getLong() {
        return aLong;
    }

    public void setLong(Long aLong) {
        this.aLong = aLong;
    }

    public Double getDouble() {
        return aDouble;
    }

    public void setDouble(Double aDouble) {
        this.aDouble = aDouble;
    }

    public Float getFloat() {
        return aFloat;
    }

    public void setFloat(Float aFloat) {
        this.aFloat = aFloat;
    }

    public Short getShort() {
        return aShort;
    }

    public void setShort(Short aShort) {
        this.aShort = aShort;
    }

    public int getPrimitiveInteger() {
        return aPrimitiveInteger;
    }

    public void setPrimitiveInteger(int aPrimitiveInteger) {
        this.aPrimitiveInteger = aPrimitiveInteger;
    }

    public long getPrimitiveLong() {
        return aPrimitiveLong;
    }

    public void setPrimitiveLong(long aPrimitiveLong) {
        this.aPrimitiveLong = aPrimitiveLong;
    }

    public double getPrimitiveDouble() {
        return aPrimitiveDouble;
    }

    public void setPrimitiveDouble(double aPrimitiveDouble) {
        this.aPrimitiveDouble = aPrimitiveDouble;
    }

    public float getPrimitiveFloat() {
        return aPrimitiveFloat;
    }

    public void setPrimitiveFloat(float aPrimitiveFloat) {
        this.aPrimitiveFloat = aPrimitiveFloat;
    }

    public short getPrimitiveShort() {
        return aPrimitiveShort;
    }

    public void setPrimitiveShort(short aPrimitiveShort) {
        this.aPrimitiveShort = aPrimitiveShort;
    }

    public Boolean getBoolean() {
        return aBoolean;
    }

    public void setBoolean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public boolean isPrimitiveBoolean() {
        return aPrimitiveBoolean;
    }

    public void setPrimitiveBoolean(boolean aPrimitiveBoolean) {
        this.aPrimitiveBoolean = aPrimitiveBoolean;
    }

    public Date getDate() {
        return aDate;
    }

    public void setDate(Date aDate) {
        this.aDate = aDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestClass2 that = (TestClass2) o;

        if (aPrimitiveInteger != that.aPrimitiveInteger) return false;
        if (aPrimitiveLong != that.aPrimitiveLong) return false;
        if (Double.compare(that.aPrimitiveDouble, aPrimitiveDouble) != 0) return false;
        if (Float.compare(that.aPrimitiveFloat, aPrimitiveFloat) != 0) return false;
        if (aPrimitiveShort != that.aPrimitiveShort) return false;
        if (aPrimitiveBoolean != that.aPrimitiveBoolean) return false;
        if (aString != null ? !aString.equals(that.aString) : that.aString != null) return false;
        if (aInteger != null ? !aInteger.equals(that.aInteger) : that.aInteger != null) return false;
        if (aLong != null ? !aLong.equals(that.aLong) : that.aLong != null) return false;
        if (aDouble != null ? !aDouble.equals(that.aDouble) : that.aDouble != null) return false;
        if (aFloat != null ? !aFloat.equals(that.aFloat) : that.aFloat != null) return false;
        if (aShort != null ? !aShort.equals(that.aShort) : that.aShort != null) return false;
        if (aBoolean != null ? !aBoolean.equals(that.aBoolean) : that.aBoolean != null) return false;
        return aDate != null ? aDate.equals(that.aDate) : that.aDate == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = aString != null ? aString.hashCode() : 0;
        result = 31 * result + (aInteger != null ? aInteger.hashCode() : 0);
        result = 31 * result + (aLong != null ? aLong.hashCode() : 0);
        result = 31 * result + (aDouble != null ? aDouble.hashCode() : 0);
        result = 31 * result + (aFloat != null ? aFloat.hashCode() : 0);
        result = 31 * result + (aShort != null ? aShort.hashCode() : 0);
        result = 31 * result + aPrimitiveInteger;
        result = 31 * result + (int) (aPrimitiveLong ^ (aPrimitiveLong >>> 32));
        temp = Double.doubleToLongBits(aPrimitiveDouble);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (aPrimitiveFloat != +0.0f ? Float.floatToIntBits(aPrimitiveFloat) : 0);
        result = 31 * result + (int) aPrimitiveShort;
        result = 31 * result + (aBoolean != null ? aBoolean.hashCode() : 0);
        result = 31 * result + (aPrimitiveBoolean ? 1 : 0);
        result = 31 * result + (aDate != null ? aDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestClass2{" +
                "\n String='" + aString + '\'' +
                "\n Integer=" + aInteger +
                "\n Long=" + aLong +
                "\n Double=" + aDouble +
                "\n Float=" + aFloat +
                "\n Short=" + aShort +
                "\n PrimitiveInteger=" + aPrimitiveInteger +
                "\n PrimitiveLong=" + aPrimitiveLong +
                "\n PrimitiveDouble=" + aPrimitiveDouble +
                "\n PrimitiveFloat=" + aPrimitiveFloat +
                "\n PrimitiveShort=" + aPrimitiveShort +
                "\n Boolean=" + aBoolean +
                "\n PrimitiveBoolean=" + aPrimitiveBoolean +
                "\n Date=" + aDate +
                "\n}";
    }
}