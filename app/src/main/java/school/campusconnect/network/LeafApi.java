package school.campusconnect.network;


import school.campusconnect.BuildConfig;

public class LeafApi {

    private final String baseHostUrl;

    public LeafApi() {
        this(BuildConfig.BASE_URL);
        //this("https://prod.gruppie.in");
    }

    public LeafApi(String baseHostUrl) {
        this.baseHostUrl = baseHostUrl;
    }

    public String getBaseHostUrl() {
        return this.baseHostUrl;
    }
}
