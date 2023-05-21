package com.example.leshemayapro.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {

    protected String id, uid, title, description, directions, rating, makingTime, numberOfPeople;
    protected ArrayList<String> ingredients, tags;

    public Recipe() {}

    public Recipe(String id, String uid, String title, String description, ArrayList<String> ingredients, ArrayList<String> tags, String directions, String makingTime, String numberOfPeople)
    {
        this.setTags(tags);
        this.setIngredients(ingredients);
        this.setDirections(directions);
//        this.setRating(rating);
        this.setMakingTime(makingTime);
        this.setNumberOfPeople(numberOfPeople);
        this.setId(id);
        this.setUid(uid);
        this.setTitle(title);
        this.setDescription(description);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getMakingTime() {
        return makingTime;
    }

    public void setMakingTime(String makingTime) {
        this.makingTime = makingTime;
    }

    public String getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(String numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", tags='" + tags + '\'' +
                ", ingredients='" + ingredients.toString() + '\'' +
                ", directions='" + directions + '\'' +
                ", rating='" + rating + '\'' +
                ", makingTime='" + makingTime + '\'' +
                ", numberOfPeople='" + numberOfPeople + '\'' +
                '}';
    }
}
