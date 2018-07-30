package com.teachableapps.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.teachableapps.bakingapp.models.Recipe;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailsFragment.OnStepClickListener,StepDetailsFragment.OnNavClickListener {
    private static final String TAG = RecipeDetailActivity.class.getSimpleName();
    public static final String DETAIL_RECIPE_KEY = "stepdetailkey";
    public static final String STEP_ID_KEY = "stepselectedkey";

    private boolean mTwoPane = true;
    private Recipe mRecipe;
    private int mStepSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipedetail);

        if(savedInstanceState!=null) {
            mRecipe = savedInstanceState.getParcelable(MainActivity.RECIPE_DETAIL_KEY);
            mStepSelected = savedInstanceState.getInt(STEP_ID_KEY);
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mRecipe = (Recipe) extras.getParcelable(MainActivity.RECIPE_DETAIL_KEY);
            }
            mStepSelected = 0;
        }
        if (mRecipe != null) {

            setTitle(mRecipe.getName());

            if(findViewById(R.id.div_twopanes)!=null) {
                mTwoPane = true;
                Log.d(TAG,"[[[ TWO PANES ]]]");

                RecipeDetailsFragment recipeFragment = new RecipeDetailsFragment(mRecipe);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.recipedetails_container,recipeFragment).commit();

                StepDetailsFragment stepFragment = new StepDetailsFragment(mRecipe.getSteps().get(mStepSelected));
                fragmentManager.beginTransaction().add(R.id.stepdetails_container,stepFragment).commit();

            } else {
                mTwoPane = false;
                Log.d(TAG,"--- Single Pane ---");
                RecipeDetailsFragment recipeFragment = new RecipeDetailsFragment(mRecipe);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.recipedetails_container,recipeFragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.RECIPE_DETAIL_KEY,mRecipe);
        outState.putInt(STEP_ID_KEY,mStepSelected);
    }

    // Define the behavior for onStepSelected
    @Override
    public void onStepSelected(int stepId) {
        mStepSelected = stepId;
        if (mTwoPane) {
            // If two panes, replace right fragment in the same activity
            FragmentManager fragmentManager = getSupportFragmentManager();
            StepDetailsFragment stepFragment = new StepDetailsFragment(mRecipe.getSteps().get(stepId));
            fragmentManager.beginTransaction().replace(R.id.stepdetails_container, stepFragment).commit();
        } else {
            // If single pane, open a new activity for step details
            Intent stepDetailIntent = new Intent(this, StepDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(DETAIL_RECIPE_KEY, mRecipe);
            bundle.putInt(STEP_ID_KEY,stepId);
            stepDetailIntent.putExtras(bundle);
            startActivity(stepDetailIntent);
        }
    }

    @Override
    public void onNavigate(int position) {
        // Navigation buttons are not active in tablet mode.
    }
}
