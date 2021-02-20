package demo.emtiaztafsir.docpoint.model;

public class Message {
    private String body;
    private String from;

    public Message() {
    }

    public Message(String body, String from) {
        this.body = body;
        this.from = from;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public String getFrom() {
        return from;
    }
}
