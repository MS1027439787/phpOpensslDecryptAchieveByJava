import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author masai
 * @date 2020/5/22
 */
public class Encode {
    public static void main(String[] args) {
        try{
            String ori = "123456";
            String str1 = java_openssl_encrypt(ori, "vow", "answernok1234567");
            System.out.println(str1);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static String java_openssl_encrypt(String data, String password, String iv) throws Exception {
        byte[] key = new byte[32];
        for (int i = 0; i < 32; i++) {
            if (i < password.getBytes().length) {
                key[i] = password.getBytes()[i];
            } else {
                key[i] = 0;
            }
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"),
                new IvParameterSpec(iv.getBytes()));

        String base64Str = Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));


        return base64Str;
    }

}
