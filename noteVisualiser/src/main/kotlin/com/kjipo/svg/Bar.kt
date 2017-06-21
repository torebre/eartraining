package com.kjipo.svg

class Bar {
    val elements:MutableList<TemporalElement> = mutableListOf()
    var availableWidth:Int = 0
    var layout:List<Int> = emptyList()


    fun addElement(temporalElement:TemporalElement) {
        elements.add(temporalElement)
    }



    private fun recalculateLayout() {




    }


    fun render() {


    }

}