package com.example.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.AppViewModel

@Composable
fun CadastroScreen(vm: AppViewModel, onVoltar: () -> Unit) {
    var user by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var rg by remember { mutableStateOf("") }
    var nascimento by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }
    var sucesso by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Surface(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Tela de Cadastro", style = MaterialTheme.typography.headlineMedium)

                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = user, onValueChange = { user = it }, label = { Text("Usuário") })
                OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") })
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome Completo") })
                OutlinedTextField(value = rg, onValueChange = { rg = it }, label = { Text("RG") })
                OutlinedTextField(value = nascimento, onValueChange = { nascimento = it }, label = { Text("Nascimento") })

                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    if (vm.cadastrar(user, senha, nome, rg, nascimento)) {
                        erro = false
                        sucesso = true
                    } else erro = true
                }) { Text("Cadastrar") }

                if (erro) {
                    Text("Erro: usuário já existe.", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (sucesso) {
        AlertDialog(
            onDismissRequest = { sucesso = false; onVoltar() },
            confirmButton = { TextButton(onClick = { sucesso = false; onVoltar() }) { Text("OK") } },
            title = { Text("Cadastro realizado") },
            text = { Text("Usuário criado com sucesso!") }
        )
    }
}