package krb.test;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Martin Simka
 */
public class Main {
    // postgresql 9.2
    private static final String URL = "jdbc:postgresql://db14.mw.lab.eng.bos.redhat.com:5432/krbusr01";
    // postgresql plus
    //private static final String URL = "jdbc:edb://db15.mw.lab.eng.bos.redhat.com:5432/dballo00";
    //local
//    private static final String URL = "jdbc:postgresql://krbtest:5432/template1";

    public static void main(String[] args) throws Exception {
//        System.setProperty("java.security.krb5.realm", "JBOSS.ORG");
//        System.setProperty("java.security.krb5.kdc", "krbtest");
        System.setProperty("java.security.krb5.realm", "MW.LAB.ENG.BOS.REDHAT.COM");
        System.setProperty("java.security.krb5.kdc", "kerberos-test.mw.lab.eng.bos.redhat.com");
        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("java.security.krb5.conf", "/home/msimka/Projekty/redhat/tmp/jdbc-client/krb5.conf");

        System.setProperty("java.security.auth.login.config", "/home/msimka/Projekty/redhat/tmp/jdbc-client/login.conf"); // EDIT PATH!!
//        System.setProperty("java.security.auth.login.config", "/home/msimka/Projekty/redhat/tmp/jdbc-client/login_local.conf"); // EDIT PATH!!

        Subject specificSubject = new Subject();
        LoginContext lc = new LoginContext("pgjdbc", specificSubject);

//        Subject specificSubject = new Subject();
//
//        Krb5LoginModule krb5Module = new Krb5LoginModule();
//        HashMap sharedState = new HashMap();
//        Map<String, String> options = new HashMap<String, String>();
//        options.put("doNotPrompt","true");
//        options.put("storeKey","true");
//        options.put("useKeyTab","true");
//        options.put("debug","true");
//        options.put("principal","host/eap@JBOSS.ORG");
//        options.put("keyTab","/home/msimka/Projekty/redhat/git/jboss-eap/build/target/jboss-as-7.3.0.Final-redhat-SNAPSHOT/host_eap.keytab");
//
//        krb5Module.initialize(specificSubject, null, sharedState, options);
//        boolean retLogin = krb5Module.login();
//        krb5Module.commit();
//        if(!retLogin)
//            throw new Exception("Kerberos5 adaptor couldn't retrieve credentials (TGT) from the cache");

        Class.forName("org.postgresql.Driver");
        Connection conn =
                (Connection)Subject.doAs(specificSubject, new PrivilegedExceptionAction()
                {
                    public Object run()
                    {
                        Connection con = null;
                        Properties prop = new Properties();
                        prop.setProperty("user", "KRBUSR01");
//                        prop.setProperty("user", "host/eap@JBOSS.ORG");
                        prop.setProperty("jaasApplicationName", "pgjdbc");
                        //prop.setProperty("kerberosServerName", "aaa");
                        String url = URL;
                        try
                        {
                            con = DriverManager.getConnection(url, prop);
                        } catch (Exception except)
                        {
                            except.printStackTrace();
                        }
                        return con;
                    }
                });

        Statement stmt = conn.createStatement();
        stmt.executeQuery("SELECT 1");

        /*
            TESTING, connection without krb, jaas
         */
//        try {
//            Properties prop = new Properties();
//            prop.put("user", "test");
//            prop.put("password", "password");
//
//            String url = "jdbc:postgresql://localhost:5432/template1";
//            Connection conn = DriverManager.getConnection(url, prop);
//
//            Statement stmt = conn.createStatement();
//            stmt.executeQuery("SELECT 1");
//
//        } catch (ClassNotFoundException cnf) {
//            cnf.printStackTrace();
//        } catch (SQLException sqle) {
//            sqle.printStackTrace();
//        }
    }

}
