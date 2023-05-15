/*
 * This file is part of panda.
 * panda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * panda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with panda.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 - 2022 熊猫（XMDS）
 */

package zhy.hongyuan.util.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import zhy.hongyuan.application.App;
import zhy.hongyuan.util.IOUtils;
import zhy.hongyuan.util.help.StringHelper;


public class FileUtils {
    //采用自己的格式去设置文件，防止文件被系统文件查询到
    public static final String SUFFIX_NB = ".nb";
    public static final String SUFFIX_FY = ".fy";
    public static final String SUFFIX_TXT = ".txt";
    public static final String SUFFIX_EPUB = ".epub";
    public static final String SUFFIX_PDF = ".pdf";
    public static final byte BLANK = 0x0a;
    //获取文件夹
    public static File getFolder(String filePath){
        File file = new File(filePath);
        //如果文件夹不存在，就创建它
        if (!file.exists()){
            file.mkdirs();
        }
        return file;
    }

    //获取文件
    public static synchronized File getFile(String filePath){
        File file = new File(filePath);
        try {
            if (!file.exists()){
                //创建父类文件夹
                getFolder(file.getParent());
                //创建文件
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getDataDir(){
        return getFileDir().getParent();
    }

    //获取Cache文件夹
    public static String getCachePath(){
        if (isSdCardExist()){
            return App.getmContext()
                    .getExternalCacheDir()
                    .getAbsolutePath();
        }
        else{
            return App.getmContext()
                    .getCacheDir()
                    .getAbsolutePath();
        }
    }

    //获取File文件夹
    public static String getFilePath(){
        return getFileDir().getAbsolutePath();
    }

    public static File getFileDir(){
        if (isSdCardExist()){
            return App.getmContext()
                    .getExternalFilesDir(null);
        }
        else{
            return App.getmContext()
                    .getFilesDir();
        }
    }

    public static long getDirSize(File file){
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {
                return file.length();
            }
        } else {
            return 0;
        }
    }

    public static String getFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"b", "KB", "MB", "G", "T"};
        //计算单位的，原理是利用lg,公式是 lg(1024^n) = nlg(1024)，最后 nlg(1024)/lg(1024) = n。
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        //计算原理是，size/单位值。单位值指的是:比如说b = 1024,KB = 1024^2
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 本来是获取File的内容的。但是为了解决文本缩进、换行的问题
     * 这个方法就是专门用来获取书籍的...
     *
     * 应该放在BookRepository中。。。
     * @param file
     * @return
     */
    public static String getFileContent(File file){
        Reader reader = null;
        String str = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null){
                //过滤空语句
                if (!str.equals("")){
                    //由于sb会自动过滤\n,所以需要加上去
                    sb.append("    "+str+"\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    //判断是否挂载了SD卡
    public static boolean isSdCardExist(){
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            return true;
        }
        return false;
    }

    //递归删除文件夹下的数据
    public static synchronized void deleteFile(String filePath){
        File file = new File(filePath);
        if (!file.exists()) return;

        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File subFile : files){
                String path = subFile.getPath();
                deleteFile(path);
            }
        }
        //删除文件
        file.delete();
    }

    //由于递归的耗时问题，取巧只遍历内部三层

    //获取txt文件
    public static List<File> getTxtFiles(String filePath, int layer){
        final List txtFiles = new ArrayList();
        File file = new File(filePath);

        //如果层级为 3，则直接返回
        if (layer == 3){
            return txtFiles;
        }

        //获取文件夹
        File[] dirs = file.listFiles(
                pathname -> {
                    if (pathname.isDirectory() && !pathname.getName().startsWith(".")) {
                        return true;
                    }
                    //获取txt文件
                    else if (pathname.getName().endsWith(".txt")) {
                        txtFiles.add(pathname);
                        return false;
                    } else {
                        return false;
                    }
                }
        );
        //遍历文件夹
        for (File dir : dirs){
            //递归遍历txt文件
            txtFiles.addAll(getTxtFiles(dir.getPath(),layer + 1));
        }
        return txtFiles;
    }

    //由于遍历比较耗时
    public static Single<List<File>> getSDTxtFile(){
        //外部存储卡路径
        final String rootPath = Environment.getExternalStorageDirectory().getPath();
        return Single.create(new SingleOnSubscribe<List<File>>() {
            @Override
            public void subscribe(SingleEmitter<List<File>> e) throws Exception {
                List<File> files = getTxtFiles(rootPath,0);
                e.onSuccess(files);
            }
        });
    }

    /**
     * 获取文件编码
     * @param file
     * @return
     */
    public static String getFileCharset(File file){
        FileInputStream fis = null;
        FileInputStream temFis = null;
        FileOutputStream fos = null;
        File temFile = null;
        try {
            temFile = getFile(getCachePath() + File.separator + "tem.fy");
            fis = new FileInputStream(file);
            fos = new FileOutputStream(temFile);
            //用10kb作为试探
            byte[] bytes = new byte[1024 * 10];
            int len;
            if ((len = fis.read(bytes)) != -1){
                fos.write(bytes, 0, len);
            }
            fos.flush();
            temFis = new FileInputStream(temFile);
            String encoding = UniversalDetector.detectCharset(temFis);
            if (encoding != null) {
                Log.d("encoding", encoding);
                return encoding;
            } else {
                return "UTF-8";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "UTF-8";
        }finally {
            IOUtils.close(fis, temFis, fos);
            if (temFile != null) {
                temFile.delete();
            }
        }
    }

    public static byte[] getBytes(File file) {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            bos.flush();
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(fis, bos);
        }
        return buffer;
    }

    /**
     * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files/ 目录下
     *
     * @param context
     */
    public static void write(Context context, String fileName, String content) {
        if (content == null)
            content = "";
        write(context, fileName, content.getBytes());
    }

    public static void write(Context context, String fileName, byte[] content) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            fos.write(content);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 读取文本文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String read(Context context, String fileName) {
        try {
            FileInputStream in = context.openFileInput(fileName);
            return readInStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readText(String path){
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(path));
            String tem;
            while ((tem = br.readLine()) != null){
                sb.append(tem);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }finally {
            IOUtils.close(br);
        }
    }

    public static String readInStream(InputStream inStream) {
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            outStream.flush();
            return outStream.toString();
        } catch (IOException e) {
            Log.i("FileTest", e.getMessage());
        }finally {
            IOUtils.close(inStream, outStream);
        }
        return null;
    }

    public static String readAssertFile(Context context, String assetName){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(assetName)));
            StringBuilder assetText = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                assetText.append(line);
                assetText.append("\n");
            }
            return assetText.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br);
        }
        return "";
    }


    /**
     * 向手机写图片
     *
     * @param buffer
     * @param folder
     * @param fileName
     * @return
     */
    public static boolean writeFile(byte[] buffer, String folder,
                                    String fileName) {
        boolean writeSucc = false;

        File fileDir = new File(folder);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file = new File(folder,fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            out.flush();
            writeSucc = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writeSucc;
    }

    public static boolean writeFile(byte[] buffer, File file) {
        boolean writeSucc = false;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            out.flush();
            writeSucc = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(out);
        }

        return writeSucc;
    }

    public static boolean writeText(String text, File file){
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write(text);
            fw.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            IOUtils.close(fw);
        }
    }

    public static boolean copy(String src, String dest){
        byte[] buffer = FileUtils.getBytes(new File(src));
        return buffer != null && FileUtils.writeFile(buffer,
                FileUtils.getFile(dest));
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringHelper.isEmpty(filePath))
            return "";
        //return filePath.substring(filePath.lastIndexOf("?") + 1);
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameNoFormat(String filePath) {
        if (StringHelper.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
                point);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (StringHelper.isEmpty(fileName))
            return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }


    /**
     * 获取目录文件个数
     *
     * @return
     */
    public long getFileList(File dir) {
        long count = 0;
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = count + getFileList(file);// 递归
                count--;
            }
        }
        return count;
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            out.write(ch);
        }
        byte buffer[] = out.toByteArray();
        out.close();
        return buffer;
    }

    /**
     * 检查文件是否存在
     *
     * @param name
     * @return
     */
    public static boolean checkFileExists(String name) {
        boolean status;
        if (!name.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + name);
            status = newPath.exists();
        } else {
            status = false;
        }
        return status;
    }
    /**
     * 返回项目的files目录
     * @return
     */
    public static String getPath(){
        File path = Environment.getExternalStorageDirectory();
        return path.toString();
    }

    /**
     * 检查路径是否存在
     *
     * @param path
     * @return
     */
    public static boolean checkFilePathExists(String path) {
        return new File(path).exists();
    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                freeSpace = availableBlocks * blockSize / 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return (freeSpace);
    }

    /**
     * 新建目录
     *
     * @param directoryName
     * @return
     */
    public static boolean createDirectory(String directoryName) {
        boolean status;
        if (!directoryName.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + directoryName);
            status = newPath.mkdir();
            status = true;
        } else
            status = false;
        return status;
    }

    /**
     * 检查是否安装SD卡
     *
     * @return
     */
    public static boolean checkSaveLocationExists() {
        String sDCardStatus = Environment.getExternalStorageState();
        boolean status;
        status = sDCardStatus.equals(Environment.MEDIA_MOUNTED);
        return status;
    }

    /**
     * 删除目录(包括：目录里的所有文件)
     *
     * @param fileName
     * @return
     */
    public static boolean deleteDirectory(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();

        if (!fileName.equals("")) {

            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isDirectory()) {
                String[] listfile = newPath.list();
                // delete all files within the specified directory and then
                // delete the directory
                try {
                    for (int i = 0; i < listfile.length; i++) {
                        File deletedFile = new File(newPath.toString() + "/"
                                + listfile[i].toString());
                        deletedFile.delete();
                    }
                    newPath.delete();
                    Log.i("DirManager delDirectory", fileName);
                    status = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    status = false;
                }

            } else
                status = false;
        } else
            status = false;
        return status;
    }



    /**
     * 删除空目录
     *
     * 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
     *
     * @return
     */
    public static int deleteBlankPath(String path) {
        File f = new File(path);
        if (!f.canWrite()) {
            return 1;
        }
        if (f.list() != null && f.list().length > 0) {
            return 2;
        }
        if (f.delete()) {
            return 0;
        }
        return 3;
    }

    /**
     * 重命名
     *
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean reNamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            Log.i("DirManager delFile", filePath);
            f.delete();
            return true;
        }
        return false;
    }

    /**
     * 获取SD卡的根目录，末尾带\
     *
     * @return
     */
    public static String getSDRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 列出root目录下所有子目录
     *

     * @return 绝对路径
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory()) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    public enum PathStatus {
        SUCCESS, EXITS, ERROR
    }

    /**
     * 创建目录
     *
     */
    public static PathStatus createPath(String newPath) {
        File path = new File(newPath);
        if (path.exists()) {
            return PathStatus.EXITS;
        }
        if (path.mkdir()) {
            return PathStatus.SUCCESS;
        } else {
            return PathStatus.ERROR;
        }
    }

    /**
     * 截取路径名
     *
     * @return
     */
    public static String getPathName(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        int end = absolutePath.length();
        return absolutePath.substring(start, end);
    }


    public static String getFilePathFromContentUri(Context context, Uri uri) {
        String photoPath = "";
        if(context == null || uri == null) {
            return photoPath;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if(isExternalStorageDocument(uri)) {
                String[] split = docId.split(":");
                if(split.length >= 2) {
                    String type = split[0];
                    if("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            }
            else if(isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(context, contentUri, null, null);
            }
            else if(isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if(split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    }
                    else if("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    }
                    else if("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[] { split[1] };
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                }
            }
        }
        else if("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        }
        else {
            photoPath = getDataColumn(context, uri, null, null);
        }

        return photoPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return null;
    }

    public static String getFileSuffix(String filePath) {
        File file = new File(filePath);
        return getFileSuffix(file);
    }

    public static String getFileSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return "";
        }
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0 ? fileName.substring(dotIndex) : "";
    }

    /**
     * 获取文件名（不包括扩展名）
     */
    public static String getNameExcludeExtension(String filePath) {
        try {
            String fileName = new File(filePath).getName();
            int lastIndexOf = fileName.lastIndexOf(".");
            if (lastIndexOf != -1) {
                fileName = fileName.substring(0, lastIndexOf);
            }
            return fileName;
        } catch (Exception e) {
           return "";
        }
    }

    public static String getPath(String rootPath, String... subDirFiles){
        StringBuilder path = new StringBuilder(rootPath);
        for (String subPath : subDirFiles){
            if (!TextUtils.isEmpty(subPath)){
                if (!path.toString().endsWith(File.separator)){
                    path.append(File.separator);
                }
                path.append(subPath);
            }
        }
        return path.toString();
    }

    public static String getPath(File root, String... subDirFiles){
        StringBuilder path = new StringBuilder(root.getAbsolutePath());
        for (String subPath : subDirFiles){
            if (!TextUtils.isEmpty(subPath)){
                if (!path.toString().endsWith(File.separator)){
                    path.append(File.separator);
                }
                path.append(subPath);
            }
        }
        return path.toString();
    }
}
