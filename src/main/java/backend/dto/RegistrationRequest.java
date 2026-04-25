package backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class RegistrationRequest {
    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Contact number", example = "09123456789")
    private String contactNumber;

    @Schema(description = "User's age", example = "25")
    private Integer age;

    @Schema(description = "User's gender", example = "Male")
    private String gender;

    @Schema(description = "User's role (defaults to resident)", example = "resident", defaultValue = "resident")
    private String role;

    @Schema(description = "Email address", example = "johndoe@example.com")
    private String email;

    @Schema(description = "Subdivision", example = "Greenwoods")
    private String subdivision;

    @Schema(description = "Street name", example = "Mabini")
    private String streetName;

    @Schema(description = "Street number", example = "123")
    private String streetNo;

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(String subdivision) {
        this.subdivision = subdivision;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
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
