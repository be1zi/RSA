import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import javax.imageio.ImageIO;

public class RSA {

    public static void main(String[] args) {

        new RSA();
    }

    private BigInteger p,q,n, fi,e, d;
    private String imgPath;
    BufferedImage img = null;

    public RSA(String imgPath) {
        this.imgPath = imgPath;
        this.mainFunction();
    }

    public RSA () {
        this("rsa.jpg");
    }

    private void mainFunction() {

        if (!loadFile()) {
            return;
        }

        p = createRandomBigInteger();
        q = createRandomBigInteger();
        n = p.multiply(q);
        fi = p.subtract(BigInteger.ONE).multiply( q.subtract(BigInteger.ONE));
        e = createEValue();
        d = createDValue();
    }

    private boolean loadFile() {
        try {
            img = ImageIO.read(new File(imgPath));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    private BigInteger createRandomBigInteger () {
        SecureRandom random = new SecureRandom();
        BigInteger number = BigInteger.probablePrime(1024, random);

        return number;
    }

    private BigInteger createEValue () {
        BigInteger e = createRandomBigInteger();

        if (e.gcd(p).intValue() != 1) {
            return createEValue();
        }

        return e;
    }

    private BigInteger createDValue () {
        BigInteger d = e.modInverse(q);

        return d;
    }
}
