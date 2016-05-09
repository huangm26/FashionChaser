package aws;

/**
 * Created by huangm26 on 5/4/16.
 */
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;


@DynamoDBTable(tableName = "Clothes")
public class Clothes_items {
    private String fileuri;
    private String username;
    private String color;
    private String colorValue;
    private String clothesStyle;

    @DynamoDBHashKey(attributeName = "fileuri")
    public String getFileuri() {
        return fileuri;
    }

    public void setFileuri(String fileuri) {
        this.fileuri = fileuri;
    }

    @DynamoDBAttribute(attributeName = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBAttribute(attributeName = "color")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @DynamoDBAttribute(attributeName = "colorValue")
    public String getColorValue() {
        return colorValue;
    }

    public void setColorValue(String colorValue) {
        this.colorValue = colorValue;
    }

    @DynamoDBAttribute(attributeName = "clothesStyle")
    public String getClothesStyle() {
        return clothesStyle;
    }

    public void setClothesStyle(String clothesStyle) {
        this.clothesStyle = clothesStyle;
    }
}
