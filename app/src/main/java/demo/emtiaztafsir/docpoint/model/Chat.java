package demo.emtiaztafsir.docpoint.model;

public class Chat {
    private String pID;
    private String pName;
    private String chatID;

    public Chat(String pID, String pName, String chatID) {
        this.pID = pID;
        this.pName = pName;
        this.chatID = chatID;
    }

    public Chat(String pID, String chatID) {
        this.pID = pID;
        this.chatID = chatID;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpID() {
        return pID;
    }

    public String getpName() {
        return pName;
    }

    public String getChatID() {
        return chatID;
    }
}
