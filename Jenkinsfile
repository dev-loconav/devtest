pipeline {
    agent any
parameters {
  string(name: 'environ', defaultValue: 'nepal', description: 'environment')
 }
  
    stages {
        stage('Create K8S Cluster') {
            steps {
                echo "Creating K8S cluster..."
                sh "rm -rf /var/lib/jenkins/.ssh/known_hosts"
                ansiColor('xterm') {
                    ansiblePlaybook( 
                        playbook: 'src/ansible/create_rke.yaml',
                        inventory: 'environments/${environ}/inventory/host.ini', 
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
