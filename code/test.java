/**
 * The HelloWorldApp class implements an application that
 * simply displays "Hello World!" to the standard output.
 */


import java.math.BigInteger;

// class Factorial {
//   //factorial implementation
//   BigInteger 
// 
// }





class test {
    public static BigInteger factorial(BigInteger n) {
    BigInteger result = BigInteger.ONE;

    while (!n.equals(BigInteger.ZERO)) {
        result = result.multiply(n);
        n = n.subtract(BigInteger.ONE);
    }

    return result;
}

    public static void main(String[] args) {
        System.out.println("Hello World!"); //Display the string.
        System.out.println(factorial(new BigInteger("52")).toString());
        System.out.println(factorial(new BigInteger("3")).toString());
    }
}