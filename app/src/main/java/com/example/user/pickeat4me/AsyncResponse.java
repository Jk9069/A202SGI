package com.example.user.pickeat4me;

import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 6/9/2017.
 */

public interface AsyncResponse {
    void getAllPlaces (List<HashMap<String,String>> place);
    void getAllPlacesDetails (HashMap<String,String> placeDetails);
}
