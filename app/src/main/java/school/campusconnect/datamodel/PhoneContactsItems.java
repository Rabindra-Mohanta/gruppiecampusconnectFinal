package school.campusconnect.datamodel;

public class PhoneContactsItems {
    String name, phone;
    public boolean isSelected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "PhoneContactsItems{" +
                "name='" + name + '\'' +
                ", adminPhone='" + phone + '\'' +
                '}';
    }
}
