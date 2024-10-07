package DeviceManagerAsSQL;

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

    // Setter-Methoden
    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setServiceTag(String ServiceTag) {
        this.ServiceTag = ServiceTag;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    // Optional: toString-Methode für einfache Ausgabe
    @Override
    public String toString() {
        return "Device{" +
                "status=" + status +
                ", ServiceTag='" + ServiceTag + '\'' +
                ", user='" + user + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}