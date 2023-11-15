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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Entity(tableName = "labels")
data class Label(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String = "",
    val color: Long = 0xFFE15241
)

@Dao
interface LabelDao {
    @Insert
    suspend fun insert(label: Label)

    @Insert
    suspend fun insertWithIdReturn(label: Label): Long

    @Delete
    fun delete(label: Label)

    @Query("SELECT * FROM labels")
    fun getAllLabels(): LiveData<List<Label>>

    @Query("SELECT * FROM labels WHERE id = :labelId")
    fun getLabelById(labelId: Int): Flow<Label>

    @Query("SELECT id FROM labels WHERE rowid = :rowId")
    fun getIdFromRowId(rowId: Long): Int
}

@Database(entities = [Label::class], version = 1)
abstract class LabelDb: RoomDatabase() {
    abstract fun labelDao(): LabelDao

    companion object {
        @Volatile
        private var INSTANCE: LabelDb? = null

        @Synchronized
        fun getDb(context: Context): LabelDb {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context.applicationContext, LabelDb::class.java, "label_db").build()
            return INSTANCE as LabelDb
        }
    }
}

class LabelRepo(private val labelDao: LabelDao) {
    val allLabels: LiveData<List<Label>> = labelDao.getAllLabels()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertLabel(newLabel: Label) {
        coroutineScope.launch(Dispatchers.IO) {
            labelDao.insert(newLabel)
        }
    }

    fun insertLabelWithIdReturn(newLabel: Label): Int {
        var id = 0
        coroutineScope.launch(Dispatchers.IO) {
            id = labelDao.getIdFromRowId(labelDao.insertWithIdReturn(newLabel))
        }
        return id
    }

    fun deleteLabel(labelToDelete: Label) {
        coroutineScope.launch(Dispatchers.IO) {
            labelDao.delete(labelToDelete)
        }
    }

    fun getLabelById(labelId: Int): Flow<Label> {
        return labelDao.getLabelById(labelId)
    }
}

class LabelsViewModel(application: Application): ViewModel() {
    val allLabels: LiveData<List<Label>>
    private val repo: LabelRepo

    init {
        val labelDb = LabelDb.getDb(application)
        val labelDao = labelDb.labelDao()

        repo = LabelRepo(labelDao)

        allLabels = repo.allLabels
    }

    fun insertLabel(label: Label) {
        repo.insertLabel(label)
    }

    fun insertLabelWithIdReturn(label: Label): Int {
        return repo.insertLabelWithIdReturn(label)
    }

    fun deleteLabel(label: Label) {
        repo.deleteLabel(label)
    }

    fun getLabelById(labelId: Int): Flow<Label> {
        return repo.getLabelById(labelId)
    }
}

class LabelsViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return LabelsViewModel(application) as T
    }
}