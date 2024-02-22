package field.compute;

import java.math.BigInteger;
import java.util.ArrayList;

public interface Field {
    BigInteger Prime();
    Field BaseField();
    ArrayList<FieldElement> IrreduciblePolynomial() throws Exception;
    FieldElement One();
    FieldElement Zero();
    FieldElement FromNum(BigInteger x);
    FieldElement FromVector(ArrayList<FieldElement> v) throws Exception;
}