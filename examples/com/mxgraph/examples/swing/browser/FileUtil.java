package com.mxgraph.examples.swing.browser;

import java.io.*;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 22:25 2019/6/19
 * @Modify By:
 */
public class FileUtil {

    public static void renameFile(String oldName,String newName,String absolutePath){
        String jsonfile=absolutePath+oldName;
        String new_jsonfile=absolutePath+newName;
        File file=new File(jsonfile);
        File fileNew=new File(new_jsonfile);
        if(!file.exists()){
            try {
                System.out.println("file is not exist , creat it!");
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //给文件改名
        System.out.println("rename...");
        file.renameTo(fileNew);
    }

    public static void copyFile(File file1, File file2){
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        byte[] bytes = new byte[1024];
        int temp = 0;
        try {
            inputStream = new FileInputStream(file1);
            fileOutputStream = new FileOutputStream(file2);
            while((temp = inputStream.read(bytes)) != -1){
                fileOutputStream.write(bytes, 0, temp);
                fileOutputStream.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void cutFile(String sourceFile,String targetFile){
        File file1 = new File(sourceFile);
        File file2 = new File(targetFile);
        //在程序结束时删除文件1
        if (file1.exists()) {
            System.out.println("file1 exists");
            file1.deleteOnExit();
        } else {
            System.out.println("file1 not exists, cut fail ...");
            return;
        }

        try {
            //在D盘创建文件2
            /*if(file2.exists()){
                System.out.println("file2 exists...");
                file2.deleteOnExit();
                file2 = new File(targetFile);
                file2.createNewFile();
            }*/
            file2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        copyFile(file1, file2);
    }

    public static boolean deleteFile(String fileName){
        File file = new File(fileName);
        if(file.isFile() && file.exists()){
            Boolean succeedDelete = file.delete();
            if(succeedDelete){
                System.out.println("删除单个文件"+fileName+"成功！");
                return true;
            }
            else{
                System.out.println("删除单个文件"+fileName+"失败！");
                return true;
            }
        }else{
            return false;
        }
    }

}
