pipeline {
    agent any

    stages {
        stage('Create K8S Cluster') {
            steps {
                echo "Creating K8S cluster..."
                sh """
                  cd devtest/rke2-cluster
                  ansible-playbook ./playbooks/create_rke.yaml -i ./inventory/host.ini
                """
            }
        
        }
    }
}
