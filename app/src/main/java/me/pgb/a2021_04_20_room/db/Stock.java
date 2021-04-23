package me.pgb.a2021_04_20_room.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Stock {

    public Stock(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="price")
    public double price;

}
