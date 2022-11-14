### How to set up Project Beam & Dataflow,BigQuery, Data Storage Bucket

### How to setup this Project Beam

1. Create GCP Service account
2. Add roles to the Service Account BigQuery Admin, Compute Storage Admin, Dataflow Admin
3. Download a key.json file at local folder
4. Create a Bucket and BigQuery into the same region.

### How to login GCP Cloud from your local machine
  ```sh
  gcloud auth activate-service-account mainserviceaccount@symbolic-tape-345822.iam.gserviceaccount.com --key-file=/Users/gitanshgumpu/GitHubGCP/GCPKeys/gssl/symbolic-tape-345822-b3a0589db198.json
  
  gcloud config set project symbolic-tape-345822
  ```




### TO REVOKE ALL LOGIN'S AT LOCAL - Incase you need
```sh
gcloud auth revoke --all
gcloud auth list
```
### This command will create a piple in to GCP Cloud Dataflow Pipelines
```sh
mvn -Pdataflow-runner compile exec:java -Dexec.mainClass=com.google.project.beam.ProjectBeamMain -Dexec.args="--project=symbolic-tape-345822 --runner=DataflowRunner --region=europe-west2"
```


### Switch Java version in Mac
```sh
$ /usr/libexec/java_home -V
export JAVA_HOME=`/usr/libexec/java_home -v 11.0.1`
java --version
```



