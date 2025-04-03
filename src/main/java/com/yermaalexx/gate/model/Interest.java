package com.yermaalexx.gate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Interest {

    SCI_FI_M("SciFiM", "Sci-Fi & Fantasy (science fiction, epic fantasy, cyberpunk)", InterestCategory.MOVIES),
    DRAMA("Drama", "Drama (melodrama, historical drama, biographical)", InterestCategory.MOVIES),
    COMEDY("Comedy", "Comedy (sitcoms, dark humor, parodies)", InterestCategory.MOVIES),
    HORROR_M("HorrorM", "Horror & Thrillers (psychological, slasher, supernatural)", InterestCategory.MOVIES),
    ACTION("Action", "Action & Adventure (spy, war, crime)", InterestCategory.MOVIES),
    ANIMATION("Anime", "Animation (anime, Western animation, children's cartoons)", InterestCategory.MOVIES),
    DOCUMENTARIES("Docs", "Documentaries (true crime, science, history)", InterestCategory.MOVIES),

    SCI_FI_B("SciFiB", "Fantasy & Sci-Fi (epic fantasy, post-apocalyptic, space opera)", InterestCategory.BOOKS),
    THRILLER("Thriller", "Mystery & Thriller (crime, psychological, spy)", InterestCategory.BOOKS),
    CLASSIC_B("Classic", "Classic Literature (world classics, poetry, modernism)", InterestCategory.BOOKS),
    NON_FICTION("NonFic", "Non-Fiction (self-improvement, science, history, business)", InterestCategory.BOOKS),
    ROMANCE("Romance", "Romance (love stories, erotica, drama)", InterestCategory.BOOKS),
    HORROR_B("HorrorB", "Horror & Supernatural (gothic horror, paranormal, thriller)", InterestCategory.BOOKS),
    MANGA("Manga", "Manga & Graphic Novels (seinen, shonen, superheroes)", InterestCategory.BOOKS),

    ROCK("Rock", "Rock (classic rock, metal, alternative)", InterestCategory.MUSIC),
    POP("Pop", "Pop (modern pop, retro pop, K-pop)", InterestCategory.MUSIC),
    HIPHOP("HipHop", "Hip-Hop & Rap (old school, trap, underground)", InterestCategory.MUSIC),
    ELECTRONIC("Electro", "Electronic (house, techno, dubstep)", InterestCategory.MUSIC),
    JAZZ("Jazz", "Jazz & Blues (traditional, fusion, swing)", InterestCategory.MUSIC),
    CLASSICAL_M("Classical", "Classical Music (baroque, romanticism, contemporary classical)", InterestCategory.MUSIC),
    FOLK("Folk", "Folk & Ethnic Music (Celtic, Balkan, Latin American)", InterestCategory.MUSIC),

    TEAM_SPORTS("TeamSp", "Team Sports (football/soccer, basketball)", InterestCategory.SPORTS),
    COMBAT_SPORTS("Combat", "Combat Sports (boxing, MMA, karate)", InterestCategory.SPORTS),
    FITNESS("Fitness", "Fitness & Bodybuilding (CrossFit, yoga, powerlifting)", InterestCategory.SPORTS),
    CYCLING_RUNNING("CycleRun", "Cycling & Running (road cycling, trail running, marathons)", InterestCategory.SPORTS),
    EXTREME_SPORTS("Extreme", "Extreme Sports (skateboarding, snowboarding, BASE jumping)", InterestCategory.SPORTS),
    ESPORTS("Esports", "Esports (MOBA, shooters, strategy games)", InterestCategory.SPORTS),

    VIDEO_GAMES("Games", "Video Games (RPG, shooters, strategy)", InterestCategory.HOBBIES),
    TRAVEL("Travel", "Travel (backpacking, ecotourism, urban travel)", InterestCategory.HOBBIES),
    PHOTOGRAPHY("Photo", "Photography & Videography (portrait, landscape, street)", InterestCategory.HOBBIES),
    COOKING("Cooking", "Cooking (baking, international cuisine, healthy eating)", InterestCategory.HOBBIES),
    BOARD_GAMES("BoardGm", "Board Games (D&D, card games, strategy)", InterestCategory.HOBBIES),
    HANDICRAFTS_ART("Art", "Handicrafts & Art (drawing, knitting, 3D modeling)", InterestCategory.HOBBIES),
    CARS_MOTORCYCLES("Cars", "Cars & Motorcycles (tuning, racing, vintage cars)", InterestCategory.HOBBIES),
    PETS("Pets", "Pets (cats, dogs, reptiles & exotic pets, aquariums, birds)", InterestCategory.HOBBIES),

    SPACE_ASTRONOMY("Space", "Space & Astronomy", InterestCategory.SCIENCE_TECH),
    PROGRAMMING_IT("IT", "Programming & IT", InterestCategory.SCIENCE_TECH),
    NEUROSCIENCE_PSYCHOLOGY("Neuro", "Neuroscience & Psychology", InterestCategory.SCIENCE_TECH),
    BIOLOGY_MEDICINE("BioMed", "Biology & Medicine", InterestCategory.SCIENCE_TECH),
    PHYSICS_MATHEMATICS("PhysMath", "Physics & Mathematics", InterestCategory.SCIENCE_TECH),
    ENGINEERING_ROBOTICS("EngRob", "Engineering & Robotics", InterestCategory.SCIENCE_TECH),
    ECOLOGY_SUSTAINABLE_DEVELOPMENT("EcoSust", "Ecology & Sustainable Development", InterestCategory.SCIENCE_TECH);

    private final String name;
    private final String description;
    private final InterestCategory category;

    public static List<Interest> getByCategory(InterestCategory category) {
        return Arrays.stream(values())
                .filter(i -> i.getCategory().equals(category))
                .toList();
    }
}
