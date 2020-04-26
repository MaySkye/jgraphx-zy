package com.mxgraph.examples.swing.auth;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExeCmd {
    public static String exec(String[] commandStr) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            // System.out.println(sb.toString());
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String priveteKeypath = "./pairs/zhao.pem";
        String[] cmd = {"C:\\Program Files\\Git\\usr\\bin\\openssl", "pkcs8", "-topk8", "-nocrypt", "-in", priveteKeypath};
        System.out.println(cmd);
        String res = exec(cmd);
        System.out.println(res);
    }
}
