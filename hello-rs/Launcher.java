import it.thomasjohansen.launcher.web.tomcat.TomcatLauncher;

import java.nio.file.Paths;

/**
 * @author thomas@thomasjohansen.it
 */
public class Launcher {

    public static void main(String[] args) throws Exception {
        new TomcatLauncher(TomcatLauncher.configuration()
                .addSecureConnector(8081, Paths.get("/Users/thomas/Google Drive/TJIT/Prosjekter/sso-demo/hello.thomasjohansen.it.p12"), "changeit")
                .addApplication("", Launcher.class.getProtectionDomain().getCodeSource().getLocation().getFile())
                .build()
        ).launch();
    }

}
