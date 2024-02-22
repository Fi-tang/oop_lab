package field.compute;

import java.math.BigInteger;
import java.util.ArrayList;

public class PrimeField implements Field{
    public BigInteger P;
    public FieldElement one;
    public FieldElement zero;

    public PrimeField(BigInteger P){
        this.P = P;
        this.one = new PrimeFieldElement(this, BigInteger.valueOf(1));
        this.zero = new PrimeFieldElement(this, BigInteger.valueOf(0));
    }

    public BigInteger Prime(){
        return this.P;
    }

    public Field BaseField(){
        return this;
    }

    public ArrayList<FieldElement> IrreduciblePolynomial() throws Exception{
        throw new Exception("PrimeField do not have IrreduciblePolynomial!");
    }

    public FieldElement One(){
        return this.one;
    }

    public FieldElement Zero(){
        return this.zero;
    }

    public PrimeFieldElement FromNum(BigInteger x){
        return new PrimeFieldElement(this, x.mod(this.P));
    }

    public FieldElement FromVector(ArrayList<FieldElement> v) throws Exception{
        throw new Exception("PrimeFieldElement does not need Vector!");
    }
}