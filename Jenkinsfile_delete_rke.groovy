pipeline {
    agent any

    stages {
        stage('Delete K8S Cluster') {
            steps {
                echo "Deleting K8S cluster..."
                ansiColor('xterm') {
                    ansiblePlaybook( 
                        playbook: 'src/ansible/delete_rke.yaml',
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
