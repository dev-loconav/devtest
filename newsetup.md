
# **Setup k8s**

## Creating new cluster

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

  This job will create volume groups, which would be used to create LV and eventually filesystems

**Pre-requisites**
1. disk attached to VM 
2. update "vg" section in inventory as below
   environments/prod/inventory/host_vars/<host>.yaml
this example has 1 vg which has 3 disk partitions each of 50GB. if you want you can create vg with single parition of 150GB or whatever size you need
<pre><code>
vg:
  - no: 1
    vgname: vg1
    disk: /dev/sda
    size: 50000
    label: gpt
  - no: 2
    vgname: vg1
    disk: /dev/sda
    size: 50000
    label: gpt
  - no: 3
    vgname: vg1
    disk: /dev/sda
    size: 50000
    label: gpt
</code></pre>

**Run job:**

http://localhost:8080/job/create_extend_vg

**Parameters:** 
 
provide the "hostname" or "all" for all the hosts in inventory

**Expected output:**
  
  During first run with above input it will create vg1 with 50GB disk partition and add 2 more 50GB partitions. 

  if you run the job again with same input, it will add 3 new partitions to vg1

  if you want to add only 1 partition, you can only give one partition input in the inventory file.
  e.g. 
 <pre><code>
 vg:
  - no: 1
    vgname: vg1
    disk: /dev/sda
    size: 50000
    label: gpt
</code></pre>

  

# **Create or extend FS**
  
This job will create or extend Filesystems

**Pre-requisites**
  
1. Voulme group is created 
2. update "fs" section in inventory as below
   environments/prod/inventory/host_vars/<host>.yaml
this example all the logical volume name, fs name and  needed sizes and if its new or it needs extention. 
<pre><code>
fs:
  - mountpoint: "/kafkazkdata"
    lvname: "kafkazklv"
    vgname: vg1
    size: 25
    mode: new  #new|extend
  - mountpoint: "/miniopgdata"
    lvname: "miniopglv"
    vgname: vg1
    size: 10
    mode: new  #new|extend
  - mountpoint: "/miniopromdata"
    lvname: "miniopromlv"
    vgname: vg1
    size: 10
    mode: new  #new|extend
  - mountpoint: "/pgdata"
    lvname: "pglv"
    vgname: vg1
    size: 25
    mode: new  #new|extend
  - mountpoint: "/jenkinsdata"
    lvname: "jenkinslv"
    vgname: vg1
    size: 20
    mode: new  #new|extend  
  - mountpoint: "/redisdata"
    lvname: "redislv"
    vgname: vg1
    size: 25
    mode: new  #new|extend
  - mountpoint: "/nfsdata"
    lvname: "nfsdatalv"
    vgname: vg1
    size: 15
    mode: new  #new|extend
</code></pre>

**Run job:**
  
http://localhost:8080/job/create_extend_fs

**Parameters:**
  
provide the "hostname" or "all" for all the hosts in inventory

**Expected output:**
  
Required FS are created if more is extend
Required FS are extended if mode is extend





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

