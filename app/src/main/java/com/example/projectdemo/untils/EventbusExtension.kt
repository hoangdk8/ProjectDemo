package com.example.projectdemo.untils

import org.greenrobot.eventbus.EventBus

fun Any.eventBusRegister() {
    if (this is Any) {
        EventBus.getDefault().register(this)    }
}
fun Any.eventBusUnRegister() {
    if (this is Any) {
        EventBus.getDefault().unregister(this)
    }
}
fun Any.eventBusPost(event: Any) {
    EventBus.getDefault().post(event)
}