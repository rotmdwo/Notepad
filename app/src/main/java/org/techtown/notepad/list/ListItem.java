package org.techtown.notepad.list;

public class ListItem {
    Boolean isWebImage;
    String image_path;
    String title;
    String content;
    String time;

    public ListItem(Boolean isWebImage, String image_path, String title, String content, String time) {
        this.isWebImage = isWebImage;
        this.image_path = image_path;
        this.title = title;
        this.content = content;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
