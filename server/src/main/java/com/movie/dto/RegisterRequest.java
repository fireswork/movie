package com.movie.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String gender;
    private Integer age;
    private String profession;
    private String phone;
    
    public RegisterRequest() {
    }
    
    public RegisterRequest(String username, String password, String gender, Integer age, String profession, String phone) {
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.profession = profession;
        this.phone = phone;
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
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getProfession() {
        return profession;
    }
    
    public void setProfession(String profession) {
        this.profession = profession;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
} 