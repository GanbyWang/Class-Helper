package com.example.group.classhelper;

class Vote {
    private String id = "（空）";
    private String head = "（空）";
    private String link = "（空）";
    private String date = "（空）";
    private String author = "（空）";

    void setId(String id) {
        if (id == null) {
            return;
        }
        this.id = id;
    }

    void setHead(String head) {
        if (head == null) {
            return;
        }
        this.head = head;
    }

    void setLink(String link) {
        if (link == null) {
            return;
        }
        this.link = link;
    }

    void setDate(String date) {
        if (date == null) {
            return;
        }
        this.date = date;
    }

    void setAuthor(String author) {
        if (author == null) {
            return;
        }
        this.author = author;
    }

    String getId() {
        return id;
    }

    String getHead() {
        return head;
    }

    String getLink() {
        return link;
    }

    String getDate() {
        return date;
    }

    String getAuthor() {
        return author;
    }
}
