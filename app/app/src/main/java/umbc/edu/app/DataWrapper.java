package umbc.edu.app;

import java.io.Serializable;
import java.util.ArrayList;

import umbc.edu.services.GuideBoxService;

/**
 * Created by Bharath on 5/5/2017.
 */

public class DataWrapper implements Serializable {

    private ArrayList<GuideBoxService.Result> results;

    public DataWrapper(ArrayList<GuideBoxService.Result> data) {
        this.results = data;
    }

    public ArrayList<GuideBoxService.Result> getParliaments() {
        return this.results;
    }

}

