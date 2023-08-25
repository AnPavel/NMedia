package ru.netology.nmedia.error

import java.sql.SQLException
import java.io.IOException
import java.lang.RuntimeException

sealed class AppError(val code: Int, info: String): RuntimeException(info) {
    companion object {
        fun from(e: Throwable): AppError = when (e) {
            //is AppError -> e
            is SQLException -> DbError
            is IOException -> NetworkError
            is InterruptedException -> SocketTimeoutError
            else -> UnknownError
            }
        }
    }


class ApiError(code: Int, message: String) : AppError(code, message)

object DbError : AppError(-1, "error_db")
object NetworkError: AppError(-1, "error_network")
object SocketTimeoutError: AppError(-1, "error_socketTimeout")
object UnknownError: AppError(-1, "error_unknown")
