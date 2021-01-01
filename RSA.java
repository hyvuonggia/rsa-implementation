import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class RSA {
    static final BigInteger MINUS_ONE = new BigInteger("-1");
    static final BigInteger ZERO = new BigInteger("0");
    static final BigInteger ONE = new BigInteger("1");
    static final BigInteger TWO = new BigInteger("2");

    public static int calculateS(BigInteger number) {
        int s = 0;
        number = number.subtract(ONE);
        while (number.mod(TWO).equals(ZERO)) {
            number = number.divide(TWO);
            s += 1;
        }
        return s;
    }

    public static boolean MR(BigInteger number, int numTest) {
        int s;
        BigInteger d;
        s = calculateS(number);
        d = number.subtract(ONE).divide(TWO.pow(s));
        if (!isOdd(number)) //any even numbers > 2 are false
            return false;
        else
            Loop:for (int i = 0; i < numTest; i++) { // repeat k time
                BigInteger a = new BigInteger(64, new Random());
                if (FME(a, d, number).equals(ONE) || FME(a, d, number).equals(number.subtract(ONE)))
                    continue Loop; //prime

                for (int r = 1; r < s - 1; r++)
                    if (FME(a, TWO.pow(r).multiply(d), number).equals(number.subtract(ONE)))
                        continue Loop; //prime
                return false; //composite
            }
        return true; //prime
    }

    public static boolean isOdd(BigInteger number) {
        if (number.mod(TWO).equals(ONE))
            return true;
        else
            return false;
    }

    public static BigInteger FME(BigInteger base, BigInteger pow, BigInteger modulo) {
        BigInteger res;
        if (pow.equals(ONE))
            return base.mod(modulo);
        else if (isOdd(pow))  //pow is odd
            return (base.mod(modulo).multiply(FME(base, pow.subtract(ONE), modulo))).mod(modulo);
        else // pow is even
            res = FME(base, pow.divide(TWO), modulo);
        return (res.multiply(res).mod(modulo));
    }

    public static BigInteger EA(BigInteger num1, BigInteger num2) {
        BigInteger r;
        while (num2.compareTo(ZERO) > 0) {
            r = num1.mod(num2);
            num1 = num2;
            num2 = r;
        }
        return num1;
    }

    public static BigInteger EEA(BigInteger num1, BigInteger num2) {
        BigInteger r;
        BigInteger q;
        BigInteger[] xk = new BigInteger[2];
        BigInteger sign;
        BigInteger xx;
        BigInteger x;

        xk[0] = ONE;
        xk[1] = ZERO;
        sign = ONE;

        while (num2.compareTo(ZERO) > 0) {
            r = num1.mod(num2);
            q = num1.divide(num2);
            num1 = num2;
            num2 = r;

            xx = xk[1];
            xk[1] = q.multiply(xk[1]).add(xk[0]);
            xk[0] = xx;
            sign = MINUS_ONE.multiply(sign);
        }
        x = sign.multiply(xk[0]);
        return x;
    }

    public static void main(String[] args) {
        BigInteger p;
        BigInteger q;
        BigInteger n;
        BigInteger phiN;
        BigInteger e = TWO.add(ONE);
        BigInteger d;
        BigInteger m;
        BigInteger c;

        // Generate random large prime number for p
        do {
            p = new BigInteger(512, new Random());
        } while (!(MR(p, 10)));

        // Generate random large prime number for q
        do {
            q = new BigInteger(512, new Random());
        } while (!(MR(q, 10)));

        // n = p * q
        n = p.multiply(q);

        //Calculate phiN
        phiN = p.subtract(ONE).multiply(q.subtract(ONE));

        //Find e
        while (EA(e, phiN).compareTo(ONE) > 0 && e.compareTo(phiN) < 0) {
            e = e.add(TWO);
        }

        // d is the inverse of e mod phiN
        if (EEA(e, phiN).compareTo(ZERO) < 0)
            d = EEA(e, phiN).add(phiN);
        else
            d = EEA(e, phiN);

        System.out.println("p:    " + p);
        System.out.println("q:    " + q);
        System.out.println("n:    " + n);
        System.out.println("phiN: " + phiN);
        System.out.println("e:    " + e);
        System.out.println("d:    " + d);
        System.out.println();

        System.out.println("1 : ENCRYPT");
        System.out.println("2 : DECRYPT");
        System.out.println("0 : END");
        System.out.print("Choose your option: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        while (choice != 0) {
            if (choice == 1) {
                System.out.print("Input message: ");
                m = scanner.nextBigInteger();
                c = FME(m, e, n);
                System.out.println("Cipher text: " + c);
                System.out.println();
            }
            if (choice == 2) {
                System.out.print("Input cipher text: ");
                c = scanner.nextBigInteger();
                m = FME(c, d, n);
                System.out.println("Message: " + m);
                System.out.println();
            }
            System.out.println("1 : ENCRYPT");
            System.out.println("2 : DECRYPT");
            System.out.println("0 : END");
            System.out.print("Choose your option: ");
            choice = scanner.nextInt();
        }
    }
}
