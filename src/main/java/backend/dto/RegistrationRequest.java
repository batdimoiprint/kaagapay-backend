package backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class RegistrationRequest {
    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Contact number", example = "09123456789")
    private String contactNumber;

    @Schema(description = "Home address", example = "123 Street, City")
    private String address;

    @Schema(description = "Desired username", example = "johndoe")
    private String username;

    @Schema(description = "User password", example = "password123")
    private String password;

    public RegistrationRequest() {}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
