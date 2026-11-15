# Kubernetes deployment reference

Compose is the verified local runtime. These manifests describe the same five workloads in the claimspace namespace. They require a local cluster, a default storage class and application images loaded into that cluster. All 13 resources passed strict kubeconform schema validation. A live Kubernetes deployment was not performed for this submission. The web upstream names assume the usual cluster.local DNS domain.

1. Build or load the three application images with tags localhost/claimspace/claims:1.0.0, localhost/claimspace/insights:1.0.0 and localhost/claimspace/web:1.0.0.
2. Import them using the cluster's own image importer. Host Podman images do not automatically appear in a separate Kubernetes node.
3. Apply: kubectl apply -f deploy/k8s/claimspace.yaml.
4. Observe: kubectl -n claimspace get pods,pvc and wait for readiness. Application pods may restart while database initialisation completes.
5. Open a local tunnel: kubectl -n claimspace port-forward service/web 8080:8080.

Credentials are explicit demo values. SQL role passwords in the initialisation ConfigMap must match the demo Secret. This is not a production secret-management example. PostgreSQL and Kafka each have one replica and a 2 GiB persistent volume; this provides persistence, not high availability. Replace images, secrets, storage, TLS, replication and operational controls for a real deployment.

Deleting the namespace removes this demonstration and may remove its persistent data, depending on the storage class. Export anything needed before teardown.
