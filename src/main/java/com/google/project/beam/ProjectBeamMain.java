package com.google.project.beam;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProjectBeamMain {

    static Logger logger = LoggerFactory.getLogger(ProjectBeamMain.class);
    private static String HEADERS = "instrid,ratingagency,ratinggroup,code,validfrom,validto,ratingstatus,ratingpurposetype";

    public static void main(String[] args) {
        logger.info("ProjectBeam Main Started >> ");
        boolean isStreaming = false;

        //tableSpec = [project_id]:[dataset_id].[table_id]a
        String masterDataset = "gs://instrumentdatabucket/input/master_dataset.csv";
        String rawDataset = "gs://instrumentdatabucket/input/raw_dataset.csv";
        String tempLocationPath = "gs://instrumentdatabucket/temp/";

        String projectID = "symbolic-tape-345822";
        String bigQueryInstrDataSet = "instrument_rating";
        String rawInstrumentRatingTable = "raw_instrument_rating";
        String masterInstrumentRatingTable = "master_instrument_rating";
        String stagingInstrumentRatingTable = "staging_instrument_rating";

        logger.info("ProjectBeam Check 01 >> ");
        //Raw BigQuery Table Reference
        TableReference rawInstrumentTableRef = new TableReference();
        rawInstrumentTableRef.setProjectId(projectID);
        rawInstrumentTableRef.setDatasetId(bigQueryInstrDataSet);
        rawInstrumentTableRef.setTableId(rawInstrumentRatingTable);

        logger.info("ProjectBeam Check 02 >> ");
        //Master BigQuery Table Reference
        TableReference masterInstrumentTableRef = new TableReference();
        masterInstrumentTableRef.setProjectId(projectID);
        masterInstrumentTableRef.setDatasetId(bigQueryInstrDataSet);
        masterInstrumentTableRef.setTableId(masterInstrumentRatingTable);
        logger.info("ProjectBeam Check 03 >> ");

        //Staging BigQuery Table Reference
        TableReference stagingInstrumentTableRef = new TableReference();
        stagingInstrumentTableRef.setProjectId(projectID);
        stagingInstrumentTableRef.setDatasetId(bigQueryInstrDataSet);
        stagingInstrumentTableRef.setTableId(stagingInstrumentRatingTable);
        logger.info("ProjectBeam Check 04 >> ");

        // Create the pipeline.
        PipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().create();

        // This is required for BigQuery
        options.setTempLocation(tempLocationPath);
        options.setJobName("ProjectBeamJob");

        Pipeline pipeline = Pipeline.create(options);

        //Step 01
        pipeline
                .apply("Read Raw Dataset", TextIO.read().from(rawDataset))
                .apply("Log messages for Raw", ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        logger.info("Processing row: " + c.element());
                        c.output(c.element());
                    }
                })).apply("Convert to BigQuery TableRow", ParDo.of(new FormatForBigquery()))
                .apply("Write into BigQuery Raw Table", BigQueryIO.writeTableRows().to(rawInstrumentTableRef).withSchema(FormatForBigquery.getSchema()).withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED).withWriteDisposition(isStreaming ? BigQueryIO.Write.WriteDisposition.WRITE_APPEND : BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));

        //Step 02
        pipeline
                .apply("Read Master Dataset", TextIO.read().from(masterDataset))
                .apply("Log messages for Master", ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        logger.info("Processing row: " + c.element());
                        c.output(c.element());
                    }
                }))
                .apply("Convert to BigQuery TableRow", ParDo.of(new FormatForBigquery()))
                .apply("Write into Master Table",
                        BigQueryIO.writeTableRows().to(masterInstrumentTableRef).withSchema(FormatForBigquery.getSchema()).withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED).withWriteDisposition(isStreaming ? BigQueryIO.Write.WriteDisposition.WRITE_APPEND : BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));


        //Step 03
        pipeline
                .apply("Read Rows from Raw Table",
                        BigQueryIO.readTableRows().fromQuery(String.format("SELECT * FROM `%s.%s.%s` WHERE ((InstrId is not NULL) AND (ValidFrom is not NULL))", projectID, bigQueryInstrDataSet, rawInstrumentRatingTable)).usingStandardSql())
                .apply("Write rows into Staging Instrument Table",
                        BigQueryIO.writeTableRows()
                                .to(String.format("%s:%s.%s", projectID, bigQueryInstrDataSet, stagingInstrumentRatingTable))
                                .withSchema(FormatForBigquery.getSchema())
                                // For CreateDisposition:
                                // - CREATE_IF_NEEDED (default): creates the table if it doesn't exist, a schema is
                                // required
                                // - CREATE_NEVER: raises an error if the table doesn't exist, a schema is not needed
                                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                                // For WriteDisposition:
                                // - WRITE_EMPTY (default): raises an error if the table is not empty
                                // - WRITE_APPEND: appends new rows to existing rows
                                // - WRITE_TRUNCATE: deletes the existing rows before writing
                                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));

        pipeline.run().waitUntilFinish();

        logger.info("ProjectBeamMain End  >> ");
    }

    /**
     *
     */
    public static class FormatForBigquery extends DoFn<String, TableRow> {
        private String[] columnNames = HEADERS.split(",");

        @ProcessElement
        public void processElement(ProcessContext c) {
            TableRow row = new TableRow();
            String[] parts = c.element().split(",");
            if (!c.element().contains(HEADERS)) {
                for (int i = 0; i < parts.length; i++) {
                    // No type conversion at the moment.
                    row.set(columnNames[i], parts[i]);
                }
                c.output(row);
            }
        }

        /**
         * Defines the BigQuery schema used for the output.
         */
        static TableSchema getSchema() {
            List<TableFieldSchema> fields = new ArrayList<>();
            // Currently store all values as String
            fields.add(new TableFieldSchema().setName("InstrId").setType("STRING"));
            fields.add(new TableFieldSchema().setName("RatingAgency").setType("STRING"));
            fields.add(new TableFieldSchema().setName("RatingGroup").setType("STRING"));
            fields.add(new TableFieldSchema().setName("Code").setType("STRING"));
            fields.add(new TableFieldSchema().setName("ValidFrom").setType("STRING"));
            fields.add(new TableFieldSchema().setName("ValidTo").setType("STRING"));
            fields.add(new TableFieldSchema().setName("RatingStatus").setType("STRING"));
            fields.add(new TableFieldSchema().setName("RatingPurposeType").setType("STRING"));
            return new TableSchema().setFields(fields);
        }
    }

}