pipeline {
    agent any

    stages {
        stage('Create K8S Cluster') {
            steps {
                echo "Creating K8S cluster..."
                ansiColor('xterm') {
                    ansiblePlaybook( 
                        playbook: 'src/ansible/create_rke.yaml',
                        inventory: 'environments/prod/inventory/host.ini', 
                        colorized: true) 
                }
            }
        
        }
        stage('Create partitions') {
            steps {
                echo "Creating partitions..."
                ansiColor('xterm') {
                    ansiblePlaybook( 
                        playbook: 'src/ansible/create_fs.yml',
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
