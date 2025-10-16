package com.example.myapplication.screens

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agendamentos")
data class AgendamentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val data: String,
    val hora: String,
    val servico: String
)
