package com.example.nurilmibila;

public class ImageUploadInfo {
    String nominal;
    String image;
    public ImageUploadInfo() {

    }
    public ImageUploadInfo(String nominal, String image) {
        this.nominal = nominal;
        this.image = image;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
