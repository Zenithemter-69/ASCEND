package ascending.com.ascendingg.data.tracking

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.isActive

/**
 * Tracks location-based metrics like distance traveled and steps.
 * Uses FusedLocationProviderClient from Google Play Services.
 */
class LocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Permission check helpers
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private fun hasLocationPermission(): Boolean {
        return permissions.all { ContextCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Flow that emits new location readings as they become available.
     * Emits only the last known location initially, then updates.
     */
    val locationFlow: Flow<Location?> = generate {
        if (hasLocationPermission()) {
            // Try to get last known location immediately
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null && isActive) {
                    emit(location)
                }
            }
        }

        // Continuously request location updates
        while (isActive) {
            try {
                val locationRequest = android.location.LocationRequest.Builder(
                    priority = android.location.LocationRequest.PriORITY_HIGH_ACCURACY,
                    intervalMillis = 5000
                ).build()

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : android.location.LocationCallback() {
                        override fun onLocationResult(locationResult: android.location.LocationResult) {
                            locationResult.locations.forEach { location ->
                                if (location != null && isActive) {
                                    emit(location)
                                }
                            }
                        }
                    },
                    null /* mainLooper */
                )

                // Wait a bit before stopping, then stop to save battery
                Thread.sleep(60000)
                fusedLocationClient.removeLocationUpdates(
                    object : android.location.LocationCallback() {}
                )
                break
            } catch (e: SecurityException) {
                // Permission denied or other error
                break
            }
        }
    }
}