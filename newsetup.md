
# **Setup k8s**

Inventory files to be updated:
environments/prod/inventory/group_vars/all/main.yaml -> this one is generic can be copied as it is

environments/prod/inventory/host.ini

<pre><code>
[masters]
loconav1 ansible_host=192.168.30.2 ansible_ssh_common_args='-o StrictHostKeyChecking=no' rke2_type=server

[workers]
loconav2 ansible_host=192.168.30.3 ansible_ssh_common_args='-o StrictHostKeyChecking=no' rke2_type=agent
loconav3 ansible_host=192.168.30.4 ansible_ssh_common_args='-o StrictHostKeyChecking=no' rke2_type=agent
loconav4 ansible_host=192.168.30.5 ansible_ssh_common_args='-o StrictHostKeyChecking=no' rke2_type=agent


[k8s_cluster:children]
masters
workers

[all:vars]
ansible_connection=ssh
ansible_user=deployer
ansible_ssh_pass=deployer
k8s_master=loconav1
</code></pre>

  **Run job:**

  http://localhost:8080/job/Deploy_rke2_cluster/

## maintainance activities

### Adding new node to the k8s cluster

  Update the environments/prod/inventory/host.ini to add new vm details in workers section

  **Run job:**

  http://localhost:8080/job/Deploy_rke2_cluster/

### Remove worker node

  **Run job:**

  http://localhost:8080/job/remove_k8s_worker_node/build?delay=0sec

  **parameters:**

  Provide k8s worker node name as a list
  Example: ['vm23','vm24']

### Remove rke2 installation

  **Run job:**

  http://localhost:8080/job/delete_rke2_cluster/


# **Setup Filesystems**

Run 2 jobs as per instructions below to create FS

https://github.com/loconav-tech/local-server-setup#create-or-extend-vg

https://github.com/loconav-tech/local-server-setup#create-or-extend-vg




# **Setup Postgres**

Follow the section to setup postgres operator and postgres db

https://github.com/loconav-tech/local-server-setup#postgres-deployment

# **Setup Redis**

Follow section below to setup redis operator and redis cluster

https://github.com/loconav-tech/local-server-setup#redis-deployment

# **Setup Kafka**

Follow section below to setup kafka operator, create kafka cluster and topics

https://github.com/loconav-tech/local-server-setup#kafka-deployment

# **Setup end points**

Follow section below to setup end points for above services

https://github.com/loconav-tech/local-server-setup/blob/master/README.md#create-aliases-for-postgres-kafka-redis-schema-registry

# **Deploy telematics**

**Pre-requisites**

All the helm charts are present at location below

https://github.com/loconav-tech/local-server-setup/tree/master/helm-charts

every helm chart has values configured for on-prem/oman region

**Deployment**

Run the job below with appropriate parameters line

provider - onprem

region - oman

http://localhost:8080/job/deploy_telematics

# **Deploy linehaul**

**Pre-requisites**

All the helm charts are present at location below

https://github.com/loconav-tech/local-server-setup/tree/master/helm-charts

every helm chart has values configured for on-prem/oman region

**Deployment**

Run the job below with appropriate parameters line

provider - onprem

region - oman

http://localhost:8080/job/deploy_linehaul/

# **Setup Minio**

follow section below to setup minio partitions, deploy minio operator and format minio partions

https://github.com/loconav-tech/local-server-setup#minio-deployment

