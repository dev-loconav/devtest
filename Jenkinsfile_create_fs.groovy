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
                        playbook: 'src/ansible/create_fs.yml',
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
