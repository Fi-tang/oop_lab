package field.compute;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @date: 2023.5.8
 * 修改之前的操作，对于加、减、乘、除中出现的所有补零操作，进行修改
 * 1. 复制本地副本，对副本进行补零
 * 2. 不修改原来的数值
 * */
public class ExtFieldElement implements FieldElement{
    public ExtField F; // 这里的 F 就是元素所在的域
    public ArrayList<FieldElement> v = new ArrayList<FieldElement>();

    public ExtFieldElement(Field F, ArrayList<FieldElement> v){
        this.F = (ExtField)F;
        this.v = v;
    }

    public Field GetField(){
        return this.F;
    }

    /**
     * 新增加的功能，专门用来作本地的副本拷贝。
     * 作用是: 新增一个和原来变量一模一样的扩域元素，不修改原先扩域元素
     * 的多项式表示。
     * */
    public ExtFieldElement CopyDuplicate(ExtFieldElement element){
        ArrayList<FieldElement> copytemp = new ArrayList<FieldElement>();
        for(int i = 0; i < element.v.size(); i++){
            copytemp.add(element.v.get(i));
        }
        return new ExtFieldElement(element.F, copytemp);
    }

    public ExtFieldElement Mod() throws Exception{
        ExtFieldElement copyMod = CopyDuplicate(this);
        for(int i = 0; i < copyMod.v.size(); i++){
            copyMod.v.set(i, copyMod.v.get(i).Mod());
        }
        return copyMod;
    }

    /*
    省略必须长度一致的判断，先直接通过调用下方的 PrimeFieldElement 的 Add() 来进行
    正确性的验证通过 FieldPolynomial 进行开展
    例如 ExtFieldElement = [5, 0, 2, 3] -> ArrayList<PrimeFieldElement>
    */
    public ExtFieldElement Add(FieldElement a) throws Exception{
        ExtFieldElement copyAddthis = CopyDuplicate(this);

        ExtFieldElement aa = (ExtFieldElement)a;
        ExtFieldElement copyAddaa = CopyDuplicate(aa);
        // 先拷贝两个副本

        int n = Normalize(copyAddthis.v).size();
        int m = Normalize(copyAddaa.v).size();
        int maxLength = (m >= n)? m : n;
        if(maxLength == m){
            for(int k = n; k < maxLength; k++){
                copyAddthis.v.add(copyAddthis.F.BaseField().Zero()); // 等价于 this.v.add(this.v.get(0).GetField().Zero())
            }
        }
        else{
            for(int k = m; k < maxLength; k++){
                copyAddaa.v.add(copyAddaa.F.BaseField().Zero());
            }
        }

        ArrayList<FieldElement> rr = new ArrayList<FieldElement>();
        for(int i = 0; i < maxLength; i++){
            rr.add(copyAddthis.v.get(i).Add(copyAddaa.v.get(i)));
        }
        return new ExtFieldElement(copyAddthis.F, rr).Div();
    }

    public ExtFieldElement Sub(FieldElement b) throws Exception{
        ExtFieldElement copySubthis = CopyDuplicate(this);

        ExtFieldElement bb = (ExtFieldElement)b;
        ExtFieldElement copySubbb = CopyDuplicate(bb);

        int n = Normalize(copySubthis.v).size();
        int m = Normalize(copySubbb.v).size();

        int maxLength = (m >= n)? m : n;
        if(maxLength == m){
            for(int k = n; k < maxLength; k++){
                copySubthis.v.add(copySubthis.F.BaseField().Zero());
            }
        }
        else{
            for(int k = m; k < maxLength; k++){
                copySubbb.v.add(copySubbb.F.BaseField().Zero());
            }
        }
        ArrayList<FieldElement> rr = new ArrayList<FieldElement>();
        for(int i = 0; i < maxLength; i++){
            rr.add(copySubthis.v.get(i).Sub(copySubbb.v.get(i)));
        }
        return new ExtFieldElement(copySubthis.F, rr).Div();
    }

