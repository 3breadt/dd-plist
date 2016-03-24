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

    public String getaString() {
        return aString;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }

    public Integer getaInteger() {
        return aInteger;
    }

    public void setaInteger(Integer aInteger) {
        this.aInteger = aInteger;
    }

    public Long getaLong() {
        return aLong;
    }

    public void setaLong(Long aLong) {
        this.aLong = aLong;
    }

    public Double getaDouble() {
        return aDouble;
    }

    public void setaDouble(Double aDouble) {
        this.aDouble = aDouble;
    }

    public Float getaFloat() {
        return aFloat;
    }

    public void setaFloat(Float aFloat) {
        this.aFloat = aFloat;
    }

    public Short getaShort() {
        return aShort;
    }

    public void setaShort(Short aShort) {
        this.aShort = aShort;
    }

    public int getaPrimitiveInteger() {
        return aPrimitiveInteger;
    }

    public void setaPrimitiveInteger(int aPrimitiveInteger) {
        this.aPrimitiveInteger = aPrimitiveInteger;
    }

    public long getaPrimitiveLong() {
        return aPrimitiveLong;
    }

    public void setaPrimitiveLong(long aPrimitiveLong) {
        this.aPrimitiveLong = aPrimitiveLong;
    }

    public double getaPrimitiveDouble() {
        return aPrimitiveDouble;
    }

    public void setaPrimitiveDouble(double aPrimitiveDouble) {
        this.aPrimitiveDouble = aPrimitiveDouble;
    }

    public float getaPrimitiveFloat() {
        return aPrimitiveFloat;
    }

    public void setaPrimitiveFloat(float aPrimitiveFloat) {
        this.aPrimitiveFloat = aPrimitiveFloat;
    }

    public short getaPrimitiveShort() {
        return aPrimitiveShort;
    }

    public void setaPrimitiveShort(short aPrimitiveShort) {
        this.aPrimitiveShort = aPrimitiveShort;
    }

    public Boolean getaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public boolean isaPrimitiveBoolean() {
        return aPrimitiveBoolean;
    }

    public void setaPrimitiveBoolean(boolean aPrimitiveBoolean) {
        this.aPrimitiveBoolean = aPrimitiveBoolean;
    }

    public Date getaDate() {
        return aDate;
    }

    public void setaDate(Date aDate) {
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
                "aString='" + aString + '\'' +
                ", aInteger=" + aInteger +
                ", aLong=" + aLong +
                ", aDouble=" + aDouble +
                ", aFloat=" + aFloat +
                ", aShort=" + aShort +
                ", aPrimitiveInteger=" + aPrimitiveInteger +
                ", aPrimitiveLong=" + aPrimitiveLong +
                ", aPrimitiveDouble=" + aPrimitiveDouble +
                ", aPrimitiveFloat=" + aPrimitiveFloat +
                ", aPrimitiveShort=" + aPrimitiveShort +
                ", aBoolean=" + aBoolean +
                ", aPrimitiveBoolean=" + aPrimitiveBoolean +
                ", aDate=" + aDate +
                '}';
    }
}