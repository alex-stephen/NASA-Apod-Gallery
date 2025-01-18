package com.example.apod_fetch.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {
    /**
     * Retrieves all APOD (Astronomy Picture of the Day) photos from the database,
     * sorted in descending order by date.
     *
     * @return A list of [ApodPhoto] objects.
     */
    @Query("SELECT * FROM apod ORDER BY date DESC")
    suspend fun getAllApods(): List<ApodPhoto>

    /**
     * Retrieves a single APOD photo from the database by its date.
     *
     * @param date The date of the APOD photo in "YYYY-MM-DD" format.
     * @return A [Flow] emitting the [ApodPhoto] object for the given date, or `null` if not found.
     */
    @Query("SELECT * FROM apod WHERE date = :date")
    fun getApodByDate(date: String): Flow<ApodPhoto?>

    /**
     * Retrieves all APOD photos within a specified date range.
     *
     * @param startDate The start date of the range in "YYYY-MM-DD" format.
     * @param endDate The end date of the range in "YYYY-MM-DD" format.
     * @return A [Flow] emitting a list of [ApodPhoto] objects within the specified range.
     */
    @Query("SELECT * FROM apod WHERE date BETWEEN :startDate AND :endDate")
    fun getApodByRange(startDate: String, endDate: String): Flow<List<ApodPhoto>>

    /**
     * Retrieves all APOD photos marked as favorite.
     *
     * @return A [Flow] emitting a list of favorite [ApodPhoto] objects.
     */
    @Query("SELECT * FROM apod WHERE favourite = 1")
    fun getFavouritePhotos(): Flow<List<ApodPhoto>>

    /**
     * Retrieves a random set of APOD photos using a custom raw SQL query.
     * This query can be customized using [SupportSQLiteQuery].
     *
     * @param query A raw SQL query to execute.
     * @return A [Flow] emitting a list of randomly selected [ApodPhoto] objects.
     */
    @RawQuery(observedEntities = [ApodPhoto::class])
    fun getRandomPhotos(query: SupportSQLiteQuery): Flow<List<ApodPhoto>>

    /**
     * Inserts a list of APOD photos into the database. If a photo with the same
     * primary key already exists, it will be replaced.
     *
     * @param listOfApods The list of [ApodPhoto] objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApods(listOfApods: List<ApodPhoto>)

    /**
     * Inserts a single APOD photo into the database. If a photo with the same
     * primary key already exists, it will be replaced.
     *
     * @param apod The [ApodPhoto] object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(apod: ApodPhoto)

    /**
     * Updates an existing APOD photo in the database.
     *
     * @param photo The [ApodPhoto] object with updated values to save.
     */
    @Update
    suspend fun update(photo: ApodPhoto)
}