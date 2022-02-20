pipeline {
    agent any

    stages {
        stage('Deploy Minio') {
            steps {
                echo "Deploying Minio..."
                ansiColor('xterm') {
                    ansiblePlaybook( 
                        playbook: 'src/ansible/deploy_minio.yaml',
                        inventory: 'environments/prod/inventory/host.ini', 
                        colorized: true) 
                }
            }
        
        }
    }
    
    post {
      // Clean after build
      always {
          cleanWs()
      }
   }
}
