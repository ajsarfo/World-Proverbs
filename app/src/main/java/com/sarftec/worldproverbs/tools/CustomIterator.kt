package com.sarftec.worldproverbs.tools

class CustomListIterator<T>(private val list: List<T>, position: Int = -1 ) {
    private var iterator = position

    fun current() : T {
       return list[iterator]
    }

    fun hasNext(): Boolean {
        return iterator + 1 >= 0 && iterator + 1 < list.size
    }

    fun hasPrevious() : Boolean {
        return iterator - 1 >= 0 && iterator -1 < list.size
    }

    fun next(): T {
        iterator += 1
        return list[iterator]
    }

    fun previous() : T {
        iterator -= 1
        return list[iterator]
    }
}