package weatherapp.project.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class UserLocation(val lat: Double, val lon: Double)

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): UserLocation? {
    val hasPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) return null

    return suspendCancellableCoroutine { cont ->
        val client = LocationServices.getFusedLocationProviderClient(context)
        val cts = CancellationTokenSource()

        // Primero intentamos ubicación actual precisa
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(UserLocation(location.latitude, location.longitude))
                } else {
                    // Fallback: última ubicación conocida
                    client.lastLocation.addOnSuccessListener { last ->
                        cont.resume(last?.let { UserLocation(it.latitude, it.longitude) })
                    }.addOnFailureListener {
                        cont.resume(null)
                    }
                }
            }
            .addOnFailureListener {
                cont.resume(null)
            }
        cont.invokeOnCancellation { cts.cancel() }
    }
}
