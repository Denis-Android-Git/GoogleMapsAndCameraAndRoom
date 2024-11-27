package com.example.myapplication.di

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.room.Room
import com.example.myapplication.data.Camera
import com.example.myapplication.data.ConnectServiceImpl
import com.example.myapplication.data.LocationServiceImpl
import com.example.myapplication.data.api.BASE_URL
import com.example.myapplication.data.api.RetrofitAndApi.PlacesApi
import com.example.myapplication.data.db.AppDataBase
import com.example.myapplication.data.repository.InfoRepository
import com.example.myapplication.data.repository.PlacesRepository
import com.example.myapplication.data.repository.SearchRepository
import com.example.myapplication.domain.ConnectService
import com.example.myapplication.domain.ILocationService
import com.example.myapplication.domain.usecase.GetInfoUseCase
import com.example.myapplication.domain.usecase.GetLocationUseCase
import com.example.myapplication.domain.usecase.GetPlacesUseCase
import com.example.myapplication.domain.usecase.GetsSpeedUseCase
import com.example.myapplication.domain.usecase.SearchUseCase
import com.example.myapplication.domain.usecase.UpdateLocationUseCase
import com.example.myapplication.viewmodel.DbViewModel
import com.example.myapplication.viewmodel.DetailScreenViewModel
import com.example.myapplication.viewmodel.IntentViewModel
import com.example.myapplication.viewmodel.MapViewModel
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
        LocationServiceImpl(androidContext(), get())
    }

    single<ConnectService> {
        ConnectServiceImpl(androidContext())
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
    factory { SearchRepository(get()) }
    factory { SearchUseCase(get()) }

    viewModelOf(::DbViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::IntentViewModel)
    viewModelOf(::DetailScreenViewModel)

}