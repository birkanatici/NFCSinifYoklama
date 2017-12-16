package birkan.nfcsinifyoklama.Objects;

/**
 * Created by birkan on 1.04.2017.
 */

public class Student {

    private String name;
    private String surname;
    private String student_no;
    private String rfid;

    public Student(String _name, String _surname, String _no, String _rfid){
        this.name = _name;
        this.surname = _surname;
        this.student_no = _no;
        this.rfid = _rfid;
    }

    public Student() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStudent_no() {
        return student_no;
    }

    public void setStudent_no(String student_no) {
        this.student_no = student_no;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }
}
