package org.quicksc0p3r.simplecounter.db

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Entity(tableName = "counters")
data class Counter(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String = "",
    val value: Int = 0,
    val defaultValue: Int = 0,
    val labelId: Int? = null,
    val allowNegativeValues: Boolean = false
)

@Dao
interface CounterDao {
    @Insert
    suspend fun insert(counter: Counter)

    @Delete
    fun delete(counter: Counter)

    @Query("SELECT * FROM counters")
    fun getAllCounters(): LiveData<List<Counter>>

    @Query("SELECT * FROM counters WHERE id = :counterId")
    fun getCounterById(counterId: Int): Flow<Counter>

    @Query("UPDATE counters SET value = :updatedValue WHERE id = :counterId")
    fun updateCounter(counterId: Int, updatedValue: Int)

    @Update
    fun update(counter: Counter)
}

@Database(entities = [Counter::class], version = 2)
abstract class CounterDb: RoomDatabase() {
    abstract fun counterDao(): CounterDao

    companion object {
        @Volatile
        private var INSTANCE: CounterDb? = null

        @Synchronized
        fun getDb(context: Context): CounterDb {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context.applicationContext, CounterDb::class.java, "counter_db")
                    .fallbackToDestructiveMigration()
                    .build()
            return INSTANCE as CounterDb
        }
    }
}

class CounterRepo(private val counterDao: CounterDao) {
    val allCounters: LiveData<List<Counter>> = counterDao.getAllCounters()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun getCounterById(counterId: Int): Flow<Counter> {
        return counterDao.getCounterById(counterId)
    }

    fun insertCounter(newCounter: Counter) {
        coroutineScope.launch(Dispatchers.IO) {
            counterDao.insert(newCounter)
        }
    }

    fun deleteCounter(counterToDelete: Counter) {
        coroutineScope.launch(Dispatchers.IO) {
            counterDao.delete(counterToDelete)
        }
    }

    fun updateCounter(counterToUpdate: Counter, updatedValue: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            counterDao.updateCounter(counterToUpdate.id, updatedValue)
        }
    }

    fun updateCounter2(counterToUpdate: Counter) {
        coroutineScope.launch(Dispatchers.IO) {
            counterDao.update(counterToUpdate)
        }
    }
}


class CountersViewModel(application: Application): ViewModel(){
    val allCounters: LiveData<List<Counter>>
    private val repo: CounterRepo

    init {
        val counterDb = CounterDb.getDb(application)
        val counterDao = counterDb.counterDao()

        repo = CounterRepo(counterDao)

        allCounters = repo.allCounters
    }

    fun getCounterById(counterId: Int): Flow<Counter> {
        return repo.getCounterById(counterId)
    }

    fun insertCounter(counter: Counter) {
        repo.insertCounter(counter)
    }

    fun deleteCounter(counter: Counter) {
        repo.deleteCounter(counter)
    }

    fun updateCounter(counter: Counter, updatedValue: Int) {
        repo.updateCounter(counter, updatedValue)
    }

    fun updateCounter2(counter: Counter) {
        repo.updateCounter2(counter)
    }
}

class CountersViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return CountersViewModel(application) as T
    }
}
