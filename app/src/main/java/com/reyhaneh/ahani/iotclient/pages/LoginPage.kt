package com.reyhaneh.ahani.iotclient.pages

import android.app.Application
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reyhaneh.ahani.iotclient.Navigator
import com.reyhaneh.ahani.iotclient.server.ServerInterface
import com.reyhaneh.ahani.iotclient.server.resposneFromErrorBody
import kotlinx.coroutines.launch
import com.reyhaneh.ahani.iotclient.Navigator.NavTarget

class LoginPageViewModel(application: Application) : AndroidViewModel(application) {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    var serverAddress by mutableStateOf("")
    var serverPort by mutableStateOf("")

    fun showToast(message: String) {
        Toast.makeText(
            getApplication(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    fun processLogin() {
        viewModelScope.launch {
            isLoading = true
            try {
                val serverInterface = ServerInterface.getInstance(getApplication<Application>().applicationContext,
                    serverAddress,
                    serverPort)
                serverInterface.login(username, password)
                Navigator.getInstance().navigateTo(NavTarget.Summary, NavTarget.Login.label, true)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()
                showToast("Could not login: ${errorBody?.let { resposneFromErrorBody(it).reason }}")
            } catch (e: java.net.SocketTimeoutException) {
                showToast("Could not login, server timed out, check your internet connection")
            } catch(e: Exception) {
                showToast("Could not login: ${e}")
            } finally {
                isLoading = false
            }
        }
    }
}

@Composable
fun LoginPage(viewModel: LoginPageViewModel = viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginTextField("Username", viewModel.username, {viewModel.username = it})
        LoginTextField("Password", viewModel.password, {viewModel.password = it}, PasswordVisualTransformation())
        LoginTextField(label = "Server address",
            value = viewModel.serverAddress,
            onValueChange = {viewModel.serverAddress = it})
        LoginTextField(label = "Server port",
            value = viewModel.serverPort,
            onValueChange = {viewModel.serverPort = it})
        Row {
            Button(onClick = {viewModel.processLogin()}) {
                Text("Login")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = {
                Navigator.getInstance().navigateTo(NavTarget.Register, NavTarget.Login.label, true)
            }) {
                Text("Create an account")
            }
        }
        AnimatedVisibility(viewModel.isLoading) {
            Column {
                Spacer(Modifier.height(10.dp))
                Text("Loading ...")
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun LoginTextField(label:String,
                   value: String,
                   onValueChange: (String) -> Unit,
                   visualTransformation: VisualTransformation = VisualTransformation.None) {
    TextField(
        value = value,
        onValueChange = {onValueChange(it)},
        singleLine = true,
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        visualTransformation = visualTransformation,
        label = {
            Text(text = label)
        }
    )
}