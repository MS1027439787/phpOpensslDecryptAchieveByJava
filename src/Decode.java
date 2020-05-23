import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author masai
 * @date 2020/5/22
 */
public class Decode {
    public static void main(String[] args) {
        try {
            //原始密文
            String ori = "5CIG7spcC2sE9ddwtbfZkA%3D%3D";
            // 1、先进行urldecode
            String str1 = URLDecoder.decode(ori, "GBK");
            System.out.println("第一步：" + str1);

            //2、然后进行字符串替换，替换规则：_替换为-号，/替换为+号
            String str2 = str1.replace("_", "-").replace("/", "+");
            System.out.println("第二步：" + str2);

            //3、再进行urlencode
            String str3 = URLEncoder.encode(str2, "GBK");
            System.out.println("第三步：" + str3);

            //4、url decode
            String str4 = URLDecoder.decode(str3, "GBK");
            System.out.println("第四步：" + str4);

            //5、base64 decode
            Base64.Decoder decoder = Base64.getDecoder();
            String str5 = new String(decoder.decode(str4), "UTF-8");
            System.out.println("第五步：" + str5);

            //6、最后解密
            String str6 = decryptUsingKey(str5, "vow" ,"answernok1234567" );
            System.out.println("第六步：" + str6);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String decryptUsingKey(String strToDecrypt, String secret, String iv) {
        try {
            String salt = "test123";
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt( String input,  String key){
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes = decoder.decode(input);
        if(bytes.length < 17) {
            return null;
        }

        byte[] ivBytes = Arrays.copyOfRange(bytes, 0, 16);
        byte[] contentBytes = Arrays.copyOfRange(bytes, 16, bytes.length);


        try {
            Cipher ciper = Cipher.getInstance("AES/CBC/PKCS5Padding");

            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"),"AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes,0, ciper.getBlockSize());

            ciper.init(Cipher.DECRYPT_MODE, keySpec, iv);
            return new String(ciper.doFinal(contentBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
