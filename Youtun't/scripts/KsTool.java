import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.math.BigInteger;
import java.util.Date;

/**
 * Keystore tool for the Youtun't build. ReVanced's CLI reads keystores with
 * KeyStore.getInstance("BKS", "BC") (ApkSigner.newKeyStoreInstance), so the
 * signing store must be BKS created with the BouncyCastle version that ships
 * inside bundar/revanced-cli.jar — JKS/PKCS12 files made by keytool fail to
 * load with "Wrong version of key store" right at signing time.
 *
 * Usage:
 *   KsTool check   <keystore.bks> <pass> <alias>   exit 0 iff loadable + alias holds a key
 *   KsTool create  <keystore.bks> <pass> <alias>   creates a fresh BKS with CN=Youtunt
 */
public final class KsTool {
    private static final String SUBJECT =
            "CN=Youtunt, OU=MOD, O=Nikkotin, L=Madrid, ST=MAD, C=ES";
    private static final long VALIDITY_DAYS = 10950; // 30 years

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {
        try {
            switch (args[0]) {
                case "check" -> check(args[1], args[2], args[3]);
                case "create" -> create(args[1], args[2], args[3]);
                default -> {
                    System.err.println("usage: KsTool check|create <bks> <pass> <alias>");
                    System.exit(2);
                }
            }
        } catch (Exception e) {
            System.err.println("KsTool failed: " + e);
            System.exit(1);
        }
    }

    private static void check(String path, String pass, String alias) throws Exception {
        try (InputStream in = new FileInputStream(path)) {
            KeyStore ks = KeyStore.getInstance("BKS", "BC");
            ks.load(in, pass.toCharArray());
            if (!ks.isKeyEntry(alias)) {
                System.err.println("KsTool: alias '" + alias + "' has no key entry");
                System.exit(3);
            }
            System.out.println("BKS ok: alias=" + alias);
        }
    }

    private static void create(String path, String pass, String alias) throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair pair = gen.generateKeyPair();

        X500Name subject = new X500Name(SUBJECT);
        JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                subject, BigInteger.valueOf(System.currentTimeMillis()),
                new Date(), new Date(System.currentTimeMillis() + VALIDITY_DAYS * 86_400_000L),
                subject, pair.getPublic());
        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(builder.build(new JcaContentSignerBuilder("SHA256withRSA").build(pair.getPrivate())));

        KeyStore ks = KeyStore.getInstance("BKS", "BC");
        ks.load(null, null);
        ks.setKeyEntry(alias, pair.getPrivate(), pass.toCharArray(), new Certificate[]{cert});
        try (OutputStream out = new FileOutputStream(path)) {
            ks.store(out, pass.toCharArray());
        }
        System.out.println("BKS created: " + path + " alias=" + alias);
    }

    private KsTool() { }
}
