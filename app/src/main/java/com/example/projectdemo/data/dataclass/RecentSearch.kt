package com.example.projectdemo.data.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_table")
data class RecentSearch(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val itemName: String
)
