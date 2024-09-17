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
}