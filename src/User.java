import lombok.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class User {
    private int id;
    private String name;
    private String gender;
    private String email;
    private String dob;
}