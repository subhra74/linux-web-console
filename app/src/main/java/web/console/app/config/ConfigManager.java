package web.console.app.config;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ConfigManager {

	private static Path CONFIG_PATH = null;

	private static final String CERTIFICATE_ALIAS = "cloudshell-cert";
	private static final String CERTIFICATE_ALGORITHM = "RSA";
	private static final String CERTIFICATE_DN = "CN=easy.cloudshell.app, O=easy.cloudshell.app, L=easy.cloudshell.app, ST=il, C=c";
	private static final int CERTIFICATE_BITS = 2048;

	static {
		Path configPath = null;
		String configPathStr = System.getenv("EASY_CLOUD_SHELL_CONFIG_DIR");
		if (configPathStr == null || configPathStr.length() < 1) {
			configPath = Paths.get(System.getProperty("user.home"),
					".easy-cloud-shell");
		} else {
			configPath = Paths.get(configPathStr, ".linux-web-console");
		}
		CONFIG_PATH = configPath;
		try {
			Files.createDirectories(configPath);
		} catch (FileAlreadyExistsException e) {
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// loadUserDetails();
	}

	public static void saveUserDetails()
			throws FileNotFoundException, IOException {
		Path userConfigPath = CONFIG_PATH.resolve("users.properties");
		Properties prop = new Properties();
		prop.put("app.default-user", System.getProperty("app.default-user"));
		prop.put("app.default-pass", System.getProperty("app.default-pass"));
		prop.put("app.default-shell", System.getProperty("app.default-shell"));

		try (OutputStream out = new FileOutputStream(userConfigPath.toFile())) {
			prop.store(out, "User details");
		}
	}

	public static void loadUserDetails(Environment env) {
		Path userConfigPath = CONFIG_PATH.resolve("users.properties");
		Properties prop = new Properties();

		if (Files.exists(userConfigPath)) {
			try (InputStream in = new FileInputStream(
					userConfigPath.toFile())) {
				prop.load(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (prop.size() < 1) {
			String adminUser = env.getProperty("app.default-admin-user");
			if (adminUser != null) {
				prop.put("app.default-user", adminUser);
			}

			String adminPassword = env.getProperty("app.default-admin-pass");
			if (adminPassword != null) {
				prop.put("app.default-pass", adminPassword);
			}

			prop.put("app.default-shell", "auto");
		}

		for (String propName : prop.stringPropertyNames()) {
			System.setProperty(propName, prop.getProperty(propName));
		}

//		System.setProperty("app.default-user",
//				prop.getProperty("app.default-user"));
//		System.setProperty("app.default-pass",
//				prop.getProperty("app.default-pass"));
//		System.setProperty("app.default-shell", prop.getProperty("app.default-shell"));
	}

	public static void checkAndConfigureSSL() {
		try {
			Path certPath = CONFIG_PATH.resolve("cert.p12");
			Path certConf = CONFIG_PATH.resolve("cert.properties");
			if (!Files.exists(certConf)) {
				createSelfSignedCertificate(certPath, certConf);
			}

			System.out.println("Loading existing certificate");
			Properties certProp = new Properties();
			try (InputStream in = new FileInputStream(certConf.toFile())) {
				certProp.load(in);
			}

			System.setProperty("server.ssl.key-store-password",
					certProp.getProperty("key-store-password"));
			System.setProperty("server.ssl.key-alias",
					certProp.getProperty("key-alias"));
			System.setProperty("server.ssl.key-store", certPath.toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	private static void createSelfSignedCertificate(Path certPath,
			Path certConf) throws IOException, NoSuchAlgorithmException,
			OperatorCreationException, CertificateException, KeyStoreException {
		String keystorePassword = UUID.randomUUID().toString();
		X509Certificate cert = null;

		KeyPairGenerator keyPairGenerator = KeyPairGenerator
				.getInstance(CERTIFICATE_ALGORITHM);
		keyPairGenerator.initialize(CERTIFICATE_BITS, new SecureRandom());
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
				new X500Name(CERTIFICATE_DN),
				BigInteger.valueOf(System.currentTimeMillis()),
				new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24),
				new Date(System.currentTimeMillis()
						+ (1000L * 60 * 60 * 24 * 365 * 10)),
				new X500Name(CERTIFICATE_DN), SubjectPublicKeyInfo
						.getInstance(keyPair.getPublic().getEncoded()));

		JcaContentSignerBuilder builder = new JcaContentSignerBuilder(
				"SHA256withRSA");
		ContentSigner signer = builder.build(keyPair.getPrivate());

		byte[] certBytes = certBuilder.build(signer).getEncoded();
		CertificateFactory certificateFactory = CertificateFactory
				.getInstance("X.509");
		cert = (X509Certificate) certificateFactory
				.generateCertificate(new ByteArrayInputStream(certBytes));

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, null);
		keyStore.setKeyEntry(CERTIFICATE_ALIAS, keyPair.getPrivate(),
				keystorePassword.toCharArray(),
				new java.security.cert.Certificate[] { cert });

		try (OutputStream out = new FileOutputStream(certPath.toFile())) {
			keyStore.store(out, keystorePassword.toCharArray());
		}

		Properties certProps = new Properties();
		certProps.setProperty("key-store-password", keystorePassword);
		certProps.setProperty("key-alias", CERTIFICATE_ALIAS);
		certProps.setProperty("key-store", certPath.toString());

		try (OutputStream out = new FileOutputStream(certConf.toFile())) {
			certProps.store(out,
					"Easy cloud shell self signed certificate details");
		}

		System.out.println("Self signed certificate created");

//		X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
//		v3CertGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
//		v3CertGen.setIssuerDN(new X509Principal(CERTIFICATE_DN));
//		v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24));
//		v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * 10)));
//		v3CertGen.setSubjectDN(new X509Principal(CERTIFICATE_DN));
//		v3CertGen.setPublicKey(keyPair.getPublic());
//		v3CertGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
//		cert = v3CertGen.generateX509Certificate(keyPair.getPrivate());
	}
}
