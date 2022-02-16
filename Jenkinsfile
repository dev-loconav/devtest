pipeline {
    agent any

    stages {
        stage('Create K8S Cluster') {
            steps {
                echo "Creating K8S cluster..."
                ansiColor('xterm') {
                    ansiblePlaybook( 
                        playbook: 'rke2-cluster/playbooks/create_rke.yaml',
                        inventory: 'rke2-cluster/inventory/host.ini', 
                        credentialsId: 'sample-ssh-key',
                        colorized: true) 
                }
            }
        
        }
    }
}
