package com.ricdev.uread.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt
import org.readium.r2.navigator.Decoration

data class DecorationStyleAnnotationMark(
    @ColorInt val tint: Int
) : Decoration.Style {
    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(tint)
    }

    companion object CREATOR : Parcelable.Creator<DecorationStyleAnnotationMark> {
        override fun createFromParcel(parcel: Parcel): DecorationStyleAnnotationMark {
            return DecorationStyleAnnotationMark(parcel.readInt())
        }

        override fun newArray(size: Int): Array<DecorationStyleAnnotationMark?> {
            return arrayOfNulls(size)
        }
    }
}
