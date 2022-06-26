package com.reyhaneh.ahani.iotclient

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reyhaneh.ahani.iotclient.pages.*
import com.reyhaneh.ahani.iotclient.server.ServerInterface
import com.reyhaneh.ahani.iotclient.ui.theme.IoTClientTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

            IoTClientTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            TopAppBar(
                                title = {Text("IoT Moblile Client", color = Color(0xFFFFFFFF))},
                                backgroundColor = MaterialTheme.colors.primarySurface
                            ) },
                        drawerContent = {
                            DrawerCompose(navController)
                        },
                        content = {
                            NavigationComponent(navController)
                        },
                    )
                }
            }
        }
    }
}


@Composable
fun NavigationComponent(navController: NavHostController) {
    LaunchedEffect("navigation") {
        Navigator.getInstance().sharedFlow.onEach {
            navController.navigate(it.navTarget.label) {
                if(it.pop != null) {
                    popUpTo(it.pop) {
                        inclusive = it.inclusive
                    }
                }
            }
        }.launchIn(this)
    }

    NavHost(
        navController = navController,
        startDestination = Navigator.NavTarget.Login.label
    ) {
        composable(Navigator.NavTarget.Login.label) {
            LoginPage()
        }
        composable(Navigator.NavTarget.Summary.label) {
            SummaryPage()
        }
        composable(Navigator.NavTarget.Register.label) {
            RegisterPage()
        }
    }
}

@Composable
fun DrawerCompose(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(R.drawable.logo),
            contentDescription = "Logo",
            contentScale = ContentScale.Crop)
        Text(
            text = "IoT Mobile Client",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Text(
            text = "An Android client for a IoTServer monitoring a flower",
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Created by Reyhaneh Ahani, for Instrumental Measurements",
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        NavHost(
            navController = navController,
            startDestination = Navigator.NavTarget.Login.label
        ) {
            composable(Navigator.NavTarget.Summary.label) {
                SummaryPageDrawer()
            }
            composable(Navigator.NavTarget.Login.label) {
            }
            composable(Navigator.NavTarget.Register.label) {
            }
        }
    }
}