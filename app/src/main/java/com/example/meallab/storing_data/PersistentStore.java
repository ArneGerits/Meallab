package com.example.meallab.storing_data;

import android.content.Context;
import android.os.AsyncTask;

import org.threeten.bp.LocalDate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;

/**
 * The persistent store is a facade class that regulates all archiving and retrieval of data.
 */
public class PersistentStore {

    private final String C_FILE_NAME = "stored_data.json";

    // The listener
    private final PersistentStoreListener listener;

    // The current context.
    private Context context;

    // The days.
    private ArrayList<StoredDay> days = new ArrayList<>();

    // region Constructor
    public PersistentStore(final PersistentStoreListener listener, final Context context) {
        this.listener = listener;
        this.context  = context;

        // Execute the read async.
        AsyncRead read = new AsyncRead(C_FILE_NAME, this.days, listener);
        read.execute(context);
    }
    // endregion

    /**
     * Synchronizes(stores) all objects to the file system.
     */
    public void synchronize() {
        StoredDay[] arr = new StoredDay[days.size()];
        arr = days.toArray(arr);

        // Execute the write async.
        AsyncWrite write = new AsyncWrite(C_FILE_NAME, arr, listener);
        write.execute(this.context);
    }

    /**
     * Retrieves all favorited recipes.
     * Calls retrievedFavorites on the listener on completion.
     * @return The users favorite recipes.
     */
    public StoredRecipe[] retrieveFavorites() {

        ArrayList<StoredRecipe> favos = new ArrayList<>();

        for (StoredDay d : this.days) {
            for (StoredRecipe r : d.recipes) {
                if (r.isFavorite) {
                    favos.add(r);
                }
            }
        }
        StoredRecipe[] arr = new StoredRecipe[favos.size()];
        arr = favos.toArray(arr);

        return arr;
    }

    /**
     * Retrieves StoredDay objects from given dates.
     * If a storedDay object did not yet exist it is created.
     * @param dates The dates of the StoredDay objects.
     * @return The days.
     */
    public StoredDay[] retrieveDays(LocalDate[] dates) {

        ArrayList<StoredDay> d = new ArrayList<>();

        for (LocalDate date : dates) {
            boolean added = false;
            for (StoredDay day: this.days) {
                if (day.date.isEqual(date)) {
                    d.add(day);
                    added = true;
                }
            }
            // There was no stored day found for this date, create a new one.
            if (!added) {
                StoredDay newDay = new StoredDay(date);
                d.add(newDay);
                this.days.add(newDay);
            }
        }

        StoredDay[] arr = new StoredDay[d.size()];
        arr = d.toArray(arr);

        return arr;
    }
    /**
     * Retrieves a StoredDay object from given date.
     * If a storedDay object did not yet exist it is created.
     * @param date The date of the StoredDay object.
     * @return The day.
     */
    public StoredDay retrieveDay(LocalDate date) {

        return retrieveDays(new LocalDate[]{date})[0];
    }

    /**
     * Retrieves all stored days.
     * @return Dates for which there exists a stored day.
     */
    public LocalDate[] retrieveAllDays() {

        LocalDate[] arr = new LocalDate[this.days.size()];
        arr = this.days.toArray(arr);

        return arr;
    }

    // region Interface
    /**
     * Interface
     */
    public interface PersistentStoreListener {
        /**
         * Called when initialize completes.
         * @param success True if the synchronization was successful, False otherwise
         */
        void initializedSuccessfully(boolean success);

        /**
         * Called when synchronize completes.
         * @param success True if the synchronization was successful, False otherwise.
         */
        void completedSynchronize(boolean success);
    }
    // endregion

    // region Async

    /**
     * Background task for reading file.
     */
    private static class AsyncRead extends AsyncTask<Context, Void, Boolean> {

        String fileName;
        ArrayList<StoredDay> days;
        PersistentStoreListener l;

        // Used for (de)serialization
        Gson gson = new Gson();

        public AsyncRead(String fileName, ArrayList<StoredDay> days, PersistentStoreListener l) {
            this.fileName = fileName;
            this.days = days;
            this.l = l;
        }
        @Override
        protected Boolean doInBackground(Context... ctx) {

            Context c = ctx[0];
            if (c != null) {
                try {
                    // Read the data file from the file system, async.
                    String jsonString = readFromFile(fileName, c);

                    StoredDay[] d;
                    // If the jsonString is empty we create a new array.
                    if (jsonString.isEmpty()) {
                        // Create an empty array.
                        d = new StoredDay[0];
                    } else {
                        // Create an array from the stored json.
                        d = gson.fromJson(jsonString, StoredDay[].class);
                    }

                    // Set the days.
                    if (this.days != null) {
                        this.days.addAll(new ArrayList<>(Arrays.asList(d)));
                    }
                } catch (IOException e) {
                    return false;
                }
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBool) {
            super.onPostExecute(aBool);

            if (this.l != null) {
                this.l.initializedSuccessfully(aBool);
            }
        }

        // Reads string (json) data from file,
        private String readFromFile(String fileName, Context context) throws IOException {

            String json = "";

            try {
                InputStream inputStream = context.openFileInput(fileName);

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append("\n").append(receiveString);
                    }

                    inputStream.close();
                    json = stringBuilder.toString();
                }
            }
            // File does not exist yet.
            catch (FileNotFoundException e) {
                // Do nothing
            }
            //TODO:UNDO
            return json;
        }
    }
    /**
     * Background task for writing file.
     */
    private static class AsyncWrite extends AsyncTask<Context, Void, Boolean> {

        String fileName;
        PersistentStoreListener l;
        StoredDay[] days;

        // Used for (de)serialization
        Gson gson = new Gson();

        public AsyncWrite(String fileName, StoredDay[] days, PersistentStoreListener l) {
            this.fileName = fileName;
            this.l = l;
            this.days = days;
        }
        @Override
        protected Boolean doInBackground(Context... ctx) {

            Context c = ctx[0];
            if (c != null) {
                try {
                    String json = gson.toJson(this.days);
                    this.writeToFile(this.fileName, json, c);

                } catch (IOException e) {
                    System.out.println("THERE HAS BEEN AN IO EXCEPTION TYRING TO WRITE.");
                    return false;
                }
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBool) {
            super.onPostExecute(aBool);

            if (this.l != null) {
                this.l.completedSynchronize(aBool);
            }
        }
        // Writes json to the file system.
        private void writeToFile(String fileName, String json, Context context) throws IOException {

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        }
    }
}
