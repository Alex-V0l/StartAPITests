package Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Superhero {
    private String birthDate;
    private String city;
    private String fullName;
    private GenderForSuperhero gender;
    private long id;
    private String mainSkill;
    private String phone;
}
