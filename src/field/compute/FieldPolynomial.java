package field.compute;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

// 如何通过 Fp4 找到 Fp2 上的零?
// 方法一，通过FieldElement 的 BaseField().Zero()
// 方法二，通过本来就低一级别的 ArrayList<FieldElement> v 的 this.v.get(0).GetField().Zero()
public class FieldPolynomial{
    public ArrayList<FieldElement> v = new ArrayList<FieldElement>();
    public FieldPolynomial(ArrayList<FieldElement> v){
        this.v = v;
    }

    public FieldPolynomial LocalCopy(FieldPolynomial element){
        ArrayList<FieldElement> copyTemp = new ArrayList<FieldElement>();
        for(int i = 0; i < element.v.size(); i++){
            copyTemp.add(element.v.get(i));
        }
        return new FieldPolynomial(copyTemp);
    }

    public FieldPolynomial Add(FieldPolynomial a) throws Exception{
        FieldPolynomial AddCopythis = LocalCopy(this);
        FieldPolynomial AddCopya = LocalCopy(a);

        int m = Normalize(AddCopythis.v).size();
        int n = Normalize(AddCopya.v).size();

        int maxLength = (m >= n)? m : n;
        if(maxLength == m){
            for(int k = n; k < maxLength; k++){
                // 这里没有问题，因为 ExtField 里面的 F 是其所基于的域
                // 而 ExtFieldElement 的 F 就是自身所在的域
                AddCopya.v.add(AddCopythis.v.get(0).GetField().Zero());
            }
        }
        else{
            for(int k = m; k < maxLength; k++){
                AddCopythis.v.add(AddCopythis.v.get(0).GetField().Zero());
            }
        }

        ArrayList<FieldElement> result = new ArrayList<FieldElement>();
        for(int i = 0; i < maxLength; i++){
            result.add(AddCopythis.v.get(i).Add(AddCopya.v.get(i)).Mod().Div());
        }
        return new FieldPolynomial(result);
    }

    public FieldPolynomial Sub(FieldPolynomial b) throws Exception{
        FieldPolynomial SubCopythis = LocalCopy(this);
        FieldPolynomial SubCopyb = LocalCopy(b);

        int m = Normalize(SubCopythis.v).size();
        int n = Normalize(SubCopyb.v).size();

        int maxLength = (m >= n)? m : n;

        if(m >= n){
            for(int k = n; k < m; k++){
                SubCopyb.v.add(SubCopythis.v.get(0).GetField().Zero());
            }
        }
        else{
            for(int k = m; k < n; k++){
                SubCopythis.v.add(SubCopythis.v.get(0).GetField().Zero());
            }
        }

        ArrayList<FieldElement> result = new ArrayList<FieldElement>();
        for(int i = 0; i < maxLength; i++){
            result.add(SubCopythis.v.get(i).Sub(SubCopyb.v.get(i)).Mod().Div());
        }
        return new FieldPolynomial(result);
    }

    public FieldPolynomial Mul(FieldPolynomial b) throws Exception{
        FieldPolynomial MulCopythis = LocalCopy(this);
        FieldPolynomial MulCopyb = LocalCopy(b);

        int m = Normalize(MulCopythis.v).size();
        int n = Normalize(MulCopyb.v).size();

        ArrayList<FieldElement> result = new ArrayList<FieldElement>();
        for(int k = 0; k < (m + n); k++){
            result.add(MulCopythis.v.get(0).GetField().Zero());  // 这里是 GetField()
        }
        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                result.set(i + j, result.get(i + j).Add(MulCopythis.v.get(i).Mul(MulCopyb.v.get(j))).Div());
            }
        }
        return new FieldPolynomial(result);
    }

    // 实现打印功能
