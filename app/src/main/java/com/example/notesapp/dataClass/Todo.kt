package com.example.notesapp.dataClass

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String?,
    var notes: String?,
    var deadline: String? = null,
    val time: String? = null,
    var taskDateType: String? = null,
    var isCompleted: Boolean = false,
    var alarmTime: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(notes)
        parcel.writeString(deadline)
        parcel.writeString(time)
        parcel.writeString(taskDateType)
        parcel.writeByte(if (isCompleted) 1 else 0)
        parcel.writeValue(alarmTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Todo> {
        override fun createFromParcel(parcel: Parcel): Todo {
            return Todo(parcel)
        }

        override fun newArray(size: Int): Array<Todo?> {
            return arrayOfNulls(size)
        }
    }
}
