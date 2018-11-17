import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class RSA {

    public static void main(String[] args) {

       RSA rsa = new RSA();

       List<BigInteger> encryptedImage = rsa.encryptImage();
       rsa.createImage(encryptedImage, "encrypted");

       List<BigInteger> decryptedImage = rsa.decryptImage(encryptedImage);
       rsa.createImage(decryptedImage, "decrypted");
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
            BigInteger newValue = bigInteger.add(new BigInteger(String.valueOf(singleByte)));

            if (newValue.compareTo(n) >= 0) {
                list.add(bigInteger);
                bigInteger = BigInteger.ZERO;
                bigInteger =  bigInteger.add(new BigInteger(String.valueOf(singleByte)));
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

    public void createImage(List<BigInteger> list, String imageName) {
        byte[] bytes = changeBigIntegersIntoBytes(list);
        BufferedImage bi = null;

        if (bytes.length == 0) {
            return;
        }

        try {
            InputStream in = new ByteArrayInputStream(bytes);
            bi = ImageIO.read(in);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (bi != null) {
            File file = new File(imageName + ".jpg");
            try {
                ImageIO.write(bi, "jpg", file);
                System.out.println("Image " + imageName + ".jpg" + " was successfully created.");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    // Parameters needed to encrypt and decrypt

    private BigInteger createRandomBigInteger () {
        SecureRandom random = new SecureRandom();
        BigInteger number = BigInteger.probablePrime(1024, random);

        return number;
    }

    private BigInteger createEValue () {
        BigInteger e = createRandomBigInteger();

        if (e.compareTo(fi) >= 0) {
            return createEValue();
        }

        if (e.gcd(fi).intValue() != 1) {
            return createEValue();
        }

        return e;
    }

    private BigInteger createDValue () {
        BigInteger d = e.modInverse(fi);

        return d;
    }

    // Encrypt and Decrypt

    public List<BigInteger> encryptImage() {
        List<BigInteger> result = new ArrayList<>();

        for (BigInteger bi : blocksList) {
            result.add(bi.modPow(e, n));
        }

        return result;
    }

    public List<BigInteger> decryptImage (List<BigInteger> encryptedImage) {
        List<BigInteger> result = new ArrayList<>();

        for (BigInteger bi : encryptedImage) {
            result.add(bi.modPow(d, n));
        }

        return result;
    }

    private byte[] changeBigIntegersIntoBytes (List<BigInteger> list) {
        byte[] result = new byte[0];


        for (BigInteger bi : list) {
            byte[] array = bi.toByteArray();

            if (array[0] == 0) {
                byte[] tmp = new byte[array.length - 1];
                System.arraycopy(array, 1, tmp, 0, tmp.length);
                array = tmp;
            }

            byte[] tmpArray = new byte[result.length + array.length];
            System.arraycopy(array, 0, tmpArray, result.length, array.length);
            result = tmpArray;
        }

        return result;
    }
}