    /*
             debug 阶段，分别打印所有的类型:
             1. 排查出原因，原来的计算中，返回 return new ExtFieldElement(this.F, result)
             this.v.get(i), pp.v.get(j), this.v.get(i).Mul(pp.v.get(j)) 都是 PrimeFieldElement 类型
             但是 result.get(i + j) 是 ExtFieldElement 类型
             所以在执行 Add 的时候， result.get(i + j).Add 类型不匹配

             2. 为什么 result.get(i + j) 是 ExtFieldElement 类型?
             问题出在之前的补零操作，直接按照 result.add(this.F.Zero()) 进行补零
             this.F.Zero() 是 ExtFieldElement 类型，所以补零操作出现错误。
              */
    //System.out.println("result.get(i + j) is: " + result.get(i + j) + " " + result.get(i + j)._str_());
    // field.compute.ExtFieldElement@1540e19d    []
    //System.out.println("this.v.get(i) is: " + this.v.get(i) + " " + this.v.get(i)._str_());
    // field.compute.PrimeFieldElement@677327b6     15
    //System.out.println("pp.v.get(j) is: " + pp.v.get(j) + " " + pp.v.get(j)._str_());
    // field.compute.PrimeFieldElement@14ae5a5      13
    //System.out.println("this.v.get(i).Mul(pp.v.get(j) is: " + this.v.get(i).Mul(pp.v.get(j)) + " " + this.v.get(i).Mul(pp.v.get(j))._str_());
    // field.compute.PrimeFieldElement@7f31245a 11
    //System.out.println("result.get(i + j).Add(this.v.get(i).Mul(pp.v.get(j))) is: " + result.get(i + j).Add(this.v.get(i).Mul(pp.v.get(j)))
    //+ " " + result.get(i + j).Add(this.v.get(i).Mul(pp.v.get(j)))._str_());
    public ExtFieldElement Mul(FieldElement p) throws Exception{
        //System.out.println("start");
        ExtFieldElement copyMulthis = CopyDuplicate(this);

        ExtFieldElement pp = (ExtFieldElement)p;
        ExtFieldElement copyMulpp = CopyDuplicate(pp);

        if(copyMulthis.Equal(copyMulthis.F.One())){
            return copyMulpp;
        }
        if(copyMulpp.Equal(copyMulthis.F.One())){
            return copyMulthis;
        }
        if(copyMulthis.Equal(copyMulthis.F.Zero()) || copyMulpp.Equal(copyMulthis.F.Zero())){
            return (ExtFieldElement)copyMulthis.F.Zero();
        }
        int n = Normalize(copyMulthis.v).size();
        int m = Normalize(copyMulpp.v).size();


        ArrayList<FieldElement> result = new ArrayList<FieldElement>();
        for(int k = 0; k < (m + n); k++){
            result.add(copyMulthis.F.BaseField().Zero());
            // System.out.println("this.basedOnF.Zero(): " + this.basedOnF.Zero() + " is " + this.basedOnF.Zero()._str_());
        }
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                result.set(i + j, result.get(i + j).Add(copyMulthis.v.get(i).Mul(copyMulpp.v.get(j))));
            }
        }
        //System.out.println("end");
        return new ExtFieldElement(copyMulthis.F, result).Div();
    }

    public ArrayList<FieldElement> Normalize(ArrayList<FieldElement> vector){
        if(vector == null || vector.size() == 1){
            return vector;
        }
        else{
            int countReal = vector.size();
            for(int i = vector.size() - 1; i >= 0; --i){
                if(!vector.get(i).Equal(vector.get(0).GetField().Zero())){
                    countReal = i;
                    break;
                }
            }

            if(countReal < vector.size() - 1){
                ArrayList<FieldElement> newVector = new ArrayList<FieldElement>();
                for(int i = 0; i <= countReal; i++){
                    newVector.add(vector.get(i));
                }
                return newVector;
            }
            else{
                return vector;
            }
        }
    }

    public ExtFieldElement Pow(int k) throws Exception{
        ExtFieldElement copyPow = CopyDuplicate(this);
        if(k == 0){
            return (ExtFieldElement)copyPow.F.One();
        }
        else if(k == 1){
            return copyPow;
        }
        else{
            // 以 k = 65538 为例
            /*
             * 以下为调试部分，
             * BigInteger 转为二进制字符串
             *   BigInteger bI1;
             *   String bI1String = bI1.toString(2);
             *   实现左右移动的方法: shiftRight()
             * */
            BigInteger powk = BigInteger.valueOf(k);
            int klength = powk.bitLength();
            int temp = 1;

            ExtFieldElement powTemp = new ExtFieldElement(copyPow.F, copyPow.v);
            ExtFieldElement result = (ExtFieldElement)copyPow.F.One();

            for(int i = 0; i < klength; i++){
                if(i == 0){
                    powTemp = powTemp;
                }
                else{
                    powTemp = powTemp.Mul(powTemp);
                }
                temp = 1 << (i);
                // System.out.println("temp = " + temp + " that is: " + BigInteger.valueOf(temp).toString(2) + " k & temp = " + BigInteger.valueOf(k & temp).toString());
                if((k & temp) == temp){
                    result = result.Mul(powTemp);
                }
            }
            return result.Div();
        }
    }

    public ExtFieldElement Inv() throws Exception{ // 后面 Fix
        ExtFieldElement copyInv = CopyDuplicate(this);

        ArrayList<FieldElement> value = Normalize(copyInv.v);
        FieldPolynomial tempValue = new FieldPolynomial(value);
        FieldPolynomial another = tempValue.ModInv(new FieldPolynomial(Normalize(copyInv.F.ir)));
        return new ExtFieldElement(copyInv.F, Normalize(another.v));
    }

    public ExtFieldElement Div() throws Exception{
        ExtFieldElement copyDiv = CopyDuplicate(this);

        ArrayList<FieldElement> value = Normalize(copyDiv.v);
        FieldPolynomial tempValue = new FieldPolynomial(value);
        FieldPolynomial another = tempValue.Div(new FieldPolynomial(Normalize(copyDiv.F.ir)))[1];
        return new ExtFieldElement(copyDiv.F, Normalize(another.v));
    }

    public boolean IsZero(){
        // 区别于 python 版本的 return self.V.Equal(self.F.Zero()) 后续检查
        int m = this.v.size();
        ArrayList<FieldElement> zeroArray = new ArrayList<FieldElement>();
        for(int i = 0; i < m; i++){
            zeroArray.add(this.v.get(0).GetField().Zero());
        }
        ExtFieldElement newValue = new ExtFieldElement(this.F, zeroArray);
        return this.Equal(newValue);
    }

    public boolean IsOne(){
        int m = this.v.size();
        ArrayList<FieldElement> oneArray = new ArrayList<FieldElement>();
        oneArray.add(this.v.get(0).GetField().One());
        for(int i = 1; i < m; i++){
            oneArray.add(this.v.get(0).GetField().Zero());
        }
        ExtFieldElement newValue = new ExtFieldElement(this.F, oneArray);
        return this.Equal(newValue);
    }

    public boolean Equal(BigInteger x){
        return false;
    }

    public boolean Equal(FieldElement e){
//        int n = Normalize(this.v).size();
//        // 本意是判断元素是否属于同一个 Field, 实际上需要 考虑 e 的长度为 零，后续添加
//        ExtFieldElement ee = (ExtFieldElement)e;
//        if(Normalize(ee.v).size() != n){
//            return false;
//        }
//        for(int i = 0; i < n; i++){
//            if(!this.v.get(i).Equal(ee.v.get(i))){
//                return false;
//            }
//        }
//        return true;

        ExtFieldElement newThis = new ExtFieldElement(this.F, Normalize(this.v));
        int n = newThis.v.size();
        ExtFieldElement ee = (ExtFieldElement)e;
        ExtFieldElement newEE = new ExtFieldElement(this.F, Normalize(ee.v));
        int m = ee.v.size();
        if(m != n){
            return false;
        }

        for(int i = 0; i < n; i++){
            if(!newThis.v.get(i).Equal(ee.v.get(i))){
                return false;
            }
        }
        return true;
    }

    /*
    新定义了一个接口，这个接口的作用主要是用来打印输出的结果。
    现在的一个问题： 不能很清晰地说明应该打印输出成何种格式，
    思想仿照 ExtFieldElement 定义 Add() 加法时，采用
        this.v.get(i).Add()
    相当于调更低一层域上的加法，直到调用到 PrimeField 上的加法后返回。
    if(iMax == -1)   return "[ 1 ]"
    后面还需要弄明白这里为什么长度为 0 的时候添了个 1。
     */