//   public String _str_(boolean isIr){
//        if(this.v == null){
//            return "null";
//        }
//        int iMax = this.v.size() - 1;
//        if(iMax == -1)  return "[ 1 ]";
//
//        StringBuilder b = new StringBuilder();
//        if(isIr){   b.append("[ x^{" + (iMax + 1) + "} + ");}
//        else{ b.append('[');}
//        for(int i = iMax; i > 0; i--){
//            if(!this.v.get(i).Equal(this.v.get(0).GetField().Zero())){
//                if(this.v.get(i).Equal(this.v.get(0).GetField().One())){
//                    b.append("x^{" + i + "}");
//                }
//                else{
//                    // 这里的 this.v.get(i) 应该换成对应元素的打印功能
//                    b.append(this.v.get(i)._str_() + "x ^{" + i + "}");
//                }
//            }
//            else{
//                continue;
//            }
//            b.append(" + ");
//        }
//
//        if(!this.v.get(0).Equal(this.v.get(0).GetField().Zero())){
//            b.append(" " + this.v.get(0)._str_());
//        }
//        return b.append(']').toString();
//   }

    // 此版打印仅作为 debug 使用
    public String _str_(boolean isIr){
        if(this.v == null){
            return "null";
        }
        int iMax = this.v.size() - 1;
        if(iMax == -1)  return "[ 1 ]";

        StringBuilder b = new StringBuilder();
        if(isIr){   b.append("[ x^{" + (iMax + 1) + "} + ");}
        else{ b.append('[');}
        for(int i = iMax; i > 0; i--){
            if(this.v.get(i).Equal(this.v.get(0).GetField().One())){
                b.append("x^{" + i + "}");
            }
            else{
                // 这里的 this.v.get(i) 应该换成对应元素的打印功能
                b.append(this.v.get(i)._str_() + "x ^{" + i + "}");
            }
            b.append(" + ");
        }

        b.append(" " + this.v.get(0)._str_());

        return b.append(']').toString();
    }

    // 实现除法功能
    public BigInteger modInverse(BigInteger a, BigInteger m)throws Exception{
        if(a.compareTo(BigInteger.valueOf(0)) == 0 || m.compareTo(BigInteger.valueOf(0)) == 0 || m.compareTo(BigInteger.valueOf(1)) == 0){
            throw new Exception("Polynomial::modInverse: " + a + "/" + m);
        }
        BigInteger u1 = BigInteger.valueOf(1), u2 = BigInteger.valueOf(0), u3 = a;
        BigInteger v1 = BigInteger.valueOf(0), v2 = BigInteger.valueOf(1), v3 = m;

        while(v3.compareTo(BigInteger.valueOf(0)) != 0){
            BigInteger q = u3.divideAndRemainder(v3)[0];
            BigInteger u1Temp = u1, u2Temp = u2, u3Temp = u3;
            BigInteger v1Temp = v1, v2Temp = v2, v3Temp = v3;

            v1 = u1Temp.subtract(q.multiply(v1Temp));
            v2 = u2Temp.subtract(q.multiply(v2Temp));
            v3 = u3Temp.subtract(q.multiply(v3Temp));
            u1 = v1Temp; u2 = v2Temp; u3 = v3Temp;
        }
        if(u3.compareTo(BigInteger.valueOf(1)) != 0){
            throw new Exception("Polynomial::modInverse: gcd(" + a + "," + m + ")" + "=" + u3);
        }
        return u1.mod(m);
    }

    // 开始循环过程:
    /*
     * 20230330: 目前的方案是对满足单轮次计算的除法进行修正。
     * 昨天发现的第一个问题:
     * 被除式:
     * [[ 16 x^{2} + 14 x^{1} + 20] x^{2} + [20 x^{2} + 6x^{1} + 6]x^{1} + [x^{2} + 17x^{1} + 9]]
     * 除式:
     * [[6 x^{2} + 8x^{1} + ] x^{2} + [11 x^{2} + 18x^{1} + ]x^{1} + [4x^{2} + 4x^{1} + 20]]
     *
     * 第一步计算:
     * 对于除法运算的过程，思路是: 假设被除式为:  An x^{n} + A(n-1) x^{n - 1} + .... + Ai x^{i} + A0 x^{0}
     *                           假设除式为:   Bm x^{m} + B(m-1) x^{m - 1} + .... + Bj x^{j} + B0 x^{0}
     *
     * 首先计算： Bm 的逆，假设表示为 Bm^{-1}
     * 上商的情况为: An x^{n - m} * (Bm x^{m} + B(m-1) x^{m - 1} + .... + Bj x^{j} + B0 x^{0}) * Bm^{-1}
     * 可以确保第一项在此处作用下为: An x^{n}
     *
     * debug断点调试遇到的情况:
     * 求得第一项的逆元为: [6x^{2} + 8x^{1} + ] 在 [x^{3} + 7] 下的逆元为: [8x^{2} + 3x + 19]
     * 此时的调试过程为: 由于 x^{2} 和 x^{2} 的次数相当，所以 这里不需要 x^{n - m} = x^{0} = 1
     * 参与运算的乘法过程为:
     *
     * 每一项之中的系数:
     * 上商的情况: [8x^{2} + 3x + 19] * [[ 16 x^{2} + 14 x^{1} + 20]]  即: [ [13 x^{4} + 22 x^{3} + 4 x^{1} + 12]]
     * 用上商的该项系数: [ [13 x^{4} + 22 x^{3} + 4 x^{1} + 12]] *
     * [[6 x^{2} + 8x^{1} + ] x^{2} + [11 x^{2} + 18x^{1} + ]x^{1} + [4x^{2} + 4x^{1} + 20]]
     *
     * 得到第一项是:
     * [[9 x^{6} + 6x^{5} + 15 x^{4} + x^{3} + 12 x ^{2} + 4x^{1}] x^{2}
     * 实际上由于 bInverse 在 modInverse 的意义下才有效，
     * 上商和乘法的过程，都需要执行:
     * currentThis.get(currentThisTimes).Mul( bInverse.Mul(bReform.get(i - currentGap)).Div() ))
     *
     * 第一次修改：
     * 改动两个地方: 首先:
     * middleTerm.add( currentThis.get(currentThisTimes).Mul( bInverse.Mul(bReform.get(i - currentGap)).Div().Div())
     *
     * 所以第一轮运算，需要上商：[5x^{1} + 19]
     * 第一次上商的系数: [13 x^{4} + 22 x^{3} + 4 x^{1} + 12] 实际上相当于: [5 x^{1} + 19], 和除式各项系数相乘之后:
     * [5 x ^{1} + 19] *  [[6 x^{2} + 8x^{1} + ] x^{2} + [11 x^{2} + 18x^{1} + ]x^{1} + [4x^{2} + 4x^{1} + 20]]
     * 得到:
     *  [[16 x^{2} + 14x^{1} + 20] x^{2} + [20x^{1} + 6]x^{1} + [4x^{2} + 15x^{1} + 10]] 结果验证正确
     * 所以新一轮的被除数是:
     * [[]x^{2} + [20x^{2} + 9x^{1} + ] x^{1} + [20 x^{2} + 2x^{1} + 22]]
     * (确实在结果中看到了这一步，但运算未停止)
     * */
