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


    public String[] getaStringArray() {
        return aStringArray;
    }

    public void setaStringArray(String[] aStringArray) {
        this.aStringArray = aStringArray;
    }

    public Integer[] getaIntegerArray() {
        return aIntegerArray;
    }

    public void setaIntegerArray(Integer[] aIntegerArray) {
        this.aIntegerArray = aIntegerArray;
    }

    public Long[] getaLongArray() {
        return aLongArray;
    }

    public void setaLongArray(Long[] aLongArray) {
        this.aLongArray = aLongArray;
    }

    public Double[] getaDoubleArray() {
        return aDoubleArray;
    }

    public void setaDoubleArray(Double[] aDoubleArray) {
        this.aDoubleArray = aDoubleArray;
    }

    public Float[] getaFloatArray() {
        return aFloatArray;
    }

    public void setaFloatArray(Float[] aFloatArray) {
        this.aFloatArray = aFloatArray;
    }

    public Short[] getaShortArray() {
        return aShortArray;
    }

    public void setaShortArray(Short[] aShortArray) {
        this.aShortArray = aShortArray;
    }

    public int[] getaPrimitiveIntegerArray() {
        return aPrimitiveIntegerArray;
    }

    public void setaPrimitiveIntegerArray(int[] aPrimitiveIntegerArray) {
        this.aPrimitiveIntegerArray = aPrimitiveIntegerArray;
    }

    public long[] getaPrimitiveLongArray() {
        return aPrimitiveLongArray;
    }

    public void setaPrimitiveLongArray(long[] aPrimitiveLongArray) {
        this.aPrimitiveLongArray = aPrimitiveLongArray;
    }

    public double[] getaPrimitiveDoubleArray() {
        return aPrimitiveDoubleArray;
    }

    public void setaPrimitiveDoubleArray(double[] aPrimitiveDoubleArray) {
        this.aPrimitiveDoubleArray = aPrimitiveDoubleArray;
    }

    public float[] getaPrimitiveFloatArray() {
        return aPrimitiveFloatArray;
    }

    public void setaPrimitiveFloatArray(float[] aPrimitiveFloatArray) {
        this.aPrimitiveFloatArray = aPrimitiveFloatArray;
    }

    public short[] getaPrimitiveShortArray() {
        return aPrimitiveShortArray;
    }

    public void setaPrimitiveShortArray(short[] aPrimitiveShortArray) {
        this.aPrimitiveShortArray = aPrimitiveShortArray;
    }

    public Boolean[] getaBooleanArray() {
        return aBooleanArray;
    }

    public void setaBooleanArray(Boolean[] aBooleanArray) {
        this.aBooleanArray = aBooleanArray;
    }

    public boolean[] getaPrimitiveBooleanArray() {
        return aPrimitiveBooleanArray;
    }

    public void setaPrimitiveBooleanArray(boolean[] aPrimitiveBooleanArray) {
        this.aPrimitiveBooleanArray = aPrimitiveBooleanArray;
    }

    public Date[] getaDateArray() {
        return aDateArray;
    }

    public void setaDateArray(Date[] aDateArray) {
        this.aDateArray = aDateArray;
    }

    public Byte[] getaByteArray() {
        return aByteArray;
    }

    public void setaByteArray(Byte[] aByteArray) {
        this.aByteArray = aByteArray;
    }

    public byte[] getaPrimitiveByteArray() {
        return aPrimitiveByteArray;
    }

    public void setaPrimitiveByteArray(byte[] aPrimitiveByteArray) {
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
                "aStringArray=" + Arrays.toString(aStringArray) +
                ", aIntegerArray=" + Arrays.toString(aIntegerArray) +
                ", aLongArray=" + Arrays.toString(aLongArray) +
                ", aDoubleArray=" + Arrays.toString(aDoubleArray) +
                ", aFloatArray=" + Arrays.toString(aFloatArray) +
                ", aShortArray=" + Arrays.toString(aShortArray) +
                ", aPrimitiveIntegerArray=" + Arrays.toString(aPrimitiveIntegerArray) +
                ", aPrimitiveLongArray=" + Arrays.toString(aPrimitiveLongArray) +
                ", aPrimitiveDoubleArray=" + Arrays.toString(aPrimitiveDoubleArray) +
                ", aPrimitiveFloatArray=" + Arrays.toString(aPrimitiveFloatArray) +
                ", aPrimitiveShortArray=" + Arrays.toString(aPrimitiveShortArray) +
                ", aBooleanArray=" + Arrays.toString(aBooleanArray) +
                ", aPrimitiveBooleanArray=" + Arrays.toString(aPrimitiveBooleanArray) +
                ", aDateArray=" + Arrays.toString(aDateArray) +
                ", aByteArray=" + Arrays.toString(aByteArray) +
                ", aPrimitiveByteArray=" + Arrays.toString(aPrimitiveByteArray) +
                '}';
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