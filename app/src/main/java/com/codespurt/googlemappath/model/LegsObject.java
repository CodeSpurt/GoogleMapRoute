package com.codespurt.googlemappath.model;

import java.util.List;

/**
 * Created by CodeSpurt on 01-09-2017.
 */

public class LegsObject {

    private List<StepsObject> steps;

    public LegsObject(List<StepsObject> steps) {
        this.steps = steps;
    }

    public List<StepsObject> getSteps() {
        return steps;
    }
}
