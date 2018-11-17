import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class RSA {

    public static void main(String[] args) {

        new RSA();
    }

    private BigInteger p,q,n, fi,e, d;
    private String imgPath;
    private BufferedImage img = null;
    private List<BigInteger> blocksList;

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
        fi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = createEValue();
        d = createDValue();
        blocksList = createBlocksList();
    }

    // FILE

    private boolean loadFile() {
        try {
            img = ImageIO.read(new File(imgPath));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private List<BigInteger> createBlocksList() {
        List<BigInteger> list = new ArrayList<>();
        byte[] byteArray = createByteArrayForImage();
        BigInteger bigInteger = BigInteger.ZERO;

        if (byteArray.length == 0) {
            return null;
        }

        for (byte singleByte : byteArray) {
            BigInteger newValue = bigInteger.add(new BigInteger(String.valueOf(singleByte & 0xff)));

            if (newValue.compareTo(n) >= 0) {
                list.add(bigInteger);
                bigInteger = BigInteger.ZERO;
                bigInteger =  bigInteger.add(new BigInteger(String.valueOf(singleByte & 0xff)));
            } else {
                bigInteger = newValue;
            }
        }

        list.add(bigInteger);

        return list;
    }

    private byte[] createByteArrayForImage() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result = null;

        try {
            ImageIO.write(img, "jpg", baos);
            baos.flush();
            result = baos.toByteArray();
            baos.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return result;
    }

    // Parameters needed to encrypt and decrypt

    private BigInteger createRandomBigInteger () {
        SecureRandom random = new SecureRandom();
        BigInteger number = BigInteger.probablePrime(1024, random);

        return number;
    }

    private BigInteger createEValue () {
        BigInteger e = createRandomBigInteger();

        if (e.compareTo(q) >= 0) {
            return createEValue();
        }

        if (e.gcd(p).intValue() != 1) {
            return createEValue();
        }

        return e;
    }

    private BigInteger createDValue () {
        BigInteger d = e.modInverse(q);

        return d;
    }

    // Encrypt and Decrypt
}
