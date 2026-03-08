package com.devrochaiago.recipeapp.di

import android.app.Application
import androidx.room.Room
import com.devrochaiago.recipeapp.data.local.MealDao
import com.devrochaiago.recipeapp.data.local.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRecipeDatabase(app: Application): RecipeDatabase {
        return Room.databaseBuilder(
            app,
            RecipeDatabase::class.java,
            RecipeDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideMealDao(db: RecipeDatabase): MealDao {
        return db.mealDao
    }
}