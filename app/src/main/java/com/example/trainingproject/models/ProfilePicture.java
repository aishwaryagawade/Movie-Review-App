package com.example.trainingproject.models;

public class ProfilePicture {

    private String fileData;
    private String fileId;
    private String fileName;
    private String fileType;


    public ProfilePicture(String fileData, String fileId, String fileName, String fileType) {
        this.fileData = fileData;
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public String getFileData() {
        return fileData;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    @Override
    public String toString() {
        return "ProfilePicture{" +
                "fileData='" + fileData + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
