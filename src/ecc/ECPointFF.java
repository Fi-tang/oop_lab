package ecc;
import field.compute.Field;
import field.compute.FieldElement;
import field.compute.PrimeField;

import java.math.BigInteger;

public class ECPointFF extends ECPoint{
    ECCurve c = null;
    FieldElement z = null;

    public ECPointFF(ECCurve c, FieldElement x, FieldElement y, FieldElement z){
        super(x,y);
        this.c = c;
        if(z == c.getF().One()){ // 这一行判断经常出现 nullptrException
            z = c.getF().One();
        }
        else{
            this.z = z;
        }
    }

    @Override
    public FieldElement getAffineX(){
        if(this.z.IsOne()){
            return this.x;
        }
        else{
            throw new RuntimeException();
        }
    }

    @Override
    public FieldElement getAffineY(){
        if(this.z.IsOne()){
            return this.y;
        }
        else{
            throw new RuntimeException();
        }
    }

    @Override
    public boolean equals(ECPoint p) throws Exception{
        ECPointFF pp = (ECPointFF)p;
        if(this.c != pp.c){
            return false;
        }
        return this.z.Pow(3).Mul(pp.y) == pp.z.Pow(3).Mul(this.y);
    }

    @Override
    public boolean isInfinity(){
        return this.z.IsZero();
    }

    @Override
    public ECPointFF doublePoint() throws Exception{
        FieldElement z_2 = this.z.Mul(this.z);
        FieldElement a1 = this.x.GetField().FromNum(BigInteger.valueOf(3)).Mul(this.x.Mul(this.x)).Add(this.c.a.Mul(z_2.Mul(z_2)));
        FieldElement a2 = this.x.GetField().FromNum(BigInteger.valueOf(4)).Mul(this.x.Mul(this.y)).Mul(this.y);
        FieldElement y_2 = this.y.Mul(this.y);
        FieldElement a3 = this.x.GetField().FromNum(BigInteger.valueOf(8)).Mul(y_2.Mul(y_2));
        FieldElement two = this.x.GetField().FromNum(BigInteger.valueOf(2));
        FieldElement x3 = a1.Mul(a1).Sub(two.Mul(a2));
        FieldElement y3 = a1.Mul(a2.Sub(x3)).Sub(a3);
        FieldElement z3 = two.Mul(this.y).Mul(this.z);
        return new ECPointFF(this.c, x3, y3, z3);
    }

    @Override
    public ECPointFF add(ECPoint p) throws Exception{
        ECPointFF pp = (ECPointFF)p;
        if(this.c != pp.c){
            throw new Exception("TypeError");
        }
        if(this.z.IsZero()){
            return pp;
        }
        if(pp.z.IsZero()){
            return this;
        }

        FieldElement z2_2 = pp.z.Mul(pp.z);
        FieldElement z1_2 = this.z.Mul(this.z);
        FieldElement y1z22 = this.y.Mul(z2_2);
        FieldElement y2z12 = pp.y.Mul(z1_2);
        if(y1z22.Equal(y2z12)){
            return this.doublePoint();
        }
        FieldElement a1 = this.x.Mul(z2_2);
        FieldElement a2 = pp.x.Mul(z1_2);
        FieldElement a3 = a1.Sub(a2);
        FieldElement a4 = y1z22.Mul(pp.z);
        FieldElement a5 = y2z12.Mul(this.z);
        FieldElement a6 = a4.Sub(a5);
        FieldElement a7 = a1.Add(a2);
        FieldElement a8 = a4.Add(a5);
        FieldElement a3_2 = a3.Mul(a3);
        FieldElement x3 = a6.Mul(a6).Sub(a7.Mul(a3_2));
        FieldElement y3 = a6.Mul(a1.Mul(a3_2)).Sub(x3).Sub(a4.Mul(a3_2.Mul(a3)));
        FieldElement z3 = this.z.Mul(pp.z).Mul(a3);
        return new ECPointFF(this.c, x3, y3, z3);
    }
    public ECPointFF multiply(int k){
        // 解释这一句: mask = int("8000000000000000000000000000000000000000000000000000000000000000",16) // 长度过长，int盛不下
        /*
        * */
        String maskhexString = "8000000000000000000000000000000000000000000000000000000000000000";
        int mask = Integer.parseInt(maskhexString, 16);
        System.out.println(mask);
        return null;
    }
}