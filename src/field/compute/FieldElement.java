package field.compute;

import java.math.BigInteger;
import java.util.ArrayList;

public interface FieldElement {
    Field GetField();
    FieldElement Add(FieldElement a) throws Exception;
    FieldElement Sub(FieldElement b) throws Exception;
    FieldElement Mul(FieldElement p) throws Exception;
    FieldElement Mod() throws Exception;
    FieldElement Div() throws Exception;     // 仅用作规范化，实际上只有 FieldPolynomial 中的 Div() 有实际除法作用
    FieldElement Pow(int k) throws Exception;
    FieldElement Inv() throws Exception;
    boolean IsZero();
    boolean IsOne();
    boolean Equal(BigInteger x);
    boolean Equal(FieldElement e);
    String _str_();
}