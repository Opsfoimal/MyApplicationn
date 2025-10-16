package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import android.widget.Toast


data class Usuario(
    val user: String,
    val senha: String,
    val nomeCompleto: String,
    val rg: String,
    val nascimento: String
)


class AppViewModel : ViewModel() {
    private val adminUser = "admin"
    private val adminPass = "1234"

    var usuarios = mutableStateOf(mutableListOf<Usuario>())
    var usuarioLogado = mutableStateOf<Usuario?>(null)
    var agendamentos = mutableStateOf(mutableListOf<Pair<String, String>>())

    fun login(user: String, senha: String): Boolean {
        return if (user == adminUser && senha == adminPass) {
            usuarioLogado.value = Usuario(adminUser, adminPass, "Administrador", "", "")
            true
        } else {
            val cliente = usuarios.value.find { it.user == user && it.senha == senha }
            if (cliente != null) {
                usuarioLogado.value = cliente
                true
            } else false
        }
    }

    fun cadastrar(user: String, senha: String, nome: String, rg: String, nascimento: String): Boolean {
        if (user == adminUser) return false
        if (usuarios.value.any { it.user == user }) return false
        usuarios.value.add(Usuario(user, senha, nome, rg, nascimento))
        return true
    }

    fun reservarHorario(horario: String): Boolean {
        val user = usuarioLogado.value?.user ?: return false
        if (user == adminUser) return false
        if (agendamentos.value.any { it.second == horario }) return false
        agendamentos.value.add(Pair(user, horario))
        return true
    }

    fun cancelarReserva(horario: String): Boolean {
        val user = usuarioLogado.value?.user ?: return false
        val reserva = agendamentos.value.find { it.first == user && it.second == horario }
        return if (reserva != null) {
            agendamentos.value.remove(reserva)
            true
        } else false
    }

    fun removerReservaAdmin(reserva: Pair<String, String>) {
        agendamentos.value.remove(reserva)
    }

    fun horariosDisponiveis(): List<String> {
        val todosHorarios = listOf("09:00", "10:00", "11:00", "14:00", "15:00", "16:00")
        val ocupados = agendamentos.value.map { it.second }.toSet()
        return todosHorarios.filterNot { it in ocupados }
    }

    fun historicoDoUsuario(user: String): List<String> {
        return agendamentos.value.filter { it.first == user }.map { it.second }
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = AppViewModel()

        setContent {
            MaterialTheme {
                var tela by remember { mutableStateOf("login") }

                when (tela) {
                    "login" -> LoginScreen(
                        vm,
                        onLogin = {
                            tela = if (vm.usuarioLogado.value?.user == "admin") "adminReservas" else "reserva"
                        },
                        onCadastro = { tela = "cadastro" }
                    )
                    "cadastro" -> CadastroScreen(vm) { tela = "login" }
                    "reserva" -> ReservaScreen(vm) { tela = "login" }
                    "adminReservas" -> AdminReservasScreen(vm) { tela = "login" }
                }
            }
        }
    }
}


@Composable
fun LoginScreen(vm: AppViewModel, onLogin: () -> Unit, onCadastro: () -> Unit) {
    var user by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TÍTULO
        Text(
            text = "Seja bem-vindo ao Corte",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        TextField(value = user, onValueChange = { user = it }, label = { Text("Usuário") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (vm.login(user, senha)) {
                onLogin()
            } else {
                Toast.makeText(context, "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show()
            }
        }) { Text("Entrar") }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onCadastro) { Text("Cadastrar") }
    }
}


@Composable
fun CadastroScreen(vm: AppViewModel, onVoltar: () -> Unit) {
    var user by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var rg by remember { mutableStateOf("") }
    var nascimento by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = user, onValueChange = { user = it }, label = { Text("Usuário") })
        TextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") })
        TextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome Completo") })
        TextField(value = rg, onValueChange = { rg = it }, label = { Text("RG") })
        TextField(value = nascimento, onValueChange = { nascimento = it }, label = { Text("Nascimento") })

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (vm.cadastrar(user, senha, nome, rg, nascimento)) {
                Toast.makeText(context, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
                onVoltar()
            } else {
                Toast.makeText(context, "Usuário já existe ou inválido", Toast.LENGTH_SHORT).show()
            }
        }) { Text("Cadastrar") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onVoltar) { Text("Voltar") }
    }
}


@Composable
fun ReservaScreen(vm: AppViewModel, onLogout: () -> Unit) {
    val user = vm.usuarioLogado.value?.user ?: ""
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Bem-vindo, $user", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Horários disponíveis:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        vm.horariosDisponiveis().forEach { horario ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(horario)
                Button(onClick = {
                    if (vm.reservarHorario(horario)) {
                        Toast.makeText(context, "Horário reservado!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Não foi possível reservar", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Reservar") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Minhas reservas:", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(vm.historicoDoUsuario(user)) { horario ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(horario)
                    Button(onClick = {
                        if (vm.cancelarReserva(horario)) {
                            Toast.makeText(context, "Reserva cancelada", Toast.LENGTH_SHORT).show()
                        }
                    }) { Text("Cancelar") }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onLogout) { Text("Sair") }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReservasScreen(vm: AppViewModel, onLogout: () -> Unit) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Reservas dos Clientes", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (vm.agendamentos.value.isEmpty()) {
            Text("Nenhuma reserva realizada ainda.")
        } else {
            LazyColumn {
                items(vm.agendamentos.value) { reserva ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Cliente: ${reserva.first}")
                                Text("Horário: ${reserva.second}")
                            }

                            Button(
                                onClick = {
                                    vm.agendamentos.value = vm.agendamentos.value.toMutableList().also { it.remove(reserva) }
                                    Toast.makeText(context, "Reserva removida com sucesso!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Remover")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onLogout) { Text("Sair") }
    }
}
