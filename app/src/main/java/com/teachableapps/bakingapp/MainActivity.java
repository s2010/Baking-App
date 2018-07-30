package com.teachableapps.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.teachableapps.bakingapp.models.Ingredient;
import com.teachableapps.bakingapp.models.Recipe;
import com.teachableapps.bakingapp.utilities.ApiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipeListAdapter.ListItemClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String RECIPE_DETAIL_KEY = "recipedetail";

    private List<Recipe> mRecipeList = new ArrayList<>();
    private RecyclerView mRecipeListRecyclerView;
    private RecipeListAdapter mRecipeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Clear recipeList
        mRecipeList.clear();

        // RecyclerView
        if(findViewById(R.id.rv_main_tablet)!=null) {
            mRecipeListRecyclerView = (RecyclerView) findViewById(R.id.rv_main_tablet);
            mRecipeListRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }else{
            mRecipeListRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
            mRecipeListRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        }
        mRecipeListRecyclerView.setHasFixedSize(false);
        mRecipeListAdapter = new RecipeListAdapter(mRecipeList, this, this);
        mRecipeListRecyclerView.setAdapter(mRecipeListAdapter);

        // Query Recipe API
        loadRecipes();
    }

    @Override
    public void OnListItemClick(Recipe recipe) {

        // Update Widget
        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.baking_widget_provider);
        ComponentName thisWidget = new ComponentName(context, BakingWidgetProvider.class);
        String widgetText = recipe.getName()+"\n";
        List<Ingredient> ingredientList = recipe.getIngredients();
        for (int i=0; i<ingredientList.size(); i++) {
            widgetText += ( " • " +
                    ingredientList.get(i).getQuantity() + " " +
                    ingredientList.get(i).getMeasure() + " " +
                    ingredientList.get(i).getIngredient() + "\n");
        }
        remoteViews.setTextViewText(R.id.appwidget_text, widgetText);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        // Start Recipe Detail Activity
        Intent recipeDetailIntent = new Intent(MainActivity.this, RecipeDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECIPE_DETAIL_KEY, recipe);
        recipeDetailIntent.putExtras(bundle);
        startActivity(recipeDetailIntent);

    }

    public void loadRecipes() {

        // Create a call object
        Call<ArrayList<Recipe>> call = ApiUtils.getRecipes();

        call.enqueue(new Callback<ArrayList<Recipe>>() {

            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {

                // Success
                mRecipeList = response.body();
                mRecipeListAdapter.setRecipeListData(mRecipeList);
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {

                // Failed
                Log.d(TAG,"Failed");
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    // Show this view when list is empty
    private void showEmptyView() {
        Toast.makeText(this, "EMPTY List", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"EMPTY List");
    }

}
