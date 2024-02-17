package dev.kuromiichi.apppeluqueriaadmin.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User (
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val uid: String = ""
) : Parcelable