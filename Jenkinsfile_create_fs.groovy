pipeline {
    agent any

    stages {
        stage('Deploy Postgres') {
            steps {
                echo "Deploying Postgres..."
                sh "rm -rf /var/lib/jenkins/.ssh/known_hosts"
                ansiColor('xterm') {
                    ansiblePlaybook( 
                        playbook: 'src/ansible/create_fs.yml',
                        inventory: 'environments/prod/inventory/host.ini',
                        limit:  '${params.host}'
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
