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

        TableReference rawInstrumentTableRef = new TableReference();
        rawInstrumentTableRef.setProjectId("symbolic-tape-345822");
        rawInstrumentTableRef.setDatasetId("instrument_rating");
        rawInstrumentTableRef.setTableId("raw_instrument_rating");

        //symbolic-tape-345822.instrument_rating.master_instrument_rating
        TableReference masterInstrumentTableRef = new TableReference();
        masterInstrumentTableRef.setProjectId("symbolic-tape-345822");
        masterInstrumentTableRef.setDatasetId("instrument_rating");
        masterInstrumentTableRef.setTableId("master_instrument_rating");
        // Create the pipeline.
        PipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().create();

        // This is required for BigQuery
        options.setTempLocation(tempLocationPath);
        options.setJobName("ProjectBeamJob");

        Pipeline pipeline = Pipeline.create(options);
        pipeline.apply("Read RAW Dataset", TextIO.read().from(rawDataset))
                .apply("Log messages", ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        logger.info("Processing row: " + c.element());
                        c.output(c.element());
                    }
                })).apply("Convert to BigQuery TableRow", ParDo.of(new FormatForBigquery()))
                .apply("Write into BigQuery",
                        BigQueryIO.writeTableRows().to(rawInstrumentTableRef).withSchema(FormatForBigquery.getSchema())
                                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                                .withWriteDisposition(isStreaming ? BigQueryIO.Write.WriteDisposition.WRITE_APPEND
                                        : BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));

        pipeline.apply("Read Master Dataset", TextIO.read().from(masterDataset))
                .apply("Log messages", ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        logger.info("Processing row: " + c.element());
                        c.output(c.element());
                    }
                })).apply("Convert to BigQuery TableRow", ParDo.of(new FormatForBigquery()))
                .apply("Write into BigQuery",
                        BigQueryIO.writeTableRows().to(masterInstrumentTableRef).withSchema(FormatForBigquery.getSchema())
                                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                                .withWriteDisposition(isStreaming ? BigQueryIO.Write.WriteDisposition.WRITE_APPEND
                                        : BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));
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