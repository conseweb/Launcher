package com.bitants.common.sqliteasset;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Utils {

    private static final String TAG = SQLiteAssetHelper.class.getSimpleName();

    public static List<String> splitSqlScript(String script, char delim) {
        List<String> statements = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean inLiteral = false;
        char[] content = script.toCharArray();
        for (int i = 0; i < script.length(); i++) {
            if (content[i] == '"') {
                inLiteral = !inLiteral;
            }
            if (content[i] == delim && !inLiteral) {
                if (sb.length() > 0) {
                    statements.add(sb.toString().trim());
                    sb = new StringBuilder();
                }
            } else {
                sb.append(content[i]);
            }
        }
        if (sb.length() > 0) {
            statements.add(sb.toString().trim());
        }
        return statements;
    }

    public static void writeExtractedFileToDisk(InputStream in, OutputStream outs) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer))>0){
            outs.write(buffer, 0, length);
        }
        outs.flush();
        outs.close();
        in.close();
    }

    public static ZipInputStream getFileFromZip(InputStream zipFileStream) throws IOException {
        ZipInputStream zis = new ZipInputStream(zipFileStream);
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            Log.w(TAG, "extracting file: '" + ze.getName() + "'...");
            return zis;
        }
        return null;
    }

    public static File unGzip(File infile, boolean deleteGzipfileOnSuccess ) throws IOException {
        GZIPInputStream gin = new GZIPInputStream(new FileInputStream(infile));
        File outFile = new File(infile.getParent(), infile.getName().replaceAll("\\.gz$", ""));
        FileOutputStream fos = new FileOutputStream(outFile);
        byte[] buf = new byte[2048]; // Buffer size is a matter of taste and application...
        int len;
        while ((len = gin.read(buf)) > 0)
            fos.write(buf, 0, len);
        gin.close();
        fos.close();
        if (deleteGzipfileOnSuccess)
            infile.delete();
        return outFile;
    }

//    public static GZIPInputStream getFileFromGz(InputStream gzFileStream) throws IOException {
//        byte[] buffer = new byte[2048];
//        GZIPInputStream gzis = new GZIPInputStream(gzFileStream);
//        int len;
//        while ((len = gzis.read(buffer)) > 0) {
//            out.write(buffer, 0, len);
//        }
//
//        return null;
//    }

    public static String convertStreamToString(InputStream is) {
        return new Scanner(is).useDelimiter("\\A").next();
    }

}
