package ecc;

import field.compute.Field;
import field.compute.FieldElement;

public class ECPoint {
    // 定义坐标x和y
    public FieldElement x;
    public FieldElement y;

    public ECPoint(){}
    public ECPoint(FieldElement x, FieldElement y){
        this.x = x;
        this.y = y;
    }

    // 定义仿射坐标 x,y
    public FieldElement getAffineX() throws Exception{return null;};
    public FieldElement getAffineY() throws Exception{return null;};

    // 判断点是否相等
    public boolean equals(ECPoint p) throws Exception{return false;};
    public boolean isInfinity(){return false;};

    public ECPoint doublePoint() throws Exception{return null;};
    public ECPoint multiply(int k){return null;};

    public ECPoint add(ECPoint p) throws Exception{return null;};

    public ECPoint normalize(){return null;};
    public String __str__(){return null;};
}