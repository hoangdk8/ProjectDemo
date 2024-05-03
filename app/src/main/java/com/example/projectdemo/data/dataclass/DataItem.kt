package com.example.projectdemo.data.dataclass

import com.google.gson.annotations.SerializedName


data class MusicBanner(val title: String, val image: Int)
data class AD(val image: Int)
class DataItem(val viewType: Int) {

    var recyclerItemList: List<MusicBanner>? = null
    var bannerList: DataDefaultRings.Data? = null
    var adversite: AD? = null

    constructor(viewType: Int, recyclerItemList: List<MusicBanner>) : this(viewType) {
        this.recyclerItemList = recyclerItemList
    }

    constructor(viewType: Int, bannerList: DataDefaultRings.Data) : this(viewType) {
        this.bannerList = bannerList
    }

    constructor(viewType: Int, adversite: AD) : this(viewType) {
        this.adversite = adversite
    }
}