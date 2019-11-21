package com.vishwanath.retroutililty

import java.net.InetAddress
import java.net.NetworkInterface


object SystemInfo {

    var ip: InetAddress? = InetAddress.getLocalHost();
    fun getOS_NAME(): String? {
        return System.getProperty("os.name")
    }

    fun getOS_TYPE(): String? {
        return System.getProperty("os.arch")
    }

    fun getOS_VERSION(): String? {
        return System.getProperty("os.version")
    }

    fun getPROCESSOR_IDENTIFIER(): String? {
        return System.getenv("PROCESSOR_IDENTIFIER")
    }

    fun getPROCESSOR_ARCHITECTURE(): String? {
        return System.getenv("PROCESSOR_ARCHITECTURE")
    }

    fun getPROCESSOR_ARCHITEW6432(): String? {
        return System.getenv("PROCESSOR_ARCHITEW6432")
    }

    fun getNUMBER_OF_PROCESSORS(): String? {
        return System.getenv("NUMBER_OF_PROCESSORS")
    }

    fun getAVAILABLE_CORES(): Int? {
        return Runtime.getRuntime().availableProcessors()
    }

    fun getFREE_MEMORY(): Long? {
        return Runtime.getRuntime().freeMemory()
    }

    fun getMAX_MEMORY(): Long? {
        return Runtime.getRuntime().maxMemory()
    }

    fun getTOTAL_MEMORY(): Long? {
        return Runtime.getRuntime().totalMemory()
    }

    fun getMAC_ADDRESS(): String? {
        val network: NetworkInterface = NetworkInterface.getByInetAddress(ip)
        val mac: ByteArray = network.getHardwareAddress()
        val sb = StringBuilder()
        for (i in 0 until mac.size) {
            sb.append(
                java.lang.String.format(
                    "%02X%s",
                    mac[i],
                    if (i < mac.size - 1) "-" else ""
                )
            )
        }
        return sb.toString()
    }

    fun getIP(): String? {
        return ip?.getHostAddress()
    }

    fun getHOST_NAME(): String? {
        return ip?.getHostName()
    }
}