package com.example.group.classhelper;

class Info {
    private String id = "（空）";
    private String head = "（空）";
    private String body = "（空）";
    private String type = "（空）";
    private String date = "（空）";
    private String author = "（空）";

    public void setId(String id) {
        if (id == null) {
            return;
        }
        this.id = id;
    }

    public void setHead(String head) {
        if (head == null) {
            return;
        }
        this.head = head;
    }

    public void setBody(String body) {
        if (body == null) {
            return;
        }
        this.body = body;
    }

    public void setType(String type) {
        if (type == null) {
            return;
        }
        this.type = type;
    }

    public void setDate(String date) {
        if (date == null) {
            return;
        }
        this.date = date;
    }

    public void setAuthor(String author) {
        if (author == null) {
            return;
        }
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public String getHead() {
        return head;
    }

    public String getBody() {
        return body;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }
}
