package com.google.project.beam.entities;/*
 *
 * @project - ProjectBeam
 * @author - Babu Gumpu , on 09/11/2022
 *
 */

import java.sql.Date;

public class InstrumentData {

    private String InstrID;
    private String RatingAgency;
    private String RatingGroup;
    private String Code;
    private Date ValidFrom;
    private Date ValidTo;
    private String RatingStatus;
    private String RatingPurposeType;


    public String getInstrID() {
        return InstrID;
    }

    public void setInstrID(String instrID) {
        InstrID = instrID;
    }

    public String getRatingAgency() {
        return RatingAgency;
    }

    public void setRatingAgency(String ratingAgency) {
        RatingAgency = ratingAgency;
    }

    public String getRatingGroup() {
        return RatingGroup;
    }

    public void setRatingGroup(String ratingGroup) {
        RatingGroup = ratingGroup;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public Date getValidFrom() {
        return ValidFrom;
    }

    public void setValidFrom(Date validFrom) {
        ValidFrom = validFrom;
    }

    public Date getValidTo() {
        return ValidTo;
    }

    public void setValidTo(Date validTo) {
        ValidTo = validTo;
    }

    public String getRatingStatus() {
        return RatingStatus;
    }

    public void setRatingStatus(String ratingStatus) {
        RatingStatus = ratingStatus;
    }

    public String getRatingPurposeType() {
        return RatingPurposeType;
    }

    public void setRatingPurposeType(String ratingPurposeType) {
        RatingPurposeType = ratingPurposeType;
    }
}
