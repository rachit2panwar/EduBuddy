package com.rachitpanwar.mycredible1.LoginClasses;

public class User
{
    private String password;

    private String email;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getPassword ()
    {
        return password;
    }

    public void setPassword (String password)
    {
        this.password = password;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [password = "+password+", email = "+email+"]";
    }
}
