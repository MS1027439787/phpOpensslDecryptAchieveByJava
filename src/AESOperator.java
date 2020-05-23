import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;


/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 */
public class AESOperator {

    /*
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private String sKey = "vow";//key，可自行修改
    private String ivParameter = "answernok1234567";//偏移量,可自行修改
    private static AESOperator instance = null;

    public static void main(String[] args) throws Exception {
//        //原始密文文件名
//        String secretPath = "E:\\a.txt";
//        //写入文件名
//        String afterPath = "E:\\b.txt";
//        List<String> result = new ArrayList<>();
//        //文件读取
//        List<String> oriList = readFromTextFile(secretPath);
//        //解密
//        for(String tmp : oriList){
//            result.add(AESOperator.getInstance().decrypt(URLDecoder.decode(tmp, "GBK").replace("_", "-").replace("/", "+")));
//        }
//        //写文件
//        writeToTextFile(result, afterPath);

        //原始密文
        String ori = "aZRLhWEEVyRYpKIZaxamvw%3D%3D";
        // 1、先进行urldecode
        String str1 = URLDecoder.decode(ori, "GBK");
        System.out.println("第一步：" + str1);

        //2、然后进行字符串替换，替换规则：_替换为-号，/替换为+号
        String str2 = str1.replace("_", "-").replace("/", "+");
        System.out.println("第二步：" + str2);

        //3、最后解密
        String str3 = AESOperator.getInstance().decrypt(str2);
        System.out.println("第三步：" + str3);

//        // 需要加密的字串
//        String ori2 = "123456";
//
//        // 加密
//        String enString = AESOperator.getInstance().encrypt(ori);
//        System.out.println("加密后的字串是：" + enString);
//
//        // 解密
//        String DeString = AESOperator.getInstance().decrypt(enString);
//        System.out.println("解密后的字串是：" + DeString);

    }

    private AESOperator() {

    }



    public static AESOperator getInstance() {
        if (instance == null)
            instance = new AESOperator();
        return instance;
    }

    public static String Encrypt(String encData ,String secretKey,String vector) throws Exception {

        if(secretKey == null) {
            return null;
        }
        if(secretKey.length() != 16) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = secretKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
        return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
    }


    // 加密
    public String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = new byte[16];
        for (int i = 0; i < 16; i++) {
            if (i < sKey.getBytes().length) {
                raw[i] = sKey.getBytes()[i];
            } else {
                raw[i] = 0;
            }
        }
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
    }

    // 解密
    public String decrypt(String sSrc) throws Exception {
        try {
            byte[] raw = new byte[16];
            for (int i = 0; i < 16; i++) {
                if (i < sKey.getBytes("ASCII").length) {
                    raw[i] = sKey.getBytes("ASCII")[i];
                } else {
                    raw[i] = 0;
                }
            }
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public String decrypt(String sSrc,String key,String ivs) throws Exception {
        try {
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }


    /**
     * 按行读取文件
     */
    public static List<String> readFromTextFile(String pathname) throws IOException {
        List<String> strArray = new ArrayList<String>();
        File filename = new File(pathname);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        line = br.readLine();
        while(line != null) {
            strArray.add(line);
            line = br.readLine();
        }
        return strArray;
    }

    public static void writeToTextFile(List<String> list, String path){
        //需要写入的文件名称
        String linuxDataPath = path;
        try{
            File file = new File(linuxDataPath);
            FileOutputStream fos = null;
            if(!file.exists()){
                file.createNewFile();//如果文件不存在，就创建该文件
                fos = new FileOutputStream(file);//首次写入获取
            }else{
                //如果文件已存在，那么就在文件末尾追加写入
                fos = new FileOutputStream(file,true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
            }

            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件

            for(String str : list){
                //LoggerUtil.info("Json字符串：" + str);
                osw.write(str);
                osw.write("\r\n");
            }
            //写入完成关闭流
            osw.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
