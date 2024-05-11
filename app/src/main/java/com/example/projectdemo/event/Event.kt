package com.example.projectdemo.event

import com.example.projectdemo.data.dataclass.DataDefaultRings

class Event {
}
class EventRefreshHome
class EventRefreshExplore
class EventRefreshSearch
class EventRefreshMe
class EventPlayDetailMusic(val ringTone : DataDefaultRings.RingTone)
class EventGoneView
class EventVisibleView

class EventUnFavorite(val id:Int)
class EventUnDownload(val id:Int)
class EventMiniPlay(val title: String,val time: Int)
class EventShowMiniPlay
class EventHideMiniPlay
class EventLoading
class EventPlayMain
class StartTime
class StopTime
class ResetTime
class EventChangeIconPlay
class EventChangeIconPause