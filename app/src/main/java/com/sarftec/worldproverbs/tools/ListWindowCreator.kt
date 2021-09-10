package com.sarftec.worldproverbs.tools

class ListWindowCreator<T>(list: List<T>, private val size: Int) {

    private val listIterator = CustomListIterator(list)
    private val result: MutableList<List<T>> = mutableListOf()

    private var currentList: MutableList<T> = mutableListOf()
    private var sizeIterator = 0

    private fun adder(item: T) {
        currentList.add(item)
        sizeIterator += 1
        if(sizeIterator >= size) {
            result.add(currentList)
            currentList = mutableListOf()
            sizeIterator = 0
        } else if(!listIterator.hasNext() && sizeIterator < size) {
            result.add(currentList)
        }
    }

    fun createWindowedList(): List<List<T>> {
        while (listIterator.hasNext()) adder(listIterator.next())
        if(result.isEmpty()) result.add(listOf())
        return result
    }
}