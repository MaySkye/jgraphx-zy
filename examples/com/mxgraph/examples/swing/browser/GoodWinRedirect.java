package com.mxgraph.examples.swing.browser;

import java.io.FileOutputStream;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 21:36 2019/6/19
 * @Modify By:
 */
public class GoodWinRedirect {
    public static void main(String args[])
    {
        if (args.length < 1)
        {
            System.out.println("USAGE java GoodWinRedirect <outputfile>");
            System.exit(1);
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(args[0]);
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("java jecho 'Hello World'");
            // any error message?
            StreamGobbler errorGobbler = new
                    StreamGobbler(proc.getErrorStream(), "ERROR");

            // any output?
            StreamGobbler outputGobbler = new
                    StreamGobbler(proc.getInputStream(), "OUTPUT", fos);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            // any error???
            int exitVal = proc.waitFor();
            System.out.println("ExitValue: " + exitVal);
            fos.flush();
            fos.close();
        } catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}
