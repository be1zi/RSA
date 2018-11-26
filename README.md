# RSA
A class used to encrypt and decrypt images with the RSA cipher

##
**Usage**

To init RSA algorithm create RSA object. As a parameter you can set:  
    1) nothing - then your image to decrypt must have name Pikachu, format jpg and be placed in main project directory  
    2) Path to image  
    3) Path to image + image format  
    
```
    //1)
       RSA rsa = new RSA();

    //2)
       RSA rsa = new RSA("Pikachu.jpg");

    //3)
       RSA rsa = new RSA("Pikachu.jpg", "jpg");

```

Call **encryptImage()** method to get list of encrypted BigIntegers. Each element of the list is build from encrypted bytes of image.

```
       List<BigInteger> encryptedImage = rsa.encryptImage();
```

If you have encrypted values of image blocks saved in BigIntegers list you can call **decryptImage()** method to decrypt. Send to this method list of encrypted BigIntegers blocks.
To generate image from decrypted values you could call **createImage()** method with list of decrypted BigIntegers and input file name as a parameters.

```
       List<BigInteger> decryptedImage = rsa.decryptImage(encryptedImage);

       rsa.createImage(decryptedImage, "decrypted");
```
