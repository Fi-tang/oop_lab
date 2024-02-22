package field.compute;

import java.math.BigInteger;
import java.util.ArrayList;

public class ExtField implements Field {
    Field F;    // 在哪个域上扩张
    public ArrayList<FieldElement> ir = new ArrayList<FieldElement>();
    FieldElement one;
    FieldElement zero;

    public ExtField(Field F, ArrayList<FieldElement> ir){
        this.F = F;
        this.ir = ir;
        ArrayList<FieldElement> oneValue = new ArrayList<FieldElement>();
        oneValue.add(this.ir.get(0).GetField().One());
        // [0], 补一个零

        ArrayList<FieldElement> zeroValue = new ArrayList<FieldElement>();
        zeroValue.add(this.ir.get(0).GetField().Zero());

        one = new ExtFieldElement(this, oneValue);
        zero = new ExtFieldElement(this, zeroValue);
    }

    public BigInteger Prime(){
        return this.F.Prime();
    }

    // 域构造中基于的域！ pf2 对应 pf, pf4 对应 pf2, pf12 对应 pf4
    public Field BaseField(){
        return this.F;
    }

    public ArrayList<FieldElement> IrreduciblePolynomial() throws Exception{
        return this.ir;
    }

    public FieldElement One(){
        return this.one;
    }

    public FieldElement Zero(){
        return this.zero;
    }
    public FieldElement FromNum(BigInteger x){
        ArrayList<FieldElement> v = new ArrayList<FieldElement>();
        v.add(this.F.FromNum(x));
        // 只对于 Fp -> Fp2 有效，后续补异常
        return new ExtFieldElement(this, v);
    }

    public FieldElement FromVector(ArrayList<FieldElement> v) throws Exception{
        return new ExtFieldElement(this, v);
    }
}