package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel : LocationViewModel = viewModel()
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel) {
    val context = LocalContext.current
    val locationUtil = LocationUtil(context)
    LocationDisplay(locationUtil = locationUtil, viewModel = viewModel, context = context)
}

@Composable
fun LocationDisplay(
    locationUtil: LocationUtil,
    viewModel: LocationViewModel,
    context: Context
) {

    val location = viewModel.locationData.value
    val address = location?.let {
        locationUtil.reverseGeocodeLocation(location)
    }
    
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                locationUtil.requestLocationUpdate(viewModel)
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationaleRequired) {
                    Toast.makeText(context, "Permission Required", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Go to settings and allow permission", Toast.LENGTH_LONG).show()
                }
            }
        })
    
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        if (location != null) {
            Text(text = "Location is ${location.latitude} ${location.longitude} \n $address")
        } else {
            Text(text = "Location not available")
        }
        
        Button(onClick = {
            if (locationUtil.hasLocationPermission(context)) {
                locationUtil.requestLocationUpdate(viewModel)
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "Get Location")
        }
    }

}