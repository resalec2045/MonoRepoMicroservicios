import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
import org.sonarsource.scanner.jenkins.*

try {
    def StringCredentialsImpl = org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
    def SonarInstallation = org.sonarsource.scanner.jenkins.SonarInstallation
    // Ruta donde el token es guardado por el script de SonarQube
    String tokenFile = "/sonar-init/sonar-token.txt"
    String sonarToken = null
    try {
        sonarToken = new File(tokenFile).text.trim()
    } catch (Exception e) {
        println "No se pudo leer el token de SonarQube: ${e.message}"
    }
    if (sonarToken) {
        // Crear credencial tipo Secret Text
        def credentialsStore = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
        def credentials = new StringCredentialsImpl(
            com.cloudbees.plugins.credentials.CredentialsScope.GLOBAL,
            "sonar-token", // ID de la credencial
            "Token SonarQube para análisis automático",
            Secret.fromString(sonarToken)
        )
        credentialsStore.addCredentials(com.cloudbees.plugins.credentials.domains.Domain.global(), credentials)
        // Configurar SonarQube server en Jenkins
        def sonarDesc = Jenkins.instance.getDescriptorByType(org.sonarsource.scanner.jenkins.SonarGlobalConfiguration.class)
        def sonarServers = [
            new SonarInstallation(
                "SonarQube", // Nombre de la instalación
                "http://sonarqube:9000", // URL interna del contenedor
                "sonar-token", // ID de la credencial
                null, null, null, null, false, null
            )
        ]
        sonarDesc.setInstallations(sonarServers as SonarInstallation[])
        sonarDesc.save()
        println "SonarQube y credencial configurados automáticamente."
    } else {
        println "Token de SonarQube no encontrado, no se configuró la credencial."
    }
} catch (Throwable e) {
    println "[WARN] Plugins de SonarQube o credenciales aún no disponibles: ${e.message}"
}
