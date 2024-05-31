![WildFly Elytron Credential Viewer](src/main/webapp/resources/images/WildFly%20Elytron%20Credential%20Viewer.png)
This project is a Java EE application developed with Java 11 that allows you to view the values stored in a credential store using WildFly's Elytron technology. The application deploys a web interface from which you can explore the different types of credentials stored in a JCEKS file.

## Features
- **Technology:** Java EE 8, WildFly Elytron
- **Frontend:** JSF (JavaServer Faces)
- **Backend:** WildFly Elytron for credential management
- **Credential Store:** Utilizes a JCEKS format credential store (`cstore.jceks`).
- **Default Key:** The default key to unlock the vault file is **`changeit`**.

## Stored Credentials

The application accesses a credential store (`cstore.jceks`) that contains the following credentials:

| Entry Name | Type                   | Algorithm   | Values                                                                          |
|------------|------------------------|-------------|---------------------------------------------------------------------------------|
| arian      | Password Credential    | clear       | ELYTRON                                                                         |
| kp1        | Key Pair Credential    | RSA         | Public Key: MIIBIjANBgkqhkiG9w0BAQ...<br/>Private Key: MIIEvQIBADANBgkqhkiG9... |
| kp2        | Key Pair Credential    | RSA         | Public Key: MIIBIjANBgkqhkiG9w0BAQ...<br/>Private Key: MIIEvQIBADANBgkqhkiG9... |
| sc1        | Secret Key Credential  | AES         | ��]2[_�n��#V�Eق�%��ߢ�ԝ�g                                                        |
| sc2        | Secret Key Credential  | AES         | ���$3Mcy��nM8a���_,��B�����                                                     |
| secret     | Password Credential    | clear       | changeit                                                                        |

These entries represent different types of credentials stored in the `cstore.jceks` file, which the application reads and displays.

## Environment Setup
1. **System Requirements:**
    - Java 11 installed and configured.
    - WildFly 26 or higher installed.
    - Maven 3.6.3 or higher.

2. **Credential Store Configuration:**
    - The `cstore.jceks` file should be located in the project's `resources` folder.
    - Ensure the key `changeit` is used to access the credential store.

3. **Build and Deploy:**
    - Build the project using Maven:
      ```bash
      mvn clean install
      ```
    - Deploy the generated WAR file (`wildfly-elytron-credential-app.war`) on WildFly.

4. **Accessing the Application:**
    - Once deployed, the application will be available at `http://localhost:8080/wildfly-elytron-credential-app`.

## Key Dependencies
- **Java EE API:** Provides the standard APIs for Java EE 8.
- **WildFly Elytron:** Security framework for WildFly.
- **Bouncy Castle:** Cryptography library used for secure credential handling.

## Contributions
This project is open to contributions. If you'd like to collaborate, please open an issue or send a pull request.

## License
This project is licensed under the MIT License. See the [LICENSE](https://opensource.org/license/MIT) file for more details.