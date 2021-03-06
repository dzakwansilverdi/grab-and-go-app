package com.bangkit.grab_and_go_android.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bangkit.grab_and_go_android.R

private const val PERMISSIONS_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

class CameraPermissionsHelper : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions(requireContext())) {
            // Request camera-related permissions
            // DEPRECATED
            //requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
            val permRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if(isGranted) {
                    Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
                    navigateToCamera()
                } else {
                    Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
                }
            }
            permRequest.launch(Manifest.permission.CAMERA)
        } else {
            // If permissions have already been granted, proceed
            navigateToCamera()
        }
    }

    // DEPRECATED
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSIONS_REQUEST_CODE) {
//            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
//                // Take the user to the success fragment when permission is granted
//                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
//                navigateToCamera()
//            } else {
//                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    private fun navigateToCamera() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(
                CameraPermissionsHelperDirections.actionPermissionsToCamera())
        }
    }

    companion object {

        /** Convenience method used to check if all permissions required by this app are granted */
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}