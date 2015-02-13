var GlyphFactory = require("./glyphFactory.js");
var Symbols = require("./symbols.js");
require("./note.js");

Stave = (function() {
  function Stave(paper, x, y, width) {
    this.init(paper, x, y, width);
  }

  Stave.prototype = {
    init: function(paper, x, y, width) {
      this.paper = paper;
      this.x = x;
      this.y = y;
      this.width = width;

      this.heightBetweenLines = 12;
      this.scale = 1.0;
      this.glyphScale = 0.05;

      this.shouldDrawClef = true;
      this.selectedClef = Symbols.G_CLEF;
    },

    render: function() {
      this.drawStave();
      this.drawClef(5 * this.heightBetweenLines);
    },

    drawStave: function() {
      var scaledHeight = this.scale * this.heightBetweenLines;

      console.log("Scaled height: " +scaledHeight);

      for(var i = 0; i < 5; ++i) {
        this.paper.path("M" +this.x +" " +(this.y + i * scaledHeight) +"h" +this.width);
      }
      this.paper.path("M" +this.x +" " +this.y +"v" +(4 * scaledHeight));
      this.paper.path("M" +(this.x + this.width) +" " +this.y +"v" +(4 * scaledHeight));
    },

    drawClef: function(height) {
      // TODO Just testing with a G-clef now
      var glyphOutline = GlyphFactory.getGlyph(this.getType());
      var glyph = this.paper.path(glyphOutline);

      console.log("x: " +this.x +". y: " +this.y);

      glyph.transform("...s" +this.glyphScale +"," +this.glyphScale +",0,0");

      console.log("Glyph scale: " +this.glyphScale);

      var center = GlyphFactory.getCenter(this.glyphScale);

      var diffX = this.x - center.x;
      var scaledHeight = this.scale * this.heightBetweenLines;
      var diffY = this.y + 3 * scaledHeight - center.y;

      console.log("diffX: " +diffX +" diffY: " +diffY);

      glyph.transform("...T" +diffX +"," +diffY);
      glyph.attr("fill", "black");

      console.log("Center: " +center.x +", " +center.y);
      var rect2 = this.paper.rect(this.x, this.y, 2 * center.x, 2 * center.y);
      rect2.attr("stroke", "blue");

    },

    drawNotes: function(notes) {
      // TODO Available space for notes is not equal to width
      var space = this.width;

      var currentX = 0; //this.x;

      for(var i = 0; i < notes.length; ++i) {
        var note = notes[i].getNote();
        var duration = notes[i].getDuration();

        if(duration != 4 && duration != 2) {
          console.log("Error");
        }

        var noteHeadOutline;
        switch(duration) {
          case 2:
            noteHeadOutline = GlyphFactory.getGlyph(Symbols.HALF_NOTE);
            break;

          case 4:
            noteHeadOutline = GlyphFactory.getGlyph(Symbols.QUARTER_NOTE);
            break;

          default:
            console.log("Unsupported duration");
        }

        var interval = this.width / duration;
        currentX += interval;

        var path = this.paper.path(noteHeadOutline); //currentX, this.y + 100);
        path.transform("...s" +this.glyphScale +"," +this.glyphScale +",0,0");
        var center = GlyphFactory.getCenter(this.glyphScale);
        var diffX = this.x - center.x + currentX;
        var scaledHeight = this.scale * this.heightBetweenLines;
        var diffY = this.y + 3 * scaledHeight - center.y + 3;

        path.transform("...T" +diffX +"," +diffY);
        path.attr("fill", "black");

      }

    },

    getDrawClef: function() {
      return this.shouldDrawClef;
    },

    setDrawClef: function(drawClef) {
      this.shouldDrawClef = drawClef;
    },

    setType: function(type) {
      this.selectedClef = type;
    },

    getType: function() {
      return this.selectedClef;
    }

  };

  return Stave;

})();
