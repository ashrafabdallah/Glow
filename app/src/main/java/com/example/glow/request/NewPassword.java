package com.example.glow.request;

public class NewPassword {
    private String email,password,password_confirmation;
  private   int id,pin_code;
    public NewPassword(String email, int pin_code, String password, String password_confirmation, int id) {
        this.email = email;
        this.pin_code = pin_code;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPin_code() {
        return pin_code;
    }

    public void setPin_code(int pin_code) {
        this.pin_code = pin_code;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }





    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }

    public void setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
    }




}
