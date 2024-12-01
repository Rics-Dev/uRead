package com.ricdev.uread.di

import android.content.Context
import androidx.room.Room
import com.ricdev.uread.data.repository.BooksRepositoryImpl
import com.ricdev.uread.data.repository.ShelfRepositoryImpl
import com.ricdev.uread.data.source.local.AppDatabase
import com.ricdev.uread.data.source.local.dao.BookDao
import com.ricdev.uread.data.source.local.AppPreferencesUtil
import com.ricdev.uread.data.source.local.ReaderPreferencesUtil
import com.ricdev.uread.data.source.local.dao.AnnotationDao
import com.ricdev.uread.data.source.local.dao.BookShelfDao
import com.ricdev.uread.data.source.local.dao.BookmarkDao
import com.ricdev.uread.data.source.local.dao.NoteDao
import com.ricdev.uread.data.source.local.dao.ReadingActivityDao
import com.ricdev.uread.data.source.local.dao.ShelfDao
import com.ricdev.uread.domain.repository.BooksRepository
import com.ricdev.uread.domain.repository.ShelfRepository
import com.ricdev.uread.util.LanguageHelper
import com.ricdev.uread.util.PdfBitmapConverter
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

    private const val DATABASE_NAME = "uread_database"

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
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun provideBookDao(appDatabase: AppDatabase): BookDao {
        return appDatabase.bookDao()
    }

    @Provides
    @Singleton
    fun provideAnnotationDao(appDatabase: AppDatabase): AnnotationDao {
        return appDatabase.annotationDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(appDatabase: AppDatabase): NoteDao {
        return appDatabase.noteDao()
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(appDatabase: AppDatabase): BookmarkDao {
        return appDatabase.bookmarkDao()
    }

    @Provides
    @Singleton
    fun provideShelfDao(appDatabase: AppDatabase): ShelfDao {
        return appDatabase.shelfDao()
    }
    @Provides
    @Singleton
    fun provideBookShelfDao(appDatabase: AppDatabase): BookShelfDao {
        return appDatabase.bookShelfDao()
    }

    @Provides
    @Singleton
    fun provideReadingActivityDao(appDatabase: AppDatabase): ReadingActivityDao {
        return appDatabase.readingActivityDao()
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
    fun provideBooksRepository ( bookDao: BookDao, annotationDao: AnnotationDao, noteDao: NoteDao, bookmarkDao: BookmarkDao ,readingActivityDao: ReadingActivityDao ): BooksRepository {
        return BooksRepositoryImpl(bookDao, annotationDao, noteDao, bookmarkDao , readingActivityDao  )
    }

    @Provides
    @Singleton
    fun provideShelfRepository (shelfDao: ShelfDao, bookShelfDao: BookShelfDao,  bookDao: BookDao): ShelfRepository {
        return ShelfRepositoryImpl(shelfDao, bookShelfDao, bookDao)
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



    @Provides
    @Singleton
    fun providePdfBitmapConverter(@ApplicationContext context: Context): PdfBitmapConverter {
        return PdfBitmapConverter(context)
    }




    @Provides
    @Singleton
    fun provideLanguageHelper(): LanguageHelper = LanguageHelper()








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