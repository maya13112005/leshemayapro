package com.example.leshemayapro.classes;

public class Guest
{
    protected String name;

    public Guest () {}

    public Guest (String name)
    {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
