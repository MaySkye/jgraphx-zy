package com.mxgraph.examples.swing.auth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Common {
    // 开发环境url
    public static final String devUrlPrefix = "http://localhost:9090/";
    // 正式环境url
    public static final String proUrlPrefix = "https://175.24.88.50:4433/api/";
    public static final String opensslPath = "D:\\softwareInstall\\Git\\usr\\bin\\openssl";

    public static String getFileContent(FileInputStream fis, String encoding) throws IOException {
        try(BufferedReader br = new BufferedReader( new InputStreamReader(fis, encoding))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        }
    }
}
