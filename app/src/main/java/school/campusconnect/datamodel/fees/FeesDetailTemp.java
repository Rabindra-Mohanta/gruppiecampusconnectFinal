package school.campusconnect.datamodel.fees;

import com.google.gson.Gson;

public class FeesDetailTemp {
    String type;
    String amount;

    public FeesDetailTemp(String type, String amount) {
        this.type = type;
        this.amount = amount;
    }

    public FeesDetailTemp() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
