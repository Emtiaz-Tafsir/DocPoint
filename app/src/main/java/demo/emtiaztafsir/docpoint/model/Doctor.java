package demo.emtiaztafsir.docpoint.model;

public class Doctor {

    private String uid;
    private String name;
    private String qualification;
    private String distance;
    private String appId;
    private String appStatus;

    public Doctor(String uid, String name, String qualification, String distance) {
        this.uid = uid;
        this.name = name;
        this.qualification = qualification;
        this.distance = distance;
    }

    public Doctor(String uid, String name, String qualification, String appId, String appStatus) {
        this.uid = uid;
        this.name = name;
        this.qualification = qualification;
        this.appId = appId;
        this.appStatus = appStatus;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getQualification() {
        return qualification;
    }

    public String getDistance() {
        return distance;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppStatus() {
        return appStatus;
    }
}
