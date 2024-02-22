package ecc;
import field.compute.Field;
import field.compute.FieldElement;

import java.math.BigInteger;

public class ECCurve {
    public Field f;
    public FieldElement a;
    public FieldElement b;
    public BigInteger seed;

    public ECCurve(Field f, FieldElement a, FieldElement b, BigInteger seed){
        this.f = f;
        this.a = a;
        this.b = b;
        // 这里 seed 的类型还不确定
        this.seed = seed;
    }

    public boolean equals(ECCurve a){
        return false;
    }
    public FieldElement getA(){
        return this.a;
    }
    public FieldElement getB(){
        return this.b;
    }
    public Field getF(){
        return this.f;
    }
    public BigInteger getSeed(){
        return this.seed;
    }
    public ECCurve getGenerator(){
        return null;
    }
    public int getOrder(){
        return 0;
    }
    public ECPoint createPoint(FieldElement x, FieldElement y){
        return null;
    }
}