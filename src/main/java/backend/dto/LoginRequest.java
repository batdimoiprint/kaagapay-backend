package backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequest {
    @Schema(description = "Username or Email address", example = "johndoe")
    private String usernameOrEmail;

    @Schema(description = "User password", example = "password123")
    private String password;

    public LoginRequest() {}

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
