package com.dd.plist.test.model;

import java.util.Arrays;
import java.util.Date;

public class TestClass3 {

    private String[] aStringArray;
    private Integer[] aIntegerArray;
    private Long[] aLongArray;
    private Double[] aDoubleArray;
    private Float[] aFloatArray;
    private Short[] aShortArray;
    private int[] aPrimitiveIntegerArray;
    private long[] aPrimitiveLongArray;
    private double[] aPrimitiveDoubleArray;
    private float[] aPrimitiveFloatArray;
    private short[] aPrimitiveShortArray;
    private Boolean[] aBooleanArray;
    private boolean[] aPrimitiveBooleanArray;
    private Date[] aDateArray;
    private Byte[] aByteArray;
    private byte[] aPrimitiveByteArray;


    public String[] getStringArray() {
        return aStringArray;
    }

    public void setStringArray(String[] aStringArray) {
        this.aStringArray = aStringArray;
    }

    public Integer[] getIntegerArray() {
        return aIntegerArray;
    }

    public void setIntegerArray(Integer[] aIntegerArray) {
        this.aIntegerArray = aIntegerArray;
    }

    public Long[] getLongArray() {
        return aLongArray;
    }

    public void setLongArray(Long[] aLongArray) {
        this.aLongArray = aLongArray;
    }

    public Double[] getDoubleArray() {
        return aDoubleArray;
    }

    public void setDoubleArray(Double[] aDoubleArray) {
        this.aDoubleArray = aDoubleArray;
    }

    public Float[] getFloatArray() {
        return aFloatArray;
    }

    public void setFloatArray(Float[] aFloatArray) {
        this.aFloatArray = aFloatArray;
    }

    public Short[] getShortArray() {
        return aShortArray;
    }

    public void setShortArray(Short[] aShortArray) {
        this.aShortArray = aShortArray;
    }

    public int[] getPrimitiveIntegerArray() {
        return aPrimitiveIntegerArray;
    }

    public void setPrimitiveIntegerArray(int[] aPrimitiveIntegerArray) {
        this.aPrimitiveIntegerArray = aPrimitiveIntegerArray;
    }

    public long[] getPrimitiveLongArray() {
        return aPrimitiveLongArray;
    }

    public void setPrimitiveLongArray(long[] aPrimitiveLongArray) {
        this.aPrimitiveLongArray = aPrimitiveLongArray;
    }

    public double[] getPrimitiveDoubleArray() {
        return aPrimitiveDoubleArray;
    }

    public void setPrimitiveDoubleArray(double[] aPrimitiveDoubleArray) {
        this.aPrimitiveDoubleArray = aPrimitiveDoubleArray;
    }

    public float[] getPrimitiveFloatArray() {
        return aPrimitiveFloatArray;
    }

    public void setPrimitiveFloatArray(float[] aPrimitiveFloatArray) {
        this.aPrimitiveFloatArray = aPrimitiveFloatArray;
    }

    public short[] getPrimitiveShortArray() {
        return aPrimitiveShortArray;
    }

    public void setPrimitiveShortArray(short[] aPrimitiveShortArray) {
        this.aPrimitiveShortArray = aPrimitiveShortArray;
    }

    public Boolean[] getBooleanArray() {
        return aBooleanArray;
    }

    public void setBooleanArray(Boolean[] aBooleanArray) {
        this.aBooleanArray = aBooleanArray;
    }

    public boolean[] getPrimitiveBooleanArray() {
        return aPrimitiveBooleanArray;
    }

    public void setPrimitiveBooleanArray(boolean[] aPrimitiveBooleanArray) {
        this.aPrimitiveBooleanArray = aPrimitiveBooleanArray;
    }

    public Date[] getDateArray() {
        return aDateArray;
    }

    public void setDateArray(Date[] aDateArray) {
        this.aDateArray = aDateArray;
    }

    public Byte[] getByteArray() {
        return aByteArray;
    }

    public void setByteArray(Byte[] aByteArray) {
        this.aByteArray = aByteArray;
    }

