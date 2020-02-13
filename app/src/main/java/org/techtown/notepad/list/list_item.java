package org.techtown.notepad.list;

public class list_item {
    Boolean isWebImage;
    String image_path;
    String title;
    String content;

    public list_item(Boolean isWebImage, String image_path, String title, String content) {
        this.isWebImage = isWebImage;
        this.image_path = image_path;
        this.title = title;
        this.content = content;
    }

    public Boolean getWebImage() {
        return isWebImage;
    }

    public void setWebImage(Boolean webImage) {
        isWebImage = webImage;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
