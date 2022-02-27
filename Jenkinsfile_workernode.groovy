pipeline {
    agent any

    stages {
        stage('Remove Node') {
            steps {
                echo "Removing node..."
                sh "rm -rf /var/lib/jenkins/.ssh/known_hosts"
                ansiColor('xterm') {
                    ansiblePlaybook( 
                        playbook: 'src/ansible/remove_node.yaml',
                        inventory: 'environments/prod/inventory/host.ini',
                        extras: "${remove_node_list}",
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
