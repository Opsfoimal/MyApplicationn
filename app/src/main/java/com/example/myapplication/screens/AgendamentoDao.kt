package com.example.myapplication.screens

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendamentoDao {
    @Insert
    suspend fun insert(agendamento: AgendamentoEntity): Long

    @Query("SELECT * FROM agendamentos ORDER BY id DESC")
    fun getAll(): Flow<List<AgendamentoEntity>>

    @Delete
    suspend fun delete(agendamento: AgendamentoEntity)
}
