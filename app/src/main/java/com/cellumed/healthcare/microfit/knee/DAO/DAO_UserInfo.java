package com.cellumed.healthcare.microfit.knee.DAO;

import com.github.mikephil.charting.components.Legend;

/**
 * Created by ljh0928 on 2018. 3. 6..
 */

public class DAO_UserInfo {
    String Id;
    String Name;
    String Birth;
    String Gender;
    String LegPart;

    public DAO_UserInfo init(){
        Id = "";
        Name = "";
        Birth = "";
        Gender = "";
        LegPart = "";
        return this;
    }

    public String getId(){
        return Id;
    }

    public String getName(){
        return Name;
    }

    public String getBirth(){
        return Birth;
    }

    public String getGender(){
        return Gender;
    }

    public String getLegPart(){
        return LegPart;
    }

    public void setId(String id){
        this.Id = id;
    }

    public void setName(String name){
        this.Name = name;
    }

    public void setBirth(String birth){
        this.Birth = birth;
    }

    public void setGender(String gender){
        this.Gender = gender;
    }

    public void setLegPart(String legPart){
        this.LegPart = legPart;
    }

}
