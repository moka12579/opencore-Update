import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;



public class ocUpdate {
    static String path2 = System.getProperty("java.class.path");
    static File path =new File(path2);
    static String a= path2.replace(path.getName(),"");
    //主函数
    public static void main(String[] args) throws Exception{
        List<String> files = new ArrayList<String>();
        System.out.println("path = " + path.getName());
        System.out.println("a = " + a);
        File file = new File(a+"old/EFI");
        System.out.println("file = " + String.valueOf(file));
        if(file.isDirectory()) {
            File[] tempList = file.listFiles();
            System.out.println("tempList = " + tempList);
            String str1 = a+ "old/EFI/OC";
            String str3 = a+ "new";
            System.out.println("str1 = " + str1);
            System.out.println("tempList = " + Arrays.toString(tempList));
            boolean flag=true;
            for (int i = 0; i < tempList.length; i++) {
                String str2 = String.valueOf(tempList[i]);
                System.out.println("str2 = " + str2);
                String str4="/EFI/OC";
                if (str2.contains(str4)){
                    update(str3);
                    flag=true;
                }else {
                    flag=false;
                }
            }
            if (!flag){
                throw new Exception("请确认这是一个OC引导程序");
            }
        }
        else {
            throw new Exception("请确认EFI文件夹已经放入");
        }

    }
    //下载新版efi
    public static void update(String path){
        String fileUrl="https://gitee.com/moka123/OpenCorePkg/attach_files/798330/download/OpenCore-0.7.2-RELEASE.zip";
        try {
            downLoadEFI(fileUrl,path,"OpenCore-0.7.2-RELEASE.zip");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void downLoad(String urlStr, String savePath, String fileName) throws Exception {
        String str1=savePath+"/"+fileName;
        System.out.println(str1);
        File file=new File(str1);
        //判断文件是否存在，不存在则创建文件
        if(!file.exists()){
            file.createNewFile();
        }
        URL url = new URL(urlStr);
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.setConnectTimeout(6000);
        urlCon.setReadTimeout(6000);
        urlCon.setRequestProperty("accept", "*/*");
        urlCon.setRequestProperty("connection", "Keep-Alive");
        urlCon.setRequestProperty("user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36");
        urlCon.setRequestProperty("Cookie",
                "user_locale=zh-CN; oschina_new_user=false; tz=Asia%2FShanghai; sajssdk_2015_cross_new_user=1; Hm_lvt_24f17767262929947cc3631f99bfd274=1628946576; gitee_user=true; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%229586785%22%2C%22first_id%22%3A%2217b44c83238449-04701f51eca36f-35607403-1296000-17b44c8323917e2%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_referrer%22%3A%22%22%7D%2C%22%24device_id%22%3A%2217b44c83238449-04701f51eca36f-35607403-1296000-17b44c8323917e2%22%7D; remote_way=http; Hm_lpvt_24f17767262929947cc3631f99bfd274=1628946640; gitee-session-n=SUVyZExKZmdZU09rOEE0d09OY1lGcnJCd1h1cmRlelVrelVGdVRuVzVTWDVvWTdSOHI1bjdGRVFPdGpLekdIVzA3UHlVUFJ2eFNrU2dVUFZiazRISDdYWWdjOW5NeTVhVW1oTEl2WnpyeXMzOWZFK2p2bEhHM1piTWUwQU1mVmNRWldQcnpaSElQcDJyVHpYM3A4RXU4ZkdNUWw5TnlRcXRiRnp3WnQzOVJsSkhRU2lkWld6TS9TL3ZBQmVuWUxiM0w2Zml0YWtCTm9uQXZTNnBtckt4RkJueVUvclAzRkhIQkE1RThaUVl4T1REMHA0Rk9QT1hMTFg1RHEwRzFDYjZBMDQ4bUpwc3I3d2dmY0NmOUltUlBvNkptUGN5WjY5ZzJHcldFN2l4a002MzlGVWZ4S0I5K3pEMHUzbXp5NW1BKzJrQ0lhTFd3YWc3QzUxWUFNeTZyd29HMjJ3Y1JrUTF3UGQrZk1mYzQ4PS0tcE1BUm9RVERaQ3NTVFN6cU1Eb2FIQT09--1506621887c74f161b874615297c557bd0d47510; visit-gitee--2021-08-11%2013%3A50%3A57%20%2B0800=1");
        int code = urlCon.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        DataInputStream in = new DataInputStream(urlCon.getInputStream());
        DataOutputStream out = new DataOutputStream(new FileOutputStream(str1));
        System.out.println("urlCon = " + urlCon);
        System.out.println("url = " + url);
        byte[] buffer = new byte[20*1024*1024];
        int count = 0;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        try {
            if(out!=null) {
                out.close();
            }
            if(in!=null) {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void downLoadEFI(String urlStr, String savePath, String fileName){
        try {
            downLoad(urlStr,savePath,fileName);
            String str=a+"new/"+fileName;
            zipDecode(str,savePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void downLoadTheme(String urlStr, String savePath, String fileName) throws Exception {
        String str1=savePath+"/"+fileName;
        downLoad(urlStr,savePath,fileName);
        File file1=new File(str1);
        String path2=a+"new";
        File file2=new File(path2);
        zipDecode(file1,file2);
    }
    //解压efi到指定位置
    public static void zip(String zipPath,String path) throws Exception{
        ZipFile zipFile = new ZipFile(zipPath,"GBK");//压缩文件的实列,并设置编码
        //获取压缩文中的所以项
        String outPath = path+"/";
        for(Enumeration<ZipEntry> enumeration = zipFile.getEntries();enumeration.hasMoreElements();)
        {
            ZipEntry zipEntry = enumeration.nextElement();//获取元素
            //排除空文件夹
            if(!zipEntry.getName().endsWith(File.separator))
            {
                System.out.println("正在解压文件:"+zipEntry.getName());//打印输出信息
                //创建解压目录
                File f = new File(outPath+zipEntry.getName().substring(0, zipEntry.getName().lastIndexOf(File.separator)));
                //判断是否存在解压目录
                if(!f.exists())
                {
                    f.mkdirs();//创建解压目录
                }
                OutputStream os = new FileOutputStream(outPath+zipEntry.getName());//创建解压后的文件
                BufferedOutputStream bos = new BufferedOutputStream(os);//带缓的写出流
                InputStream is = zipFile.getInputStream(zipEntry);//读取元素
                BufferedInputStream bis = new BufferedInputStream(is);//读取流的缓存流
                CheckedInputStream cos = new CheckedInputStream(bis, new CRC32());//检查读取流，采用CRC32算法，保证文件的一致性
                byte [] b = new byte[1024];//字节数组，每次读取1024个字节
                //循环读取压缩文件的值
                while(cos.read(b)!=-1)
                {
                    bos.write(b);//写入到新文件
                }
                cos.close();
                bis.close();
                is.close();
                bos.close();
                os.close();
            }
            else
            {
                //如果为空文件夹，则创建该文件夹
                new File(outPath+zipEntry.getName()).mkdirs();
            }
        }
        System.out.println("解压完成");
        zipFile.close();
    }
    public static void zipDecode(String zipPath,String path) throws Exception{
        zip(zipPath,path);
        File fromFile=new File(path+"/Docs/Sample.plist");
        File toFile=new File(path+"/X64/EFI/OC/config.plist");
        copyFile(fromFile,toFile);
        updateEFI();
    }
    public static void zipDecode(File zipPath,File path) throws Exception{
        zip(String.valueOf(zipPath),String.valueOf(path));
        String str1=a+"new/OcBinaryData-master/Resources";
        String str2=a+"result/EFI/OC/Resources";
        File file=new File(str1);
        if (!file.isDirectory()) {
            str1=a+"new/Resources";
            updateTheme(str1,str2);
        }else {
            updateTheme(str1,str2);
        }

    }
    //复制文件
    public static void copyFile(File resource,File target) throws Exception{
        FileInputStream inputStream = new FileInputStream(resource);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        // 文件输出流并进行缓冲
        FileOutputStream outputStream = new FileOutputStream(target);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        // 缓冲数组
        // 大文件 可将 1024 * 2 改大一些，但是 并不是越大就越快
        byte[] bytes = new byte[1024 * 2];
        int len = 0;
        while ((len = inputStream.read(bytes)) != -1) {
            bufferedOutputStream.write(bytes, 0, len);
        }
        // 刷新输出缓冲流
        bufferedOutputStream.flush();
        //关闭流
        bufferedInputStream.close();
        bufferedOutputStream.close();
        inputStream.close();
        outputStream.close();
    }
    //更新EFI
    public static void updateEFI() throws Exception{
        String str1=a + "old/EFI/OC";
        String str2=a+"old/EFI/OC/Tools";
        String str3=a+"old/EFI/OC/Drivers";
        File file2=new File(str1);
        File[] tempList2 = file2.listFiles();
        for (int j = 0; j < tempList2.length; j++) {
            if (tempList2[j].isDirectory()){
                File file3=new File(String.valueOf(tempList2[j]));
                File[] tempList3 = file3.listFiles();
                for (int k = 0; k < tempList3.length; k++) {
                    File fileName=new File(String.valueOf(tempList3[k]));
                    System.out.println("tempList3 = " + tempList3[k]);
                    if (fileName.getName().endsWith(".aml")){
                        File fromFile=new File(String.valueOf(tempList3[k]));
                        File toFile= new File(a+"new/X64/EFI/OC/ACPI"+"/"+fileName.getName());
                        try {
                            copyFolder(String.valueOf(fromFile), String.valueOf(toFile));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if (fileName.getName().endsWith(".kext")){
                        File fromFile=new File(String.valueOf(tempList3[k]));
                        File toFile= new File(a+"new/X64/EFI/OC/Kexts"+"/"+fileName.getName());
                        String str4=fromFile.toString();
                        String str5=toFile.toString();
                        try {
                            copyFolder(str4,str5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(String.valueOf(tempList2[j]).equals(str2)){
                        if (fileName.getName().endsWith(".efi")){
                            File fromFile=new File(String.valueOf(tempList3[k]));
                            File toFile= new File(a+"/src/new/X64/EFI/OC/Tools"+"/"+fileName.getName());
                            try {
                                copyFolder(String.valueOf(fromFile), String.valueOf(toFile));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }else if (String.valueOf(tempList2[j]).equals(str3)){
                        if (fileName.getName().endsWith(".efi")){
                            File fromFile=new File(String.valueOf(tempList3[k]));
                            File toFile= new File(a+"new/X64/EFI/OC/Drivers"+"/"+fileName.getName());
                            try{
                                copyFile(fromFile,toFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        copy();
        String str6 = a+"old/EFI/Microsoft";
        String str7 = a+"new/X64/EFI/Microsoft";
        copyFolder(str6,str7);
        end();
    }
    //复制文件内容
    public static void copy() throws Exception{
        String str1=a+"old/EFI/OC/config.plist";
        String str2=a+"new/X64/EFI/OC/config.plist";

        InputStream inputStream = new FileInputStream(str1); //把文件内容以流的形式读取
        OutputStream outputStream = new FileOutputStream(str2);  //把内容以流的形式写到文件
        byte[] bytes = new byte[1024];
        int length;
        while ((length = inputStream.read(bytes))>0){
            outputStream.write(bytes,0,length);
        }
        inputStream.close();
        outputStream.close();
    }
    //复制文件夹
    public static void copyFolder(String resource, String target)  {
        Path path1 = Paths.get(resource);
        Path path2 = Paths.get(target);
        try {
            Files.move(path1,path2, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("移动成功");
        }
    }
    //结束
    public static void end() throws Exception{
        try {
            String str6 = a+"new/X64/EFI";
            String str7 = a+"result/EFI";
            copyFolder(str6,str7);
            System.out.println("OC升级已完成，在result文件夹里，请自行测试");
            System.out.println("您是否要添加仿冒白苹果主题");
            System.out.println("请输入 是 或 否");
            System.out.println("如果输入 否 则复制当前主题");
            System.out.println("如果输入这两个以外的内容则使用第三方主题");
            Scanner s=new Scanner(System.in);
            String str=s.next();
            switch (str){
                case "是":
                    String fileUrl="https://gitee.com/moka123/OcBinaryData/attach_files/800256/download/OcBinaryData-master.zip";
                    String path=a+"new";
                    String fileName="OcBinaryData-master.zip";
                    downLoadTheme(fileUrl,path,fileName);
                    break;
                case "否":
                    String str1=a+"old/EFI/OC/Resources";
                    String str2=a+"result/EFI/OC/Resources";
                    updateTheme(str1,str2);
                    break;
                default:
                    thirdPartyThemes();
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void updateTheme(String str1,String str2){
        File file2=new File(str1);
        File[] tempList2 = file2.listFiles();
        for (int i = 0; i < tempList2.length; i++) {
            if (tempList2[i].isDirectory()){
                File file3=new File(String.valueOf(tempList2[i]));
                File[] tempList3 = file3.listFiles();
                for (int j = 0; j < tempList3.length; j++) {
                    File fileName=new File(String.valueOf(tempList3[j]));
                    System.out.println("tempList3 = " + tempList3[j]);
                    String str="image";
                    if (fileName.getName().endsWith(".mp3")){
                        File fromFile=new File(String.valueOf(tempList3[j]));
                        File toFile= new File(a+"result/EFI/OC/Resources/Audio"+File.separator+fileName.getName());
                        copyFolder(String.valueOf(fromFile), String.valueOf(toFile));
                    }else if (fileName.getName().endsWith(".bin")||fileName.getName().endsWith(".png")){
                        File fromFile=new File(String.valueOf(tempList3[j]));
                        File toFile= new File(a+"result/EFI/OC/Resources/Font"+File.separator+fileName.getName());
                        copyFolder(String.valueOf(fromFile), String.valueOf(toFile));
                    }else if(String.valueOf(tempList3[j]).contains(str.substring(0,1).toUpperCase(Locale.ROOT))||String.valueOf(tempList3[j]).contains(str)) {
                        File fromFile = new File(a + "new/OcBinaryData-master/Resources/Image/Acidanthera");
                        System.out.println("!fromFile.isDirectory() = " + !fromFile.isDirectory());
                        if (!fileName.isDirectory()) {
                            fromFile = new File(a + "new/Resources/image");
                            File toFile = new File(a + "result/EFI/OC/Resources/Image");
                            copyFolder(String.valueOf(fromFile), String.valueOf(toFile));
                            System.out.println("fromFile = " + fromFile.isDirectory());
                            if (!fromFile.isDirectory()){
                                fromFile = new File(a + "old/EFI/OC/Resources/Image");
                                copyFolder(String.valueOf(fromFile), String.valueOf(toFile));
                            }
                        } else {
                            System.out.println("fromFile = " + fromFile);
                            File toFile = new File(a + "result/EFI/OC/Resources/Image");
                            copyFolder(String.valueOf(fromFile), String.valueOf(toFile));
                        }
                    }
                    else if(fileName.getName().endsWith(".l2x")||fileName.getName().endsWith(".lbl")){
                        File fromFile=new File(String.valueOf(tempList3[j]));
                        File toFile= new File(a+"result/EFI/OC/Resources/Label"+File.separator+fileName.getName());
                        copyFolder(String.valueOf(fromFile), String.valueOf(toFile));
                    }
                }
            }
        }
    }
    //第三方主题下载
    public static void thirdPartyThemes() throws Exception{
        String webString = getWebString("https://gitee.com/moka123/My-Simple-OC-Themes/tree/main/Resources-0.7.0","UTF-8");
        String pattern = "(\\w||\\-)*\\.zip";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(webString);
        TreeSet  TreeSet = new TreeSet();
        Set set = Collections.synchronizedSet(TreeSet);
        while (m.find()){
            if(m.group().equals(".zip")||m.group().equals("26Steel.zip")) continue;
            set.add(m.group());
        }
        ArrayList <String> arruni = new ArrayList<String>(set);
        for (int i = 1;i < arruni.size() ; i++) {
            System.out.println(i+"."+arruni.get(i));
        }
        System.out.println("请输入您要下载的第三方主题（输入序号即可）");
        Scanner s=new Scanner(System.in);
        int i=s.nextInt();
        System.out.println(arruni.get(i));
        String urlStr="https://gitee.com/moka123/My-Simple-OC-Themes/raw/main/Resources-0.7.0/"+arruni.get(i);
        String savePath=a+"new";
        String fileName=arruni.get(i);
        downLoadTheme(urlStr,savePath,fileName);
    }
    public static String getWebString(String pageURL,String encoding) {
        StringBuffer sbBuffer = new StringBuffer();
        try {
            URL url = new URL(pageURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),encoding));
            String lineString;
            while((lineString=in.readLine())!=null)
            {
                sbBuffer.append(lineString);
                sbBuffer.append("\n");
            }
            in.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return sbBuffer.toString();

    }

}
