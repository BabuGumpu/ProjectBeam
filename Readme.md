### -How to set up Project Beam & Dataflow,BigQuery, Data Storage Bucket

### How to setup this Project Beam

1. Create GCP Service account
2. Add roles to the Service Account BigQuery Admin, Cloud Storage Admin, Dataflow Admin
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



### Other Commands
```sh
gcloud beta dataflow jobs list --status=active 

gcloud dataflow jobs --help

cancel
Cancels all jobs that match the command line arguments.

describe
Outputs the Job object resulting from the Get API.

drain
Drains all jobs that match the command line arguments.

list
Lists all jobs in a particular project.

run
Runs a job from the specified path.

show
Shows a short description of the given job.
```




/Users/gitanshgumpu/GitHubGCP/GCPKeys/new/assignment-363220-2da137020b2c.json

gcloud auth activate-service-account new-service-ac@assignment-363220.iam.gserviceaccount.com --key-file=/Users/gitanshgumpu/GitHubGCP/GCPKeys/new/assignment-363220-2da137020b2c.json
gcloud config set project assignment-363220

mvn -Pdataflow-runner compile exec:java -Dexec.mainClass=com.google.project.beam.ProjectBeamMain -Dexec.args="--project=assignment-363220 --runner=DataflowRunner --region=europe-west2"
projectaserviceaccount.json

gcloud auth activate-service-account projecta@assignment-363220.iam.gserviceaccount.com --key-file=/Users/gitanshgumpu/GitHubGCP/GCPKeys/new/projectaserviceaccount.json