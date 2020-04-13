package com.mxgraph.examples.swing.startup;

import com.kong.CasbinFunc;
import com.kong.XMLParser;
import com.mxgraph.examples.swing.util.FileUtil;
import org.apache.log4j.BasicConfigurator;
import org.kong.ConnectionProfile;
import org.kong.channel.FabricUser;
import org.kong.wallet.WalletConfig;
import org.kong.wallet.WalletRepository;


import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 11:28 2019/6/18
 * @Modify By:
 */
public class FabricAuth {

    //安装路径
    private static String appPath = FileUtil.getAppPath();
    //kong 的配置文件流
    private static InputStream in = FabricAuth.class.getResourceAsStream("/config/kong_conf.properties");


    private static String directoryPath;
    private static String modelPath;
    private static String policyPath;

    static {
        Properties properties = new Properties();
        try {
            properties.load(in);
            directoryPath = appPath + properties.getProperty("PolicyDirectoryPath");
            modelPath = appPath + properties.getProperty("CasbinModelPath");
            policyPath = appPath + properties.getProperty("CasbinPolicyPath");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FabricAuth() {

    }

    // 认证用户身份
    public static boolean loginVerify(String path, String username) throws Exception {
        try {
            BasicConfigurator.configure();
            String cardsPath = path;
            InputStream connection = new FileInputStream(appPath + "/config/connection.json");
            ConnectionProfile connectionProfile = new ConnectionProfile(connection);
            String userName = username;
            WalletConfig walletConfig = new WalletConfig(userName, Paths.get(cardsPath, userName), true);
            WalletRepository walletRepository = new WalletRepository(walletConfig, connectionProfile.getNetworkConfig().getClientOrganization());
            FabricUser fabricUser = walletRepository.reEnrollUser();
            System.out.println("[reEnroll] 登录成功，用户名: " + fabricUser.getName());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("认证失败，请重新登录");
            return true;
        }
        return true;
    }

    // 用户权限判定
    public static boolean Enforce(String policy) throws Exception {
        //XMLParser.readfiles(directoryPath);
        //List<String> policyList = XMLParser.getPolicyList();
        //CasbinFunc casbinFunc = new CasbinFunc(modelPath, policyPath);
        //casbinFunc.addPolicy(policyList);
        return true;
    }
}
