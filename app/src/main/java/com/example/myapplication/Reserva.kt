
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.AppViewModel
import com.example.myapplication.screens.AgendamentoViewModel

@Composable
fun ReservaScreen(vm: AppViewModel, onVoltar: () -> Unit) {
    val context = LocalContext.current
    val user = vm.usuarioLogado.value?.user ?: ""
    var msg by remember { mutableStateOf<String?>(null) }


    val agendamentos by vm.agendamentos

    Scaffold { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Bem-vindo, $user", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))


                Text("Horários disponíveis:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))


                val disponiveis = vm.horariosDisponiveis()
                if (disponiveis.isEmpty()) {
                    Text("Nenhum horário disponível.")
                } else {
                    disponiveis.forEach { horario ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(horario)
                            Button(onClick = {

                                if (vm.reservarHorario(horario)) {
                                    msg = "Horário $horario reservado com sucesso!"
                                } else {
                                    msg = "Não foi possível reservar o horário."
                                }
                            }) {
                                Text("Reservar")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))


                Text("Meu Histórico de Reservas:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                val reservasCliente = vm.historicoDoUsuario(user)
                if (reservasCliente.isEmpty()) {
                    Text("Você ainda não fez reservas.")
                } else {
                    LazyColumn {
                        items(reservasCliente) { horario ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Horário reservado: $horario")
                                    Button(onClick = {
                                        // Cancelar reserva e liberar o horário novamente
                                        if (vm.cancelarReserva(horario)) {
                                            msg = "Reserva $horario cancelada com sucesso."
                                        }
                                    }) {
                                        Text("Cancelar")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(onClick = onVoltar, modifier = Modifier.fillMaxWidth()) {
                    Text("Sair")
                }
            }
        }
    }


    if (msg != null) {
        AlertDialog(
            onDismissRequest = { msg = null },
            confirmButton = {
                TextButton(onClick = { msg = null }) {
                    Text("OK")
                }
            },
            title = { Text("Resultado") },
            text = { Text(msg!!) }
        )
    }
}





@Composable
fun TelaAgendamentoRoomHook(viewModel: AgendamentoViewModel = viewModel()) {
    val agendamentos by viewModel.agendamentos.collectAsState()

    var dataText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var horaText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var servicoText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    Column(modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = dataText, onValueChange = { dataText = it }, label = { Text("Data") })
        OutlinedTextField(value = horaText, onValueChange = { horaText = it }, label = { Text("Hora") })
        OutlinedTextField(value = servicoText, onValueChange = { servicoText = it }, label = { Text("Serviço") })
        Button(onClick = { 
            if (dataText.isNotBlank() && horaText.isNotBlank() && servicoText.isNotBlank()) {
                viewModel.insert(dataText, horaText, servicoText)
                dataText = ""; horaText = ""; servicoText = ""
            }
        }) { Text("Salvar Agendamento") }

        Text("\nAgendamentos salvos:")
        LazyColumn { items(agendamentos) { ag ->
            androidx.compose.material3.Card(modifier = androidx.compose.ui.Modifier
                .fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = androidx.compose.ui.Modifier.padding(8.dp)) {
                    Text(text = "#${ag.id} - ${ag.servico}" )
                    Text(text = "Data: ${ag.data}  Hora: ${ag.hora}" )
                    Button(onClick = { viewModel.delete(ag) }) { Text("Excluir") }
                }
            }
        } }
    }
}
