package com.rachitpanwar.mycredible1.PersonalDetailsClasses;

public class PersonalDetailsData
{
    private Data data;

    public PersonalDetailsData() {
    }

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+"]";
    }
}