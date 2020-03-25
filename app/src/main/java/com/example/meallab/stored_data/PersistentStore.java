package com.example.meallab.stored_data;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;

/**
 * The persistent store is a facade class that regulates all archiving and retrieval of data.
 */
public class PersistentStore {

    PersistentStoreListener listener;

    StoredDay[] days;
    StoredRecipe[] recipes;
    // Days

    /**
     *  Loads every stored object from the file system into app memory.
     *  Calls the initialized method on the listener on completion.
     */
    public void initialize(PersistentStoreListener listener) {
        this.listener = listener;



        this.listener.completedInitialized(true);
    }

    /**
     * Synchronizes(stores) all objects to the file system.
     */
    public void synchronize() {


        this.listener.completedSynchronize(true);
    }

    /**
     * Retrieves all favorited recipes.
     * Calls retrievedFavorites on the listener on completion.
     * @return The users favorite recipes.
     */
    public StoredRecipe[] retrieveFavorites() {

        ArrayList<StoredRecipe> favos = new ArrayList<>();

        for (StoredRecipe r : this.recipes) {
            if (r.isFavorite) {
                favos.add(r);
            }
        }

        StoredRecipe[] arr = new StoredRecipe[favos.size()];
        arr = favos.toArray(arr);

        return arr;
    }

    /**
     * Retrieves StoredDay objects from given dates.
     * @param dates The dates of the StoredDay objects.
     * @return The days.
     */
    public StoredDay[] retrieveDays(LocalDate[] dates) {

        ArrayList<StoredDay> d = new ArrayList<>();

        for (StoredDay day : this.days) {
            for (LocalDate date : dates) {
                if (day.date.isEqual(date))
                    d.add(day);
            }
        }

        StoredDay[] arr = new StoredDay[d.size()];
        arr = d.toArray(arr);

        return arr;
    }
    /**
     * Stores a single day to the file system.
     * @param day The day to store.
     * @return True if the store was succesful, false otherwise.
     */
    public boolean newDay(StoredDay day) {

    }

    // Recipes
    /**
     * Stores a single recipe to the file system.
     * @param recipe The recipe to store.
     * @return True if the store was succesful, false otherwise.
     */
    public boolean newRecipe(StoredRecipe recipe) {

    }

    /**
     * Interface
     */
    public interface PersistentStoreListener {
        /**
         * Called when initialize completes.
         * @param success True if the synchronization was successful, False otherwise
         */
        void completedInitialized(boolean success);

        /**
         * Called when synchronize completes.
         * @param success True if the synchronization was successful, False otherwise.
         */
        void completedSynchronize(boolean success);
    }
}