    public byte[] getPrimitiveByteArray() {
        return aPrimitiveByteArray;
    }

    public void setPrimitiveByteArray(byte[] aPrimitiveByteArray) {
        this.aPrimitiveByteArray = aPrimitiveByteArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestClass3 that = (TestClass3) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aStringArray, that.aStringArray)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aIntegerArray, that.aIntegerArray)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aLongArray, that.aLongArray)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aDoubleArray, that.aDoubleArray)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aFloatArray, that.aFloatArray)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aShortArray, that.aShortArray)) return false;
        if (!Arrays.equals(aPrimitiveIntegerArray, that.aPrimitiveIntegerArray)) return false;
        if (!Arrays.equals(aPrimitiveLongArray, that.aPrimitiveLongArray)) return false;
        if (!Arrays.equals(aPrimitiveDoubleArray, that.aPrimitiveDoubleArray)) return false;
        if (!Arrays.equals(aPrimitiveFloatArray, that.aPrimitiveFloatArray)) return false;
        if (!Arrays.equals(aPrimitiveShortArray, that.aPrimitiveShortArray)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aBooleanArray, that.aBooleanArray)) return false;
        if (!Arrays.equals(aPrimitiveBooleanArray, that.aPrimitiveBooleanArray)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aDateArray, that.aDateArray)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aByteArray, that.aByteArray)) return false;
        return Arrays.equals(aPrimitiveByteArray, that.aPrimitiveByteArray);

    }

    @Override
    public String toString() {
        return "TestClass3{" +
                "\n StringArray=" + Arrays.toString(aStringArray) +
                "\n IntegerArray=" + Arrays.toString(aIntegerArray) +
                "\n LongArray=" + Arrays.toString(aLongArray) +
                "\n DoubleArray=" + Arrays.toString(aDoubleArray) +
                "\n FloatArray=" + Arrays.toString(aFloatArray) +
                "\n ShortArray=" + Arrays.toString(aShortArray) +
                "\n PrimitiveIntegerArray=" + Arrays.toString(aPrimitiveIntegerArray) +
                "\n PrimitiveLongArray=" + Arrays.toString(aPrimitiveLongArray) +
                "\n PrimitiveDoubleArray=" + Arrays.toString(aPrimitiveDoubleArray) +
                "\n PrimitiveFloatArray=" + Arrays.toString(aPrimitiveFloatArray) +
                "\n PrimitiveShortArray=" + Arrays.toString(aPrimitiveShortArray) +
                "\n BooleanArray=" + Arrays.toString(aBooleanArray) +
                "\n PrimitiveBooleanArray=" + Arrays.toString(aPrimitiveBooleanArray) +
                "\n DateArray=" + Arrays.toString(aDateArray) +
                "\n ByteArray=" + Arrays.toString(aByteArray) +
                "\n PrimitiveByteArray=" + Arrays.toString(aPrimitiveByteArray) +
                "\n}";
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(aStringArray);
        result = 31 * result + Arrays.hashCode(aIntegerArray);
        result = 31 * result + Arrays.hashCode(aLongArray);
        result = 31 * result + Arrays.hashCode(aDoubleArray);
        result = 31 * result + Arrays.hashCode(aFloatArray);
        result = 31 * result + Arrays.hashCode(aShortArray);
        result = 31 * result + Arrays.hashCode(aPrimitiveIntegerArray);
        result = 31 * result + Arrays.hashCode(aPrimitiveLongArray);
        result = 31 * result + Arrays.hashCode(aPrimitiveDoubleArray);
        result = 31 * result + Arrays.hashCode(aPrimitiveFloatArray);
        result = 31 * result + Arrays.hashCode(aPrimitiveShortArray);
        result = 31 * result + Arrays.hashCode(aBooleanArray);
        result = 31 * result + Arrays.hashCode(aPrimitiveBooleanArray);
        result = 31 * result + Arrays.hashCode(aDateArray);
        result = 31 * result + Arrays.hashCode(aByteArray);
        result = 31 * result + Arrays.hashCode(aPrimitiveByteArray);
        return result;


    }
}