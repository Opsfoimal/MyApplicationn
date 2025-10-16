package com.example.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.AppViewModel
import com.example.myapplication.Usuario

@Composable
fun LoginScreen(vm: AppViewModel, onLogin: () -> Unit, onCadastro: () -> Unit) {
    var user by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold { padding ->
        Surface(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Tela de Login", style = MaterialTheme.typography.headlineMedium)

                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = user, onValueChange = { user = it }, label = { Text("Usuário") })
                OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") })

                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    if (vm.login(user, senha)) {
                        erro = false
                        onLogin()
                    } else {
                        erro = true
                        Toast.makeText(context, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Entrar") }

                Spacer(Modifier.height(8.dp))
                Button(onClick = onCadastro) { Text("Ir para Cadastro") }

                if (erro) {
                    Text("Erro: Usuário ou senha inválidos", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
