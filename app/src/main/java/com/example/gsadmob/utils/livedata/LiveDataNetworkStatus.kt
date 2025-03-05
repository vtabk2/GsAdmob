package com.example.gsadmob.utils.livedata

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import java.net.UnknownHostException

class LiveDataNetworkStatus(context: Context) : LiveData<Boolean>() {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networks: MutableList<Network> = mutableListOf()

    private val networkStateObject = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            networks.remove(network)
            postValue(!networks.all { !checkInternetConnectivity(it) })
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            networks.add(network)
            postValue(checkInternetConnectivity(network))
        }

        fun checkInternetConnectivity(network: Network): Boolean {
            return try {
                network.getByName(ROOT_SERVER_CHECK_URL) != null
            } catch (e: UnknownHostException) {
                false
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        try {
            connectivityManager.registerNetworkCallback(networkRequestBuilder(), networkStateObject)
            postValue(isNetworkEnabled()) // Consider all networks "unavailable" on start
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInactive() {
        super.onInactive()
        try {
            connectivityManager.unregisterNetworkCallback(networkStateObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isNetworkEnabled(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            // For API level below 23, use deprecated methods
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI ||
                    activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE)
        }
    }

    private fun networkRequestBuilder(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }

    companion object {
        const val ROOT_SERVER_CHECK_URL = "a.root-servers.net"
    }
}