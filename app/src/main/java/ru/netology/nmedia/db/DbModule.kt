package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.dao.PostDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class) //зависимость на уровне всего приложения
@Module
class DbModule {

    @Singleton   //в рамках всего приложения в одном единственном экземпляре, БД создается единственный раз
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providepostDao(
        appDb: AppDb
    ):PostDao = appDb.postDao()
}
