/Users/gitanshgumpu/GitHubGCP/GCPKeys/assignment-363220-d12c87773db0.json

export GOOGLE_APPLICATION_CREDENTIALS="/Users/gitanshgumpu/GitHubGCP/GCPKeys/assignment-363220-d12c87773db0.json"

/Users/gitanshgumpu/GitHubGCP/GCPKeys/dataflowtest@assignment-363220.iam.gserviceaccount.com

dataflowtest@assignment-363220.iam.gserviceaccount.com

gcloud auth activate-service-account
gcloud auth activate-service-account dataflowtest@assignment-363220.iam.gserviceaccount.com --key-file=/Users/gitanshgumpu/GitHubGCP/GCPKeys/assignment-363220-d12c87773db0.json

gcloud config set project assignment-363220

mvn compile exec:java -Dexec.mainClass=com.google.project.beam.ProjectBeamMain

                  InstrumentDataStore
assignment-363220.InstrumentDataStore

com.google.project.beam.ProjectBeamMain

java -jar target/ProjectBeam-1.0-SNAPSHOT.jar --runner=DataflowRunner --project=assignment-363220 --region=europe-west2 --tempLocation=gs://instrument-data-bucket/Temp

java -jar ProjectBeam-1.0-SNAPSHOT.jar --runner=DataflowRunner --project=assignment-363220 --region=europe-west2 --tempLocation=gs://instrument-data-bucket/Temp/

mvn -Pdataflow-runner compile exec:java -Dexec.mainClass=com.google.project.beam.ProjectBeamMain -Dexec.args="--project=assignment-363220 --gcpTempLocation=gs://instrument-data-bucket/Temp/ --output=s://instrument-data-bucket/Output/ --runner=DataflowRunner --region=europe-west2"

mvn -Pdataflow-runner compile exec:java -Dexec.mainClass=com.google.project.beam.ProjectBeamMain -Dexec.args="--project=assignment-363220 --gcpTempLocation=gs://instrument-data-bucket/ --runner=DataflowRunner --region=europe-west2"

mvn -Pdataflow-runner compile exec:java -Dexec.mainClass=com.google.project.beam.ProjectBeamMain -Dexec.args="--project=assignment-363220 --runner=DataflowRunner --region=europe-west2 --tempLocation=gs://instrument-data-bucket/temp/"

https://console.developers.google.com/apis/api/cloudresourcemanager.googleapis.com/overview?project=324179565435 

https://console.developers.google.com/apis/api/cloudresourcemanager.googleapis.com/overview?project=181145493396 

#How to login GCP Cloud from your local machine

gcloud auth activate-service-account mainserviceaccount@symbolic-tape-345822.iam.gserviceaccount.com --key-file=/Users/gitanshgumpu/GitHubGCP/GCPKeys/gssl/symbolic-tape-345822-b3a0589db198.json

# TO REVOKE ALL LOGIN'S AT LOCAL
gcloud auth revoke --all
gcloud auth list

mvn -Pdataflow-runner compile exec:java -Dexec.mainClass=com.google.project.beam.ProjectBeamMain -Dexec.args="--project=symbolic-tape-345822 --runner=DataflowRunner --region=europe-west2"


gcloud config set project symbolic-tape-345822

#Switch Java version in Mac
/usr/libexec/java_home -V
export JAVA_HOME=`/usr/libexec/java_home -v 11.0.1`
java --version