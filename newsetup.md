
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
this example has 1 vg which has 3 disk partitions each of 50GB. 
if you want you can create vg with single parition of 150GB or whatever size you need
```
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
```

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

## Setup postgres operator
    
 **Pre-requisites:**
  
 k8s is installed

 **Run Job:**
  
  http://localhost:8080/job/deploy_postgres_operator/

## setup postgres DB cluster

Default installation will create 

 1. Namespace: locopgdb
 
 2. persistent volumes: 
 
     pg-pv1, pg-pv2, pg-pv3 
 
     based on manifests pv1.yaml, pv2.yaml and pv3.yaml present in 
 
     src/ansible/roles/deploy-postgres/files/postgres-operator/manifests
 
 3. postgres db cluster and dbs based on manifest pgdb.yaml present in 
 
    src/ansible/roles/deploy-postgres/files/postgres-operator/manifests


Incase you want to add new cluster
 
1. Create 3 new pv manifests similar to pv1.yaml(change the name of pvs in the yaml)

2. Create new cluster manifest file similar to pgdb.yaml(change the name of cluster)

3. Update manifest file with required dbs, users and parameters

4. update the following tasks in deploy-postgres role present in 

    src/ansible/roles/deploy-postgres/tasks/main.yml
     
    Update:
 
  - namespace: 
 
  - postgres cluster manifest files
 
  - pv template file names:
 
 
 ```
 - name: create postgres db ns
  shell: bash -ic "kubectl create ns locopgdb"
  tags: pgdb
  ignore_errors: True
 
 
 - name: Create PV
  shell: bash -ic "kubectl apply -f /tmp/postgres-operator/manifests/{{ item }}"
  with_items:
    - 'pv1.yaml'
    - 'pv2.yaml'
    - 'pv3.yaml'
  tags: pgdb
 
 
 - name: Deploy Postgres DB
  shell: bash -ic "kubectl apply -f /tmp/postgres-operator/manifests/pgdb.yaml -n locopgdb"
  tags: pgdb


- name: Wait for postgres db pods to come up
  shell: bash -ic "kubectl get pods -o json -l application=spilo -L spilo-role -n locopgdb"
  register: kubectl_get_pods
  until: kubectl_get_pods.stdout|from_json|json_query('items[*].status.phase')|unique == ["Running"]
  retries: 100
  delay: 15
  tags: pgdb
 
 ```
 
 
## Postgres maintanance activities


 
 ## Cleanup postgres DB cluster
 
Get the DB cluster name:
```
 root@loconav1:~# kubectl get postgresql  -n locopgdb
NAME        TEAM   VERSION   PODS   VOLUME   CPU-REQUEST   MEMORY-REQUEST   AGE   STATUS
loco-pgdb   loco   14        3      20Gi     100m          100Mi            11d   Running
root@loconav1:~#

```
 delete the db cluster you wish to delete
 ```
 kubectl delete postgresql loco-pgdb -n locopgdb
 ```
   wait for all the resources to be cleaned up from namespace
 ```
 kubectl get all -n locopgdb
 ```
   get pvcs associated with above cluster and delete the pvcs
 
 ```
 root@loconav1:~# kubectl get pvc -n locopgdb
NAME                 STATUS   VOLUME   CAPACITY   ACCESS MODES   STORAGECLASS   AGE
pgdata-loco-pgdb-0   Bound    pg-pv1   20Gi       RWO                           11d
pgdata-loco-pgdb-1   Bound    pg-pv3   20Gi       RWO                           11d
pgdata-loco-pgdb-2   Bound    pg-pv2   20Gi       RWO                           11d
root@loconav1:~#

kubectl delete pvc pgdata-loco-pgdb-0 pgdata-loco-pgdb-1 pgdata-loco-pgdb-2 -n locopgdb
 
```
  get pvs associated with the above cluster and delete pvs
 
  example below has pvs being used, you would see pvs in released state

 ```
 root@loconav1:~# kubectl get pv | grep pg 
pg-pv1                                     20Gi         RWO            Retain           Bound    locopgdb/pgdata-loco-pgdb-0                                                                   11d
pg-pv2                                     20Gi         RWO            Retain           Bound    locopgdb/pgdata-loco-pgdb-2                                                                   11d
pg-pv3                                     20Gi         RWO            Retain           Bound    locopgdb/pgdata-loco-pgdb-1                                                                   11d
root@loconav1:~#

 kubectl delete pv pg-pv1 pg-pv2  pg-pv3 
 
 ```
 

## Cleanup entire postgres setup including Operator
 
 First cleanup all the postgres DB clusters using above steps
 
run following steps
 
 ```
helm delete postgres-operator -n postgres-operator
helm install postgres-operator-ui -n postgres-operator
kubectl delete crd operatorconfigurations.acid.zalan.do postgresqls.acid.zalan.do postgresteams.acid.zalan.do
 
 ```
 
 
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

