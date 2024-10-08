package com.example.myapplication.di

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.room.Room
import com.example.myapplication.data.AppDataBase
import com.example.myapplication.data.BASE_URL
import com.example.myapplication.data.Camera
import com.example.myapplication.data.InfoRepository
import com.example.myapplication.data.LocationService
import com.example.myapplication.data.PlacesRepository
import com.example.myapplication.data.RetrofitAndApi.PlacesApi
import com.example.myapplication.domain.GetInfoUseCase
import com.example.myapplication.domain.GetLocationUseCase
import com.example.myapplication.domain.GetPlacesUseCase
import com.example.myapplication.domain.GetsSpeedUseCase
import com.example.myapplication.domain.ILocationService
import com.example.myapplication.domain.UpdateLocationUseCase
import com.example.myapplication.viewmodel.MapViewModel
import com.example.myapplication.viewmodel.MyViewModel
import com.example.myapplication.viewmodel.SearchViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RequiresApi(Build.VERSION_CODES.S)
val module = module {
    single<FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(
            androidContext()
        )
    }
    single<ILocationService> {
        LocationService(androidContext(), get())
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            "db"
        ).fallbackToDestructiveMigration().build()
    }
    single {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
                    it.level = HttpLoggingInterceptor.Level.BODY
                }).build()
            )
            .build()
            .create(PlacesApi::class.java)
    }

    factory { (lifecycleOwner: LifecycleOwner, previewView: PreviewView) ->
        Camera(
            lifecycleOwner = lifecycleOwner,
            context = androidContext(),
            viewModel = get(),
            contentResolver = androidContext().contentResolver,
            previewView = previewView
        )
    }

    factory { InfoRepository(get()) }
    factory { PlacesRepository(get()) }
    factory { GetPlacesUseCase(get()) }
    factory { GetInfoUseCase(get()) }
    factory { GetLocationUseCase(get()) }
    factory { GetsSpeedUseCase(get()) }
    factory { UpdateLocationUseCase(get()) }

    viewModelOf(::MyViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::SearchViewModel)
}