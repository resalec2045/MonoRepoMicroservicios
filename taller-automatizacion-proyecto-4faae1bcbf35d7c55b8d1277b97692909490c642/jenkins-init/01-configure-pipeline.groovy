import jenkins.model.*
import hudson.security.*
import jenkins.install.*

// Configuración básica de seguridad y pipeline
Jenkins.instance.setInstallState(InstallState.INITIAL_SETUP_COMPLETED)

try {
    def CpsFlowDefinition = org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
    def WorkflowJob = org.jenkinsci.plugins.workflow.job.WorkflowJob
    // Crear un pipeline básico si no existe
    def jobName = "CI-Pipeline"
    if (Jenkins.instance.getItem(jobName) == null) {
        def job = Jenkins.instance.createProject(WorkflowJob, jobName)
        job.definition = new CpsFlowDefinition('''
pipeline {
    agent any
    stages {
        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
    }
}
''', true)
        job.save()
    }
} catch (Throwable e) {
    println "[WARN] Plugins de pipeline aún no disponibles: ${e.message}"
}
workflow-aggregator
git
sonar
gitlab-plugin
blueocean
credentials-binding
