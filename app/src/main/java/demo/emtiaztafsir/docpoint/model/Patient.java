package demo.emtiaztafsir.docpoint.model;

public class Patient {
    private String uid;
    private String name;
    private String age;
    private String distance;
    private String appId;

    public Patient(String uid, String name, String age, String distance, String appId) {
        this.uid = uid;
        this.name = name;
        this.age = age;
        this.distance = distance;
        this.appId = appId;
    }

    public Patient(String uid, String name, String age, String appId) {
        this.uid = uid;
        this.name = name;
        this.age = age;
        this.appId = appId;
    }


    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getDistance() {
        return distance;
    }

    public String getAppId() {
        return appId;
    }
}
