package com.example.uread.di

import android.content.Context
import androidx.room.Room
import com.example.uread.data.repository.BooksRepositoryImpl
import com.example.uread.data.source.local.AppDatabase
import com.example.uread.data.source.local.BookDao
import com.example.uread.data.source.local.AppPreferencesUtil
import com.example.uread.data.source.local.ReaderPreferencesUtil
import com.example.uread.domain.repository.BooksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.http.HttpClient
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class) //live as long as our application
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

//    @Provides
//    @Singleton
//    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
////        return Room.databaseBuilder(
////            appContext,
////            AppDatabase::class.java,
////            "book_database"
////        )
//////            .addMigrations(AppDatabase.MIGRATION_1_2) // Add your migration here
////            .build()
//
//        return Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()
//    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "book_database")
            .fallbackToDestructiveMigration()  //for debug useful
            .build()
    }

    @Provides
    @Singleton
    fun provideBookDao(appDatabase: AppDatabase): BookDao {
        return appDatabase.bookDao()
    }


    @Provides
    @Singleton
    fun provideReaderPreferences(@ApplicationContext context: Context): ReaderPreferencesUtil {
        return ReaderPreferencesUtil(context)
    }

    @Provides
    @Singleton
    fun provideAppPreferencesUtil(@ApplicationContext context: Context): AppPreferencesUtil {
        return AppPreferencesUtil(context)
    }

    @Provides
    @Singleton
    fun provideBooksRepository ( bookDao: BookDao): BooksRepository {
        return BooksRepositoryImpl(bookDao)
    }


    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return DefaultHttpClient()
    }

    @Provides
    @Singleton
    fun provideAssetRetriever(
        @ApplicationContext context: Context,
        httpClient: HttpClient
    ): AssetRetriever {
        return AssetRetriever(context.contentResolver, httpClient)
    }

    @Provides
    @Singleton
    fun providePublicationParser(
        @ApplicationContext context: Context,
        httpClient: HttpClient,
        assetRetriever: AssetRetriever
    ): DefaultPublicationParser {
        return DefaultPublicationParser(context, httpClient, assetRetriever, null)
    }

    @Provides
    @Singleton
    fun providePublicationOpener(publicationParser: DefaultPublicationParser): PublicationOpener {
        return PublicationOpener(publicationParser)
    }

}
//
//@Singleton
//@Provides
//fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
//    return Room.databaseBuilder(context, AppDatabase::class.java, "YourDatabaseName.db")
//        .addCallback(object : RoomDatabase.Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                // Check some condition here if needed
//                context.deleteDatabase("YourDatabaseName.db")
//            }
//        })
//        .build()
//}