package hixing.contacts.uitl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @TODO todo
 * Created by hiXing.
 * Date from 2016/5/31 .
 * MyContacts
 */
public class HttpUtil {
        URL url;
        HttpURLConnection conn;
        String boundary = "--------httppost123";
        Map<String, String> textParams = new HashMap<String, String>();
        Map<String, File> fileparams = new HashMap<String, File>();
        DataOutputStream ds;

        public HttpUtil(String url) throws Exception {
            this.url = new URL(url);
        }
        //重新设置要请求的服务器地址，即上传文件的地址。
        public void setUrl(String url) throws Exception {
            this.url = new URL(url);
        }
        //增加一个普通字符串数据到form表单数据中
        public void addTextParameter(String name, String value) {
            textParams.put(name, value);
        }
        //增加一个文件到form表单数据中
        public void addFileParameter(String name, File value) {
            fileparams.put(name, value);
        }
        // 清空所有已添加的form表单数据
        public void clearAllParameters() {
            textParams.clear();
            fileparams.clear();
        }
        // 发送数据到服务器，返回一个字节包含服务器的返回结果的数组
        public byte[] send() throws Exception {
            initConnection();
            try {
                conn.connect();
            } catch (SocketTimeoutException e) {
                // something
                throw new RuntimeException();
            }
            ds = new DataOutputStream(conn.getOutputStream());
            writeFileParams();
            writeStringParams();
            paramsEnd();
            InputStream in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            conn.disconnect();
            return out.toByteArray();
        }
        //文件上传的connection的一些必须设置
        private void initConnection() throws Exception {
            conn = (HttpURLConnection) this.url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000); //连接超时为10秒
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
        }
        //普通字符串数据
        private void writeStringParams() throws Exception {
            Set<String> keySet = textParams.keySet();
            for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
                String name = it.next();
                String value = textParams.get(name);
                ds.writeBytes("--" + boundary + "\r\n");
                ds.writeBytes("Content-Disposition: form-data; name=\"" + name
                        + "\"\r\n");
                ds.writeBytes("\r\n");
                ds.writeBytes(encode(value) + "\r\n");
            }
        }
        //文件数据
        private void writeFileParams() throws Exception {
            Set<String> keySet = fileparams.keySet();
            for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
                String name = it.next();
                File value = fileparams.get(name);
                ds.writeBytes("--" + boundary + "\r\n");
                ds.writeBytes("Content-Disposition: form-data; name=\"" + name
                        + "\"; filename=\"" + encode(value.getName()) + "\"\r\n");
                ds.writeBytes("Content-Type: " + getContentType(value) + "\r\n");
                ds.writeBytes("\r\n");
                ds.write(getBytes(value));
                ds.writeBytes("\r\n");
            }
        }
        //获取文件的上传类型，图片格式为image/png,image/jpg等。非图片为application/octet-stream
        private String getContentType(File f) throws Exception {

            return "application/octet-stream";  // 此行不再细分是否为图片，全部作为application/octet-stream 类型
//            ImageInputStream imagein = ImageIO.createImageInputStream(f);
//            if (imagein == null) {
//                return "application/octet-stream";
//            }
//            Iterator<ImageReader> it = ImageIO.getImageReaders(imagein);
//            if (!it.hasNext()) {
//                imagein.close();
//                return "application/octet-stream";
//            }
//            imagein.close();
//            return "image/" + it.next().getFormatName().toLowerCase();//将FormatName返回的值转换成小写，默认为大写

        }
        //把文件转换成字节数组
        private byte[] getBytes(File f) throws Exception {
            FileInputStream in = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            return out.toByteArray();
        }
        //添加结尾数据
        private void paramsEnd() throws Exception {
            ds.writeBytes("--" + boundary + "--" + "\r\n");
            ds.writeBytes("\r\n");
        }
        // 对包含中文的字符串进行转码，此为UTF-8。服务器那边要进行一次解码
        private String encode(String value) throws Exception{
            return URLEncoder.encode(value, "UTF-8");
        }


        public static void main(String[] args) throws Exception {
            HttpUtil u = new HttpUtil("http://tryworld.cn/index.php/Home/Appapi/upload");
            u.addFileParameter("uploadedfile", new File(
                    "E:\\A_GitHub\\MyContacts\\contact.html"));
            u.addTextParameter("usertel", "13323456789");
            byte[] b = u.send();
            String result = new String(b);
            System.out.println(result);

        }

    public static String sendFile(String urlPath, String filePath,
                                  String newName,CustomFileInputStream.OnUploadListener listener) throws Exception {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";

        URL url = new URL(urlPath);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
        // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
        con.setChunkedStreamingMode(128 * 1024);// 128K
        //下载需要将setDoInput方法的参数值设为true
        con.setDoInput(true);
        //上传需要将setDoOutput方法的参数值设为true
        con.setDoOutput(true);
        //禁止HttpURLConnection使用缓存
        con.setUseCaches(false);
        //使用POST请求，必须大写
        con.setRequestMethod("POST");
        //以下三行设置http请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        //在模拟web页面向服务器端上传文件时，每个文件的开头需要有一个分界符，
        //分界符需要在http请求头中指定。boundary是任意一个字符串，一般为******
        con.setRequestProperty("Content-Type", "multipart/form-data;boundary="
                + boundary);

        DataOutputStream ds = new DataOutputStream(con.getOutputStream());

        ds.writeBytes(twoHyphens + boundary + end);
        //上传文件相关信息，这些信息包括请求参数名，上传文件名，文件类型，但并不限于此
        ds.writeBytes("Content-Disposition: form-data; "
                + "name=\"file1\";filename=\"" + newName + "\"" + end);
        ds.writeBytes(end);

        //获得文件的输入流，通过流传输文件。在这里我重写了FileInputStream，为了监听上传进度
        CustomFileInputStream fStream = new CustomFileInputStream(filePath);
        fStream.setOnUploadListener(listener);
        /* 设置每次写入1024bytes */
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int length = -1;
        // 从文件读取数据至缓冲区
        while ((length = fStream.read(buffer)) != -1) {
            //将资料写入DataOutputStream中
            ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

        fStream.close();
        ds.flush();

        //上传完成以后获取服务器的反馈
        InputStream is = con.getInputStream();
        int ch;
        StringBuffer b = new StringBuffer();
        while ((ch = is.read()) != -1) {
            b.append((char) ch);
        }

        ds.close();
        is.close();
        return b.toString();
    }

}
