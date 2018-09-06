package com.huanke.iot.manage.common.util;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

public class SSHConnectionUtils {



    private final static String S_PATH_FILE_PRIVATE_KEY = "~/.ssh/id_rsa";
    private final static String S_PATH_FILE_KNOWN_HOSTS = "~/.ssh/known_hosts";
    private final static String S_PASS_PHRASE = "";
    private final static int LOCAl_PORT = 3307;
    private final static int REMOTE_PORT = 3306;
    private final static int SSH_REMOTE_PORT = 22;
    private final static String SSH_USER = "root";
    private final static String SSH_PASSWORD = "Kong198818";
    private final static String SSH_REMOTE_SERVER = "39.104.52.84";
    private final static String MYSQL_REMOTE_SERVER = "39.104.52.84";


//
//    @Value("${SSH_USER}")
//    private  String SSH_USER;
//
//
//    @Value("${SSH_PASSWORD}")
//    private  static String SSH_PASSWORD;
//    @Value("${SSH_REMOTE_SERVER}")
//    private  static String SSH_REMOTE_SERVER;
//    @Value("${MYSQL_REMOTE_SERVER}")
//    private  static String MYSQL_REMOTE_SERVER;

    private Session sesion; //represents each ssh session

    public void closeSSH ()
    {
        sesion.disconnect();
    }

    public SSHConnectionUtils() throws Throwable
    {

        JSch jsch = null;
        jsch = new JSch();
        jsch.setKnownHosts(S_PATH_FILE_KNOWN_HOSTS);
        jsch.addIdentity(S_PATH_FILE_PRIVATE_KEY,"pass");

        sesion = jsch.getSession(SSH_USER, SSH_REMOTE_SERVER, SSH_REMOTE_PORT);

        sesion.setPassword(SSH_PASSWORD);

        Properties config = new Properties();
//        config.put("StrictHostKeyChecking", "no");
        //不需要 Kerberos  的用户名密码认证
        config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
        config.put("StrictHostKeyChecking", "no");
        sesion.setConfig(config);

        System.out.println("Establishing Connection...");
        sesion.connect(); //ssh connection established!
        //by security policy, you must connect through a fowarded port
        sesion.setPortForwardingL(LOCAl_PORT, MYSQL_REMOTE_SERVER, REMOTE_PORT);
    }
}