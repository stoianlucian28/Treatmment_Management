package lucian;

public class TreatmentDetalis {

    private int ID;
    private String date;
    private String bodyPart;

    public TreatmentDetalis(int ID, String date, String bodyPart) {
        this.ID = ID;
        this.date = date;
        this.bodyPart = bodyPart;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }
}

