package com.reyhaneh.ahani.iotclient.pages

import android.app.Application
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reyhaneh.ahani.iotclient.Navigator
import com.reyhaneh.ahani.iotclient.server.ServerInterface
import com.reyhaneh.ahani.iotclient.server.resposneFromErrorBody
import kotlinx.coroutines.launch


class RegisterPageViewModel(application: Application) : AndroidViewModel(application) {
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

    fun processRegister() {
        viewModelScope.launch {
            isLoading = true
            try {

                val serverInterface = ServerInterface.getInstance(getApplication<Application>().applicationContext,
                    serverAddress,
                    serverPort)

                serverInterface.register(username, password)
                Navigator.getInstance().navigateTo(Navigator.NavTarget.Login, Navigator.NavTarget.Register.label, true)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()
                showToast("Could not register: ${errorBody?.let { resposneFromErrorBody(it).reason }}")
            } catch (e: java.net.SocketTimeoutException) {
                showToast("Could not register, server timed out, check your internet connection")
            } catch(e: Exception) {
                showToast("Could not login: ${e}")
            } finally {
                isLoading = false
            }
        }
    }
}

@Composable
fun RegisterPage(viewModel: RegisterPageViewModel = viewModel()) {
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
            Button(onClick = {viewModel.processRegister()}) {
                Text("Register")
            }
            Spacer(modifier = Modifier.width(10.dp))
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