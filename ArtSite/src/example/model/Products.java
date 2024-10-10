package example.model;

import java.sql.*;

public class Products {
    double price;

    String title, description, artistName, artistCountry, genre, userUpload;
    Date creationDate;

    public void setPrice(int price) {
        this.price = price;
    }

    //OVERLOAD : PRICE CAN BE EITHER INT OR DOUBLE
    public void setPrice(double price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;

    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;

    }

    public void setArtistCountry(String artistCountry) {
        this.artistCountry = artistCountry;

    }

    public void setGenre(String genre) {
        this.genre = genre;

    }

    public void setUserUpload(String userUpload) {
        this.userUpload = userUpload;

    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;

    }

    public double getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistCountry() {
        return artistCountry;
    }

    public String getGenre() {
        return genre;
    }

    public String getUserUpload() {
        return userUpload;
    }

    public Date getCreationDate() {
        return creationDate;
    }



    public Products(double price, String title, String description, String artistName, String artistCountry, String genre, String userUpload, Date creationDate) {
        this.price = price;
        this.title = title;
        this.description = description;
        this.artistName = artistName;
        this.artistCountry = artistCountry;
        this.genre = genre;
        this.userUpload = userUpload;
        this.creationDate = creationDate;
    }

    //OVERLOAD : PRICE CAN BE EITHER INT OR DOUBLE
    public Products(int price, String title, String description, String artistName, String artistCountry, String genre, String userUpload, Date creationDate) {
        this.price = price;
        this.title = title;
        this.description = description;
        this.artistName = artistName;
        this.artistCountry = artistCountry;
        this.genre = genre;
        this.userUpload = userUpload;
        this.creationDate = creationDate;
    }
}