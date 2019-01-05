package com.kjipo.score


class ScoreBuilderImpl(override val debug: Boolean = false) : ScoreBuilderInterface<RenderingSequence> {
    private val currentElements = mutableListOf<ScoreRenderingElement>()
    val scoreData = ScoreSetup()
    private var restCounter = 0
    private var noteCounter = 0


    override fun onBarAdded(inputBar: BAR) {
        // TODO Figure out best way to set the value

        inputBar.let {
            val bar = it.barData
            bar.scoreRenderingElements.addAll(currentElements)
            currentElements.clear()
            if (scoreData.bars.isNotEmpty()) {
                // TODO Is this the correct direction? In which direction are the bars added through the method calls?
                bar.previousBar = scoreData.bars.last()
            }
            scoreData.bars.add(bar)
        }
    }

    override fun onNoteAdded(note: NOTE): String {
        val id = "note-${noteCounter++}"
        val noteElement = NoteElement(note.note, note.octave, note.duration, 0, 0, note.beamGroup, id)

        currentElements.add(noteElement)
        scoreData.noteElements.add(noteElement)

        return id
    }

    override fun onRestAdded(rest: REST) {
        val restElement = RestElement(rest.duration, 0, 0, "rest-${restCounter++}")

        currentElements.add(restElement)
        scoreData.noteElements.add(restElement)
    }

    fun score(init: SCORE.() -> Unit) = SCORE(this).apply(init).finalize(this)

    override fun build() = scoreData.build()

}