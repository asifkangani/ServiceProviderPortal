package com.example.organization.dto;

public class DocumentResponseDTO {

    private String documentLabel;
    private String documentType;
    private String documentData;

    public String getDocumentLabel() {
        return documentLabel;
    }

    public void setDocumentLabel(String documentLabel) {
        this.documentLabel = documentLabel;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentData() {
        return documentData;
    }

    public void setDocumentData(String documentData) {
        this.documentData = documentData;
    }

    @Override
    public String toString() {
        return "DocumentResponseDTO{" +
                "documentLabel='" + documentLabel + '\'' +
                ", documentType='" + documentType + '\'' +
                ", documentData='" + documentData + '\'' +
                '}';
    }
}
