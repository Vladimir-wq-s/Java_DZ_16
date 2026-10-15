package vladimir.java_dz_16;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    private String username;
    private String password;
    private String role;
    private boolean blocked;
}
