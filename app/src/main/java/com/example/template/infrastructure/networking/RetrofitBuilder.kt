/*
 * Copyright (c) 2022 by Hukman Thayib Amri.
 */

package com.example.template.infrastructure.networking

import com.example.template.BuildConfig
import com.example.template.infrastructure.model.ErrorResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton [RetrofitBuilder] that user for consist of generated implementation API using [Retrofit.Builder]such as [templateAPI] and [errorConverter]
 * */
object RetrofitBuilder {

    private const val BASE_URL = BuildConfig.SERVER_URL

    private val clientBuilder = OkHttpClient.Builder().also {
        it.connectTimeout(15, TimeUnit.SECONDS)
        it.writeTimeout(15, TimeUnit.SECONDS)
        it.readTimeout(15, TimeUnit.SECONDS)
    }

    private val retrofitBuilder :Retrofit.Builder by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientBuilder.build())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
    }

    @JvmStatic
    val templateAPI : TemplateAPI by lazy {
        retrofitBuilder
            .build()
            .create(TemplateAPI::class.java)
    }

    @JvmStatic
    val errorConverter: Converter<ResponseBody, ErrorResponse> by lazy {
        retrofitBuilder.build().responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java, arrayOfNulls<Annotation>(0)
        )
    }

}