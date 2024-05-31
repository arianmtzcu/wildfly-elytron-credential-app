package com.arian.example.bean;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.wildfly.security.WildFlyElytronProvider;
import org.wildfly.security.credential.Credential;
import org.wildfly.security.credential.KeyPairCredential;
import org.wildfly.security.credential.PasswordCredential;
import org.wildfly.security.credential.SecretKeyCredential;
import org.wildfly.security.credential.source.FactoryCredentialSource;
import org.wildfly.security.credential.store.CredentialStore;
import org.wildfly.security.credential.store.CredentialStoreException;
import org.wildfly.security.credential.store.impl.KeyStoreCredentialStore;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.interfaces.ClearPassword;

@Named
@RequestScoped
public class CredentialStoreBean {

   private static final String DEFAULT_PASSWORD = "changeit";

   private String credentialValue;

   private String credentialAlias;

   private CredentialStore credentialStore;

   @PostConstruct
   public void init() throws NoSuchAlgorithmException {
      try {
         // Registering providers
         Security.addProvider(new WildFlyElytronProvider());
         Security.addProvider(Security.getProvider("SUN"));
         Security.addProvider(new BouncyCastleProvider());  // -->  support AES-256

         // Configuration properties
         final Map<String, String> config = new HashMap<>();
         final String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/configuration/cstore.jceks");
         config.put("location", path);
         // config.put("location", "../standalone/configuration/cstore.jceks");
         config.put("create", "false");
         config.put("keyStoreType", "JCEKS");

         final PasswordCredential passwordCredential = new PasswordCredential(
               ClearPassword.createRaw(ClearPassword.ALGORITHM_CLEAR, DEFAULT_PASSWORD.toCharArray()));
         final FactoryCredentialSource credentialSource = new FactoryCredentialSource(() -> passwordCredential);

         // Get CredentialStore Instance
         credentialStore = CredentialStore.getInstance(KeyStoreCredentialStore.KEY_STORE_CREDENTIAL_STORE);

         // Initialize the Credential Store with providers
         CredentialStore.ProtectionParameter protectionParameter = new CredentialStore.CredentialSourceProtectionParameter(credentialSource);
         Provider[] providers = { new BouncyCastleProvider(), new WildFlyElytronProvider(), Security.getProvider("SUN") };
         credentialStore.initialize(config, protectionParameter, providers);
      } catch (CredentialStoreException e) {
         e.printStackTrace();
      }
   }

   public void loadCredential() {
      if (Objects.isNull(credentialAlias) || credentialAlias.isEmpty()) {
         credentialValue = "Credential alias is required";
         return;
      }

      try {
         final Credential credential = credentialStore.retrieve(credentialAlias, Credential.class);

         if (credential instanceof PasswordCredential) {
            final PasswordCredential passwordCredential = (PasswordCredential) credential;
            final Password password = passwordCredential.getPassword();

            if (password instanceof ClearPassword) {
               final ClearPassword clearPassword = (ClearPassword) password;
               credentialValue = new String(clearPassword.getPassword());
            }

         } else if (credential instanceof KeyPairCredential) {
            final KeyPairCredential keyPairCredential = (KeyPairCredential) credential;
            final KeyPair keyPair = keyPairCredential.getKeyPair();
            final PublicKey publicKey = keyPair.getPublic();
            final PrivateKey privateKey = keyPair.getPrivate();

            final String publicKeyEncoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            final String privateKeyEncoded = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            credentialValue = "Public Key: " + publicKeyEncoded + "\nPrivate Key: " + privateKeyEncoded;

         } else if (credential instanceof SecretKeyCredential) {
            final SecretKeyCredential secretKeyCredential = (SecretKeyCredential) credential;
            credentialValue = new String(secretKeyCredential.getSecretKey().getEncoded());

         } else {
            credentialValue = "Unsupported credential type";
         }

      } catch (final CredentialStoreException e) {
         credentialValue = "Error retrieving credential: " + e.getMessage();
         e.printStackTrace();
      }
   }

   public String getCredentialValue() {
      return credentialValue;
   }

   public String getCredentialAlias() {
      return credentialAlias;
   }

   public void setCredentialAlias(String credentialAlias) {
      this.credentialAlias = credentialAlias;
   }
}
