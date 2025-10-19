package com.yermaalexx.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
@AllArgsConstructor
public enum InterestCategory {
    MOVIES("Movies & TV Shows"),
    BOOKS("Books"),
    MUSIC("Music"),
    HOBBIES("Hobbies"),
    SCIENCE_TECH("Science & Technology"),
    SPORTS("Sports");

    private final String description;

    public static LinkedHashMap<InterestCategory, List<Interest>> getCategoryMap() {
        LinkedHashMap<InterestCategory, List<Interest>> map = new LinkedHashMap<>();
        for(InterestCategory category : InterestCategory.values())
            map.put(category, Interest.getByCategory(category));

        return map;

    }
}
