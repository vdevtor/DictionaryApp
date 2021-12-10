package com.example.dictionaryapp.feature_dictionary.data

import com.example.dictionaryapp.core.util.Resource
import com.example.dictionaryapp.feature_dictionary.data.local.WordInfoDao
import com.example.dictionaryapp.feature_dictionary.data.remote.DictionaryApi
import com.example.dictionaryapp.feature_dictionary.domain.model.WordInfo
import com.example.dictionaryapp.feature_dictionary.domain.repository.WordInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WordInfoRepositoryImp(
    private val api: DictionaryApi,
    private val dao: WordInfoDao
) : WordInfoRepository {


    override fun getWordInfo(word: String): Flow<Resource<List<WordInfo>>> = flow {
        emit(Resource.Loading())
        val wordInfos = dao.getWordInfos(word).map { it.toWordInfo() }
        emit(Resource.Loading(wordInfos))

        try {
            val remoteWordInfos = api.getWordInfo(word)
            dao.deleteWordInfos(remoteWordInfos.map { it.word })
            dao.insertWordInfos(remoteWordInfos.map { it.toWordInfoEntity() })

        } catch (exception: HttpException) {
            emit(Resource.Error(message = "oops, something went wrong!", data = wordInfos))
        } catch (exception: IOException) {
            emit(
                Resource.Error(
                    message = "Couldn't reach the server, check your network connection",
                    data = wordInfos
                )
            )
        }
        val newWordInfos = dao.getWordInfos(word).map { it.toWordInfo() }
        emit(Resource.Success(newWordInfos))
    }
}