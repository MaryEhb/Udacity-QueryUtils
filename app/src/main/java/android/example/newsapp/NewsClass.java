package android.example.newsapp;

public class NewsClass {
    private final String title , section, author, time,url;

    public NewsClass(String aTitle, String aSection, String aAuthor, String aTime, String aUrl){
        title = aTitle;
        section = aSection;
        author = aAuthor;
        time = aTime;
        url = aUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getSection() {
        return section;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }
}
