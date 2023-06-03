package com.example.gobiteseller.di

import com.example.gobiteseller.MainActivity
import com.example.gobiteseller.data.retrofit.AuthInterceptor
import com.example.gobiteseller.utils.AppConstants
import com.example.gobiteseller.data.retrofit.ItemRepository
import com.example.gobiteseller.data.retrofit.OrderRepository
import com.example.gobiteseller.data.retrofit.ShopRepository
import com.example.gobiteseller.data.retrofit.UserRespository
import com.google.android.datatransport.BuildConfig
import com.google.gson.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { AuthInterceptor(get(),get()) }
    single { provideRetrofit(get()) }
    single { ItemRepository(get()) }
    single { ShopRepository(get()) }
    single { OrderRepository(get()) }
    single { UserRespository(get()) }
}

fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit {
    val gson = GsonBuilder().registerTypeAdapter(Date::class.java, DateTypeDeserializer()).create()
    return Retrofit.Builder()
        .baseUrl(AppConstants.CUSTOM_BASE_URL)
        .client(provideOkHttpClient(authInterceptor))
        .addConverterFactory(GsonConverterFactory.create(gson)).build()
}

fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    val builder = OkHttpClient()
        .newBuilder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addNetworkInterceptor(authInterceptor)


        val requestInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addNetworkInterceptor(requestInterceptor)

    return builder.build()
}

class DateTypeDeserializer : JsonDeserializer<Date> {
    private val DATE_FORMATS = arrayOf("dd/MM/yyyy HH:mm:ss", "HH:mm:ss")

    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
        for (format in DATE_FORMATS) {
            try {
                return SimpleDateFormat(format).parse(jsonElement.asString)
            } catch (e: Exception) {
            }
        }
        throw JsonParseException("Unparseable date: \"" + jsonElement.asString)
    }
}