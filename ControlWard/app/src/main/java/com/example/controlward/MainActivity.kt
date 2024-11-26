package com.example.controlward

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.controlward.ui.theme.ControlWardTheme
import com.google.firebase.auth.FirebaseAuth

object Value {
    lateinit var uid: String
    val disasterList1: List<DisasterModel> =
        listOf(
            DisasterModel("ex1", "ex1", "37.5665", "126.9780"),
            DisasterModel("ex2", "ex2", "37.5265", "126.9380"),
            DisasterModel("ex3", "ex3", "37.5765", "126.9880"),
        )
    val disasterList2: List<DisasterModel> =
        listOf(
            DisasterModel("ex4", "ex4", "37.5565", "126.9680"),
            DisasterModel("ex5", "ex5", "37.5865", "126.9980"),
            DisasterModel("ex6", "ex6", "37.5465", "126.9580"),
            DisasterModel("ex7", "ex7", "37.5365", "126.9480")
        )

    val disasterAllList = disasterList1 + disasterList2
}

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //위치 확인 -> disaster list 가져오기
        //val disasterList = DisasterViewModel().getDisasterList()

//        setContent {
//            ControlWardTheme {
//                LoadingScreen()
//            }
//        }

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                auth = FirebaseAuth.getInstance()
                signInAnonymously()

                setContent {
                    ControlWardTheme {
                        ScreenNavigator()
                    }
                }
            }else{
                Toast.makeText(this, "앱을 사용하기 위해서 위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        requestLocationPermission(this)
    }

    private fun requestLocationPermission(context: Context) {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission && hasCoarseLocationPermission) {
            setContent {
                ControlWardTheme {
                    ScreenNavigator()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun signInAnonymously() {
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Value.uid = auth.currentUser?.uid ?: ""
                    }
                }
        } else {
            Value.uid = auth.currentUser?.uid ?: ""
        }
    }
}
