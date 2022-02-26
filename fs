Job1: creating partitions for minio disks
Create lvm partitions for miniopartitions
record partuid and keep it on the VM somewhere --- contentious

Job2: creations lvm partitions 
create lvm partitions - based on no of parts
create the pv
create the vg
create lvms for each filesystem
create fs
mount it


job3: extend filesyestems
extend lvm
extend fs_device

job4: extend vg
get the disk_name
get no of partitions
get size of each partition



job1: Deploy minio
job2: prepare disks for minio tenents
  go to the server
  find minio partuid file
  derive disk name from there
  check if it is in ready state
  /dev/sdb1 1.8 TiB 964 MiB xfs - ltda-srv001 - Ready
  if not,
    kubectl directpv drives format --drives /dev/sdc2 --nodes vm12 --force
