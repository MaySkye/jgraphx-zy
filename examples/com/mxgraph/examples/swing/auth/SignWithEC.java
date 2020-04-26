package com.mxgraph.examples.swing.auth;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignWithEC {

    // 获取私钥 需要保证私钥格式为 PKCS8 格式, 即以BEGIN PRIVATE KEY开头, 而非 BEGIN EC PRIVATE KEY, 可用openssl转换
    // openssl pkcs8 -topk8 -nocrypt -in a.pem
    public static PrivateKey getPrivateKey(String privateKeyPath, String opensslPath) throws Exception {
        String[] cmd = {opensslPath, "pkcs8", "-topk8", "-nocrypt", "-in", privateKeyPath};
        String privateKeyPEM = ExeCmd.exec(cmd);

        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replaceAll("\n", "");
        privateKeyPEM = privateKeyPEM.replaceAll(" ", "");

        byte[] keyData = Base64.decode(privateKeyPEM);
        EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(keyData);
        KeyFactory kf = KeyFactory.getInstance("EC");
        PrivateKey ecPrivateKey = kf.generatePrivate(privKeySpec);

        return ecPrivateKey;
    }

    // 从证书中获取公钥
    public static PublicKey getPublicKeyFromCert(String certPath) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)cf.generateCertificate(new FileInputStream(certPath));
        PublicKey ecPublicKey = cert.getPublicKey();
        return ecPublicKey;
    }

    // 签名
    public static byte[] sign(PrivateKey privateKey, String source) throws Exception {
        Signature signature = Signature.getInstance("SHA1withECDSA");
        signature.initSign(privateKey);
        signature.update(source.getBytes());
        byte[] res = signature.sign();
        return res;
    }

    // 验证
    public static boolean verify(PublicKey publicKey, byte[] signed, String source) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey newPublicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Signature signature = Signature.getInstance("SHA1withECDSA");
        signature.initVerify(newPublicKey);
        signature.update(source.getBytes());
        boolean bool = signature.verify(signed);
        return bool;
    }

    // 从证书中获取有效日期
    public static String getCertValidity(String certPath) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)cf.generateCertificate(new FileInputStream(certPath));
        String validity = cert.getNotBefore().toString() + " 到 " + cert.getNotAfter().toString();
        return validity;
    }

    public static void main(String[] args) throws Exception {
        String certPath = "./pairs/zhao.crt";
        String privatePath = "./pairs/zhao.pem";
        String content = "123456";
        PrivateKey privateKey = getPrivateKey(privatePath, Common.opensslPath);
        PublicKey publicKey = getPublicKeyFromCert(certPath);
        byte[] s = sign(privateKey, content);
        System.out.println("签名："+ Base64.encode(s));
        boolean bool = verify(publicKey, s, content);
        System.out.println("验证："+ bool);
        System.out.println(getCertValidity(certPath));
    }
}
