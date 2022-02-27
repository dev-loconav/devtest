pipeline {
    agent any
parameters {
  string(name: 'hosts', defaultValue: 'all', description: 'Hosts on which FS to be created')

 }
    stages {
        stage('Deploy Postgres') {
            steps {
                echo "Deploying Postgres..."
                sh "rm -rf /var/lib/jenkins/.ssh/known_hosts"
                ansiColor('xterm') {
                    ansiblePlaybook( 
                        playbook: 'src/ansible/minio_disks.yaml',
                        inventory: 'environments/prod/inventory/host.ini',
                        limit:  "${params.hosts}",
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
