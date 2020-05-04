package org.techtown.notepad.list;

public class ListItem {
    Boolean isWebImage;
    String imagePath;
    String title;
    String content;
    String time;

    public ListItem(Boolean isWebImage, String imagePath, String title, String content, String time) {
        this.isWebImage = isWebImage;
        this.imagePath = imagePath;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
