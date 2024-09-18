package DeviceManager;

public class Device {
    public boolean status;
    public String ServiceTag;
    public String user;
    public String startDate;
    public String endDate;

    public Device(boolean status, String ServiceTag, String user, String startDate, String endDate) {
        this.status = status;
        this.ServiceTag = ServiceTag;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Device() {

    }

    public boolean getStatus() {
        return status;
    }
    public String getStatusText() {
        return status ? "Verfügbar" : "Nicht verfügbar";
    }
    public String getServiceTag() {
        return ServiceTag;
    }

    public String getBenutzer() {
        return user;
    }

    public String getStartDatum() {
        return startDate;
    }

    public String getEndDatum() {
        return endDate;
    }
}