//    public String _str_(){
//        if(this.v == null){
//            return "null";
//        }
//        int iMax = this.v.size() - 1;
//        if(iMax == -1)  return "[ 1 ]";
//
//        StringBuilder b = new StringBuilder();
//        b.append('[');
//        for(int i = iMax; i > 0; i--){
//            if(!this.v.get(i).Equal(this.v.get(0).GetField().Zero())){
//                if(this.v.get(i).Equal(this.v.get(0).GetField().One())){
//                    b.append("x^{" + i + "}");
//                }
//                else{
//                    b.append(this.v.get(i)._str_() + " x^{" + i + "}");
//                }
//            }
//            else{
//                continue;
//            }
//            b.append(" + ");
//        }
//
//        if(!this.v.get(0).Equal(this.v.get(0).GetField().Zero())){
//            b.append("" + this.v.get(0)._str_());
//        }
//        return b.append(']').toString();
//    }

    // 仅用于debug
    public String _str_(){
        if(this.v == null){
            return "null";
        }
        int iMax = this.v.size() - 1;
        if(iMax == -1)  return "[ 1 ]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for(int i = iMax; i > 0; i--){
            if(this.v.get(i).Equal(this.v.get(0).GetField().One())){
                b.append("x^{" + i + "}");
            }
            else {
                b.append(this.v.get(i)._str_() + " x^{" + i + "}");
            }
            b.append(" + ");
        }


        b.append("" + this.v.get(0)._str_());
        return b.append(']').toString();
    }

    public static void main(String[] args) throws Exception{
        // 构造三层扩域
        PrimeField pfBase = new PrimeField(BigInteger.valueOf(23));

        //*******************************************************************************************************************************************
        // 第一层 pfBase -> pf2
        // 第一个 pfBase -> pf2 上的不可约多项式: 9x^{2} + 2 = [2, 0, 9], 其构造元素都是 pfBase 上的
        ArrayList<FieldElement> pf2Ir = new ArrayList<FieldElement>(
                Arrays.asList(pfBase.FromNum(BigInteger.valueOf(2)), pfBase.FromNum(BigInteger.valueOf(0)), pfBase.FromNum(BigInteger.valueOf(9)))
        );
        ExtField pf2 = new ExtField(pfBase, pf2Ir);
        // 第一层 pf2 上的元素1: 7x + 15, 其构造元素都是 pfBase 上的
        // 第一层 pf2 上的元素2: 21x + 13
        ArrayList<FieldElement> pf2Element1Array = new ArrayList<FieldElement>(
                Arrays.asList(pfBase.FromNum(BigInteger.valueOf(15)) , pfBase.FromNum(BigInteger.valueOf(7))));
        ArrayList<FieldElement> pf2Element2Array = new ArrayList<FieldElement>(
                Arrays.asList(pfBase.FromNum(BigInteger.valueOf(13)), pfBase.FromNum(BigInteger.valueOf(21))));

        ExtFieldElement pf2Element1 = new ExtFieldElement(pf2, pf2Element1Array);
        // 从直观上理解，传入的参数 pf2 和 pf2Element1Array 实际上就表示 pf2 上的一个扩域元素
        // 在补零的过程中，我们需要给定新的参数，pf2 所基于的域: pf
        // 换言之，pf4 所基于的域 pf2.
        // 在构造的时候，尽量简化，我们只关注 (这个元素是哪个扩域上的?) (这个元素所在的值是多少, 值构造过程中的系数依赖于所 baseOn 的元素)
        ExtFieldElement pf2Element2 = new ExtFieldElement(pf2, pf2Element2Array);

        System.out.println("Fp2 上的第一个元素是: " + pf2Element1._str_()); // [7 x^{1} + 15]
        System.out.println("Fp2 上的第二个元素是: " + pf2Element2._str_()); // [21 x^{1} + 13]
        System.out.println("Fp2 上的加法运算结果: " + pf2Element1.Add(pf2Element2)._str_()); //  [5 x^{1} + 5]
        System.out.println("Fp2 上的减法运算结果: " + pf2Element1.Sub(pf2Element2)._str_()); //  [9 x^{1} + 2]
        System.out.println("Fp2 上的乘法运算结果: " + pf2Element1.Mul(pf2Element2)._str_()); //  [9 x^{2} + 15 x^{1} + 11]
//
//
//        for(int i = 0; i <= 15; i++){   // 19 往后开始卡住
//            System.out.println("Fp2 上的乘方运算结果: 第" + i + "次为: " + pf2Element1.Pow(i)._str_());
//        }
        //*******************************************************************************************************************************************
        // 第二层: pf2 -> pf4
        // 第二个 pf2 -> pf4 上的不可约多项式: 7x^{2} + 22x = [0, 22, 7],其构造元素都是 pf2 上的
        ArrayList<FieldElement> pf4Ir = new ArrayList<FieldElement>(
                Arrays.asList(pf2.FromVector(pf2Element1Array), pf2.FromVector(pf2Element2Array), pf2.FromVector(pf2Element2Array)));
        ExtField pf4 = new ExtField(pf2, pf4Ir);

        // 构造 pf4上的元素，其系数是 pf2 上的元素， pf2 上的元素又是通过系数为 pf 的元素构造得到
        ArrayList<FieldElement> pf4Element1Array = new ArrayList<FieldElement>(
                Arrays.asList(pf2.FromVector(pf2Element2Array), pf2.FromVector(pf2Element1Array), pf2.FromVector(pf2Element2Array)));
        ArrayList<FieldElement> pf4Element2Array = new ArrayList<FieldElement>(
                Arrays.asList(pf2.FromVector(pf2Element1Array), pf2.FromVector(pf2Element2Array), pf2.FromVector(pf2Element2Array)));
        ExtFieldElement pf4Element1 = new ExtFieldElement(pf4, pf4Element1Array);
        ExtFieldElement pf4Element2 = new ExtFieldElement(pf4, pf4Element2Array);

        System.out.println("Fp4 上的第一个元素是: " + pf4Element1._str_());
//        System.out.println("Fp4 上的第二个元素是: " + pf4Element2._str_());
////        System.out.println("Fp4 上的加法运算结果: " + pf4Element1.Add(pf4Element2)._str_());
////        System.out.println("Fp4 上的减法运算结果: " + pf4Element1.Sub(pf4Element2)._str_());
////        System.out.println("Fp4 上的乘法运算结果: " + pf4Element1.Mul(pf4Element2)._str_());

        for(int i = 0; i <= 15; i++){   // 跑到 i = 3 卡住
            System.out.println("Fp4 上的乘方运算结果: 第" + i + "次为：" + pf4Element1.Pow(i)._str_());
        }
    }
}