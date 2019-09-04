package com.ngandroidaja.examplefirebase.model;

public class Biodata {

    private String nama;
    private String notelp;
    private String alamat;
    private String kampus;
    private String key;
    private String url;

    public Biodata(String nama, String notelp, String alamat, String kampus, String url) {
        this.nama = nama;
        this.notelp = notelp;
        this.alamat = alamat;
        this.kampus = kampus;
        this.url = url;
    }

    public Biodata() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNotelp() {
        return notelp;
    }

    public void setNotelp(String notelp) {
        this.notelp = notelp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKampus() {
        return kampus;
    }

    public void setKampus(String kampus) {
        this.kampus = kampus;
    }

    @Override
    public String toString() {
        return " " + nama + "\n" + " " + notelp + "\n" + " " + alamat + "\n"
                + " " + kampus + "\n" + " " + url;
    }
}
