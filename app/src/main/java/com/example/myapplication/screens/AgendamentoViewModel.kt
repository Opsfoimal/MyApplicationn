package com.example.myapplication.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AgendamentoViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).agendamentoDao()

    val agendamentos: StateFlow<List<AgendamentoEntity>> = dao.getAll()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun insert(data: String, hora: String, servico: String) = viewModelScope.launch {
        dao.insert(AgendamentoEntity(data = data, hora = hora, servico = servico))
    }

    fun delete(agendamento: AgendamentoEntity) = viewModelScope.launch {
        dao.delete(agendamento)
    }
}
