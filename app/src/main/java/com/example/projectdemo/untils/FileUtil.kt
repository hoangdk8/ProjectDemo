package com.example.projectdemo.untils

import android.content.Context
import android.os.Environment
import com.example.projectdemo.di.NetworkModule
import com.example.projectdemo.domain.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

object FileUtil {

    fun downloadAndWriteToCache(
        context: Context,
        url: String,
        fileName: String,
        callback: (Boolean) -> Unit
    ) {
        val retrofit = NetworkModule.provideRetrofit()
        val apiClient = retrofit.create(ApiService::class.java)
        val call = apiClient.downloadFile(url)
        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).absolutePath
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    MainScope().launch {
                        withContext(Dispatchers.IO) {
                            val file = File(directoryPath, fileName)
                            val isSaveFileCache = writeResponseBodyToDisk(response.body(), file)
                            callback(isSaveFileCache)
                        }
                    }
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(false)
            }
        })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody?, fileDownload: File): Boolean {
        return try {
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                inputStream = body!!.byteStream()
                outputStream = FileOutputStream(fileDownload)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}
