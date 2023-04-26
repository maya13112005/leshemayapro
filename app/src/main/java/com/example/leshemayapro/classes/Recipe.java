package com.example.leshemayapro.classes;

public class Recipe {

    private String id, uid, title, description, tags, ingredients, directions, rating, makingTime, numberOfPeople;

    public Recipe()
    {

    }

    public Recipe(String id, String uid, String title, String description, String ingredients, String directions, String makingTime, String numberOfPeople)
    {
//        this.setTags(tags);
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


    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
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
}
