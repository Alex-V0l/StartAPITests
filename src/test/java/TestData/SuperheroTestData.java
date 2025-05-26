package TestData;

import Models.GenderForSuperhero;
import Models.Superhero;
import Models.SuperheroError;

public class SuperheroTestData {
    public static final long INVALID_ID = -5;
    public static final long NON_USED_ID = 1;

    public static final Superhero HERO_WITH_ALL_FIELDS = Superhero.builder()
            .birthDate("1975-08-18")
            .city("New York")
            .fullName("Tony Stark")
            .gender(GenderForSuperhero.M)
            .id(33)
            .mainSkill("Intelligence")
            .phone("+69817952")
            .build();

    public static final Superhero BASIC_HERO = Superhero.builder()
            .birthDate("1890-01-21")
            .city("New York")
            .fullName("Steve Rogers")
            .gender(GenderForSuperhero.M)
            .mainSkill("Physical abilities")
            .phone("+78971234580")
            .build();

    public static final Superhero HERO_WITHOUT_PHONE = Superhero.builder()
            .birthDate("1920-08-12")
            .city("Gotham")
            .fullName("Bruce Wayne")
            .gender(GenderForSuperhero.M)
            .mainSkill("Money")
            .build();

    public static final Superhero HERO_WITH_INVALID_DATE = Superhero.builder()
            .birthDate("no information")
            .city("Boston")
            .fullName("Logan")
            .gender(GenderForSuperhero.M)
            .mainSkill("Regeneration")
            .phone("+6797198453")
            .build();

    public static final Superhero HERO_FOR_UPDATE = Superhero.builder()
            .birthDate("1756-05-11")
            .city("Boston")
            .fullName("James")
            .gender(GenderForSuperhero.M)
            .mainSkill("Claws")
            .phone("didn't exist")
            .build();

    public static final Superhero HERO_WITHOUT_SKILL = Superhero.builder()
            .birthDate("1990-03-21")
            .city("Bucharest")
            .fullName("Natasha")
            .gender(GenderForSuperhero.F)
            .id(303)
            .phone("+6719808254")
            .build();

    public static final SuperheroError RESPONSE_WITH_NO_AVAILABLE_MESSAGE = SuperheroError.builder()
            .message("No message available")
            .build();

}
