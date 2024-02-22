package field.compute;

import java.math.BigInteger;
import java.util.ArrayList;

public class PrimeFieldElement implements FieldElement{
    // FieldElement 里面的 F 指的是所基于的域，但由于 PrimeField 的 BaseField() 依然是自己
    public PrimeField F;    // 这里属性如果不设置成 public,则从外部包调用时无法访问，所以设为 public，后续考虑封装性
    public BigInteger v;

    public PrimeFieldElement(Field F, BigInteger v){
        this.F = (PrimeField) F;
        this.v = v;
    }

    public Field GetField(){
        return this.F;
    }

    public FieldElement Mod() throws Exception{
        return new PrimeFieldElement(this.F, this.v.mod(this.F.P));
    }

    /**
     * 一个问题，如果写成
     * public FieldElement Add(FieldElement a), 则返回值是 FieldElement 类型
     * 在后续打印结果进行验证测试的时候，如果直接写成
     * System.out.println(pfe1.Add(pfe2).v);
     * 由于返回值为 FieldElement 类型，实际上不具有成员 .v, 所以报错
     * 改写方式一: 统一将返回值改写为子类
     * 继承关系的 Override, 返回值类型属于原来类型的子类
     *public PrimeFieldElement Add(FieldElement a)
     * 改写方式二:
     * System.out.println(((PrimeFieldElement)pfe1.Add(pfe2).v)
     */
    public PrimeFieldElement Add(FieldElement a) throws Exception{
        PrimeFieldElement aa = (PrimeFieldElement)a;
        return new PrimeFieldElement(this.F, this.v.add(aa.v).mod(F.P));
    }

    public PrimeFieldElement Sub(FieldElement b) throws Exception{
        PrimeFieldElement bb = (PrimeFieldElement)b;
        return new PrimeFieldElement(this.F, this.v.subtract(bb.v).mod(F.P));
    }

    public PrimeFieldElement Mul(FieldElement p) throws Exception{
        PrimeFieldElement pp = (PrimeFieldElement)p;
        return new PrimeFieldElement(this.F, this.v.multiply(pp.v).mod(F.P));
    }

    public PrimeFieldElement Inv() throws Exception{
        return new PrimeFieldElement(this.F, modInverse(this.v, this.F.P));
    }

    public PrimeFieldElement Div() throws Exception{
        return new PrimeFieldElement(this.F, this.v.mod(this.F.P));
    }

    public PrimeFieldElement Pow(int k) throws Exception{
        return new PrimeFieldElement(this.F, this.v.pow(k).mod(this.F.P));
    }

    public BigInteger modInverse(BigInteger a, BigInteger m) throws Exception{
        if(a.equals(BigInteger.valueOf(0)) || m.equals(BigInteger.valueOf(0))
                || m.equals(BigInteger.valueOf(1))){
            throw new Exception("PrimeFieldElement::modInverse: " + a + m);
        }
        BigInteger u1 = BigInteger.valueOf(1), u2 = BigInteger.valueOf(0), u3 = a;
        BigInteger v1 = BigInteger.valueOf(0), v2 = BigInteger.valueOf(1), v3 = m;
        while(!v3.equals(BigInteger.valueOf(0))){
            BigInteger q = u3.divideAndRemainder(v3)[0];
            BigInteger v1Temp = v1, v2Temp = v2, v3Temp = v3, u1Temp = u1, u2Temp = u2, u3Temp = u3;

            /*
            q = u3 // v3
            v1,v2,v3, u1, u2, u3 = (u1 - q * v1),
            (u2 - q * v2), (u3 - q * v3), v1,v2,v3
             */
            v1 = (u1Temp.subtract(q.multiply(v1Temp)));
            v2 = (u2Temp.subtract(q.multiply(v2Temp)));
            v3 = (u3Temp.subtract(q.multiply(v3Temp)));
            u1 = v1Temp;    u2 = v2Temp;    u3 = v3Temp;
        }
        if(!u3.equals(BigInteger.valueOf(1))){
            throw new Exception("PrimeFieldElement::modInverse:gcd( " + a + "," + m + " ) = " + u3);
        }
        return u1.mod(m);
   }

    public boolean IsZero(){
        return this.v.equals(BigInteger.valueOf(0));
    }

    public boolean IsOne(){
        return this.v.equals(BigInteger.valueOf(1));
    }

    public boolean Equal(BigInteger x){
        return this.v.mod(this.F.P).equals(x.mod(this.F.P));
    }
    // 后续是否需要修改成默认 [5,0,0,0,0] 类型 == 5 ? 是否需要判断 FieldElement 如果是 ExtFieldElement 类型，强转成 PrimeFieldElement 类型出错?
    public boolean Equal(FieldElement e){
        PrimeFieldElement ee = (PrimeFieldElement)e;
        if(ee.GetField() == this.F && (ee.v.mod(this.F.P)) == (this.v.mod(this.F.P))){
            return true;
        }
        else{
            return false;
        }
    }

//    public String _str_(){
//        if(this.v == BigInteger.valueOf(0)){
//            return "0";
//        }
//        else{
//            StringBuilder b = new StringBuilder();
//            b.append(this.v + "");
//            return b.toString();
//        }
//    }

    // debug 后恢复
    public String _str_(){
        StringBuilder b = new StringBuilder();
        b.append(this.v + "");
        return b.toString();
    }

    public static void main(String[] args) throws Exception{
        // 此处仅测试素域上的加减乘运算
        PrimeField pf = new PrimeField(BigInteger.valueOf(23));
        PrimeFieldElement pfe1 = new PrimeFieldElement(pf, BigInteger.valueOf(55));
        PrimeFieldElement pfe2 = new PrimeFieldElement(pf, BigInteger.valueOf(179));

        System.out.println("计算加的结果为: " + pfe1.Add(pfe2).v + " 素域为: " + pfe1.Add(pfe2).F.P);  // 55 + 179 = 4 mod 23
        System.out.println("计算减的结果为: " + pfe1.Sub(pfe2).v + " 素域为：" + pfe1.Sub(pfe2).F.P);  // 55 - 179 = 14 mod 23
        System.out.println("计算乘的结果为: " + pfe1.Mul(pfe2).v + " 素域为: " + pfe1.Mul(pfe2).F.P);  // 55 * 179 = 1 mod 23

        System.out.println("计算前一个元素: " + pfe1.v + " 的逆元为: " + pfe1.Inv().v);                    // 55 * 18 = 1 mod 23
        System.out.println("计算后一个元素: " + pfe2.v + " 的逆元为: " + pfe2.modInverse(pfe2.v, pfe2.F.P) // 179 * 9 = 1 mod 23
        + " 也就是 " + pfe2.Inv().v);

        for(int i = 0; i < 10; i++){
            System.out.println("计算乘方的结果: " + i + " 次方为: " + pfe1.Pow(i).v);
        }
    }
}