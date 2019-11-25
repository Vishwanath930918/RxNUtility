package com.vishwanath.retroutililty

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.URL
import java.net.URLStreamHandler
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel


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
        this.fileName = fileName
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(WebService::class.java)
        //downloadAndRunScriptFile(BASE_URL + fileName, fileName, SCRIPT_PATH, BUNDLE_PATH, context)

        downloadFiles(BASE_URL+fileName, File(BUNDLE_PATH+fileName))
        //downloadFile(url,BUNDLE_PATH+fileName)
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
                    //val command: String = getPrivateFilesDir(context) + ""
                    //response.body()?.byteStream()?.saveToFile(SCRIPT_PATH)
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
                    var file: File = File(BUNDLE_PATH + File.separator + fileName)
                    if (!file.exists())
                        file.mkdir()
                    else
                        file.delete()

                    response.body()?.byteStream()?.saveToFile(file.absolutePath)
                } else {
                    println(response.errorBody()?.string() + "\t${System.currentTimeMillis()}")
                }
            }

        })
    }
    fun downloadFiles(url: String, outputFilePath: File) {
        try {
            val u = URL(url)
            val conn = u.openConnection()
            val contentLength = conn.getContentLength()

            val stream = DataInputStream(u.openStream())

            val buffer = ByteArray(contentLength)
            stream.readFully(buffer)
            stream.close()

            val fos = DataOutputStream(FileOutputStream(outputFilePath))
            fos.write(buffer)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            return  // swallow a 404
        } catch (e: IOException) {
            return  // swallow a 404
        } catch (e: Exception) {
            return  // swallow any
        }
    }

    //Using NIO
    fun downloadFile(url: URL, FILE_NAME: String) {
        //To read the file from our URL, we'll create a new ReadableByteChannel from the URL stream:
        val readableByteChannel: ReadableByteChannel = Channels.newChannel(url.openStream())

        //The bytes read from the ReadableByteChannel will be transferred to a FileChannel
        // corresponding to the file that will be downloaded:
        val fileOutputStream = FileOutputStream(FILE_NAME)

        //We'll use the transferFrom() method from the ReadableByteChannel class to
        // download the bytes from the given URL to our FileChannel:
        //The transferTo() and transferFrom() methods are more efficient than simply reading from a stream using a buffer.
        val fileChannel: FileChannel = fileOutputStream.getChannel()
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
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