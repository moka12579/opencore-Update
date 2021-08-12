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
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;



public class ocUpdate {
    static String path2 = System.getProperty("java.class.path");
    static File path =new File(path2);
    static String a= path2.replace(path.getName(),"");
    public static void main(String[] args) throws Exception{
        List<String> files = new ArrayList<String>();
        System.out.println("path = " + path.getName());
        System.out.println("a = " + a);
        File file = new File(a+"old/EFI");
//        File[] tempList = file.listFiles();
//        System.out.println("tempList = " + tempList[0]);
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
    public static void update(String path){
        String fileUrl="https://gitee.com/moka123/OpenCorePkg/attach_files/798330/download/OpenCore-0.7.2-RELEASE.zip";
        try {
            downLoad(fileUrl,path,"OpenCore-0.7.2-RELEASE.zip");
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
        int code = urlCon.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        DataInputStream in = new DataInputStream(urlCon.getInputStream());
        DataOutputStream out = new DataOutputStream(new FileOutputStream(str1));
        byte[] buffer = new byte[2048];
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
        zipDecode(str1,savePath);
    }
    public static void zipDecode(String zipPath,String path) throws Exception{
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
        File fromFile=new File(path+"/Docs/Sample.plist");
        File toFile=new File(path+"/X64/EFI/OC/config.plist");
        copyFile(fromFile,toFile);
        updateEFI();
    }
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
    public static void copyFolder(String resource, String target)  {
        Path path1 = Paths.get(resource);
        Path path2 = Paths.get(target);
        try {
            Files.move(path1,path2, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("移动成功");
        }
    }
    public static void end() {
        try {
            String str6 = a+"new/X64/EFI";
            String str7 = a+"result/EFI";
            copyFolder(str6,str7);
            System.out.println("OC升级已完成，在result文件夹里，请自行测试");
        }catch (Exception e){
            try {
                throw new Exception("出现错误，请检查后再次运行");
            } catch (Exception ex) {

            }
        }

    }
}
