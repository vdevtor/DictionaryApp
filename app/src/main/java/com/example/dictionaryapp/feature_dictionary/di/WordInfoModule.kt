package com.example.dictionaryapp.feature_dictionary.di

import android.app.Application
import androidx.room.Room
import com.example.dictionaryapp.feature_dictionary.data.WordInfoRepositoryImp
import com.example.dictionaryapp.feature_dictionary.data.local.WordInfoDataBase
import com.example.dictionaryapp.feature_dictionary.data.remote.DictionaryApi
import com.example.dictionaryapp.feature_dictionary.data.remote.DictionaryApi.Companion.BASE_URL
import com.example.dictionaryapp.feature_dictionary.data.util.GsonParser
import com.example.dictionaryapp.feature_dictionary.domain.repository.WordInfoRepository
import com.example.dictionaryapp.feature_dictionary.domain.use_case.GetWordInfo
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WordInfoModule {

    @Provides
    @Singleton
    fun provideGetWordInfoUseCase(repository: WordInfoRepository) : GetWordInfo{
        return GetWordInfo(repository)
    }

    @Provides
    @Singleton
    fun provideWordInfoRepository(db:WordInfoDataBase,api:DictionaryApi): WordInfoRepository{
        return WordInfoRepositoryImp(api, db.dao)
    }

    @Provides
    @Singleton
    fun provideWordInfoDataBase(app: Application): WordInfoDataBase{
        return Room.databaseBuilder(
            app,WordInfoDataBase::class.java,"word_db"
        )
            .addTypeConverter(GsonParser(Gson()))
            .build()
    }

    @Provides
    @Singleton
    fun providesDictionaryApi(): DictionaryApi{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApi::class.java)
    }
}