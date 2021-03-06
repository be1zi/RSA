import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

public class RSA {

    public static void main(String[] args) {

       RSA rsa = new RSA();

       List<BigInteger> encryptedImage = rsa.encryptImage();
       List<BigInteger> decryptedImage = rsa.decryptImage(encryptedImage);

       rsa.createImage(decryptedImage, "decrypted");
    }

    private BigInteger p, q, n, fi, e, d;
    private String imgPath;
    private BufferedImage img = null;
    private List<BigInteger> blocksList;
    private String format;
    private byte[] imgBytes = null;

    public RSA () {
        this("Pikachu.jpg", "jpg");
    }

    public RSA(String imgPath) {
        this(imgPath, "jpg");
    }

    public RSA(String imgPath, String imgFormat) {
        this.imgPath = imgPath;
        this.format = imgFormat;
        this.mainFunction();
    }

    private void mainFunction() {

        System.out.println("\nData is being prepared");

        if (!loadFile()) {
            return;
        }

        p = createRandomBigIntegerWithLength(1024);
        q = createRandomBigIntegerWithLength(1024);
        n = p.multiply(q);
        fi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = createEValue();
        d = createDValue();
        imgBytes = createByteArrayForImage();
        blocksList = createBlocksList();

        System.out.println("Data was correctly prepared");

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
        byte[] tmpArray;

        if (imgBytes.length == 0) {
            return null;
        }

        for (int i = 0; i<imgBytes.length; i+=255) {
            int length = Math.min(255, imgBytes.length - i);
            tmpArray = new byte[length];
            System.arraycopy(imgBytes, i, tmpArray, 0, length);
            list.add(new BigInteger(1, tmpArray));
        }

        return list;
    }

    private byte[] createByteArrayForImage() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result = null;

        try {
            ImageIO.write(img, format, baos);
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
            File file = new File(imageName + "." + format);
            try {
                ImageIO.write(bi, format, file);
                System.out.println("Image " + imageName + "." + format + " was successfully created.");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    // Parameters needed to encrypt and decrypt

    private BigInteger createRandomBigIntegerWithLength (int length) {
        SecureRandom random = new SecureRandom();
        BigInteger number = BigInteger.probablePrime(length, random);

        return number;
    }

    private BigInteger createEValue () {

        Random r = new Random();
        BigInteger e = createRandomBigIntegerWithLength(r.nextInt(1023) + 1);

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
        System.out.println("Encryption started");

        List<BigInteger> result = new ArrayList<>();

        for (BigInteger bi : blocksList) {
            result.add(bi.modPow(e, n));
        }

        return result;
    }

    public List<BigInteger> decryptImage (List<BigInteger> encryptedImage) {
        System.out.println("Decryption started");

        List<BigInteger> result = new ArrayList<>();

        for (BigInteger bi : encryptedImage) {
            result.add(bi.modPow(d, n));
        }

        return result;
    }

    //Helpers

    private byte[] changeBigIntegersIntoBytes (List<BigInteger> list) {
        byte[] result = new byte[0];

        for (BigInteger bi : list) {
            byte[] array = bi.toByteArray();

            if (array.length != 255 && array[0] == 0) {
                byte[] tmp = new byte[array.length - 1];
                System.arraycopy(array, 1, tmp, 0, tmp.length);
                array = tmp;
            }

            byte[] tmpArray = new byte[result.length + array.length];
            System.arraycopy(result, 0, tmpArray, 0, result.length);
            System.arraycopy(array, 0, tmpArray, result.length, array.length);
            result = tmpArray;
        }

        return result;
    }
}
