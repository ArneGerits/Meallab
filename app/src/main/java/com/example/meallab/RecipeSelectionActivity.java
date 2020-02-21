package com.example.meallab;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.ImageLoader;
import com.example.meallab.Spoonacular.*;

import java.util.Arrays;

public class RecipeSelectionActivity extends AppCompatActivity implements SpoonacularBatchRecipeListener, RecipeInfoFragment.OnFragmentInteractionListener {

    // The recipes loaded.
    private Recipe[] recipes;
    // Used for api communication with Spoonacular
    private SpoonacularAPI api;

    // The current offset of the recipes.
    private int currentOffset = 0;

    // The meal type of this activity.
    SpoonacularMealType mealType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_selection);

        // Resolve the meal type.
        Bundle b      = getIntent().getExtras();
        this.mealType = SpoonacularMealType.valueOf(b.getString("mealType"));
        this.setTitle(this.mealType);

        // Setup the API communication
        api = new SpoonacularAPI(this);
        // Load the recipes.
        loadRecipes(0);
    }

    // Loads 9 breakfast recipes.
    // @param offset The offset to use in the retrieval of the recipes.
    protected void loadRecipes(int offset) {
        // Create a breakfast request.
        RecipeRequest request = new RecipeRequest(this.mealType);
        request.offset        = offset;
        // todo: Intellegently set min/max values.
        request.maxCals = 800;

        api.retrieveRecipes(request, this);
    }

    // Sets the correct title on the title label.
    protected  void setTitle(SpoonacularMealType type) {

        TextView title = (TextView) findViewById(R.id.titleTextView);
        if (this.mealType == SpoonacularMealType.BREAKFAST) {
            title.setText("Choose breakfast");
        } else if (this.mealType == SpoonacularMealType.LUNCH) {
            title.setText("Choose lunch");
        } else if (this.mealType == SpoonacularMealType.SNACK) {
            title.setText("Choose snack");
        } else {
            title.setText("Choose dinner");
        }
    }

    // Loads and sets images for given recipes.
    // NOTE: recipes.length == 3 should hold.
    protected void loadImages(Recipe[] recipes) {
        // Get the image loader from the Volley singleton.
        ImageLoader imageLoader = VolleySingleton.getInstance(this).getImageLoader();

        int[] ids = {R.id.topInfo,R.id.middleInfo,R.id.bottomInfo};

        for (int i = 0; i < ids.length; i++) {
            // Get the fragment.
            RecipeInfoFragment frag = (RecipeInfoFragment)getSupportFragmentManager().findFragmentById(ids[i]);
            // Get the url.
            String url = recipes[i].getImageURLForSize(SpoonacularImageSize.S_636x393);
            System.out.println("Connecting to url: " + url);
            // Load the image.
            frag.networkImageView().setImageUrl(url,imageLoader);
        }
    }
    //region SpoonacularBatchRecipeListener
    @Override
    public void retrievedRecipes(Recipe[] recipes) {

        this.recipes = recipes;

        System.out.println("Retrieved the recipes");
        // Load the 3 images.
        this.loadImages(Arrays.copyOfRange(recipes,0,3));
    }
    @Override
    public void batchRecipesErrorHandler() {
        // todo: Handle error here.
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println("interacted with fragment!");
        // Switch fragment to selected state....
        // But how do we know which fragment was selected?
    }
    //endregion
}