//                // 有误写法: //  middleTerm.add(currentThis.get(currentThisTimes).Mul(bInverse).Mul(bReform.get(i - currentGap)).Mod());
    // 有误写法:
//                   middleTerm.add( currentThis.get(currentThisTimes).Mul( bInverse.Mul(bReform.get(i-currentGap)).Div()  )  );
    public FieldPolynomial[] Div(FieldPolynomial b) throws Exception{
        // 用之前的用例完成基本测试
//        System.out.println("+++++Enter Div+++++");
        FieldPolynomial DivCopythis = LocalCopy(this);
        FieldPolynomial DivCopyb = LocalCopy(b);
        ArrayList<FieldElement> thisReform = new ArrayList<FieldElement>();
        for(int i = 0; i < Normalize(DivCopythis.v).size(); i++){
            thisReform.add(Normalize(DivCopythis.v).get(i).Div());
        }
        ArrayList<FieldElement> bReform = new ArrayList<FieldElement>();
        for(int i = 0; i < Normalize(DivCopyb.v).size(); i++){
            bReform.add(Normalize(DivCopyb.v).get(i).Div());
        }

//        System.out.println("实际的被除数：" + new FieldPolynomial(thisReform)._str_(false) + " 大小为: " + thisReform.size());
//        System.out.println("实际的除数: " + new FieldPolynomial(bReform)._str_(false) + " 大小为: " + bReform.size());

        int position = bReform.size() - 1;
        for(int i = bReform.size() - 1; i >= 0; --i){
            if(!bReform.get(i).Equal(bReform.get(0).GetField().Zero())){
                position = i;
                break;
            }
        }
        int positionThis = thisReform.size() - 1;
        for(int i = thisReform.size() - 1; i >= 0; --i){
            if(!thisReform.get(i).Equal(thisReform.get(0).GetField().Zero())){
                positionThis = i;
                break;
            }
        }
        FieldElement bInverse = Normalize(b.v).get(position).Inv();
//        System.out.println("被除数的实际大小 = " + positionThis  + " 除数的实际大小：" + position + " , bInverse = " + bInverse._str_());
        ArrayList<FieldElement> currentThis = new ArrayList<FieldElement>();
        for(int i = 0; i < thisReform.size(); i++){
            currentThis.add(thisReform.get(i));
        }

        ArrayList<FieldElement> quotientResult = new ArrayList<FieldElement>();
        for(int i = 0; i < thisReform.size(); i++){
            quotientResult.add(this.v.get(0).GetField().Zero());
        }

        // 循环准备工作
        int currentThisTimes = positionThis;
        int unchangeableTimes = position;
        while(currentThisTimes >= unchangeableTimes){
//            System.out.println("\n\n\n轮数: " + (currentThis.size() - currentThisTimes));
            int currentGap = currentThisTimes - unchangeableTimes;
//            System.out.println("现在被减式的阶: " + currentThisTimes);
//            System.out.println("不变的减式的阶: " + unchangeableTimes);
//            System.out.println("相差的阶: " + currentGap);
            ArrayList<FieldElement> middleTerm = new ArrayList<FieldElement>();
            // middleTerm 长度固定
            for(int i = 0; i < thisReform.size(); i++){
                if(i < currentGap || i >= currentGap + bReform.size()){
                    middleTerm.add(thisReform.get(0).GetField().Zero());
                }
                else if(i >= currentGap && i < currentGap + bReform.size()){
                    FieldElement temp1 = bInverse.Mul(bReform.get(i - currentGap)).Div();
//                    System.out.println("Temp1 = " + temp1._str_());
                    FieldElement temp2 = currentThis.get(currentThisTimes).Mul(temp1).Div();
//                    System.out.println("Temp2 = " + temp2._str_());
                    middleTerm.add(temp2);
//                    middleTerm.add( currentThis.get(currentThisTimes).Mul(
//                            bInverse.Mul(bReform.get(i - currentGap)).Div()).Div());
                }
            }
            // 实际上，中间过程的式子，是由 [[除式的每一项]] 乘上 [除式最高项系数的逆] 乘上 [被除式最高项系数] 乘上 [[被除式和除式相差的阶]]// [除式最高项系数的逆] * [被除式最高项系数]

//            System.out.println("现在需要做减法运算的减式: " + new FieldPolynomial(middleTerm)._str_(false));

            quotientResult.set(currentGap, currentThis.get(currentThisTimes).Mul(bInverse).Div().Mod());
//            System.out.println("这一轮上商的情况: " + new FieldPolynomial(quotientResult)._str_(false));

            for(int i = 0; i < currentThis.size(); i++){
                currentThis.set(i, (currentThis.get(i).Sub(middleTerm.get(i))).Mod());
            }
//            System.out.println("现在的新一轮被除式: " + new FieldPolynomial(currentThis)._str_(false));
            currentThis = Normalize(currentThis);
            boolean iszeroFlag = true;
            for(int i = currentThis.size() - 1; i >= 0; --i){
                if(!currentThis.get(i).IsZero()){
                    iszeroFlag = false;
                    break;
                }
            }
            if(iszeroFlag){
                break;
            }
            for(int count = currentThis.size() - 1; count >= 0; count--){
//                System.out.println("currentThis.get(count) = " + currentThis.get(count)._str_());
//                System.out.println("Zero: " + thisReform.get(0).GetField().Zero()._str_());
//                System.out.println("currentThis_Is_Zero: " + currentThis.get(count).IsZero());
                if(!currentThis.get(count).IsZero() && currentThis.get(count) != null){
                    currentThisTimes = count;
                    break;
                }
            }
        }

        FieldPolynomial quotient = new FieldPolynomial(quotientResult);
        FieldPolynomial remainer = new FieldPolynomial(currentThis);
        FieldPolynomial finalResult[] = new FieldPolynomial[]{quotient, remainer};
//        System.out.println("函数内本轮商结果: " + finalResult[0]._str_(false));
//        System.out.println("函数内本轮余数结果: " + finalResult[1]._str_(false));
//        System.out.println("++++++++Leave Div+++++");
        return finalResult;
    }

    public boolean FormLengthIsOne(FieldPolynomial m){
        if(m.v.size() < 1){
            return false;
        }
        else if(m.v.size() == 1){
            return true;
        }
        else{
            for(int i = 1; i < m.v.size(); i++){
                if(!m.v.get(i).Equal(m.v.get(0).GetField().Zero())){
                    return false;
                }
            }
            return true;
        }
    }

    public ArrayList<FieldElement> Normalize(ArrayList<FieldElement> vector){
        if(vector == null || vector.size() == 1){
            return vector;
        }
        else{
            boolean flag = false;
            int countReal = vector.size();
            for(int i = vector.size() - 1; i >= 0; --i){
                if(!vector.get(i).IsZero()){
                    flag = true;
                    countReal = i;
                    break;
                }
            }
            if(flag == false){
                countReal = 0;
            }
            if(countReal < vector.size() - 1){
                ArrayList<FieldElement> newVector = new ArrayList<FieldElement>();
                for(int i = 0; i <= countReal; i++){
                    newVector.add(vector.get(i));
                }
                return newVector;
            }
            else{
                return  vector;
            }
        }
    }

