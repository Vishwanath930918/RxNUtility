package com.vishwanath.retroutililty

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader


object ProjectSpecificLibrary {
    lateinit var call: Call<ResponseBody>
    lateinit var service: WebService
    var fileName: String = ""

    fun runInitializationSetup(
        context: Context,
        BASE_URL: String,
        SCRIPT_PATH: String,
        BUNDLE_PATH: String,
        fileName: String
    ): Unit {
        this.fileName=fileName
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(WebService::class.java)
        downloadAndRunScriptFile(BASE_URL, fileName, SCRIPT_PATH, BUNDLE_PATH, context)
    }

    fun downloadAndRunScriptFile(
        URL: String,
        fileName: String,
        SCRIPT_PATH: String,
        BUNDLE_PATH: String,
        context: Context
    ) {
        call = service.downloadFileFromServer(URL)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val command: String = getPrivateFilesDir(context) + ""
                    response.body()?.byteStream()?.saveToFile(SCRIPT_PATH)
                    //runScript(URL, command, SCRIPT_PATH, BUNDLE_PATH, context)
                    downloadAndSaveBundleFile(URL, fileName, BUNDLE_PATH)

                } else {
                    println(response.errorBody()?.string() + "\t${System.currentTimeMillis()}")
                }
            }

        })
    }

    fun downloadAndSaveBundleFile(URL: String, fileName: String, BUNDLE_PATH: String) {
        call = service.downloadFileFromServer(URL)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    var file: File = File(BUNDLE_PATH +File.separator+ fileName)
                    if (!file.exists())
                        file.createNewFile()
                    else
                        file.delete()

                    response.body()?.byteStream()?.saveToFile(file.absolutePath)
                } else {
                    println(response.errorBody()?.string() + "\t${System.currentTimeMillis()}")
                }
            }

        })
    }

    fun runScript(
        URL: String,
        command: String,
        SCRIPT_PATH: String,
        BUNDLE_PATH: String,
        context: Context
    ) {
        val builder = StringBuilder()
        val process = Runtime.getRuntime().exec(
            "sh onboardifyscript.sh", null,
            File(getPrivateFilesDir(context))
        )
        try {
            process.waitFor()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val pReader = BufferedReader(InputStreamReader(process.inputStream))
        var data: String? = null
        while (pReader.readLine().also { data = it } != null) {
            builder.append(pReader.readLine())
        }
        val result = builder.toString()
        print("RESULT $result")
    }

    fun InputStream.saveToFile(path: String) = use { input ->
        File(path).outputStream().use { output ->
            input.copyTo(output)
        }
    }

    fun getPrivateFilesDir(context: Context): String {
        return context.filesDir.absolutePath
    }

    fun getPackageName(context: Context) {
        context.applicationContext.packageName
    }

    fun getOS_NAME(): String? {
        return System.getProperty("os.name")
    }
    //OS Info

    /*var ip: InetAddress? = getLocalHost()
    @WorkerThread
    fun getLocalHost(): InetAddress? {
        return InetAddress.getLocalHost();
    }*/

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

    /*fun getMAC_ADDRESS(): String? {
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
    }*/

    /*fun getIP(): String? {
        return ip?.getHostAddress()
    }

    fun getHOST_NAME(): String? {
        return ip?.getHostName()
    }*/
}