//    public FieldPolynomial ModInv(FieldPolynomial m) throws Exception{
//        System.out.println("Enter ModInv--------------------------------");
//        FieldPolynomial thisReform = this.Div(m)[1];
//        for(int i = 0; i < thisReform.v.size(); i++){
//            thisReform.v.set(i, thisReform.v.get(i).Div());
//        }
//        this.v = Normalize(thisReform.v);
//        System.out.println("*************实际上进行运算的式子: " + this._str_(false));
//
//        ArrayList<FieldElement> arrayOne = new ArrayList<FieldElement>();
//        ArrayList<FieldElement> arrayZero = new ArrayList<FieldElement>();
//        arrayOne.add(this.v.get(0).GetField().One());
//        arrayZero.add(this.v.get(0).GetField().Zero());
//
//        if(m.v.size() == 1 && (m.v.get(0).Equal(m.v.get(0).GetField().Zero()) || m.v.get(0).Equal(m.v.get(0).GetField().One()))){
//            throw new Exception("FieldPolynomial.ModInv: invalid m = " + m.v.get(0));
//        }
//
//        FieldPolynomial u1 = new FieldPolynomial(arrayOne);
//        FieldPolynomial u2 = new FieldPolynomial(arrayZero);
//        FieldPolynomial u3 = new FieldPolynomial(this.v);
//        FieldPolynomial v1 = new FieldPolynomial(arrayZero);
//        FieldPolynomial v2 = new FieldPolynomial(arrayOne);
//        FieldPolynomial v3 = new FieldPolynomial(m.v);
//
//        int i = 0;
//        FieldPolynomial q, r;
//        while(FormLengthIsOne(v3) == false || !v3.v.get(0).Equal(this.v.get(0).GetField().Zero())){
//            FieldPolynomial u1Temp = new FieldPolynomial(Normalize(u1.v));
//            FieldPolynomial u2Temp = new FieldPolynomial(Normalize(u2.v));
//            FieldPolynomial u3Temp = new FieldPolynomial(Normalize(u3.v));
//            FieldPolynomial v1Temp = new FieldPolynomial(Normalize(v1.v));
//            FieldPolynomial v2Temp = new FieldPolynomial(Normalize(v2.v));
//            FieldPolynomial v3Temp = new FieldPolynomial(Normalize(v3.v));
//
//            FieldPolynomial[] resultTemp = u3Temp.Div(v3Temp);
//            ArrayList<FieldElement> qArray = new ArrayList<FieldElement>();
//            ArrayList<FieldElement> rArray = new ArrayList<FieldElement>();
//            for(int k = 0; k < resultTemp[0].v.size(); k++){
//                qArray.add(resultTemp[0].v.get(k).Div());
//            }
//            for(int k = 0; k < resultTemp[1].v.size(); k++){
//                rArray.add(resultTemp[1].v.get(k).Div());
//            }
//
//            q = new FieldPolynomial(Normalize(qArray));
//            r = new FieldPolynomial(Normalize(rArray));
//
//
//            System.out.println("第" + (i + 1) + "轮");    i++;
//            System.out.println("*****************q = " + q._str_(false));
//            System.out.println("*****************r = " + r._str_(false));
//            FieldPolynomial qTemp = new FieldPolynomial(Normalize(q.v));
//            FieldPolynomial rTemp = new FieldPolynomial(Normalize(r.v));
//
//            ArrayList<FieldElement> v1Array = new ArrayList<FieldElement>();
//            for(int k = 0; k < u1Temp.Sub(qTemp.Mul(v1Temp)).v.size(); k++){
//                v1Array.add(u1Temp.Sub(qTemp.Mul(v1Temp)).v.get(k).Div());
//            }
//            v1 = new FieldPolynomial(Normalize(v1Array));
//            ArrayList<FieldElement> v2Array = new ArrayList<FieldElement>();
//            for(int k = 0; k <  u2Temp.Sub(qTemp.Mul(v2Temp)).v.size(); k++){
//                v2Array.add( u2Temp.Sub(qTemp.Mul(v2Temp)).v.get(k).Div());
//            }
//            v2 = new FieldPolynomial(Normalize(v2Array));
////            System.out.println("v1Temp " + v1Temp._str_(false));
////            System.out.println("qTemp.Mul(v1Temp): " + qTemp.Mul(v1Temp)._str_(false));
////            System.out.println("u1Temp : " + u1Temp._str_(false));
////            System.out.println("u1Temp.Sub(qTemp.Mul(v1Temp): " + u1Temp.Sub(qTemp.Mul(v1Temp))._str_(false));
//            // 作修改
//            // v1 = u1Temp.Sub(qTemp.Mul(v1Temp))
//            // v2 = u2Temp.Sub(qTemp.Mul(v2Temp));
//            v3 = rTemp;
//            u1 = v1Temp;
//            u2 = v2Temp;
//            u3 = v3Temp;
//            System.out.println("*******************v1 = " + v1._str_(false));
//            System.out.println("*******************v2 = " + v2._str_(false));
//            System.out.println("*******************v3 = " + v3._str_(false));
//            System.out.println("*******************u1 = " + u1._str_(false));
//            System.out.println("*******************u2 = " + u2._str_(false));
//            System.out.println("*******************u3 = " + u3._str_(false));
//        }
//        System.out.println("!!!!!! important u3 = " + u3._str_(false));
//        if(FormLengthIsOne(u3) == false){
//            throw new Exception("FieldPolynomial::ModInv: gcd not zero");
//        }
//        else if(!u3.v.get(0).Equal(this.v.get(0).GetField().One())){
//            FieldElement u3Inv = u3.v.get(0).Inv();
//            ArrayList<FieldElement> u3Inverse = new ArrayList<FieldElement>();
//            u3Inverse.add(u3Inv);
//            u1 = u1.Mul(new FieldPolynomial(u3Inverse));
//            System.out.println("*************current u1 = " + u1._str_(false));
//        }
//
//        FieldPolynomial[] result = u1.Div(m);
//        q = result[0];
//        r = result[1];
//        System.out.println("****************final q = " + q._str_(false));
//        System.out.println("****************final r = " + r._str_(false));
//        System.out.println("Leave ModInv---------------------------------------------------------");
//        return r;
//    }

    public FieldPolynomial ModInv(FieldPolynomial m) throws Exception{
        FieldPolynomial thisReform = this.Div(m)[1];
        for(int i = 0; i < thisReform.v.size(); i++){
            thisReform.v.set(i, thisReform.v.get(i).Div());
        }
        this.v = Normalize(thisReform.v);

        ArrayList<FieldElement> arrayOne = new ArrayList<FieldElement>();
        ArrayList<FieldElement> arrayZero = new ArrayList<FieldElement>();
        arrayOne.add(this.v.get(0).GetField().One());
        arrayZero.add(this.v.get(0).GetField().Zero());

        if(m.v.size() == 1 && (m.v.get(0).Equal(m.v.get(0).GetField().Zero()) || m.v.get(0).Equal(m.v.get(0).GetField().One()))){
            throw new Exception("FieldPolynomial.ModInv: invalid m = " + m.v.get(0));
        }

        FieldPolynomial u1 = new FieldPolynomial(arrayOne);
        FieldPolynomial u2 = new FieldPolynomial(arrayZero);
        FieldPolynomial u3 = new FieldPolynomial(this.v);
        FieldPolynomial v1 = new FieldPolynomial(arrayZero);
        FieldPolynomial v2 = new FieldPolynomial(arrayOne);
        FieldPolynomial v3 = new FieldPolynomial(m.v);

        int i = 0;
        FieldPolynomial q, r;
        while(FormLengthIsOne(v3) == false || !v3.v.get(0).Equal(this.v.get(0).GetField().Zero())){
            FieldPolynomial u1Temp = new FieldPolynomial(Normalize(u1.v));
            FieldPolynomial u2Temp = new FieldPolynomial(Normalize(u2.v));
            FieldPolynomial u3Temp = new FieldPolynomial(Normalize(u3.v));
            FieldPolynomial v1Temp = new FieldPolynomial(Normalize(v1.v));
            FieldPolynomial v2Temp = new FieldPolynomial(Normalize(v2.v));
            FieldPolynomial v3Temp = new FieldPolynomial(Normalize(v3.v));

            FieldPolynomial[] resultTemp = u3Temp.Div(v3Temp);
            ArrayList<FieldElement> qArray = new ArrayList<FieldElement>();
            ArrayList<FieldElement> rArray = new ArrayList<FieldElement>();
            for(int k = 0; k < resultTemp[0].v.size(); k++){
                qArray.add(resultTemp[0].v.get(k).Div());
            }
            for(int k = 0; k < resultTemp[1].v.size(); k++){
                rArray.add(resultTemp[1].v.get(k).Div());
            }

            q = new FieldPolynomial(Normalize(qArray));
            r = new FieldPolynomial(Normalize(rArray));

            FieldPolynomial qTemp = new FieldPolynomial(Normalize(q.v));
            FieldPolynomial rTemp = new FieldPolynomial(Normalize(r.v));

            ArrayList<FieldElement> v1Array = new ArrayList<FieldElement>();
            for(int k = 0; k < u1Temp.Sub(qTemp.Mul(v1Temp)).v.size(); k++){
                v1Array.add(u1Temp.Sub(qTemp.Mul(v1Temp)).v.get(k).Div());
            }
            v1 = new FieldPolynomial(Normalize(v1Array));
            ArrayList<FieldElement> v2Array = new ArrayList<FieldElement>();
            for(int k = 0; k <  u2Temp.Sub(qTemp.Mul(v2Temp)).v.size(); k++){
                v2Array.add( u2Temp.Sub(qTemp.Mul(v2Temp)).v.get(k).Div());
            }
            v2 = new FieldPolynomial(Normalize(v2Array));
//            System.out.println("v1Temp " + v1Temp._str_(false));
//            System.out.println("qTemp.Mul(v1Temp): " + qTemp.Mul(v1Temp)._str_(false));
//            System.out.println("u1Temp : " + u1Temp._str_(false));
//            System.out.println("u1Temp.Sub(qTemp.Mul(v1Temp): " + u1Temp.Sub(qTemp.Mul(v1Temp))._str_(false));
            // 作修改
            // v1 = u1Temp.Sub(qTemp.Mul(v1Temp))
            // v2 = u2Temp.Sub(qTemp.Mul(v2Temp));
            v3 = rTemp;
            u1 = v1Temp;
            u2 = v2Temp;
            u3 = v3Temp;
        }

        if(FormLengthIsOne(u3) == false){
            throw new Exception("FieldPolynomial::ModInv: gcd not zero");
        }
        else if(!u3.v.get(0).Equal(this.v.get(0).GetField().One())){
            FieldElement u3Inv = u3.v.get(0).Inv();
            ArrayList<FieldElement> u3Inverse = new ArrayList<FieldElement>();
            u3Inverse.add(u3Inv);
            u1 = u1.Mul(new FieldPolynomial(u3Inverse));
        }

        FieldPolynomial[] result = u1.Div(m);
        q = result[0];
        r = result[1];
        return r;
    }
}